/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.JUnitCommons;



import org.junit.*;
import org.q.*;
import org.references2.*;
import org.tools.swing.*;
import org.toolza.*;



/**
 * all Suit and Test classes in "tests" src folder, must extend this class, in order to ensure that the tree of
 * exceptions (showed in
 * a swing JFrame) is kept alive (not auto-destroyed when tests are completed)<br>
 * thus JUnit tests are complete when the tree is closed by user<br>
 * LIMITATION: you cannot hook on {@value JUnitPos#JUNIT_STARTS}<br>
 * LIMITATION2: you cannot use a lock and .await() while inside any hooks AND call any of this class' methods from a diff.
 * thread because this will deadlock, {@link #isInsideJUnit()} can be called though
 * lim3: this may only work well on eclipse, it's assumed only 1 thread is the initiator
 */
public class JUnitHooker
{
	
	// static
	// {
	// System.out.println( "static block suite" );
	// }
	
	
	// this will only work if all Suite and test-classes(those containing @Test methods) extend this class!!
	public static long														level						= 0;
	private static ListOfUniqueNonNullObjects<JUnitTestClassStartsAdapter>	allTestClassStartsCallBacks	= null;
	private static ListOfUniqueNonNullObjects<JUnitTestClassEndsAdapter>	allTestClassEndsCallBacks	= null;
	private static ListOfUniqueNonNullObjects<JUnitStartsAdapter>			allStartsCallBacks			= null;
	private static ListOfUniqueNonNullObjects<JUnitEndsAdapter>				allEndsCallBacks			= null;
	
