

package org.demlinks.nodemaps;



import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class CharNodeTest {
	
	CharNode	p1;
	
	@Before
	public void init() {

		this.p1 = new CharNode();
	}
	
	@Test
	public void testPointerNode() {

		System.out.println( Environment.AllCharNodes.numChildren() );
		assertTrue( Environment.AllCharNodes.hasChild( this.p1 ) );
		assertTrue( this.p1.hasParent( Environment.AllCharNodes ) );
		

	}
}
