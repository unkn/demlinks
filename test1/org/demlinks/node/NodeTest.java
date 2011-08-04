/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.node;



import static org.junit.Assert.*;

import org.demlinks.exceptions.*;
import org.junit.*;
import org.q.*;



public class NodeTest {
	
	Node	parent, child;
	
	
	@Before
	public void init() {
		
		parent = new Node();
		child = new Node();
	}
	
	
	@Test
	public void testInsert() {
		
		final Node a = new Node();
		final Node b = new Node();
		final Node c = new Node();
		final Node d = new Node();
		assertFalse( parent.appendChild( a ) );
		assertTrue( parent.getLastChild() == a );
		assertFalse( parent.insertChildAfter( c, a ) );
		assertTrue( parent.getFirstChild() == a );
		assertTrue( parent.getLastChild() == c );
		assertFalse( parent.insertChildBefore( b, c ) );
		assertFalse( parent.insertChildAfter( d, c ) );
		assertTrue( parent.getChildNextOf( a ) == b );
		assertTrue( parent.getChildPrevOf( d ) == c );
		assertTrue( parent.numChildren() == 4 );
		assertFalse( child.appendParent( a ) );
		assertTrue( child.getLastParent() == a );
		assertFalse( child.insertParentAfter( c, a ) );
		assertTrue( child.getFirstParent() == a );
		assertTrue( child.getLastParent() == c );
		assertFalse( child.insertParentBefore( b, c ) );
		assertFalse( child.insertParentAfter( d, c ) );
		assertTrue( child.getParentNextOf( a ) == b );
		assertTrue( child.getParentPrevOf( d ) == c );
		assertTrue( child.numParents() == 4 );
	}
	
	
	@Test
	public void testGet() {
		
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
	public void testAppendChild() {
		
		assertFalse( parent.hasChild( child ) );
		assertFalse( child.hasChild( parent ) );
		assertFalse( parent.hasParent( child ) );
		assertFalse( child.hasParent( parent ) );
		assertTrue( child.numParents() == 0 );
		assertTrue( parent.numChildren() == 0 );
		assertFalse( parent.appendChild( child ) );// parent->child
		assertTrue( parent.hasChild( child ) );
		assertTrue( parent.numChildren() == 1 );
		assertTrue( parent.appendChild( child ) );// already exists,
		// not re-added
		// and not moved at rear end of list
		assertTrue( parent.hasChild( child ) );
		assertTrue( parent.numChildren() == 1 );
		assertFalse( child.hasChild( parent ) );
		assertTrue( child.hasParent( parent ) );// parent<-child ? yes
		assertFalse( parent.hasParent( child ) );// child<-parent ? no
		try {
			parent.appendChild( null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		assertTrue( parent.numChildren() == 1 );
	}
	
	
	@Test
	public void testNullParams() {
		
		try {
			parent.hasChild( null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			parent.hasParent( null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
		try {
			parent.appendChild( null );
			Q.fail();
		} catch ( final NullPointerException e ) {
		}
	}
	
	
	@Test
	public void testInconsistentLink() {
		
		assertFalse( parent.internalAppendChild( child ) );
		assertTrue( parent.internalAppendChild( child ) );
		boolean excepted = false;
		try {
			parent.hasChild( child );
			Q.fail();
		} catch ( final InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		excepted = false;
		try {
			parent.appendChild( child );
			// after the above call, the link will nolonger be inconsistent
		} catch ( final InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
		assertTrue( parent.removeChild( child ) );
		// assertTrue(parent.internalRemoveChild(child));
		assertFalse( child.internalAppendParent( parent ) );
		assertTrue( child.internalAppendParent( parent ) );
		excepted = false;
		try {
			child.hasParent( parent );
		} catch ( final InconsistentLinkException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
	
	
	@Test
	public void testRemove() {
		
		assertTrue( child.numParents() == 0 );
		assertTrue( parent.numChildren() == 0 );
		assertFalse( parent.removeChild( child ) );
		assertFalse( parent.appendChild( child ) );
		assertTrue( parent.hasChild( child ) );
		assertTrue( child.numParents() == 1 );
		assertTrue( parent.removeChild( child ) );
		assertFalse( parent.removeChild( child ) );
		assertFalse( child.removeParent( parent ) );
		assertFalse( parent.appendChild( child ) );
		assertTrue( parent.hasChild( child ) );
		assertTrue( parent.numChildren() == 1 );
		assertTrue( child.removeParent( parent ) );
		assertFalse( child.removeParent( parent ) );
		assertTrue( child.numParents() == 0 );
		assertTrue( parent.numChildren() == 0 );
	}
	
	
	@Test
	public void testGetNextParent() {
		
		Node a, b, c, d, e, f;
		a = new Node();
		b = new Node();
		c = new Node();
		d = new Node();
		e = new Node();
		f = new Node();
		
		assertFalse( parent.appendChild( b ) );// parent->b
		assertFalse( parent.appendChild( d ) );// parent->d
		assertFalse( parent.appendChild( f ) );// parent->f
		
		assertFalse( child.appendParent( a ) );// a->child
		
		// get first
		assertTrue( null == child.getNextParent( parent, null ) );
		
		
		assertFalse( child.appendParent( b ) );// b->child
		assertTrue( child.getFirstParent() == a );
		final Node tmp = child.getNextParent( parent, null );
		assertFalse( null == tmp );
		assertTrue( b == tmp );
		assertTrue( null == child.getNextParent( parent, b ) );
		assertFalse( child.appendParent( c ) );
		assertFalse( child.appendParent( d ) );
		assertFalse( child.appendParent( e ) );
		assertFalse( child.appendParent( f ) );
		
		assertTrue( d == child.getNextParent( parent, b ) );
		assertTrue( f == child.getNextParent( parent, d ) );
		assertTrue( null == child.getNextParent( parent, f ) );
		
	}
}
