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



import org.junit.Before;
import org.junit.Test;

public class EnvironmentTest {

	Environment env;
	
	@Before
	public void init() {
		env = new Environment();

		assertTrue(env.size() == 0);
	}
	
	@Test
	public void testisNode() {
		// empty string not allowed
		try {
			env.isNode("");
		} catch (Error e) {
			//e.printStackTrace();
		}
		
		// null Node not allowed
		try {
			Node ca=null;
			env.isNode(ca);
		} catch (Error e) {
			//e.printStackTrace();
		}
		
		//null string not allowed
		try {
			String cas = null;
			System.out.println(env.isNode(cas));
		} catch (Error e) {
			//e.printStackTrace();
		}
		
		//non-existent node "A"
		assertFalse(env.isNode("A"));
	}
	
	@Test
	public void testLink() throws Exception {


		// delete non-existent link
		env.unLink("F","G");
		assertTrue(env.size() == 0);

		// get non-existent node
		assertTrue(null == env.getNode("C"));

		// create a link between two (non-existent) nodes
		env.link("A", "B");
		assertTrue(env.isLink("A","B"));
		assertTrue(env.size() == 2); // two nodes exist
		assertTrue(env.isNode("A"));
		assertTrue(env.isNode("B"));
		
		
		Node _par = env.getNode("A");
		Node _chi = env.getNode("B");
		assertTrue(_chi.isLinkFrom(_par));
		assertTrue(_par.isLinkTo(_chi));
		assertTrue(env.isNode(_par));
		assertTrue(env.isNode(_chi));
		
		env.unLink(_par, _chi);
		//env.unLink("A","B");
		//assertTrue(0 == _chi.getChildrenListSize());
		//assertTrue(0 == _chi.getParentsListSize());
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
		assertTrue(env.link("dood", "d"));
		assertTrue(env.link("dood", "o"));
		assertFalse(env.link("dood", "o")); // false=already exists hehe, no DUPs supported like that
		assertFalse(env.link("dood", "d")); // same here
		//assertTrue(2 == env.getNode("dood").getChildrenListSize());
		
		env.link("AllWords", "DOOD");
		env.link("DOOD", "RND_2180");
		env.link("DOOD", "RND_7521");
		env.link("DOOD", "RND_1288");
		env.link("DOOD", "RND_1129");
		env.link("RND_2180", "D");
		env.link("RND_7521", "O");
		env.link("RND_1288", "O");
		env.link("RND_1129", "D");
		//assertTrue( env.getNode("DOOD").getChildrenListSize() == 4 );

		//parseTree("AllWords",20,"");
		env.link("A", "B");
		env.link("B","C");
		env.link("C", "A");
		parseTree("A",20,"");
		
		Node allWords = env.getNode("AllWords");
		addAllChars();
		parseTree("AllWords",20,"");
		//System.out.print(List.CHILDREN);
		/*
		NodeIterator itr = allWords.getNodeIterator(List.CHILDREN);
		NodeIterator itr = env.getListIterator(allWords,kChildrenList);
		itr.append("a");
		itr.find("a");
		itr.insert("b",kAfter);
		itr.insert("c",kFirst);
		itr.insert("d",kLast);
		itr.insert("e",kBefore); // before "a"
		itr.insert("f",kInstead); // a gets replaced with "f"
		itr.insert(_par,kBefore,_chi); //assuming _chi exists else fails
		itr.insert("g",kAfter,10); // after index 10, overridden method
		itr.insert("h",kBefore,10); // before index 10
		itr.insert("i",kAfter,"a");//this is something the list should have, not the iterator
		//OR the environment even
		env.insert("b",kAfter,"a","AllWords",List.CHILDREN);
		//OR
		env.insert("AllWords",List.CHILDREN,"k", Location.BEFORE,"f");//in children list of AllWords, insert "k" before "f"
		// order now: AllWords->k   , AllWords->f
		Node aw = env.getNode("AllWords");
		aw.insert(List.CHILDREN, "k", kBefore, "f");
		UniqueListOfNodes nl = aw.get(List.CHILDREN);
		nl.insert("k",kBefore,"f");
		*/
		env.getNode("AllWords").get(List.CHILDREN).insert(env.getNode("k"), Location.BEFORE, env.getNode("f"));
		UniqueListOfNodes chiList= allWords.get(List.CHILDREN);
		Node _k = env.getNode("k");
		Node _f	= env.getNode("f");
		chiList.insert(_k, Location.BEFORE, _f);
		NodeIterator ni = chiList.nodeIterator(0);
		ni.find(_f);
		ni.insert(_k, Location.BEFORE);
		
//		String k = String.format("%c",65);
//		String kk = "A";
//		String kkk = "A".toString();
//		String k4 = new String("A");
//		assertTrue(kkk.equals(kk));
//		assertTrue(kkk.equals(k4));
//		assertTrue(kkk.equals(k));
//		
//		//TODO so technically we would need to handle a node as a String ID and as a Node object,wherever such node is to be used
//		env.getNode("AllWords").get(List.CHILDREN).insert("k", Location.BEFORE, "f");
//		env.getNode("AllWords").get(List.CHILDREN).insert("k", Location.BEFORE, _f);
//		env.getNode("AllWords").get(List.CHILDREN).insert("k", Location.BEFORE, "f");
//		env.getNode("AllWords").get(List.CHILDREN).insert(_k, Location.BEFORE, _f);
//		//only "k" may not exist here tho, and will be created, but "f" must exist in that context
//		//so supposedly the above can be reduces to 2 methods:
//		env.getNode("AllWords").get(List.CHILDREN).insert("k", Location.BEFORE, env.getNode("f"));
//		env.getNode("AllWords").get(List.CHILDREN).insert(_k, Location.BEFORE, env.getNode("f"));
//		// but obviously the latter can only be used if _k exists and it's the Node object of "k"
//		//still this implies UniqueListOfNodes to have access to Environment aka env here so it can create the needed new ID-Node
	}
	
	public void addAllChars() throws Exception {
		Node _a = env.getNode("AllWords");
		for (int i = 65; i < 72; i++) {
			env.link(_a,String.format("%c", i));
		}
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
		ListIterator<Node> litr = nod.get(List.CHILDREN).listIterator();
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
