/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import org.demlinks.node.*;
import org.q.*;



/**
 * can have 0 or max 1 children can have any number of parents MUST have the
 * node GlobalPointers.AllPointers as parent (full link ie. this is child to
 * that, also)<br>
 * typically when created the pointer is .setNull()
 */
public class PointerNode extends Node {
	
	public PointerNode() {
		
		super();
		Environment.internalEnsureNodeIsChildOf( this, Environment.AllPointerNodes );
	}
	
	
	@Override
	public void integrityCheck() {
		
		super.integrityCheck();
		if ( numChildren() > 1 ) {
			Q.bug( "someone made the pointer have more than 1 child" );
		}
		if ( !hasParent( Environment.AllPointerNodes ) ) {
			Q.bug( "somehow the parent was removed" );
		}
	}
	
	
	/**
	 * @param pointee
	 *            childNode to point to
	 * @return true if the pointee was already pointed by this pointer, nothing
	 *         changed
	 */
	public boolean pointTo( final Node pointee ) {
		
		assert null != pointee;
		
		if ( numChildren() == 1 ) {
			// already have a pointer
			final Node tmp = getLastChild();
			if ( tmp == pointee ) {
				return true;// already has the pointee we wanted to set
			}
			if ( null == tmp ) {
				Q.bug( "can't be null here" );
			}
			if ( !removeChild( tmp ) ) {
				Q.bug( "should've removed it!" );
			}
		}
		// we're here the pointer points to nothing, has no child
		if ( super.appendChild( pointee ) ) {
			Q.bug( "couldn't've already existed, maybe bug in appendChild?!" );
		}
		integrityCheck();
		return false;
	}
	
	
	/**
	 * @return null or the Node that this pointer points to
	 */
	public Node getPointee() {
		
		return getLastChild();
	}
	
	
	/**
	 * @return true if was pointing to something before call<br>
	 *         false if was pointing to nothing already
	 */
	public boolean setNull() {
		
		// at this point, the pointer could point to something that's even from
		// out of domain, if the pointee was removed from domain
		// so integrityCheck()-ing the already existing pointee(from above)
		// would fail
		boolean ret = false;
		if ( numChildren() == 1 ) {
			if ( !removeChild( getPointee() ) ) {
				Q.bug( "should've returned true" );
			}
			ret = true;
		}
		integrityCheck();
		return ret;
	}
	
	
	@Override
	public boolean appendChild( final Node child ) {
		
		throw new BadCallError( "use .pointTo()" );
	}
}
