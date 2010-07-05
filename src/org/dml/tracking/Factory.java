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
import org.javapart.logger.Log;
import org.references.Position;
import org.references.TreeOfNonNullObjects;
import org.references.method.MethodParams;



/**
 * this does not work for inner classes, to init them fails; unless it's an inner public static class 'cause obviously
 * its default constructor must be accessible from Factory class<br>
 * 
 */
public class Factory {
	
	
	// LIFO list tracking all instances of ALL subclasses that are inited
	// add new ones to first, and when remove-all start from last to first
	// LIFO manner add/remove on this tree thingy
	private final static TreeOfNonNullObjects<Initer>	root	= new TreeOfNonNullObjects<Initer>();
	private static TreeOfNonNullObjects<Initer>			currentParent;
	
	/**
	 * generic method with a type variable<br>
	 * this will do a new and an init() with no params
	 * 
	 * @param class1
	 *            any subclass of StaticInstanceTracker
	 * @param constructorParameters
	 *            a list of objects to be passed to the constructor(which is auto found based on objects passed)<br>
	 *            can be unspecified aka null<br>
	 * @return the new instance, don't forget to assign this to a variable (FIXME: I wonder if we can do a warning on
	 *         this?)
	 */
	public static <T extends Initer> T getNewInstanceAndInit( Class<T> type, Object... constructorParameters ) {

		return getNewInstanceAndInit( type, null, constructorParameters );
	}
	
	
	/**
	 * using default constructor, do a new()<br>
	 * 
	 * @param <T>
	 * @param type
	 *            the class type to do a 'new' on
	 * @param initargsObjects
	 *            a list of objects to be passed to the constructor(which is auto found based on objects passed)<br>
	 *            can be unspecified aka null<br>
	 * @return the new instance, don't forget to assign this to a variable (FIXME: I wonder if we can do a warning on
	 *         this?)
	 */
	private static <T extends Initer> T getGenericNewInstance( Class<T> type, Object... initargsObjects ) {

		T ret = null;
		
		Constructor<T> con = null;
		try {
			Class<?>[] initargsClasses = null;
			if ( null != initargsObjects ) {
				initargsClasses = new Class<?>[initargsObjects.length];
				RunTime.assumedTrue( initargsClasses.length == initargsObjects.length );
				for ( int i = 0; i < initargsObjects.length; i++ ) {
					initargsClasses[i] = initargsObjects[i].getClass();
				}
			}
			con = type.getConstructor( initargsClasses );
		} catch ( SecurityException e ) {
			RunTime.bug( e, "method not accessible ie. private init() method instead of public" );
		} catch ( NoSuchMethodException e ) {
			RunTime.bug( e, "private default constructor? or a public one doesn't exist ? "
					+ "or you're calling this on an inner class which is not public static; "
					+ "and yet we do have the right class or subclass of Initer; "
					+ "or default constructor not explicitly defined" );
		}
		
		try {
			ret = con.newInstance( initargsObjects );// no params constructor
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
	 * @param type
	 *            any subclass of Initer
	 * @param params
	 *            MethodParams instance for init(params)
	 * @param constructorParameters
	 *            to use a specific constructor based on the specified parameters and passing these to it when doing the
	 *            'new'
	 * @return the new instance, don't forget to assign this to a variable (FIXME: I wonder if we can do a warning on
	 *         this?)
	 */
	public static <T extends Initer> T getNewInstanceAndInit( Class<T> type, MethodParams params,
			Object... constructorParameters ) {

		T ret = Factory.getGenericNewInstance( type, constructorParameters );
		Factory.init( ret, params );
		RunTime.assumedTrue( ret.isInited() );
		return ret;
	}
	
	/**
	 * @param <T>
	 * @param instance
	 *            non null instance to call .init(params) on
	 * @param params
	 *            can be null
	 */
	public static <T extends Initer> void init( T instance, MethodParams params ) {

		RunTime.assumedNotNull( instance );
		if ( instance.isInited() ) {
			RunTime.badCall( "must not be already init-ed" );
		}
		// must add before init because init may init others before we get to add so the order gets foobar-ed
		// addNewInitedInstance( instance );// shouldn't already exist since wasn't inited so not in our list
		
		set_InitWork_StartsInParent( instance );
		// so if anything else between Start and Done gets inited, it does so as children of 'instance'
		try {
			instance._init( params );// params may be null
		} finally {
			set_InitWork_DoneInParent( instance );
		}
		
		// addNewInitedInstance_asAfterInit( instance );
		// goShallowToThisParentInstanceAsChild( instance );
		
		RunTime.assumedTrue( instance.isInited() );
	}
	
	// private static <T extends Initer> void internal_init( T instance, MethodParams params ) {
	//
	// RunTime.assumedNotNull( instance );
	// RunTime.assumedFalse( instance.isInited() );
	//
	// RunTime.assumedTrue( instance.isInited() );
	// }
	
	/**
	 * @param <T>
	 * @param instance
	 *            the parent
	 */
	private static <T extends Initer> void set_InitWork_StartsInParent( T instance ) {

		// go deep
		RunTime.assumedNotNull( instance );
		RunTime.assumedNotNull( currentParent );
		// TreeOfUniqueNonNullObjects<Initer> newChildInCurrent = new TreeOfUniqueNonNullObjects<Initer>();
		// newChildInCurrent.setParent( currentParent );
		// newChildInCurrent.setValue( instance );
		currentParent = currentParent.addChildFirst( instance );
		RunTime.assumedNotNull( currentParent );
	}
	
	/**
	 * @param <T>
	 * @param instance
	 *            the parent
	 */
	private static <T extends Initer> void set_InitWork_DoneInParent( T instance ) {

		// go shallow
		RunTime.assumedNotNull( instance );
		RunTime.assumedNotNull( currentParent );
		RunTime.assumedTrue( root != currentParent );// can't be root yet
		RunTime.assumedTrue( currentParent.getValue() == instance );
		currentParent = currentParent.getParent();
		RunTime.assumedNotNull( currentParent );
	}
	
	/**
	 * after this you can call reInit
	 * 
	 * @param params
	 */
	public static <T extends Initer> void deInit( T instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedTrue( instance.isInited() );
		
		removeExistingInitedInstance( instance );
		internal_deInit( instance );
		
		RunTime.assumedFalse( instance.isInited() );
	}
	
	/**
	 * internal<br>
	 * this will just call deInit, it will not remove the instance from list<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	private static <T extends Initer> void internal_deInit( T instance ) {

		RunTime.assumedNotNull( instance );
		Log.special( "deInit-ing " + instance.getClass().getName() );
		instance._deInit();
	}
	
	/**
	 * this will keep the instance (ie. no new again) and just do a deInit() and init()<br>
	 * it MUST be already inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( T instance ) {

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
	 * it MUST NOT be already inited, else this will throw<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void reInit_aka_InitAgain_WithOriginalPassedParams( T instance ) {

		RunTime.assumedNotNull( instance );
		RunTime.assumedFalse( instance.isInited() );
		// must NOT be already in our list
		reInitIfNotInited( instance );
	}
	
	/**
	 * whether this was or not inited already, after this call it will be reInited()<br>
	 * NOTE: that an init() should've been called on this instance at least once before, else the parameters passed to
	 * start() are null since init() didn't clone-save them before<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void reInitIfNotInited( T instance ) {

		RunTime.assumedNotNull( instance );
		if ( !instance.isInited() ) {
			// not inited then we reInit it as follows:
			
			if ( ALL_INITED_INSTANCES.containsObject( instance ) ) {
				RunTime.bug( "bug somewhere this instance must NOT be in the list, and it is in list and it's also not inited?!!" );
			}
			
			// must add before init see on init() why
			addNewInitedInstance( instance );
			
			try {
				instance._reInit_aka_initAgain_WithOriginalPassedParams();
			} finally {
				if ( !instance.isInited() ) {
					// removeExistingInitedInstance( instance );
					RunTime.bug( "so, reinit failed but it's still not inited, bug somewhere since a call to reInit "
							+ "should always set the isInited() flag regardless of thrown exceptions, to pave the road "
							+ "for a latter deInit()" );
				}
			}
		}
		
		RunTime.assumedTrue( instance.isInited() );
	}
	
	/**
	 * basically, the last one added is first and the order to deInit is from first to last<br>
	 * NOTE that if variables are going to be inited in the new instance's init, then they are not first, and
	 * these
	 * instances are first and they will deinit their own variables which follow<br>
	 * 
	 * @param instance
	 */
	@Deprecated
	private final static void temporary_addNewInitedInstance( Initer instance ) {

		// we're adding these to first, and remove-all will parse from last to first (order)
		RunTime.assumedNotNull( instance );
		// yeah must not be inited already but soon after exiting this method it will get init call
		RunTime.assumedFalse( instance.isInited() );
		// System.out.println( "added: " + instance.getClass().getName() );
		if ( ALL_INITED_INSTANCES.addFirstQ( instance ) ) {
			RunTime.badCall( "should not have existed" );
		}
	}
	
	@Deprecated
	private final static void temporary_removeExistingInitedInstance( Initer instance ) {

		RunTime.assumedNotNull( instance );
		// must be already inited but next after this call, a deInit is issued
		RunTime.assumedTrue( instance.isInited() );
		// Log.entry( instance.toString() );
		if ( !ALL_INITED_INSTANCES.removeObject( instance ) ) {
			RunTime.badCall( "should've existed" );
		}
	}
	
	
	/**
	 * this will try to postpone all exceptions until after done deInit-ing all<br>
	 */
	public static void deInitAll() {

		Log.entry();
		
		RunTime.assumedTrue( currentParent == root );
		RunTime.assumedNotNull( root );
		internal_deInitTree( root );
		// re-throw all postponed exceptions:
		RunTime.throwPosponed();
	}// method
	
	/**
	 * this will also postpone all throws so use RunTime.throwPosponed(); after calling this<br>
	 * 
	 * @param tree
	 */
	private static void internal_deInitTree( TreeOfNonNullObjects<Initer> tree ) {

		try {
			RunTime.assumedNotNull( tree );// 1t
			while ( true ) {
				// get the first subtree in this tree (aka next in our context)
				TreeOfNonNullObjects<Initer> currentSubTree = tree.getChildAt( Position.FIRST );// 1t
				if ( null != currentSubTree ) {
					try {
						Initer instance = currentSubTree.getValue();
						// first deInit
						try {
							Factory.internal_deInit( instance );
						} finally {
							// second, remove; even if above threw
							root.removeChildAt( Position.FIRST );
						}
					} catch ( Throwable t ) {
						// postpone all that were thrown (or re-thrown) with RunTime.thro()
					} finally {
						
					}
				} else {
					// no more subtrees so we're at
				}
			}// while true
		} catch ( Throwable t ) {// 1c
			// postponing all
		}
	}
}// class
