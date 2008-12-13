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
		assertTrue( _a.isLinkTo(new String("b")) );

		
		assertTrue( null == env.getNode("c") ); //inexistent Node
		
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
		
		Node _boo = new Node(env);//a node that's not in environment because it's not mapped to an ID
		try {
			env.link(_boo, _a); //an attempt to link these nodes should fail because one of them is not in the environment/mapped
		} catch (Error e) {
			
		}
		assertFalse(env.isLink(_boo, _a));
		
		
	}
	
	
	
	@Test
	public void testNullParameters() throws Exception {
		
		String nullStr = null;
		Node nullNode = null;
		String fullStr = "something";
		Node fullNode = new Node(env);
		
		boolean excepted=false;
		
		try {
			env.link("a", new Node(env));
		} catch (Error e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(new Node(env),"a");
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
		
		excepted = false;
		try {
			env.link(fullNode, nullStr);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(fullNode, nullNode);
		} catch (Error e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullStr, fullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
		excepted = false;
		try {
			env.link(nullNode, fullNode);
		} catch (NullPointerException e) {
			excepted = true;
		}
		assertTrue(excepted);
		
	}
	
}
