package org.demlinks.javaone;
import static org.junit.Assert.*;

import org.demlinks.javaone.NodeLevel0;
import org.junit.Before;
import org.junit.Test;


public class UniqueListOfNodesTest {

	ListOfUniqueNodes ul;
	
	@Before
	public void testUniqueListOfNodes() {
		ul = new ListOfUniqueNodes();
	}

	@Test
	public void testAppend() {
		NodeLevel0 a = new NodeLevel0();
		assertTrue(ul.addLast(a));
		NodeLevel0 b = new NodeLevel0();
		assertTrue(ul.addLast(b));
		
		assertFalse(ul.addLast(a));
		
		NodeLevel0 c = null;
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
		NodeLevel0 a;
		ul.addLast(a=new NodeLevel0());
		assertFalse(ul.isEmpty());
		assertTrue(ul.getSize() == 1);
		ul.addLast(new NodeLevel0());
		assertTrue(ul.getSize() == 2);
		assertTrue(ul.containsObj(a));
		assertFalse(ul.containsObj(new NodeLevel0()));
		ul.removeObj(a);
		assertTrue(ul.getSize() == 1);
	}

	@Test
	public void testRemove() {
		NodeLevel0 a=new NodeLevel0();
		ul.addLast(a);
		assertTrue(ul.removeObj(a));
		assertFalse(ul.removeObj(a));
		
		NodeLevel0 b= null;
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
