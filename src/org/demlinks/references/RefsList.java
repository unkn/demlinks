/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>

 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.demlinks.references;

import java.util.NoSuchElementException;

import org.demlinks.debug.Debug;
import org.demlinks.crap.Position;

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
public class RefsList<Obje> {

	private int cachedSize; // cached size, prevents parsing the entire list
	private Reference<Obje> firstRef;// points to first Ref in list, or null if
	// the list is empty
	private Reference<Obje> lastRef;// points to last Ref in list, or null if
	// the list is empty

	// increased by 1 on each operation, useful to see
	// if someone else modified the list while using
	// a ListCursor
	private int modCount = 0;

	// constructor
	/**
	 * 
	 */
	protected RefsList() {
		setListToEmpty();
	}

	private void setModified() {
		modCount++;
	}

	public int getModified() {
		return modCount;
	}

	/**
	 * 
	 */
	private void setListToEmpty() {
		cachedSize = 0;// increased on add, decreased on remove and related
		firstRef = null;
		lastRef = null;
		setModified();
	}

	/**
	 * @return
	 */
	public int size() {
		return cachedSize;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return (0 == size()) || (firstRef == null) || (lastRef == null);
	}

	/**
	 * @param newLastRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addLast(Reference<Obje> newLastRef) {
		Debug.nullException(newLastRef);
		if (containsRef(newLastRef)) {
			return true;// already exists
		}
		if (!newLastRef.isAlone()) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev.");
		}
		setModified();
		if (lastRef == null) {// list is initially empty
			lastRef = firstRef = newLastRef;
		} else {// list not empty
			lastRef.setNext(newLastRef);
			newLastRef.setPrev(lastRef);
			lastRef = newLastRef;
		}
		cachedSize++;
		setModified();// again
		return false;
	}
	
	/**
	 * @param newFirstRef
	 * @return true if already exists; false if it didn't but it does now after
	 *         call
	 */
	public boolean addFirst(Reference<Obje> newFirstRef) {
		Debug.nullException(newFirstRef);
		if (containsRef(newFirstRef)) {
			return true;// already exists
		}
		if (!newFirstRef.isAlone()) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev.");
		}
		setModified();
		if (firstRef == null) {// list is initially empty
			firstRef = lastRef = newFirstRef;
		} else {// list not empty
			firstRef.setPrev(newFirstRef);
			newFirstRef.setNext(firstRef);
			firstRef = newFirstRef;
		}
		cachedSize++;
		setModified();// again
		return false;
	}
	
	/**
	 * @param newRef
	 * @param pos only BEFORE/AFTER allowed
	 * @param posRef must already exists, it's what pos is referring to
	 * @return true if already existed in list and wasn't moved as specified by call<br>
	 * 		false if all went ok
	 */
	public boolean insertObjAt(Reference<Obje> newRef, Position pos, Reference<Obje> posRef) {
		if (containsRef(newRef)) {
			return true;// already exists
		}
		if (!newRef.isAlone()) {// this allows null objects
			throw new AssertionError(
					"the new Ref must be empty, because we fill next and prev.");
		}
		if (!containsRef(posRef)) {
			throw new NoSuchElementException();
		}
		
		switch (pos) {
		case BEFORE://insert newRef BEFORE posRef:
			//beforePosRef <-> posRef <->
			// null <- posRef <->
			setModified();
			newRef.setNext(posRef);//1) newRef -> posRef
			Reference<Obje> beforePosRef = posRef.getPrev();//could be null if posRef is first
			newRef.setPrev(beforePosRef);//2) beforePosRef(or null) <- newRef
			if (beforePosRef != null) {//so posRef isn't first
				beforePosRef.setNext(newRef);//3) beforePosRef <-> newRef -> posRef,  beforePosRef<- posRef
			} else {//is first so also set firstRef
				this.firstRef = newRef; //a new first in list
				//if posRef was last, then it remains last, but if it was first newRef is first now
			}
			posRef.setPrev(newRef);//4) beforePosRef<->newRef<->posRef
			break;
			
		case AFTER:
			//order before call: posRef <-> afterPosRef(or null)
			//order after call: posRef <-> newRef <-> afterPosRef(or null)
			setModified();
			newRef.setPrev(posRef);//1) posRef <- newRef
			Reference<Obje> afterPosRef = posRef.getNext();
			newRef.setNext(afterPosRef);//2) newRef -> afterPosRef
			if (afterPosRef == null) {
				//posRef is last
				this.lastRef = newRef; 
			} else {
				//posRef isn't last
				afterPosRef.setPrev(newRef);//3) newRef <- afterPosRef
			}
			posRef.setNext(newRef);//4) posRef -> newRef
			break;
			
		default:
			throw new AssertionError("undefined location here.");
		}
		
		cachedSize++;
		setModified();// again
		return false;
	}
	
	/**
	 * @return the firstNodeRef
	 */
	protected Reference<Obje> getFirstRef() {
		return firstRef;
	}

	/**
	 * @return the lastNodeRef
	 */
	protected Reference<Obje> getLastRef() {
		return lastRef;
	}

	/**
	 * @param killRef
	 * @return true if removed, false if it was already inexistent
	 */
	public boolean removeRef(Reference<Obje> killRef) {
		Debug.nullException(killRef);
		if (!containsRef(killRef)) {
			return false;
		}

		setModified();

		Reference<Obje> prev = killRef.getPrev();// beware if you remove this
		// local var
		Reference<Obje> next = killRef.getNext();
		if (prev != null) {
			prev.setNext(next);
			// killRef.setPrev(null);//beware
		} else {
			if (firstRef == killRef) {
				firstRef = next;// can be null
			} else {
				throw new AssertionError("compromised integrity of list");
			}
		}

		if (next != null) {
			next.setPrev(prev);// beware
			// killRef.setNext(null);
		} else {
			if (lastRef == killRef) {
				lastRef = prev;// can be null
			} else {
				throw new AssertionError("compromised integrity of list (2)");
			}
		}

		// killRef.setObject(null);
		killRef.destroy();
		cachedSize--;
		setModified();
		return true;
	}

	/**
	 * @param whichRef
	 * @return true if the reference already exists; doesn't matter to what
	 *         object it points to
	 */
	public boolean containsRef(Reference<Obje> whichRef) {
		Debug.nullException(whichRef);
		Reference<Obje> parser = firstRef;
		while (null != parser) {
			if (whichRef.equals(parser)) {
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}

	/**
	 * @param location only FIRST/LAST allowed
	 * @return a reference
	 * @see #getNodeRefAt(Position, Reference)
	 */
	public Reference<Obje> getNodeRefAt(Position location) {
		switch (location) {
		case FIRST:
			return getFirstRef();
		case LAST:
			return getLastRef();
		default:
			throw new AssertionError("undefined location here.");
		}
	}

	/**
	 * @param location FIRST/LAST allowed; but BEFORE/AFTER are supposed to be used
	 * @param locationRef the reference that location is referring to
	 * @return the ref or null
	 * @see #getNodeRefAt(Position)
	 */
	public Reference<Obje> getNodeRefAt(Position location,
			Reference<Obje> locationRef) {

		Debug.nullException(location, locationRef);

		if (!this.containsRef(locationRef)) {// this will unfortunately
			// parse the list until it
			// finds it
			return null;
		}

		//locationRef cannot be null past this point, no checks follow
		switch (location) {
		case BEFORE:
			return locationRef.getPrev();

		case AFTER:
			return locationRef.getNext();

		case FIRST:
		case LAST:
			return getNodeRefAt(location);

		default:
			throw new AssertionError("undefined location within this context");
		}
	}

	// TODO move method that will delete Ref and create a new one OR not
	
	// TODO parser
}
