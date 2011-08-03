/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



package org.dml.level010;



import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.dml.JUnits.*;
import org.dml.database.bdb.level1.*;
import org.dml.error.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.method.*;



/**
 * 
 *
 */
public class Level010_DMLEnvironmentTest {
	
	Level010_DMLEnvironment	dml1;
	Symbol					a, b, c;
	MethodParams			params;
	Symbol					anewFromDiffThread	= null;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		params = MethodParams.getNew();
		// params.init( null );
		
		// dml1 = new Level010_DMLEnvironment();
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// dml1.init( params );
		dml1 = Factory.getNewInstanceAndInit( Level010_DMLEnvironment.class, params );
	}
	
	
	@After
	public void tearDown() {
		
		if ( null != dml1 ) {
			Factory.deInitIfAlreadyInited( dml1 );
			// dml1.deInitSilently();
			dml1 = null;
		}
		// Factory.deInit( params );// don't move this to setUp() because is used in tests still
		params = null;
	}
	
	
	@SuppressWarnings( "boxing" )
	@Test
	public void test1() throws InterruptedException {
		
		try {
			a = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "A" ) );
			b = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "B" ) );
			c = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "C" ) );
			assertTrue( a != null );
			assertTrue( a == dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) );
			// refs equals too
			assertTrue( a == dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) );
			assertTrue( b == dml1.getSymbol( JavaID.ensureJavaIDFor( "B" ) ) );
			assertTrue( c == dml1.getSymbol( JavaID.ensureJavaIDFor( "C" ) ) );
			final Object t = dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) );
			assertTrue( a == t );
			// Object t2 = new Object();
			// boolean threw = false;
			// try {
			// assertFalse( a == t2 );
			// } catch ( BugError be ) {
			// threw = true;
			// } finally {
			// assertTrue( threw );
			// }
			
			
			// same contents in java
			final MethodParams params1 = MethodParams.getNew();
			// params = Factory.getNewInstanceAndInitWithoutParams( MethodParams.class );
			
			params1.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH + File.separator + "second" );
			params1.set( PossibleParams.jUnit_wipeDB, true );
			params1.set( PossibleParams.jUnit_wipeDBWhenDone, true );
			// RunTime.thro( new Exception( "testy" ) );
			final Level1_Storage_BerkeleyDB bdbL1 = Factory.getNewInstanceAndInit( Level1_Storage_BerkeleyDB.class, params1 );
			// Level1_Storage_BerkeleyDB bdbL1=
			final Long la = a.getTheStoredSymbol().getLong();
			final Symbol anew = Symbol.getNew( bdbL1, TheStoredSymbol.getNew( la ) );
			
			final Thread th0 = new Thread() {
				
				@Override
				public void run() {
					anewFromDiffThread = Symbol.getNew( bdbL1, TheStoredSymbol.getNew( la ) );
				}
			};
			th0.start();
			boolean threw = false;
			try {
				assertFalse( a.equals( anew ) );// not equal, from different storages!
			} catch ( final Throwable t1 ) {
				if ( RunTime.isThisWrappedException_of_thisType( t1, BadCallError.class ) ) {
					threw = true;
					RunTime.clearLastThrown_andAllItsWraps();
				}
			}
			assertTrue( threw );
			
			final JavaID aJID = dml1.getJavaID( a );
			assertTrue( dml1.getJavaID( anew ) == aJID );
			th0.join();
			assertNotNull( anewFromDiffThread );
			assertTrue( anew == anewFromDiffThread );
			assertTrue( dml1.getJavaID( anewFromDiffThread ) == aJID );
			final HashSet<Symbol> hs = new HashSet<Symbol>();
			hs.add( a );// calls Symbol.hashCode() first then if ever needed .equals
			hs.add( a );
			final Symbol b1 = dml1.newUniqueSymbol();
			hs.add( b1 );
			assertTrue( hs.size() == 2 );
		} finally {
			Factory.deInit( dml1 );
			// // dml1.deInit();
		}
	}
	
	
	@Test
	public void testMultiInits() {
		
		try {
			Factory.deInit( dml1 );
			// dml1.deInit();
			Factory.init( dml1, params );
			// dml1.init( params );
			// dml2.deInit();
			// dml2.init( Consts.DEFAULT_BDB_ENV_PATH );
		} finally {
			Factory.deInit( dml1 );
			// dml1.deInitAllLikeMe();
		}
	}
	
	
	@Test
	public void testUniqueAndAssociation() {
		
		final Symbol noID = dml1.newUniqueSymbol();
		assertNotNull( noID );
		final JavaID jid = JavaID.ensureJavaIDFor( "UniqueSymbol" );
		assertNull( dml1.getJavaID( noID ) );
		assertNull( dml1.getSymbol( jid ) );
		dml1.newLink( noID, jid );// no throws
		assertTrue( dml1.getJavaID( noID ) == jid );
		assertTrue( dml1.getSymbol( jid ) == noID );
		assertTrue( dml1.ensureLink( noID, jid ) );
	}
}
