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



package org.dml.level030;



import static org.junit.Assert.*;

import org.dml.error.*;
import org.dml.level010.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.method.*;



/**
 * 
 *
 */
public class Level030_DMLEnvironmentTest {
	
	Level030_DMLEnvironment	l3;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		final MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// l3 = new Level030_DMLEnvironment();
		// l3.init( params );
		l3 = Factory.getNewInstanceAndInit( Level030_DMLEnvironment.class, params );
		// params.deInit();
		// Factory.deInit( params );
	}
	
	
	@After
	public void tearDown() {
		
		if ( null != l3 ) {
			Factory.deInitIfAlreadyInited( l3 );
		}
		// l3.deInitSilently();
	}
	
	
	@Test
	public void testPointer() {
		
		final JavaID name = JavaID.ensureJavaIDFor( "Ptr1" );
		final Symbol name2 = l3.createSymbol( name );
		
		final Pointer p1 = l3.getExistingPointer( name2, true );
		// l3.associateJavaIDWithSymbol( name, p1.getAsSymbol() );
		p1.assumedValid();
		assertNull( p1.getPointee() );
		
		final Pointer p2 = l3.getNewNullPointer();// allowed to point to nothing
		assertNull( p2.getPointee() );
		
		// can point to nothing
		final Pointer p1_1 = l3.getExistingPointer( name2, true );
		assertNull( p1_1.getPointee() );
		final Symbol uni1 = l3.newUniqueSymbol();
		assertNull( p1.pointTo( uni1 ) );
		assertTrue( p1_1.getPointee() == uni1 );
		assertTrue( p1.getPointee() == uni1 );
		assertTrue( p1_1.pointTo( null ) == uni1 );
		
		final Symbol pointsTo = l3.newUniqueSymbol();
		final Pointer p3 = l3.getNewNonNullPointer( pointsTo );
		// must already point to something, which it does
		final Pointer p3_3 = l3.getExistingPointer( p3.getAsSymbol(), false );
		assertTrue( p3 == p3_3 );
		assertTrue( p3_3.getPointee() == pointsTo );
		assertTrue( p3.getPointee() == pointsTo );
		boolean threw = false;
		try {
			p3.pointTo( null );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		// allow null
		final Pointer p4 = l3.getNewNullPointer();
		assertTrue( p4 != p3 );
		assertTrue( p4.getPointee() == null );
		assertTrue( p4.pointTo( null ) == null );
		threw = false;
		try {
			p3.assumedValid();
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertFalse( threw );
		
		threw = false;
		try {
			p3.getPointee();
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertFalse( threw );
		
		threw = false;
		try {
			p3.pointTo( pointsTo );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertFalse( threw );
		
		threw = false;
		try {
			p3.getAsSymbol();
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertFalse( threw );
	}
	
	
	@Test
	public void testDomainPointer() {
		
		final Symbol domain = l3.ensureSymbol( JavaID.ensureJavaIDFor( "domain" ) );
		final Symbol pointTo = l3.newUniqueSymbol();
		assertFalse( l3.ensureVector( domain, pointTo ) );
		final DomainPointer dp1 = l3.getNewNonNullDomainPointer( domain, pointTo );
		final DomainPointer dp1_1 = l3.getExistingDomainPointer( dp1.getAsSymbol(), domain, false );
		
		boolean must = false;
		try {
			// existing with different domain this time
			@SuppressWarnings( "unused" )
			final DomainPointer diffDom = l3.getExistingDomainPointer( dp1.getAsSymbol(), pointTo, false );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				must = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( must );
		
		must = false;
		try {
			// existing with different domain this time
			@SuppressWarnings( "unused" )
			final DomainPointer diffDom = l3.getExistingDomainPointer( dp1.getAsSymbol(), domain, true );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				must = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( must );
		
		assertTrue( dp1_1.getPointee() == pointTo );
		assertTrue( dp1_1.getDomain() == domain );
		assertTrue( dp1 == dp1_1 );
		
		final DomainPointer dp2 = l3.getNewNullDomainPointer( domain );
		final DomainPointer dp2_2 = l3.getExistingDomainPointer( dp2.getAsSymbol(), domain, true );
		assertTrue( dp2.getDomain() == domain );
		assertTrue( dp2_2.getDomain() == dp2.getDomain() );
		assertTrue( dp2 == dp2_2 );
		boolean threw = false;
		try {
			dp2.setDomain( dp2.getAsSymbol() );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, AssumptionError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		// TODO more tests
	}
}
