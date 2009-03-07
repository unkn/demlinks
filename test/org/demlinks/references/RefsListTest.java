/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>

 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.demlinks.references;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.demlinks.node.Position;
import org.junit.Before;
import org.junit.Test;

public class RefsListTest {
	
	RefsList<Object>	refList;
	Object				obj1, obj2;
	Reference<Object>	ref1, ref2;
	
	@Before
	public void init() {
		this.refList = new RefsList<Object>();
		this.obj1 = new Object();
		this.obj2 = new Object();
		this.ref1 = new Reference<Object>();
		this.ref2 = new Reference<Object>();
		this.ref1.setObject( this.obj1 );
		assertTrue( this.ref1.getObject() == this.obj1 );
		this.ref2.setObject( this.obj2 );
		assertTrue( this.ref2.getObject() == this.obj2 );
	}
	
	@Test
	public void testInsert() {
		assertFalse( this.refList.addFirst( this.ref1 ) );
		assertFalse( this.refList.insertObjAt( this.ref2, Position.AFTER,
				this.ref1 ) );
		Reference<Object> ref3 = new Reference<Object>();
		assertFalse( this.refList
				.insertObjAt( ref3, Position.BEFORE, this.ref1 ) );
		assertTrue( this.refList.getFirstRef() == ref3 );
		assertTrue( this.refList.getLastRef() == this.ref2 );
		assertTrue( this.refList.getRefAt( Position.BEFORE, this.ref2 ) == this.ref1 );
		assertTrue( this.refList.size() == 3 );
		assertTrue( this.refList.removeRef( ref3 ) );
		boolean excepted = false;
		try {
			// even though ref2 exists, while ref3 doesn't, the call is bugged
			// so we alert:
			// ref3 should exist
			this.refList.insertObjAt( this.ref2, Position.BEFORE, ref3 );// 3rd
																			// param,
																			// not
																			// exists
		} catch ( NoSuchElementException e ) {
			excepted = true;
		}
		assertTrue( excepted );
	}
	
	@Test
	public void testSomething() throws Exception {
		assertTrue( this.refList.isEmpty() );
		assertFalse( this.refList.addLast( this.ref1 ) );
		assertTrue( this.refList.containsRef( this.ref1 ) );
		assertTrue( this.refList.addLast( this.ref1 ) );
		assertTrue( this.refList.containsRef( this.ref1 ) );
		int mod = this.refList.getModified();
		assertFalse( this.refList.addLast( this.ref2 ) );
		assertTrue( mod != this.refList.getModified() );
		mod = this.refList.getModified();
		assertTrue( this.refList.containsRef( this.ref2 ) );
		assertFalse( mod != this.refList.getModified() );
		assertTrue( this.refList.addLast( this.ref2 ) );
		assertTrue( this.refList.addLast( this.ref1 ) );// after this call ref1
														// mustn't move
		assertFalse( mod != this.refList.getModified() );
		// from first position
		assertTrue( this.refList.getFirstRef() == this.ref1 );
		assertTrue( this.refList.getLastRef() == this.ref2 );
		assertTrue( this.refList.getRefAt( Position.FIRST ) == this.refList
				.getFirstRef() );
		assertTrue( this.refList.getRefAt( Position.LAST ) == this.refList
				.getLastRef() );
		assertTrue( this.refList.getRefAt( Position.AFTER, this.ref1 ) == this.ref2 );
		assertTrue( this.refList.getRefAt( Position.BEFORE, this.ref2 ) == this.ref1 );
		assertFalse( mod != this.refList.getModified() );
		Reference<Object> ref3 = new Reference<Object>();
		ref3.setObject( null );
		assertTrue( ref3.isDead() );
		assertFalse( this.refList.addLast( ref3 ) );// null objects can be added
													// in this
		// list level
		Reference<Object> ref0 = new Reference<Object>();
		ref0.setObject( null );
		assertTrue( ref0.isAlone() );
		assertFalse( this.refList.addFirst( ref0 ) );
		assertTrue( this.refList.getFirstRef() == ref0 );
		assertTrue( this.refList.size() == 4 );
		boolean excepted = false;
		try {
			this.refList.addFirst( null );
		} catch ( NullPointerException e ) {// yeah stupid, I know :)
			excepted = true;
		}
		assertTrue( excepted );
	}
}
