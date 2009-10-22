/**
 * File creation: Jun 7, 2009 2:58:18 AM
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


package org.dml.database;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.Level2_BerkeleyDB;
import org.dml.database.bdb.OneToManyDBMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class OneToManyDBMapTest {
	
	OneToManyDBMap	o2m;
	// the following two should be random unique names not already in the dbase
	// or else the tests may fail
	final String	_a	= "A" + new Object();
	final String	_b	= "B" + new Object();
	final String	_c	= "C" + new Object();
	Level2_BerkeleyDB		bdb;
	
	@Before
	public void setUp() throws DatabaseException {

		bdb = new Level2_BerkeleyDB( Consts.BDB_ENV_PATH, true );
		o2m = new OneToManyDBMap( bdb, "one to many" );// + new Object() );
	}
	
	@After
	public void tearDown() {

		o2m.silentClose();
		bdb.deInit();
	}
	
	/**
	 * Test method for
	 * {@link org.dml.database.bdb.OneToManyDBMap#ensureGroup(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testEnsureGroup() throws DatabaseException {

		System.out.println( _a );
		System.out.println( _b );
		System.out.println( _c );
		System.out.println( o2m.getName() );
		
		assertFalse( o2m.isGroup( _a, _b ) );
		assertFalse( o2m.ensureGroup( _a, _b ) );
		assertTrue( o2m.isGroup( _a, _b ) );
		
		assertTrue( o2m.ensureGroup( _a, _b ) );
		

		assertFalse( o2m.isGroup( _b, _a ) );
		assertFalse( o2m.ensureGroup( _b, _a ) );
		assertTrue( o2m.isGroup( _b, _a ) );
		
		assertTrue( o2m.ensureGroup( _b, _a ) );
		

		assertFalse( o2m.isGroup( _a, _c ) );
		assertFalse( o2m.ensureGroup( _a, _c ) );
		assertTrue( o2m.isGroup( _a, _c ) );
		
		assertTrue( o2m.ensureGroup( _a, _c ) );
		
		assertFalse( o2m.isGroup( _c, _b ) );
		assertFalse( o2m.ensureGroup( _c, _b ) );
		assertTrue( o2m.isGroup( _c, _b ) );
		
		assertTrue( o2m.ensureGroup( _c, _b ) );
		
		// assertFalse( o2m.ensureGroup( "", "" ) );
		// assertTrue( o2m.ensureGroup( "", "" ) );
	}
	
}
