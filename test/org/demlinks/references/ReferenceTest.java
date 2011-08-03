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

import org.junit.Before;
import org.junit.Test;



public class ReferenceTest {
	
	Reference<Object>	n1, n2, n3;
	Object				obj1;
	
	@Before
	public void init() {
	
		this.n1 = new Reference<Object>();
		this.n2 = new Reference<Object>();
		this.n3 = new Reference<Object>();
		this.obj1 = new Object();
	}
	
	@Test
	public void testSetObject() {
	
		assertTrue( this.n1.getObject() == null );
		this.n1.setObject( this.obj1 );
		assertTrue( this.obj1 == this.n1.getObject() );
		this.n1.setObject( null );
		assertTrue( null == this.n1.getObject() );
	}
	
	@Test
	public void testSetNext() {
	
		assertTrue( this.n1.getPrev() == null );
		assertTrue( this.n1.getNext() == null );
		assertTrue( this.n1.isAlone() );
		assertTrue( this.n1.isDead() );
		assertTrue( this.n2.isAlone() );
		assertTrue( this.n2.isDead() );
		this.n2.setNext( this.n3 );
		assertFalse( this.n2.isAlone() );
		assertFalse( this.n2.isDead() );
		assertTrue( this.n2.getNext() == this.n3 );
		assertFalse( this.n3.getPrev() == this.n2 );
		this.n2.setPrev( this.n1 );
		assertTrue( this.n2.getPrev() == this.n1 );
		assertFalse( this.n1.getNext() == this.n2 );
		this.n1.setNext( this.n2 );
		assertTrue( this.n1.getNext() == this.n2 );
	}
}
