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


package org.references;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class ReferenceTest {
	
	Reference<Object>	n1, n2, n3;
	Object				obj1;
	
	@Before
	public void init() {

		n1 = new Reference<Object>();
		n2 = new Reference<Object>();
		n3 = new Reference<Object>();
		obj1 = new Object();
	}
	
	@Test
	public void testSetObject() {

		assertTrue( n1.getObject() == null );
		n1.setObject( obj1 );
		assertTrue( obj1 == n1.getObject() );
		n1.setObject( null );
		assertTrue( null == n1.getObject() );
	}
	
	@Test
	public void testSetNext() {

		assertTrue( n1.getPrev() == null );
		assertTrue( n1.getNext() == null );
		assertTrue( n1.isAlone() );
		assertTrue( n1.isDead() );
		assertTrue( n2.isAlone() );
		assertTrue( n2.isDead() );
		n2.setNext( n3 );
		assertFalse( n2.isAlone() );
		assertFalse( n2.isDead() );
		assertTrue( n2.getNext() == n3 );
		assertFalse( n3.getPrev() == n2 );
		n2.setPrev( n1 );
		assertTrue( n2.getPrev() == n1 );
		assertFalse( n1.getNext() == n2 );
		n1.setNext( n2 );
		assertTrue( n1.getNext() == n2 );
	}
}
