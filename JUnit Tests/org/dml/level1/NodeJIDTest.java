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
public class NodeJIDTest {
	
	NodeJID	a, b, c;
	
	@Before
	public void init() throws StorageException {

		// DMLEnvironment.init();
		NodeJID.clearAllForJUnit();
		assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 0 );
		a = NodeJID.ensureJIDFor( "A" );
		b = NodeJID.ensureJIDFor( "B" );
		c = NodeJID.ensureJIDFor( "C" );
	}
	
	@After
	public void tearDown() {

		// DMLEnvironment.deInit();
	}
	
	@Test
	public void listOfAllTest() {

		assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
		System.out.println( NodeJID.all_Level1_NodeJIDs );
	}
	
	@Test
	public void duplicatesNotAllowedTest() {

		String _a = "ABCDEDFASA".substring( 0, 1 );
		assertTrue( "A" != _a );
		assertTrue( "A".equals( _a ) );
		assertTrue( a == NodeJID.ensureJIDFor( _a ) );
		assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
		// assertTrue( null != DMLEnvironment.AllWords );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 4 );
		System.out.println( NodeJID.all_Level1_NodeJIDs );
		// NodeJID.forgetJIDFor( "A" );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 2 );
		// assertTrue( a != NodeJID.getJIDFor( "A" ) );
		// assertTrue( NodeJID.all_Level1_NodeJIDs.size() == 3 );
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
			NodeJID.ensureJIDFor( null );
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
