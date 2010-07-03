/**
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * File creation: Jul 1, 2010 12:42:41 AM
 */


package org.dml.tracking;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.references.ListOfUniqueNonNullObjects;
import org.references.method.MethodParams;



/**
 * this does not work for inner classes, to init them fails; unless it's an inner public static class 'cause obviously
 * its default constructor must be accessible from Factory class<br>
 * 
 */
public class Factory {
	
	
	// TODO
	// LIFO list tracking all instances of ALL subclasses that are inited
	private final static ListOfUniqueNonNullObjects<Initer>	ALL_INITED_INSTANCES	= new ListOfUniqueNonNullObjects<Initer>();
	
	

	/**
	 * generic method with a type variable<br>
	 * this will do a new and an init() with no params
	 * 
	 * @param class1
	 *            any subclass of StaticInstanceTracker
	 * @return
	 */
	public static <T extends Initer> T getNewInstance( Class<T> type ) {

		return getNewInstance( type, null );
	}
	
	
	/**
	 * using default constructor, do a new()<br>
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	private static <T extends Initer> T getGenericNewInstance( Class<T> type ) {

		T ret = null;
		
		Constructor<T> con = null;
		try {
			con = type.getConstructor();
		} catch ( SecurityException e ) {
			RunTime.bug( e, "method not accessible ie. private init() method instead of public" );
		} catch ( NoSuchMethodException e ) {
			RunTime.bug( e, "private default constructor? or a public one doesn't exist ? "
					+ "or you're calling this on an inner class which is not public static; "
					+ "and yet we do have the right class or subclass of Initer; "
					+ "or default constructor not explicitly defined" );
		}
		
		try {
			ret = con.newInstance();// no params constructor
			// ret = type.newInstance(); this works but we want to catch more exceptions above
		} catch ( IllegalArgumentException e ) {
			RunTime.bug( e );
		} catch ( InstantiationException e ) {
			RunTime.bug( e );
		} catch ( IllegalAccessException e ) {
			RunTime.bug( e );
		} catch ( InvocationTargetException e ) {
			RunTime.bug( e );
			// eclipse bug gone since this part was moved here
		}
		
		return ret;
	}
	
	/**
	 * this will do a new with the default constructor which should be public <br>
	 * and an .init(params)<br>
	 * 
	 * @param class1
	 *            any subclass of StaticInstanceTracker
	 * @param params
	 *            MethodParams instance for init(params)
	 * @return
	 */
	public static <T extends Initer> T getNewInstance( Class<T> type, MethodParams params ) {

		T ret = Factory.getGenericNewInstance( type );
		Factory.init( ret, params );
		RunTime.assumedTrue( ret.isInited() );
		return ret;
	}
	
	public static <T extends Initer> void init( T instance, MethodParams params ) {

		RunTime.assumedNotNull( instance );
		if ( instance.isInited() ) {
			RunTime.badCall( "must not be already init-ed" );
		}
		try {
			instance._init( params );// params may be null
		} finally {
			// any exceptions from the try block are postponed until after finally block is done
			addNewInitedInstance( instance );// shouldn't already exist since wasn't inited so not in our list
		}
		RunTime.assumedTrue( instance.isInited() );
	}
	
	/**
	 * after this you can call reInit
	 * 
	 * @param params
	 */
	public static <T extends Initer> void deInit( T instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedTrue( instance.isInited() );
		try {
			instance._deInit();
		} finally {
			removeExistingInitedInstance( instance );
		}
		RunTime.assumedFalse( instance.isInited() );
	}
	
	/**
	 * this will keep the instance (ie. no new again) and just do a deInit() and init()<br>
	 * it MUST be already inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void restart( T instance ) {

		RunTime.assumedNotNull( instance );
		if ( !instance.isInited() ) {
			RunTime.badCall( "must be inited" );
		}
		// must be already in our list
		if ( !ALL_INITED_INSTANCES.containsObject( instance ) ) {
			RunTime.bug( "bug somewhere this instance should've been added before, or it's a badcall" );
		}
		try {
			instance._restart_aka_deInit_and_initAgain_WithOriginalPassedParams();
		} finally {
			if ( !instance.isInited() ) {
				removeExistingInitedInstance( instance );
				RunTime.bug( "yeah dno how to handle this one, maybe remove instance from our list? and continue" );
			}
		}
		RunTime.assumedTrue( instance.isInited() );
	}
	
	/**
	 * this will keep the instance (ie. no new again) and just do a init() again<br>
	 * it MUST NOT be already inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void reInit( T instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedFalse( instance.isInited() );
		// must NOT be already in our list
		if ( ALL_INITED_INSTANCES.containsObject( instance ) ) {
			RunTime.bug( "bug somewhere this instance must NOT be in the list, and it is in list and it's also not inited?!!" );
		}
		try {
			instance._reInit_aka_initAgain_WithOriginalPassedParams();
		} finally {
			if ( !instance.isInited() ) {
				// removeExistingInitedInstance( instance );
				RunTime.bug( "so, reinit failed but it's still not inited, bug somewhere" );
			}
			addNewInitedInstance( instance );
		}
		RunTime.assumedTrue( instance.isInited() );
	}
	
	
	private final static void addNewInitedInstance( Initer instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedTrue( instance.isInited() );
		if ( ALL_INITED_INSTANCES.addFirstQ( instance ) ) {
			RunTime.badCall( "should not have existed" );
		}
	}
	
	private final static void removeExistingInitedInstance( Initer instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedFalse( instance.isInited() );
		// Log.entry( instance.toString() );
		if ( !ALL_INITED_INSTANCES.removeObject( instance ) ) {
			RunTime.badCall( "should've existed" );
		}
	}
}
