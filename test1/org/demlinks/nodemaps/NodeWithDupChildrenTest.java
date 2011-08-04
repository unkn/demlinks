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


package org.demlinks.nodemaps;



import static org.junit.Assert.*;

import org.demlinks.constants.*;
import org.demlinks.node.*;
import org.junit.*;
import org.q.*;



public class NodeWithDupChildrenTest {
	
	Node				normalNode1, normalNode2, normalNode3;
	NodeWithDupChildren	nodeWithDups;
	
	
	@Before
	public void init() {
		
		nodeWithDups = new NodeWithDupChildren();
		normalNode1 = new Node();
		normalNode2 = new Node();
		normalNode3 = new Node();
	}
	
	
	@Test
	public void testOne() {
		
		assertTrue( Environment.isNodeWithDupChildren( nodeWithDups ) );
		assertFalse( Environment.isIntermediaryNode( normalNode1 ) );
		
		nodeWithDups.dupAppendChild( normalNode1 );
		// Node c = null;// will be filled by Solve
		// if ( b.Solve( a, Sense.Child, c, Sense.Child, b ) ) {// a->c->b, is
		// c?
		// // there was a solution
		// // we might need SolveNext, kind of, in case more than 1 solution
		// assertTrue( Environment.isPointer( c ) );
		// }
		//
		assertTrue( nodeWithDups.dupHasChild( normalNode1 ) );
		final IntermediaryNode i = nodeWithDups.getIntermediaryForFirstChild( normalNode1 );
		validateIntermediary( i );
		
		// only one normalNode1 occurrence in list
		assertTrue( i == nodeWithDups.getIntermediaryForLastChild( normalNode1 ) );
		
		assertTrue( i == nodeWithDups.getIntermediaryForFirstChild() );
		assertTrue( i.getPointee() == normalNode1 );
		
		
		// adding the same node again, now there's two
		nodeWithDups.dupAppendChild( normalNode1 );
		
		final IntermediaryNode i2 = nodeWithDups.getIntermediaryForNextChild( normalNode1, i, DO.SKIP );
		// continue from "i" as last intermediary found
		
		validateIntermediary( i2 );
		assertTrue( i2 != i );
		assertTrue( i2 == nodeWithDups.getNextIntermediary( i ) );
		assertTrue( i2 == nodeWithDups.getIntermediaryForLastChild() );
		
		assertTrue( nodeWithDups.getCountOfChildren( normalNode2 ) == 0 );
		assertTrue( nodeWithDups.getCountOfChildren( normalNode3 ) == 0 );
		nodeWithDups.dupAppendChild( normalNode2 );
		nodeWithDups.dupAppendChild( normalNode3 );
		
		final IntermediaryNode i3 = nodeWithDups.getNextIntermediary( i2 );
		validateIntermediary( i3 );
		
		final IntermediaryNode i4 = nodeWithDups.getNextIntermediary( i3 );
		validateIntermediary( i4 );
		
		assertTrue( 2 == nodeWithDups.getCountOfChildren( normalNode1 ) );
		assertTrue( nodeWithDups.getCountOfChildren( normalNode2 ) == 1 );
		assertTrue( nodeWithDups.getCountOfChildren( normalNode3 ) == 1 );
	}
	
	
	public void validateIntermediary( final IntermediaryNode i ) {
		
		assertTrue( i != null );
		assertTrue( Environment.isIntermediaryNode( i ) );
		assertTrue( nodeWithDups.hasChild( i ) );
		// assertTrue( i.imGetParent() == this.nodeWithDups );
		assertTrue( i.hasParent( nodeWithDups ) );
	}
	
	
	@Test
	public void testNulls() {
		
		// TODO more ?
		
		try {
			nodeWithDups.getCountOfChildren( null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
	}
}
