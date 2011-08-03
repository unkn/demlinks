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



package org.dml.level040;



import static org.junit.Assert.*;

import org.dml.error.*;
import org.dml.level010.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.*;
import org.references.method.*;



/**
 * 
 *
 */
public class Level040_DMLEnvironmentTest {
	
	Level040_DMLEnvironment	l4;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		final MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// l4 = new Level040_DMLEnvironment();
		// l4.init( params );
		l4 = Factory.getNewInstanceAndInit( Level040_DMLEnvironment.class, params );
		// Factory.deInit( params );
		// params.deInit();
	}
	
	
	@After
	public void tearDown() {
		if ( null != l4 ) {
			Factory.deInit( l4 );
		}
		// l4.deInitSilently();
	}
	
	
	@Test
	public void test1() {
		
		final JavaID name = JavaID.ensureJavaIDFor( "booList" );
		final Symbol name2 = l4.createSymbol( name );
		
		final ListOrderedOfSymbols list = l4.getNewListOOS( name2, false, false );
		// RunTime.assumedFalse( l4.ensureLink( list.getAsSymbol(), name ) );
		
		test1_1( list );
		final Symbol name3 = l4.createSymbol( JavaID.ensureJavaIDFor( "WWFlist" ) );
		final ListOrderedOfSymbolsWithFastFind list2 = l4.getNewListOOSWFF( name3, false );
		test1_1( list2 );
	}
	
	
	private void test1_1( final OrderedList list ) {
		
		list.assumedValid();
		
		assertNull( list.get( Position.FIRST ) );
		assertNull( list.get( Position.LAST ) );
		assertTrue( list.size() == 0 );
		
		final Symbol e1 = l4.newUniqueSymbol();
		list.add( e1, Position.LAST );
		assertTrue( list.get( Position.LAST ) == e1 );
		assertTrue( list.get( Position.FIRST ) == e1 );
		
		final Symbol e2 = l4.newUniqueSymbol();
		list.add( e2, Position.BEFORE, e1 );
		assertTrue( list.get( Position.FIRST ) == e2 );
		assertTrue( list.get( Position.LAST ) == e1 );
		assertTrue( list.get( Position.AFTER, e2 ) == e1 );
		assertNull( list.get( Position.AFTER, e1 ) );
		assertTrue( list.get( Position.BEFORE, e1 ) == e2 );
		assertNull( list.get( Position.BEFORE, e2 ) );
		assertTrue( list.size() == 2 );
		list.assumedValid();
		
		final Symbol e3 = l4.newUniqueSymbol();
		list.add( e3, Position.AFTER, e1 );
		assertTrue( list.get( Position.LAST ) == e3 );
		assertTrue( list.size() == 3 );
		
		final Symbol e4 = l4.newUniqueSymbol();
		list.add( e4, Position.LAST );
		assertTrue( list.get( Position.LAST ) == e4 );
		assertTrue( list.size() == 4 );
		assertTrue( list.get( Position.BEFORE, e4 ) == e3 );
		
		// adding self in the list
		list.add( list.getAsSymbol(), Position.AFTER, e1 );
		assertTrue( list.getAsSymbol() == list.get( Position.BEFORE, e3 ) );
		assertTrue( list.size() == 5 );
		list.assumedValid();
		
		boolean threw = false;
		try {
			list.add( e4, Position.FIRST );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		l4.newLink( e1, JavaID.ensureJavaIDFor( "e1 " + list ) );
		l4.newLink( e2, JavaID.ensureJavaIDFor( "e2 " + list ) );
		l4.newLink( e3, JavaID.ensureJavaIDFor( "e3 " + list ) );
		l4.newLink( e4, JavaID.ensureJavaIDFor( "e4 " + list ) );
		Symbol iter = list.get( Position.FIRST );
		while ( null != iter ) {
			System.out.println( l4.getJavaID( iter ) );
			iter = list.get( Position.AFTER, iter );
		}
		// 2,1,list, 3,4
		assertTrue( list.get( Position.BEFORE, e3 ) == list.get( Position.AFTER, e1 ) );
		assertTrue( list.get( Position.AFTER, e1 ) == list.getAsSymbol() );
		final Symbol removedOne = list.remove( Position.AFTER, e1 );
		
		System.out.println( l4.getJavaID( removedOne ) );
		RunTime.assumedNotNull( removedOne );
		RunTime.assumedTrue( list.getAsSymbol() == removedOne );
		RunTime.assumedTrue( list.size() == 4 );
	}
	
	
	@Test
	public void test2() {
		
		final Symbol name = l4.newUniqueSymbol();
		final ListOrderedOfElementCapsules eclist = ListOrderedOfElementCapsules.getListOOEC( l4, name );
		eclist.assumedValid();
		
		final Symbol ec1name = l4.newUniqueSymbol();
		final ElementCapsule ec1 = ElementCapsule.getNewEmptyElementCapsule( l4, ec1name );
		ec1.setElement( l4.newUniqueSymbol() );
		eclist.add_ElementCapsule( Position.FIRST, ec1 );
		assertTrue( l4.allHeads_Set.hasSymbol( ec1.getAsSymbol() ) );
		assertTrue( l4.isVector( eclist.getAsSymbol(), ec1.getAsSymbol() ) );
		final ElementCapsule tmpLast = eclist.get_ElementCapsule( Position.LAST );
		assertNotNull( tmpLast );
		assertTrue( tmpLast == ec1 );
		assertTrue( eclist.get_ElementCapsule( Position.FIRST ) == ec1 );
	}
	
	
	@Test
	public void testAllowNull() {
		
		final Symbol name = l4.newUniqueSymbol();
		final ListOrderedOfSymbols list = l4.getNewListOOS( name, true, false );
		list.assumedValid();
		final Symbol e1 = l4.newUniqueSymbol();
		RunTime.assumedTrue( list.isNullAllowed() );
		list.add( null, Position.LAST );
		boolean threw = false;
		try {
			list.add( null, Position.LAST );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		RunTime.assumedTrue( 1 == list.size() );
		list.add( e1, Position.LAST );
		RunTime.assumedTrue( 2 == list.size() );
		
		threw = false;
		try {
			list.add( null, Position.LAST );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		RunTime.assumedTrue( list.isNullAllowed() );
		RunTime.assumedTrue( 2 == list.size() );
		list.checkIntegrity();
		
		threw = false;
		try {
			@SuppressWarnings( "unused" )
			final ListOrderedOfSymbols list2 = l4.getNewListOOS( name, false, false );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
	}
}
