/**
 * 
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
 */



package org.dml.tools;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dml.error.BadCallError;
import org.dml.tracking.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class IniterTest
{
	
	Testy					t, tt;
	Testy2					t2, tt2;
	ProperSubClassingOfSIT	psc;
	MethodParams			params	= null;
	
	
	@Before
	public
			void
			setUp()
	{
		
		t = new Testy();
		
		t2 = Testy2.getNew();
		tt = Testy.getNew();
		tt2 = new Testy2();
		
		params = MethodParams.getNew();
		// params.init( null );
		
	}
	

	@After
	public
			void
			tearDown()
	{
		Factory.deInitIfAlreadyInited( tt2 );
		Factory.deInitIfAlreadyInited( tt );
		Factory.deInitIfAlreadyInited( t2 );
		Factory.deInitIfAlreadyInited( t );
		// t.deInit();
		// t2.deInit();
		// else
		// StaticInstanceTracker.deInitAllThatExtendMe();
		// params.deInitSilently();
		// Factory.deInitIfAlreadyInited( params );
		// Factory.deInitAll();
	}
	

	@Test
	public
			void
			test1()
					throws Exception
	{
		
		try
		{
			assertTrue( params.size() == 0 );
			params.set(
						PossibleParams.homeDir,
						"1/2" );
			assertTrue( params.size() == 1 );
			
			Factory.init(
							t,
							params );
			// t.init( params );
			t.show();
			Factory.deInit( t );
			// t.deInit();
			
			// Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( params );
			params.clear();// this will empty params
			// params.restart();
			assertTrue( params.size() == 0 );
			
			params.set(
						PossibleParams.homeDir,
						"2/2" );
			assertTrue( params.size() == 1 );
			
			// t.init( params );
			Factory.init(
							t,
							params );
			t.show();
			// t.deInit();
			Factory.deInit( t );
			
			assertTrue( params.size() == 1 );
			// params.restart();
			// Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( params );
			params.clear();
			assertTrue( params.size() == 0 );
			
			tt.show();
			t2.show();
			
			params.set(
						PossibleParams.homeDir,
						"3+ 1/2" );
			// tt2.init( params );
			Factory.init(
							tt2,
							params );
			tt2.show();
			// tt2.deInit();
			Factory.deInit( tt2 );
			
			params.set(
						PossibleParams.homeDir,
						"3+ 2/2" );
			// tt2.init( params );
			Factory.init(
							tt2,
							params );
			tt2.show();
			// tt2.deInit();
			Factory.deInit( tt2 );
			// throw new Exception();
		}
		finally
		{
			Factory.deInitIfAlreadyInited( tt );
			Factory.deInitIfAlreadyInited( t2 );
			Factory.deInitIfAlreadyInited( tt2 );
			Factory.deInitIfAlreadyInited( t );
			// tt.deInitAllLikeMe();
			// t2.deInitAllLikeMe();
		}
	}
	

	@Test
	public
			void
			test2()
	{
		
		params.set(
					PossibleParams.homeDir,
					"something" );
		assertTrue( params.size() == 1 );
		boolean errored = false;
		try
		{
			Factory.init(
							t,
							params );
			Factory.init(
							t,
							params );
			// t.init( params );
			// t.init( params );
		}
		catch ( Throwable zt )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																zt,
																BadCallError.class ) )
			{
				errored = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		finally
		{
			assertTrue( errored );
		}
		assertTrue( params.size() == 1 );
		
		errored = false;
		try
		{
			Factory.deInit( t );
			Factory.deInit( t );
			// t.deInit();
			// t.deInit();
		}
		catch ( Throwable t1 )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t1,
																BadCallError.class ) )
			{
				errored = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		finally
		{
			assertTrue( errored );
		}
		assertTrue( params.size() == 1 );
		
		errored = false;
		try
		{
			Testy2 r = new Testy2();
			// r.deInit();
			Factory.deInit( r );
		}
		catch ( Throwable t1 )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t1,
																BadCallError.class ) )
			{
				errored = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		finally
		{
			assertTrue( errored );
		}
		
		assertTrue( params.size() == 1 );
		System.out.println( params.getExString( PossibleParams.homeDir ) );
		// t.init( params );
		Factory.init(
						t,
						params );
		// params.deInit();
		errored = false;
		try
		{
			Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
			Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
			Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
			Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
			
			// t.restart();
			// t.restart();
			// t.restart();
			// t.restart();
		}
		catch ( BadCallError bce )
		{
			errored = true;
		}
		finally
		{
			assertFalse( errored );
		}
	}
	

	@Test
	public
			void
			testDeInitAllExtenders()
	{
		
		System.out.println( "===============" );
		params.set(
					PossibleParams.homeDir,
					"nothing" );
		Factory.init(
						t,
						params );
		Factory.init(
						tt2,
						params );
		// t.init( params );
		// tt2.init( params );
	}
	

	@Test
	public
			void
			testSilent()
	{
		
		boolean excepted = false;
		try
		{
			Factory.deInitIfAlreadyInited( t2 );
			Factory.deInitIfAlreadyInited( t2 );
			// t2.deInitSilently();
			// t2.deInitSilently();
		}
		catch ( BadCallError bce )
		{
			excepted = true;
		}
		finally
		{
			assertFalse( excepted );
		}
		
		excepted = false;
		params.set(
					PossibleParams.homeDir,
					"some" );
		Factory.init(
						t2,
						params );
		// t2.init( params );
		Factory.deInit( t2 );
		// t2.deInit();
		try
		{
			
			Factory.deInitIfAlreadyInited( t2 );
			// t2.deInitSilently();
		}
		catch ( BadCallError bce )
		{
			excepted = true;
		}
		finally
		{
			assertFalse( excepted );
		}
		
	}
	

	@Test
	public
			void
			testPSC()
	{
		
		psc = new ProperSubClassingOfSIT();
		
		// params.set( PossibleParams.homeDir, "homePSC" );
		try
		{
			Factory.initWithoutParams( psc );
			// psc.init( null );
			psc.exec();
		}
		finally
		{
			boolean rted = false;
			boolean excepted = false;
			try
			{
				Factory.deInit( psc );
				// psc.deInit();
			}
			catch ( Throwable t1 )
			{
				if ( RunTime.isThisWrappedException_of_thisType(
																	t1,
																	RuntimeException.class ) )
				{
					// ignore, threw by overridden done() method
					rted = true;
					RunTime.clearLastThrown_andAllItsWraps();
				}
			}
			finally
			{
				try
				{
					Factory.deInit( psc );
					// psc.deInit();
				}
				catch ( Throwable zt )
				{
					if ( RunTime.isThisWrappedException_of_thisType(
																		zt,
																		BadCallError.class ) )
					{
						excepted = true;
						RunTime.clearLastThrown_andAllItsWraps();
					}
				}
				finally
				{
					assertTrue( excepted );
				}
			}
			assertTrue( excepted );
			assertTrue( rted );
		}
	}
	

	@Test
	public
			void
			testExceptionOnStart()
	{
		
		Testy3StartThrower t3 = new Testy3StartThrower();
		boolean threw = false;
		try
		{
			Factory.initWithoutParams( t3 );
			// t3.init( null );
		}
		catch ( Throwable zt )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																zt,
																RuntimeException.class ) )
			{
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		// t3.deInit();
		Factory.deInit( t3 );
	}
	

	@Test
	public
			void
			testParams()
	{
		
		String home = "home";
		params.set(
					PossibleParams.homeDir,
					home );
		Factory.init(
						t,
						params );
		// t.init( params );
		params.clear();
		assertTrue( params.size() == 0 );
		assertTrue( t.getHome() == home );
		Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
		// t.restart();
		t.show();
		assertTrue( t.getHome() == home );
		Factory.deInit( t );
		// t.deInit();
		String home2 = "home2";
		params.set(
					PossibleParams.homeDir,
					home2 );
		// t.init( params );
		Factory.init(
						t,
						params );
		assertTrue( t.getHome() == home2 );
		assertTrue( home != home2 );
		Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( t );
		// t.restart();
		assertTrue( t.getHome() == home2 );
		// t.deInit();
		Factory.deInit( t );
		assertTrue( t.getHome() == null );
		// Factory.deInit( params );
		// params.deInit();
	}
	
}
