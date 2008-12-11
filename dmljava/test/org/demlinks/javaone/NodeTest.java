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

package org.demlinks.javaone;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;


public class NodeTest {
	
	private Environment env;
	
	@Before
	public void init() {
		env = new Environment();
	}
	
	@Test
	public void testNode() throws Exception {

		//TODO: tests need to be remade, in both units
		Node a = new Node(env);
		Node b = new Node(env);
		Node c = new Node(env);
		a.linkTo(b);
		a.linkTo("somename");
		assertTrue(a.isLinkTo(b));
		assertFalse(b.isLinkFrom(a));
		b.unlinkFrom(a);
		assertTrue(a.isLinkTo(b));
		assertFalse(b.isLinkFrom(a));
		a.unlinkTo(b);
		assertFalse(a.isLinkTo(b));

		a.linkTo(b);
		b.linkTo(c);
		c.linkTo(a);
		b.linkTo(a);
		assertFalse(a.isLinkFrom(b));
		assertTrue(a.isLinkTo(b));
		assertFalse(a.isLinkTo(c));
		assertFalse(a.isLinkFrom(c));
		//a.getChildrenList().add
		/*ListIterator<Node> itr = a.getChildrenListIterator();
		//System.out.println(itr.toString());
		itr.add(c);
		itr.previous();
		while(itr.hasNext()) {
			System.out.println(itr.next()+".");
		}*/
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}
	
}
