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

import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;



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
	 * parentNode -> this, but not also parentNode <- this !
	 * 
	 * @param parentNode
	 * @throws DuplicateName 
	 * 
	 */
	public void linkFrom(Node parentNode) throws DuplicateName {
		boolean changed = this.parentsList.add(parentNode);
		if (!changed) {
			// then it already exited, if so then maybe bad programming at the caller level?
			throw new DuplicateName();
		}
	}
	
	/**
	 * this -> childNode, this won't imply this <- childNode link
	 * however this is consistent at this level, but not at the Environment level
	 * at the latter level, both or none links should exits
	 * 
	 * @param childNode
	 * @throws DuplicateName 
	 */
	public void linkTo(Node childNode) throws DuplicateName {
		// TODO: we may want to interface (aka public interface) some stuffs like linkTo
		if (!this.childrenList.add(childNode)) {
			// false, means collection not changed hence child already existed
			throw new DuplicateName();
		}
	}

	public void unlinkTo(Node childNode) {
		this.childrenList.remove(childNode);
	}


	public void unlinkFrom(Node parentNode) {
		this.parentsList.remove(parentNode);
	}

	/** 
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
	
	
	public ListIterator<Node> getChildrenListIterator() {
		return childrenList.listIterator();
	}
}
