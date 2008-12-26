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

import static org.demlinks.javathree.Environment.nullException;

// at this level the Nodes don't have IDs, they're just java objects

/**
 * Node is an object that contains two lists of Node objects<br>
 * called parents and children
 * It only knows to do operations for itself (ie. it won't also link parent to child is a child to parent link is requested via linkFrom)
 * linkTo(child) makes sure only this.childList.has(child) but nothing else like child.parentList.has(this) - that's in NodeLevel1
 */
public class Node {
	//this doesn't change, not while the program is running!
	private static List sense=List.CHILDREN;//default sense ie. A->B means B exists in the "sense" list of A; and -> means forward link
	//'sense' defines the meaning of forward
	
	// if both lists are empty the node shouldn't exist (in the Environment)
	// lists should never be null
	protected NodeRefsList parentsList=null;//list of all Nodes that point to <this>
	protected NodeRefsList childrenList=null;//list of all Nodes that <this> points to
	
	public Node() {
		createLists();
	}

	protected void createLists() {
		parentsList = new NodeRefsList();
		childrenList = new NodeRefsList();
	}

	private boolean putNodeInList(Node node, NodeRefsList list) {
		nullException(node, list);
		return list.addLast(node);
	}
	
	private boolean unPutNodeInList(Node node, NodeRefsList list) {
		nullException(node, list);
		return list.removeNode(node);
	}
	
	
	/**
	 * Makes sure that after the call <tt>this</tt> Node object has <tt>childNode</tt> in its children list.<br>
	 * @param childNode the Node object that will be a child for <tt>this</tt> Node
	 * @return true if the link didn't exist before call
	 */
	public boolean linkForward(Node childNode) {
		return putNodeInList(childNode, getForwardList());
	}
	
	/**
	 * we will no longer point to <tt>childNode</tt>
	 * @param childNode Node that will be removed from being a child of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkForward(Node childNode) {
		return unPutNodeInList(childNode, getForwardList());
	}
	
	/**
	 * ensures there's a link from parentNode to <tt>this</tt> Node<br>
	 * @param parentNode the node that will point to us
	 * @return true if the link didn't exist before call
	 */
	public boolean linkBackward(Node parentNode) {
		return putNodeInList(parentNode, getBackwardList());
	}
	
	/**
	 * <tt>parentNode</tt> will no longer point to <tt>this</tt><br>
	 * @param parentNode Node that will be removed from being a parent of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkBackward(Node parentNode) {
		return unPutNodeInList(parentNode, getBackwardList());
	}

	/**
	 * checks if <tt>this</tt> points to <tt>childNode</tt>
	 * @param childNode
	 * @return true is so
	 */
	public boolean isLinkForward(Node childNode) {
		boolean ret = getForwardList().containsNode(childNode);
		return ret;
	}
	
	/**
	 * checks if <tt>this</tt> is pointed by <tt>parentNode</tt>
	 * @param parentNode
	 * @return true if so
	 * @see #isLinkForward(Node)
	 */
	public boolean isLinkBackward(Node parentNode) {
		boolean ret = getBackwardList().containsNode(parentNode);
		return ret;
	}
	
	/**
	 * @return true if the Node has no children and no parents
	 */
	public final boolean isAlone() {
		return ( (getForwardList().isEmpty()) && (getBackwardList().isEmpty()) );
	}
	
	/**
	 * @param list List.CHILDREN or List.PARENTS
	 * @return the specified list object
	 * @throws AssertionError if you specify unknown type of list to be returned
	 */
	public final NodeRefsList getList(List list) {
		switch (list) {
		case CHILDREN:
			return this.childrenList;
		case PARENTS:
			return this.parentsList;
		default:
			throw new AssertionError("Unhandled list type: "+list);
		}
	}
	
	/**
	 * @return
	 */
	public final NodeRefsList getForwardList() {
		return getList(sense);
	}
	
	/**
	 * @return
	 */
	public final NodeRefsList getBackwardList() {
		switch (sense) {
		case CHILDREN:
			return getList(List.PARENTS);
		case PARENTS:
			return getList(List.CHILDREN);
		default:
			throw new AssertionError("undefined behaviour");
		}
	}
	


} // END of class
