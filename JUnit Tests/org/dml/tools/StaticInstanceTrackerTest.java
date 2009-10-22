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



/**
 * 
 *
 */
public class StaticInstanceTrackerTest {
	
	Testy					t, tt;
	Testy2					t2, tt2;
	ProperSubClassingOfSIT	psc;
	
	@Before
	public void setUp() {

		t = new Testy();
		
		t2 = Testy2.getNew();
		tt = Testy.getNew();
		tt2 = new Testy2();
	}
	
	@After
	public void tearDown() {

		// t.deInit();
		// t2.deInit();
		StaticInstanceTracker.deInitAllThatExtendMe();
		
	}
	
	@Test
	public void test1() throws Exception {

		try {
			t.init( "1/2" );
			t.show();
			t.deInit();
			t.init( "2/2" );
			t.show();
			t.deInit();
			
			tt.show();
			t2.show();
			
			tt2.init( "3+ 1/2" );
			tt2.show();
			tt2.deInit();
			tt2.init( "3+ 2/2" );
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

		boolean errored = false;
		try {
			t.init();
			t.init();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
		
		errored = false;
		try {
			t.deInit();
			t.deInit();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
		
		errored = false;
		try {
			Testy2 r = new Testy2();
			r.deInit();
		} catch ( BadCallError bce ) {
			errored = true;
		} finally {
			assertTrue( errored );
		}
	}
	
	@Test
	public void testDeInitAllExtenders() {

		System.out.println( "===============" );
		t.init();
		tt2.init();
	}
	
	@Test
	public void testSilent() {

		boolean excepted = false;
		try {
			t2.silentDeInit();
			t2.silentDeInit();
		} catch ( BadCallError bce ) {
			excepted = true;
		} finally {
			assertFalse( excepted );
		}
		
		excepted = false;
		try {
			t2.init();
			t2.deInit();
			t2.silentDeInit();
		} catch ( BadCallError bce ) {
			excepted = true;
		} finally {
			assertFalse( excepted );
		}
		
	}
	
	@Test
	public void testPSC() {

		psc = new ProperSubClassingOfSIT();
		
		try {
			psc.init();
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
}
