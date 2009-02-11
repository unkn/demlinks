
package org.demlinks.crap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.naming.CannotProceedException;

import org.demlinks.exceptions.InconsistentLinkException;
import org.junit.Before;
import org.junit.Test;

public class NodeTest {
	
	Node	parent, child;
	
	@Before
	public void init() {
		this.parent = new Node();
		this.child = new Node();
	}
	
	@Test
	public void testInsert() throws InconsistentLinkException {
		Node a = new Node();
		Node b = new Node();
		Node c = new Node();
		Node d = new Node();
		assertFalse( this.parent.appendChild( a ) );
		assertTrue( this.parent.getLastChild() == a );
		assertFalse( this.parent.insertChildAfter( c, a ) );
		assertTrue( this.parent.getFirstChild() == a );
		assertTrue( this.parent.getLastChild() == c );
		assertFalse( this.parent.insertChildBefore( b, c ) );
		assertFalse( this.parent.insertChildAfter( d, c ) );
		assertTrue( this.parent.getChildNextOf( a ) == b );
		assertTrue( this.parent.getChildPrevOf( d ) == c );
		assertTrue( this.parent.numChildren() == 4 );
		assertFalse( this.child.appendParent( a ) );
		assertTrue( this.child.getLastParent() == a );
		assertFalse( this.child.insertParentAfter( c, a ) );
		assertTrue( this.child.getFirstParent() == a );
		assertTrue( this.child.getLastParent() == c );
		assertFalse( this.child.insertParentBefore( b, c ) );
		assertFalse( this.child.insertParentAfter( d, c ) );
		assertTrue( this.child.getParentNextOf( a ) == b );
		assertTrue( this.child.getParentPrevOf( d ) == c );
		assertTrue( this.child.numParents() == 4 );
	}
	
	@Test
	public void testGet() throws InconsistentLinkException {
		Node a, b, c, d, e, f, g;
		a = new Node();
		b = new Node();
		c = new Node();
		d = new Node();
		e = new Node();
		f = new Node();
		g = new Node();
		assertFalse( a.appendChild( b ) );
		assertFalse( a.appendChild( c ) );
		assertFalse( a.appendChild( d ) );
		assertFalse( a.appendChild( e ) );
		assertFalse( a.appendParent( f ) );
		assertFalse( a.appendParent( g ) );
		Node parser = a.getFirstChild();
		assertTrue( b == parser );
		while ( null != parser ) {
			System.out.println( parser );
			parser = a.getChildNextOf( parser );
		}
		parser = a.getChildPrevOf( a.getLastChild() );
		assertTrue( parser == d );
		parser = a.getChildPrevOf( parser );
		assertTrue( parser == c );
		parser = a.getChildPrevOf( parser );
		assertTrue( parser == b );
		parser = a.getChildPrevOf( parser );
		assertTrue( parser == null );
		parser = a.getFirstParent();
		assertTrue( parser == f );
		parser = a.getParentNextOf( parser );
		assertTrue( parser == g );
		parser = a.getParentNextOf( parser );
		assertTrue( parser == null );
		assertTrue( f == a.getParentPrevOf( a.getLastParent() ) );
	}
	
	@Test
	public void testAppendChild() throws InconsistentLinkException {
		assertFalse( this.parent.hasChild( this.child ) );
		assertFalse( this.child.hasChild( this.parent ) );
		assertFalse( this.parent.hasParent( this.child ) );
		assertFalse( this.child.hasParent( this.parent ) );
		assertTrue( this.child.numParents() == 0 );
		assertTrue( this.parent.numChildren() == 0 );
		assertFalse( this.parent.appendChild( this.child ) );// parent->child
		assertTrue( this.parent.hasChild( this.child ) );
		assertTrue( this.parent.numChildren() == 1 );
		assertTrue( this.parent.appendChild( this.child ) );// already exists,
		// not re-added
		// and not moved at rear end of list
		assertTrue( this.parent.hasChild( this.child ) );
		assertTrue( this.parent.numChildren() == 1 );
		assertFalse( this.child.hasChild( this.parent ) );
		assertTrue( this.child.hasParent( this.parent ) );// parent<-child ? yes
		assertFalse( this.parent.hasParent( this.child ) );// child<-parent ? no
		boolean excepted = false;
		try {
			this.parent.appendChild( null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		assertTrue( this.parent.numChildren() == 1 );
	}
	
	@Test
	public void testNullParams() throws InconsistentLinkException {
		boolean excepted = false;
		try {
			this.parent.hasChild( null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.parent.hasParent( null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.parent.appendChild( null );
		} catch ( NullPointerException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
	
	@Test
	public void testInconsistentLink() throws CannotProceedException,
			InconsistentLinkException {
		assertFalse( this.parent.internalAppendChild( this.child ) );
		assertTrue( this.parent.internalAppendChild( this.child ) );
		boolean excepted = false;
		try {
			this.parent.hasChild( this.child );
		} catch ( InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			this.parent.appendChild( this.child );
			// after the above call, the link will nolonger be inconsistent
		} catch ( InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		assertTrue( this.parent.removeChild( this.child ) );
		// assertTrue(parent.internalRemoveChild(child));
		assertFalse( this.child.internalAppendParent( this.parent ) );
		assertTrue( this.child.internalAppendParent( this.parent ) );
		excepted = false;
		try {
			this.child.hasParent( this.parent );
		} catch ( InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
	
	@Test
	public void testRemove() throws CannotProceedException,
			InconsistentLinkException {
		assertTrue( this.child.numParents() == 0 );
		assertTrue( this.parent.numChildren() == 0 );
		assertFalse( this.parent.removeChild( this.child ) );
		assertFalse( this.parent.appendChild( this.child ) );
		assertTrue( this.parent.hasChild( this.child ) );
		assertTrue( this.child.numParents() == 1 );
		assertTrue( this.parent.removeChild( this.child ) );
		assertFalse( this.parent.removeChild( this.child ) );
		assertFalse( this.child.removeParent( this.parent ) );
		assertFalse( this.parent.appendChild( this.child ) );
		assertTrue( this.parent.hasChild( this.child ) );
		assertTrue( this.parent.numChildren() == 1 );
		assertTrue( this.child.removeParent( this.parent ) );
		assertFalse( this.child.removeParent( this.parent ) );
		assertTrue( this.child.numParents() == 0 );
		assertTrue( this.parent.numChildren() == 0 );
	}
}
