/**
 * File creation: May 31, 2009 3:42:17 PM
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


package org.dml.database;



import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level1.OneToOneDBMap;
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
public class OneToOneDBMapTest {
	
	OneToOneDBMap				x;
	final String				_a	= "AAAAAAAAAAAAAAAAAAA";
	final String				_b	= "BBBBBBBBBBBBBBBBBBBBBBBBB";
	Level1_Storage_BerkeleyDB	bdb;
	
	@Before
	public void setUp() throws DatabaseException {

		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.wipeDB, true );
		bdb = new Level1_Storage_BerkeleyDB();
		bdb.init( params );
		params.deInit();
		// bdb = new Level1_Storage_BerkeleyDB( Consts.BDB_ENV_PATH, true );
		x = new OneToOneDBMap( bdb, "someMap" );
	}
	
	@After
	public void tearDown() {

		x = x.silentClose();
		assertTrue( null == x );
		bdb.deInit();
	}
	
	
	@Test
	public void linkTest() throws DatabaseException,
			UnsupportedEncodingException {

		
		System.out.println( x.link( _a, _b ) );
		assertTrue( x.getKey( _b ).equals( _a ) );
		assertTrue( x.getData( _a ).equals( _b ) );
		// different objects, same content
		assertTrue( _a != x.getKey( _b ) );
		assertTrue( _b != x.getData( _a ) );
		assertTrue( _b.equals( x.getData( x.getKey( _b ) ) ) );
	}
}
