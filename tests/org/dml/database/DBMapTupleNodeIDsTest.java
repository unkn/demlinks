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



package org.dml.database;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.database.bdb.level2.DBMapSymbolsTuple;
import org.dml.error.BadCallError;
import org.dml.level010.JavaID;
import org.dml.level010.Symbol;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class DBMapTupleNodeIDsTest
{
	
	DBMapSymbolsTuple			tdb;
	// Level010_DMLEnvironment dmlEnv;
	Level1_Storage_BerkeleyDB	bdb;
	
	
	@Before
	public
			void
			setUp()
					throws DatabaseException
	{
		
		MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set(
					PossibleParams.homeDir,
					Consts.BDB_ENV_PATH );
		params.set(
					PossibleParams.jUnit_wipeDB,
					true );
		params.set(
					PossibleParams.jUnit_wipeDBWhenDone,
					true );
		bdb = Factory.getNewInstanceAndInit(
												Level1_Storage_BerkeleyDB.class,
												params );
		// bdb = new Level1_Storage_BerkeleyDB();
		// bdb.init( params );
		// params.deInit();
		// Factory.deInit( params );
		// tdb = new DBMapSymbolsTuple( bdb, "tupleIDs" );
		// tdb.init( null );
		tdb = Factory.getNewInstanceAndInitWithoutMethodParams(
																DBMapSymbolsTuple.class,
																bdb,
																"tupleIDs" );
		
	}
	

	@After
	public
			void
			tearDown()
	{
		

		Factory.deInitIfInited_WithPostponedThrows( bdb );
		bdb = null;
		System.out.println( "tdb:" );
		Factory.deInitIfInited_WithPostponedThrows( tdb );
		tdb = null;
		// System.out.println( "all:" );
		// Factory.deInitAll();
		// System.out.println( "done" );
	}
	

	@Test
	public
			void
			test1()
					throws DatabaseException,
					StorageException
	{
		
		String strA = "A";
		JavaID jidA = JavaID.ensureJavaIDFor( strA );
		Symbol _a = bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																	jidA );
		Symbol _b = bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																	JavaID.ensureJavaIDFor( "B" ) );
		Symbol _d = bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																	JavaID.ensureJavaIDFor( "D" ) );
		Symbol _e = bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																	JavaID.ensureJavaIDFor( "E" ) );
		Symbol _c = bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																	JavaID.ensureJavaIDFor( "C" ) );
		
		assertNotNull( _a );
		assertNotNull( _b );
		assertNotNull( _d );
		assertNotNull( _e );
		assertNotNull( _c );
		
		assertTrue( jidA.equals( bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
																				_a ) ) );
		assertTrue( bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
																	_a ) == jidA );
		
		org.junit.Assert.assertFalse( tdb.isVector(
													_a,
													_b ) );
		org.junit.Assert.assertFalse( tdb.ensureVector(
														_a,
														_b ) );
		
		assertTrue( tdb.isVector(
									_a,
									_b ) );
		assertTrue( tdb.ensureVector(
										_a,
										_b ) );
		
		assertFalse( tdb.ensureVector(
										_d,
										_e ) );
		
		assertFalse( tdb.ensureVector(
										_a,
										_c ) );
		assertTrue( tdb.isVector(
									_a,
									_c ) );
		
		assertFalse( tdb.ensureVector(
										_c,
										_a ) );
		assertFalse( tdb.ensureVector(
										_c,
										_b ) );
		assertTrue( tdb.isVector(
									_c,
									_a ) );
		assertTrue( tdb.isVector(
									_c,
									_b ) );
		
		BDBVectorIterator<Symbol, Symbol> iter = tdb.getIterator_on_Terminals_of( _a );
		try
		{
			iter.goFirst();
			do
			{
				if ( null != iter.now() )
				{
					System.out.println( bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
																						iter.now() ) );
				}
				iter.goNext();
			}
			while ( iter.now() != null );
			
			iter.goFirst();
			// assertTrue( _b.equals( iter.now() ) );
			assertTrue( _b == iter.now() );// ye as ref also!
			
			iter.goNext();
			// assertTrue( _c.equals( iter.now() ) );
			assertTrue( _c == iter.now() );// ye as ref also
			
			iter.goNext();
			assertNull( iter.now() );
			
			iter.goTo( _c );
			assertTrue( iter.now() == _c );
			iter.goPrev();
			assertTrue( iter.now() == _b );
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
		

		iter = tdb.getIterator_on_Initials_of( _b );
		try
		{
			iter.goFirst();
			do
			{
				if ( null != iter.now() )
				{
					System.out.println( "/2/" + bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
																								iter.now() ) );
				}
				iter.goNext();
			}
			while ( iter.now() != null );
			

			iter.goFirst();
			System.out.println( "//" + bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
																					iter.now() ) );
			// assertTrue( _a.equals( iter.now() ) );
			assertTrue( _a == iter.now() );// also as refs
			
			iter.goNext();
			// assertTrue( _c.equals( iter.now() ) );
			assertTrue( _c == iter.now() );// ack
			
			iter.goNext();
			assertNull( iter.now() );
			boolean threw = false;
			try
			{
				iter.goPrev();
			}
			catch ( Throwable t )
			{
				if ( RunTime.isThisWrappedException_of_thisType(
																	t,
																	BadCallError.class ) )
				{
					threw = true;
					RunTime.clearLastThrown_andAllItsWraps();
				}
			}
			assertTrue( threw );
			iter.goFirst();
			iter.goNext();
			iter.goPrev();
			// assertTrue( _a.equals( iter.now() ) );
			assertTrue( _a == iter.now() );// indeed
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
		
		iter = tdb.getIterator_on_Initials_of( _b );
		boolean threw = false;
		try
		{
			iter.goNext();// w/o goFirst
		}
		catch ( Throwable t )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																BadCallError.class ) )
			{
				threw = true;// should throw!
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
		assertTrue( threw );
		
		iter = tdb.getIterator_on_Initials_of( _b );
		threw = false;
		try
		{
			iter.goPrev();// w/o goFirst
		}
		catch ( Throwable t )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																BadCallError.class ) )
			{
				threw = true;// shouldn't throw though
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
		assertTrue( threw );
		
		assertTrue( tdb.countInitials( _b ) == 2 );
		assertTrue( tdb.countTerminals( _a ) == 2 );
		
		assertTrue( tdb.removeVector(
										_a,
										_b ) );
		assertTrue( tdb.countInitials( _b ) == 1 );
		assertTrue( tdb.countTerminals( _a ) == 1 );
		
		assertFalse( tdb.removeVector(
										_a,
										_b ) );
		assertTrue( tdb.countInitials( _b ) == 1 );
		assertTrue( tdb.countTerminals( _a ) == 1 );
	}
}
