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

public class NodeRef {
	private NodeRef prev=null;
	private Node_L0 node=null;
	private NodeRef next=null;
	
	public void setNode(Node_L0 nod) {//even if null
		node = nod;
	}
	
	public boolean isAlone() {
		return ((prev == null) && (next == null));
	}
	
	/**
	 * @return the prev
	 */
	public NodeRef getPrev() {
		return prev;
	}

	/**
	 * @param prev the prev to set
	 */
	public void setPrev(NodeRef prev) {
		this.prev = prev;
	}

	/**
	 * @return the next
	 */
	public NodeRef getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(NodeRef next) {
		this.next = next;
	}

	/**
	 * @return the node
	 */
	public Node_L0 getNode() {
		return node;
	}

}
