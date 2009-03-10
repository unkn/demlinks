

package org.demlinks.environment;



import static org.junit.Assert.assertTrue;

import org.demlinks.node.NodeList;
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
		NodeList not = this.pm.getNodeForWord( "Not" );
		assertTrue( not.size() == 1 );
		NodeList yet = this.pm.getNodeForWord( "yet" );
		assertTrue( yet.size() == 1 );
		NodeList implemented = this.pm.getNodeForWord( "implemented" );
		assertTrue( implemented.size() == 1 );
	}
	
}
