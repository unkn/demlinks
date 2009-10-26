/**
 * File creation: Oct 23, 2009 9:45:39 AM
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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dml.error.BadCallError;
import org.junit.Test;



/**
 * 
 *
 */
public class LevelsTest {
	
	MainLevel1	ml1;
	MainLevel2	ml2;
	
	@Test
	public void test1() {

		ml1 = new MainLevel1();
		ml2 = new MainLevel2();
		
		VarLevel1 v1 = new VarLevel1();
		v1.init();
		
		ml1.initLevel1( v1 );
		ml1.do1();
		
		VarLevel2 v2 = new VarLevel2();
		v2.init( "home2" );
		boolean threw = false;
		try {
			ml2.initLevel1( v2 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertFalse( threw );
		}
		
		ml2.initLevel2( v2 );
		ml2.showHome();
		
		try {
			threw = false;
			ml2.initLevel1();
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		try {
			threw = false;
			ml2.initLevel1( v1 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		

		ml2.showHome();
		ml2.initLevel2( "home3" );
		ml2.showHome();
		
		ml2.initLevel2();
		ml2.showHome();
	}
}
