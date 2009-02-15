
package org.demlinks.node;

import org.demlinks.errors.BugError;
import org.demlinks.exceptions.InconsistentLinkException;


public class RandomNode extends Node {
	
	public RandomNode() {
		super();
		boolean existsAlready = false;
		try {
			existsAlready = GlobalNodes.AllRandomNodes.appendChild( this );
		} catch ( InconsistentLinkException e ) {// half of the link exists
			// already
			existsAlready = true;// so we can throw
		} finally {
			if ( existsAlready ) {
				throw new BugError( "AllRandomNodes->this already existing?!" );
			}
		}
	}
	
	@Override
	public void integrityCheck() {
		// TODO Auto-generated method stub
		super.integrityCheck();
	}
}
