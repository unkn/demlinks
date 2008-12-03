/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@sourceforge.net>
 	
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


import java.util.Iterator;
import java.util.LinkedHashSet;

// at this level the Nodes don't have IDs, they're just java objects

public class Node {
	// if both lists are empty the node shouldn't exist (in the Environment)
	// lists should never be null
	protected LinkedHashSet<Node> parentsList;
	protected LinkedHashSet<Node> childrenList;
	
	public Node() {
		parentsList = new LinkedHashSet<Node>();
		childrenList = new LinkedHashSet<Node>();
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

/* bad using these here, instead use them in Environment class
  	public void die() {
		removeChildren();
		removeParents();
	}

	public void removeChildren() {
		if ( (childrenList != null) && (false == childrenList.isEmpty()) ) {
			Iterator<Node> itr = childrenList.iterator();
			while (itr.hasNext()) {
				Node current = itr.next();
				this.unlinkTo(current); // this implies current.unlinkFrom(this);
			}
		}
	}
	
	public void removeParents() {
		if ( (parentsList != null) && (false == parentsList.isEmpty()) ) {
			Iterator<Node> itr = parentsList.iterator();
			while (itr.hasNext()) {
				Node current = itr.next();
				this.unlinkFrom(current); // this implies current.unlinkTo(this);
			}
		}
	}
*/
	public void unlinkTo(Node childNode) {
		if (this.childrenList.remove(childNode)) { // true= contained then removed
			childNode.unlinkFrom(this);
			// TODO if (isDead()) must remove from Environment.allNodes too
		}
	}


	public void unlinkFrom(Node parentNode) {
		if (this.parentsList.remove(parentNode)) {
			parentNode.unlinkTo(this);
			// TODO if (isDead()) must remove from Environment.allNodes too
		}
	}

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
}
