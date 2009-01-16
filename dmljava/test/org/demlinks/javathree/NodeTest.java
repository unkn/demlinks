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

import org.junit.Before;
import org.junit.Test;

public class NodeTest {

	Node a,b,c,nul;
	
	@Before
	public void init() {
		a= new Node();
		b= new Node();
		c= new Node();
		nul=null;
	}

	@Test
	public void testLinkTo() {
		assertTrue(a.isAlone());
		assertTrue( a.linkForward(b) );
		assertFalse(a.isAlone());
		assertFalse(b.isLinkBackward(a));
		assertTrue(a.isLinkForward(b));
		assertFalse(b.isLinkForward(a));
		
		assertTrue(c.isAlone());
		assertTrue( c.linkBackward(b) );
		assertFalse(c.isAlone());
		assertTrue(c.isLinkBackward(b));
		assertFalse(b.isLinkForward(c));
		assertFalse(b.isLinkBackward(c));
		assertFalse(c.isLinkForward(b));
		
		boolean npe = false;
		try {
			b.linkForward(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			a.linkBackward(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			a.unLinkBackward(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe =false;
		try {
			a.unLinkForward(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		
		assertFalse(a.isLinkBackward(c));
		assertFalse( a.unLinkBackward(c) );
		
		assertTrue(a.isLinkForward(b));
		assertTrue(a.unLinkForward(b));
		assertFalse(a.isLinkForward(b));
		
		
		assertTrue( c.isLinkBackward(b) );
		assertTrue( c.unLinkBackward(b) );
		assertFalse(c.isLinkBackward(b));
		
		assertTrue(a.isAlone());
		assertTrue(b.isAlone());
		assertTrue(c.isAlone());
		
		npe=false;
		try {
			a.isLinkForward(nul);
		}catch (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			a.isLinkBackward(nul);
		}catch (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
	}

	@Test
	public void testGet() {
		NodeRefsList_L2 par = a.getList(List.BACKWARD);
		NodeRefsList_L2 chi = a.getList(List.FORWARD);
		assertTrue(par != null);
		assertTrue(chi != null);
		assertTrue(par != chi);
		assertTrue(par.isEmpty());
		assertTrue(chi.isEmpty());
		assertTrue( a.linkForward(b) );
		assertTrue(par.isEmpty());
		assertTrue(chi.size() == 1);
		assertTrue(chi.getFirstNode() == b);
		assertTrue(b.getList(List.FORWARD).isEmpty());
		assertTrue(b.getList(List.BACKWARD).size() == 0);
	}

}
