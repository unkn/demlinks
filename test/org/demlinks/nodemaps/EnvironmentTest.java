
package org.demlinks.nodemaps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.node.Node;
import org.demlinks.nodemaps.Environment;
import org.demlinks.nodemaps.IntermediaryNode;
import org.demlinks.nodemaps.NodeWithDupChildren;
import org.demlinks.nodemaps.PointerNode;
import org.demlinks.nodemaps.RandomNode;
import org.junit.Test;



public class EnvironmentTest {
	
	@Test
	public void testAllPointers() {
		Node n = new Node();
		Node m = new PointerNode();
		assertFalse( Environment.isPointer( n ) );
		assertTrue( Environment.isPointer( m ) );
	}
	
	@Test
	public void testAllRandomNodes() {
		Node a = new RandomNode();
		Node b = new Node();
		assertTrue( Environment.isRandomNode( a ) );
		assertFalse( Environment.isRandomNode( b ) );
	}
	
	@Test
	public void testAllNodesWithDupChildren() {
		NodeWithDupChildren a = new NodeWithDupChildren();
		assertTrue( Environment.isNodeWithDupChildren( a ) );
		PointerNode b = new PointerNode();
		assertFalse( Environment.isNodeWithDupChildren( b ) );
	}
	
	@Test
	public void testAllIntermediaryNodes() {
		IntermediaryNode a = new IntermediaryNode();
		Node b = new Node();
		PointerNode c = new PointerNode();
		PointerNode d = new IntermediaryNode();
		
		assertTrue( Environment.isIntermediaryNode( a ) );
		assertTrue( Environment.isPointer( a ) );
		
		assertFalse( Environment.isIntermediaryNode( b ) );
		assertFalse( Environment.isIntermediaryNode( c ) );
		assertTrue( Environment.isPointer( c ) );
		assertTrue( Environment.isPointer( d ) );
		assertTrue( Environment.isIntermediaryNode( d ) );
	}
}
