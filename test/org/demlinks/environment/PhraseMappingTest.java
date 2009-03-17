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


package org.demlinks.environment;



import static org.junit.Assert.assertTrue;

import org.demlinks.node.Node;
import org.demlinks.nodemaps.PhraseNode;
import org.junit.Before;
import org.junit.Test;



public class PhraseMappingTest {
	
	PhraseMapping	pm;
	
	@Before
	public void init() {

		this.pm = new PhraseMapping();
	}
	
	@Test
	public void testAddPhrase() {

		PhraseNode pn = this.pm.addPhrase( "Not yet implemented" );
		System.out.println( pn.numChildren() );
		Node not = this.pm.getNodeForWord( "Not" );
		assertTrue( not.numChildren() == 1 );
		Node yet = this.pm.getNodeForWord( "yet" );
		assertTrue( yet.numChildren() == 1 );
		Node implemented = this.pm.getNodeForWord( "implemented" );
		assertTrue( implemented.numChildren() == 1 );
	}
	
}
