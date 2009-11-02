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
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class LevelsTest {
	
	MainLevel1	ml1;
	MainLevel2	ml2;
	MainLevel3	ml3;
	
	@Test
	public void test1() {

		ml1 = new MainLevel1();
		ml2 = new MainLevel2();
		ml3 = new MainLevel3();
		
		VarLevel1 v1 = new VarLevel1();
		v1.init();
		MethodParams<Object> params1 = new MethodParams<Object>();
		params1.set( PossibleParams.varLevelAll, v1 );
		ml1.initMainLevel( params1 );
		ml1.do1();
		
		VarLevel2 v2 = new VarLevel2();
		v2.init( "home2" );
		
		MethodParams<Object> params2 = new MethodParams<Object>();
		params2.set( PossibleParams.varLevelAll, v2 );
		
		boolean threw = false;
		try {
			ml2.initMainLevel( params2 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertFalse( threw );
		}
		
		ml2.initMainLevel( params2 );
		ml2.showHome();
		
		try {
			threw = false;
			ml1.initMainLevel( params2 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertFalse( threw );
		}
		
		try {
			threw = false;
			ml2.initMainLevel( params1 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		params1.set( PossibleParams.varLevelAll, "something" );
		try {
			threw = false;
			ml2.initMainLevel( params1 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		params1.set( PossibleParams.varLevelAll, null );
		try {
			threw = false;
			ml2.initMainLevel( params1 );
		} catch ( AssertionError ae ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		ml2.showHome();
		params2.set( PossibleParams.homeDir, "home3" );
		params2.remove( PossibleParams.varLevelAll );
		ml2.initMainLevel( params2 );
		ml2.showHome();
		
		ml2.initMainLevel( params2 );
		ml2.showHome();
		
		params2.set( PossibleParams.varLevelAll, null );
		try {
			threw = false;
			ml1.initMainLevel( params2 );
		} catch ( AssertionError ae ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		// Level 3:
		VarLevel3 v3 = new VarLevel3();
		v3.init( "homedirL3" );
		
		MethodParams<Object> params3 = new MethodParams<Object>();
		params3.set( PossibleParams.varLevelAll, null );
		try {
			threw = false;
			ml3.initMainLevel( params3 );
		} catch ( AssertionError ae ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		params3.set( PossibleParams.varLevelAll, v2 );
		try {
			threw = false;
			ml3.initMainLevel( params3 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		params3.set( PossibleParams.varLevelAll, v1 );
		try {
			threw = false;
			ml3.initMainLevel( params3 );
		} catch ( BadCallError bce ) {
			threw = true;
		} finally {
			assertTrue( threw );
		}
		
		params3.set( PossibleParams.varLevelAll, v3 );
		ml3.initMainLevel( params3 );
		ml3.showHome();
		
		params3.remove( PossibleParams.varLevelAll );
		params3.set( PossibleParams.homeDir, "L3nondefaultHomeDir" );
		ml3.initMainLevel( params3 );
		ml3.showHome();
	}
}
