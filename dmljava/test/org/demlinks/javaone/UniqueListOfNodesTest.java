package org.demlinks.javaone;
import static org.junit.Assert.*;

import org.demlinks.javaone.Node;
import org.junit.Before;
import org.junit.Test;


public class UniqueListOfNodesTest {

	UniqueListOfNodes ul;
	
	@Before
	public void testUniqueListOfNodes() {
		ul = new UniqueListOfNodes();
	}

	@Test
	public void testAppend() {
		Node a = new Node();
		assertTrue(ul.addLast(a));
		Node b = new Node();
		assertTrue(ul.addLast(b));
		
		assertFalse(ul.addLast(a));
		
		Node c = null;
		boolean upe=false;
		try {
			ul.addLast(c);
		}catch (NullPointerException e) {
			upe = true;
		}
		assertTrue(upe);
		assertTrue(ul.getSize() == 2);
	}

	@Test
	public void testSize() {
		assertTrue(ul.getSize() == 0);
		assertTrue(ul.isEmpty());
		Node a;
		ul.addLast(a=new Node());
		assertFalse(ul.isEmpty());
		assertTrue(ul.getSize() == 1);
		ul.addLast(new Node());
		assertTrue(ul.getSize() == 2);
		assertTrue(ul.containsObj(a));
		assertFalse(ul.containsObj(new Node()));
		ul.removeObj(a);
		assertTrue(ul.getSize() == 1);
	}

	@Test
	public void testRemove() {
		Node a=new Node();
		ul.addLast(a);
		assertTrue(ul.removeObj(a));
		assertFalse(ul.removeObj(a));
		
		Node b= null;
		boolean npe=false;
		try {
			ul.removeObj(b);
		} catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
	}

	@Test
	public void testNodeCursor() {
//		NodeIterator ni = ul.listCursor(0);
//		
//		boolean npe=false;
//		try {
//			ni.find(null);
//		} catch  (NullPointerException e) {
//			npe=true;
//		}
//		assertTrue(npe);
//		
//		Node a = new Node();
//		Node b = a;
//		ul.addLast(a);
//		assertTrue(ni.find(b));
//		
//		assertFalse(ni.find(new Node()));
//		
		
	}

}
