/**
 * File creation: Jun 8, 2009 3:25:28 PM
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level2.DBMapTupleNodeIDs;
import org.dml.level1.Level1_DMLEnvironment;
import org.dml.level1.Symbol;
import org.dml.level1.SymbolJavaID;
import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.Position;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class DBMapTupleNodeIDsTest {
	
	DBMapTupleNodeIDs			tdb;
	Level1_DMLEnvironment		dmlEnv;
	Level1_Storage_BerkeleyDB	bdb;
	
	@Before
	public void setUp() throws DatabaseException {

		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		bdb = new Level1_Storage_BerkeleyDB();
		bdb.init( params );
		params.deInit();
		tdb = new DBMapTupleNodeIDs( bdb, "tupleIDs" );
		
	}
	
	@After
	public void tearDown() {

		// tdb.silentClose();
		tdb = null;
		bdb.deInit();
		bdb = null;
	}
	
	@Test
	public void test1() throws DatabaseException, StorageException {

		String strA = "A";
		SymbolJavaID jidA = SymbolJavaID.ensureJavaIDFor( strA );
		Symbol _a = bdb.getDBMap_JavaIDs_To_NodeIDs().ensureSymbol( jidA );
		Symbol _b = bdb.getDBMap_JavaIDs_To_NodeIDs().ensureSymbol(
				SymbolJavaID.ensureJavaIDFor( "B" ) );
		Symbol _d = bdb.getDBMap_JavaIDs_To_NodeIDs().ensureSymbol(
				SymbolJavaID.ensureJavaIDFor( "D" ) );
		Symbol _e = bdb.getDBMap_JavaIDs_To_NodeIDs().ensureSymbol(
				SymbolJavaID.ensureJavaIDFor( "E" ) );
		Symbol _c = bdb.getDBMap_JavaIDs_To_NodeIDs().ensureSymbol(
				SymbolJavaID.ensureJavaIDFor( "C" ) );
		
		assertNotNull( _a );
		assertNotNull( _b );
		assertNotNull( _d );
		assertNotNull( _e );
		assertNotNull( _c );
		
		assertTrue( jidA.equals( bdb.getDBMap_JavaIDs_To_NodeIDs().getSymbolJavaID(
				_a ) ) );
		assertTrue( bdb.getDBMap_JavaIDs_To_NodeIDs().getSymbolJavaID( _a ) == jidA );
		
		org.junit.Assert.assertFalse( tdb.isVector( _a, _b ) );
		org.junit.Assert.assertFalse( tdb.ensureVector( _a, _b ) );
		
		assertTrue( tdb.isVector( _a, _b ) );
		assertTrue( tdb.ensureVector( _a, _b ) );
		
		assertFalse( tdb.ensureVector( _d, _e ) );
		
		assertFalse( tdb.ensureVector( _a, _c ) );
		assertTrue( tdb.isVector( _a, _c ) );
		
		assertFalse( tdb.ensureVector( _c, _a ) );
		assertFalse( tdb.ensureVector( _c, _b ) );
		assertTrue( tdb.isVector( _c, _a ) );
		assertTrue( tdb.isVector( _c, _b ) );
		
		VectorIterator iter = tdb.getTerminalIteratorFor( _a );
		iter.goFirst();
		assertTrue( _b.equals( iter.now() ) );
		assertTrue( _b != iter );
		
		iter = tdb.getTerminal( _a, Position.AFTER, iter );
		assertTrue( _c.equals( iter ) );
		assertTrue( _c != iter );
		
		iter = tdb.getTerminal( _a, Position.AFTER, iter );
		assertNull( iter );
		
		iter = tdb.getInitialIteratorFor( _b );
		iter.goFirst();
		assertTrue( _a.equals( iter.now() ) );
		assertTrue( _a != iter );
		
		iter = tdb.getInitial( _b, Position.LAST );
		assertTrue( _c.equals( iter ) );
		assertTrue( _c != iter );
		
		assertNull( tdb.getInitial( _b, Position.AFTER, iter ) );
		iter = tdb.getInitial( _b, Position.BEFORE, iter );
		assertTrue( _a.equals( iter ) );
		assertTrue( _a != iter );
		
		assertTrue( tdb.countInitial( _b ) == 2 );
		assertTrue( tdb.countTerminal( _a ) == 2 );
	}
}
