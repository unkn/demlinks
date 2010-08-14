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
import org.dml.tools.NonNullHashMap;
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
	
	// this will make sure you don't miss calling deInitAll() even if JVM gets interrupted or something
	static { // Factory INIT
		new ShutDownHook( new Thread( null, null, "shutdown-hook thread" ) {
			
			@Override
			public void run() {

				Log.special( "on Factory shutdown-hook entry" );
				Factory.deInitAll();
				// throw
				// new
				// RuntimeException(
				// "taest"
				// );
			}
		} );
	}
	
	// LIFO list tracking all instances of ALL subclasses that are inited
	// add new ones to first, and when remove-all start from last to first
	// LIFO manner add/remove on this tree thingy
	// hardwired: add any new ones to first;
	// this tree is a list of lists; the depth is when each class that got .init()-ed also inits it's own instances
	// inside that init(); if a class inits new instances while not in it's own init, then's ok they're just added on
	// the current level ie. root
	private final static TreeOfNonNullObjects<Initer>							root			= new TreeOfNonNullObjects<Initer>();
	private static TreeOfNonNullObjects<Initer>									currentParent	= root;
	private final static NonNullHashMap<Initer, TreeOfNonNullObjects<Initer>>	QUICK_FIND		= new NonNullHashMap<Initer, TreeOfNonNullObjects<Initer>>();
	
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
	public static <T extends Initer> T getNewInstanceAndInitWithoutParams( Class<T> type,
			Object... constructorParameters ) {

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
	 *         cannot return null!
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
					+ "or default constructor not explicitly defined"
					+ "OR you're using that on a generic class ie. GenericClass<A,B> using it as GenericClass.class"
					+ "that won't work, you need to 'new' that yourself and then call Factory.init()" );
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
		RunTime.assumedNotNull( ret );
		return ret;
	}
	
	/**
	 * this will do a new with the default constructor which should be public <br>
	 * and an .init(params)<br>
	 * 
	 * @param type
	 *            any subclass of Initer, EXCEPT gerenic classes ie. GenericClass<X,Y,Z> for these you have to new
	 *            manually and then call {@link #init(Initer, MethodParams)} OR {@link #initWithoutParams(Initer)}
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
	
	
	public static <T extends Initer> void initWithoutParams( T instance ) {

		Factory.init( instance, null );
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
		
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		String factClassThisMethod = stea[1].getMethodName();
		System.out.println( "!" + factClassThisMethod );
		StackTraceElement ste;
		String factClassName = Factory.class.getCanonicalName();
		RunTime.assumedNotNull( factClassName );
		int i = 0;
		boolean findFactoryFirst = true;
		while ( i < stea.length ) {
			ste = stea[i];
			i++;
			if ( findFactoryFirst ) {
				if ( factClassName.equals( ste.getClassName() ) ) {
					// System.out.println( ste.getMethodName() );
					// we found this class aka Factory class
					if ( factClassThisMethod.equals( ste.getMethodName() ) ) {
						// we found exactly this method we're in aka init()
						System.out.println( ste );
						findFactoryFirst = false;
					}
				}
			} else {// found factory already, now we must find non-factory
				if ( !factClassName.equals( ste.getClassName() ) ) {
					// we found the one that is outside Factory class and that called us
					System.out.println( ste );
					break;
				}
			}
		}
		
		// must add before init because init may init others before we get to add so the order gets foobar-ed
		// addNewInitedInstance( instance );// shouldn't already exist since wasn't inited so not in our list
		
		set_InitWork_StartsInParent( instance );
		// so if anything else between Start and Done gets inited, it does so as children of 'instance'
		try {
			instance._init( params );// params may be null
			Log.special2( "init-ed: " + instance.getClass().getCanonicalName() );
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
		// System.out.println( "STRT:" + instance.getClass().getName() + "/" + instance );
		RunTime.assumedNotNull( currentParent );
		// TreeOfUniqueNonNullObjects<Initer> newChildInCurrent = new TreeOfUniqueNonNullObjects<Initer>();
		// newChildInCurrent.setParent( currentParent );
		// newChildInCurrent.setValue( instance );
		currentParent = currentParent.addChildFirst( instance );// hardwired to first
		RunTime.assumedFalse( QUICK_FIND.put( instance, currentParent ) );
		RunTime.assumedNotNull( currentParent );
	}
	
	/**
	 * @param <T>
	 * @param instance
	 *            the parent
	 */
	private static <T extends Initer> void set_InitWork_DoneInParent( T instance ) {

		
		RunTime.assumedNotNull( instance );
		// System.out.println( "DONE:" + instance.getClass().getName() + "/" + instance );
		RunTime.assumedNotNull( currentParent );
		RunTime.assumedTrue( root != currentParent );// can't be root yet
		RunTime.assumedTrue( currentParent.getValue() == instance );
		RunTime.assumedTrue( QUICK_FIND.getValue( instance ) == currentParent );
		// go shallow
		currentParent = currentParent.getParent();
		RunTime.assumedNotNull( currentParent );// can be root, but not higher
	}
	
	/**
	 * throws if not inited<br>
	 * 
	 * @see #deInitIfInited_WithPostponedThrows(Initer)
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void deInit_WithPostponedThrows( T instance ) {

		RunTime.assumedNotNull( instance );
		if ( !instance.isInited() ) {
			RunTime.badCall( "not already inited" );
		}
		deInitIfInited_WithPostponedThrows( instance );
	}
	
	/**
	 * only throws from instance._deInit() are postponed; not others that are related to successfully processing this
	 * method in its
	 * system<br>
	 * -in other words, exceptions thrown by this engine which handles all these wrapping around stuff are thrown as
	 * they
	 * occur which usually shouldn't happen unless engine needs some bugfixing<br>
	 * 
	 * @param <T>
	 * @param instance
	 *            must not be null, can be deInited already - will not throw in this latter case
	 */
	public static <T extends Initer> void deInitIfInited_WithPostponedThrows( T instance ) {

		RunTime.assumedNotNull( instance );
		if ( !instance.isInited() ) {
			return;// silently
		}
		
		try {
			// first deInit()
			// this will also deInit all in the subTree because that class will deInit all those that it inited itself;
			// and those are the ones we have in our subtree
			internal_deInit( instance );
		} catch ( Throwable t ) {
			// postpone throws
			RunTime.throPostponed( t );
		} finally {
			// then remove from tree and from QUICK_FIND
			if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) ) {
				RunTime.bug( "failed to find it or remove it, so forgot to add instance to QUICK_FIND ? somewhere" );
			}
		}
		
		RunTime.assumedFalse( instance.isInited() );
		// don't recall postponed here
	}
	
	/**
	 * after this you can call reInit
	 * 
	 * @see #deInitIfAlreadyInited(Initer)
	 * @param params
	 */
	public static <T extends Initer> void deInit( T instance ) {

		Factory.deInit_WithPostponedThrows( instance );
		RunTime.throwAllThatWerePosponed();
	}
	
	/**
	 * only deInit if it's inited, so in other words it won't throw if not inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static <T extends Initer> void deInitIfAlreadyInited( T instance ) {

		Factory.deInitIfInited_WithPostponedThrows( instance );
		RunTime.throwAllThatWerePosponed();
	}
	
	/**
	 * FIXME: if any subtrees are found in this instance they're added on the root level(no need to add them to same
	 * level! they'll be passed back up to root anyway) , but instance will
	 * be gone<br>
	 * 
	 * @param <T>
	 * @param instance
	 * @return false if not found; true if found and removed
	 */
	private static <T extends Initer> boolean removeAnyInstanceFromAnywhereInOurLists( T instance ) {

		TreeOfNonNullObjects<Initer> subTree = QUICK_FIND.getValue( instance );
		if ( null == subTree ) {// can't be null here
			return false;
		}
		RunTime.assumedTrue( root != subTree );// it's never root
		
		RunTime.assumedTrue( QUICK_FIND.remove( instance ) == subTree );
		// the subTree must be empty already if not, then
		if ( !subTree.isEmpty() ) {
			Log.warn2( "subtree not empty(" + subTree.size() + "), means we got a class ("
					+ instance.getClass().getName()
					+ ") that didn't properly deInit all the variables it created+inited" );
			// FIXME: deInit leftover subTree(s) else they're just lost here OR merge this subtree's children with
			// the root tree(not to parent tree) in the same LIFO manner
			// FIXME: commented below:
			while ( !subTree.isEmpty() ) {
				TreeOfNonNullObjects<Initer> t = subTree.getChildAt( Position.LAST );// hardwired to LAST(opposite to
																						// FIRST)
				RunTime.assumedNotNull( t );
				
				Initer inst = t.getValue();
				// RunTime.assumedTrue( QUICK_FIND.remove( inst ) == t );
				RunTime.assumedTrue( subTree.removeChild( t ) );
				t = root.addChildFirst( inst );// hardwired to FIRST
				RunTime.assumedTrue( root.getChildAt( Position.FIRST ) == t );// first
				RunTime.assumedTrue( QUICK_FIND.put( inst, t ) );
				RunTime.assumedTrue( QUICK_FIND.getValue( inst ) == t );
			}
		}
		TreeOfNonNullObjects<Initer> parent = subTree.getParent();
		// if ( parent == root ) {
		// System.out.println( "ROOT on: " + instance );
		// }
		RunTime.assumedNotNull( parent );// can't be null, at worst is == root
		RunTime.assumedFalse( parent.isEmpty() );
		RunTime.assumedTrue( parent.removeChild( subTree ) );// ie. root.remove this subTree
		return true;
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
		Log.special4( "deInit-ing " + instance.getClass().getName() );
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
		if ( null == QUICK_FIND.getValue( instance ) ) {
			RunTime.bug( "bug somewhere this instance should've been added before, or it's a badcall" );
		}
		try {
			instance._restart_aka_deInit_and_initAgain_WithOriginalPassedParams();
		} finally {
			if ( !instance.isInited() ) {
				// so it was inited before this call, but now it's not inited anymore which means a deInit happened and
				// remained and thus we need to remove this instance from our lists
				try {
					if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) ) {
						RunTime.bug( "weird, this should've existed in our lists" );
					}
				} finally {
					RunTime.bug( "the restart(aka deInit+Init) caused the instance to become deInit-ed or bug somewhere" );
				}
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
			
			if ( null != QUICK_FIND.getValue( instance ) ) {
				RunTime.bug( "bug somewhere this instance must NOT be in the list, and it is in list and it's also not inited?!!" );
			}
			
			// must add before init see on init() why
			// addNewInitedInstance( instance );
			set_InitWork_StartsInParent( instance );
			try {
				instance._reInit_aka_initAgain_WithOriginalPassedParams();
			} finally {
				set_InitWork_DoneInParent( instance );
			}
			
			if ( !instance.isInited() ) {
				// removeExistingInitedInstance( instance );
				RunTime.bug( "so, reinit failed but it's still not inited, bug somewhere since a call to reInit "
						+ "should always set the isInited() flag regardless of thrown exceptions, to pave the road "
						+ "for a latter deInit()" );
			}
		}
		
		RunTime.assumedTrue( instance.isInited() );
	}
	
	// /**
	// * basically, the last one added is first and the order to deInit is from first to last<br>
	// * NOTE that if variables are going to be inited in the new instance's init, then they are not first, and
	// * these
	// * instances are first and they will deinit their own variables which follow<br>
	// *
	// * @param instance
	// */
	// @Deprecated
	// private final static void temporary_addNewInitedInstance( Initer instance ) {
	//
	// // we're adding these to first, and remove-all will parse from last to first (order)
	// RunTime.assumedNotNull( instance );
	// // yeah must not be inited already but soon after exiting this method it will get init call
	// RunTime.assumedFalse( instance.isInited() );
	// // System.out.println( "added: " + instance.getClass().getName() );
	// if ( ALL_INITED_INSTANCES.addFirstQ( instance ) ) {
	// RunTime.badCall( "should not have existed" );
	// }
	// }
	//
	// @Deprecated
	// private final static void temporary_removeExistingInitedInstance( Initer instance ) {
	//
	// RunTime.assumedNotNull( instance );
	// // must be already inited but next after this call, a deInit is issued
	// RunTime.assumedTrue( instance.isInited() );
	// // Log.entry( instance.toString() );
	// if ( !ALL_INITED_INSTANCES.removeObject( instance ) ) {
	// RunTime.badCall( "should've existed" );
	// }
	// }
	
	/**
	 * this will try to postpone all exceptions until after done deInit-ing all<br>
	 * this will deInit all in LIFO manner, also note that if a class inited other instances white inside it's init
	 * method, then those will be deInited first (since they're stored as a tree, the root is the class that inited
	 * first) BUT <br>
	 * FIXME: if a class that was already inited does more inits while running then those inits will be
	 * deInited before that class, so when that class will be deInited it will probably call some deInits in it's done
	 * method and it will get that they were already deinited and likely some exception will be thrown<br>
	 * FIXME: we need to fix that (above) by putting all inits done from within class X into the tree at the right
	 * position so that we deinit the class first and then we see if it left anything still inited for us to deinit<br>
	 */
	public static void deInitAll() {

		try {
			Log.entry();
		} catch ( Throwable t ) {
			// postpone
			RunTime.throPostponed( t );
		}
		
		RunTime.assumedNotNull( currentParent );
		RunTime.assumedTrue( currentParent == root );
		RunTime.assumedNotNull( root );
		
		while ( true ) {
			// get the last subtree in root (aka next in our context)
			// which means get the one which was first inited FIFO manner
			TreeOfNonNullObjects<Initer> currentSubTree = root.getChildAt( Position.FIRST );// hardwired: to first LIFO
			if ( null == currentSubTree ) {
				// no subtrees exist in root then:
				// root is empty, consistency check
				RunTime.assumedTrue( root.isEmpty() );
				break;// exit while
			}
			
			Initer instance = currentSubTree.getValue();
			RunTime.assumedNotNull( instance );
			RunTime.assumedTrue( instance.isInited() );
			// first deInit
			// this should deInit all subtrees of instance and they should be auto-removed from this subtree
			try {
				// this will do a chain deInit; this chain is tree-ed as the current subtree and all its
				// subtrees
				// Factory.deInit( instance );// not the internal deInit
				// NOTE: we didn't call Factory.deInit() here because we want to postpone only internal_deInit()
				// exceptions
				internal_deInit( instance );
			} catch ( Throwable t ) {
				// postpone these throws from deInit only
				RunTime.throPostponed( t );
			} finally {
				// then remove from tree and from QUICK_FIND
				// allow throws from these: to avoid possible recursive loop repeating w/o ever exiting
				if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) ) {
					RunTime.bug( "failed to find it or remove it, so forgot to add instance to QUICK_FIND ? somewhere" );
				}
				// even if above fails, the subtree should be emptied:
				RunTime.assumedTrue( currentSubTree.isEmpty() );
			}
		}// while true
		
		// re-throw all postponed exceptions:
		RunTime.throwAllThatWerePosponed();
	}// method
	
}// class
