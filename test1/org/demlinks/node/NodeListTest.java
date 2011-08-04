/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
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


package org.demlinks.node;



import static org.junit.Assert.*;

import org.junit.*;
import org.q.*;



public class NodeListTest {
	
	Node		node1, node2, node3, nullNode;
	NodeList	list;
	
	
	@Before
	public void init() {
		
		node1 = new Node();
		node2 = new Node();
		node3 = new Node();
		nullNode = null;
		list = new NodeList();
	}
	
	
	@Test
	public void testAppendNode() {
		
		assertTrue( list.isEmpty() );
		assertTrue( list.getFirstNode() == null );
		assertTrue( list.getLastNode() == null );
		assertFalse( list.hasNode( node1 ) );
		assertFalse( list.hasNode( node2 ) );
		assertFalse( list.appendNode( node1 ) ); // false= node didn't
		// already exist
		assertTrue( node1 == list.getLastNode() );
		assertFalse( list.appendNode( node2 ) );
		assertTrue( node2 == list.getLastNode() );
		assertTrue( list.appendNode( node1 ) );// already there
		assertFalse( node1 == list.getLastNode() );// it wasn't moved
		// last
		assertTrue( 2 == list.size() );
		try {
			list.appendNode( nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		assertTrue( node2 == list.getNodeAfter( node1 ) );
		assertTrue( node1 == list.getNodeBefore( node2 ) );
		assertTrue( list.getNodeAfter( node2 ) == null );
		assertTrue( list.getNodeBefore( node1 ) == null );
		try {
			list.hasNode( nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		assertTrue( list.hasNode( node1 ) );
		assertTrue( list.hasNode( node2 ) );
	}
	
	
	@Test
	public void testInsertNode() {
		
		assertTrue( list.isEmpty() );
		assertFalse( list.insertNode( node1, Position.FIRST ) );
		assertFalse( list.insertNode( node2, Position.LAST ) );
		assertTrue( list.getFirstNode() == node1 );
		assertTrue( list.getLastNode() == node2 );
		assertTrue( list.size() == 2 );
		try {
			list.insertNode( nullNode, Position.FIRST );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertNode( node1, null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertNode( null, null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
	}
	
	
	@Test
	public void testInsertAfterNode() {
		
		assertTrue( list.isEmpty() );
		assertFalse( list.appendNode( node1 ) );
		assertFalse( list.insertAfterNode( node2, node1 ) );
		assertTrue( list.getLastNode() == node2 );
		assertFalse( list.insertAfterNode( node3, node2 ) );
		assertTrue( list.getLastNode() == node3 );
		assertTrue( list.size() == 3 );
		assertTrue( list.removeNode( node2 ) );
		assertFalse( list.removeNode( node2 ) );
		assertTrue( list.size() == 2 );
		assertTrue( list.hasNode( node1 ) );
		assertTrue( list.hasNode( node3 ) );
		assertFalse( list.insertBeforeNode( node2, node3 ) );
		assertTrue( list.getNodeAfter( node1 ) == list.getNodeBefore( node3 ) );
		assertTrue( list.getNodeAfter( node1 ) == node2 );
		try {
			list.insertAfterNode( nullNode, node2 );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertAfterNode( node1, nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertAfterNode( nullNode, nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertBeforeNode( nullNode, node2 );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertBeforeNode( node1, nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.insertBeforeNode( nullNode, nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			list.removeNode( nullNode );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
	}
}
