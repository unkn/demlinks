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
 * File creation: Aug 23, 2010 12:02:59 AM
 */


package org.dml.tools;



import static org.junit.Assert.*;

import org.dml.error.*;
import org.dml.tracking.*;
import org.junit.*;



/**
 * 
 *
 */
public class RunTimeTest {
	
	
	@Before
	public void setUp() {
		
		RunTime.clearThrowChain();
	}
	
	
	@After
	public void tearDown() {
		
		RunTime.clearThrowChain();
	}
	
	
	private StackTraceElement tc() {
		return RunTime.getTheCaller_OutsideOfClass( this.getClass() );
	}
	
	
	private void ho() {
		System.exit( 111 );
	}
	
	private class F {
		
		public F() {
			//
		}
		
		
		public StackTraceElement getCaller() {
			return RunTime.getTheCaller_OutsideOfThisClass();
		}
		
		
		public StackTraceElement getCaller2() {
			return RunTime.getTheCaller_OutsideOfClass( this.getClass() );
		}
		
		
		public StackTraceElement getCaller3() {
			return RunTime.getTheCaller_OutsideOfClass( F.class );
		}
		
		
		public StackTraceElement getCaller4( final Class<?> which ) {
			RunTime.assumedNotNull( which );
			return RunTime.getTheCaller_OutsideOfClass( which );
		}
	}
	
	public class G extends F {
		//
	}
	
	
	
