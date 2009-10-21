/**
 * File creation: Oct 17, 2009 6:40:55 AM
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


package org.dml.level4;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.level1.Level1_DMLEnvironment;
import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.level4.Level4_DMLEnvironment;
import org.junit.Test;



/**
 * 
 *
 */
public class Level4_DMLEnvironmentTest {
	
	@Test
	public void multiple() throws Exception {

		Level4_DMLEnvironment d1 = Level4_DMLEnvironment.getNew(
				Consts.BDB_ENV_PATH + "1&2", true );
		Level4_DMLEnvironment d2 = Level4_DMLEnvironment.getNew(
				Consts.BDB_ENV_PATH + "1&2", false );
		Level4_DMLEnvironment d3 = Level4_DMLEnvironment.getNew(
				Consts.BDB_ENV_PATH + "3", true );
		try {
			// if ( 1 == 1 ) {
			// throw new Exception( "blah" );
			// }
			String test1 = "test1";
			String test2 = "test2";
			NodeJID j1 = NodeJID.ensureJIDFor( test1 );
			NodeJID j2 = NodeJID.ensureJIDFor( test2 );
			NodeID n1 = d1.createNodeID( j1 );
			NodeID n2 = d1.createNodeID( j2 );
			assertTrue( n1.equals( d1.getNodeID( j1 ) ) );
			assertTrue( n2.equals( d1.getNodeID( j2 ) ) );
			assertTrue( n1.equals( d2.getNodeID( j1 ) ) );// d2 is d1 inside BDB
			// because they're
			// in same dir
			assertTrue( n2.equals( d2.getNodeID( j2 ) ) );
			
			NodeID n3 = d3.getNodeID( j1 );// d3 is in diff dir
			assertNull( n3 );
			n3 = d3.getNodeID( j2 );
			assertNull( n3 );
			System.out.println( d3.getNodeJID( n1 ) );
			assertFalse( d1.ensureGroup( n1, n2 ) );
			assertFalse( d1.ensureGroup( n1, n1 ) );
			assertTrue( d1.ensureGroup( n1, n2 ) );
			assertTrue( d1.ensureGroup( n1, n1 ) );
			
		} finally {
			d1.deInit();
			// d2.deInit();
			Level1_DMLEnvironment.deInitAll();
		}
	}
}
