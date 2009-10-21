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
	
	Testy	t, tt;
	Testy2	t2, tt2;
	
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
		StaticInstanceTracker.deInitAll();
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
			StaticInstanceTracker.deInitAll();
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
	
}
