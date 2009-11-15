/**
 * File creation: May 30, 2009 12:01:31 AM
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


package org.dml.level1;



import static org.junit.Assert.assertTrue;

import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class NodeJavaIDTest {
	
	NodeJavaID	a, b, c;
	
	@Before
	public void init() throws StorageException {

		// DMLEnvironment.init();
		NodeJavaID.junitClearAll();
		assertTrue( NodeJavaID.all_Level1_NodeJavaIDs.size() == 0 );
		a = NodeJavaID.ensureJavaIDFor( "A" );
		b = NodeJavaID.ensureJavaIDFor( "B" );
		c = NodeJavaID.ensureJavaIDFor( "C" );
	}
	
	@After
	public void tearDown() {

		// DMLEnvironment.deInit();
	}
	
	@Test
	public void listOfAllTest() {

		assertTrue( NodeJavaID.all_Level1_NodeJavaIDs.size() == 3 );
		System.out.println( NodeJavaID.all_Level1_NodeJavaIDs );
	}
	
	@Test
	public void duplicatesNotAllowedTest() {

		String _a = "ABCDEDFASA".substring( 0, 1 );
		assertTrue( "A" != _a );
		assertTrue( "A".equals( _a ) );// compare by contents yields true
		assertTrue( a == NodeJavaID.ensureJavaIDFor( _a ) );// not re-added
		assertTrue( NodeJavaID.all_Level1_NodeJavaIDs.size() == 3 );
		// assertTrue( null != DMLEnvironment.AllWords );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 4 );
		System.out.println( NodeJavaID.all_Level1_NodeJavaIDs );
		// NodeJID.forgetJIDFor( "A" );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 2 );
		// assertTrue( a != NodeJID.getJIDFor( "A" ) );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
		String test = "test";
		NodeJavaID j1 = NodeJavaID.ensureJavaIDFor( test );
		NodeJavaID.ensureJavaIDFor( "middle" );
		NodeJavaID j2 = NodeJavaID.ensureJavaIDFor( test );
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
			NodeJavaID.ensureJavaIDFor( null );
		} catch ( AssertionError ae ) {
			aeGot = true;
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
