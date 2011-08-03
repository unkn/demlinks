/**
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
 * 
 * 
 * File creation: Sep 9, 2010 3:08:34 PM
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
public class SymbolTest {
	
	Level1_Storage_BerkeleyDB	b1	= null, b2 = null;
	
	
	@After
	public void tearDown() {
		// RunTime.clearThrowChain(); maybe this will clear all prev throws if any, and eclipse won't get them
		if ( null != b2 ) {
			Factory.deInitIfInited_WithPostponedThrows( b2 );
		}
		if ( null != b1 ) {
			Factory.deInitIfInited_WithPostponedThrows( b1 );
		}
		RunTime.throwAllThatWerePostponed();
	}
	
	
	@SuppressWarnings( "boxing" )
	@Test
	public void test() {
		b1 = new Level1_Storage_BerkeleyDB();
		final long l1 = 1290312l;
		final TheStoredSymbol tsSym = TheStoredSymbol.getNew( new Long( l1 ) );
		Symbol a = null;
		boolean threw = false;
		try {
			a = Symbol.getNew( b1, tsSym );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		final MethodParams params = MethodParams.getNew();
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		Factory.init( b1, params );
		
		assertTrue( b1.isInitedSuccessfully() );
		
		a = Symbol.getNew( b1, tsSym );
		final Symbol b = Symbol.getNew( b1, tsSym );
		assertNotNull( a );
		assertNotNull( b );
		assertTrue( a == b );// same instance
		assertNotNull( params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH + File.separator + "second" ) );
		b2 = Factory.getNewInstanceAndInit( Level1_Storage_BerkeleyDB.class, params );
		final Symbol c = Symbol.getNew( b2, tsSym );
		assertTrue( b != c );// different instances due to different bdb-s
		
		Symbol.junitClearCache();// ie. due to too many cached Symbols some were deleted from cache
		final Symbol aa = Symbol.getNew( b1, tsSym );
		assertNotNull( aa );
		assertTrue( a != aa );// different instances
		assertTrue( a.equals( aa ) );// but same contents
		assertTrue( aa.equals( a ) );
		
		final Symbol bb = Symbol.getNew( b1, tsSym );
		assertNotNull( bb );
		assertTrue( b != bb );// diff instances due to not being previously cached
		assertTrue( b.equals( bb ) );// same contents
		assertTrue( bb.equals( b ) );
		
		final Symbol cc = Symbol.getNew( b2, tsSym );
		assertNotNull( cc );
		assertTrue( c != cc );
		assertTrue( c.equals( cc ) );
		assertTrue( cc.equals( c ) );
		
		final HashSet<Symbol> hs = new HashSet<Symbol>();
		assertTrue( hs.size() == 0 );
		assertTrue( hs.add( a ) );
		assertTrue( hs.size() == 1 );
		assertFalse( hs.add( aa ) );
		assertTrue( hs.size() == 1 );
		assertFalse( hs.add( b ) );
		assertTrue( hs.size() == 1 );
		assertFalse( hs.add( bb ) );
		assertTrue( hs.size() == 1 );
		
		assertTrue( hs.add( c ) );
		assertTrue( hs.size() == 2 );
		assertFalse( hs.add( cc ) );
		assertTrue( hs.size() == 2 );
		Factory.deInit( b2 );
		Factory.deInit( b1 );
	}
}
