
package org.demlinks.node;

import org.demlinks.errors.BugError;


public class RandomNode extends Node {
	
	public RandomNode() {
		super();
		GlobalNodes.internalCreateNodeAsChildOf( this,
				GlobalNodes.AllRandomNodes );
	}
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		if ( !this.hasParent( GlobalNodes.AllRandomNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
}
