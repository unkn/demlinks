/**
 * File creation: May 31, 2009 10:29:20 AM
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
import org.dml.environ.DMLEnvironment;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class NodeIDTest {
	
	DMLEnvironment	dml;
	NodeID			a, b, c;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		dml = DMLEnvironment.getNew( Consts.BDB_ENV_PATH, true );
		a = dml.ensureNodeID( NodeJID.ensureJIDFor( "A" ) );
		b = dml.ensureNodeID( NodeJID.ensureJIDFor( "B" ) );
		c = dml.ensureNodeID( NodeJID.ensureJIDFor( "C" ) );
		assertTrue( a != null );
	}
	
	@After
	public void tearDown() {

		dml.deInit();
	}
	
	@Test
	public void someTest() throws StorageException {

		
		assertTrue( a.equals( dml.getNodeID( NodeJID.ensureJIDFor( "A" ) ) ) );
		assertTrue( b.equals( dml.getNodeID( NodeJID.ensureJIDFor( "B" ) ) ) );
		assertTrue( c.equals( dml.getNodeID( NodeJID.ensureJIDFor( "C" ) ) ) );
		
	}
	
	@Test
	public void exceptionTest() throws Exception {

		// throw new Exception();
	}
}
