/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



package org.references;



import java.util.NoSuchElementException;

import org.dml.error.BadCallError;
import org.dml.tools.RunTime;



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
 */
public class ListOfReferences<Obje> {
	
	// TODO cachedLast would remember last successful Ref accessed, and use it
	// before any access, to try speed up some common things
	
	private int						cachedSize;		// cached size, prevents
	// parsing
	// the entire list
	private ChainedReference<Obje>	firstRef;			// points to first Ref
	// in list,
	// or null if
	// the list is empty
	private ChainedReference<Obje>	lastRef;			// points to last Ref in
	// list,
	// or null if
	// the list is empty
	

	// increased by 1 on each operation, useful to see
	// if someone else modified the list while using
	// a ListCursor
	private int						modCount	= 0;
	
	/**
	 * constructor
	 */
	protected ListOfReferences() {

		this.setListToEmpty();
	}
	
	/** accessors */
	private void setModified() {

		this.modCount++;
	}
	
	public int getModified() {

		return this.modCount;
	}
	
	/**
	 * @return the firstNodeRef or null if empty list
	 */
	protected ChainedReference<Obje> getFirstRef() {

		return this.firstRef;
	}
	
	/**
	 * @return the lastNodeRef or null if empty list
	 */
	protected ChainedReference<Obje> getLastRef() {

		return lastRef;
	}
	
	private void setFirstRef( ChainedReference<Obje> toValue ) {

		this.firstRef = toValue;
	}
	
	private void setLastRef( ChainedReference<Obje> toValue ) {

		this.lastRef = toValue;
	}
	
	
	private void setSize( int size ) {

		cachedSize = size;
	}
	
	/**
	 * @return number of elements in list
	 */
	public int size() {

		return cachedSize;
	}
	
	private void incSize() {

		this.setSize( this.size() + 1 );
	}
	
	private void decSize() {

		this.setSize( this.size() - 1 );
	}
	
