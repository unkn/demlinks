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

import static org.demlinks.javaone.Environment.nullException;

/**
 * a double-linked list of NodeReferences where no two are alike (no duplicates allowed)<br>
 * these NodeRefs however may contain the same Node objects thus allowing duplicate Nodes at that level<br>
 * but the list itself is comprised of unique NodeReferences<br>
 * no null nodes allowed<br>no null NodeRefs allowed<br>
 * ability to insert anywhere<br>
 * 
 */
public class NodeRefsList {

	private int cachedSize; // cached size, prevents parsing the entire list
	private NodeRef firstNodeRef;//points to first nodeRef in list, or null if empty list
	private NodeRef lastNodeRef;//points to last nodeRef in list, or null if empty list
	
	// constructor
	/**
	 * 
	 */
	NodeRefsList() {
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
	public int getSize() {
		return cachedSize;
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return (0 == getSize()) || (firstNodeRef == null) || (lastNodeRef == null);
	}

	/**
	 * override this
	 * @param nodeRefL1
	 * @return
	 */
	public boolean addLast(NodeRef nodeRef) {
		return addLast_L0(nodeRef);
	}
	
	public final boolean addLast_L0(NodeRef newLastNodeRef) {
		nullException(newLastNodeRef);
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
	 * @param node_L0
	 * @return
	 */
	public NodeRef getNodeRef(Node node_L0) {
		return getNodeRef_L0(node_L0);
	}

	/**
	 * @param node_L0
	 * @return
	 */
	public final NodeRef getNodeRef_L0(Node node_L0) {
		nullException(node_L0);
		NodeRef parser = firstNodeRef;
		while (null != parser) {
			if (node_L0.equals(parser.getNode())) {
				break;
			}
			parser = parser.getNext();
		}
		return parser;
	}

	/**
	 * @param nodeRef
	 * @return
	 */
	public boolean removeNodeRef(NodeRef nodeRef) {
		return removeNodeRef_L0(nodeRef);
	}

	/**
	 * @param killNR
	 * @return
	 */
	public final boolean removeNodeRef_L0(NodeRef killNR) {
		nullException(killNR);
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
				throw new AssertionError("compromised integrity of list2");
			}
		}
		
		cachedSize--;
		return true;
	}

	public boolean containsNodeRef(NodeRef whichNR) {
		nullException(whichNR);
		NodeRef parser = firstNodeRef;
		while (null != parser) {
			if (whichNR.equals(parser)) {
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}

	public boolean containsNodeL0(Node nodeLevel0) {
		return containsNodeL0_L0(nodeLevel0);
	}
	
	public final boolean containsNodeL0_L0(Node nodeLevel0) {
		return (null != getNodeRef(nodeLevel0));
	}

	/**
	 * @return
	 */
	public Node getFirstNode() {
		if (firstNodeRef != null) {
			return firstNodeRef.getNode();
		}
		return null;
	}

	/**
	 * creates a new NodeRef to be added to this list, but it's not added via this method
	 * @param nodeLevel0
	 * @return
	 */
	public NodeRef newNodeRef(Node nodeLevel0) {
		nullException(nodeLevel0);
		NodeRef n = new NodeRef();
		n.setNode(nodeLevel0);
		return n;
	}
}
