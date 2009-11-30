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


package org.dml.level4;



import static org.junit.Assert.assertTrue;

import org.dml.level1.JavaID;
import org.dml.level1.Symbol;
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
public class Level4_DMLEnvironmentTest {
	
	Level4_DMLEnvironment	l4;
	
	@Before
	public void setUp() {

		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		l4 = new Level4_DMLEnvironment();
		l4.init( params );
		params.deInit();
	}
	
	@After
	public void tearDown() {

		l4.deInitSilently();
	}
	
	@Test
	public void test1() {

		JavaID name = JavaID.ensureJavaIDFor( "boo" );
		Symbol name2 = l4.createSymbol( name );
		ListOrderedOfSymbols list = l4.newList( name2 );
		list.assumedValid();
	}
	
	@Test
	public void test2() {

		Symbol name = l4.newUniqueSymbol();
		ListOrderedOfElementCapsules eclist = new ListOrderedOfElementCapsules(
				l4, name );
		eclist.assumedValid();
		
		Symbol ec1name = l4.newUniqueSymbol();
		ElementCapsule ec1 = new ElementCapsule( l4, ec1name );
		eclist.add_ElementCapsule( ec1, Position.FIRST );
		assertTrue( eclist.get_ElementCapsule( Position.LAST ) == ec1 );
		assertTrue( eclist.get_ElementCapsule( Position.FIRST ) == ec1 );
	}
}
