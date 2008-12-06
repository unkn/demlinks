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

import java.util.ListIterator;




// at this level the Nodes don't have IDs, they're just java objects

public class Node {
	// if both lists are empty the node shouldn't exist (in the Environment)
	// lists should never be null
	//TODO: make a new class for these lists, also an interface for it which may be used somewhere else also
	private LinkedListSet<Node> parentsList;
	private LinkedListSet<Node> childrenList;
	
	public Node() {
		parentsList = new LinkedListSet<Node>();
		childrenList = new LinkedListSet<Node>();
	}


	/**
	 * parentNode -> this, but not also parentNode <- this ! <br>
	 * ensures the link exits
	 * 
	 * @param parentNode
	 * @return <tt>true</tt> if added and didn't previously exist<br>
	 * <tt>false</tt> if already exited hence it remains
	 */
	public boolean linkFrom(Node parentNode) {
		return this.parentsList.add(parentNode); // boolean changed
			// not changed? then it already exited, 
			// if so then maybe bad programming at the caller level? so to assume
	}
	
	/**
	 * this -> childNode, this won't imply this <- childNode link<br>
	 * however this is consistent at this level, but not at the Environment level<br>
	 * at the latter level, both or none links should exits
	 * 
	 * @param childNode
	 * @return same as {@link #linkFrom} 
	 */
	public boolean linkTo(Node childNode) {
		// TODO: we may want to interface (aka public interface) some stuffs like linkTo
		return this.childrenList.add(childNode);
			// false, means collection not changed hence child already existed
	}

	/**
	 * @param childNode
	 * @return true if the child existed before call, now not anymore
	 */
	public boolean unlinkTo(Node childNode) {
		return this.childrenList.remove(childNode);
	}


	/**
	 * @param parentNode
	 * @return see: {@link #unlinkTo(Node)}
	 */
	public boolean unlinkFrom(Node parentNode) {
		return this.parentsList.remove(parentNode);
	}

	/**
	 * <tt>this</tt> -> <tt>childNode</tt> link exist?
	 * @param childNode
	 * @return
	 */
	public boolean isLinkTo(Node childNode) {
		return this.childrenList.contains(childNode);
	}
	
	public boolean isLinkFrom(Node parentNode) {
		return this.parentsList.contains(parentNode);
	}
	
	public boolean isDead() {
		return ( (parentsList.isEmpty()) && (childrenList.isEmpty()) );
	}
	
	public int getChildrenListSize() {
		return childrenList.size();
	}
	
	// TODO: temporarily here:
	public ListIterator<Node> getChildrenListIterator() {
		return childrenList.listIterator();
	}
}
