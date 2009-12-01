/**
 * File creation: Oct 20, 2009 1:44:39 AM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class StaticInstanceTrackerTest {
	
	Testy					t, tt;
	Testy2					t2, tt2;
	ProperSubClassingOfSIT	psc;
	MethodParams<Object>	params	= null;
	
	@Before
	public void setUp() {

		t = new Testy();
		
		t2 = Testy2.getNew();
		tt = Testy.getNew();
		tt2 = new Testy2();
		
		params = new MethodParams<Object>();
		params.init( null );
		
	}
	
	@After
	public void tearDown() {

		// t.deInit();
		// t2.deInit();
		// FIXME: need callback; or just silentDeInits everywhere; or something
		// else
		// StaticInstanceTracker.deInitAllThatExtendMe();
		params.deInitSilently();
	}
	
	@Test
	public void test1() throws Exception {

		try {
			assertTrue( params.size() == 0 );
			params.set( PossibleParams.homeDir, "1/2" );
			assertTrue( params.size() == 1 );
			
			t.init( params );
			t.show();
			t.deInit();
			
			params.restart();// this will empty params
			assertTrue( params.size() == 0 );
			
			params.set( PossibleParams.homeDir, "2/2" );
			assertTrue( params.size() == 1 );
			
			t.init( params );
			t.show();
			t.deInit();
			
			assertTrue( params.size() == 1 );
			params.restart();
			assertTrue( params.size() == 0 );
			
			tt.show();
			t2.show();
			
			params.set( PossibleParams.homeDir, "3+ 1/2" );
			tt2.init( params );
			tt2.show();
			tt2.deInit();
			
			params.set( PossibleParams.homeDir, "3+ 2/2" );
			tt2.init( params );
			tt2.show();
			tt2.deInit();
			// throw new Exception();
		} finally {
			tt.deInitAllLikeMe();
			t2.deInitAllLikeMe();
		}
	}
	
	@Test
	public void test2() {

		params.set( PossibleParams.homeDir, "something" );
		assertTrue( params.size() == 1 );
		boolean errored = false;
		try {
			t.init( params );
			t.init( params );
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
		assertTrue( params.size() == 1 );
		
		errored = false;
		try {
			t.deInit();
			t.deInit();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
		assertTrue( params.size() == 1 );
		
		errored = false;
		try {
			Testy2 r = new Testy2();
			r.deInit();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
		
		assertTrue( params.size() == 1 );
		System.out.println( params.getExString( PossibleParams.homeDir ) );
		t.init( params );
		// params.deInit();
		errored = false;
		try {
			t.restart();
			t.restart();
			t.restart();
			t.restart();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertFalse( errored );
		}
	}
	
	@Test
	public void testDeInitAllExtenders() {

		System.out.println( "===============" );
		params.set( PossibleParams.homeDir, "nothing" );
		t.init( params );
		tt2.init( params );
	}
	
	@Test
	public void testSilent() {

		boolean excepted = false;
		try {
			t2.deInitSilently();
			t2.deInitSilently();
		} catch ( BadCallError bce ) {
			excepted = true;
		} finally {
			assertFalse( excepted );
		}
		
		excepted = false;
		params.set( PossibleParams.homeDir, "some" );
		t2.init( params );
		t2.deInit();
		try {
			
			t2.deInitSilently();
		} catch ( BadCallError bce ) {
			excepted = true;
		} finally {
			assertFalse( excepted );
		}
		
	}
	
	@Test
	public void testPSC() {

		psc = new ProperSubClassingOfSIT();
		
		// params.set( PossibleParams.homeDir, "homePSC" );
		try {
			psc.init( null );
			psc.exec();
		} finally {
			boolean rted = false;
			boolean excepted = false;
			try {
				psc.deInit();
			} catch ( RuntimeException re ) {
				// ignore
				rted = true;
			} finally {
				try {
					psc.deInit();
				} catch ( BadCallError bce ) {
					excepted = true;
				} finally {
					assertTrue( excepted );
				}
			}
			assertTrue( excepted );
			assertTrue( rted );
		}
	}
	
	@Test
	public void testExceptionOnStart() {

		Testy3StartThrower t3 = new Testy3StartThrower();
		boolean threw = false;
		try {
			t3.init( null );
		} catch ( RuntimeException rte ) {
			threw = true;
		}
		assertTrue( threw );
		t3.deInit();
	}
	
	@Test
	public void testParams() {

		String home = "home";
		params.set( PossibleParams.homeDir, home );
		t.init( params );
		params.deInit();
		assertTrue( params.size() == 0 );
		assertTrue( t.getHome() == home );
		t.restart();
		t.show();
		assertTrue( t.getHome() == home );
		t.deInit();
		String home2 = "home2";
		params.set( PossibleParams.homeDir, home2 );
		t.init( params );
		assertTrue( t.getHome() == home2 );
		assertTrue( home != home2 );
		t.restart();
		assertTrue( t.getHome() == home2 );
		t.deInit();
		assertTrue( t.getHome() == null );
	}
	
}
