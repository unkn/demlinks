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



package org.dml.level010;



import static org.junit.Assert.assertTrue;

import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class JavaIDTest {
	
	JavaID	a, b, c;
	
	@Before
	public void init() throws StorageException {

		// DMLEnvironment.init();
		JavaID.junitClearAll();
		assertTrue( JavaID.all_JavaIDs.size() == 0 );
		a = JavaID.ensureJavaIDFor( "A" );
		b = JavaID.ensureJavaIDFor( "B" );
		c = JavaID.ensureJavaIDFor( "C" );
	}
	
	@After
	public void tearDown() {

		// DMLEnvironment.deInit();
	}
	
	@Test
	public void listOfAllTest() {

		assertTrue( JavaID.all_JavaIDs.size() == 3 );
		System.out.println( JavaID.all_JavaIDs );
	}
	
	@Test
	public void duplicatesNotAllowedTest() {

		String _a = "ABCDEDFASA".substring( 0, 1 );
		assertTrue( "A" != _a );
		assertTrue( "A".equals( _a ) );// compare by contents yields true
		assertTrue( a == JavaID.ensureJavaIDFor( _a ) );// not re-added
		assertTrue( JavaID.all_JavaIDs.size() == 3 );
		// assertTrue( null != DMLEnvironment.AllWords );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 4 );
		System.out.println( JavaID.all_JavaIDs );
		// NodeJID.forgetJIDFor( "A" );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 2 );
		// assertTrue( a != NodeJID.getJIDFor( "A" ) );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
		String test = "test";
		JavaID j1 = JavaID.ensureJavaIDFor( test );
		JavaID.ensureJavaIDFor( "middle" );
		JavaID j2 = JavaID.ensureJavaIDFor( test );
		assertTrue( j1 == j2 );
		System.out.println( "!" + j1.toString() + "!" + j2.toString() + "!" );
		assertTrue( j1.equals( j2 ) );
	}
	
	// @Test
	// public void badCallTest() {
	//
	// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
	//
	// boolean aeGot = false;
	// try {
	// NodeJID.forgetJIDFor( "D" );
	// } catch ( BugError ae ) {
	// aeGot = true;
	// }
	// assertTrue( aeGot );
	//
	// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
	// }
	
	@Test
	public void nullTest() throws StorageException {

		boolean aeGot = false;
		try {
			JavaID.ensureJavaIDFor( null );
		} catch ( Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssertionError.class ) ) {
				aeGot = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( aeGot );
		
		// aeGot = false;
		// try {
		// DMLEnvironment.getJIDFor( null );
		// } catch ( AssertionError e ) {
		// aeGot = true;
		// }
		// assertTrue( aeGot );
		
		// aeGot = false;
		// try {
		// NodeJID.forgetJIDFor( null );
		// } catch ( AssertionError ae ) {
		// aeGot = true;
		// }
		// assertTrue( aeGot );
		
	}
}
