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

package org.demlinks.javaone;

import java.util.NoSuchElementException;

/**
 * a double-linked list of unique objects (no duplicates allowed)<br>
 * no null objects allowed<br>
 * ability to insert anywhere<br>
 * 
 * @param <Obj>
 *            the list will have objects of this type
 */
public class LinkedListSet<Obj> {

	private int cachedSize; // cached size, prevents parsing the entire list
	private Capsule<Obj> head;//points to first capsule in list, or null if empty list
	private Capsule<Obj> tail;//points to last capsule in list, or null if empty list
	
	// constructor
	/**
	 * 
	 */
	LinkedListSet() {
		setListToEmpty();
	}

	/**
	 * 
	 */
	private void setListToEmpty() {
		cachedSize = 0;//increased on add, decreased on remove and related
		head = null;
		tail = null;
	}
	
	/**
	 * @return
	 */
	public int getSize() {
		return cachedSize;
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return (0 == getSize()) || (head == null) || (tail == null);
	}

	/**
	 * @return
	 */
	private Capsule<Obj> getFirstCapsule() {
		return head;
	}
	
	/**
	 * @return
	 */
	private Capsule<Obj> getLastCapsule() {
		return tail;
	}
	
	/**
	 * @return
	 */
	public Obj getFirstObj() {
		if (isEmpty()) {
			return null;
		}
		return getFirstCapsule().object;
	}

