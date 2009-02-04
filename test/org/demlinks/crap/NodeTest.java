package org.demlinks.crap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;

public class NodeTest {

	Node parent, child;

	@Before
	public void init() {
		parent = new Node();
		child = new Node();
	}

	@Test
	public void testAppendChild() throws InconsistentTypeCode {

		assertFalse(parent.hasChild(child));
		assertFalse(child.hasChild(parent));
		assertFalse(parent.hasParent(child));
		assertFalse(child.hasParent(parent));

		assertFalse(parent.appendChild(child));// parent->child
		assertTrue(parent.hasChild(child));

		assertTrue(parent.appendChild(child));// already exists, not re-added
										// and not moved at rear end of list
		assertTrue(parent.hasChild(child));

		assertFalse(child.hasChild(parent));

		assertTrue(child.hasParent(parent));// parent<-child ?
		assertFalse(parent.hasParent(child));// child<-parent ?

	}
	
	@Test
	public void testNullParams() throws InconsistentTypeCode {
		boolean excepted = false;
		try {
			parent.hasChild(null);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);

		excepted = false;
		try {
			parent.hasParent(null);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			parent.appendChild(null);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
	}

	@Test
	public void testInconsistentLink() {
		assertFalse( parent.internalAppendChild(child) );
		assertTrue(parent.internalAppendChild(child));
	}
}
