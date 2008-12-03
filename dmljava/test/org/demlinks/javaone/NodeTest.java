/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@sourceforge.net>
 	
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

import java.util.Iterator;

import org.junit.Test;


public class NodeTest {
	@Test
	public void testNode() {
		Node a = new Node();
		Node b = new Node();
		a.linkTo(b);
		checkIntegrity(a);
		checkIntegrity(b);
		assertTrue(a.isLinkTo(b));
		assertTrue(b.isLinkFrom(a));
		b.unlinkFrom(a);
		assertFalse(a.isLinkTo(b));
		assertFalse(b.isLinkFrom(a));
		//checkIntegrity(a);
		//checkIntegrity(b);
//		System.out.println(a.toString());
//		System.out.println(b.toString());
	}
	
	public void checkIntegrity(Node masterNode) {
		// check if both lists are null/empty
		assertFalse ( ((masterNode.parentsList == null) || (masterNode.parentsList.isEmpty())) &&
				((masterNode.childrenList == null) || (masterNode.childrenList.isEmpty())) );
		
		// parse parentsList; parent <- masterNode
		if (masterNode.parentsList != null) {
			Iterator<Node> itr = masterNode.parentsList.iterator();
			while (itr.hasNext()) {
				Node parentNode = itr.next();
				assertTrue(parentNode.isLinkTo(masterNode));
				assertTrue(masterNode.isLinkFrom(parentNode));
			}
		}
		
		//parse childrenList;  masterNode -> child
		if (masterNode.childrenList != null) {
			Iterator<Node> itr = masterNode.childrenList.iterator();
			while (itr.hasNext()) {
				Node childNode = itr.next();
				assertTrue(childNode.isLinkFrom(masterNode));
				assertTrue(masterNode.isLinkTo(childNode));
			}
		}
	}
}
