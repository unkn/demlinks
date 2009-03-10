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

import org.demlinks.constants.DO;
import org.demlinks.node.Node;
import org.junit.Before;
import org.junit.Test;



public class NodeWithDupChildrenTest {
	
	Node				normalNode1, normalNode2, normalNode3;
	NodeWithDupChildren	nodeWithDups;
	
	@Before
	public void init() {

		this.nodeWithDups = new NodeWithDupChildren();
		this.normalNode1 = new Node();
		this.normalNode2 = new Node();
		this.normalNode3 = new Node();
	}
	
	@Test
	public void testOne() {

		assertTrue( Environment.isNodeWithDupChildren( this.nodeWithDups ) );
		assertFalse( Environment.isIntermediaryNode( this.normalNode1 ) );
		
		this.nodeWithDups.dupAppendChild( this.normalNode1 );
		// Node c = null;// will be filled by Solve
		// if ( b.Solve( a, Sense.Child, c, Sense.Child, b ) ) {// a->c->b, is
		// c?
		// // there was a solution
		// // we might need SolveNext, kind of, in case more than 1 solution
		// assertTrue( Environment.isPointer( c ) );
		// }
		//		 
		assertTrue( this.nodeWithDups.dupHasChild( this.normalNode1 ) );
		IntermediaryNode i = this.nodeWithDups.getIntermediaryForFirstChild( this.normalNode1 );
		this.validateIntermediary( i );
		
		// only one normalNode1 occurrence in list
		assertTrue( i == this.nodeWithDups.getIntermediaryForLastChild( this.normalNode1 ) );
		
		assertTrue( i == this.nodeWithDups.getIntermediaryForFirstChild() );
		assertTrue( i.getPointee() == this.normalNode1 );
		

		// adding the same node again, now there's two
		this.nodeWithDups.dupAppendChild( this.normalNode1 );
		
		IntermediaryNode i2 = this.nodeWithDups.getIntermediaryForNextChild(
				this.normalNode1, i, DO.SKIP );
		// continue from "i" as last intermediary found
		
		this.validateIntermediary( i2 );
		assertTrue( i2 != i );
		assertTrue( i2 == this.nodeWithDups.getNextIntermediary( i ) );
		assertTrue( i2 == this.nodeWithDups.getIntermediaryForLastChild() );
		
		assertTrue( this.nodeWithDups.getCountOfChildren( this.normalNode2 ) == 0 );
		assertTrue( this.nodeWithDups.getCountOfChildren( this.normalNode3 ) == 0 );
		this.nodeWithDups.dupAppendChild( this.normalNode2 );
		this.nodeWithDups.dupAppendChild( this.normalNode3 );
		
		IntermediaryNode i3 = this.nodeWithDups.getNextIntermediary( i2 );
		this.validateIntermediary( i3 );
		
		IntermediaryNode i4 = this.nodeWithDups.getNextIntermediary( i3 );
		this.validateIntermediary( i4 );
		
		assertTrue( 2 == this.nodeWithDups.getCountOfChildren( this.normalNode1 ) );
		assertTrue( this.nodeWithDups.getCountOfChildren( this.normalNode2 ) == 1 );
		assertTrue( this.nodeWithDups.getCountOfChildren( this.normalNode3 ) == 1 );
	}
	
	public void validateIntermediary( IntermediaryNode i ) {

		assertTrue( i != null );
		assertTrue( Environment.isIntermediaryNode( i ) );
		assertTrue( this.nodeWithDups.hasChild( i ) );
		// assertTrue( i.imGetParent() == this.nodeWithDups );
		assertTrue( i.hasParent( this.nodeWithDups ) );
	}
	
	@Test
	public void testNulls() {

		// TODO more ?
		
		boolean ex = false;
		try {
			this.nodeWithDups.getCountOfChildren( null );
		} catch ( NullPointerException e ) {
			ex = true;
		}
		assertTrue( ex );
	}
}
