

package org.demlinks.nodemaps;


public class WordDelimiterNode extends PointerNode {
	
	// TODO
	public WordDelimiterNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllDelimiterNodes );
	}
}
