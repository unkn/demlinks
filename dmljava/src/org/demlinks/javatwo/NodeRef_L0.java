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


public class NodeRef_L0 {
	private NodeRef_L0 prevNodeRef=null, nextNodeRef=null;
	private Node_L0 node=null;
	
	/**
	 * @return the prevNodeRef
	 */
	public NodeRef_L0 getPrevNodeRef() {
		return prevNodeRef;
	}

	/**
	 * @return the nextNodeRef
	 */
	public NodeRef_L0 getNextNodeRef() {
		return nextNodeRef;
	}

	/**
	 * @return the node
	 */
	public Node_L0 getNode() {
		return node;
	}

	
	/**
	 * override this
	 * @param prevNodeRef
	 * @return
	 */
	public boolean setPrevNodeRef(NodeRef_L0 prevNodeRef) {
		this.setPrevNodeRef_L0(prevNodeRef);
		return true;
	}
	
	/**
	 * @param prevNodeRef the prevNodeRef to set
	 */
	public final void setPrevNodeRef_L0(NodeRef_L0 prevNodeRef) {
		this.prevNodeRef = prevNodeRef;
	}

	public boolean setNextNodeRef(NodeRef_L0 nextNodeRef) {
		this.setNextNodeRef_L0(nextNodeRef);
		return true;
	}
	
	/**
	 * @param nextNodeRef the nextNodeRef to set
	 * @throws Exception 
	 */
	public final void setNextNodeRef_L0(NodeRef_L0 nextNodeRef) {
		this.nextNodeRef = nextNodeRef;
	}

	/**
	 * @param node the node to set
	 */
	public boolean setNode(Node_L0 node) {
		return setNode_L0(node);
	}
	
	/**
	 * allows null
	 * @param node
	 * @return
	 */
	public final boolean setNode_L0(Node_L0 node) {
		this.node = node;
		return true;
	}

	public boolean isAlone() {
		return ( (this.getPrevNodeRef() == null) && (this.getNextNodeRef() == null) );
	}
	
}
