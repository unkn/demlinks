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

package org.demlinks.javatwo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Node_L0Test {

	Node_L0 a,b,c,nul;
	
	@Before
	public void init() {
		a= new Node_L0();
		b= new Node_L0();
		c= new Node_L0();
		nul=null;
	}

	@Test
	public void testLinkTo() {
		assertTrue(a.isAlone());
		assertTrue( a.linkTo(b) );
		assertFalse(a.isAlone());
		assertFalse(b.isLinkFrom(a));
		assertTrue(a.isLinkTo(b));
		
		assertTrue( c.linkFrom(b) );
		assertTrue(c.isLinkFrom(b));
		assertFalse(b.isLinkTo(c));
		
		boolean npe = false;
		try {
			b.linkTo(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			a.linkFrom(nul);
		}catch (NullPointerException e) {
			npe = true;
		}
		assertTrue(npe);
		
		assertFalse( a.unLinkFrom(c) );
		assertTrue(a.unLinkTo(b));
		assertFalse(a.isLinkTo(b));
		
		assertTrue( c.unLinkFrom(b) );
		assertFalse(c.isLinkFrom(b));
		
		assertTrue(a.isAlone());
		assertTrue(b.isAlone());
		assertTrue(c.isAlone());
		
		npe=false;
		try {
			a.isLinkTo(nul);
		}catch (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
		
		npe=false;
		try {
			a.isLinkFrom(nul);
		}catch (NullPointerException e) {
			npe=true;
		}
		assertTrue(npe);
	}

	@Test
	public void testGet() {
		NodeRefsList_L0 par = a.get(List.PARENTS);
		NodeRefsList_L0 chi = a.get(List.CHILDREN);
		assertTrue(par != null);
		assertTrue(chi != null);
		assertTrue(par != chi);
		assertTrue(par.isEmpty());
		assertTrue(chi.isEmpty());
		a.linkTo(b);
		assertTrue(par.isEmpty());
		assertTrue(chi.getSize() == 1);
		assertTrue(chi.getFirstNode() == b);
		assertTrue(b.get(List.CHILDREN).isEmpty());
		assertTrue(b.get(List.PARENTS).getSize() == 0);
	}

}
