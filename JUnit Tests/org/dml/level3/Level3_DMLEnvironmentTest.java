/**
 * File creation: Nov 16, 2009 11:54:21 PM
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


package org.dml.level3;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.level1.JavaID;
import org.dml.level1.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Level3_DMLEnvironmentTest {
	
	Level3_DMLEnvironment	l3;
	
	@Before
	public void setUp() {

		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		l3 = new Level3_DMLEnvironment();
		l3.init( params );
		params.deInit();
	}
	
	@After
	public void tearDown() {

		l3.deInitSilently();
	}
	
	@Test
	public void testPointer() {

		JavaID name = JavaID.ensureJavaIDFor( "Ptr1" );
		Symbol name2 = l3.createSymbol( name );
		// TODO
		
		Pointer p1 = l3.getExistingPointer( name2, true );
		// l3.associateJavaIDWithSymbol( name, p1.getAsSymbol() );
		p1.assumedValid();
		assertNull( p1.getPointee() );
		
		Pointer p2 = l3.getNewNullPointer();// allowed to point to nothing
		assertNull( p2.getPointee() );
		
		// can point to nothing
		Pointer p1_1 = l3.getExistingPointer( name2, true );
		assertNull( p1_1.getPointee() );
		Symbol uni1 = l3.newUniqueSymbol();
		assertNull( p1.pointTo( uni1 ) );
		assertTrue( p1_1.getPointee() == uni1 );
		assertTrue( p1.getPointee() == uni1 );
		assertTrue( p1_1.pointTo( null ) == uni1 );
		
		Symbol pointsTo = l3.newUniqueSymbol();
		Pointer p3 = l3.getNewNonNullPointer( pointsTo );
		// must already point to something, which it does
		Pointer p3_3 = l3.getExistingPointer( p3.getAsSymbol(), false );
		assertTrue( p3 != p3_3 );
		assertTrue( p3_3.getPointee() == pointsTo );
		assertTrue( p3.getPointee() == pointsTo );
		boolean threw = false;
		try {
			p3.pointTo( null );
		} catch ( AssertionError ae ) {
			threw = true;
		}
		assertTrue( threw );
		
		// allow null
		Pointer p4 = l3.getExistingPointer( p3.getAsSymbol(), true );
		assertTrue( p4 != p3 );
		assertTrue( p4.getPointee() == pointsTo );
		assertTrue( p4.pointTo( null ) == pointsTo );
		threw = false;
		try {
			p3.assumedValid();
		} catch ( AssertionError ae ) {
			threw = true;
		}
		assertTrue( threw );
		
		threw = false;
		try {
			p3.getPointee();
		} catch ( AssertionError ae ) {
			threw = true;
		}
		assertTrue( threw );
		
		threw = false;
		try {
			p3.pointTo( pointsTo );
		} catch ( AssertionError ae ) {
			threw = true;
		}
		assertTrue( threw );
		
		threw = false;
		try {
			p3.getAsSymbol();
		} catch ( AssertionError ae ) {
			threw = true;
		}
		assertTrue( threw );
	}
	
	@Test
	public void testDomainPointer() {

		Symbol domain = l3.ensureSymbol( JavaID.ensureJavaIDFor( "domain" ) );
		Symbol pointTo = l3.newUniqueSymbol();
		assertFalse( l3.ensureVector( domain, pointTo ) );
		DomainPointer dp1 = l3.getNewNonNullDomainPointer( domain, pointTo );
		DomainPointer dp1_1 = l3.getExistingDomainPointer( dp1.getAsSymbol(),
				domain );
		assertTrue( dp1_1.getPointee() == pointTo );
		assertTrue( dp1_1.getDomain() == domain );
		assertTrue( dp1 != dp1_1 );
		
		DomainPointer dp2 = l3.getNewNullDomainPointer( domain );
		DomainPointer dp2_2 = l3.getExistingDomainPointer( dp2.getAsSymbol(),
				domain );
		assertTrue( dp2.getDomain() == domain );
		assertTrue( dp2_2.getDomain() == dp2.getDomain() );
		
	}
}
