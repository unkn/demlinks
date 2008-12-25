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

package org.demlinks.javathree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NodeRefTest {

	NodeRef n1,n2,n3;
	Node_L0 nod1;
	
	@Before
	public void init() {
		n1 = new NodeRef();
		n2 = new NodeRef();
		n3 = new NodeRef();
		nod1 = new Node_L0();
	}
	
	@Test
	public void testSetNode() {
		assertTrue(n1.getNode() == null);
		n1.setNode(nod1);
		assertTrue( nod1 == n1.getNode() );
		n1.setNode(null);
		assertTrue(null == n1.getNode());
	}
	
	@Test
	public void testSetNext() {
		assertTrue( n1.getPrev() == null );
		assertTrue( n1.getNext() == null );
		assertTrue(n1.isAlone());
		
		n2.setNext(n3);
		assertTrue(n2.getNext() == n3);
		assertFalse(n3.getPrev() == n2);
		
		n2.setPrev(n1);
		assertTrue(n2.getPrev() == n1);
		assertFalse(n1.getNext() == n2);
		
		n1.setNext(n2);
		assertTrue(n1.getNext() == n2);
		
	}

}
