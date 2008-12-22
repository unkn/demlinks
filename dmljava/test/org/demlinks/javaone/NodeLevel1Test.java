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


public class NodeLevel1Test {
	
	NodeLevel1 a,b,c;
	NodeLevel0 a0,b0,c0;
	
	@Before
	public void init() {
		a=new NodeLevel1();
		b=new NodeLevel1();
		c=new NodeLevel1();
		a0=(NodeLevel0)a;
		b0=b;
		c0=c;
	}
	
	@Test
	public void testLinkTo() {
		
		assertTrue( a.linkTo(b) );//L1
		assertTrue( a0.isLinkTo(b));//calls L0 method
		assertTrue( a0.isLinkTo(b0));//calls L0 method
		assertTrue( b0.isLinkFrom(a));//calls L0 method
		assertTrue( b0.isLinkFrom(a0));//calls L0 method
		assertTrue(b.isLinkFrom(a));//L1
		assertTrue(a.isLinkTo(b));//L1
		
		assertTrue( c.linkFrom(a) );
		assertTrue(a.isLinkTo(c));
		assertTrue(c.isLinkFrom(a));
		
		
		
		assertTrue( a.unLinkTo(c) );
		assertFalse(a.isLinkTo(c));
		assertFalse(c.isLinkFrom(a));
		
		assertTrue( b.unLinkFrom(a) );
		assertFalse(b.isLinkFrom(a));
		assertFalse(a.isLinkTo(b));
		
		assertTrue(a.isAlone());
		assertTrue(b.isAlone());
		assertTrue(c.isAlone());
	}
}
