

package org.demlinks.nodemaps;


public class PhraseNode extends NodeWithDupChildren {
	
	// TODO
	public PhraseNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllPhraseNodes );
	}
	

}
