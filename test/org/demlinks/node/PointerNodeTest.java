
package org.demlinks.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PointerNodeTest {
	
	PointerNode	p1;
	
	@Before
	public void init() {
		this.p1 = new PointerNode();
	}
	
	@Test
	public void testPointerNode() {
		// assertTrue( GlobalNodes.AllPointers.numChildren() == 1 );
		System.out.println( GlobalNodes.AllPointers.numChildren() );
		assertTrue( GlobalNodes.AllPointers.hasChild( this.p1 ) );
		assertTrue( this.p1.hasParent( GlobalNodes.AllPointers ) );
		
		Node a = new Node();
		assertTrue( this.p1.getPointee() == null );
		assertFalse( this.p1.pointTo( a ) );
		assertTrue( this.p1.getPointee() == a );
		


		assertTrue( this.p1.setNull() );
		assertTrue( null == this.p1.getPointee() );
		assertFalse( this.p1.setNull() );
		assertTrue( null == this.p1.getPointee() );
		
		assertTrue( GlobalNodes.isPointer( this.p1 ) );
		Node n = new Node();
		Node m = new PointerNode();
		assertFalse( GlobalNodes.isPointer( n ) );
		assertTrue( GlobalNodes.isPointer( m ) );
	}
}
