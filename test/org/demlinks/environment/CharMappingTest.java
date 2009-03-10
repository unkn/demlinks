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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.exceptions.BadParameterException;
import org.demlinks.nodemaps.CharNode;
import org.junit.Before;
import org.junit.Test;



public class CharMappingTest {
	
	CharMapping	env;
	
	@Before
	public void init() {

		this.env = new CharMapping();
	}
	
	@Test
	public void testInit() {

		// assertTrue( Environment.AllCharNodes.numChildren() == 0 );
		char c = 'c';
		assertFalse( this.env.isMappedChar( c ) );
		
		CharNode cn = this.env.mapNewChar( c );
		assertTrue( null != cn );
		
		boolean ex = false;
		try {
			this.env.mapNewChar( c );
		} catch ( BadParameterException e ) {
			ex = true;
		}
		assertTrue( ex );
		
		CharNode cn2 = this.env.ensureNodeForChar( c );
		assertTrue( cn2 != null );
		assertTrue( cn2 == cn );
		

		char d = 'd';
		CharNode dn = this.env.ensureNodeForChar( d );
		assertTrue( dn != null );
		ex = false;
		try {
			this.env.mapNewChar( d );
		} catch ( BadParameterException e ) {
			ex = true;
		}
		assertTrue( ex );
		
		// assertTrue( Environment.AllCharNodes.numChildren() == 2 ); heh static
		
		assertTrue( this.env.getNodeForChar( c ) == cn2 );
		assertTrue( this.env.getNodeForChar( d ) == dn );
		assertTrue( this.env.getNodeForChar( 'd' ) == dn );
		assertTrue( this.env.getNodeForChar( 'c' ) == cn );
		assertTrue( this.env.getNodeForChar( 'e' ) == null );
		assertTrue( this.env.isMappedChar( c ) );
		assertTrue( this.env.isMappedChar( d ) );
		assertFalse( this.env.isMappedChar( 'e' ) );
		
		// TODO more
	}
}
