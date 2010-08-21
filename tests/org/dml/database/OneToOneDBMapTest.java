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
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.AllTupleBindings;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level1.OneToOneDBMap;
import org.dml.error.AssumptionError;
import org.dml.error.BadCallError;
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
public class OneToOneDBMapTest
{
	
	OneToOneDBMap<String, String>	x;
	final String					_a	= "AAAAAAAAAAAAAAAAAAA";
	final String					_b	= "BBBBBBBBBBBBBBBBBBBBBBBBB";
	Level1_Storage_BerkeleyDB		bdb;
	
	
	@SuppressWarnings( "unchecked" )
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
		// bdb = new Level1_Storage_BerkeleyDB();
		// bdb.init( params );
		bdb = Factory.getNewInstanceAndInit(
												Level1_Storage_BerkeleyDB.class,
												params );
		// params.deInit();
		// Factory.deInit( params );
		// bdb = new Level1_Storage_BerkeleyDB( Consts.BDB_ENV_PATH, true );
		x = new OneToOneDBMap<String, String>(
												bdb,
												"someMap",
												String.class,
												AllTupleBindings.getBinding( String.class ),
												String.class,
												AllTupleBindings.getBinding( String.class ) );
		// x.init( null );
		// x = Factory.getNewInstanceAndInitWithoutParams( OneToOneDBMap.class, bdb, "someMap", String.class,
		// AllTupleBindings.getBinding( String.class ), String.class, AllTupleBindings.getBinding( String.class ) );
		Factory.initWithoutParams( x );
	}
	

	@After
	public
			void
			tearDown()
	{
		
		Factory.deInitIfAlreadyInited( x );
		Factory.deInitIfAlreadyInited( bdb );
		// Factory.deInitAll();
		// ( x );
		// Factory.deInit( bdb );
		// x.deInit();
		// bdb.deInit();
	}
	

	@Test
	public
			void
			linkTest()
					throws DatabaseException
	{
		

		assertFalse( x.link(
								_a,
								_b ) );
		assertTrue( x.getKey(
								_b ).equals(
												_a ) );
		assertTrue( x.getData(
								_a ).equals(
												_b ) );
		// different objects, same content
		assertTrue( _a != x.getKey( _b ) );
		assertTrue( _b != x.getData( _a ) );
		assertTrue( _b.equals( x.getData( x.getKey( _b ) ) ) );
		assertTrue( x.link(
							_a,
							_b ) );
	}
	

	@Test
	public
			void
			extendedTest()
					throws DatabaseException
	{
		
		// this makes sure those 2 methods are protected to having a parameter
		// that extends the given base class, as the extended class needs to
		// have a new TupleBinding class defined for it, or so
		OneToOneDBMap<JUnit_Base1, JUnit_Base1> map = new OneToOneDBMap<JUnit_Base1, JUnit_Base1>(
																									bdb,
																									"extendsMap",
																									JUnit_Base1.class,
																									AllTupleBindings
																											.getBinding( JUnit_Base1.class ),
																									JUnit_Base1.class,
																									AllTupleBindings
																											.getBinding( JUnit_Base1.class ) );
		JUnit_Ex2 e = new JUnit_Ex2();
		boolean threw = false;
		try
		{
			map.getKey( e );
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
		
		threw = false;
		try
		{
			map.getData( e );
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
	}
	

	@Test
	public
			void
			integrityTest()
					throws DatabaseException
	{
		
		OneToOneDBMap<JUnit_Base1, String> map = new OneToOneDBMap<JUnit_Base1, String>(
																							bdb,
																							"irrelevant",
																							JUnit_Base1.class,
																							AllTupleBindings
																									.getBinding( JUnit_Base1.class ),
																							String.class,
																							AllTupleBindings
																									.getBinding( String.class ) );
		JUnit_Base1 key1 = null;
		JUnit_Ex2 key2 = null;
		String data = null;
		boolean threw = false;
		try
		{
			map.getData( key1 );
		}
		catch ( Throwable t )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																AssumptionError.class ) )
			{
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		threw = false;
		try
		{
			map.getData( key2 );
		}
		catch ( Throwable t )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																AssumptionError.class ) )
			{
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		threw = false;
		try
		{
			map.getKey( data );
		}
		catch ( Throwable t )
		{
			if ( RunTime.isThisWrappedException_of_thisType(
																t,
																AssumptionError.class ) )
			{
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		data = "some";
		key1 = new JUnit_Base1();
		map.getData( key1 );// shouldn't throw!
		
		key2 = new JUnit_Ex2();
		
		threw = false;
		try
		{
			map.getData( key2 );
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
		
		map.link(
					key1,
					data );// shouldn't throw!
		
		key1 = new JUnit_Ex2();
		threw = false;
		try
		{
			map.getData( key1 );
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
		
		threw = false;
		try
		{
			map.link(
						key1,
						data );
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
		

		threw = false;
		try
		{
			map.link(
						key2,
						data );
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
		
	}
}
