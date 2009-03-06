

package org.demlinks.nodemaps;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.errors.BadCallError;
import org.demlinks.node.Node;
import org.junit.Before;
import org.junit.Test;



public class ChildlessNodeTest {
	
	ChildlessNode	p1;
	
	@Before
	public void init() {

		this.p1 = new ChildlessNode();
	}
	
	@Test
	public void testChildlessNode() {

		assertTrue( this.p1.numChildren() == 0 );
		// TODO
		
		boolean ex = false;
		try {
			this.p1.appendChild( new Node() );
		} catch ( BadCallError e ) {
			ex = true;
		}
		assertTrue( ex );
		
		Node p2 = new Node();
		assertFalse( this.p1.appendParent( p2 ) );
		this.p1.integrityCheck();
		assertTrue( this.p1.numChildren() == 0 );
	}
}