	/**
	 * @return
	 */
	public Obj getLastObj() {
		if (isEmpty()) {
			return null;
		}
		return getLastCapsule().object;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean addFirst(Obj obj) {
		if (containsObj(obj)) {
			return false;
		}
		addObjBeforeCapsule(obj, head);
		return getFirstObj() == obj;
	}

	/**
	 * @param obj
	 * @return
	 */
	public boolean addLast(Obj obj) {
		if (containsObj(obj)) {
			return false;
		}
		addObjAfterCapsule(obj, tail);
		return getLastObj() == obj;
	}
	
	/**
	 * @return
	 */
	public Obj removeFirst() {
		return removeCapsule(getFirstCapsule());
	}

	/**
	 * @return
	 */
	public Obj removeLast() {
		return removeCapsule(getLastCapsule());
	}
	
	/**
	 * @param whichObj
	 * @return
	 */
	public boolean containsObj(Obj whichObj) {
		return indexOfObj(whichObj) != -1;
	}

	/**
	 * @param whichObj
	 * @return true if list changed as a result of this call
	 */
	public boolean removeObj(Obj whichObj) {
		Environment.nullException(whichObj);
		if (isEmpty()) {
			return false;
		}
		for (Capsule<Obj> current = getFirstCapsule(); current != null; current = current.nextCapsule) {
			if ( current.object.equals(whichObj) ) {
				removeCapsule(current);
				return true;
			}
		}
		return false;
	}


	
	/**
	 * @return true if list changed as a result of this call
	 */
	public boolean clear() {
		if (isEmpty()) {
			return false;
		}
		Capsule<Obj> parser = getFirstCapsule();//first in list
		while (parser != null) {
			Capsule<Obj> tmpNext = parser.nextCapsule;
			parser.prevCapsule = null;
			parser.nextCapsule = null;
			parser.object = null;
			parser = tmpNext;
		}
		
		setListToEmpty();
		return true;
	}


	/**
	 * @param index
	 * @param withWhatObject
	 * @return
	 */
	public Obj replaceObjAt(int index, Obj withWhatObject) {
		if (containsObj(withWhatObject)) {
			return null;//failed because object already exists somewhere (and we're a Set)
		}
		Capsule<Obj> capsule = getCapsuleAt(index);
		Obj oldObj = capsule.object;
		capsule.object = withWhatObject;
		return oldObj;
	}

	/**
	 * @param object
	 * @param index
	 * @return
	 */
	public boolean insertObjAt(Obj object, int index) {
		if (isEmpty()) {
			return addFirst(object);
		} else {
			if (index == getSize()) {
				return addLast(object);
			}
		}
		
		if (containsObj(object)) {
			return false;
		}
		checkIndex(index);
		addObjBeforeCapsule(object, getCapsuleAt(index));
		return true;
	}

	/**
	 * @param object
	 * @param location
	 * @return
	 */
	public boolean insertObjAt(Obj object, Location location) {
		switch (location) {
		case FIRST:
			return addFirst(object);
		case LAST:
			return addLast(object);
		default:
			throw new AssertionError("That location is unused in this context.");
		}
	}
	
	//TODO move method

	/**
	 * @param object
	 * @param location
	 * @param locationObject
	 * @return
	 */
	public boolean insertObjAt(Obj object, Location location, Obj locationObject) {
		if (containsObj(object)) {
			return false;
		}
		Capsule<Obj> caps = getCapsuleAt(indexOfObj(locationObject));
		
		switch (location) {
		case BEFORE:
			addObjBeforeCapsule(object, caps);
			break;
		case AFTER:
			addObjAfterCapsule(object, caps);
			break;
		case FIRST:
		case LAST:
			return insertObjAt(object, location);
		default:
			throw new AssertionError("undefined location within this context");
		}
		return true;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public Obj getObjAt(int index) {
		return getCapsuleAt(index).object;
	}

	/**
	 * @param location
	 * @return
	 */
	public Obj getObjAt(Location location) {
		switch (location) {
		case FIRST:
			return getFirstObj();
		case LAST:
			return getLastObj();
		default:
			throw new AssertionError("undefined location here.");
		}
	}

	/**
	 * @param location
	 * @param object
	 * @return
	 */
	public Obj getObjAt(Location location, Obj object) {
		int index=0;
		if (object != null){
			index = indexOfObj(object);
			if (-1 == index) {
				return null;
			}
		}
		switch (location) {
		case BEFORE:
			if (object == null) {
				return getLastObj();
			}
			index -= 1;
			if (indexInBounds(index)) {
				return getObjAt(index);
			}
			break;
		case AFTER:
			if (object == null) {
				return getFirstObj();
			}
			index += 1;
			if (indexInBounds(index)) {
				return getObjAt(index);
			}
			break;
		case FIRST:
		case LAST:
			throw new AssertionError("use the other method.");
		default:
			throw new AssertionError("undefined location within this context");
		}
		return null;
	}
	
	public boolean indexInBounds(int index) {
		if ((index < 0) || (index >= cachedSize)) {
			return false;
		}
		return true;
	}

	/**
	 * @param index
	 * @return
	 */
	public Obj removeAt(int index) {
		return removeCapsule(getCapsuleAt(index));
	}

	/**
	 * @param anyObjectInList
	 * @return
	 */
	public int indexOfObj(Obj anyObjectInList) {
		Environment.nullException(anyObjectInList);
		int index = 0;
		for (Capsule<Obj> capsule = getFirstCapsule(); capsule != null; capsule = capsule.nextCapsule) {
			if (anyObjectInList.equals(capsule.object)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * @param index
	 */
	private void checkIndex(int index) {
		if (!indexInBounds(index)) {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * @param addWhatObj
	 * @param beforeWhatCapsule
	 * @return
	 */
	private Capsule<Obj> addObjBeforeCapsule(Obj addWhatObj, Capsule<Obj> beforeWhatCapsule) {
		if (beforeWhatCapsule == null) {
			head = new Capsule<Obj>(addWhatObj, null, null);
			tail = head;
			cachedSize++;
			return head;
		}
		Capsule<Obj> newCapsule = new Capsule<Obj>(addWhatObj, beforeWhatCapsule.prevCapsule, beforeWhatCapsule);
		if (beforeWhatCapsule.prevCapsule != null) { // has prev
			beforeWhatCapsule.prevCapsule.nextCapsule = newCapsule;
		} else {
			head = newCapsule;
		}
		beforeWhatCapsule.prevCapsule = newCapsule;
		cachedSize++;
		return newCapsule;
	}
	
	/**
	 * @param addWhatObj
	 * @param afterWhatCapsule
	 * @return
	 */
	private Capsule<Obj> addObjAfterCapsule(Obj addWhatObj, Capsule<Obj> afterWhatCapsule) {
		if (afterWhatCapsule == null) {
			head = new Capsule<Obj>(addWhatObj, null, null);
			tail = head;
			cachedSize++;
			return head;
		}
		Capsule<Obj> newCapsule = new Capsule<Obj>(addWhatObj, afterWhatCapsule, afterWhatCapsule.nextCapsule);
		if (afterWhatCapsule.nextCapsule != null) { // has next
			afterWhatCapsule.nextCapsule.prevCapsule = newCapsule;
		} else {//this was last, we move tail also
			tail = newCapsule;
		}
		afterWhatCapsule.nextCapsule = newCapsule;
		cachedSize++;
		return newCapsule;
	}

	/**
	 * @param capsule
	 * @return
	 */
	private Obj removeCapsule(Capsule<Obj> capsule) {
		if (capsule == null) {
			throw new NoSuchElementException();
		}

		Obj oldObject = capsule.object;
		if (capsule.prevCapsule != null) {//has prev
			capsule.prevCapsule.nextCapsule = capsule.nextCapsule;
		} else {
			head = capsule.nextCapsule;
		}
		if (capsule.nextCapsule != null) {//has next
			capsule.nextCapsule.prevCapsule = capsule.prevCapsule;
			
		} else {
			tail = capsule.prevCapsule;
		}
		
		capsule.prevCapsule = null;
		capsule.nextCapsule = null;
		capsule.object = null;
		
		cachedSize--;
		return oldObject;
	}

	/**
	 * @param index
	 * @return
	 */
	private Capsule<Obj> getCapsuleAt(int index) {
		checkIndex(index);
		Capsule<Obj> current;
		if (index <= (cachedSize >> 1)) { // divided by 2, avoiding div by 0 tho
			current = getFirstCapsule();
			for (int i = 0; i < index; i++)
				current = current.nextCapsule;
		} else {
			current = getLastCapsule();
			for (int i = index +1; i < cachedSize; i++)
				current = current.prevCapsule;
		}
		return current;
	}
	
	//CLASS
	/**
	 * 
	 *
	 * @param <Obj>
	 */
	private static class Capsule<Obj> {
		Capsule<Obj> prevCapsule;
		Obj object;
		Capsule<Obj> nextCapsule;

		Capsule(Obj theObject, Capsule<Obj> previousCapsule, Capsule<Obj> nextCapsule) {
			object = theObject;
			prevCapsule = previousCapsule;
			this.nextCapsule = nextCapsule;
		}
	}//end CLASS
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380435324961393742L;



//TODO use a lastCapsule field to make seeks faster

}
