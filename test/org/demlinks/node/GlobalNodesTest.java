
package org.demlinks.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



public class GlobalNodesTest {
	
	@Test
	public void testAllPointers() {
		Node n = new Node();
		Node m = new PointerNode();
		assertFalse( GlobalNodes.isPointer( n ) );
		assertTrue( GlobalNodes.isPointer( m ) );
	}
	
	@Test
	public void testAllRandomNodes() {
		Node a = new RandomNode();
		Node b = new Node();
		assertTrue( GlobalNodes.isRandomNode( a ) );
		assertFalse( GlobalNodes.isRandomNode( b ) );
	}
	
	@Test
	public void testAllNodesWithDupChildren() {
		NodeWithDupChildren a = new NodeWithDupChildren();
		assertTrue( GlobalNodes.isNodeWithDupChildren( a ) );
		PointerNode b = new PointerNode();
		assertFalse( GlobalNodes.isNodeWithDupChildren( b ) );
	}
	
	@Test
	public void testAllIntermediaryNodes() {
		IntermediaryNode a = new IntermediaryNode();
		Node b = new Node();
		PointerNode c = new PointerNode();
		PointerNode d = new IntermediaryNode();
		
		assertTrue( GlobalNodes.isIntermediaryNode( a ) );
		assertTrue( GlobalNodes.isPointer( a ) );
		
		assertFalse( GlobalNodes.isIntermediaryNode( b ) );
		assertFalse( GlobalNodes.isIntermediaryNode( c ) );
		assertTrue( GlobalNodes.isPointer( c ) );
		assertTrue( GlobalNodes.isPointer( d ) );
		assertTrue( GlobalNodes.isIntermediaryNode( d ) );
	}
}
