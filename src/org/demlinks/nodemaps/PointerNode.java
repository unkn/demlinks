/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BadCallError;
import org.demlinks.errors.BugError;
import org.demlinks.node.Node;



/**
 * can have 0 or max 1 children can have any number of parents MUST have the
 * node GlobalPointers.AllPointers as parent (full link ie. this is child to
 * that, also)<br>
 * typically when created the pointer is .setNull()
 */
public class PointerNode extends Node {
	
	public PointerNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllPointerNodes );
	}
	
	@Override
	public void integrityCheck() {

		super.integrityCheck();
		if ( this.numChildren() > 1 ) {
			throw new BugError(
					"someone made the pointer have more than 1 child" );
		}
		if ( !this.hasParent( Environment.AllPointerNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
	/**
	 * @param pointee
	 *            childNode to point to
	 * @return true if the pointee was already pointed by this pointer, nothing
	 *         changed
	 */
	public boolean pointTo( Node pointee ) {

		Debug.nullException( pointee );
		
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
		if ( super.appendChild( pointee ) ) {
			throw new BugError(
					"couldn't've already existed, maybe bug in appendChild?!" );
		}
		this.integrityCheck();
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
	 */
	public boolean setNull() {

		boolean ret = false;
		if ( this.numChildren() == 1 ) {
			if ( !this.removeChild( this.getPointee() ) ) {
				throw new BugError( "should've returned true" );
			}
			ret = true;
		}
		this.integrityCheck();
		return ret;
	}
	
	@Override
	public boolean appendChild( Node child ) {

		throw new BadCallError( "use .pointTo()" );
	}
}
