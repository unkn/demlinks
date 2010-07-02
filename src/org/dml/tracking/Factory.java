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
 * 
 *
 */
public class Factory {
	
	
	// TODO
	// LIFO list tracking all instances of ALL subclasses
	private final static ListOfUniqueNonNullObjects<Initer>	ALL_INSTANCES	= new ListOfUniqueNonNullObjects<Initer>();
	
	

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
	 * this will do a new with the default constructor which should be public and an .init(params)
	 * 
	 * @param class1
	 *            any subclass of StaticInstanceTracker
	 * @param params
	 *            MethodParams instance for init(params)
	 * @return
	 */
	public static <T extends Initer> T getNewInstance( Class<T> type, MethodParams params ) {

		Constructor<T> con = null;
		T ret = null;
		try {
			con = type.getConstructor();
		} catch ( SecurityException e ) {
			e.printStackTrace();
			RunTime.bug( "method not accessible ie. private init() method instead of public" );
		} catch ( NoSuchMethodException e ) {
			e.printStackTrace();
			RunTime.bug( "private default constructor? or a public one doesn't exist and yet we do have the right class or subclass of wtw" );
		}
		
		try {
			ret = con.newInstance();// no params constructor
		} catch ( IllegalArgumentException e ) {
			e.printStackTrace();
			RunTime.bug();
		} catch ( InstantiationException e ) {
			e.printStackTrace();
			RunTime.bug();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
			RunTime.bug();
		} catch ( InvocationTargetException e ) {
			e.printStackTrace();
			RunTime.bug();
		}
		
		RunTime.assumedNotNull( ret );
		RunTime.assumedFalse( ret.isInited() );
		ret.init( params );
		RunTime.assumedTrue( ret.isInited() );
		return ret;
	}
	
	/**
	 * after this you can call reInit
	 * 
	 * @param params
	 */
	public static <T extends Initer> void deInit( T instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedTrue( instance.isInited() );
		instance.deInit();
		RunTime.assumedFalse( instance.isInited() );
	}
	
	/**
	 * after this you can call deInit() or reInit() again
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void reInit( T instance ) {

		RunTime.assumedNotNull( instance );
		instance.reInit();
		RunTime.assumedTrue( instance.isInited() );
	}
	
}
