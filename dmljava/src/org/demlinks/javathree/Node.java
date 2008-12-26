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

	
	/**
	 * Makes sure that after the call <tt>this</tt> Node object has <tt>childNode</tt> in its children list.<br>
	 * @param childNode the Node object that will be a child for <tt>this</tt> Node
	 * @return true if the link didn't exist before call
	 */
	public boolean linkTo(Node childNode) {//TODO persistent return into Environment class methods
		return linkTo_L0(childNode);
	}
		
	public final boolean linkTo_L0(Node childNode) {
		nullException(childNode);
		NodeRefsList list = get(List.CHILDREN);
		boolean ret = list.addLast(childNode);
		return ret;
	}
	

	/**
	 * ensures there's a link from parentNode to <tt>this</tt> Node<br>
	 * @param parentNode the node that will point to us
	 * @return true if the link didn't exist before call
	 */
	public boolean linkFrom(Node parentNode) {
		return linkFrom_L0(parentNode);
	}
	
	public final boolean linkFrom_L0(Node parentNode) {
		NodeRefsList list = get(List.PARENTS);
		boolean ret = list.addLast(parentNode);
		return ret;
	}
	
	/**
	 * we will no longer point to <tt>childNode</tt>
	 * @param childNode Node that will be removed from being a child of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkTo(Node childNode) {
		return unLinkTo_L0(childNode);
	}
	
	public final boolean unLinkTo_L0(Node childNode) {
		NodeRefsList list = get(List.CHILDREN);
		boolean ret = list.removeNode( childNode );
		return ret;
	}


	/**
	 * <tt>parentNode</tt> will no longer point to <tt>this</tt><br>
	 * @param parentNode Node that will be removed from being a parent of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkFrom(Node parentNode) {
		return unLinkFrom_L0(parentNode);
	}
	
	public final boolean unLinkFrom_L0(Node parentNode) {
		NodeRefsList list = get(List.PARENTS);
		boolean ret = list.removeNode(parentNode);;
		return ret;
	}

	/**
	 * checks if <tt>this</tt> points to <tt>childNode</tt>
	 * @param childNode
	 * @return true is so
	 */
	public boolean isLinkTo(Node childNode) {
		return isLinkTo_L0(childNode);
	}
	
	public final boolean isLinkTo_L0(Node childNode) {
		boolean ret = get(List.CHILDREN).containsNode(childNode);
		return ret;
	}
	
	/**
	 * checks if <tt>this</tt> is pointed by <tt>parentNode</tt>
	 * @param parentNode
	 * @return true if so
	 * @see #isLinkTo(Node)
	 */
	public boolean isLinkFrom(Node parentNode) {
		return isLinkFrom_L0(parentNode);
	}
	
	public final boolean isLinkFrom_L0(Node parentNode) {
		boolean ret = get(List.PARENTS).containsNode(parentNode);
		return ret;
	}
	
	/**
	 * @return true if the Node has no children and no parents
	 */
	public final boolean isAlone() {
		return ( (get(List.PARENTS).isEmpty()) && (get(List.CHILDREN).isEmpty()) );
	}
	
	/**
	 * @param list List.CHILDREN or List.PARENTS
	 * @return the specified list object
	 * @throws AssertionError if you specify unknown type of list to be returned
	 */
	public final NodeRefsList get(List list) throws AssertionError {
		switch (list) {
		case CHILDREN:
			return this.childrenList;
		case PARENTS:
			return this.parentsList;
		default:
			throw new AssertionError("Unhandled list type: "+list);
		}
	}


} // END of class
