package org.demlinks.crap;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {

	Node a, b;

	@Before
	public void init() {
		a = new Node();
		b = new Node();
	}

	@Test
	public void testAppendChild() {
		assertTrue(a.appendChild(b));
		assertTrue(a.hasChild(b));
		assertFalse(a.appendChild(b));
		assertTrue(a.hasChild(b));
	}
}
