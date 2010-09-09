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
import org.references.Position;
import org.references.TreeOfNonNullObjects;
import org.references.method.MethodParams;



/**
 * this does not work for inner classes, to init them fails; unless it's an inner public static class 'cause obviously
 * its default constructor must be accessible from Factory class<br>
 * 
 */

public class Factory
{
	
	// MAX_POSSIBLE_DEFINED_PARAMS_A_METHOD_WILL_EVER_HAVE
	private static final int	MAX_DEFINED_PARAMS	= 50;
	private static final int	SUBCLASS			= 1;
	private static final int	SAMECLASS			= MAX_DEFINED_PARAMS
														* ( 1 + SUBCLASS );
	
	static
	{
		RunTime.assumedTrue( SAMECLASS > SUBCLASS );
		RunTime.assumedTrue( SAMECLASS > SUBCLASS
											* MAX_DEFINED_PARAMS );
	}
	
	
	// this will make sure you don't miss calling deInitAll() even if JVM gets interrupted or something
	// static
	// { // Factory INIT
	// // FIXME: temporarily disabled
	// @SuppressWarnings( "unused" )
	// ShutDownHook o = new ShutDownHook(
	// new Thread(
	// null,
	// null,
	// "shutdown-hook thread" )
	// {
	//
	// @Override
	// public
	// void
	// run()
	// {
	//
	// Log.special( "on Factory shutdown-hook entry" );
	// RunTime.throwAllThatWerePosponed();
	// RunTime.clearThrowChain();
	// // Factory.deInitAll();
	// // throw
	// // new
	// // RuntimeException(
	// // "taest"
	// // );
	// }
	// } );
	// }
	
	// static int initDepth = 1;
	// static int deInitDepth = 1;
	
	// LIFO list tracking all instances of ALL subclasses that are inited
	// add new ones to first, and when remove-all start from last to first
	// LIFO manner add/remove on this tree thingy
	// hardwired: add any new ones to first;
	// this tree is a list of lists; the depth is when each class that got .init()-ed also inits it's own instances
	// inside that init(); if a class inits new instances while not in it's own init, then's ok they're just added on
	// the current level ie. root
	// private final static TreeOfNonNullObjects<Initer> root = new TreeOfNonNullObjects<Initer>();
	// private static TreeOfNonNullObjects<Initer> currentParent = root;
	// private final static NonNullHashMap<Initer, TreeOfNonNullObjects<Initer>> QUICK_FIND = new NonNullHashMap<Initer,
	// TreeOfNonNullObjects<Initer>>();
	

