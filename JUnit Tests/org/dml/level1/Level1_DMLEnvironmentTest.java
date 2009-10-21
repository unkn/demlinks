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


package org.dml.level1;



import static org.junit.Assert.assertTrue;

import org.dml.error.BadCallError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class Level1_DMLEnvironmentTest {
	
	Level1_DMLEnvironment	dml1;
	
	@Before
	public void setUp() {

		dml1 = new Level1_DMLEnvironment();
		dml1.init();
		NodeJID.clearAllForJUnit();
	}
	
	@After
	public void tearDown() {

		try {
			dml1.deInit();
		} catch ( BadCallError bce ) {
			// ignore
		}
		dml1 = null;
	}
	
	@Test
	public void test1() {

		try {
			String test = "test";
			NodeJID j1 = NodeJID.ensureJIDFor( test );
			NodeJID.ensureJIDFor( "middle" );
			NodeJID j2 = NodeJID.ensureJIDFor( test );
			assertTrue( j1 == j2 );
			System.out.println( "!" + j1.getAsString() + "!" + j2.getAsString()
					+ "!" );
			assertTrue( j1.equals( j2 ) );
			
		} finally {
			dml1.deInit();
		}
	}
}
