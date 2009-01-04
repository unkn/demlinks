package org.demlinks.javathree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IDToNodeMapTest {

	IDToNodeMap imap;
	String a,b,c,d,e,f;
	Node nodea, nodeb;
	
	@Before
	public void init() {
		imap = new IDToNodeMap();
		a = "a";
		b = new String(a);
		c = new String("a");
		d="a";
		e=a;
		f=c;
		nodea = new Node();
		nodeb = new Node();
	}

	@Test
	public void testGetNode() throws Exception {
		imap.put(a, nodea);
		imap.put(b, nodeb);//replaced
	}

	@Test
	public void testGetID() {
		fail("Not yet implemented");
	}

	@Test
	public void testPut() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveID() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testSize() {
		fail("Not yet implemented");
	}

}
