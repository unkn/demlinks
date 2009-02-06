package org.demlinks.crap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class NodeListTest {

	Node node1, node2, nullNode;
	NodeList list;
	
	@Before
	public void init() {
		node1 = new Node();
		node2 = new Node();
		nullNode = null;
		list = new NodeList();
	}
	
	@Test
	public void testAppendNode() {
		assertTrue(list.isEmpty());
		assertFalse(list.appendNode(node1)); //false= node didn't already exist
		assertTrue(node1 == list.getLastNode());
		assertFalse(list.appendNode(node2));
		assertTrue(node2 == list.getLastNode());
		assertTrue(list.appendNode(node1));//already there
		assertFalse(node1 == list.getLastNode());//it wasn't moved last
		assertTrue(2 == list.size());
		
		boolean excepted = false;
		try {
			list.appendNode(nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
	}
}
