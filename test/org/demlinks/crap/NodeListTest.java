
package org.demlinks.crap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NodeListTest {
	
	Node		node1, node2, node3, nullNode;
	NodeList	list;
	
	@Before
	public void init() {
		this.node1 = new Node();
		this.node2 = new Node();
		this.node3 = new Node();
		this.nullNode = null;
		this.list = new NodeList();
	}
	
	@Test
	public void testAppendNode() {
		assertTrue( this.list.isEmpty() );
		assertTrue( this.list.getFirstNode() == null );
		assertTrue( this.list.getLastNode() == null );
		assertFalse( this.list.hasNode( this.node1 ) );
		assertFalse( this.list.hasNode( this.node2 ) );
		assertFalse( this.list.appendNode( this.node1 ) ); // false= node didn't
															// already exist
		assertTrue( this.node1 == this.list.getLastNode() );
		assertFalse( this.list.appendNode( this.node2 ) );
		assertTrue( this.node2 == this.list.getLastNode() );
		assertTrue( this.list.appendNode( this.node1 ) );// already there
		assertFalse( this.node1 == this.list.getLastNode() );// it wasn't moved
																// last
		assertTrue( 2 == this.list.size() );
		boolean excepted = false;
		try {
			this.list.appendNode( this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		assertTrue( this.node2 == this.list.getNodeAfter( this.node1 ) );
		assertTrue( this.node1 == this.list.getNodeBefore( this.node2 ) );
		assertTrue( this.list.getNodeAfter( this.node2 ) == null );
		assertTrue( this.list.getNodeBefore( this.node1 ) == null );
		excepted = false;
		try {
			this.list.hasNode( this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		assertTrue( this.list.hasNode( this.node1 ) );
		assertTrue( this.list.hasNode( this.node2 ) );
	}
	
	@Test
	public void testInsertNode() {
		assertTrue( this.list.isEmpty() );
		assertFalse( this.list.insertNode( this.node1, Position.FIRST ) );
		assertFalse( this.list.insertNode( this.node2, Position.LAST ) );
		assertTrue( this.list.getFirstNode() == this.node1 );
		assertTrue( this.list.getLastNode() == this.node2 );
		assertTrue( this.list.size() == 2 );
		boolean excepted = false;
		try {
			this.list.insertNode( this.nullNode, Position.FIRST );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertNode( this.node1, null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertNode( null, null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
	
	@Test
	public void testInsertAfterNode() {
		assertTrue( this.list.isEmpty() );
		assertFalse( this.list.appendNode( this.node1 ) );
		assertFalse( this.list.insertAfterNode( this.node2, this.node1 ) );
		assertTrue( this.list.getLastNode() == this.node2 );
		assertFalse( this.list.insertAfterNode( this.node3, this.node2 ) );
		assertTrue( this.list.getLastNode() == this.node3 );
		assertTrue( this.list.size() == 3 );
		assertTrue( this.list.removeNode( this.node2 ) );
		assertFalse( this.list.removeNode( this.node2 ) );
		assertTrue( this.list.size() == 2 );
		assertTrue( this.list.hasNode( this.node1 ) );
		assertTrue( this.list.hasNode( this.node3 ) );
		assertFalse( this.list.insertBeforeNode( this.node2, this.node3 ) );
		assertTrue( this.list.getNodeAfter( this.node1 ) == this.list
				.getNodeBefore( this.node3 ) );
		assertTrue( this.list.getNodeAfter( this.node1 ) == this.node2 );
		boolean excepted = false;
		try {
			this.list.insertAfterNode( this.nullNode, this.node2 );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertAfterNode( this.node1, this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertAfterNode( this.nullNode, this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertBeforeNode( this.nullNode, this.node2 );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertBeforeNode( this.node1, this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.insertBeforeNode( this.nullNode, this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.list.removeNode( this.nullNode );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
}
