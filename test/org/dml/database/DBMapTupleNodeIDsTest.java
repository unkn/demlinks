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



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.dml.database.bdb.BerkeleyDB;
import org.dml.database.bdb.DBMapTupleNodeIDs;
import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class DBMapTupleNodeIDsTest {
	
	DBMapTupleNodeIDs	tdb;
	
	@Before
	public void setUp() {

		BerkeleyDB.initAll();
		tdb = new DBMapTupleNodeIDs( "tupleIDs" + new Object(),
				BerkeleyDB.getDBMapJIDsToNodeIDs() );
		
	}
	
	@After
	public void tearDown() {

		tdb.silentClose();
		tdb = null;
		BerkeleyDB.deInitAll();
	}
	
	@Test
	public void test1() throws StorageException, DatabaseException {

		NodeID _a = NodeID.ensureNode( NodeJID.ensureJIDFor( "A" + new Object() ) );
		NodeID _b = NodeID.ensureNode( NodeJID.ensureJIDFor( "B" + new Object() ) );
		assertNotNull( _a );
		assertNotNull( _b );
		org.junit.Assert.assertFalse( tdb.isGroup( _a, _b ) );
		org.junit.Assert.assertFalse( tdb.ensureGroup( _a, _b ) );
		
		assertTrue( tdb.isGroup( _a, _b ) );
		assertTrue( tdb.ensureGroup( _a, _b ) );
	}
}
