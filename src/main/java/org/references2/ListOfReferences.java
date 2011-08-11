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



/**
 * 
 */
/**
 * a double-linked list of References where no two are alike (no duplicates
 * allowed)<br>
 * these Refs however may contain the same objects thus allowing duplicate
 * objects<br>
 * but the list itself is comprised of unique References; the Refs are unique,
 * but there can be two different Refs pointing to same object<br>
 * null objects are allowed at this level<br>
 * no null Refs allowed<br>
 * ability to insert anywhere<br>
 * 
 * this is handled at Ref level, not at object level<br>
 * so this is a list of unique references meaning the reference doesn't repeat itself in the list, and this doesn't care
 * what that reference content is meaning the `Obje` is disregarded when keeping this list, so it can hold null `Obje`s
 * and duplicate `Obje`s<br>
 * 
 * @param <Obje>
 *            list-of-references to this object
 */
public class ListOfReferences<Obje>
{
	
	// TODO cachedLast would remember last successful Ref accessed, and use it
	// before any access, to try speed up some common things
	// private ChainedReference<Obje> cachedLast;
	
	private int						cachedSize; // cached size, prevents
	// parsing
	// the entire list
	private ChainedReference<Obje>	firstRef;	// points to first Ref
	// in list,
	// or null if
	// the list is empty
	private ChainedReference<Obje>	lastRef;	// points to last Ref in
												
												
	// list,
	// or null if
	// the list is empty
	
	
	
