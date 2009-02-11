
package org.demlinks.crap;

import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.InconsistentLinkException;

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
		} catch ( InconsistentLinkException e ) {// half of the link exists
			// already
			existsAlready = true;// so we can throw
		} finally {
			if ( existsAlready ) {
				throw new BugError( "AllPointers->this already existing?!" );
			}
		}
	}
	
	private void integrityCheck() throws BugError {
		if ( this.numChildren() > 1 ) {
			throw new BugError(
					"someone made the pointer have more than 1 child" );
		}
	}
	
	/**
	 * @param pointee
	 *            childNode to point to
	 * @return true if the pointee was already pointed by this pointer, nothing
	 *         changed
	 * @throws BugError
	 *             if more than 1 child detected OR other 2 reasons
	 * @throws InconsistentLinkException
	 *             half link detected
	 */
	public boolean pointTo( Node pointee ) throws BugError,
			InconsistentLinkException {
		Debug.nullException( pointee );
		this.integrityCheck();
		if ( this.numChildren() == 1 ) {
			// already have a pointer
			Node tmp = this.getLastChild();
			if ( tmp == pointee ) {
				return true;// already has the pointee we wanted to set
			}
			if ( null == tmp ) {
				throw new BugError( "can't be null here" );
			}
			if ( !this.removeChild( tmp ) ) {
				throw new BugError( "should've removed it!" );
			}
		}
		// we're here the pointer points to nothing, has no child
		if ( this.appendChild( pointee ) ) {
			throw new BugError(
					"couldn't've already existed, maybe bug in appendChild?!" );
		}
		return false;
	}
	
	/**
	 * @return null or the Node that this pointer points to
	 */
	public Node getPointee() {
		return this.getLastChild();
	}
	
	/**
	 * @return true if was pointing to something before call<br>
	 *         false if was pointing to nothing already
	 * @throws BugError
	 * @throws InconsistentLinkException
	 */
	public boolean setNull() throws InconsistentLinkException, BugError {
		this.integrityCheck();
		if ( this.numChildren() == 1 ) {
			if ( !this.removeChild( this.getPointee() ) ) {
				throw new BugError( "should've returned true" );
			}
			return true;
		}
		return false;
	}
}
