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


package org.references2;



import java.util.*;

import org.q.*;
import org.toolza.*;



/**
 * 
 */
/**
 * @param <WHICHOBJ>
 * 
 */
public class ListOfNonNullObjects<WHICHOBJ>
		extends ListOfObjects<WHICHOBJ>
{
	
	/**
	 * not by content comparison ie. not .eQuals() instead it's "=="
	 * 
	 * @param obj
	 * @return true if so
	 */
	@Override
	public boolean containsObject( final WHICHOBJ obj ) {
		
		assert null != obj;
		return super.containsObject( obj );
	}
	
	
	/**
	 * @param obj
	 * @param index
	 *            0 based index
	 * @return true if obj is at index
	 */
	@Override
	public boolean containsObjectAtPos( final WHICHOBJ obj, final int index ) {
		
		assert null != obj;
		return super.containsObjectAtPos( obj, index );
	}
	
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via
	 * this method
	 * 
	 * @param obj
	 * @return new reference to <tt>obj</tt>
	 */
	@Override
	protected ChainedReference<WHICHOBJ> newRef( final WHICHOBJ obj ) {
		
		assert null != obj;// must not be null
		return super.newRef( obj );
	}
	
	
	/**
	 * @param obj
	 * @return null or the reference containing the obj
	 */
	@Override
	public ChainedReference<WHICHOBJ> getRef( final WHICHOBJ obj ) {
		
		assert null != obj;
		return super.getRef( obj );
	}
	
	
	/**
	 * @param pos
	 *            only FIRST/LAST
	 * @return null or the object at specified position
	 */
	@Override
	public WHICHOBJ getObjectAt( final Position pos ) {
		
		assert null != pos;
		WHICHOBJ obj = null;
		try {
			obj = super.getObjectAt( pos );
		} catch ( final Throwable nsee ) {
			if ( Q.isBareException( nsee, NoSuchElementException.class ) ) {
				// Q.markAsHandled( nsee );
				return null;
			} else {
				Q.rethrow( nsee );
			}
		}
		assert null != obj;
		return obj;
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return the object at index, never null
	 */
	@Override
	public WHICHOBJ getObjectAt( final int index ) {
		final WHICHOBJ obj = super.getObjectAt( index );
		assert null != obj;
		return obj;
	}
	
	
	/**
	 * @param pos
	 *            BEFORE/AFTER...of...
	 * @param objPos
	 *            ...which object
	 * @return null or the object
	 */
	public WHICHOBJ getObjectAt( final Position pos, final WHICHOBJ objPos ) {
		
		assert null != pos;
		assert null != objPos;
		
		final ChainedReference<WHICHOBJ> refPos = this.getRef( objPos );
		if ( null == refPos ) {
			// couldn't find objPos
			Q.badCall( "position object not found" );
		}
		// ie. what's the ref that's BEFORE(pos) ref1(refPos) ?
		final ChainedReference<WHICHOBJ> reference = this.getRefAt( pos, refPos );
		WHICHOBJ ret = null;// not found
		if ( null != reference ) {
			ret = reference.getObject();
			assert null != ret;
		}
		return ret;
	}
	
	
	/**
	 * @param obj
	 *            not null; can already exist in list
	 * 
	 * @return the ref
	 */
	@Override
	public ChainedReference<WHICHOBJ> addLast( final WHICHOBJ obj ) {
		
		assert null != obj;
		return super.addLast( obj );
	}
	
	
	/**
	 * @param obj
	 *            non null object that could already exist in list
	 * 
	 * @return the ref to the object
	 */
	@Override
	public ChainedReference<WHICHOBJ> addFirst( final WHICHOBJ obj ) {
		
		assert null != obj;
		return super.addFirst( obj );
	}
	
	
	/**
	 * @return ref to the object that was just added (even if it existed, new
	 *         one added)
	 */
	@Override
	public ChainedReference<WHICHOBJ> insert( final WHICHOBJ obj, final Position position ) {
		
		assert null != obj;
		assert null != position;
		switch ( position ) {
		case FIRST:
			return this.addFirst( obj );
		case LAST:
			return this.addLast( obj );
		default:
			Q.bug( "undefined location here." );
		}
		return null;// not reached
	}
	
	
	/**
	 * @param newObj
	 *            can't be null; can already exist;
	 * @param pos
	 * @param posObj
	 * @return true if already existed in the expected position; false if it didn't but it does now; throws otherwise
	 */
	public boolean insert( final WHICHOBJ newObj, final Position pos, final WHICHOBJ posObj ) {
		
		assert null != newObj;
		assert null != pos;
		assert null != posObj;
		
		final ChainedReference<WHICHOBJ> posRef = this.getRef( posObj );
		if ( null == posRef ) {
			// posObj non existent? stop some bugs by throwing exception
			Q.thro( new NoSuchElementException() );
			assert false : "not reached";
			return false;// not reached!
		}
		assert null != posRef;
		final ChainedReference<WHICHOBJ> existentRef = getRefAt( pos, posRef );
		if ( null != existentRef ) {
			if ( Z.equals_enforceCompatibleClassesAndNotNull( existentRef.getObject(), newObj ) ) {
				return true;// already exists at that expected position
			}
		} else {
			assert ( !containsObject( newObj ) ) : Q.badCall( "object `" + newObj
				+ "` exists already but it's in a different location than this call wanted it to be" );
			// if ( ) {
			
			// }
		}
		// otherwise newObj doesn't exist yet (at least not in that position)
		final ChainedReference<WHICHOBJ> newRef = this.newRef( newObj );
		final boolean ret = insertRefAt( newRef, pos, posRef );
		assert ( !ret ) :
		// if ( true == ret ) {
		// so the reference already exists(not the object pointed to by that ref),
		// and if we're here, it's in a different pos than expected
		Q
			.badCall( "not possible! the reference was `new` it cannot possible have existed! if equals was comparing as it should've by reference ie. ==" );
		// }
		assert !ret;
		return ret;
	}
	
	
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	@Override
	public boolean removeObject( final WHICHOBJ obj ) {
		
		assert null != obj;
		return super.removeObject( obj );
	}
}
