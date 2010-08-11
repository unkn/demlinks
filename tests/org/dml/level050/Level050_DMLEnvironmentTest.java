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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dml.error.BadCallError;
import org.dml.level010.JavaID;
import org.dml.level010.Symbol;
import org.dml.tracking.Factory;
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
public class Level050_DMLEnvironmentTest {
	
	Level050_DMLEnvironment	env;
	
	@Before
	public void setUp() {

		MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// env = new Level050_DMLEnvironment();
		// env.init( params );
		env = Factory.getNewInstanceAndInit( Level050_DMLEnvironment.class, params );
		// params.deInit();
		Factory.deInit( params );
	}
	
	@After
	public void tearDown() {

		Factory.deInit( env );
		// env.deInitSilently();
	}
	
	@Test
	public void test1() {

		JavaID name = JavaID.ensureJavaIDFor( "Node1" );
		Symbol name2 = env.createSymbol( name );
		
		Node node = env.getNewNode( name2, false );
		assertTrue( node.getAsSymbol() == name2 );
		
		// can't get new Node with same Symbol
		Node node2;
		boolean threw = false;
		try {
			node2 = env.getNewNode( name2, false );
		} catch ( BadCallError bce ) {
			threw = true;
		}
		assertTrue( threw );
		
		// can't use different setting for allowDUPs
		threw = false;
		try {
			node2 = env.getExistingNode( name2, true );
		} catch ( BadCallError bce ) {
			threw = true;
		}
		assertTrue( threw );
		
		// got the same instance for the same selfSymbol
		node2 = env.getExistingNode( name2, false );
		Node node3 = env.getExistingNode( name2 );
		assertTrue( node == node2 );
		assertTrue( node == node3 );
		assertTrue( node3.isDUPAllowedForChildren() == false );
		
		node2.assumedValid();
		assertFalse( node2.isDUPAllowedForChildren() );
		assertTrue( node2.sizeOfChildren() == 0 );
		assertTrue( node2.sizeOfParents() == 0 );
		
		Symbol new1s = env.newUniqueSymbol();
		Node new1 = env.getNewNode( new1s, false );
		Symbol new2s = env.newUniqueSymbol();
		Node new2 = env.getNewNode( new2s, false );
		Symbol new3s = env.newUniqueSymbol();
		Node new3 = env.getNewNode( new3s, false );
		Symbol new4s = env.newUniqueSymbol();
		Node new4 = env.getNewNode( new4s, false );
		Symbol new5s = env.newUniqueSymbol();
		Node new5 = env.getNewNode( new5s, false );
		
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
