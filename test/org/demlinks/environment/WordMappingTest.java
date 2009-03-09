

package org.demlinks.environment;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.demlinks.node.Node;
import org.demlinks.nodemaps.IntermediaryNode;
import org.demlinks.nodemaps.WordNode;
import org.junit.Test;



public class WordMappingTest {
	
	@Test
	public void testAddWord() {

		fail( "Not yet implemented" );
	}
	
	@Test
	public void testGetNodeForWord() {

		fail( "Not yet implemented" );
	}
	
	@Test
	public void testGetNextIntermediaryNodeForNodeAt() {

		WordNode w = new WordNode();
		Node norm1 = new Node();
		Node dummy1 = new Node();
		
		w.dupAppendChild( norm1 );
		
		WordMapping wm = new WordMapping();
		assertTrue( w.getIntermediaryForFirstChild() == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		

		w.dupAppendChild( dummy1 );
		assertTrue( w.getIntermediaryForFirstChild() == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		
		// assertTrue( w.removeChild( w.getIntermediaryForFirstChild() ) ); bug
		IntermediaryNode inNorm1 = w.getIntermediaryForFirstChild();
		w.dupRemoveIntermediaryNode( inNorm1 );
		assertFalse( w.dupHasChild( norm1 ) );
		assertFalse( w.hasChild( inNorm1 ) );
		
		w.dupAppendChild( norm1 );
		assertFalse( w.getIntermediaryForFirstChild() == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 0, null ) );
		assertTrue( w.getIntermediaryForLastChild() == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, null ) );
		assertTrue( w.getIntermediaryForLastChild().getPointee() == norm1 );
		
		WordNode w2 = new WordNode();
		w2.dupAppendChild( new Node() );
		
		assertTrue( null == wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w.getIntermediaryForLastChild() ) );
		
		w2.dupAppendChild( norm1 );
		w2.dupAppendChild( new Node() );
		assertTrue( w2.getIntermediaryForFirstChild( norm1 ) == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w.getIntermediaryForLastChild() ) );
		
		assertTrue( null == wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		WordNode w3 = new WordNode();
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( new Node() );
		w3.dupAppendChild( norm1 );
		w3.dupAppendChild( norm1 );
		
		assertTrue( null == wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		
		assertTrue( null == wm.getNextIntermediaryNodeForNodeAt( norm1, 1,
				w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		w3.dupRemoveIntermediaryNode( w3.getIntermediaryForFirstChild() );
		assertTrue( w3.getIntermediaryForFirstChild( norm1 ) == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
		
		assertFalse( w3.getIntermediaryForLastChild( norm1 ) == wm.getNextIntermediaryNodeForNodeAt(
				norm1, 1, w2.getIntermediaryForFirstChild( norm1 ) ) );
	}
}
