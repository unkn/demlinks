package org.demlinks.javaone;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class LinkedListSetTest {

	LinkedListSet<String> lls;
	String a = "a";
	String b = "b";
	String c = "c";
	
	@Before
	public void init() {
		lls = new LinkedListSet<String>();
	}

	@Test
	public void testIsEmpty() {
		assertTrue(lls.isEmpty());
		assertTrue(lls.getSize() == 0);
		
		String boo = "boo";
		assertTrue( lls.addFirst(boo) );
		assertTrue(lls.getSize() == 1);
		assertFalse(lls.isEmpty());
		
		assertTrue( boo == lls.removeLast() );
		assertTrue(lls.isEmpty());
		assertTrue(lls.getSize() == 0);
		
		assertTrue( lls.addLast("boo") );
		assertFalse(lls.isEmpty());
		assertTrue(lls.getSize() == 1);
		
		assertTrue( boo == lls.removeFirst() );
		assertTrue(lls.isEmpty());
		assertTrue(lls.getSize() == 0);
	}

	@Test
	public void testGetFirstObj() {
		boolean nsee;
			assertTrue(null == lls.getFirstObj() );
		
		nsee = false;
		try {
			lls.removeLast();
		} catch (NoSuchElementException e) {
			nsee = true;
		}
		assertTrue(nsee);
		
		String boo = "boo";
		lls.addLast(boo);
		String newBoo = new String("boo");
		assertTrue(boo != newBoo);
		String gotten = lls.getFirstObj();
		assertTrue( newBoo != gotten );
		assertTrue( newBoo.equals(gotten));
		
		assertTrue( boo == lls.removeLast() );
	}

	@Test
	public void testGetLastObj() {
		boolean nsee;
			assertTrue( null == lls.getLastObj() );
		
		nsee = false;
		try {
			lls.removeFirst();
		} catch (NoSuchElementException e) {
			nsee = true;
		}
		assertTrue(nsee);
		
		String boo = "boo";
		lls.addLast(boo);
		String newBoo = new String("boo");
		assertTrue(boo != newBoo);
		String gotten = lls.getLastObj();
		assertTrue( newBoo != gotten );
		assertTrue( newBoo.equals(gotten));
		
		assertTrue(gotten == lls.getFirstObj() );
		
		assertTrue( boo == lls.removeFirst() );
	}

	@Test
	public void testAddFirst() {

		assertTrue( lls.addFirst(a) );
		assertTrue( a == lls.getFirstObj());
		
		assertTrue( lls.addFirst(b) );
		assertTrue( b == lls.getFirstObj());
		
		assertTrue( lls.addFirst(c) );
		assertTrue( c == lls.getFirstObj());
		
		assertTrue( a == lls.getLastObj());
		assertFalse( lls.addLast(c) );//not added, already exists
		assertTrue( a == lls.getLastObj());
	}

	@Test
	public void testAddLast() {
		assertTrue(lls.addLast(a));
		assertFalse(lls.addLast(a));
		assertTrue(lls.addLast(b));
		assertTrue(lls.getFirstObj() == a);
		assertTrue(b == lls.getLastObj());
		
		assertTrue(lls.containsObj(a));
		assertTrue(lls.containsObj(b));
		assertFalse(lls.containsObj(c));
		
		assertTrue(lls.addLast(c));
		assertFalse(lls.addLast(b));
	}

	@Test
	public void testContainsObj() {
		String str=null;
		boolean npe = false;
		try {
			lls.containsObj(str);
		} catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		assertTrue(lls.addFirst(a));
		assertTrue(lls.containsObj(a));
		assertFalse(lls.containsObj(b));
	}

	@Test
	public void testRemoveObj() {
		lls.addFirst(a);
		lls.addLast(b);
		lls.addLast(c);
		assertTrue(lls.removeObj(b));
		assertTrue(lls.getSize() == 2);
		assertTrue(lls.removeObj(a));
		assertTrue(lls.getSize() == 1);
		assertFalse(lls.removeObj(b));
		assertTrue(lls.removeObj(c));
		assertTrue(lls.getSize() == 0);
		assertTrue(lls.addFirst(b));//not a
		
		assertFalse(lls.removeObj(a));
		assertTrue(lls.removeObj(b));
		assertTrue(lls.isEmpty());
		assertFalse(lls.removeObj(a));
	
		
		String nul=null;
		boolean npe=false;
		try {
			lls.removeObj(nul);
		}catch (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
	}

	@Test
	public void testClear() {
		lls.addFirst(a);
		lls.addLast(b);
		lls.addLast(c);
		assertTrue(lls.getSize() == 3);
		assertTrue(lls.clear());
		assertTrue(lls.isEmpty());
		assertFalse(lls.clear());
	}

	@Test
	public void testGet() {
		lls.addFirst(a);
		lls.addLast(b);
		lls.addLast(c);
		assertTrue( a == lls.get(0));
		assertTrue( b == lls.get(1));
		assertTrue( c == lls.get(2));
		assertTrue(lls.clear());
		assertTrue(lls.isEmpty());
		boolean ioobe = false;
		try {
			lls.get(0);
		}catch (IndexOutOfBoundsException e) {
			ioobe = true;
		}
		assertTrue(ioobe);
	}

	@Test
	public void testReplace() {
		boolean ioobe = false;
		try {
			lls.replace(0, a);
		}catch (IndexOutOfBoundsException e) {
			ioobe = true;
		}
		assertTrue(ioobe);
		
		lls.addFirst(a);
		lls.addLast(b);
		assertTrue(lls.get(0) == a);
		assertTrue( a == lls.replace(0,c) );
		assertTrue( b == lls.replace(1,a) );
		assertTrue( null == lls.replace(0, a) ); //again, but fail
		assertTrue( null == lls.replace(0, c) );//already exists too
	}

	@Test
	public void testListCursor() {
//		lls.addFirst(a);
//		lls.insertAt(0,b);
//		lls.insertAt(0, c);
//		
//		CursorIterator<String> li = lls.listCursor(0, Location.INSTEADOF);
//		while (li.hasNext()) {
//			System.out.print(li.next()+", ");
//		}
//		while (li.hasPrevious()) {
//			System.out.print(li.previous()+", ");
//		}
//		assertTrue( lls.indexOfObj(a) == 2 );
//		
//		assertTrue( c == lls.removeAt(0) );
//		assertTrue(-1 == lls.indexOfObj(c));
//		
//		assertTrue( a == lls.removeAt(1) );
//		assertTrue( b == lls.removeAt(0) );
	}

}
