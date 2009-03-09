

package org.demlinks.nodemaps;


public class WordNode extends NodeWithDupChildren {
	
	// TODO
	public WordNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this, Environment.AllWordNodes );
	}
	

}
