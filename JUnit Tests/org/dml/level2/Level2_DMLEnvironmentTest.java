/**
 * File creation: Oct 20, 2009 12:22:26 AM
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



import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.error.BadCallError;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.StaticInstanceTracker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class Level2_DMLEnvironmentTest {
	
	Level2_DMLEnvironment	dml2;
	NodeID					a, b, c;
	
	@Before
	public void setUp() throws StorageException {

		dml2 = new Level2_DMLEnvironment();
		dml2.init( Consts.BDB_ENV_PATH );
		
	}
	
	@After
	public void tearDown() {

		try {
			dml2.deInit();
		} catch ( BadCallError bce ) {
			// ignore
		}
		dml2 = null;
	}
	
	@Test
	public void test1() throws StorageException {

		try {
			a = dml2.ensureNodeID( NodeJID.ensureJIDFor( "A" ) );
			b = dml2.ensureNodeID( NodeJID.ensureJIDFor( "B" ) );
			c = dml2.ensureNodeID( NodeJID.ensureJIDFor( "C" ) );
			assertTrue( a != null );
			assertTrue( a.equals( dml2.getNodeID( NodeJID.ensureJIDFor( "A" ) ) ) );
			assertTrue( a != dml2.getNodeID( NodeJID.ensureJIDFor( "A" ) ) );
			assertTrue( b.equals( dml2.getNodeID( NodeJID.ensureJIDFor( "B" ) ) ) );
			assertTrue( c.equals( dml2.getNodeID( NodeJID.ensureJIDFor( "C" ) ) ) );
		} finally {
			dml2.deInit();
		}
	}
	
	@Test
	public void testMultiInits() throws StorageException {

		try {
			dml2.deInit();
			dml2.init( Consts.BDB_ENV_PATH );
			// dml2.deInit();
			// dml2.init( Consts.DEFAULT_BDB_ENV_PATH );
		} finally {
			for ( int i = 0; i < StaticInstanceTracker.ALL_INSTANCES.size(); i++ ) {
				System.out.println( StaticInstanceTracker.ALL_INSTANCES.getObjectAt( i ) );
			}
			System.out.println();
			StaticInstanceTracker.deInitAll();
		}
	}
}
