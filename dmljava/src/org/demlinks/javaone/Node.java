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

/**
 * Node is an object that contains two lists of Node objects<br>
 * called parents and children
 */
public class Node {
	// if both lists are empty the node shouldn't exist (in the Environment)
	// lists should never be null
	private UniqueListOfNodes parentsList;
	private UniqueListOfNodes childrenList;
	
	public Node() {
		parentsList = new UniqueListOfNodes(this);
		childrenList = new UniqueListOfNodes(this);
	}

	/**
	 * Makes sure that after the call <tt>this</tt> Node object has <tt>childNode</tt> in its children list.<br>
	 * AND <tt>childNode</tt> has <tt>this</tt> Node object in its parents list.
	 * @param childNode the Node object that will be a child for <tt>this</tt> Node
	 * @return true if the link didn't exist before call
	 */
	public boolean linkTo(Node childNode) {
		boolean ret = get(List.CHILDREN).append(childNode);
		childNode.get(List.PARENTS).append(this);
		return ret;
	}


	public boolean linkFrom(Node parentNode) {
		return get(List.PARENTS).append(parentNode);
	}
	
	public boolean unLinkTo(Node childNode) {
		return get(List.CHILDREN).remove(childNode);
	}


	public boolean unLinkFrom(Node parentNode) {
		return get(List.PARENTS).remove(parentNode);
	}

	public boolean isLinkTo(Node childNode) {
		return get(List.CHILDREN).contains(childNode);
	}
	
	public boolean isLinkTo(String nodeID) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isLinkFrom(Node parentNode) {
		return get(List.PARENTS).contains(parentNode);
	}
	
	public boolean isDead() {
		return ( (get(List.PARENTS).isEmpty()) && (get(List.CHILDREN).isEmpty()) );
	}
	
	public UniqueListOfNodes get(List list) throws AssertionError {
		switch (list) {
		case CHILDREN:
			return this.childrenList;
		case PARENTS:
			return this.parentsList;
		default:
			throw new AssertionError("Unhandled list type: "+list);
		}
	}


	
}
