

package org.demlinks.javathree;



import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;



public class IDToNodeMapTest
{
	
	IDToNodeMap	imap;
	Id			a, b, c, d, e, f, g;
	Node		nodea, nodeb;
	
	
	@Before
	public
			void
			init()
	{
		imap = new IDToNodeMap();
		
		a = new Id(
					"a" );
		b = new Id(
					new String(
								new String(
											"a" ) ) );
		c = new Id(
					new String(
								"a" ) );
		d = new Id(
					"a" );
		e = a;
		f = c;
		g = new Id(
					"gggggg" );
		
		nodea = new Node();
		nodeb = new Node();
	}
	
	
	public
			void
			showAllKeys(
							String s )
	{
		Iterator<Map.Entry<Id, Node>> itr = imap.getKeyValueIterator();
		System.out.println( s );
		while ( itr.hasNext() )
		{
			System.out.println( itr.next() );
		}
	}
	
	
	@Test
	public
			void
			testGetNode()
	{
		assertTrue( imap.put(
								a,
								nodea ) );// first time
		this.showAllKeys( "1" );
		assertTrue( imap.getNode( a ) == nodea );
		assertFalse( imap.put(
								b,
								nodeb ) );// replaced
		this.showAllKeys( "2" );
		assertTrue( imap.getNode( b ) == nodeb );
		assertTrue( a.equals( b ) );
		assertFalse( a == b );
		assertFalse( nodea.equals( nodeb ) );
		assertFalse( nodea == nodeb );
		assertTrue( imap.getID(
								nodeb ).equals(
												a ) );
		assertTrue( imap.getID(
								nodeb ).equals(
												b ) );
		assertTrue( imap.getID( nodeb ) == b );
		
		assertFalse( imap.put(
								c,
								nodea ) );// 2nd time
		assertFalse( imap.put(
								c,
								nodeb ) );// 3rd time
		assertTrue( imap.size() == 1 );
		assertFalse( imap.put(
								g,
								nodeb ) );// nodeb already exists
		assertTrue( imap.size() == 1 );
		assertFalse( imap.getNode( g ) == null );
		assertTrue( nodeb == imap.removeID( g ) );
		assertTrue( imap.getNode( g ) == null );
		assertTrue( imap.put(
								g,
								nodea ) );// new
		assertTrue( imap.size() == 1 );
		this.showAllKeys( "3" );
		assertTrue( g == imap.removeNode( nodea ) );
		assertTrue( imap.size() == 0 );
		
		
		assertTrue( a.equals( b ) );
		assertTrue( b.equals( c ) );
		assertTrue( c.equals( d ) );
		assertTrue( d.equals( e ) );
		assertTrue( e.equals( f ) );
		assertFalse( f.equals( g ) );
		assertTrue( a != b );
		assertTrue( a != c );
		assertTrue( b != c );
		assertFalse( a == d );
		assertTrue( a == e );
		assertTrue( c == f );
		assertTrue( imap.put(
								a,
								nodea ) );
		assertTrue( imap.getID(
								nodea ).equals(
												b ) );
		assertTrue( imap.getID(
								nodea ).equals(
												c ) );
		assertTrue( imap.getID(
								nodea ).equals(
												d ) );
		assertTrue( imap.getID(
								nodea ).equals(
												e ) );
		assertTrue( imap.getID(
								nodea ).equals(
												f ) );
		assertTrue( imap.size() == 1 );
		assertTrue( imap.getNode( a ) == nodea );
		assertTrue( imap.getNode( b ) == nodea );
		assertTrue( imap.getNode( c ) == nodea );
		assertTrue( imap.getNode( d ) == nodea );
		assertTrue( imap.getNode( e ) == nodea );
		assertTrue( imap.getNode( f ) == nodea );
		assertTrue( imap.getNode( g ) == null );
		assertTrue( imap.getID( nodeb ) == null );
		this.showAllKeys( "4" );
	}
	
}
