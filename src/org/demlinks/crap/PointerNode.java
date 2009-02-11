
package org.demlinks.crap;

import org.demlinks.debug.Debug;
import org.demlinks.exceptions.BugDetected;
import org.demlinks.exceptions.InconsistentLinkDetected;

/**
 * can have 0 or max 1 children can have any number of parents MUST have the
 * node GlobalPointers.AllPointers as parent (full link ie. this is child to
 * that, also)
 */
public class PointerNode extends Node {
	
	public PointerNode() {
		super();
		boolean existsAlready = false;
		try {
			existsAlready = GlobalNodes.AllPointers.appendChild( this );
		} catch ( InconsistentLinkDetected e ) {// half of the link exists
												// already
			existsAlready = true;// so we can throw
		} finally {
			if ( existsAlready ) {
				throw new IllegalAccessError(
						"AllPointers->this already existing?!" );
			}
		}
	}
	
	private void integrityCheck() throws BugDetected {
		if ( this.numChildren() > 1 ) {
			throw new BugDetected(
					"someone made the pointer have more than 1 child" );
		}
	}
	
	/**
	 * @param pointee
	 *            childNode to point to
	 * @return true if the pointee was already pointed by this pointer, nothing
	 *         changed
	 * @throws BugDetected
	 *             if more than 1 child detected OR other 2 reasons
	 * @throws InconsistentLinkDetected
	 *             half link detected
	 */
	public boolean pointTo( Node pointee ) throws BugDetected,
			InconsistentLinkDetected {
		Debug.nullException( pointee );
		this.integrityCheck();
		if ( this.numChildren() == 1 ) {
			// already have a pointer
			Node tmp = this.getLastChild();
			if ( tmp == pointee ) {
				return true;// already has the pointee we wanted to set
			}
			if ( null == tmp ) {
				throw new BugDetected( "can't be null here" );
			}
			if ( !this.removeChild( tmp ) ) {
				throw new BugDetected( "should've removed it!" );
			}
		}
		// we're here the pointer points to nothing, has no child
		if ( this.appendChild( pointee ) ) {
			throw new BugDetected(
					"couldn't've already existed, maybe bug in appendChild?!" );
		}
		return false;
	}
	
	public Node getPointee() {
		return this.getLastChild();
	}
}
