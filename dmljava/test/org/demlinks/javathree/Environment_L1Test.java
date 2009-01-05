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

import org.junit.Test;

public class Environment_L1Test {

	@Test
	public void testLink() {
		Environment_L1 env1 = new Environment_L1();
		Node a = new Node();
		Node b = new Node();
		Node _a = a;
		Node _b = b;
		Node nul = null;
		
		assertFalse( env1.isLink(_a, _b) );
		assertTrue(env1.link(a, b));
		assertFalse(env1.link(_a, _b));//already exists
		assertTrue( env1.isLink(_a, _b) );
		assertTrue(a.isLinkForward(b));
		assertTrue(b.isLinkBackward(a));
		
		boolean npe=false;
		try {
			env1.link(a, nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			env1.link(nul, a);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			env1.link(nul, nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			env1.isLink(a, nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			env1.isLink(nul, b);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			env1.isLink(nul, nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		
		assertTrue( a.unLinkForward(b) );//"creating" inconsistent link
		assertFalse(a.isLinkForward(b));
		assertTrue(b.isLinkBackward(a));
		boolean err=false;
		try {
			env1.isLink(a, b);
		}catch (Error e) {
			err=true;
		}
		assertTrue(err);
		
		err=false;
		try {
			env1.isLink(b, a);
		}catch (Error e) {
			err=true;
		}
		assertFalse(err);
		
		
	}

}
