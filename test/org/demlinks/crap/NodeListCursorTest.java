package org.demlinks.crap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class NodeListCursorTest {
	Node node1, node2, node3;
	NodeList list;
	NodeListCursor cur;
	
	@Before
	public void init() {
		list = new NodeList();
		assertFalse(list.appendNode(node1));
		assertFalse(list.appendNode(node2));
		assertFalse(list.appendNode(node3));
		cur = null;
	}
	
	@Test
	public void testBegin() throws NoSuchFieldException {
		cur = list.getCursor();//returns undefined cursor aka current is unknown
		assertTrue(cur.isUndefined());
		boolean excepted = false;
		try {
			cur.getCurrent();
		}catch (NoSuchFieldException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		//parse forward
		int count = 0;
		cur.goTo(Position.FIRST);
		while (cur.hasCurrent()) {
			count++;
			System.out.println(cur.getCurrent());
			cur.next();
		}
		assertTrue(list.size() == count);

		//parse backward
		count = 0;
		cur.goTo(Position.LAST);
		while (cur.hasCurrent()) {
			count++;
			System.out.println(cur.getCurrent());
			cur.prev();
		}
		assertTrue(list.size() == count);
		
	}
}
