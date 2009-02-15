
package org.demlinks.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		
		assertFalse( this.nodeWithDups.dupAppendChild( this.normalNode1 ) );
		// Node c = null;// will be filled by Solve
		// if ( b.Solve( a, Sense.Child, c, Sense.Child, b ) ) {// a->c->b, is
		// c?
		// // there was a solution
		// // we might need SolveNext, kind of, in case more than 1 solution
		// assertTrue( GlobalNodes.isPointer( c ) );
		// }
		//		 
		assertTrue( this.nodeWithDups.dupHasChild( this.normalNode1 ) );
		IntermediaryNode i = this.nodeWithDups
				.getIntermediaryForFirstChild( this.normalNode1 );
		this.validateIntermediary( i );
		assertTrue( i == this.nodeWithDups.getIntermediaryForFirstChild() );
		assertTrue( i.getChild() == this.normalNode1 );
		
		// adding the same node again, now there's two
		assertTrue( this.nodeWithDups.dupAppendChild( this.normalNode1 ) );
		IntermediaryNode i2 = this.nodeWithDups.getIntermediaryForNextChild(
				this.normalNode1, i );// continue from "i" as last intermediary
		// found
		this.validateIntermediary( i2 );
		assertTrue( i2 != i );
		assertTrue( i2 == this.nodeWithDups.getIntermediaryForNextChild( i ) );
		assertTrue( i2 == this.nodeWithDups.getIntermediaryForLastChild() );
		
		assertFalse( this.nodeWithDups.dupAppendChild( this.normalNode2 ) );
		assertFalse( this.nodeWithDups.dupAppendChild( this.normalNode3 ) );
		
		IntermediaryNode i3 = this.nodeWithDups
				.getIntermediaryForNextChild( i2 );
		this.validateIntermediary( i3 );
		
		IntermediaryNode i4 = this.nodeWithDups
				.getIntermediaryForNextChild( i3 );
		this.validateIntermediary( i4 );
	}
	
	public void validateIntermediary( IntermediaryNode i ) {
		assertTrue( i != null );
		assertTrue( GlobalNodes.isIntermediaryNode( i ) );
		assertTrue( this.nodeWithDups.hasChild( i ) );
		// assertTrue( i.imGetParent() == this.nodeWithDups );
		assertTrue( i.hasParent( this.nodeWithDups ) );
	}
}
