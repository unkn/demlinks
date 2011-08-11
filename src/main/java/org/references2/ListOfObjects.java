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
 * handles the RefsList list at the Object level ie. accepting only Object
 * parameters where Object is the type of element stored in list; that Object is
 * E DOES YES allow adding of NULL or DUPlicate Objects<br>
 * two object THEOBJ are equal if they're either both null or their equals() indicates they are equal and thus, this
 * means that there can be two diff instances of THEOBJ which are .equals()<br>
 * 
 * @param <THEOBJ>
 */
public class ListOfObjects<THEOBJ>
		extends ListOfReferences<THEOBJ>
{
	
	public ListOfObjects() {
		super();
	}
	
	
	/**
	 * doesn't compare by content
	 * 
	 * @param obj
	 * @return true if so
	 */
	public boolean containsObject( final THEOBJ obj ) {
		
		return ( null != this.getRef( obj ) );
	}
	
	
	/**
	 * @param obj
	 * @param index
	 *            0 based index
	 * @return true if obj is at index
	 */
	public boolean containsObjectAtPos( final THEOBJ obj, final int index ) {
		
		final ChainedReference<THEOBJ> ref = getRefAtIndex( index );
		if ( null == ref ) {
			return false;// doesn't contain it
		}
		return Z.equalsSimple_enforceNotNull( obj, ref.getObject() );
	}
	
	
	/**
	 * creates a new Ref to be added to this list, but it's not added to list
	 * via
	 * this method
	 * 
	 * @param obj
	 *            that will be encapsulated into the ref
	 * @return new reference to <tt>obj</tt>
	 */
	protected ChainedReference<THEOBJ> newRef( final THEOBJ obj ) {
		
		final ChainedReference<THEOBJ> n = new ChainedReference<THEOBJ>( obj );
		// n.setObject( obj );// is no longer null
		return n;// should never return null
	}
	
	
	/**
	 * objects are compared by content aka .equals() although .equals by default compares the references see
	 * {@link Object#equals(Object)}<br>
	 * they are not compared by reference ie. not `==` <br>
	 * 
	 * @param containingThisObject
	 *            could be null apparently
	 * @return null or the reference containing the obj
	 */
	public ChainedReference<THEOBJ> getRef( final THEOBJ containingThisObject ) {
		
		ChainedReference<THEOBJ> parser = getLastRef();
		if ( null != parser ) {
			// check if it's last
			if ( !Z.equalsSimple_enforceNotNull( parser.getObject(), containingThisObject ) ) {
				// it's not last then let's start parsing from first
				parser = getFirstRef();// can be null if empty
				// list
				while ( null != parser ) {
					if ( Z.equalsSimple_enforceNotNull( containingThisObject, parser.getObject() ) ) {
						break;
					}
					parser = parser.getNext();
					// so here it will eventually end-up checking last one again, np
				}// parsed all
			}// last
		}// null
		return parser;
	}
	
	
	/**
	 * @param pos
	 *            only FIRST/LAST
	 * @return the object at specified position(which can be null)
	 * @throws NoSuchElementException
	 *             if no such object is found
	 */
	public THEOBJ getObjectAt( final Position pos ) {
		
		assert null != pos;
		assert Position.isFirstOrLast( pos );
		final ChainedReference<THEOBJ> ref = this.getRefAt( pos );
		if ( null == ref ) {
			Q.thro( new NoSuchElementException() );
			assert false : "not reached";
			return null;// not reached
		} else {
			return ref.getObject();
		}
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return the object at index which could be a null object
	 * @throws NoSuchElementException
	 *             if no such object is found
	 */
	public THEOBJ getObjectAt( final int index ) {
		
		final ChainedReference<THEOBJ> ref = getRefAtIndex( index );
		if ( null == ref ) {
			// because there can be an object that is `null` we cannot just return null
			Q.thro( new NoSuchElementException() );
			assert false : "unreachable";
			return null;// not reached
		} else {
			return ref.getObject();
		}
	}
	
	
	/**
	 * @param obj
	 *            can be null and can exist already(a new dup would be added)
	 * @return ChainedReference
	 */
	public ChainedReference<THEOBJ> addLast( final THEOBJ obj ) {
		
		final ChainedReference<THEOBJ> nr = this.newRef( obj );
		assert null != nr;
		if ( addLastRef( nr ) ) {
			Q.bug( "must not compare `Reference`s by contents, only objects are compared by contents!" );
		}
		return nr;
	}
	
	
	/**
	 * @param obj
	 *            that could already exist in list; even null
	 * @return ChainedReference to this object
	 */
	public ChainedReference<THEOBJ> addFirst( final THEOBJ obj ) {
		
		final ChainedReference<THEOBJ> nr = this.newRef( obj );
		assert null != nr;
		if ( addFirstRef( nr ) ) {
			Q.bug( "must not compare `Reference`s by contents" );
		}
		return nr;
	}
	
	
	/**
	 * @param obj
	 *            can be null
	 * @param position
	 * @return true if object existed before call
	 */
	public ChainedReference<THEOBJ> insert( final THEOBJ obj, final Position position ) {
		
		assert null != position;
		assert Position.isFirstOrLast( position );
		switch ( position ) {
		case FIRST:
			return this.addFirst( obj );
		case LAST:
			return this.addLast( obj );
		default:
			Q.bug( "should not be reached! Position.isFirstOrLast must be bugged" );
			assert false : "not reached";
			return null;// unreachable but eclipse complains
		}
	}
	
	
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	public boolean removeObject( final THEOBJ obj ) {
		
		final ChainedReference<THEOBJ> nr = this.getRef( obj );
		if ( null == nr ) {
			// Q.thro( new NoSuchElementException() );
			return false;
		} else {
			removeRef( nr );
			return true;
		}
	}
	
	// TODO add replace methods
	
	
}
