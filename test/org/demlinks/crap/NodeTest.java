package org.demlinks.crap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.naming.CannotProceedException;

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
	public void testGet() throws InconsistentTypeCode {
		Node a,b,c,d,e,f,g;
		a = new Node();
		b = new Node();
		c = new Node();
		d = new Node();
		e = new Node();
		f = new Node();
		g = new Node();
		assertFalse(a.appendChild(b));
		assertFalse(a.appendChild(c));
		assertFalse(a.appendChild(d));
		assertFalse(a.appendChild(e));
		assertFalse(a.appendParent(f));
		assertFalse(a.appendParent(g));
		
		Node parser = a.getFirstChild();
		assertTrue(b == parser);
		while (null != parser) {
			System.out.println(parser);
			parser = a.getChildNextOf(parser);
		}
		parser = a.getChildPrevOf(a.getLastChild());
		assertTrue(parser == d);
		parser = a.getChildPrevOf(parser);
		assertTrue(parser == c);
		parser = a.getChildPrevOf(parser);
		assertTrue(parser == b);
		parser = a.getChildPrevOf(parser);
		assertTrue(parser == null);
		
		parser = a.getFirstParent();
		assertTrue(parser == f);
		parser = a.getParentNextOf(parser);
		assertTrue(parser == g);
		parser = a.getParentNextOf(parser);
		assertTrue(parser == null);
		
		assertTrue(f == a.getParentPrevOf(a.getLastParent()));
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

		assertTrue(child.hasParent(parent));// parent<-child ? yes
		assertFalse(parent.hasParent(child));// child<-parent ? no

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
	public void testInconsistentLink() throws CannotProceedException, InconsistentTypeCode {
		assertFalse( parent.internalAppendChild(child) );
		assertTrue(parent.internalAppendChild(child));
		
		boolean excepted = false;
		try {
			parent.hasChild(child);
		}catch (InconsistentTypeCode e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			parent.appendChild(child);
			//after the above call, the link will nolonger be inconsistent
		}catch (InconsistentTypeCode e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		assertTrue(parent.removeChild(child));
		
		//assertTrue(parent.internalRemoveChild(child));
		assertFalse(child.internalAppendParent(parent));
		assertTrue(child.internalAppendParent(parent));
		
		excepted = false;
		try {
			child.hasParent(parent);
		}catch (InconsistentTypeCode e) {
			excepted = true;
		}
		assertTrue(excepted);
	}
}
