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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class RunTimeTest
{
	
	
	@Before
	public
			void
			setUp()
	{
		
		RunTime.clearThrowChain();
	}
	

	@After
	public
			void
			tearDown()
	{
		
		RunTime.clearThrowChain();
	}
	

	private
			StackTraceElement
			tc()
	{
		return RunTime.getTheCaller_OutsideOfClass( this.getClass() );
	}
	

	private
			void
			ho()
	{
		System.exit( 111 );
	}
	
	private class F
	{
		
		public F()
		{
			//
		}
		

		public
				StackTraceElement
				getCaller()
		{
			return RunTime.getTheCaller_OutsideOfThisClass();
		}
		

		public
				StackTraceElement
				getCaller2()
		{
			return RunTime.getTheCaller_OutsideOfClass( this.getClass() );
		}
		

		public
				StackTraceElement
				getCaller3()
		{
			return RunTime.getTheCaller_OutsideOfClass( F.class );
		}
		

		public
				StackTraceElement
				getCaller4(
							Class<?> which )
		{
			RunTime.assumedNotNull( which );
			return RunTime.getTheCaller_OutsideOfClass( which );
		}
	}
	
	public class G
			extends
			F
	{
		//
	}
	
	
	@Test
	public
			void
			testCaller()// don't change this method's name!!!!
	{
		F f = new F();
		G g = new G();
		
		// don't move relative to each other the following lines!! each must be on 1 line in that order else fails
		StackTraceElement[] steaR = RunTime.getCurrentStackTraceElementsArray();
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		StackTraceElement curSTE = RunTime.getCurrentStackTraceElement();
		StackTraceElement f1 = f.getCaller();
		StackTraceElement f2 = f.getCaller2();
		StackTraceElement f3 = f.getCaller3();
		StackTraceElement f41 = f.getCaller4( F.class );
		StackTraceElement f42 = f.getCaller4( f.getClass() );
		StackTraceElement f43 = f.getCaller4( g.getClass() );
		StackTraceElement g1 = g.getCaller();
		StackTraceElement g2 = g.getCaller2();
		StackTraceElement g3 = g.getCaller3();
		StackTraceElement g41 = g.getCaller4( G.class );
		StackTraceElement g42 = g.getCaller4( g.getClass() );
		StackTraceElement g43 = g.getCaller4( F.class );
		// do not move the above calls ^^^ order matters and each should be on only 1 line! wrap=120chars
		
		assertNotNull( stea );
		assertNotNull( steaR );
		assertNotNull( curSTE );
		// TODO: transfer eclipse settings to project specific settings ie. wrap 120chars should be in project
		StackTraceElement actualLocation = steaR[+2];
		StackTraceElement otherAcLoc = stea[+1];
		assertTrue( actualLocation.getClassName() == this.getClass().getName() );
		assertTrue( otherAcLoc.getClassName() == this.getClass().getName() );
		assertTrue( curSTE.getClassName() == this.getClass().getName() );
		assertTrue( actualLocation.getMethodName() == otherAcLoc.getMethodName() );
		assertTrue( actualLocation.getMethodName() == curSTE.getMethodName() );
		assertTrue( actualLocation.getMethodName() == "testCaller" );
		assertTrue( actualLocation.getLineNumber() + 1 == otherAcLoc.getLineNumber() );
		assertTrue( actualLocation.getLineNumber() + 2 == curSTE.getLineNumber() );
		

		assertTrue( f1.getClassName() == this.getClass().getName() );
		assertTrue( f2.getClassName() == this.getClass().getName() );
		assertTrue( f3.getClassName() == this.getClass().getName() );
		assertTrue( f41.getClassName() == this.getClass().getName() );
		assertTrue( f42.getClassName() == this.getClass().getName() );
		assertTrue( f1.getMethodName() == actualLocation.getMethodName() );// aspect is enabled?
		assertTrue( f2.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f3.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f41.getMethodName() == actualLocation.getMethodName() );
		assertTrue( f42.getMethodName() == actualLocation.getMethodName() );
		assertTrue( actualLocation.getLineNumber() + 3 == f1.getLineNumber() );
		assertTrue( f1.getLineNumber() + 1 == f2.getLineNumber() );
		assertTrue( f1.getLineNumber() + 2 == f3.getLineNumber() );
		assertTrue( f1.getLineNumber() + 3 == f41.getLineNumber() );
		assertTrue( f1.getLineNumber() + 4 == f42.getLineNumber() );
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
		assertTrue( f42.getLineNumber() + 2 == g1.getLineNumber() );
		assertTrue( g1.getLineNumber() + 2 == g3.getLineNumber() );
		assertTrue( g3.getLineNumber() + 3 == g43.getLineNumber() );
		// for ( int i = 0; i < stea.length; i++ )
		// {
		// System.out.println( i + " " + stea[i] );
		// }
		// this.ho();
		
		// System.out.println( curSTE );
		// StackTraceElement st = this.tc();
		
		// System.out.println( f.getCaller() );
		StackTraceElement ste = RunTime.getTheCaller_OutsideOfThisClass();
		assertNull( ste );
		assertNull( this.tc() );
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
}
