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



import static org.junit.Assert.*;

import org.dml.JUnits.*;
import org.dml.database.bdb.level1.*;
import org.dml.database.bdb.level2.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.method.*;

import com.sleepycat.db.*;



/**
 * 
 *
 */
public class OneToManyDBMapTest {
	
	OneToManyDBMap<String, String>	o2m;
	// the following two should be random unique names not already in the dbase
	// or else the tests may fail
	final String					_a	= "A" + new Object();
	final String					_b	= "B" + new Object();
	final String					_c	= "C" + new Object();
	Level1_Storage_BerkeleyDB		bdb;
	
	
	@SuppressWarnings( {
		"unchecked", "boxing"
	} )
	@Before
	public void setUp() {
		
		// bdb = new Level1_Storage_BerkeleyDB();
		final MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// bdb.init( params );
		bdb = Factory.getNewInstanceAndInit( Level1_Storage_BerkeleyDB.class, params );
		// params.deInit();
		// Factory.deInit( params );
		
		// o2m = new OneToManyDBMap<String, String>( bdb, "one to many", String.class,
		// AllTupleBindings.getBinding( String.class ), String.class, AllTupleBindings.getBinding( String.class ) );
		// o2m.init( null );
		params.clear();
		RunTime.assumedNull( params.set( PossibleParams.level1_BDBStorage, bdb ) );
		RunTime.assumedNull( params.set( PossibleParams.dbName, "one to many" ) );
		o2m =
			Factory.getNewInstanceAndInit(
				OneToManyDBMap.class,
				params,
				String.class,
				AllTupleBindings.getBinding( String.class ),
				String.class,
				AllTupleBindings.getBinding( String.class ) );
		// Factory.initWithoutParams( o2m );
	}
	
	
	@After
	public void tearDown() {
		if ( null != o2m ) {
			Factory.deInitIfInited_WithPostponedThrows( o2m );
			o2m = null;
		}
		if ( null != bdb ) {
			Factory.deInitIfInited_WithPostponedThrows( bdb );
			bdb = null;
		}
		RunTime.throwAllThatWerePostponed();
		// o2m.deInit();
		// bdb.deInit();
	}
	
	
	/**
	 * Test method for {@link org.dml.database.bdb.level2.OneToManyDBMap#ensureVector(java.lang.String, java.lang.String)} .
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void testEnsureGroup() throws DatabaseException {
		
		System.out.println( _a );
		System.out.println( _b );
		System.out.println( _c );
		System.out.println( o2m.getDBName() );
		
		assertFalse( o2m.isVector( _a, _b ) );
		assertFalse( o2m.ensureVector( _a, _b ) );
		assertTrue( o2m.isVector( _a, _b ) );
		
		assertTrue( o2m.ensureVector( _a, _b ) );
		
		
		assertFalse( o2m.isVector( _b, _a ) );
		assertFalse( o2m.ensureVector( _b, _a ) );
		assertTrue( o2m.isVector( _b, _a ) );
		
		assertTrue( o2m.ensureVector( _b, _a ) );
		
		
		assertFalse( o2m.isVector( _a, _c ) );
		assertFalse( o2m.ensureVector( _a, _c ) );
		assertTrue( o2m.isVector( _a, _c ) );
		
		assertTrue( o2m.ensureVector( _a, _c ) );
		
		assertFalse( o2m.isVector( _c, _b ) );
		assertFalse( o2m.ensureVector( _c, _b ) );
		assertTrue( o2m.isVector( _c, _b ) );
		
		assertTrue( o2m.ensureVector( _c, _b ) );
		
		BDBVectorIterator<String, String> iter = o2m.getIterator_on_Initials_of( _c );
		assertNotNull( iter );
		try {
			iter.goFirst();
			do {
				System.out.println( iter.now() + " -> _c" );
				assertTrue( iter.now().equals( _a ) );
				assertTrue( iter.now() != _a );
				iter.goNext();
			} while ( null != iter.now() );
		} finally {
			try {
				iter.close();
			} finally {
				iter = null;
			}
		}
		
		iter = o2m.getIterator_on_Terminals_of( _c );
		try {
			iter.goFirst();
			do {
				if ( null != iter ) {
					System.out.println( "_c -> " + iter.now() );
					assertTrue( iter.now().equals( _b ) );
					assertTrue( iter.now() != _b );
				}
				iter.goNext();
			} while ( null != iter.now() );
		} finally {
			try {
				iter.close();
			} finally {
				iter = null;
			}
		}
	}
	
	
	@Test
	public void testSame() throws DatabaseException {
		
		assertFalse( o2m.ensureVector( _a, _a ) );
		assertTrue( o2m.ensureVector( _a, _a ) );
		assertTrue( o2m.isVector( _a, _a ) );
	}
	
	
	@Test
	public void testEmpty() throws DatabaseException {
		
		assertFalse( o2m.ensureVector( "", "" ) );
		assertTrue( o2m.ensureVector( "", "" ) );
		assertTrue( o2m.isVector( "", "" ) );
	}
	
	
	@Test
	public void testAll() throws DatabaseException {
		
		testEnsureGroup();
		testSame();
		testEmpty();
	}
}
