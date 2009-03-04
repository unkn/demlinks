
package org.demlinks.node;

import org.demlinks.errors.BugError;


/**
 * the Node that's between a parent NodeWithDupChildren and a child as seen
 * through that NodeWithDupChildren <br>
 * 
 * NodeWithDupChildren -> IntermediaryNode -> normalChildNode<br>
 * all intermediary nodes are unique as children of NodeWithDupChildren<br>
 * but normalChildNode can be any Node even if it repeats, ie: <br>
 * NodeWithDupChildren -> RND1 -> A <br>
 * NodeWithDupChildren -> RND1 -> B <br>
 * NodeWithDupChildren -> RND1 -> A (again) <br>
 * NodeWithDupChildren -> RND1 -> C <br>
 */
public class IntermediaryNode extends PointerNode {
	
	public IntermediaryNode() {
		super();
		GlobalNodes.internalCreateNodeAsChildOf( this,
				GlobalNodes.AllIntermediaryNodes );
	}
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		// if you remove extends PointerNode then uncomment:
		// if ( this.numChildren() > 1 ) {
		// throw new BugError(
		// "someone made the pointer have more than 1 child" );
		// }
		if ( !this.hasParent( GlobalNodes.AllIntermediaryNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
}
