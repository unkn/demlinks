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



// at this level the Nodes don't have IDs, they're just java objects

public class Node {
	// if both lists are empty the node shouldn't exist (in the Environment)
	// lists should never be null
	private LinkedListSet<Node> parentsList;
	private LinkedListSet<Node> childrenList;
	
	public Node() {
		parentsList = new LinkedListSet<Node>();
		childrenList = new LinkedListSet<Node>();
	}


	/**
	 * parentNode -> this
	 * 
	 * @param parentNode
	 * 
	 */
	public void linkFrom(Node parentNode) {
		if (this.parentsList.add(parentNode)) {
			// true if didn't already had it
			parentNode.linkTo(this);
		}
	}
	
	/**
	 * this -> childNode
	 * 
	 * @param childNode
	 */
	public void linkTo(Node childNode) {
		if (this.childrenList.add(childNode)) {
			// true if didn't already had it
			childNode.linkFrom(this);
		}
	}

	public void unlinkTo(Node childNode) {
		if (this.childrenList.remove(childNode)) { // true= contained then removed
			childNode.unlinkFrom(this);
		}
	}


	public void unlinkFrom(Node parentNode) {
		if (this.parentsList.remove(parentNode)) {
			parentNode.unlinkTo(this);
		}
	}

	/** 
	 * @param childNode
	 * @return
	 */
	public boolean isLinkTo(Node childNode) {
		if (this.childrenList.contains(childNode)) {
			return true;
		}
		return false;
	}
	
	public boolean isLinkFrom(Node parentNode) {
		if (this.parentsList.contains(parentNode)) {
			return true;
		}
		return false;
	}
	
	public boolean isDead() {
		return ( (parentsList.isEmpty()) && (childrenList.isEmpty()) );
	}


	/**
	 * @return the parentsList
	 */
	public LinkedListSet<Node> getParentsList() {
		return parentsList;
	}


	/**
	 * @return the childrenList
	 */
	public LinkedListSet<Node> getChildrenList() {
		return childrenList;
	}
	
}
