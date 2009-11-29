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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.error.BugError;
import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Level1_DMLEnvironmentTest {
	
	Level1_DMLEnvironment	dml1;
	Symbol					a, b, c;
	MethodParams<Object>	params;
	
	@Before
	public void setUp() throws StorageException {

		params = new MethodParams<Object>();
		params.init( null );
		
		dml1 = new Level1_DMLEnvironment();
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		dml1.init( params );
		
	}
	
	@After
	public void tearDown() {

		dml1.deInitSilently();
		dml1 = null;
		params.deInit();
		params = null;
	}
	
	@Test
	public void test1() throws StorageException {

		try {
			a = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "A" ) );
			b = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "B" ) );
			c = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "C" ) );
			assertTrue( a != null );
			assertTrue( a.equals( dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) ) );
			// refs equals too
			assertTrue( a == dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) );
			assertTrue( b.equals( dml1.getSymbol( JavaID.ensureJavaIDFor( "B" ) ) ) );
			assertTrue( c.equals( dml1.getSymbol( JavaID.ensureJavaIDFor( "C" ) ) ) );
			Object t = dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) );
			assertTrue( a.equals( t ) );
			Object t2 = new Object();
			boolean threw = false;
			try {
				assertFalse( a.equals( t2 ) );
			} catch ( BugError be ) {
				threw = true;
			} finally {
				assertTrue( threw );
			}
			

			// same contents in java
			long la = a.internalGetForBDBBinding();
			Symbol anew = Symbol.internalNewSymbolRepresentationFor( la );
			assertTrue( a == anew );// indeed
			assertTrue( a.equals( anew ) );
			assertTrue( dml1.getJavaID( anew ) == dml1.getJavaID( a ) );
		} finally {
			dml1.deInit();
		}
	}
	
	@Test
	public void testMultiInits() throws StorageException {

		try {
			dml1.deInit();
			dml1.init( params );
			// dml2.deInit();
			// dml2.init( Consts.DEFAULT_BDB_ENV_PATH );
		} finally {
			dml1.deInitAllLikeMe();
		}
	}
	
	@Test
	public void testUniqueAndAssociation() {

		Symbol noID = dml1.newUniqueSymbol();
		assertNotNull( noID );
		JavaID jid = JavaID.ensureJavaIDFor( "UniqueSymbol" );
		assertNull( dml1.getJavaID( noID ) );
		assertNull( dml1.getSymbol( jid ) );
		dml1.newLink( noID, jid );// no throws
		assertTrue( dml1.getJavaID( noID ) == jid );
		assertTrue( dml1.getSymbol( jid ) == noID );
		assertTrue( dml1.ensureLink( noID, jid ) );
	}
}
