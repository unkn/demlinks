
package org.demlinks.crap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.exceptions.BugDetected;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;

public class PointerNodeTest {
	
	PointerNode	p1;
	
	@Before
	public void init() {
		this.p1 = new PointerNode();
	}
	
	@Test
	public void testPointerNode() throws InconsistentTypeCode, BugDetected {
		assertTrue( GlobalNodes.AllPointers.numChildren() == 1 );
		assertTrue( GlobalNodes.AllPointers.hasChild( this.p1 ) );
		assertTrue( this.p1.hasParent( GlobalNodes.AllPointers ) );
		Node a = new Node();
		assertTrue( this.p1.getPointee() == null );
		assertFalse( this.p1.pointTo( a ) );
		assertTrue( this.p1.getPointee() == a );
	}
}
