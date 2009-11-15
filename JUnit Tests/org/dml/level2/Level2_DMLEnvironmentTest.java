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


package org.dml.level2;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.level1.NodeID;
import org.dml.level1.NodeJavaID;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Level2_DMLEnvironmentTest {
	
	@Test
	public void multiple() throws Exception {

		MethodParams<Object> params = new MethodParams<Object>();
		params.set( PossibleParams.wipeDB, true );
		
		Level2_DMLEnvironment d1 = new Level2_DMLEnvironment();
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH + "1&2" );
		d1.init( params );
		
		Level2_DMLEnvironment d2 = new Level2_DMLEnvironment();
		d2.init( params );
		
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH + "3" );
		Level2_DMLEnvironment d3 = new Level2_DMLEnvironment();
		d3.init( params );
		
		try {
			// if ( 1 == 1 ) {
			// throw new Exception( "blah" );
			// }
			String test1 = "test1";
			String test2 = "test2";
			NodeJavaID j1 = NodeJavaID.ensureJavaIDFor( test1 );
			assertTrue( test1 == j1.getObject() );
			NodeJavaID j2 = NodeJavaID.ensureJavaIDFor( test2 );
			assertTrue( j2.getObject() == test2 );
			assertTrue( test1 != test2 );
			assertTrue( j1 != j2 );
			assertFalse( j1.equals( j2 ) );
			NodeID n1 = d1.createNodeID( j1 );
			NodeID n2 = d1.createNodeID( j2 );
			assertNotNull( n1 );
			assertNotNull( d1.getNodeJavaID( n1 ) );
			assertTrue( d1.getNodeJavaID( n1 ).equals( j1 ) );
			assertTrue( d1.getNodeJavaID( n1 ) == j1 );
			assertTrue( n1.equals( d1.getNodeID( j1 ) ) );
			// FIXME: maybe fix? getNodeID() does a new every time
			assertTrue( n1 != d1.getNodeID( j1 ) );
			assertTrue( n2.equals( d1.getNodeID( j2 ) ) );
			assertTrue( n2 != d1.getNodeID( j2 ) );
			assertTrue( n1.equals( d2.getNodeID( j1 ) ) );// d2 is d1
			// inside
			// BDB
			// because they're
			// in same dir
			assertTrue( n2.equals( d2.getNodeID( j2 ) ) );
			
			NodeID n3 = d3.getNodeID( j1 );// d3 is in diff dir
			assertNull( n3 );
			n3 = d3.getNodeID( j2 );
			assertNull( n3 );
			System.out.println( d1.getNodeJavaID( n1 ) );
			System.out.println( d3.getNodeJavaID( n1 ) );
			assertFalse( d1.isVector( n1, n2 ) );
			assertFalse( d1.isVector( n1, n1 ) );
			assertFalse( d1.ensureVector( n1, n2 ) );
			assertFalse( d1.ensureVector( n1, n1 ) );
			assertTrue( d1.ensureVector( n1, n2 ) );
			assertTrue( d1.ensureVector( n1, n1 ) );
			assertTrue( d1.isVector( n1, n2 ) );
			assertTrue( d1.isVector( n1, n1 ) );
			
		} finally {
			d1.deInit();
			// d2.deInit();
			assertFalse( d1.isInited() );
			assertTrue( d2.isInited() );
			assertTrue( d3.isInited() );
			d1.deInitAllLikeMe();
			assertFalse( d1.isInited() );
			assertFalse( d2.isInited() );
			assertFalse( d3.isInited() );
		}
	}
}
