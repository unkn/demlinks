package org.demlinks.crap;

import static org.junit.Assert.*;

import javax.naming.CannotProceedException;

import org.junit.Before;
import org.junit.Test;


public class NodeListTest {

	Node node1, node2, node3, nullNode;
	NodeList list;
	
	@Before
	public void init() {
		node1 = new Node();
		node2 = new Node();
		node3 = new Node();
		nullNode = null;
		list = new NodeList();
	}
	
	@Test
	public void testAppendNode() {
		assertTrue(list.isEmpty());
		assertTrue(list.getFirstNode() == null);
		assertTrue(list.getLastNode() == null);
		assertFalse(list.hasNode(node1));
		assertFalse(list.hasNode(node2));
		
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
		
		assertTrue( node2 == list.getNodeAfter(node1) );
		assertTrue( node1 == list.getNodeBefore(node2) );
		assertTrue(list.getNodeAfter(node2) == null);
		assertTrue(list.getNodeBefore(node1) == null);
		
		excepted = false;
		try {
			list.hasNode(nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		assertTrue(list.hasNode(node1));
		assertTrue(list.hasNode(node2));
	}

	@Test
	public void testInsertNode() {
		assertTrue(list.isEmpty());
		assertFalse(list.insertNode(node1, Position.FIRST));
		assertFalse(list.insertNode(node2, Position.LAST));
		assertTrue(list.getFirstNode() == node1);
		assertTrue(list.getLastNode() == node2);
		assertTrue(list.size() == 2);
		
		boolean excepted = false;
		try {
			list.insertNode(nullNode, Position.FIRST);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertNode(node1, null);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertNode(null, null);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
	}
	

	@Test
	public void testInsertAfterNode() throws CannotProceedException {
		assertTrue(list.isEmpty());
		assertFalse(list.appendNode(node1));
		assertFalse(list.insertAfterNode(node2, node1));
		assertTrue(list.getLastNode() == node2);
		assertFalse(list.insertAfterNode(node3, node2));
		assertTrue(list.getLastNode() == node3);
		assertTrue(list.size() == 3);
		
		assertTrue(list.removeNode(node2));
		assertFalse(list.removeNode(node2));
		assertTrue(list.size() == 2);
		assertTrue(list.hasNode(node1));
		assertTrue(list.hasNode(node3));
		
		assertFalse(list.insertBeforeNode(node2, node3));
		assertTrue(list.getNodeAfter(node1) == list.getNodeBefore(node3));
		assertTrue(list.getNodeAfter(node1) == node2);
		
		
		
		boolean excepted = false;
		try {
			list.insertAfterNode(nullNode, node2);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertAfterNode(node1, nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertAfterNode(nullNode, nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertBeforeNode(nullNode, node2);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertBeforeNode(node1, nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.insertBeforeNode(nullNode, nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			list.removeNode(nullNode);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
	}

}
