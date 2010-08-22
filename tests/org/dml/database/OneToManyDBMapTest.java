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
import static org.junit.Assert.assertTrue;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.AllTupleBindings;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.database.bdb.level2.OneToManyDBMap;
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
public class OneToManyDBMapTest
{
	
	OneToManyDBMap<String, String>	o2m;
	// the following two should be random unique names not already in the dbase
	// or else the tests may fail
	final String					_a	= "A" + new Object();
	final String					_b	= "B" + new Object();
	final String					_c	= "C" + new Object();
	Level1_Storage_BerkeleyDB		bdb;
	
	
	@SuppressWarnings( "unchecked" )
	@Before
	public
			void
			setUp()
					throws DatabaseException
	{
		
		// bdb = new Level1_Storage_BerkeleyDB();
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
		// bdb.init( params );
		bdb = Factory.getNewInstanceAndInit(
												Level1_Storage_BerkeleyDB.class,
												params );
		// params.deInit();
		// Factory.deInit( params );
		
		// o2m = new OneToManyDBMap<String, String>( bdb, "one to many", String.class,
		// AllTupleBindings.getBinding( String.class ), String.class, AllTupleBindings.getBinding( String.class ) );
		// o2m.init( null );
		o2m = new OneToManyDBMap<String, String>(
													bdb,
													"one to many",
													String.class,
													AllTupleBindings.getBinding( String.class ),
													String.class,
													AllTupleBindings.getBinding( String.class ) );
		Factory.initWithoutParams( o2m );
	}
	

	@After
	public
			void
			tearDown()
	{
		
		Factory.deInitIfAlreadyInited( o2m );
		Factory.deInitIfAlreadyInited( bdb );
		// o2m.deInit();
		// bdb.deInit();
	}
	

	/**
	 * Test method for
	 * {@link org.dml.database.bdb.level2.OneToManyDBMap#ensureVector(java.lang.String, java.lang.String)} .
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public
			void
			testEnsureGroup()
					throws DatabaseException
	{
		
		System.out.println( _a );
		System.out.println( _b );
		System.out.println( _c );
		System.out.println( o2m.getDBName() );
		
		assertFalse( o2m.isVector(
									_a,
									_b ) );
		assertFalse( o2m.ensureVector(
										_a,
										_b ) );
		assertTrue( o2m.isVector(
									_a,
									_b ) );
		
		assertTrue( o2m.ensureVector(
										_a,
										_b ) );
		

		assertFalse( o2m.isVector(
									_b,
									_a ) );
		assertFalse( o2m.ensureVector(
										_b,
										_a ) );
		assertTrue( o2m.isVector(
									_b,
									_a ) );
		
		assertTrue( o2m.ensureVector(
										_b,
										_a ) );
		

		assertFalse( o2m.isVector(
									_a,
									_c ) );
		assertFalse( o2m.ensureVector(
										_a,
										_c ) );
		assertTrue( o2m.isVector(
									_a,
									_c ) );
		
		assertTrue( o2m.ensureVector(
										_a,
										_c ) );
		
		assertFalse( o2m.isVector(
									_c,
									_b ) );
		assertFalse( o2m.ensureVector(
										_c,
										_b ) );
		assertTrue( o2m.isVector(
									_c,
									_b ) );
		
		assertTrue( o2m.ensureVector(
										_c,
										_b ) );
		
		BDBVectorIterator<String, String> iter = o2m.getIterator_on_Initials_of( _c );
		assertNotNull( iter );
		try
		{
			iter.goFirst();
			do
			{
				System.out.println( iter.now() + " -> _c" );
				assertTrue( iter.now().equals(
												_a ) );
				assertTrue( iter.now() != _a );
				iter.goNext();
			}
			while ( null != iter.now() );
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
		
		iter = o2m.getIterator_on_Terminals_of( _c );
		try
		{
			iter.goFirst();
			do
			{
				if ( null != iter )
				{
					System.out.println( "_c -> " + iter.now() );
					assertTrue( iter.now().equals(
													_b ) );
					assertTrue( iter.now() != _b );
				}
				iter.goNext();
			}
			while ( null != iter.now() );
		}
		finally
		{
			Factory.deInit( iter );
			// iter.deInit();
		}
	}
	

	@Test
	public
			void
			testSame()
					throws DatabaseException
	{
		
		assertFalse( o2m.ensureVector(
										_a,
										_a ) );
		assertTrue( o2m.ensureVector(
										_a,
										_a ) );
		assertTrue( o2m.isVector(
									_a,
									_a ) );
	}
	

	@Test
	public
			void
			testEmpty()
					throws DatabaseException
	{
		
		assertFalse( o2m.ensureVector(
										"",
										"" ) );
		assertTrue( o2m.ensureVector(
										"",
										"" ) );
		assertTrue( o2m.isVector(
									"",
									"" ) );
	}
	

	@Test
	public
			void
			testAll()
					throws DatabaseException
	{
		
		this.testEnsureGroup();
		this.testSame();
		this.testEmpty();
	}
}
