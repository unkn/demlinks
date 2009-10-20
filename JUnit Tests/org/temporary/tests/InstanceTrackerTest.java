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


package org.temporary.tests;



import org.dml.storagewrapper.Testy2;
import org.dml.tools.StaticInstanceTracker;
import org.dml.tools.Testy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class InstanceTrackerTest {
	
	Testy	t;
	Testy2	t2;
	
	@Before
	public void setUp() {

		t = Testy.getNew();
		
		t2 = Testy2.getNew();
	}
	
	@After
	public void tearDown() {

		// t.deInit();
		// t2.deInit();
		StaticInstanceTracker.deInitAll();
	}
	
	@Test
	public void test1() {

		t.show();
		t2.show();
	}
	
}
