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

package org.demlinks.javatwo;

import org.demlinks.javaone.Environment;

/**
 * a double-linked list of NodeReferences where no two are alike (no duplicates allowed)<br>
 * these NodeRefs however may contain the same Node objects thus allowing duplicate Nodes at that level<br>
 * but the list itself is comprised of unique NodeReferences<br>
 * no null nodes allowed<br>no null NodeRefs allowed<br>
 * ability to insert anywhere<br>
 * 
 */
public class NodeRefsList_L0 {

	private int cachedSize; // cached size, prevents parsing the entire list
	private NodeRef_L1 firstNodeRef;//points to first nodeRef in list, or null if empty list
	private NodeRef_L1 lastNodeRef;//points to last nodeRef in list, or null if empty list
	
	// constructor
	/**
	 * 
	 */
	NodeRefsList_L0() {
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
	public boolean addLast(NodeRef_L1 nodeRefL1) {
		return addLast_L0(nodeRefL1);
	}
	
	/**
	 * @param nodeRefL1
	 * @return
	 */
	public final boolean addLast_L0(NodeRef_L1 nodeRefL1) {
		boolean ret = nodeRefL1.setPrevNodeRef(lastNodeRef);
		if (ret) {
			if (lastNodeRef == null) {
				lastNodeRef = nodeRefL1;
			}
			if (firstNodeRef == null) {
				firstNodeRef = nodeRefL1;
			}
			cachedSize++;
		}
		return ret;
	}

	/**
	 * created a new NodeRef for this list but doesn't link it to anything<br>
	 * it's basically to preserve the type of NodeRef<br>
	 * @param node_L0
	 * @return
	 */
	public NodeRef_L1 newNodeRef(Node_L0 node_L0) {
		Environment.nullException(node_L0);
		NodeRef_L1 tmp= new NodeRef_L1();
		tmp.setNode(node_L0);
		return tmp;
	}
	
	/**
	 * @param node_L0
	 * @return
	 */
	public NodeRef_L1 getNodeRef(Node_L0 node_L0) {
		return getNodeRef_L0(node_L0);
	}

	/**
	 * @param node_L0
	 * @return
	 */
	public final NodeRef_L1 getNodeRef_L0(Node_L0 node_L0) {
		Environment.nullException(node_L0);
		NodeRef_L1 parser = firstNodeRef;
		while (null != parser) {
			if (node_L0.equals(parser.getNode())) {
				break;
			}
			parser = (NodeRef_L1) parser.getNextNodeRef();
		}
		return parser;
	}

	/**
	 * @param nodeRef_L1
	 * @return
	 */
	public boolean removeNodeRef(NodeRef_L1 nodeRef_L1) {
		return removeNodeRef_L0(nodeRef_L1);
	}

	/**
	 * @param nodeRef_L1
	 * @return
	 */
	public final boolean removeNodeRef_L0(NodeRef_L1 nodeRef_L1) {
		Environment.nullException(nodeRef_L1);
		if (firstNodeRef == nodeRef_L1) {
			firstNodeRef = (NodeRef_L1) nodeRef_L1.getNextNodeRef();
		}
		if (lastNodeRef == nodeRef_L1) {
			lastNodeRef = (NodeRef_L1) nodeRef_L1.getPrevNodeRef();
		}
		boolean ret = nodeRef_L1.selfRemove();
		cachedSize--;
		return ret;
	}

	public boolean containsNodeL0(Node_L0 nodeLevel0) {
		return containsNodeL0_L0(nodeLevel0);
	}
	
	public final boolean containsNodeL0_L0(Node_L0 nodeLevel0) {
		return (null != getNodeRef(nodeLevel0));
	}

	/**
	 * @return
	 */
	public Node_L0 getFirstNode() {
		if (firstNodeRef != null) {
			return firstNodeRef.getNode();
		}
		return null;
	}
}
