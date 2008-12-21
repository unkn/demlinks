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

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class EnvironmentTest {

	Environment env;
	
	@Before
	public void init() {
		env = new Environment();
	}
	
	

	@Test
	public void testLink() throws Exception {
		
		

		env.link("a", "b"); //link two new nodes "a"->"b"
		
		Node _a = env.getNode("a");//get the Node object who's ID is "a"
		Node _b = env.getNode("b");
		assertTrue(null != _a);
		assertTrue(null != _b);
		
		assertTrue( _a.isLinkTo(_b) );// does link _a -> _b exists ? also implies _a <- _b exists
		//assertTrue( _a.isLinkTo(new String("b")) );
		assertTrue(env.isLink(_a, "b"));

		
		assertTrue( null == env.getNode("c") ); //inexistent Node
		
		//assertTrue( null == env.getID(new Node(env)));
		
		assertTrue( "b".equals(env.getID(_b)));
		assertTrue( env.getID(_a).equals("a"));
		
		env.link(_a,"c"); // link between existing node _a and new node "c"
		assertTrue(env.isLink(_a,"c"));
		
		Node _c = env.getNode("c");
		assertTrue(null != _c);
		
		assertTrue(env.isLink(_a, _c));//same test different identifying ways
		assertTrue(env.isLink("a", _c));//same test different identifying ways
		assertTrue(env.isLink("a", "c"));//same test different identifying ways
		
		env.link(_b,_c); //link two existing nodes
		assertTrue(env.isLink(_b, _c));
		
		env.link("d", _c);//new node "d"
		assertTrue(env.isLink("d", _c));
		
		Node _d = env.getNode("d");
		assertTrue(env.isLink(_d, _c));
		

		// -------------------------
		
		env.link("AllChars","A");
		//Node ac = env.getNode("AllChars");
		//ac.linkTo("B");
		addAllChars();
		
		//UniqueListOfNodes chi = ac.get(List.CHILDREN);
//		chi.append("H");
//		assertTrue(chi.contains("H"));
//		assertTrue(chi.contains("B"));
//		chi.remove("B");

		parseTree("AllChars",20,"");
		parseTree("a",20,"");
		parseTree("d",20,"");

	}
	
	public void addAllChars() throws Exception {
		Node _a = env.getNode("AllChars");
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
		ListOfUniqueNodes list = nod.get(List.CHILDREN);
		Node curr=null;
		curr = list.getObjAt(Location.AFTER, curr);
		if (null == curr) { //no more children
			System.out.println(whatWas);
			return;
		} else {
			do {
				String id = env.getID(curr);
				parseTree(id, downToLevel - 1, whatWas+"->");
				curr = list.getObjAt(Location.AFTER, curr);
			} while (null != curr);
		}

	}

	
	@Test
	public void testUnMappedNode() throws Exception {
		//this will test behavior while using an unmapped new Node() , that is: it has no ID associated with it (at least not in this environment)
		
		//you know, never catch Errors ... in general, if you do here things may remain inconsistent link-wise
		env.link("a","b");
		Node _a = env.getNode("a");
		//Node _b = env.getNode("b");
		
		Node _boo = new Node();//a node that's not mapped ID to Node in the environment
		boolean errored=false;
		try {
			env.link(_boo, _a); //an attempt to link these nodes should fail because one of them is not in the environment/mapped
		} catch (Error e) {
			errored=true;
		}
		assertTrue(errored);
		
		assertFalse(env.isLink(_boo, _a));
//		
//		errored=false;
//		try {
//			_boo.linkTo(_a);//_boo has no corresponding ID ! hence this link should not succeed
//		} catch (Error e) {
//			errored=true;
//		}
//		//assertTrue(errored);
//		assertFalse(env.isLink(_boo, _a)); //_boo wasn't mapped in the environment
//		
		
//		errored=false;
//		try {
//			_a.linkTo(_boo);
//		}catch (Error e) {
//			errored=true;
//		}
//		assertTrue(errored);
//		assertFalse(env.isLink(_a,_boo));
//		
//		errored=false;
//		try {
//			_boo.linkFrom(_b);
//		}catch (Error e) {
//			errored=true;
//		}
//		assertTrue(errored);
//		assertFalse(env.isLink(_b,_boo));
//		
		
//		errored=false;
//		try {
//			_b.linkFrom(_boo);
//		}catch (Error e) {
//			errored=true;
//		}
//		assertTrue(errored);
//		assertFalse(env.isLink(_boo,_b));
//		
//		errored=false;
//		try {
//			_boo.linkTo("boo2");//boo2 is new
//		}catch (Error e) {
//			errored=true;
//		}
//		assertTrue(errored);
//		assertFalse(env.isLink(_boo, "boo2"));
		
//		_b.linkFrom("boo");//ofc "boo" here is a new Node , not _boo
//		assertTrue(env.isLink("boo", _b));
	}
	
	@Test
	public void testNullParameters() throws Exception {
		
		String nullStr = null;
		Node nullNode = null;
		String fullStr = "something";
		Node fullNode = new Node();
		
		boolean excepted=false;
		
		try {
			env.link("a", new Node());
		} catch (Error e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(new Node(),"a");
		} catch (Error e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullStr, nullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted=false;
		try {
			env.link(nullNode, nullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted=false;
		try {
			env.link(nullStr, nullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted=false;
		try {
			env.link(nullNode, nullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(fullStr, nullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(fullStr, nullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullStr, fullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullNode, fullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
//		excepted = false;
//		try {
//			env.link(fullNode, nullStr);
//		} catch (NullPointerException e) {
//			excepted = true;
//		}
//		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(fullNode, nullNode);
		} catch (Error e) {
			excepted = true;
		}
		assertTrue(excepted);
		
//		excepted = false;
//		try {
//			env.link(nullStr, fullNode);
//		} catch (NullPointerException e) {
//			excepted = true;
//		}
//		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullNode, fullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
	}
	
}
