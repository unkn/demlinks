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

package org.demlinks.javatwo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NodeRef_L1Test {

	NodeRef_L1 n1,n2,n3;
	
	@Before
	public void init() {
		n1 = new NodeRef_L1();
		n2 = new NodeRef_L1();
		n3 = new NodeRef_L1();
	}
	
	@Test
	public void testNodeRef_L1() {
		assertTrue( n1.getPrevNodeRef() == null );
		assertTrue( n1.getNextNodeRef() == null );
		assertTrue(n1.isAlone());
		
		assertTrue( n2.setNextNodeRef(n3) );
		assertTrue(n2.getNextNodeRef() == n3);
		assertTrue(n3.getPrevNodeRef() == n2);
		
		assertTrue( n2.setPrevNodeRef(n1) );
		assertTrue(n2.getPrevNodeRef() == n1);
		assertTrue(n1.getNextNodeRef() == n2);
		
		assertTrue( n2.selfRemove() );
		assertTrue( n1.selfRemove() );
		assertTrue( n3.selfRemove() );
	}

}
