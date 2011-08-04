/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import static org.junit.Assert.*;

import org.demlinks.node.*;
import org.junit.*;
import org.q.*;



public class ChildlessNodeTest {
	
	ChildlessNode	p1;
	
	
	@Before
	public void init() {
		
		p1 = new ChildlessNode();
	}
	
	
	@Test
	public void testChildlessNode() {
		
		assertTrue( p1.numChildren() == 0 );
		// TODO
		
		boolean ex = false;
		try {
			p1.appendChild( new Node() );
		} catch ( final BadCallError e ) {
			ex = true;
		}
		assertTrue( ex );
		
		final Node p2 = new Node();
		assertFalse( p1.appendParent( p2 ) );
		p1.integrityCheck();
		assertTrue( p1.numChildren() == 0 );
	}
}
