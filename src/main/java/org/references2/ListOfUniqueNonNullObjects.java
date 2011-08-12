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



import org.q.*;
import org.toolza.*;



/**
 * 
 */
/**
 * handles the RefsList list at the Object level ie. accepting only Object
 * parameters where Object is the type of element stored in list; that Object is
 * E DOES NOT allow adding of NULL or DUPlicate Objects<br>
 * DUPlicate objects are those that == OR .equals(), in other words if two
 * different objects ie. a != b, have same contents ie. a.equals(b) == true then
 * if a is already in list, b can't be added (it already exists as a)<br>
 * for that to work, you need to override .equals() or else it defaults to ==
 * 
 * @param <E>
 */
public class ListOfUniqueNonNullObjects<E>
		extends ListOfNonNullObjects<E>
{
	
	public ListOfUniqueNonNullObjects() {
		super();
		assert 0 == size();
	}
	
	
	
	/**
	 * @param obj
	 *            that doesn't already exist; not null
	 * @return the ref
	 */
	@Override
	public ChainedReference<E> addLast( final E obj ) {
		
		assert null != obj;// redundant
		final ChainedReference<E> ref = getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			return ref;
		}
		return super.addLast( obj );
	}
	
	
	/**
	 * @param obj
	 *            that doesn't already exist; not null
	 * @return true if existed and nothing was changed; false if it didn't
	 *         exist, but it does now
	 */
	public boolean addLastQ( final E obj ) {
		
		assert null != obj;// redundant
		ChainedReference<E> ref = getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			assert ( getLastRef() == ref ) : Q.badCall( "existed but in a different place than expected, ie. wasn't Last" );
			// if ( getLastRef() != ref ) {
			// }
			return true;
		}
		
		
		ref = super.addLast( obj );
		assert null != ref;
		assert Z.equals_enforceExactSameClassTypesAndNotNull( ref, getRef( obj ) );
		return false;// didn't exist
	}
	
	
	// public
	// boolean
	// addFirstQ(
	// E obj )
	// {
	//
	// ChainedReference<E> ref = getRef( obj );
	// if ( null != ref )
	// {
	// // already exists, not added/moved
	// if ( getFirstRef() != ref )
	// {
	// Q.badCall( "existed but in a different place than expected, ie. wasn't Last" );
	// }
	// return true;
	// }
	//
	// return false;// didn't exist
	// }
	//
	//
	// /**
	// * @param obj
	// * that doesn't already exist; not allowing null
	// * @return the ref to the object, if it existed; or the new ref if it didn't
	// * exist
	// */
	// @Override
	// public
	// ChainedReference<E>
	// addFirst(
	// E obj )
	// {
	//
	//
	// ChainedReference<E> ref = getRef( obj );
	// if ( null != ref )
	// {
	// // already exists, not added/moved
	// return ref;
	// }
	// return super.addFirst( obj );
	// }
	
	
	/**
	 * @param position
	 * @param obj
	 *            not null, not already existing;
	 * @return true if existed and nothing was changed; false if it didn't
	 *         exist, but it does now
	 */
	public boolean addObjectAtPosition( final Position position, final E obj ) {
		
		assert null != position;
		assert null != obj;
		
		final ChainedReference<E> ref = getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			final ChainedReference<E> existingAtPos = getRefAt( position );
			assert null != existingAtPos;
			if ( Z.equalsByReference_enforceNotNull( ref, existingAtPos ) ) {
				// exists in the expected position
				return true;
			} else {
				// existing in different position
				Q.badCall( "the object `" + obj + "` already existed in a different position!" );
			}
		}
		
		switch ( position ) {
		case FIRST:
			ChainedReference<E> tempRet = super.addFirst( obj );
			assert null != tempRet;
			// );
			break;
		case LAST:
			// ChainedReference<E>
			tempRet = super.addLast( obj );
			// );
			assert null != tempRet;
			break;
		default:
			Q.badCall( "bad position" );
		}
		
		return false;// didn't exist
	}
	
	
	/**
	 * @param newObj
	 * @param pos
	 * @param posObj
	 * @return true if newObj already exists, and nothing is done with it<br>
	 *         false is all went according to call
	 */
	@Override
	public boolean insert( final E newObj, final Position pos, final E posObj ) {
		
		assert null != newObj;
		assert null != pos;
		assert null != posObj;
		final ChainedReference<E> newRef = getRef( newObj );
		if ( null != newRef ) {
			// already exists, not added/moved
			return true;
		}
		
		
		final boolean tempRet = super.insert( newObj, pos, posObj );
		assert !tempRet;
		// );// false
		return false;
	}
	
	
	/**
	 * @param object
	 *            existing object
	 * @return the 0 based index, or throws if not found :))
	 */
	public synchronized int getIndexOfObject( final E object ) {
		assert null != object;
		final ChainedReference<E> existingRef = getRef( object );
		assert ( null != existingRef ) : Q.badCall( "the passed object `" + object + "` was not already in list" );
		// if ( null == existingRef ) {
		
		// return -1;// not reached
		// }
		assert null != existingRef;
		final int index = getIndexOfRef( existingRef );
		assert ( ( index >= 0 ) && ( index < size() ) ) : Q.bug( "bug somewhere, index is invalid" );
		// if ( ( index < 0 ) || ( index >= size() ) ) {
		// }
		return index;
	}
	
	// TODO add replace methods
}