	/**
	 * constructor
	 */
	protected ListOfReferences() {
		this.setListToEmpty();
	}
	
	
	/**
	 * @return the firstNodeRef or null if empty list
	 */
	protected synchronized ChainedReference<Obje> getFirstRef() {
		
		return this.firstRef;
	}
	
	
	/**
	 * @return the lastNodeRef or null if empty list
	 */
	protected synchronized ChainedReference<Obje> getLastRef() {
		
		return lastRef;
	}
	
	
	private synchronized void setFirstRef( final ChainedReference<Obje> toValue ) {
		if ( null != toValue ) {
			assert !toValue.isDead();
		}
		this.firstRef = toValue;
	}
	
	
	private synchronized void setLastRef( final ChainedReference<Obje> toValue ) {
		if ( null != toValue ) {
			assert !toValue.isDead();
		}
		this.lastRef = toValue;
	}
	
	
	private synchronized void setSize( final int size ) {
		assert size >= 0;
		cachedSize = size;
	}
	
	
	/**
	 * @return number of elements in list
	 */
	public synchronized int size() {
		
		return cachedSize;
	}
	
	
	private synchronized void incSize() {
		
		this.setSize( this.size() + 1 );
	}
	
	
	private synchronized void decSize() {
		
		this.setSize( this.size() - 1 );
	}
	
	
	// =================================
	/**
	 * 
	 */
	private synchronized void setListToEmpty() {
		
		this.setSize( 0 );// increased on add, decreased on remove and related
		this.setFirstRef( null );
		this.setLastRef( null );
		// this.setModified();
	}
	
	
	/**
	 * @return true if empty
	 */
	public synchronized boolean isEmpty() {
		final boolean empty = ( 0 == this.size() );
		if ( empty ) {
			assert null == this.getFirstRef();
			assert null == this.getLastRef();
		}
		return empty;
	}
	
	
	/**
	 * @param newLastRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public synchronized boolean addLastRef( final ChainedReference<Obje> newLastRef ) {
		
		assert null != newLastRef;
		assert newLastRef.isAlone() : Q.bug( "the new Ref must be empty, because we fill next and prev." );
		assert !newLastRef.isDead();
		
		if ( this.containsRef( newLastRef ) ) {
			return true;// already exists
		}
		
		// this.setModified();
		if ( this.getLastRef() == null ) {// list is initially empty
			assert null == this.getFirstRef();
			this.setLastRef( newLastRef );
			this.setFirstRef( newLastRef );
			assert newLastRef.equals( this.getFirstRef() );
		} else {// list was not empty
			assert null != this.getFirstRef();
			assert null == getLastRef().getNext();
			this.getLastRef().setNext( newLastRef );
			newLastRef.setPrev( this.getLastRef() );
			this.setLastRef( newLastRef );
		}
		assert newLastRef.equals( this.getLastRef() );
		final int oldSize = size();
		this.incSize();
		assert size() == ( oldSize + 1 );
		// this.setModified();// again
		return false;// didn't already exist, but it does now
	}
	
	
	/**
	 * @param newFirstRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public synchronized boolean addFirstRef( final ChainedReference<Obje> newFirstRef ) {
		
		assert null != newFirstRef;
		assert newFirstRef.isAlone() :
		// if ( ! ) {
		// this allows null objects(ie. getObject());
		// because of using isAlone instead of isDead
		Q.bug( "the new Ref must be empty, because we fill next and prev." );
		// }
		assert !newFirstRef.isDead();
		
		if ( this.containsRef( newFirstRef ) ) {
			return true;// already exists
		}
		
		// this.setModified();
		if ( this.getFirstRef() == null ) {// list is initially empty
			assert null == getLastRef();
			this.setLastRef( newFirstRef );
			this.setFirstRef( newFirstRef );
		} else {// list not empty
			assert null != getLastRef();
			assert null == this.getFirstRef().getPrev();
			this.getFirstRef().setPrev( newFirstRef );
			newFirstRef.setNext( this.getFirstRef() );
			this.setFirstRef( newFirstRef );
		}
		final int oldSize = size();
		this.incSize();
		assert newFirstRef.equals( this.getFirstRef() );
		assert size() == ( oldSize + 1 );
		// this.setModified();// again
		
		return false;
	}
	
	
	/**
	 * @param newRef
	 * @param pos
	 *            only BEFORE/AFTER allowed
	 * @param posRef
	 *            must already exists, it's what pos is referring to
	 * @return true if already existed in list and wasn't moved as specified by
	 *         call<br>
	 *         false if all went ok
	 * @throws NoSuchElementException
	 *             if posRef not found
	 */
	public synchronized boolean insertRefAt( final ChainedReference<Obje> newRef, final Position pos,
												final ChainedReference<Obje> posRef ) {
		assert null != newRef;
		assert null != posRef;
		assert null != pos;
		assert newRef.isAlone() :
		// ) {// this allows null objects
		Q.bug( "the new Ref must be empty, because we fill next and prev." );
		// }
		assert !newRef.isDead();
		
		
		assert this.containsRef( posRef ) :
		// if ( ! ) {// this first for buggy calls
		Q.thro( new NoSuchElementException() );
		// }
		
		if ( this.containsRef( newRef ) ) {
			return true;// already exists
		}
		
		switch ( pos ) {
		case BEFORE:// insert newRef BEFORE posRef:
			// beforePosRef <-> posRef <->
			// null <- posRef <->
			// this.setModified();
			newRef.setNext( posRef );// 1) newRef -> posRef
			final ChainedReference<Obje> beforePosRef = posRef.getPrev();// could be
			// null if
			// posRef is first
			newRef.setPrev( beforePosRef );// 2) beforePosRef(or null) <- newRef
			if ( beforePosRef != null ) {// so posRef isn't first
				beforePosRef.setNext( newRef );// 3) beforePosRef <-> newRef ->
				// posRef, beforePosRef<- posRef
			} else {// is first so also set firstRef
				assert posRef.equals( getFirstRef() );
				this.setFirstRef( newRef ); // a new first in list
				// if posRef was last, then it remains last, but if it was first
				// newRef is first now
				assert newRef.equals( getFirstRef() );
			}
			posRef.setPrev( newRef );// 4) beforePosRef<->newRef<->posRef
			break;
		case AFTER:
			// order before call: posRef <-> afterPosRef(or null)
			// order after call: posRef <-> newRef <-> afterPosRef(or null)
			// this.setModified();
			newRef.setPrev( posRef );// 1) posRef <- newRef
			final ChainedReference<Obje> afterPosRef = posRef.getNext();
			newRef.setNext( afterPosRef );// 2) newRef -> afterPosRef
			if ( afterPosRef == null ) {
				// posRef is last
				assert posRef.equals( getLastRef() );
				this.setLastRef( newRef );
				assert newRef.equals( getLastRef() );
			} else {
				// posRef isn't last
				afterPosRef.setPrev( newRef );// 3) newRef <- afterPosRef
			}
			posRef.setNext( newRef );// 4) posRef -> newRef
			break;
		default:
			Q.bug( "undefined location here." );
			assert false : "not reached";
		}
		final int oldSize = size();
		this.incSize();
		assert size() == ( oldSize + 1 );
		// this.setModified();// again
		
		return false;
	}
	
	
	/**
	 * @param killRef
	 *            the ref in that will be removed from the list<br>
	 *            this ref is also KILL-ed and set its fields to null<br>
	 */
	public synchronized void removeRef( final ChainedReference<Obje> killRef ) {
		
		assert null != killRef;
		assert this.containsRef( killRef ) :
		// if ( !) {
		Q.badCall( "reference is not in list" );
		// }
		assert size() >= 1;
		// this.setModified();
		final ChainedReference<Obje> cachedPrev = killRef.getPrev();// beware if you
		// remove this local var
		final ChainedReference<Obje> cachedNext = killRef.getNext();
		if ( cachedPrev != null ) {
			cachedPrev.setNext( cachedNext );
			// killRef.setPrev(null);//beware
		} else {
			// killRef is first already
			assert killRef.equals( this.getFirstRef() ) : Q.bug( "detected compromised integrity of list" );
			// if ( ) {
			this.setFirstRef( cachedNext );// can be null
			// } else {
			
			// }
		}
		if ( cachedNext != null ) {
			cachedNext.setPrev( cachedPrev );// beware
			// killRef.setNext(null);
		} else {
			// killRef is last already
			assert killRef.equals( this.getLastRef() ) : Q.bug( "compromised integrity of list (2)" );
			// if ( ) {
			this.setLastRef( cachedPrev );// can be null
			// } else {
			
			// }
		}
		killRef.destroy();
		final int oldSize = size();
		this.decSize();
		assert size() == ( oldSize - 1 );
		// this.setModified();
		
	}
	
	
	/**
	 * @param whichRef
	 * @return true if the reference already exists; doesn't matter to what
	 *         object it points to; identity check only ie. ==
	 */
	public synchronized boolean containsRef( final ChainedReference<Obje> whichRef ) {
		
		assert null != whichRef;
		assert !whichRef.isDead();
		
		ChainedReference<Obje> parser = this.getFirstRef();
		while ( null != parser ) {
			// compare by reference not contents, two diff refs can have same
			// contents but they are diff refs, so don't use .equals()
			if ( whichRef.equals( parser ) )// that equals compares by reference
			{
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}
	
	
	/**
	 * @param location
	 *            only FIRST/LAST allowed
	 * @return a reference or null if list is empty
	 * @see #getRefAt(Position, ChainedReference)
	 */
	public synchronized ChainedReference<Obje> getRefAt( final Position location ) {
		assert null != location;
		assert Position.isFirstOrLast( location );
		switch ( location ) {
		case FIRST:
			return this.getFirstRef();
		case LAST:
			return this.getLastRef();
		default:
			Q.badCall( "impossible" );
			assert false : "not reached";
			return null;// not reached
		}
	}
	
	
	/**
	 * @param location
	 *            BEFORE/AFTER are supposed to be used; others aren't allowed
	 * @param locationRef
	 *            the reference that location is referring to
	 * @return the ref or null
	 * @see #getRefAt(Position)
	 */
	public synchronized ChainedReference<Obje> getRefAt( final Position location, final ChainedReference<Obje> locationRef ) {
		
		assert null != location;
		assert null != locationRef;
		assert this.containsRef( locationRef ) :
		// if ( ! ) {
		Q.badCall( "that ref doesn't exist in list" );
		// }
		switch ( location ) {
		case BEFORE:
			return locationRef.getPrev();
		case AFTER:
			return locationRef.getNext();
		default:
			Q.bug( "undefined location within this context" );
			assert false : "unreached";
			return null;// unreached
		}
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return never null so it's the ref at position 'index'
	 * @exception BadCallError
	 *                if index out of bounds
	 */
	public synchronized ChainedReference<Obje> getRefAtIndex( final int index ) {
		assert ( ( index >= 0 ) && ( index < this.size() ) ) :
		// if ( ( index < 0 ) || ( index >= this.size() ) ) {
		Q.badCall( "out of bounds" );
		// }
		
		ChainedReference<Obje> parser = this.getFirstRef();
		int pos = 0;
		while ( ( null != parser ) && ( index != pos ) ) {
			// if ( index == pos )
			// {
			// break;
			// }
			parser = parser.getNext();
			pos++;
		}
		assert null != parser;
		return parser;
	}
	
	
	public synchronized int getIndexOfRef( final ChainedReference<Obje> whichRef ) {
		
		assert null != whichRef;
		assert !whichRef.isDead();
		int index = 0;
		ChainedReference<Obje> parser = this.getFirstRef();
		while ( null != parser ) {
			// compare by reference not contents, two diff refs can have same
			// contents but they are diff refs, so don't use .equals()
			if ( whichRef.equals( parser ) ) {
				assert ( ( index >= 0 ) && ( index < this.size() ) ) :
				// if ( ( index < 0 ) || ( index >= this.size() ) ) {
				Q.bug( "index got out of bounds, that's quite odd" );
				// }
				return index;
			}
			parser = parser.getNext();
			index++;
		}
		
		return -1;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		
		String ret = "{";// this.getClass().getSimpleName();
		ChainedReference<Obje> iter = this.getFirstRef();
		while ( null != iter ) {
			ret += iter.toString() + ", ";
			// get next
			iter = this.getRefAt( Position.AFTER, iter );
		}
		return ret + "}";
	}
}
