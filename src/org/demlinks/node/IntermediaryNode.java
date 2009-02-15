
package org.demlinks.node;

import org.demlinks.errors.BugError;
import org.demlinks.exceptions.InconsistentLinkException;


public class IntermediaryNode extends Node {
	
	public IntermediaryNode() {
		super();
		boolean existsAlready = false;
		try {
			existsAlready = GlobalNodes.AllIntermediaryNodes.appendChild( this );
		} catch ( InconsistentLinkException e ) {// half of the link exists
			// already
			existsAlready = true;// so we can throw
		} finally {
			if ( existsAlready ) {
				throw new BugError(
						"AllIntermediaryNodes->this already existing?!" );
			}
		}
	}
	
	
	// public NodeWithDupChildren imGetParent() {//we can't know for sure who's
	// parent
	// // TODO Auto-generated method stub
	// return null;
	// }
	
	@Override
	public void integrityCheck() {
		if ( this.numChildren() > 1 ) {
			throw new BugError(
					"someone made the pointer have more than 1 child" );
		}
		if ( !this.hasParent( GlobalNodes.AllIntermediaryNodes ) ) {
			// TODO if above throws InconsistentLinkException we should still
			// throw BugError
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
