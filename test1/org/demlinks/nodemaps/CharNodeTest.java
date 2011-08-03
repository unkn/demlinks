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


package org.demlinks.nodemaps;



import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class CharNodeTest {
	
	CharNode	p1;
	
	@Before
	public void init() {

		this.p1 = new CharNode();
	}
	
	@Test
	public void testPointerNode() {

		System.out.println( Environment.AllCharNodes.numChildren() );
		assertTrue( Environment.AllCharNodes.hasChild( this.p1 ) );
		assertTrue( this.p1.hasParent( Environment.AllCharNodes ) );
		

	}
}
