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


package org.demlinks.nodemaps;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;
import org.junit.Before;
import org.junit.Test;



public class EnvironmentTest {
	
	Environment	env;
	
	@Before
	public void init() {

		this.env = new Environment();
	}
	
	@Test
	public void testAllPointers() {

		Node n = new Node();
		Node m = new PointerNode();
		assertFalse( Environment.isPointer( n ) );
		assertTrue( Environment.isPointer( m ) );
	}
	
	@Test
	public void testAllRandomNodes() {

		Node a = new RandomNode();
		Node b = new Node();
		assertTrue( Environment.isRandomNode( a ) );
		assertFalse( Environment.isRandomNode( b ) );
	}
	
	@Test
	public void testAllNodesWithDupChildren() {

		NodeWithDupChildren a = new NodeWithDupChildren();
		assertTrue( Environment.isNodeWithDupChildren( a ) );
		PointerNode b = new PointerNode();
		assertFalse( Environment.isNodeWithDupChildren( b ) );
	}
	
	@Test
	public void testAllIntermediaryNodes() {

		IntermediaryNode a = new IntermediaryNode();
		Node b = new Node();
		PointerNode c = new PointerNode();
		PointerNode d = new IntermediaryNode();
		
		assertTrue( Environment.isIntermediaryNode( a ) );
		assertTrue( Environment.isPointer( a ) );
		
		assertFalse( Environment.isIntermediaryNode( b ) );
		assertFalse( Environment.isIntermediaryNode( c ) );
		assertTrue( Environment.isPointer( c ) );
		assertTrue( Environment.isPointer( d ) );
		assertTrue( Environment.isIntermediaryNode( d ) );
	}
	
	// ------------------------------------ CharNode
	
