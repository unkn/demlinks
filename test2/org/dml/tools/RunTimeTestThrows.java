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
 * File creation: Jul 1, 2010 11:06:04 PM
 */


package org.dml.tools;



import static org.junit.Assert.*;

import java.io.IOException;

import org.dml.error.AssumptionError;
import org.dml.error.BadCallError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * tests here are supposed to fail, it's supposed to show how exceptions are thrown and shown in eclipse<br>
 * these tests can only be manually tested, ie. by a human<br>
 * 
 */
public class RunTimeTestThrows
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
	
	
	@Test
	public
			void
			testChainedException1()
	{
		
		// this will successfully chain all 3 throwables and eclipse will show them on the exact lines they happen
		try
		{
			RunTime.thro( new Error(
										"err" ) );
		}
		catch ( Throwable e )
		{
			try
			{
				throw new IOException(
										"rogue java method that we cannot control throws this" );
			}
			catch ( Throwable f )
			{
				RunTime.thro( f ); // mustn't chain these like: new Exception( f ) );
			}
		}
		finally
		{
			RunTime.thro( new Exception(
											"final" ) );
		}
	}
	
	
	@Test
	public
			void
			testChainedException1_1()
	{
		
		// this will successfully chain all the last 3 throwables and eclipse will show them on the exact lines they
		// happen
		// however the first thrown exception "err" will not be seen in eclipse, 'cause it got overwritten by the new
		// thrown exception which already had in its chain a normal exception that was caught from java normal throws
		System.err.println( "1_1" );
		try
		{
			RunTime.thro( new Error(
										"err" ) );
		}
		catch ( Throwable e )
		{
			try
			{
				throw new IOException(
										"rogue java method that we cannot control throws this" );
			}
			catch ( Throwable f )
			{
				RunTime.thro( new Exception(
												"new one that also chains f",
												f ) );
				// mustn't chain these like: new Exception( f ) ); because all is lost from before ie. the 'err'
				// exception from above, is lost; and also the f exception is not pointed out in console output, only in
				// eclipse Failure Trace window
			}
		}
		finally
		{
			RunTime.thro( new Exception(
											"final" ) );
		}
	}
	
	
	@Test
	public
			void
			testChainedException2()
	{
		
		// this will not show the Error one
		try
		{
			throw new Error(
								"err1" );// this gets overridden by IOException below, which is java normal
		}
		catch ( Throwable e )
		{
			try
			{
				throw new IOException(
										"rogue java method that we cannot control throws this" );
			}
			catch ( Throwable f )
			{
				RunTime.thro( f ); // mustn't chain these like: new Exception( f ) );
			}
		}
		finally
		{
			RunTime.thro( new Exception(
											"final" ) );
		}
	}
	
	
	@Test
	public
			void
			testChainedException3()
	{
		
		// this only shows the first and last throwed ones
		try
		{
			RunTime.thro( new Error(
										"err" ) );
		}
		catch ( Throwable e )
		{
			// e.printStackTrace();
			RunTime.thro( e );// this will not be shown
		}
		finally
		{
			RunTime.thro( new Exception(
											"final" ) );
		}
	}
	
	
	@Test
	public
			void
			testChainedException4()
	{
		
		
		// Log.thro( "x" );
		// this will successfully chain all 3 throwables and eclipse will show them on the exact lines they happen
		try
		{
			RunTime.thro( new Error(
										"err" ) );
		}
		catch ( Throwable e )
		{
			RunTime.thro( new RuntimeException(
												"rte",
												e ) );// and this is chained
		}
		finally
		{
			RunTime.thro( new Exception(
											"final" ) );
		}
	}
	
	
	@Test
	public
			void
			testDefer()
	{
		
		try
		{
			RunTime.thro( new Error(
										"this was deferred" ) );
		}
		catch ( Throwable e )
		{
			// ignore
		}
		System.out
				.println( "doing something else until the next exception occurs which will show the deferred one also" );
		RunTime.thro( new Exception(
										"the now" ) );
	}
	
	
	@Test
	public
			void
			testDeferIfChained()
	{
		
		// so all 4 are chained in normal order and eclipse show show them all in Failure Trace window
		try
		{
			RunTime.thro( new Error(
										"Zthis was deferred" ) );
		}
		catch ( Throwable e )
		{
			// ignore
		}
		System.out
				.println( "doing something else until the next exception occurs which will show the deferred one also" );
		try
		{
			RunTime.thro( new Exception(
											"A" ) );
		}
		catch ( Throwable e )
		{
			// ignoring A
			try
			{
				throw new IOException(
										"rogue java method that we cannot control throws this" );
			}
			catch ( Throwable f )
			{
				RunTime.thro( f ); // mustn't chain these like: new Exception( f ) );
			}
		}
		finally
		{
			RunTime.thro( new RuntimeException(
												"B, which is chained to A" ) );
		}
	}
	
	
	@Test
	public
			void
			testDeferredNormal()
					throws IOException
	{
		
		try
		{
			try
			{
				throw new IOException(
										"x" );
			}
			finally
			{
				System.out.println( "doing some stuff first" );
			}
		}
		finally
		{
			System.out.println( "reachable" );
		}
	}
	
	
	@Test
	public
			void
			testBadCallNoParams()
	{
		
		// the link in console should point to this line
		RunTime.badCall();
	}
	
	
	@Test
	public
			void
			testBadCallWithParams()
	{
		
		// the link in console should point to this line
		RunTime.badCall( "message" );
	}
	
	
	@Test
	public
			void
			testBadCall2()
	{
		
		RunTime.thro( new BadCallError(
										"wtw" ) );
	}
	
	
	@Test
	public
			void
			testBadCall3()
	{
		
		BadCallError b = new BadCallError();
		// the below throw should point to the above line;
		RunTime.thro( b );
	}
	
	
	@Test
	public
			void
			testBadCall4()
	{
		
		// this should have the console point to the line with 'new' and the last badCall() statement
		try
		{
			BadCallError b = new BadCallError();
			// the below throw should point to the above line;
			RunTime.thro( b );
		}
		finally
		{
			RunTime.badCall();// autochained the above as cause
		}
	}
	
	
	private
			void
			throwy1()
	{
		
		throw new BadCallError(
								"throwy1" );
	}
	
	
	private
			void
			throwy2()
	{
		
		this.throwy1();
	}
	
	
	@Test
	public
			void
			testChainingOfBug()
	{
		
		try
		{
			// the console will show this line and also eclipse too
			this.throwy2();
			// throw new BadCallError();
		}
		catch ( Throwable t )
		{
			// the console will also show this line and eclipse also
			RunTime.bug( t );
		}
	}
	
	
	@Test
	public
			void
			testChainingOfBadCall()
	{
		
		try
		{
			// the console will show this line and also eclipse too
			this.throwy2();
			// throw new BadCallError();
		}
		catch ( Throwable t )
		{
			// the console will also show this line and eclipse also
			RunTime.badCall( t );
		}
	}
	
	
	@Test
	public
			void
			testThroAndAssumed()
	{
		
		// all of the following should be accessible from console links and also from eclipse Failure Trace
		// the links should point on these lines exactly, not on their sub procedures
		try
		{
			RunTime.assumedTrue( 1 == 2 );
			
		}
		finally
		{
			try
			{
				RunTime.thro( new AssertionError(
													"expected true condition was false!" ) );
				
			}
			finally
			{
				try
				{
					int a = 2;
					RunTime.assumedFalse( a == ( 1 + 1 ) );
				}
				finally
				{
					try
					{
						Object o = null;
						RunTime.assumedNotNull( o );
					}
					finally
					{
						RunTime.assumedNull( new Object() );
					}
				}
			}
		}
	}
	
	
	@Test
	public
			void
			testCaughtAlready()
	{
		
		// both exceptions are shown on console, but only last one is shown in eclipse Failure Trace
		try
		{
			RunTime.thro( new Exception(
											"something" ) );
		}
		catch ( Exception e )
		{
			// caught, handled , wtw
			RunTime.clearThrowChain();// this clears all previous ones hmm
		}
		// somewhere later, this throw should not be chained with above one
		RunTime.thro( new IOException(
										"else" ) );
	}
	
	
	@Test
	public
			void
			testCaughtAlreadyClearingOnlyLastOne()
	{
		
		// first and third are shown in eclipse; all 3 in console
		try
		{
			try
			{
				RunTime.thro( new Exception(
												"something2" ) );
			}
			finally
			{
				RunTime.thro( new Exception(
												"this gets cleared" ) );
			}
		}
		catch ( Exception e )
		{
			// caught, handled , wtw
			RunTime.clearLastThrown_andAllItsWraps();// this clears only the previously thrown one aka last one thrown
		}
		// somewhere later, this throw should not be chained with above one
		RunTime.thro( new IOException(
										"else2" ) );
	}
	
	
	@Test
	public
			void
			testCaughtAlreadyWronglyClearingOnlyLastOneInsteadOfCaughtOne()
	{
		
		// first and third are shown in eclipse, all 3 are shown in console
		try
		{
			try
			{
				RunTime.thro( new Exception(
												"first, this is caught but it should be cleared but it's not" ) );
			}
			finally
			{
				RunTime.thro( new RuntimeException(
													"second, not caught but gets cleared since it's last" ) );
			}
		}
		catch ( Exception e )
		{// we catch only first one
			RunTime.clearLastThrown_andAllItsWraps();// this clears the second one, but we wanted to clear the caught
														// one heh
		}
		// somewhere later, this throw should not be chained with above one
		RunTime.thro( new IOException(
										"else3" ) );
	}
	
	
	@Test
	public
			void
			testCaughtAlreadyRightlyClearingOnlyLastOneAkaCaughtOne()
	{
		
		// first and third are shown in eclipse, all 3 are shown in console
		try
		{
			try
			{
				RunTime.thro( new Exception(
												"first, not caught" ) );
			}
			finally
			{
				RunTime.thro( new RuntimeException(
													"second, caught and cleared" ) );
			}
		}
		catch ( Throwable t )
		{// we catch only second one which is last heh
			RunTime.clearLastThrown_andAllItsWraps();// this clears the second one, but that's the one we caught anyway
		}
		// somewhere later, this throw should not be chained with above one
		RunTime.thro( new IOException(
										"else4" ) );
	}
	
	
	@Test
	public
			void
			testReThrow()
	{
		
		// you will see in eclipse Failure Trace all points of throWrapped below and also the 'throw new'
		try
		{
			try
			{
				try
				{
					try
					{
						throw new Exception(
												"some normal throw happens" );
					}
					catch ( Throwable t )
					{
						RunTime.throWrapped( t );// wrap one
					}
				}
				catch ( Throwable t )
				{
					RunTime.throWrapped( t );// wrap two
				}
			}
			catch ( Throwable t )
			{
				RunTime.throWrapped( t );// wrap two
			}
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );// wrap two
		}
	}
	
	
	@Test
	public
			void
			testNull()
	{
		try
		{
			RunTime.throWrapped( new RuntimeException(
														"xy" ) );
			RunTime.getTheCaller_OutsideOfClass( null );
		}
		catch ( Throwable t )
		{
			
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																AssumptionError.class ) )
			{
				RunTime.clearLastThrown_andAllItsWraps();
			}
			else
			{
				// assertTrue( false );
			}
			// RunTime.clearLastThrown_andAllItsWraps();
		}
		RunTime.throwAllThatWerePostponed();// should re-throw the RTE(xy) from above so you see it in eclipse
	}
	
}
