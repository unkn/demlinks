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

package org.demlinks.javathree;


/**
 * a double-linked list of NodeReferences where no two are alike (no duplicates allowed)<br>
 * these NodeRefs however may contain the same Node objects thus allowing duplicate Nodes at that level<br>
 * but the list itself is comprised of unique NodeReferences<br>
 * no null nodes allowed<br>no null NodeRefs allowed<br>
 * ability to insert anywhere<br>
 * 
 * this is handled at NodeRef level, not at Node level<br>
 */
public class NodeRefsList_L1 {

	private int cachedSize; // cached size, prevents parsing the entire list
	private NodeRef firstNodeRef;//points to first nodeRef in list, or null if empty list
	private NodeRef lastNodeRef;//points to last nodeRef in list, or null if empty list
	
	// constructor
	/**
	 * 
	 */
	NodeRefsList_L1() {
		setListToEmpty();
	}
	
	/**
	 * 
	 */
	private void setListToEmpty() {
		cachedSize = 0;//increased on add, decreased on remove and related
		firstNodeRef = null;
		lastNodeRef = null;
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
		return (0 == size()) || (firstNodeRef == null) || (lastNodeRef == null);
	}

	/**
	 * @param newLastNodeRef
	 * @return false if already exists; true if it didn't but it does now after call
	 */
	public boolean addLast(NodeRef newLastNodeRef) {
		Debug.nullException(newLastNodeRef);
		if (containsNodeRef(newLastNodeRef)) {
			return false;//already exists
		}
		if (!newLastNodeRef.isAlone()) {
			throw new AssertionError();
		}
		if (lastNodeRef == null) {//list is initially empty
			lastNodeRef = firstNodeRef = newLastNodeRef;
		} else {//list not empty
			lastNodeRef.setNext(newLastNodeRef);
			newLastNodeRef.setPrev(lastNodeRef);
			lastNodeRef = newLastNodeRef;
		}
		cachedSize++;
		return true;
	}

	/**
	 * @return the firstNodeRef
	 */
	protected NodeRef getFirstNodeRef() {
		return firstNodeRef;
	}

	/**
	 * @param firstNodeRef the firstNodeRef to set
	 */
	protected void setFirstNodeRef(NodeRef firstNodeRef) {
		this.firstNodeRef = firstNodeRef;
	}

	/**
	 * @return the lastNodeRef
	 */
	protected NodeRef getLastNodeRef() {
		return lastNodeRef;
	}

	/**
	 * @param lastNodeRef the lastNodeRef to set
	 */
	protected void setLastNodeRef(NodeRef lastNodeRef) {
		this.lastNodeRef = lastNodeRef;
	}

	/**
	 * @param killNR
	 * @return true if removed, false if it was already inexistent
	 */
	public boolean removeNodeRef(NodeRef killNR) {
		Debug.nullException(killNR);
		if (!containsNodeRef(killNR)) {
			return false;
		}
		
		
		
		NodeRef prev = killNR.getPrev();//beware if you remove this local var
		NodeRef next = killNR.getNext();
		if (prev != null) {
			prev.setNext(next);
			killNR.setPrev(null);//beware
		} else {
			if (firstNodeRef == killNR) {
				firstNodeRef = next;//can be null
			} else {
				throw new AssertionError("compromised integrity of list");
			}
		}
		
		if (next != null) {
			next.setPrev(prev);//beware
			killNR.setNext(null);
		} else {
			if (lastNodeRef == killNR) {
				lastNodeRef = prev;//can be null
			} else {
				throw new AssertionError("compromised integrity of list (2)");
			}
		}
		
		killNR.setNode(null);
		cachedSize--;
		return true;
	}

	public boolean containsNodeRef(NodeRef whichNR) {
		Debug.nullException(whichNR);
		NodeRef parser = firstNodeRef;
		while (null != parser) {
			if (whichNR.equals(parser)) {
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}

	
	public NodeRef getNodeRefAt(Location location) {
		switch (location) {
		case FIRST:
			return getFirstNodeRef();
		case LAST:
			return getLastNodeRef();
		default:
			throw new AssertionError("undefined location here.");
		}
	}

	/**
	 * @param location
	 * @param locationNodeRef
	 * @return
	 */
	public NodeRef getNodeRefAt(Location location, NodeRef locationNodeRef) {
		if (locationNodeRef != null){
			if (!this.containsNodeRef(locationNodeRef)) {
				return null;
			}
		}
		switch (location) {
		case BEFORE:
			if (locationNodeRef == null) {
				return getLastNodeRef();
			}
			return locationNodeRef.getPrev();
		case AFTER:
			if (locationNodeRef == null) {
				return getFirstNodeRef();
			}
			return locationNodeRef.getNext();
		case FIRST:
		case LAST:
			return getNodeRefAt(location);
		default:
			throw new AssertionError("undefined location within this context");
		}
	}
	
	//TODO move
	//TODO addFirst
}