	@Test
	public void testChar() {

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
	
	// ------------------------------------ WordNode
	
	@Test
	public void testAddWord() {

		assertTrue( Environment.lastSolutionsForLastGottenWord.numChildren() == 0 );
		
		boolean ex = false;
		try {
			this.env.addWord( "" );
		} catch ( BadParameterException e ) {
			ex = true;
		}
		assertTrue( ex );
		
		WordNode actah = this.env.addWord( "actah" );
		assertTrue( null != actah );
		assertTrue( actah.numChildren() == 5 );
		assertTrue( actah.dupGetFirstChild() == this.env.getNodeForChar( 'a' ) );
		assertTrue( actah.dupGetLastChild() == this.env.getNodeForChar( 'h' ) );
		
		Node nl = this.env.getNodeForWord( "actah" );
		// System.out.println( nl.numChildren() );
		assertFalse( nl.numChildren() == 0 );
		assertTrue( nl.getFirstChild() == actah );
		assertTrue( nl.numChildren() == 1 );
		

		nl = this.env.getNodeForWord( "jkl" );
		assertTrue( nl.numChildren() == 0 );
		
		nl = this.env.getNodeForWord( "ac" );
		assertTrue( nl.numChildren() == 0 );
		nl = this.env.getNodeForWord( "a" );
		assertTrue( nl.numChildren() == 0 );
		
		// nl = this.wm.getNodeForWord( "actah" );
		// assertFalse( nl.isEmpty() );
		// System.out.println( nl.size() );
		// assertTrue( actah == this.wm.addWord( "actah" ) );
		
		WordNode act = this.env.addWord( "act" );
		WordNode ah = this.env.addWord( "ah" );
		WordNode ac = this.env.addWord( "ac" );
		WordNode ta = this.env.addWord( "ta" );
		WordNode h = this.env.addWord( "h" );
		// WordNode a = this.wm.addWord( "a" );
		
		// nl = this.wm.getNodeForWord( "ac" );
		// assertTrue( nl.isEmpty() );
		nl = this.env.getNodeForWord( "a" );
		assertTrue( nl.numChildren() == 0 );
		nl = this.env.getNodeForWord( "h" );
		assertFalse( nl.numChildren() == 0 );
		assertTrue( nl.numChildren() == 1 );
		
		WordNode newactah = new WordNode();
		newactah.dupAppendChild( act );
		newactah.dupAppendChild( ah );
		
		WordNode actah2 = new WordNode();
		actah2.dupAppendChild( this.env.ensureNodeForChar( 'a' ) );
		actah2.dupAppendChild( this.env.ensureNodeForChar( 'c' ) );
		actah2.dupAppendChild( this.env.ensureNodeForChar( 't' ) );
		actah2.dupAppendChild( this.env.ensureNodeForChar( 'a' ) );
		actah2.dupAppendChild( this.env.ensureNodeForChar( 'h' ) );
		
		nl = this.env.getNodeForWord( "actah" );
		assertTrue( nl == Environment.lastSolutionsForLastGottenWord );
		assertFalse( nl.numChildren() == 0 );
		// System.out.println( nl.size() );
		assertTrue( nl.getFirstChild() == actah );
		assertTrue( nl.getChildNextOf( actah ) == newactah );
		assertTrue( nl.getChildNextOf( newactah ) == actah2 );
		assertTrue( nl.numChildren() == 3 );// solutions
		WordNode actah3 = new WordNode();
		actah3.dupAppendChild( ac );
		WordNode tah = new WordNode();
		tah.dupAppendChild( ta );
		tah.dupAppendChild( h );
		actah3.dupAppendChild( tah );
		
		nl = this.env.getNodeForWord( "actah" );
		assertFalse( nl.numChildren() == 0 );
		// System.out.println( nl.numChildren() );
		assertTrue( nl.numChildren() == 4 );
		assertTrue( nl.getChildPrevOf( actah2 ) == actah3 );
		assertTrue( nl == Environment.lastSolutionsForLastGottenWord );
		
		WordNode z1 = this.env.addWord( "z" );
		WordNode z2 = this.env.addWord( "zz" );
		
		WordNode z3 = this.env.addWord( "zzz" );
		
		WordNode zz = new WordNode();
		zz.dupAppendChild( z1 );
		zz.dupAppendChild( z1 );
		
		WordNode zzz = new WordNode();
		zzz.dupAppendChild( z1 );
		zzz.dupAppendChild( zz );
		
		WordNode z4 = new WordNode();
		z4.dupAppendChild( z1 );
		z4.dupAppendChild( zz );
		z4.dupAppendChild( z1 );
		
		Node gz = this.env.getNodeForWord( "z" );
		assertTrue( gz.numChildren() == 1 );
		assertTrue( z1 == gz.getFirstChild() );
		Node gz2 = this.env.getNodeForWord( "zz" );
		// System.out.println( gz2.numChildren() );
		assertTrue( gz.numChildren() == 2 );
		assertTrue( zz == gz2.getFirstChild() );
		assertTrue( z2 == gz2.getLastChild() );
		
		Node gz3 = this.env.getNodeForWord( "zzz" );
		// System.out.println( gz3.numChildren() );
		assertTrue( gz3.numChildren() == 2 );
		assertTrue( zzz == gz3.getFirstChild() );
		assertTrue( z3 == gz3.getLastChild() );
		
		Node gz4 = this.env.getNodeForWord( "zzzz" );
		// System.out.println( gz4.numChildren() );
		assertTrue( gz3.numChildren() == 1 );
		assertTrue( z4 == gz4.getFirstChild() );
		
		//
		// WordNode dup = new WordNode();
		// dup.dupAppendChild( this.wm.ensureNodeForChar( 'd' ) );
		// dup.dupAppendChild( dup );
		// this.wm.ensureNodeForChar( 'e' );
		// Node dupsol = this.wm.getNodeForWord( "de" );
		// System.out.println( dupsol.numChildren() );
		
	}
	
	@Test
	public void testGetNextIntermediaryNodeForNodeAt() {

		WordNode w = new WordNode();
		Node norm1 = new Node();
		Node dummy1 = new Node();
		
		w.dupAppendChild( norm1 );
		
		assertTrue( w.getIntermediaryForFirstChild() == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		

		w.dupAppendChild( dummy1 );
		assertTrue( w.getIntermediaryForFirstChild() == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		
		// assertTrue( w.removeChild( w.getIntermediaryForFirstChild() ) ); bug
		IntermediaryNode inNorm1 = w.getIntermediaryForFirstChild();
		w.dupRemoveIntermediaryNode( inNorm1 );
		assertFalse( w.dupHasChild( norm1 ) );
		assertFalse( w.hasChild( inNorm1 ) );
		
		w.dupAppendChild( norm1 );
		assertFalse( w.getIntermediaryForFirstChild() == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		assertTrue( w.getIntermediaryForLastChild() == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 1, null ) );
		assertTrue( w.getIntermediaryForLastChild().getPointee() == norm1 );
		
		WordNode w2 = new WordNode();
		w2.dupAppendChild( new Node() );
		
		assertTrue( null == this.env.getNextIntermediaryNodeForNodeAt( norm1,
				1, w.getIntermediaryForLastChild() ) );
		
		w2.dupAppendChild( norm1 );
		w2.dupAppendChild( new Node() );
		assertTrue( w2.getIntermediaryForFirstChild( norm1 ) == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w.getIntermediaryForLastChild() ) );
		
		assertTrue( null == this.env.getNextIntermediaryNodeForNodeAt( norm1,
				1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		WordNode w3 = new WordNode();
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( norm1 );
		
		assertTrue( null == this.env.getNextIntermediaryNodeForNodeAt( norm1,
				1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		
		assertTrue( null == this.env.getNextIntermediaryNodeForNodeAt( norm1,
				1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		assertTrue( w3.getIntermediaryForFirstChild( norm1 ) == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		assertFalse( w3.getIntermediaryForLastChild( norm1 ) == this.env.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
	}
	
	// ---------------------------------- PhraseNode
	
	@Test
	public void testAddPhrase() {

		PhraseNode pn = this.env.addPhrase( "Not yet implemented..." );
		// System.out.println( pn.numChildren() );
		assertTrue( pn.numChildren() == 8 );
		Node not = this.env.getNodeForWord( "Not" );
		assertTrue( not.numChildren() == 1 );
		Node yet = this.env.getNodeForWord( "yet" );
		assertTrue( yet.numChildren() == 1 );
		Node implemented = this.env.getNodeForWord( "implemented" );
		assertTrue( implemented.numChildren() == 1 );
	}
	

}
