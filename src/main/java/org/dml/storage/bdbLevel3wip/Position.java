/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dml.storage.bdbLevel3wip;

import org.q.*;



public enum Position {
		FIRST,
		LAST,
		BEFORE,
		AFTER;
	
	/**
	 * @param whichPosition
	 *            the position to be checked against the list
	 * @param posList
	 *            non-null, non-empty list of allowed positions
	 * @return true if the position is in list
	 */
	public static boolean isInList( final Position whichPosition, final Position... posList ) {
		
		assert null != whichPosition;
		assert null != posList;// no null array aka posList must exist
		assert posList.length > 0;
		boolean found1 = false;
		for ( final Position currentPos : posList ) {
			assert null != currentPos;// no null parameters!
			if ( whichPosition == currentPos ) {
				found1 = true;
			}
		}
		return found1;
	}
	
	
	/**
	 * @param pos
	 * @return
	 */
	public static Position opposite( final Position pos ) {
		assert null != pos;
		switch ( pos ) {
		case FIRST:
			return LAST;
		case LAST:
			return FIRST;
		case BEFORE:
			return AFTER;
		case AFTER:
			return BEFORE;
			
		default:
			Q.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
	
	
	public static Position getAsEdge( final Position pos ) {
		assert null != pos;
		switch ( pos ) {
		case BEFORE:
			return FIRST;
		case AFTER:
			return LAST;
		case LAST:
		case FIRST:
			Q.badCall( "already edge" );
			break;
		default:
			Q.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
	
	
	/**
	 * @param pos
	 * @return
	 */
	public static Position getAsNear( final Position pos ) {
		assert null != pos;
		switch ( pos ) {
		case FIRST:
			return BEFORE;
		case LAST:
			return AFTER;
		case BEFORE:
		case AFTER:
			Q.badCall( "already near" );
			break;
		default:
			Q.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
}
