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



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Bridge_SymbolAndBDB;
import org.dml.storagewrapper.StorageException;
import org.dml.tracking.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Level010_DMLEnvironmentTest
{
	
	Level010_DMLEnvironment	dml1;
	Symbol					a, b, c;
	MethodParams			params;
	
	
	@Before
	public
			void
			setUp()
					throws StorageException
	{
		
		params = MethodParams.getNew();
		// params.init( null );
		
		// dml1 = new Level010_DMLEnvironment();
		params.set(
					PossibleParams.homeDir,
					Consts.BDB_ENV_PATH );
		params.set(
					PossibleParams.jUnit_wipeDBWhenDone,
					true );
		// dml1.init( params );
		dml1 = Factory.getNewInstanceAndInit(
												Level010_DMLEnvironment.class,
												params );
	}
	

	@After
	public
			void
			tearDown()
	{
		
		Factory.deInitIfAlreadyInited( dml1 );
		// dml1.deInitSilently();
		dml1 = null;
		// Factory.deInit( params );// don't move this to setUp() because is used in tests still
		params = null;
	}
	

	@Test
	public
			void
			test1()
					throws StorageException
	{
		
		try
		{
			a = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "A" ) );
			b = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "B" ) );
			c = dml1.ensureSymbol( JavaID.ensureJavaIDFor( "C" ) );
			assertTrue( a != null );
			assertTrue( a == dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) );
			// refs equals too
			assertTrue( a == dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) ) );
			assertTrue( b == dml1.getSymbol( JavaID.ensureJavaIDFor( "B" ) ) );
			assertTrue( c == dml1.getSymbol( JavaID.ensureJavaIDFor( "C" ) ) );
			Object t = dml1.getSymbol( JavaID.ensureJavaIDFor( "A" ) );
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
			long la = Bridge_SymbolAndBDB.getLongFrom( a );
			Symbol anew = Bridge_SymbolAndBDB.newSymbolFrom( la );
			assertTrue( a == anew );// indeed
			assertTrue( dml1.getJavaID( anew ) == dml1.getJavaID( a ) );
		}
		finally
		{
			Factory.deInit( dml1 );
			// dml1.deInit();
		}
	}
	

	@SuppressWarnings( "deprecation" )
	@Test
	public
			void
			testMultiInits()
					throws StorageException
	{
		
		try
		{
			Factory.deInit( dml1 );
			// dml1.deInit();
			Factory.init(
							dml1,
							params );
			// dml1.init( params );
			// dml2.deInit();
			// dml2.init( Consts.DEFAULT_BDB_ENV_PATH );
		}
		finally
		{
			Factory.deInit( dml1 );
			// dml1.deInitAllLikeMe();
		}
	}
	

	@Test
	public
			void
			testUniqueAndAssociation()
	{
		
		Symbol noID = dml1.newUniqueSymbol();
		assertNotNull( noID );
		JavaID jid = JavaID.ensureJavaIDFor( "UniqueSymbol" );
		assertNull( dml1.getJavaID( noID ) );
		assertNull( dml1.getSymbol( jid ) );
		dml1.newLink(
						noID,
						jid );// no throws
		assertTrue( dml1.getJavaID( noID ) == jid );
		assertTrue( dml1.getSymbol( jid ) == noID );
		assertTrue( dml1.ensureLink(
										noID,
										jid ) );
	}
}
