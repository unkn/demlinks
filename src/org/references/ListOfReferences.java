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


package org.references;



import java.util.NoSuchElementException;

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
	
	// constructor
	/**
	 * 
	 */
	protected ListOfReferences() {

		this.setListToEmpty();
	}
	
	private void setModified() {

		this.modCount++;
	}
	
	public int getModified() {

		return this.modCount;
	}
	
	/**
	 * 
	 */
	private void setListToEmpty() {

		this.cachedSize = 0;// increased on add, decreased on remove and related
		this.firstRef = null;
		this.lastRef = null;
		this.setModified();
	}
	
	/**
	 * @return
	 */
	public int size() {

		return this.cachedSize;
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {

		return ( 0 == this.size() ) || ( this.firstRef == null )
				|| ( this.lastRef == null );
	}
	
	/**
	 * @param newLastRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addLastRef( ChainedReference<Obje> newLastRef ) {

		RunTime.assertNotNull( newLastRef );
		if ( this.containsRef( newLastRef ) ) {
			return true;// already exists
		}
		if ( !newLastRef.isAlone() ) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev." );
		}
		this.setModified();
		if ( this.lastRef == null ) {// list is initially empty
			this.lastRef = this.firstRef = newLastRef;
		} else {// list not empty
			this.lastRef.setNext( newLastRef );
			newLastRef.setPrev( this.lastRef );
			this.lastRef = newLastRef;
		}
		this.cachedSize++;
		this.setModified();// again
		return false;
	}
	
	/**
	 * @param newFirstRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addFirstRef( ChainedReference<Obje> newFirstRef ) {

		RunTime.assertNotNull( newFirstRef );
		if ( this.containsRef( newFirstRef ) ) {
			return true;// already exists
		}
		if ( !newFirstRef.isAlone() ) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev." );
		}
		this.setModified();
		if ( this.firstRef == null ) {// list is initially empty
			this.firstRef = this.lastRef = newFirstRef;
		} else {// list not empty
			this.firstRef.setPrev( newFirstRef );
			newFirstRef.setNext( this.firstRef );
			this.firstRef = newFirstRef;
		}
		this.cachedSize++;
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
	 */
	public boolean insertObjAt( ChainedReference<Obje> newRef, Position pos,
			ChainedReference<Obje> posRef ) {

		if ( !this.containsRef( posRef ) ) {// this first for buggy calls
			throw new NoSuchElementException();
		}
		if ( this.containsRef( newRef ) ) {
			return true;// already exists
		}
		if ( !newRef.isAlone() ) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev." );
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
				this.firstRef = newRef; // a new first in list
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
				this.lastRef = newRef;
			} else {
				// posRef isn't last
				afterPosRef.setPrev( newRef );// 3) newRef <- afterPosRef
			}
			posRef.setNext( newRef );// 4) posRef -> newRef
			break;
		default:
			throw new AssertionError( "undefined location here." );
		}
		this.cachedSize++;
		this.setModified();// again
		return false;
	}
	
	/**
	 * @return the firstNodeRef
	 */
	protected ChainedReference<Obje> getFirstRef() {

		return this.firstRef;
	}
	
	/**
	 * @return the lastNodeRef
	 */
	protected ChainedReference<Obje> getLastRef() {

		return this.lastRef;
	}
	
	/**
	 * @param killRef
	 * @return true if removed, false if it was already inexistent
	 */
	public boolean removeRef( ChainedReference<Obje> killRef ) {

		RunTime.assertNotNull( killRef );
		if ( !this.containsRef( killRef ) ) {
			return false;
		}
		this.setModified();
		ChainedReference<Obje> prev = killRef.getPrev();// beware if you remove
		// this
		// local var
		ChainedReference<Obje> next = killRef.getNext();
		if ( prev != null ) {
			prev.setNext( next );
			// killRef.setPrev(null);//beware
		} else {
			if ( this.firstRef == killRef ) {
				this.firstRef = next;// can be null
			} else {
				throw new AssertionError( "compromised integrity of list" );
			}
		}
		if ( next != null ) {
			next.setPrev( prev );// beware
			// killRef.setNext(null);
		} else {
			if ( this.lastRef == killRef ) {
				this.lastRef = prev;// can be null
			} else {
				throw new AssertionError( "compromised integrity of list (2)" );
			}
		}
		// killRef.setObject(null);
		killRef.destroy();
		this.cachedSize--;
		this.setModified();
		return true;
	}
	
	/**
	 * @param whichRef
	 * @return true if the reference already exists; doesn't matter to what
	 *         object it points to; identity check only
	 */
	public boolean containsRef( ChainedReference<Obje> whichRef ) {

		RunTime.assertNotNull( whichRef );
		ChainedReference<Obje> parser = this.getFirstRef();
		while ( null != parser ) {
			if ( whichRef.equals( parser ) ) {
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}
	
	/**
	 * @param location
	 *            only FIRST/LAST allowed
	 * @return a reference
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
	 *            FIRST/LAST allowed; but BEFORE/AFTER are supposed to be used
	 * @param locationRef
	 *            the reference that location is referring to
	 * @return the ref or null
	 * @see #getRefAt(Position)
	 */
	public ChainedReference<Obje> getRefAt( Position location,
			ChainedReference<Obje> locationRef ) {

		RunTime.assertNotNull( location, locationRef );
		if ( !this.containsRef( locationRef ) ) {// this will unfortunately
			// parse the list until it
			// finds it
			return null;
		}
		// locationRef cannot be null past this point, no checks follow
		switch ( location ) {
		case BEFORE:
			return locationRef.getPrev();
		case AFTER:
			return locationRef.getNext();
		case FIRST:
		case LAST:
			return this.getRefAt( location );
		default:
			throw new AssertionError( "undefined location within this context" );
		}
	}
	// parser can be done using parser=getFirstRef() and
	// parser=getNodeRefAt(Position.AFTER, parser)
}