	// =================================
	/**
	 * 
	 */
	private void setListToEmpty() {

		this.setSize( 0 );// increased on add, decreased on remove and related
		this.setFirstRef( null );
		this.setLastRef( null );
		this.setModified();
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {

		return ( 0 == this.size() ) || ( this.getFirstRef() == null ) || ( this.getLastRef() == null );
	}
	
	/**
	 * @param newLastRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addLastRef( ChainedReference<Obje> newLastRef ) {

		RunTime.assumedNotNull( newLastRef );
		if ( this.containsRef( newLastRef ) ) {
			return true;// already exists
		}
		
		if ( !newLastRef.isAlone() ) {// this allows null objects
			RunTime.bug( "the new Ref must be empty, because we fill next and prev." );
		}
		
		this.setModified();
		if ( this.getLastRef() == null ) {// list is initially empty
			this.setLastRef( newLastRef );
			this.setFirstRef( newLastRef );
		} else {// list was not empty
			this.getLastRef().setNext( newLastRef );
			newLastRef.setPrev( this.getLastRef() );
			this.setLastRef( newLastRef );
		}
		this.incSize();
		this.setModified();// again
		
		return false;// didn't already exist, but it does now
	}
	
	/**
	 * @param newFirstRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addFirstRef( ChainedReference<Obje> newFirstRef ) {

		RunTime.assumedNotNull( newFirstRef );
		if ( this.containsRef( newFirstRef ) ) {
			return true;// already exists
		}
		

		if ( !newFirstRef.isAlone() ) {
			// this allows null objects(ie. getObject());
			// because of using isAlone instead of isDead
			RunTime.bug( "the new Ref must be empty, because we fill next and prev." );
		}
		this.setModified();
		if ( this.getFirstRef() == null ) {// list is initially empty
			this.setLastRef( newFirstRef );
			this.setFirstRef( newFirstRef );
		} else {// list not empty
			this.getFirstRef().setPrev( newFirstRef );
			newFirstRef.setNext( this.getFirstRef() );
			this.setFirstRef( newFirstRef );
		}
		this.incSize();
		this.setModified();// again
		
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
	public boolean insertRefAt( ChainedReference<Obje> newRef, Position pos, ChainedReference<Obje> posRef ) {

		if ( !this.containsRef( posRef ) ) {// this first for buggy calls
			throw new NoSuchElementException();
		}
		
		if ( this.containsRef( newRef ) ) {
			return true;// already exists
		}
		
		if ( !newRef.isAlone() ) {// this allows null objects
			RunTime.bug( "the new Ref must be empty, because we fill next and prev." );
		}
		
		switch ( pos ) {
		case BEFORE:// insert newRef BEFORE posRef:
			// beforePosRef <-> posRef <->
			// null <- posRef <->
			this.setModified();
			newRef.setNext( posRef );// 1) newRef -> posRef
			ChainedReference<Obje> beforePosRef = posRef.getPrev();// could be
			// null if
			// posRef is first
			newRef.setPrev( beforePosRef );// 2) beforePosRef(or null) <- newRef
			if ( beforePosRef != null ) {// so posRef isn't first
				beforePosRef.setNext( newRef );// 3) beforePosRef <-> newRef ->
				// posRef, beforePosRef<- posRef
			} else {// is first so also set firstRef
				this.setFirstRef( newRef ); // a new first in list
				// if posRef was last, then it remains last, but if it was first
				// newRef is first now
			}
			posRef.setPrev( newRef );// 4) beforePosRef<->newRef<->posRef
			break;
		case AFTER:
			// order before call: posRef <-> afterPosRef(or null)
			// order after call: posRef <-> newRef <-> afterPosRef(or null)
			this.setModified();
			newRef.setPrev( posRef );// 1) posRef <- newRef
			ChainedReference<Obje> afterPosRef = posRef.getNext();
			newRef.setNext( afterPosRef );// 2) newRef -> afterPosRef
			if ( afterPosRef == null ) {
				// posRef is last
				this.setLastRef( newRef );
			} else {
				// posRef isn't last
				afterPosRef.setPrev( newRef );// 3) newRef <- afterPosRef
			}
			posRef.setNext( newRef );// 4) posRef -> newRef
			break;
		default:
			RunTime.bug( "undefined location here." );
		}
		this.incSize();
		this.setModified();// again
		
		return false;
	}
	
	
	/**
	 * @param killRef
	 *            the ref in that will be removed from the list<br>
	 *            this ref is also KILL-ed and set its fields to null<br>
	 */
	public void removeRef( ChainedReference<Obje> killRef ) {

		RunTime.assumedNotNull( killRef );
		if ( !this.containsRef( killRef ) ) {
			RunTime.badCall( "parameter not in list" );
		}
		this.setModified();
		ChainedReference<Obje> cachedPrev = killRef.getPrev();// beware if you
		// remove this local var
		ChainedReference<Obje> cachedNext = killRef.getNext();
		if ( cachedPrev != null ) {
			cachedPrev.setNext( cachedNext );
			// killRef.setPrev(null);//beware
		} else {
			// killRef is first already
			if ( this.getFirstRef() == killRef ) {
				this.setFirstRef( cachedNext );// can be null
			} else {
				RunTime.bug( "compromised integrity of list" );
			}
		}
		if ( cachedNext != null ) {
			cachedNext.setPrev( cachedPrev );// beware
			// killRef.setNext(null);
		} else {
			// killRef is last already
			if ( this.getLastRef() == killRef ) {
				this.setLastRef( cachedPrev );// can be null
			} else {
				RunTime.bug( "compromised integrity of list (2)" );
			}
		}
		killRef.destroy();
		this.decSize();
		this.setModified();
		
	}
	
	/**
	 * @param whichRef
	 * @return true if the reference already exists; doesn't matter to what
	 *         object it points to; identity check only ie. ==
	 */
	public boolean containsRef( ChainedReference<Obje> whichRef ) {

		RunTime.assumedNotNull( whichRef );
		ChainedReference<Obje> parser = this.getFirstRef();
		while ( null != parser ) {
			// compare by reference not contents, two diff refs can have same
			// contents but they are diff refs, so don't use .equals()
			if ( whichRef == parser ) {
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
	public ChainedReference<Obje> getRefAt( Position location ) {

		switch ( location ) {
		case FIRST:
			return this.getFirstRef();
		case LAST:
			return this.getLastRef();
		default:
			throw new AssertionError( "undefined location here." );
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
	public ChainedReference<Obje> getRefAt( Position location, ChainedReference<Obje> locationRef ) {

		RunTime.assumedNotNull( location, locationRef );
		if ( this.containsRef( locationRef ) ) {
			switch ( location ) {
			case BEFORE:
				return locationRef.getPrev();
			case AFTER:
				return locationRef.getNext();
			default:
				RunTime.bug( "undefined location within this context" );
			}
		}
		return null;
	}
	
	/**
	 * @param index
	 *            0 based index
	 * @return null or the ref at position 'index'
	 * @exception BadCallError
	 *                if index out of bounds
	 */
	public ChainedReference<Obje> getRefAtIndex( int index ) {

		if ( ( index < 0 ) || ( index >= this.size() ) ) {
			throw new BadCallError( "out of bounds" );
		}
		
		ChainedReference<Obje> parser = this.getFirstRef();
		int pos = 0;
		while ( null != parser ) {
			if ( index == pos ) {
				break;
			}
			parser = parser.getNext();
			pos++;
		}
		return parser;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String ret = "";// this.getClass().getSimpleName();
		ChainedReference<Obje> iter = this.getFirstRef();
		while ( null != iter ) {
			ret += iter.toString() + ", ";
			// get next
			iter = this.getRefAt( Position.AFTER, iter );
		}
		return ret + ".";
	}
}