	private static boolean													insideJUnitDetected			= false;
	
	
	// public JUnitHooker()
	// {
	// System.out.println( "base constr called after @BeforeClass" );
	// }
	
	
	// TODO: maybe allow adding specific listeners ie. JUNIT_ENDS and `throw` if already passed as in, called already so you
	// added it too late, this is in order to catch badCalls when you expecting something to be called but it never will
	// private static HashMap<JUnitPos, Boolean> alreadyCalled =
	// new HashMap<JUnitPos, Boolean>();
	
	
	private static synchronized final ListOfUniqueNonNullObjects<JUnitEndsAdapter> getEndsCallBacks()// `this method`
	{
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );// `this method` isn't called while inside
		// itself
		if ( null == allEndsCallBacks ) {
			allEndsCallBacks = new ListOfUniqueNonNullObjects<JUnitEndsAdapter>();
		}
		assert null != allEndsCallBacks;
		return allEndsCallBacks;
	}
	
	
	private static synchronized final ListOfUniqueNonNullObjects<JUnitStartsAdapter> getStartsCallBacks()// `this method`
	{
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );// `this method` isn't called while inside
		// itself
		if ( null == allStartsCallBacks ) {
			allStartsCallBacks = new ListOfUniqueNonNullObjects<JUnitStartsAdapter>();
		}
		assert null != allStartsCallBacks;
		return allStartsCallBacks;
	}
	
	
	private static synchronized final ListOfUniqueNonNullObjects<JUnitTestClassStartsAdapter> getTestClassStartsCallBacks()// `this
																															// method`
	{
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );// `this method` isn't called while inside
		// itself
		if ( null == allTestClassStartsCallBacks ) {
			allTestClassStartsCallBacks = new ListOfUniqueNonNullObjects<JUnitTestClassStartsAdapter>();
		}
		assert null != allTestClassStartsCallBacks;
		return allTestClassStartsCallBacks;
	}
	
	
	private static synchronized final ListOfUniqueNonNullObjects<JUnitTestClassEndsAdapter> getTestClassEndsCallBacks()// `this
																														// method`
	{
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );// `this method` isn't called while inside
		// itself
		if ( null == allTestClassEndsCallBacks ) {
			allTestClassEndsCallBacks = new ListOfUniqueNonNullObjects<JUnitTestClassEndsAdapter>();
		}
		assert null != allTestClassEndsCallBacks;
		return allTestClassEndsCallBacks;
	}
	
	
	
	/**
	 * you can call this even inside a @BeforeClass, but it will not be run before our @BeforeClass defined here (unless ours
	 * would've been overridden)
	 * 
	 * @param listener
	 */
	public static synchronized final void addJUnitListener( final JUnitBaseAdapter listener ) {
		// System.out.println( "adding JUnit listener" );
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );
		assert assumedInsideJUnit();
		assert null != listener;
		// JUnitPos juPos =
		// null;
		final Class<?> superClass = Z.getSuperClassOf( listener );
		assert null != superClass;
		if ( superClass.equals( JUnitEndsAdapter.class ) ) {
			final boolean tempRet = getEndsCallBacks().addLastQ( (JUnitEndsAdapter)listener );
			assert !tempRet;
		} else {
			if ( superClass.equals( JUnitTestClassEndsAdapter.class ) ) {
				final boolean tempRet = getTestClassEndsCallBacks().addLastQ( (JUnitTestClassEndsAdapter)listener );
				assert !tempRet;
			} else {
				if ( superClass.equals( JUnitTestClassStartsAdapter.class ) ) {
					final boolean tempRet = getTestClassStartsCallBacks().addLastQ( (JUnitTestClassStartsAdapter)listener );
					assert !tempRet;
				} else {
					assert ( superClass.equals( JUnitStartsAdapter.class ) ) : Q
						.bug( "the specified object's class is not supported" );
					//
					Q.badCall( "we cannot ever hook on JUNIT_STARTS, because we already used that to init, "
						+ "and it was called before any user defined @BeforeClass" );
				}
			}
		}
		//
		// switch ( juPos )
		// {
		// case JUNIT_STARTS:
		//
		// break;// line not reached
		// case JUNIT_ENDS:
		// listener,
		// JUnitEndsAdapter.class ) );
		//
		// break;
		// case TESTCLASS_ENDS:
		// listener,
		// JUnitTestClassEndsAdapter.class ) );
		//
		// break;
		// case TESTCLASS_STARTS:
		// listener,
		// JUnitTestClassStartsAdapter.class ) );
		//
		// break;
		// default:
		// Q.bug( "you forgot to make a case for newly added enums" );
		// }
	}
	
	
	// /**
	// * cannot call this, the Starts hooks are not possible to be hooked
	// *
	// * @param juStarts
	// */
	// public static synchronized final
	// void
	// addJUnitStartsListener(
	// JUnitStartsAdapter juStarts )
	// {
	// Q.badCall( "we cannot ever hook on JUNIT_STARTS, because we already used that to init, "
	// + "and it was called before any user defined @BeforeClass" );
	// assumedInsideJUnit();
	// // if ( juPos == JUnitPos.JUNIT_STARTS )
	// // {
	// // }
	// Q.assumedFalse( getStartsCallBacks().addLastQ(
	// juStarts ) );
	// }
	//
	//
	// public static synchronized final
	// void
	// addJUnitEndsListener(
	// JUnitEndsAdapter juEnds )
	// {
	// assumedInsideJUnit();
	// // if ( juPos == JUnitPos.JUNIT_STARTS )
	// // {
	// // Q.badCall( "we cannot ever hook on JUNIT_STARTS, because we already used that to init, "
	// // + "and it was called before any user defined @BeforeClass" );
	// // }
	// Q.assumedFalse( getEndsCallBacks().addLastQ(
	// juEnds ) );
	// }
	//
	//
	// public static synchronized final
	// void
	// addJUnitTestClassStartsListener(
	// JUnitTestClassStartsAdapter juTestClass_Starts )
	// {
	// assumedInsideJUnit();
	// // if ( juPos == JUnitPos.JUNIT_STARTS )
	// // {
	// // }
	// Q.assumedFalse( getTestClassStartsCallBacks().addLastQ(
	// juTestClass_Starts ) );
	// }
	//
	//
	// public static synchronized final
	// void
	// addJUnitTestClassEndsListener(
	// JUnitTestClassEndsAdapter juTestClass_Ends )
	// {
	// assumedInsideJUnit();
	// // if ( juPos == JUnitPos.JUNIT_STARTS )
	// // {
	// // Q.badCall( "we cannot ever hook on JUNIT_STARTS, because we already used that to init, "
	// // + "and it was called before any user defined @BeforeClass" );
	// // }
	// Q.assumedFalse( getTestClassEndsCallBacks().addLastQ(
	// juTestClass_Ends ) );
	// }
	
	
	private static final// not synchronized, just in case
			boolean assumedInsideJUnit() {
		// no good Q.assumedFalse( E.inEDTNow() );// can't be in EDT for sure!
		if ( !isInsideJUnit() ) {
			Q.badCall( "you cannot call this method in a non-JUnit program" );
			
		}
		return true;
	}
	
	
	// /**
	// * you should probably never need to call this
	// *
	// * @param listener
	// */
	// public static synchronized final
	// void
	// removeJUnitListener(
	// JUnitListener listener )
	// {
	// assumedInsideJUnit();
	// if ( null == allCallBacks )
	// {
	// Q.badCall( "called remove before ever calling add" );
	// }
	// listener ) );
	// }
	
	
	/**
	 * 
	 * @return true if we're running inside a JUnit, false if it's just a normal ie. main() program<br>
	 */
	public static final// never synchronized, will deadlock
			boolean isInsideJUnit() {
		return insideJUnitDetected;
		// return ( level >= 1 );
	}
	
	
	// private static// not synchronized
	// void
	// setAlready(
	// JUnitPos juPos,
	// boolean state )
	// {
	// juPos,
	// new Boolean(
	// state ) ) );
	// }
	//
	//
	// private static
	// boolean
	// getAlready(
	// JUnitPos juPos )
	// {
	// Boolean b =
	// alreadyCalled.get( juPos );
	// if ( null == b )
	// {
	// return false;
	// }
	// else
	// {
	// return b.booleanValue();
	// }
	// }
	//
	
	private static synchronized final void invokeForAllListeners( final JUnitPos juPos ) {
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );
		assert assumedInsideJUnit();
		assert null != juPos;
		assert !E.inEDTNow();// obviously cannot be in EDT since we're in JUnit or main thread
		
		JUnitBaseAdapter jua = null;
		
		switch ( juPos ) {
		case JUNIT_STARTS:
			if ( null != allStartsCallBacks ) {
				jua = getStartsCallBacks().getObjectAt( Position.FIRST );
			}
			break;
		case JUNIT_ENDS:
			if ( null != allEndsCallBacks ) {
				jua = getEndsCallBacks().getObjectAt( Position.FIRST );
			}
			break;
		case TESTCLASS_ENDS:
			if ( null != allTestClassEndsCallBacks ) {
				jua = getTestClassEndsCallBacks().getObjectAt( Position.FIRST );
			}
			break;
		case TESTCLASS_STARTS:
			if ( null != allTestClassStartsCallBacks ) {
				jua = getTestClassStartsCallBacks().getObjectAt( Position.FIRST );
			}
			break;
		default:
			Q.bug( "you forgot to make a case for newly added enums" );
		}
		
		if ( null == jua ) {
			// no listeners for the current juPos
			return;
		}
		
		assert null != jua;
		
		while ( null != jua ) {
			try {
				switch ( juPos ) {
				case JUNIT_STARTS:
					Q.bug( "this will never be reached, because you cannot add a listener before `this method` is called,"
						+ " tried static block AND @BeforeClass => no worky" );
					jua.JUnitStarts();
					break;
				case JUNIT_ENDS:
					jua.JUnitEnds();
					break;
				case TESTCLASS_ENDS:
					jua.JUnitTestClassEnds();
					break;
				case TESTCLASS_STARTS:
					jua.JUnitTestClassStarts();
					break;
				default:
					Q.bug( "you forgot to make a case for newly added enums" );
				}
			} catch ( final Throwable t ) {
				Q.postpone( t );// only postpone those from listeners, other throws will be allowed
			}
			
			switch ( juPos ) {
			case JUNIT_STARTS:
				if ( !getStartsCallBacks().containsObject( (JUnitStartsAdapter)jua ) ) {
					Q.bug( "you removed the parser in your callback (one of the above 4 calls) which wasn't expected" );
					// FIXME: maybe handle this situation, so to allow such removal without effects
				}
				jua = getStartsCallBacks().getObjectAt( Position.AFTER, (JUnitStartsAdapter)jua );
				break;
			case JUNIT_ENDS:
				if ( !getEndsCallBacks().containsObject( (JUnitEndsAdapter)jua ) ) {
					Q.bug( "you removed the parser in your callback (one of the above 4 calls) which wasn't expected" );
					// FIXME: maybe handle this situation, so to allow such removal without effects
				}
				jua = getEndsCallBacks().getObjectAt( Position.AFTER, (JUnitEndsAdapter)jua );
				break;
			case TESTCLASS_ENDS:
				if ( !getTestClassEndsCallBacks().containsObject( (JUnitTestClassEndsAdapter)jua ) ) {
					Q.bug( "you removed the parser in your callback (one of the above 4 calls) which wasn't expected" );
					// FIXME: maybe handle this situation, so to allow such removal without effects
				}
				jua = getTestClassEndsCallBacks().getObjectAt( Position.AFTER, (JUnitTestClassEndsAdapter)jua );
				break;
			case TESTCLASS_STARTS:
				if ( !getTestClassStartsCallBacks().containsObject( (JUnitTestClassStartsAdapter)jua ) ) {
					Q.bug( "you removed the parser in your callback (one of the above 4 calls) which wasn't expected" );
					// FIXME: maybe handle this situation, so to allow such removal without effects
				}
				jua = getTestClassStartsCallBacks().getObjectAt( Position.AFTER, (JUnitTestClassStartsAdapter)jua );
				break;
			default:
				Q.bug( "you forgot to make a case for newly added enums" );
			}
			
		}// while
		Q.throwPostponedOnes();
		
	}
	
	
	// /**
	// * to avoid repeating the parser code<br>
	// *
	// * @param juPos
	// */
	// private static synchronized final
	// void
	// invokeForAllListeners(
	// JUnitPos juPos )
	// {
	// // Q.assumedFalse( E.inEDTNow() );// invoking these should definitely not be inside EDT
	// assumedInsideJUnit();
	// Q.assumedFalse( E.inEDTNow() );// obviously cannot be in EDT since we're in JUnit or main thread
	//
	// switch ( juPos )
	// {
	// case JUNIT_STARTS:
	// invokeStart
	// parser.JUnitStarts();
	// break;
	// case JUNIT_ENDS:
	// parser.JUnitEnds();
	// break;
	// case TESTCLASS_ENDS:
	// parser.JUnitTestClassEnds();
	// break;
	// case TESTCLASS_STARTS:
	// parser.JUnitTestClassStarts();
	// break;
	// default:
	// Q.bug( "you forgot to make a case for newly added enums" );
	// }
	//
	// if ( null != allCallBacks )
	// {
	// // setAlready(
	// // juPos,
	// // true );
	// // synchronized ( JUnitHooker.class )
	// // {
	// // int size =
	// // getAllCallBacks().size();
	// // do
	// // {
	// JUnitListener parser =
	// getAllCallBacks().getObjectAt(
	// Position.FIRST );
	// // if ( null == parser )
	// // {
	// // parser =
	// // getAllCallBacks().getObjectAt(
	// // Position.FIRST );
	// // }
	// // can be empty due to removeJUnitListener() having been called
	// while ( null != parser )
	// {
	// try
	// {
	// switch ( juPos )
	// {
	// case JUNIT_STARTS:
	// parser.JUnitStarts();
	// break;
	// case JUNIT_ENDS:
	// parser.JUnitEnds();
	// break;
	// case TESTCLASS_ENDS:
	// parser.JUnitTestClassEnds();
	// break;
	// case TESTCLASS_STARTS:
	// parser.JUnitTestClassStarts();
	// break;
	// default:
	// Q.bug( "you forgot to make a case for newly added enums" );
	// }
	// }
	// catch ( Throwable t )
	// {
	// Q.postpone( t );
	// }
	//
	// if ( !getAllCallBacks().containsObject(
	// parser ) )
	// {
	// Q.bug( "you removed the parser in your callback (one of the above 4 calls) which wasn't expected" );
	// // : maybe handle this situation, so to allow such removal without effects
	// }
	// parser =
	// getAllCallBacks().getObjectAt(
	// Position.AFTER,
	// parser );
	// }
	// Q.throwPostponedOnes();
	//
	// // }// basically repeat this, if something was added while processing
	// // while ( ( getAllCallBacks().size() > size )
	// // && ( size == 0 ) );
	// // }// sync
	// }
	// }
	
	
	@BeforeClass
	public static synchronized final void beforeSuite()// `this method`
	{
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );
		// System.out.println( "JUnit beforeSuite!" );
		if ( false == insideJUnitDetected ) {
			insideJUnitDetected = true;
		}
		assert assumedInsideJUnit();// naturally this would be true, else inconsistency fail
		
		assert level >= 0;
		level++;
		if ( level == 1 ) {
			// System.out.println( "JUnit starts" );
			invokeForAllListeners( JUnitPos.JUNIT_STARTS );
		} else {
			// System.out.println( "JUnit: subclass-test begins" );
			invokeForAllListeners( JUnitPos.TESTCLASS_STARTS );
		}
	}
	
	
	@AfterClass
	public static synchronized final void afterSuite() {
		// Q.assumedFalse( R.recursionDetectedForCurrentThread.get().booleanValue() );
		assert level > 0 : Q.bug( "this should never happen!" );
		try {
			if ( level == 1 ) {
				// System.out.println( "JUnit ends" );
				invokeForAllListeners( JUnitPos.JUNIT_ENDS );
			} else {
				// System.out.println( "JUnit: subclass-test ends" );
				invokeForAllListeners( JUnitPos.TESTCLASS_ENDS );
			}
		} finally {
			level--;
		}
	}
	
}
