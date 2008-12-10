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
	private UniqueListOfNodes parentsList;
	private UniqueListOfNodes childrenList;
	
	public Node() {
		parentsList = new UniqueListOfNodes();
		childrenList = new UniqueListOfNodes();
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
		return get(List.PARENTS).append(parentNode); // boolean changed
			// not changed? then it already exited, 
			// if so then maybe bad programming at the caller level? so to assume
	}
	
	/**
	 * this -> childNode, this won't imply this <- childNode link<br>
	 * however this is consistent at this level, but not at the Environment level<br>
	 * at the latter level, both or none links should exits
	 * 
	 * @param childNode
	 * @return true if link changed as a result of the call, that is a links didn't exist already but it does now<br>
	 * false is link existed and still exists after the call but well nothing changed then.
	 */
	public boolean linkTo(Node childNode) {
		return get(List.CHILDREN).append(childNode);
			// false, means collection not changed hence child already existed
	}

	/**
	 * @param childNode
	 * @return true if the child existed before call, now not anymore
	 */
	public boolean unlinkTo(Node childNode) {
		return get(List.CHILDREN).remove(childNode);
	}


	/**
	 * @param parentNode
	 * @return see: {@link #unlinkTo(Node)}
	 */
	public boolean unlinkFrom(Node parentNode) {
		return get(List.PARENTS).remove(parentNode);
	}

	/**
	 * <tt>this</tt> -> <tt>childNode</tt> link exist?
	 * @param childNode
	 * @return
	 */
	public boolean isLinkTo(Node childNode) {
		return get(List.CHILDREN).contains(childNode);
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
