/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.node.Node;
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