	/**
	 * generic method with a type variable<br>
	 * this will do a new and an init() with no params
	 * 
	 * @param <T>
	 * 
	 * @param parentDeIniterInstance
	 * @param listOfOtherMoreParents
	 * 
	 * @param type
	 *            any subclass of StaticInstanceTracker
	 * @param constructorParameters
	 *            a list of objects to be passed to the constructor(which is auto found based on objects passed)<br>
	 *            can be unspecified aka null<br>
	 * @return the new instance, don't forget to assign this to a variable (FIXME: I wonder if we can do a warning on
	 *         this?)
	 */
	public static
			<T extends Initer>
			T
			getNewInstanceAndInitWithoutMethodParams(
														/*
														 * Initer parentDeIniterInstance,
														 * Initer[] listOfOtherMoreParents,
														 */
														Class<T> type,
														Object... constructorParameters )
	{
		
		return getNewInstanceAndInit(
										/*
										 * parentDeIniterInstance,
										 * listOfOtherMoreParents,
										 */
										type,
										null,
										constructorParameters );
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
	private static
			<T extends Initer>
			T
			getGenericNewInstance(
									Class<T> type,
									Object... initargsObjects )
	{
		
		T ret = null;
		
		Constructor<T> con = null;
		try
		{
			int until = initargsObjects.length;
			if ( ( null == initargsObjects )
					|| ( until <= 0 ) )
			{
				con = type.getConstructor();// get the default public constructor - the one with no params
			}
			else
			{
				// Class<?>[] initargsClasses = null;
				// if ( null != initargsObjects )
				// {
				// initargsClasses = new Class<?>[initargsObjects.length];
				// RunTime.assumedTrue( initargsClasses.length == initargsObjects.length );
				// for ( int i = 0; i < initargsObjects.length; i++ )
				// {
				// initargsClasses[i] = initargsObjects[i].getClass();
				// }
				// }
				// con = type.getConstructor( initargsClasses );
				int max = 0;
				Constructor<?>[] allConstructors = type.getConstructors();
				
				findTheRightConstructor:
				for ( Constructor<?> curConstr : allConstructors )
				{
					// System.out.println( curConstr );
					Class<?>[] constrParams = curConstr.getParameterTypes();
					if ( constrParams.length == until )
					{
						// System.out.println( constrParams.length );
						// so we're on a constructor with same ammount of params
						int cur = 0;
						

						// checkParams:
						for ( int i = 0; i < until; i++ )
						{
							// System.out.println( constrParams[i] );
							Class<?> expectedClass = initargsObjects[i].getClass();
							if ( constrParams[i] == expectedClass )
							{
								cur += SAMECLASS;
							}
							else
							{
								if ( constrParams[i].isAssignableFrom( expectedClass ) )
								{
									// the left one is a superclass or same as the right one
									cur += SUBCLASS;
								}
								else
								{
									// no matching param, move to next constructor
									// break checkParams;
									continue findTheRightConstructor;
								}
							}
						}// for params
						
						if ( ( cur > SAMECLASS
										* until )
								|| ( cur <= 0 )
								|| ( cur < SUBCLASS
											* until ) )
						{
							RunTime.bug();
						}
						else
						{
							if ( ( cur >= until
											* SUBCLASS )
									&& ( cur < until
												* SAMECLASS ) )
							{
								// the passed parameters are 1) all subclasses compared to the ones defined
								// on the constructor, so it's a valid contructor so far, but we might find
								// better so keep looking
								// OR 2) a mix of SAMECLASS -es and SUBCLASS -es
								if ( max < cur )
								{// found better version
									max = cur;
									con = (Constructor<T>)curConstr;
								}
								else
								{
									if ( max == cur )
									{
										RunTime.bug( "ambigous constructors present in class: "
														+ type );
									}// else found lesser version
								}
								continue;// try next constructor maybe we find better version
							}
							else
							{
								if ( until
										* SAMECLASS == cur )
								{
									// perfect match, all parameters match type perfectly so there can't be
									// another constructor like this
									con = (Constructor<T>)curConstr;
									// System.out.println( "Perfection: " + con + " for type: " + type );
									break findTheRightConstructor;
								}
								else
								{
									RunTime.bug( "unhandled IF variant" );
								}
							}
						}
					}
				}
			}
		}
		catch ( SecurityException e )
		{
			RunTime.bug(
							e,
							"method not accessible ie. private init() method instead of public" );
		}
		catch ( NoSuchMethodException e )
		{
			RunTime
					.bug(
							e,
							"private default constructor? or a public one doesn't exist ? "
									+ "or you're calling this on an inner class which is not public static; "
									+ "and yet we do have the right class or subclass of Initer; "
									+ "or default constructor not explicitly defined"
									+ "OR you're using that on a generic class ie. GenericClass<A,B> using it as GenericClass.class"
									+ "that won't work, you need to 'new' that yourself and then call Factory.init()" );
		}
		
		// RunTime.assumedNotNull( con );
		if ( null == con )
		{
			RunTime
					.badCall( "either the class("
								+ type
								+ ") doesn't have the required constuctor OR "
								+ "you're trying to init an inner class which means you must prepend a 'this' to constructor params" );
		}
		else
		{
			try
			{
				ret = con.newInstance( initargsObjects );// no params constructor
				// ret = type.newInstance(); this works but we want to catch more exceptions above
			}
			catch ( IllegalArgumentException e )
			{
				RunTime.bug( e );
			}
			catch ( InstantiationException e )
			{
				RunTime.bug( e );
			}
			catch ( IllegalAccessException e )
			{
				RunTime.bug( e );
			}
			catch ( InvocationTargetException e )
			{
				RunTime.bug( e );
				// eclipse bug gone since this part was moved here
			}
		}
		RunTime.assumedNotNull( ret );
		return ret;
	}
	

	/**
	 * this will do a new with the default constructor which should be public <br>
	 * and an .init(params)<br>
	 * 
	 * @param parentDeIniterInstance
	 *            the parent instance to this new instance, that will deInit this new instance when it is
	 *            deInited itself; so this means we won't deinit this new instance before we deinit this parent
	 *            instance; instead we will deinit this parent instance and it will auto deinit this new instance
	 * @param listOfOtherMoreParents
	 *            other parents to this new instance that means they will not be deInited before this new instance is
	 *            deInited; Note that <tt>parentDeIniterInstance</tt> is already considered a parent, do not specify it
	 *            again inhere<br>
	 *            can use new Initer[]{a,b,c,d}
	 * 
	 * @param typeOfInstanceToInit
	 *            any subclass of Initer, EXCEPT generic classes ie. GenericClass<X,Y,Z> for these you have to new
	 *            manually and then call {@link #init(Initer, MethodParams)} OR {@link #initWithoutParams(Initer)}
	 * @param methodParams
	 *            MethodParams instance for init(params)<br>
	 *            can be null
	 * @param constructorParams
	 *            to use a specific constructor based on the specified parameters and passing these to it when doing the
	 *            'new'<br>
	 *            can be unspecified or nulls<br>
	 *            can use comma to separate them<br>
	 * @return the new instance, don't forget to assign this to a variable (FIXME: I wonder if we can do a warning on
	 *         this?)
	 */
	public static
			<T extends Initer>
			T
			getNewInstanceAndInit(
									/*
									 * Initer parentDeIniterInstance,//TODO: this param we need
									 * Initer[] listOfOtherMoreParents,FIXME: we can do w/o this param
									 */
									Class<T> typeOfInstanceToInit,
									MethodParams methodParams,
									Object... constructorParams )
	{
		
		T ret = Factory.getGenericNewInstance(
												typeOfInstanceToInit,
												constructorParams );
		
		// TreeOfNonNullObjects<Initer> parent = null;
		// // try to find an already inited instance from constructorParameters and use that as a parent for this init
		// // because that means this 'ret' instance uses that parent instance and we will have to deinit that first
		// for ( Object param : constructorParams )
		// {
		// if ( null != param )
		// {
		// if ( param instanceof Initer )
		// {
		// // is Initer or subclass of Initer
		// parent = QUICK_FIND.getValue( (Initer)param );
		// if ( parent != null )
		// {
		// System.out.println( parent.getValue().getClass().getCanonicalName() );
		// break;// FIXME: we cannot handle more than one parent that this "ret" instance depends upon
		// }
		// }
		// }
		// }
		//
		// TreeOfNonNullObjects<Initer> savedParent = null;
		// if ( null != parent )
		// {
		// RunTime.assumedNotNull( parent.getValue() );
		// // set_InitWork_StartsInParent( parent.getValue() );
		// savedParent = currentParent;
		// currentParent = parent;
		//
		// // StackTraceElement caller = RunTime.getTheCaller_OutsideOfThisClass();
		// // System.out.println( String.format(
		// // "%-" + initDepth * 3 + "s",
		// // " " ) + "i->" + initDepth + " "
		// // + parent.getValue().getClass().getSimpleName() + " caller: " + caller );
		// // initDepth++;
		// }
		// try
		// {
		Factory.init(
						ret,
						methodParams );// /////////////////////////
		// }
		// finally
		// {
		// if ( null != parent )
		// {
		// RunTime.assumedNotNull(
		// parent.getValue(),
		// currentParent );
		// RunTime.assumedTrue( currentParent.getParent() == savedParent );
		// currentParent = savedParent;
		// // set_InitWork_DoneInParent( parent.getValue() );
		// // initDepth--;
		// }
		// }
		RunTime.assumedTrue( ret.isInitedSuccessfully() );
		return ret;
	}
	

	public static
			<T extends Initer>
			void
			initWithoutParams(
								T instance )
	{
		
		Factory.init(
						instance,
						null );
	}
	

	/**
	 * @param <T>
	 * @param instance
	 *            non null instance to call .init(params) on
	 * @param params
	 *            can be null
	 */
	public static
			<T extends Initer>
			void
			init(
					T instance,
					MethodParams params )
	{
		
		RunTime.assumedNotNull( instance );
		if ( instance.isInitingOrInited() )
		{
			RunTime.badCall( "must not be already init-ed or in the process of initing(but failed due to exceptions)" );
		}
		

		// String factClassThisMethod = RunTime.getCurrentMethodName();// stea[1].getMethodName();
		// System.out.println( "!" + factClassThisMethod );
		
		// ,RunTime.getCurrentMethodName())
		// System.out.println( RunTime.getCurrentStackTraceElement() );
		// must add before init because init may init others before we get to add so the order gets foobar-ed
		// addNewInitedInstance( instance );// shouldn't already exist since wasn't inited so not in our list
		
		// set_InitWork_StartsInParent( instance );
		// so if anything else between Start and Done gets inited, it does so as children of 'instance'
		// try
		// {
		instance._init( params );// params may be null
		Log.exit1( "init-ed: "
					+ instance.getClass().getCanonicalName() );
		// }
		// finally
		// {
		// set_InitWork_DoneInParent( instance );
		// // System.out.println( "i<-" + depth + " " + caller );
		// // System.out.println( String.format( "%-" + initDepth * 3 + "s", " " ) + "i<-" + initDepth + " "
		// // + instance.getClass().getSimpleName() + " caller: " + caller );
		//
		// }
		
		// addNewInitedInstance_asAfterInit( instance );
		// goShallowToThisParentInstanceAsChild( instance );
		
		RunTime.assumedTrue( instance.isInitedSuccessfully() );
	}
	

	// private static <T extends Initer> void internal_init( T instance, MethodParams params ) {
	//
	// RunTime.assumedNotNull( instance );
	// RunTime.assumedFalse( instance.isInited() );
	//
	// RunTime.assumedTrue( instance.isInited() );
	// }
	
	// /**
	// * @param <T>
	// * @param instance
	// * the parent
	// */
	// private static
	// <T extends Initer>
	// void
	// set_InitWork_StartsInParent(
	// T instance )
	// {
	//
	// // go deep
	// RunTime.assumedNotNull( instance );
	//
	//
	// // StackTraceElement caller = RunTime.getTheCaller_OutsideOfThisClass();
	// // System.out.println( String.format(
	// // "%-" + initDepth * 3 + "s",
	// // " " ) + "i->" + initDepth + " " + instance.getClass().getSimpleName()
	// // + " caller: " + caller );
	// // initDepth++;
	//
	// // System.out.println( "STRT:" + instance.getClass().getName() + "/" + instance );
	// RunTime.assumedNotNull( currentParent );
	// // TreeOfUniqueNonNullObjects<Initer> newChildInCurrent = new TreeOfUniqueNonNullObjects<Initer>();
	// // newChildInCurrent.setParent( currentParent );
	// // newChildInCurrent.setValue( instance );
	// currentParent = currentParent.addChildAtPos(
	// Position.FIRST,
	// instance );// hardwired to first, LIFO
	// RunTime.assumedFalse( QUICK_FIND.put(
	// instance,
	// currentParent ) );
	//
	// RunTime.assumedNotNull( currentParent );
	// }
	

	// /**
	// * @param <T>
	// * @param instance
	// * the parent
	// */
	// private static
	// <T extends Initer>
	// void
	// set_InitWork_DoneInParent(
	// T instance )
	// {
	//
	//
	// RunTime.assumedNotNull( instance );
	// // initDepth--;
	// // System.out.println( "DONE:" + instance.getClass().getName() + "/" + instance );
	// RunTime.assumedNotNull( currentParent );
	// RunTime.assumedTrue( root != currentParent );// can't be root yet
	// RunTime.assumedTrue( currentParent.getValue() == instance );
	// RunTime.assumedTrue( QUICK_FIND.getValue( instance ) == currentParent );
	// // go shallow
	// currentParent = currentParent.getParent();
	// RunTime.assumedNotNull( currentParent );// can be root, but not higher
	// }
	

	/**
	 * throws if not inited or was in the process of initing but failed due to thrown exceptions<br>
	 * 
	 * @see #deInitIfInited_WithPostponedThrows(Initer)
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static
			<T extends Initer>
			void
			deInit_WithPostponedThrows(
										T instance )
	{
		
		RunTime.assumedNotNull( instance );
		if ( !instance.isInitingOrInited() )
		{
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
	public static
			<T extends Initer>
			void
			deInitIfInited_WithPostponedThrows(
												T instance )
	{
		try
		{
			RunTime.assumedNotNull( instance );
		}
		catch ( Throwable t )
		{
			RunTime.throPostponed( t );
			return;
		}
		if ( !instance.isInitingOrInited() )
		{
			return;// silently
		}
		
		try
		{
			// first deInit()
			// this will also deInit all in the subTree because that class will deInit all those that it inited itself;
			// and those are the ones we have in our subtree
			internal_deInit( instance );
		}
		catch ( Throwable t )
		{
			// postpone throws
			RunTime.throPostponed( t );
		}
		// finally
		// {
		// // then remove from tree and from QUICK_FIND
		// if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) )
		// {
		// RunTime.bug( "failed to find it or remove it, so forgot to add instance to QUICK_FIND ? somewhere" );
		// }
		// }
		
		RunTime.assumedFalse( instance.isInitingOrInited() );
		// don't recall postponed here
	}
	

	/**
	 * after this you can call reInit
	 * 
	 * @param instance
	 * @param <T>
	 * 
	 * @see #deInitIfAlreadyInited(Initer)
	 */
	public static
			<T extends Initer>
			void
			deInit(
					T instance )
	{
		
		Factory.deInit_WithPostponedThrows( instance );
		RunTime.throwAllThatWerePosponed();
	}
	

	/**
	 * only deInit if it's inited, so in other words it won't throw if not inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static
			<T extends Initer>
			void
			deInitIfAlreadyInited(
									T instance )
	{
		
		Factory.deInitIfInited_WithPostponedThrows( instance );
		RunTime.throwAllThatWerePosponed();
	}
	

	// /**
	// * FIXME: if any subtrees are found in this instance they're added on the root level(no need to add them to same
	// * level! they'll be passed back up to root anyway) , but instance will
	// * be gone<br>
	// *
	// * @param <T>
	// * @param instance
	// * @return false if not found; true if found and removed
	// */
	// private static
	// <T extends Initer>
	// boolean
	// removeAnyInstanceFromAnywhereInOurLists(
	// T instance )
	// {
	//
	// TreeOfNonNullObjects<Initer> subTree = QUICK_FIND.getValue( instance );
	// if ( null == subTree )
	// {// can't be null here
	// return false;
	// }
	// RunTime.assumedTrue( root != subTree );// it's never root
	//
	// RunTime.assumedTrue( QUICK_FIND.remove( instance ) == subTree );
	// // the subTree must be empty already if not, then
	// if ( !subTree.isEmpty() )
	// {
	// Log.warn2( "subtree not empty(" + subTree.size() + "), means we got a class ("
	// + instance.getClass().getName()
	// + ") that didn't properly deInit all the variables it created+inited" );
	// // FIXME: deInit leftover subTree(s) else they're just lost here OR merge this subtree's children with
	// // the root tree(not to parent tree) in the same LIFO manner
	// // FIXME: commented below:
	//
	// final Position tmpPos = Position.FIRST;// hardwired to FIRST
	// while ( !subTree.isEmpty() )
	// {
	// TreeOfNonNullObjects<Initer> t = subTree.getChildAt( Position.LAST );// hardwired to LAST(opposite to
	// // FIRST)
	// RunTime.assumedNotNull( t );
	//
	// Initer inst = t.getValue();
	// // RunTime.assumedTrue( QUICK_FIND.remove( inst ) == t );
	// RunTime.assumedTrue( subTree.removeChild( t ) );
	// t = root.addChildAtPos(
	// tmpPos,
	// inst );
	// RunTime.assumedTrue( root.getChildAt( tmpPos ) == t );// first
	// RunTime.assumedTrue( QUICK_FIND.put(
	// inst,
	// t ) );
	// RunTime.assumedTrue( QUICK_FIND.getValue( inst ) == t );
	// }
	// }
	// TreeOfNonNullObjects<Initer> parent = subTree.getParent();
	// // if ( parent == root ) {
	// // System.out.println( "ROOT on: " + instance );
	// // }
	// RunTime.assumedNotNull( parent );// can't be null, at worst is == root
	// RunTime.assumedFalse( parent.isEmpty() );
	// RunTime.assumedTrue( parent.removeChild( subTree ) );// ie. root.remove this subTree
	// return true;
	// }
	

	/**
	 * internal<br>
	 * this will just call deInit, it will not remove the instance from list<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	private static
			<T extends Initer>
			void
			internal_deInit(
								T instance )
	{
		
		RunTime.assumedNotNull( instance );
		Log.entry1( "deInit-ing "
					+ instance.getClass().getName() );
		
		// StackTraceElement caller = RunTime.getTheCaller_OutsideOfThisClass();
		// System.out.println( String.format(
		// "%-" + deInitDepth * 3 + "s",
		// " " ) + "d->" + deInitDepth + " " + instance.getClass().getSimpleName()
		// + " caller: " + caller );
		// deInitDepth++;
		try
		{
			instance._deInit();
		}
		finally
		{
			// deInitDepth--;
			// System.out.println( String.format( "%-" + deInitDepth * 3 + "s", " " ) + "d<-" + deInitDepth + " "
			// + instance.getClass().getSimpleName() + " caller: " + caller );
		}
	}
	

	/**
	 * this will keep the instance (ie. no new again) and just do a deInit() and init()<br>
	 * it MUST be already inited<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static
			<T extends Initer>
			void
			restart_aka_DeInitAndInitAgain_WithOriginalPassedParams(
																		T instance )
	{
		
		RunTime.assumedNotNull( instance );
		if ( !instance.isInitingOrInited() )
		{
			RunTime.badCall( "must be inited" );
		}
		// must be already in our list
		// if ( null == QUICK_FIND.getValue( instance ) )
		// {
		// RunTime.bug( "bug somewhere this instance should've been added before, or it's a badcall" );
		// }
		try
		{
			instance._restart_aka_deInit_and_initAgain_WithOriginalPassedParams();
		}
		finally
		{
			if ( !instance.isInitingOrInited() )
			{
				// so it was inited before this call, but now it's not inited anymore which means a deInit happened and
				// remained and thus we need to remove this instance from our lists
				// try
				// {
				// if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) )
				// {
				// RunTime.bug( "weird, this should've existed in our lists" );
				// }
				// }
				// finally
				// {
				RunTime.bug( "the restart(aka deInit+Init) caused the instance to become deInit-ed or bug somewhere" );
				// }
			}
		}
		RunTime.assumedTrue( instance.isInitedSuccessfully() );
	}
	

	/**
	 * this will keep the instance (ie. no new again) and just do a init() again<br>
	 * it MUST NOT be already inited, else this will throw<br>
	 * 
	 * @param <T>
	 * @param instance
	 */
	public static
			<T extends Initer>
			void
			reInit_aka_InitAgain_WithOriginalPassedParams(
															T instance )
	{
		
		RunTime.assumedNotNull( instance );
		RunTime.assumedFalse( instance.isInitingOrInited() );
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
	public static
			<T extends Initer>
			void
			reInitIfNotInited(
								T instance )
	{
		
		RunTime.assumedNotNull( instance );
		if ( !instance.wasInitedEver() )
		{
			RunTime.badCall( "was never inited not even once, you shouldn't use this method then" );
		}
		if ( !instance.isInitingOrInited() )
		{
			// not inited then we reInit it as follows:
			
			// if ( null != QUICK_FIND.getValue( instance ) )
			// {
			// RunTime
			// .bug(
			// "bug somewhere this instance must NOT be in the list, and it is in list and it's also not inited?!!" );
			// }
			
			// must add before init see on init() why
			// addNewInitedInstance( instance );
			// set_InitWork_StartsInParent( instance );
			// try
			// {
			instance._reInit_aka_initAgain_WithOriginalPassedParams();
			// }
			// finally
			// {
			// //set_InitWork_DoneInParent( instance );
			// }
			
			if ( !instance.isInitedSuccessfully() )
			{
				// removeExistingInitedInstance( instance );
				RunTime
						.bug( "so, reinit failed but it's still not inited, bug somewhere since a call to reInit "
								+ "should always set the isInited() flag regardless of thrown exceptions, to pave the road "
								+ "for a latter deInit()" );
			}
		}
		
		RunTime.assumedTrue( instance.isInitedSuccessfully() );
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
	
	// /**
	// * this will try to postpone all exceptions until after done deInit-ing all<br>
	// * this will deInit all in LIFO manner, also note that if a class inited other instances white inside it's init
	// * method, then those will be deInited first (since they're stored as a tree, the root is the class that inited
	// * first) BUT <br>
	// * FIXME: if a class that was already inited does more inits while running then those inits will be
	// * deInited before that class, so when that class will be deInited it will probably call some deInits in it's done
	// * method and it will get that they were already deinited and likely some exception will be thrown<br>
	// * FIXME: we need to fix that (above) by putting all inits done from within class X into the tree at the right
	// * position so that we deinit the class first and then we see if it left anything still inited for us to
	// deinit<br>
	// */
	// @Deprecated
	// public static
	// void
	// deInitAll()
	// {
	//
	// try
	// {
	// Log.entry();
	// }
	// catch ( Throwable t )
	// {
	// // postpone
	// RunTime.throPostponed( t );
	// }
	//
	// RunTime.assumedNotNull( currentParent );
	// RunTime.assumedTrue( currentParent == root );
	// RunTime.assumedNotNull( root );
	//
	// System.out.println( root.size() );// FIXME: remove
	//
	// while ( true )
	// {
	// // get the last subtree in root (aka next in our context)
	// // which means get the one which was first inited FIFO manner
	// TreeOfNonNullObjects<Initer> currentSubTree = root.getChildAt( Position.FIRST );// hardwired: to first LIFO
	// if ( null == currentSubTree )
	// {
	// // no subtrees exist in root then:
	// // root is empty, consistency check
	// RunTime.assumedTrue( root.isEmpty() );
	// break;// exit while
	// }
	//
	// Initer instance = currentSubTree.getValue();
	// RunTime.assumedNotNull( instance );
	// RunTime.assumedTrue( instance.isInited() );
	// // first deInit
	// // this should deInit all subtrees of instance and they should be auto-removed from this subtree
	// try
	// {
	// // this will do a chain deInit; this chain is tree-ed as the current subtree and all its
	// // subtrees
	// // Factory.deInit( instance );// not the internal deInit
	// // NOTE: we didn't call Factory.deInit() here because we want to postpone only internal_deInit()
	// // exceptions
	// internal_deInit( instance );
	// }
	// catch ( Throwable t )
	// {
	// // postpone these throws from deInit only
	// RunTime.throPostponed( t );
	// }
	// finally
	// {
	// // then remove from tree and from QUICK_FIND
	// // allow throws from these: to avoid possible recursive loop repeating w/o ever exiting
	// if ( !removeAnyInstanceFromAnywhereInOurLists( instance ) )
	// {
	// RunTime.bug( "failed to find it or remove it, so forgot to add instance to QUICK_FIND ? somewhere" );
	// }
	// // even if above fails, the subtree should be emptied:
	// RunTime.assumedTrue( currentSubTree.isEmpty() );
	// }
	// }// while true
	//
	// // re-throw all postponed exceptions:
	// RunTime.throwAllThatWerePosponed();
	// }// method
	
}// class
