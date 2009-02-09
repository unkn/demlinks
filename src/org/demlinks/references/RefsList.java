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
	// TODO addFirst or generalize addLast to insert(whatRef, location,
	// locationRef)
	// TODO parser
}
