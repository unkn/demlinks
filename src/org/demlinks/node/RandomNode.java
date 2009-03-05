
package org.demlinks.node;

import org.demlinks.errors.BugError;


public class RandomNode extends Node {
	
	public RandomNode() {
		super();
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllRandomNodes );
	}
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		if ( !this.hasParent( Environment.AllRandomNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
}
