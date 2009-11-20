/**
 * File creation: Nov 20, 2009 6:52:32 AM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
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


package org.dml.level3;



import org.dml.tools.RunTime;
import org.references.Position;



/**
 * 
 *
 */
public class ElementCapsule {
	
	/**
	 * @return
	 */
	public boolean isAlone() {

		boolean ret1 = ( getPrevCapsule() == null );
		boolean ret2 = ( getNextCapsule() == null );
		if ( ret1 != ret2 ) {
			RunTime.bug( "they should be same value" );
		}
		return ret1;
	}
	
	/**
	 * @param opposite
	 * @return
	 */
	public ElementCapsule getSideCapsule( Position pos ) {

		switch ( pos ) {
		case FIRST:
		case BEFORE:
			return getPrevCapsule();
			break;
		case LAST:
		case AFTER:
			return getNextCapsule();
			break;
		default:
			RunTime.bug( "shouldn't be here" );
		}
		return null;
	}
	
	/**
	 * @param pos
	 * @param newPointer
	 */
	public void setCapsule( Position pos, ElementCapsule newPointer ) {

		switch ( pos ) {
		case FIRST:
		case BEFORE:
			setNextCapsule( newPointer );
			break;
		case LAST:
		case AFTER:
			setPrevCapsule( newPointer );
			break;
		default:
			RunTime.bug( "cannot reach this" );
		}
	}
	
}
