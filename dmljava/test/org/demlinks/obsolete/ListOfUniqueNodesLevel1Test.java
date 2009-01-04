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

package org.demlinks.obsolete;

import static org.junit.Assert.*;

import org.demlinks.obsolete.List;
import org.demlinks.obsolete.ListOfUniqueNodesLevel1;
import org.demlinks.obsolete.NodeLevel1;
import org.junit.Before;
import org.junit.Test;


public class ListOfUniqueNodesLevel1Test {
	
	NodeLevel1 n1,d1;
	ListOfUniqueNodesLevel1 list1;
	
	@Before
	public void init() {
		n1 = new NodeLevel1();
		list1 = (ListOfUniqueNodesLevel1) n1.get(List.CHILDREN);
		d1 = new NodeLevel1();
	}
	
	@Test
	public void testAddLast() throws Exception {
		assertTrue( list1.addLast(d1) );
		assertTrue(list1.getSize() == 1);
		
	}
}
