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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class EnvironmentTest {

	Environment env;
	
	@Test
	public void testLink() {
		env = new Environment();
		assertTrue(null == env.getNode("C"));
		env.link("A", "B");
		assertTrue(env.isLink("A","B"));
		assertTrue(env.size() == 2);
		Node _par = env.getNode("A");
		Node _chi = env.getNode("B");
		assertTrue(_chi.isLinkFrom(_par));
		assertTrue(_par.isLinkTo(_chi));
		
		env.unLink("A","B");
		assertTrue(0 == _chi.getChildrenList().size());
		assertTrue(0 == _chi.getParentsList().size());
		assertTrue(_chi.isDead());
		assertTrue(null == env.getNode("B"));
		assertTrue(null == env.getNode("A"));
		
		env.link("A", "B");
		env.link("B", "C");
		assertTrue(env.isLink("A", "B"));
		assertTrue(env.isLink("B", "C"));
		assertFalse(env.isLink("A", "C"));
		assertFalse(env.isLink("B", "A"));
		assertFalse(env.isLink("C", "B"));
		env.unLink("A", "B");
		assertFalse(env.isLink("A", "B"));
		assertTrue(env.isLink("B", "C"));
		assertTrue(env.getID(env.getNode("B")).equals("B"));
		assertTrue(env.getID(env.getNode("C")).equals("C"));
		assertTrue(null == env.getNode("A"));
		
		env.link("AllWords", "dood");
		env.link("dood", "d");
		env.link("dood", "o");
		env.link("dood", "o"); // already exists hehe, no DUPs supported like that
		env.link("dood", "d"); // same here
		assertTrue(2 == env.getNode("dood").getChildrenList().size());
		
		env.link("AllWords", "DOOD");
		env.link("DOOD", "RND_2180");
		env.link("DOOD", "RND_7521");
		env.link("DOOD", "RND_1288");
		env.link("DOOD", "RND_1129");
		env.link("RND_2180", "D");
		env.link("RND_7521", "O");
		env.link("RND_1288", "O");
		env.link("RND_1129", "D");
		assertTrue( env.getNode("DOOD").getChildrenList().size() == 4 );

		parseTree("AllWords",20,"");
		env.link("A", "B");
		env.link("B","C");
		env.link("C", "A");
		parseTree("A",20,"");
	}
	
	public void parseTree(String ID, int downToLevel, String whatWas) {
		whatWas+=ID;
		if  (downToLevel < 0) {
			System.out.println(whatWas+" {max level reached}");
			return;
		}
		Node nod = env.getNode(ID);
		if (null == nod) { // this will never happen (unless first call)
			throw new NoSuchElementException();
		}
		ListIterator<Node> litr = nod.getChildrenList().listIterator();
		if (!litr.hasNext()) { // no more children
			System.out.println(whatWas);
			return;
		}
		while (litr.hasNext()) {
			Node curr = litr.next();
			String id = env.getID(curr);
			parseTree(id, downToLevel - 1, whatWas+"->");
		}
	}
}
