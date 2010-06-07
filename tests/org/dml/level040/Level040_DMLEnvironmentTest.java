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



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.error.BadCallError;
import org.dml.level010.JavaID;
import org.dml.level010.Symbol;
import org.dml.tools.RunTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.Position;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Level040_DMLEnvironmentTest {
	
	Level040_DMLEnvironment	l4;
	
	@Before
	public void setUp() {

		MethodParams params = new MethodParams();
		params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		l4 = new Level040_DMLEnvironment();
		l4.init( params );
		params.deInit();
	}
	
	@After
	public void tearDown() {

		l4.deInitSilently();
	}
	
	@Test
	public void test1() {

		JavaID name = JavaID.ensureJavaIDFor( "booList" );
		Symbol name2 = l4.createSymbol( name );
		
		ListOrderedOfSymbols list = l4.getNewListOOS( name2, false, false );
		// RunTime.assumedFalse( l4.ensureLink( list.getAsSymbol(), name ) );
		
		this.test1_1( list );
		Symbol name3 = l4.createSymbol( JavaID.ensureJavaIDFor( "WWFlist" ) );
		ListOrderedOfSymbolsWithFastFind list2 = l4.getNewListOOSWFF( name3, false );
		this.test1_1( list2 );
	}
	
	private void test1_1( OrderedList list ) {

		list.assumedValid();
		
		assertNull( list.get( Position.FIRST ) );
		assertNull( list.get( Position.LAST ) );
		assertTrue( list.size() == 0 );
		
		Symbol e1 = l4.newUniqueSymbol();
		list.add( e1, Position.LAST );
		assertTrue( list.get( Position.LAST ) == e1 );
		assertTrue( list.get( Position.FIRST ) == e1 );
		
		Symbol e2 = l4.newUniqueSymbol();
		list.add( e2, Position.BEFORE, e1 );
		assertTrue( list.get( Position.FIRST ) == e2 );
		assertTrue( list.get( Position.LAST ) == e1 );
		assertTrue( list.get( Position.AFTER, e2 ) == e1 );
		assertNull( list.get( Position.AFTER, e1 ) );
		assertTrue( list.get( Position.BEFORE, e1 ) == e2 );
		assertNull( list.get( Position.BEFORE, e2 ) );
		assertTrue( list.size() == 2 );
		list.assumedValid();
		
		Symbol e3 = l4.newUniqueSymbol();
		list.add( e3, Position.AFTER, e1 );
		assertTrue( list.get( Position.LAST ) == e3 );
		assertTrue( list.size() == 3 );
		
		Symbol e4 = l4.newUniqueSymbol();
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
		} catch ( BadCallError bce ) {
			threw = true;
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
		Symbol removedOne = list.remove( Position.AFTER, e1 );
		
		System.out.println( l4.getJavaID( removedOne ) );
		RunTime.assumedNotNull( removedOne );
		RunTime.assumedTrue( list.getAsSymbol() == removedOne );
		RunTime.assumedTrue( list.size() == 4 );
	}
	
	@Test
	public void test2() {

		Symbol name = l4.newUniqueSymbol();
		ListOrderedOfElementCapsules eclist = ListOrderedOfElementCapsules.getListOOEC( l4, name );
		eclist.assumedValid();
		
		Symbol ec1name = l4.newUniqueSymbol();
		ElementCapsule ec1 = ElementCapsule.getNewEmptyElementCapsule( l4, ec1name );
		ec1.setElement( l4.newUniqueSymbol() );
		eclist.add_ElementCapsule( Position.FIRST, ec1 );
		assertTrue( l4.allHeads_Set.hasSymbol( ec1.getAsSymbol() ) );
		assertTrue( l4.isVector( eclist.getAsSymbol(), ec1.getAsSymbol() ) );
		ElementCapsule tmpLast = eclist.get_ElementCapsule( Position.LAST );
		assertNotNull( tmpLast );
		assertTrue( tmpLast == ec1 );
		assertTrue( eclist.get_ElementCapsule( Position.FIRST ) == ec1 );
	}
	
	@Test
	public void testAllowNull() {

		Symbol name = l4.newUniqueSymbol();
		ListOrderedOfSymbols list = l4.getNewListOOS( name, true, false );
		list.assumedValid();
		Symbol e1 = l4.newUniqueSymbol();
		RunTime.assumedTrue( list.isNullAllowed() );
		list.add( null, Position.LAST );
		boolean threw = false;
		try {
			list.add( null, Position.LAST );
		} catch ( BadCallError bce ) {
			threw = true;
		}
		assertTrue( threw );
		
		RunTime.assumedTrue( 1 == list.size() );
		list.add( e1, Position.LAST );
		RunTime.assumedTrue( 2 == list.size() );
		
		threw = false;
		try {
			list.add( null, Position.LAST );
		} catch ( BadCallError bce ) {
			threw = true;
		}
		assertTrue( threw );
		RunTime.assumedTrue( list.isNullAllowed() );
		RunTime.assumedTrue( 2 == list.size() );
		list.checkIntegrity();
		
		threw = false;
		try {
			@SuppressWarnings( "unused" )
			ListOrderedOfSymbols list2 = l4.getNewListOOS( name, false, false );
		} catch ( BadCallError bce ) {
			threw = true;
		}
		assertTrue( threw );
	}
}
