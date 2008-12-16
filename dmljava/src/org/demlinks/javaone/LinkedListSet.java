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
	LinkedListSet() {
		setListToEmpty();
	}

	private void setListToEmpty() {
		cachedSize = 0;//increased on add, decreased on remove and related
		head = null;
		tail = null;
	}
	
	public int getSize() {
		return cachedSize;
	}
	
	public boolean isEmpty() {
		return (0 == getSize()) || (head == null) || (tail == null);
	}

	private Capsule<Obj> getFirstCapsule() {
		return head;
	}
	
	private Capsule<Obj> getLastCapsule() {
		return tail;
	}
	
	public Obj getFirstObj() {
		if (isEmpty()) {
			return null;
		}
		return getFirstCapsule().object;
	}

	public Obj getLastObj() {
		if (isEmpty()) {
			return null;
		}
		return getLastCapsule().object;
	}
	
	public boolean addFirst(Obj obj) {
		if (containsObj(obj)) {
			return false;
		}
		addObjBeforeCapsule(obj, head);
		return getFirstObj() == obj;
	}

	public boolean addLast(Obj obj) {
		if (containsObj(obj)) {
			return false;
		}
		addObjAfter(obj, tail);
		return getLastObj() == obj;
	}
	
	public Obj removeFirst() {
		return removeCapsule(getFirstCapsule());
	}

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

	public Obj get(int index) {
		return getCapsuleAt(index).object;
	}

	/**
	 * @param index
	 * @param withWhatObject
	 * @return
	 */
	public Obj replace(int index, Obj withWhatObject) {
		if (containsObj(withWhatObject)) {
			return null;//failed because object already exists somewhere (and we're a Set)
		}
		Capsule<Obj> capsule = getCapsuleAt(index);
		Obj oldObj = capsule.object;
		capsule.object = withWhatObject;
		return oldObj;
	}

	/**
	 * @param index
	 * @param object
	 * @return
	 */
	public boolean insertAt(int index, Obj object) {
		if (containsObj(object)) {
			return false;
		}
		checkIndex(index);
		addObjBeforeCapsule(object, getCapsuleAt(index));
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

	private void checkIndex(int index) {
		if ((index < 0) || (index >= cachedSize)) {
			throw new IndexOutOfBoundsException();
		}
	}

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
	
	private Capsule<Obj> addObjAfter(Obj addWhatObj, Capsule<Obj> afterWhatCapsule) {
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
	
	
	public ListCursor<Obj> listCursor(int index) {
		return new ListItr(index);
	}
	
	//CLASS ListItr
	/**
	 * 
	 *
	 */
	private class ListItr implements ListCursor<Obj> {

		//constructor
		ListItr(int index) {
			checkIndex(index);
			//TODO 
		}

	} //end CLASS ListItr
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380435324961393742L;

}
