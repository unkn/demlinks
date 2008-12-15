package org.demlinks.javaone;
import static org.junit.Assert.*;

import org.demlinks.javaone.Node;
import org.demlinks.javaone.NodeIterator;
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
		assertTrue(ul.append(a));
		Node b = new Node();
		assertTrue(ul.append(b));
		
		assertFalse(ul.append(a));
		
		Node c = null;
		boolean upe=false;
		try {
			ul.append(c);
		}catch (NullPointerException e) {
			upe = true;
		}
		assertTrue(upe);
		assertTrue(ul.size() == 2);
	}

	@Test
	public void testSize() {
		assertTrue(ul.size() == 0);
		assertTrue(ul.isEmpty());
		Node a;
		ul.append(a=new Node());
		assertFalse(ul.isEmpty());
		assertTrue(ul.size() == 1);
		ul.append(new Node());
		assertTrue(ul.size() == 2);
		assertTrue(ul.contains(a));
		assertFalse(ul.contains(new Node()));
		ul.remove(a);
		assertTrue(ul.size() == 1);
	}

	@Test
	public void testRemove() {
		Node a=new Node();
		ul.append(a);
		assertTrue(ul.remove(a));
		assertFalse(ul.remove(a));
		
		Node b= null;
		boolean npe=false;
		try {
			ul.remove(b);
		} catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
	}

	@Test
	public void testNodeIterator() {
		NodeIterator ni = ul.nodeIterator(0);
		
		boolean npe=false;
		try {
			ni.find(null);
		} catch  (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
		
		Node a = new Node();
		Node b = a;
		ul.append(a);
		assertTrue(ni.find(b));
		
		assertFalse(ni.find(new Node()));
		
		
	}

}
