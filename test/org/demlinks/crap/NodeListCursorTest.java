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
		cur = null;
	}
	
	public void populateList() {
		assertTrue(list.isEmpty());
		assertFalse(list.appendNode(node1));
		assertFalse(list.appendNode(node2));
		assertFalse(list.appendNode(node3));
		assertTrue(list.size() == 3);
	}
	
	@Test
	public void testAll() throws NoSuchFieldException {
		cur = list.getCursor();//returns undefined cursor aka current is unknown
		assertTrue(cur.isUndefined());
		assertTrue(null == cur.getCurrent());
		
		boolean excepted = true;
		try {
			cur.goTo(null);
		}catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		//parse both ways, first with empty then with full list
		int count;
		do {
			//parse forward
			count = 0;
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

			populateList();
		} while (count == 0);
		
		
	}
}
