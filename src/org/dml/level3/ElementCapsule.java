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



import org.dml.level1.Symbol;
import org.dml.tools.RunTime;
import org.references.Position;



/**
 * 
 *
 */
public class ElementCapsule {
	
	Symbol					name;
	Level3_DMLEnvironment	envL3;
	
	/**
	 * @param env_L3
	 * @param nameOfEC
	 *            is an ElementCapsule already
	 */
	public ElementCapsule( Level3_DMLEnvironment env_L3, Symbol nameOfEC ) {

		RunTime.assumedNotNull( nameOfEC, env_L3 );
		RunTime.assumedTrue( env_L3.isInited() );
		name = nameOfEC;
		envL3 = env_L3;
		this.assumedIsValidCapsule();
	}
	
	/**
	 * @param name2
	 * @return
	 */
	public void assumedIsValidCapsule() {

		// make sure it is an EC ie. AllECs->name
		// it could not have either Prev or Next though
		RunTime.assumedTrue( envL3.isVector( envL3.allElementCapsules_Symbol,
				name ) );
		int size = envL3.countTerminals( name );
		RunTime.assumedTrue( ( size <= 3 ) && ( size >= 0 ) );
	}
	
	/**
	 * @return
	 */
	public boolean isAlone() {

		boolean ret1 = ( this.getSideCapsule( Position.FIRST ) == null );
		boolean ret2 = ( this.getSideCapsule( Position.LAST ) == null );
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
	
	/**
	 * @return
	 */
	public Symbol getAsSymbol() {

		this.assumedIsValidCapsule();
		return name;
	}
	
}
