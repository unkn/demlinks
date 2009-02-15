
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
}