	@Test
	public void testCaller()// don't change this method's name!!!!
	{
		final F f = new F();
		final G g = new G();
		
		// don't move relative to each other the following lines!! each must be on 1 line in that order else fails
		final StackTraceElement[] steaR = RunTime.getCurrentStackTraceElementsArray();
		final StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		final StackTraceElement curSTE = RunTime.getCurrentStackTraceElement();
		final StackTraceElement curSTE2 = RunTimeTest2.getCurrentStackTraceElement();
		final StackTraceElement f1 = f.getCaller();
		final StackTraceElement f2 = f.getCaller2();
		final StackTraceElement f3 = f.getCaller3();
		final StackTraceElement f41 = f.getCaller4( F.class );
		final StackTraceElement f42 = f.getCaller4( f.getClass() );
		final StackTraceElement f43 = f.getCaller4( g.getClass() );
		final StackTraceElement g1 = g.getCaller();
		final StackTraceElement g2 = g.getCaller2();
		final StackTraceElement g3 = g.getCaller3();
		final StackTraceElement g41 = g.getCaller4( G.class );
		final StackTraceElement g42 = g.getCaller4( g.getClass() );
		final StackTraceElement g43 = g.getCaller4( F.class );
		// do not move the above calls ^^^ order matters and each should be on only 1 line! wrap=120chars
		
		assertNotNull( stea );
		assertNotNull( steaR );
		assertNotNull( curSTE );
		assertNotNull( curSTE2 );
		// for ( int i = 0; i < steaR.length; i++ )
		// {
		// System.out.println( i
		// + " "
		// + steaR[i] );
		// }
		// for ( int i = 0; i < stea.length; i++ )
		// {
		// System.out.println( i
		// + " "
		// + stea[i] );
		// }
		// System.out.println( curSTE );
		// System.out.println( curSTE2 );
		// TODO: transfer eclipse settings to project specific settings ie. wrap 120chars should be in project
		final int aLPos = RunTime.skipBackOverCallers( steaR, 0, 2 );
		final StackTraceElement actualLocation = steaR[aLPos];
		final StackTraceElement otherAcLoc = stea[RunTime.skipBackOverCallers( stea, 0, 1 )];
		// System.out.println( aLPos
		// + " "
		// + actualLocation
		// + " "
		// + otherAcLoc );
		assertTrue( actualLocation.getClassName() == this.getClass().getName() );
		assertTrue( otherAcLoc.getClassName() == this.getClass().getName() );
		assertTrue( curSTE.getClassName() == this.getClass().getName() );
		assertTrue( curSTE2.getClassName() == this.getClass().getName() );
		assertTrue( actualLocation.getMethodName() == otherAcLoc.getMethodName() );
		assertTrue( actualLocation.getMethodName() == curSTE.getMethodName() );
		assertTrue( actualLocation.getMethodName() == "testCaller" );
		assertTrue( ( actualLocation.getLineNumber() + 1 ) == otherAcLoc.getLineNumber() );
		assertTrue( ( actualLocation.getLineNumber() + 2 ) == curSTE.getLineNumber() );
		
		
		assertTrue( f1.getClassName() == this.getClass().getName() );
		assertTrue( f2.getClassName() == this.getClass().getName() );
		assertTrue( f3.getClassName() == this.getClass().getName() );
		assertTrue( f41.getClassName() == this.getClass().getName() );
		assertTrue( f42.getClassName() == this.getClass().getName() );
		// System.out.println( f1 );
		assertTrue( f1.getMethodName() == actualLocation.getMethodName() );// aspect is enabled?
		assertTrue( f2.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f3.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f41.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f42.getMethodName() == actualLocation.getMethodName() );
		assertTrue( ( actualLocation.getLineNumber() + 4 ) == f1.getLineNumber() );
		assertTrue( ( f1.getLineNumber() + 1 ) == f2.getLineNumber() );
		assertTrue( ( f1.getLineNumber() + 2 ) == f3.getLineNumber() );
		assertTrue( ( f1.getLineNumber() + 3 ) == f41.getLineNumber() );
		assertTrue( ( f1.getLineNumber() + 4 ) == f42.getLineNumber() );
		assertNull( f43 );
		
		assertTrue( g1.getClassName() == this.getClass().getName() );
		assertNull( g2 );
		assertTrue( g3.getClassName() == this.getClass().getName() );
		assertNull( g41 );
		assertNull( g42 );
		assertTrue( g43.getClassName() == this.getClass().getName() );
		assertTrue( g1.getMethodName() == actualLocation.getMethodName() );
		assertTrue( g3.getMethodName() == actualLocation.getMethodName() );
		assertTrue( g43.getMethodName() == actualLocation.getMethodName() );
		assertTrue( ( f42.getLineNumber() + 2 ) == g1.getLineNumber() );
		assertTrue( ( g1.getLineNumber() + 2 ) == g3.getLineNumber() );
		assertTrue( ( g3.getLineNumber() + 3 ) == g43.getLineNumber() );
		// for ( int i = 0; i < stea.length; i++ )
		// {
		// System.out.println( i + " " + stea[i] );
		// }
		// this.ho();
		
		// System.out.println( curSTE );
		// StackTraceElement st = this.tc();
		
		// System.out.println( f.getCaller() );
		final StackTraceElement ste = RunTime.getTheCaller_OutsideOfThisClass();
		assertNull( ste );
		assertNull( tc() );
		
		StackTraceElement out = RunTimeTest2.outterCall();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		final RunTimeTest2 rtt2 = new RunTimeTest2();
		out = rtt2.innerCall();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw();
		// System.out.println( out );
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw2();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw3();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw4();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw5();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = rtt2.wtw6();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = some( rtt2 );
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == "some" );
		
