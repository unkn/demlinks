

package org.demlinks.environment;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.node.Node;
import org.demlinks.node.NodeList;
import org.demlinks.nodemaps.IntermediaryNode;
import org.demlinks.nodemaps.WordNode;
import org.junit.Before;
import org.junit.Test;



public class WordMappingTest {
	
	WordMapping	wm;
	
	@Before
	public void init() {

		this.wm = new WordMapping();
	}
	
	@Test
	public void testAddWord() {

		
		WordNode actah = this.wm.addWord( "actah" );
		assertTrue( null != actah );
		assertTrue( actah.numChildren() == 5 );
		assertTrue( actah.dupGetFirstChild() == this.wm.getNodeForChar( 'a' ) );
		assertTrue( actah.dupGetLastChild() == this.wm.getNodeForChar( 'h' ) );
		
		NodeList nl = this.wm.getNodeForWord( "actah" );
		assertFalse( nl.isEmpty() );
		assertTrue( nl.getFirstNode() == actah );
		assertTrue( nl.size() == 1 );
		

		nl = this.wm.getNodeForWord( "jkl" );
		assertTrue( nl.isEmpty() );
		
		nl = this.wm.getNodeForWord( "ac" );
		assertTrue( nl.isEmpty() );
		nl = this.wm.getNodeForWord( "a" );
		assertTrue( nl.isEmpty() );
		
		// nl = this.wm.getNodeForWord( "actah" );
		// assertFalse( nl.isEmpty() );
		// System.out.println( nl.size() );
		// assertTrue( actah == this.wm.addWord( "actah" ) );
		
		WordNode act = this.wm.addWord( "act" );
		WordNode ah = this.wm.addWord( "ah" );
		WordNode ac = this.wm.addWord( "ac" );
		WordNode ta = this.wm.addWord( "ta" );
		WordNode h = this.wm.addWord( "h" );
		// WordNode a = this.wm.addWord( "a" );
		
		// nl = this.wm.getNodeForWord( "ac" );
		// assertTrue( nl.isEmpty() );
		nl = this.wm.getNodeForWord( "a" );
		assertTrue( nl.isEmpty() );
		nl = this.wm.getNodeForWord( "h" );
		assertFalse( nl.isEmpty() );
		assertTrue( nl.size() == 1 );
		
		WordNode newactah = new WordNode();
		newactah.dupAppendChild( act );
		newactah.dupAppendChild( ah );
		
		WordNode actah2 = new WordNode();
		actah2.dupAppendChild( this.wm.ensureNodeForChar( 'a' ) );
		actah2.dupAppendChild( this.wm.ensureNodeForChar( 'c' ) );
		actah2.dupAppendChild( this.wm.ensureNodeForChar( 't' ) );
		actah2.dupAppendChild( this.wm.ensureNodeForChar( 'a' ) );
		actah2.dupAppendChild( this.wm.ensureNodeForChar( 'h' ) );
		
		nl = this.wm.getNodeForWord( "actah" );
		assertFalse( nl.isEmpty() );
		System.out.println( nl.size() );
		assertTrue( nl.getFirstNode() == actah );
		assertTrue( nl.getNodeAfter( actah ) == newactah );
		assertTrue( nl.getNodeAfter( newactah ) == actah2 );
		assertTrue( nl.size() == 3 );// solutions
		WordNode actah3 = new WordNode();
		actah3.dupAppendChild( ac );
		WordNode tah = new WordNode();
		tah.dupAppendChild( ta );
		tah.dupAppendChild( h );
		actah3.dupAppendChild( tah );
		
		nl = this.wm.getNodeForWord( "actah" );
		assertFalse( nl.isEmpty() );
		System.out.println( nl.size() );
		assertTrue( nl.size() == 4 );
		assertTrue( nl.getNodeBefore( actah2 ) == actah3 );
	}
	
	@Test
	public void testGetNextIntermediaryNodeForNodeAt() {

		WordNode w = new WordNode();
		Node norm1 = new Node();
		Node dummy1 = new Node();
		
		w.dupAppendChild( norm1 );
		
		assertTrue( w.getIntermediaryForFirstChild() == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		

		w.dupAppendChild( dummy1 );
		assertTrue( w.getIntermediaryForFirstChild() == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		
		// assertTrue( w.removeChild( w.getIntermediaryForFirstChild() ) ); bug
		IntermediaryNode inNorm1 = w.getIntermediaryForFirstChild();
		w.dupRemoveIntermediaryNode( inNorm1 );
		assertFalse( w.dupHasChild( norm1 ) );
		assertFalse( w.hasChild( inNorm1 ) );
		
		w.dupAppendChild( norm1 );
		assertFalse( w.getIntermediaryForFirstChild() == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		assertTrue( w.getIntermediaryForLastChild() == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, null ) );
		assertTrue( w.getIntermediaryForLastChild().getPointee() == norm1 );
		
		WordNode w2 = new WordNode();
		w2.dupAppendChild( new Node() );
		
		assertTrue( null == this.wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w.getIntermediaryForLastChild() ) );
		
		w2.dupAppendChild( norm1 );
		w2.dupAppendChild( new Node() );
		assertTrue( w2.getIntermediaryForFirstChild( norm1 ) == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w.getIntermediaryForLastChild() ) );
		
		assertTrue( null == this.wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		WordNode w3 = new WordNode();
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( norm1 );
		
		assertTrue( null == this.wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		
		assertTrue( null == this.wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		assertTrue( w3.getIntermediaryForFirstChild( norm1 ) == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		assertFalse( w3.getIntermediaryForLastChild( norm1 ) == this.wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
	}
}
