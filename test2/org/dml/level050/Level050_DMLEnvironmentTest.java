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



package org.dml.level050;



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
public class Level050_DMLEnvironmentTest {
	
	Level050_DMLEnvironment	env;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		final MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// env = new Level050_DMLEnvironment();
		// env.init( params );
		env = Factory.getNewInstanceAndInit( Level050_DMLEnvironment.class, params );
		// params.deInit();
		// Factory.deInit( params );
	}
	
	
	@After
	public void tearDown() {
		
		if ( null != env ) {
			Factory.deInit( env );
		}
		// env.deInitSilently();
	}
	
	
	@Test
	public void test1() {
		
		final JavaID name = JavaID.ensureJavaIDFor( "Node1" );
		final Symbol name2 = env.createSymbol( name );
		
		final Node node = env.getNewNode( name2, false );
		assertTrue( node.getAsSymbol() == name2 );
		
		// can't get new Node with same Symbol
		Node node2;
		boolean threw = false;
		try {
			node2 = env.getNewNode( name2, false );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		// can't use different setting for allowDUPs
		threw = false;
		try {
			node2 = env.getExistingNode( name2, true );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		// got the same instance for the same selfSymbol
		node2 = env.getExistingNode( name2, false );
		final Node node3 = env.getExistingNode( name2 );
		assertTrue( node == node2 );
		assertTrue( node == node3 );
		assertTrue( node3.isDUPAllowedForChildren() == false );
		
		node2.assumedValid();
		assertFalse( node2.isDUPAllowedForChildren() );
		assertTrue( node2.sizeOfChildren() == 0 );
		assertTrue( node2.sizeOfParents() == 0 );
		
		final Symbol new1s = env.newUniqueSymbol();
		final Node new1 = env.getNewNode( new1s, false );
		final Symbol new2s = env.newUniqueSymbol();
		final Node new2 = env.getNewNode( new2s, false );
		final Symbol new3s = env.newUniqueSymbol();
		final Node new3 = env.getNewNode( new3s, false );
		final Symbol new4s = env.newUniqueSymbol();
		final Node new4 = env.getNewNode( new4s, false );
		final Symbol new5s = env.newUniqueSymbol();
		final Node new5 = env.getNewNode( new5s, false );
		
		assertTrue( new2.sizeOfParents() == 0 );
		assertFalse( node2.ensure( NodeType.CHILD, new2 ) );
		assertTrue( node2.sizeOfChildren() == 1 );
		assertTrue( new2.sizeOfChildren() == 0 );
		assertTrue( new2.sizeOfParents() == 1 );
		assertTrue( new2.has( NodeType.PARENT, node2 ) );
		assertTrue( node2.has( NodeType.CHILD, new2 ) );
		
		
		node2.add( NodeType.CHILD, new4, Position.AFTER, new2 );
		assertTrue( new4.sizeOfParents() == 1 );
		assertTrue( node2.sizeOfChildren() == 2 );
		assertTrue( node2.has( NodeType.CHILD, new4 ) );
		assertTrue( new4.has( NodeType.PARENT, node2 ) );
		
		
		
		node2.add( NodeType.CHILD, new5, Position.LAST );
		assertTrue( new5.ensure( NodeType.PARENT, node2 ) );
		
		node2.add( NodeType.CHILD, new1, Position.FIRST );
		node2.add( NodeType.CHILD, new3, Position.BEFORE, new4 );
		assertTrue( node2.sizeOfChildren() == 5 );
		
		assertTrue( node2.get( NodeType.CHILD, Position.FIRST ) == new1 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new1 ) == new2 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new2 ) == new3 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new3 ) == new4 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new4 ) == new5 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new5 ) == null );
		assertTrue( node2.get( NodeType.CHILD, Position.BEFORE, new1 ) == null );
		assertTrue( new1.sizeOfChildren() == 0 );
		assertTrue( new1.sizeOfParents() == 1 );
		assertTrue( new2.sizeOfParents() == 1 );
		assertTrue( new3.sizeOfParents() == 1 );
		assertTrue( new4.sizeOfParents() == 1 );
		assertTrue( new5.sizeOfParents() == 1 );
		assertTrue( new1.has( NodeType.PARENT, node2 ) );
		assertTrue( new2.has( NodeType.PARENT, node2 ) );
		assertTrue( new3.has( NodeType.PARENT, node2 ) );
		assertTrue( new4.has( NodeType.PARENT, node2 ) );
		assertTrue( new5.has( NodeType.PARENT, node2 ) );
		
		assertTrue( node2.remove( NodeType.CHILD, Position.AFTER, new3 ) == new4 );
		assertFalse( node2.has( NodeType.CHILD, new4 ) );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new3 ) == new5 );
		assertTrue( node2.get( NodeType.CHILD, Position.BEFORE, new5 ) == new3 );
		assertTrue( node2.remove( NodeType.CHILD, Position.FIRST ) == new1 );
		assertTrue( node2.get( NodeType.CHILD, Position.BEFORE, new2 ) == null );
		assertTrue( node2.get( NodeType.CHILD, Position.FIRST ) == new2 );
		assertTrue( node2.remove( NodeType.CHILD, Position.LAST ) == new5 );
		assertTrue( node2.get( NodeType.CHILD, Position.LAST ) == new3 );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new3 ) == null );
		assertTrue( node2.remove( NodeType.CHILD, new3 ) );
		assertFalse( node2.remove( NodeType.CHILD, new3 ) );
		assertTrue( node2.get( NodeType.CHILD, Position.AFTER, new2 ) == null );
		assertTrue( node2.get( NodeType.CHILD, Position.BEFORE, new2 ) == null );
		assertTrue( node2.sizeOfChildren() == 1 );
		assertTrue( node2.remove( NodeType.CHILD, Position.LAST ) == new2 );
		assertTrue( node2.remove( NodeType.CHILD, Position.LAST ) == null );
		assertTrue( node2.sizeOfChildren() == 0 );
		assertTrue( new1.sizeOfParents() == 0 );
		assertTrue( new2.sizeOfParents() == 0 );
		assertTrue( new3.sizeOfParents() == 0 );
		assertTrue( new4.sizeOfParents() == 0 );
		assertTrue( new5.sizeOfParents() == 0 );
	}
}