		out = RunTime.forJunit();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		
		out = Log.forJunit();
		assertNotNull( out );
		assertTrue( out.getClassName() == this.getClass().getName() );
		assertTrue( out.getMethodName() == actualLocation.getMethodName() );
		// String callerName = ste.getMethodName();
		// System.out.println( callerName );
		// assertTrue( this.tc().getMethodName() == callerName );
		// assertTrue( callerName == "invoke" );
		// System.out.println();// st.getMethodName() );
		// StackTraceElement[] sta = Thread.currentThread().getStackTrace();
		// for ( StackTraceElement element : sta )
		// {
		// System.out.println( element );
		// }
		
	}
	
	
	private StackTraceElement some(
	// don't rename this!
	final RunTimeTest2 rtt2 ) {
		return rtt2.wtw6();
	}
	
	
	
	public int loop( final int i, final int j ) {
		if ( ( !RunTime.recursiveLoopDetected.get().booleanValue() ) && ( i < 5 ) ) {
			System.out.println( "in2 with: " + i );
			return this.loop( i + 1 );
		}
		return i;
	}
	
	
	public int loop( final int i ) {
		if ( ( !RunTime.recursiveLoopDetected.get().booleanValue() ) && ( i < 5 ) ) {
			System.out.println( "in1 with: " + i );
			return this.loop( i + 1, 0 );
		}
		return i;
		// if ( i < 10 )
		// {
		// return this.loop( i + 1 );
		// }
		// else
		// {
		// return RunTime.getCurrentStackTraceElementsArray();
		// }
	}
	
	
	@Test
	public void loopingTest() {
		// StackTraceElement[] erm =
		// if this fails you don't have AspectJ installed or/and RecursionDetector.enableRecursionDetection is false
		assertTrue( 3 == this.loop( 1 ) );
		// for ( int i = 0; i < erm.length; i++ )
		// {
		// System.out.println( i + " " + erm[i] );
		// }
		// StackTraceElement[] stack = this.loop( 0 );// RunTime.getCurrentStackTraceElementsArray();
		// HashSet<StackTraceElement> m = new HashSet<StackTraceElement>();
		// m.clear();
		// for ( int i = 0; i < stack.length; i++ )
		// {
		// System.out.println( i + " " + stack[i] );
		// if ( !m.add( stack[i] ) )
		// {
		// // already existed
		// System.out.println( "Loop on: " + i + " " + stack[i] );
		// break;
		// }
		// }
	}
	
	
	@Test
	public void testOtherThanExpectedThrown() {
		try {
			try {
				// an exception other than the expected one(s) in catch is thrown in this try block
				RunTime.throWrapped( new RuntimeException( "xy" ) );
				// expecting AssumptionError from the following:
				RunTime.getTheCaller_OutsideOfClass( null );
			} catch ( final Throwable t ) {
				
				if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
					RunTime.clearLastThrown_andAllItsWraps();// clear it before we do our stuff
					// ...code (that could throw new exceptions) goes here
					throw new IllegalArgumentException( "test" );
				} else {
					// wasn't what we expected, so we must re-throw it
					RunTime.throWrapped( t );
				}
				// RunTime.clearLastThrown_andAllItsWraps();don't
			}
		} catch ( final Throwable t ) {
			assertTrue( RunTime.isThisWrappedException_of_thisType( t, RuntimeException.class ) );
			RunTime.clearLastThrown_andAllItsWraps();
		}
		// RunTime.throwAllThatWerePosponed();
	}
	
	
	@Test
	public void testNull() {
		try {
			RunTime.getTheCaller_OutsideOfClass( null );
		} catch ( final Throwable t ) {
			
			assertTrue( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) );
			RunTime.clearLastThrown_andAllItsWraps();
		}
		RunTime.throwAllThatWerePostponed();// should re-throw the RTE(xy) from above so you see it in eclipse
		Log.reportError( "test1" );
		Log.reportErrorHere( "test2" );
		c();
		
	}
	
	
	private void c() {
		Log.reportError( "test3" );
		System.out.println( Log.getLine( +1 ) );
		System.out.println( Log.getThisLineLocation() );
		System.out.println( Log.getThisLineLocation( +1 ) );
	}
	
	
	@Test
	public void testRegex() {
		String s = "getCaller4_aroundBody34";
		System.out.println( s.matches( "^getCaller4_aroundBody[0-9]+$" ) );
		s = "getCaller4_aroundBody35$advice";
		System.out.println( s.matches( "^getCaller4" + "_aroundBody[0-9]+\\$advice$" ) );
	}
	
	
	@Test
	public void clearTC() {
		try {
			RunTime.thro( new Exception( "wtw" ) );
		} catch ( final Throwable t ) {
			RunTime.throPostponed( t );
		} finally {
			try {
				assertFalse( RunTime.isNullChain() );
				try {
					RunTime.throwAllThatWerePostponed();
				} catch ( final Throwable t ) {
				}
			} finally {
				// due to aspectJ the chain is continued because the thrown exceptions(as chain) above are re-thrown as
				// new chain, so the chain is back due to aspectJ doing the wrapping
				assertTrue( RunTime.isNullChain() );
				RunTime.throwAllThatWerePostponed();
			}
		}
		
	}
}
