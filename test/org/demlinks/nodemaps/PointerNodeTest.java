
package org.demlinks.nodemaps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.node.Node;
import org.demlinks.nodemaps.Environment;
import org.demlinks.nodemaps.PointerNode;
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
		// assertTrue( Environment.AllPointers.numChildren() == 1 );
		System.out.println( Environment.AllPointers.numChildren() );
		assertTrue( Environment.AllPointers.hasChild( this.p1 ) );
		assertTrue( this.p1.hasParent( Environment.AllPointers ) );
		
		Node a = new Node();
		assertTrue( this.p1.getPointee() == null );
		assertFalse( this.p1.pointTo( a ) );
		assertTrue( this.p1.getPointee() == a );
		


		assertTrue( this.p1.setNull() );
		assertTrue( null == this.p1.getPointee() );
		assertFalse( this.p1.setNull() );
		assertTrue( null == this.p1.getPointee() );
		
		assertTrue( Environment.isPointer( this.p1 ) );
		Node n = new Node();
		Node m = new PointerNode();
		assertFalse( Environment.isPointer( n ) );
		assertTrue( Environment.isPointer( m ) );
	}
}
