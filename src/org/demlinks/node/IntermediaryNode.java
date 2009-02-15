
package org.demlinks.node;

import org.demlinks.errors.BugError;


public class IntermediaryNode extends PointerNode {
	
	public IntermediaryNode() {
		super();
		GlobalNodes.internalCreateNodeAsChildOf( this,
				GlobalNodes.AllIntermediaryNodes );
	}
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		// if ( this.numChildren() > 1 ) {
		// throw new BugError(
		// "someone made the pointer have more than 1 child" );
		// }
		if ( !this.hasParent( GlobalNodes.AllIntermediaryNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
}
