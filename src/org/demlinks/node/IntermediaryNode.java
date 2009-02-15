
package org.demlinks.node;

import org.demlinks.errors.BugError;


public class IntermediaryNode extends Node {
	
	public IntermediaryNode() {
		super();
		GlobalNodes.internalCreateNodeAsChildOf( this,
				GlobalNodes.AllIntermediaryNodes );
	}
	
	
	// public NodeWithDupChildren imGetParent() {//we can't know for sure who's
	// parent
	// 
	// return null;
	// }
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		if ( this.numChildren() > 1 ) {
			throw new BugError(
					"someone made the pointer have more than 1 child" );
		}
		if ( !this.hasParent( GlobalNodes.AllIntermediaryNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
	/**
	 * there can be only 1 child
	 * 
	 * @return THE child
	 */
	public Node getChild() {
		// TODO Auto-generated method stub
		this.integrityCheck();
		return this.getFirstChild();
	}
}
