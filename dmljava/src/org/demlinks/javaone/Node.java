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
	private UniqueListOfNodes parentsList;//list of all Nodes that point to <this>
	private UniqueListOfNodes childrenList;//list of all Nodes that <this> points to
	Environment environ;
	
	public Node(Environment env) {
		environ = env;
		parentsList = new UniqueListOfNodes(environ, this);
		childrenList = new UniqueListOfNodes(environ, this);
	}

	/**
	 * Makes sure that after the call <tt>this</tt> Node object has <tt>childNode</tt> in its children list.<br>
	 * AND <tt>childNode</tt> has <tt>this</tt> Node object in its parents list.
	 * @param childNode the Node object that will be a child for <tt>this</tt> Node
	 * @return true if the link didn't exist before call
	 */
	public boolean linkTo(Node childNode) {
		//before you link this to the childNode shouldn't we ensure that both this and childNode are mapped within the environment?
		if (null == environ.getID(this)) {
			throw new AssertionError("can't link unmapped Node");
		}
		if (null == environ.getID(childNode)) {
			throw new AssertionError("can't link unmapped Node");
		}
		boolean ret = get(List.CHILDREN).append(childNode);
		childNode.get(List.PARENTS).append(this);
		return ret;
	}

	/**
	 * @param childID
	 * @throws Exception 
	 * @see #linkTo(Node)
	 */
	public void linkTo(String childID) throws Exception {
		environ.link(this, childID);
	}

	
	/**
	 * ensures there's a link from parentNode to <tt>this</tt> Node<br>
	 * @param parentNode the node that will point to us
	 * @return true if the link didn't exist before call
	 */
	public boolean linkFrom(Node parentNode) {
		if (null == environ.getID(this)) {
			throw new AssertionError("can't link unmapped Node");
		}
		if (null == environ.getID(parentNode)) {
			throw new AssertionError("can't link unmapped Node");
		}
		boolean ret = get(List.PARENTS).append(parentNode);
		parentNode.get(List.CHILDREN).append(this);
		return ret;
	}
	
	/**
	 * @param string
	 * @throws Exception 
	 * @see #linkFrom(Node)
	 */
	public void linkFrom(String parentID) throws Exception {
		environ.link(parentID, this);
	}

	
	/**
	 * we will no longer point to <tt>childNode</tt>
	 * @param childNode Node that will be removed from being a child of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkTo(Node childNode) {
		boolean ret = get(List.CHILDREN).remove(childNode);
		childNode.get(List.PARENTS).remove(this);
		return ret;
	}


	/**
	 * <tt>parentNode</tt> will no longer point to <tt>this</tt><br>
	 * @param parentNode Node that will be removed from being a parent of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkFrom(Node parentNode) {
		boolean ret = get(List.PARENTS).remove(parentNode);
		parentNode.get(List.CHILDREN).remove(this);
		return ret;
	}

	/**
	 * checks if <tt>this</tt> points to <tt>childNode</tt>
	 * also checks if <tt>childNode</tt> has <tt>this</tt> pointing to it
	 * @param childNode
	 * @return true is so
	 */
	public boolean isLinkTo(Node childNode) {
		boolean ret = get(List.CHILDREN).contains(childNode);
		boolean ret2 = childNode.get(List.PARENTS).contains(this);
		return (ret && ret2);
	}
	
	/**
	 * @param childID the ID of a child Node object
	 * @return
	 */
	public boolean isLinkTo(String childID) {
		Node childNode = environ.getNode(childID);
		if (null == childNode) {
			//no mapping for that ID
			return false;
		}
		return isLinkTo(childNode);
	}
	
	/**
	 * checks if <tt>parentNode <- this</tt> and if <tt>parentNode -> this</tt>
	 * @param parentNode
	 * @return true if so
	 * @see #isLinkTo(Node)
	 */
	public boolean isLinkFrom(Node parentNode) {
		boolean ret = get(List.PARENTS).contains(parentNode);
		boolean ret2 = parentNode.get(List.CHILDREN).contains(this);
		return ret && ret2;
	}
	
	public boolean isLinkFrom(String parentID) {
		Node parentNode = environ.getNode(parentID);
		if (null == parentNode) {
			return false;
		}
		return isLinkFrom(parentNode);
	}

	
	/**
	 * @return true if the Node object should not exist in the Environment, since it's not part of any links
	 */
	public boolean isDead() {
		return ( (get(List.PARENTS).isEmpty()) && (get(List.CHILDREN).isEmpty()) );
	}
	
	/**
	 * @param list List.CHILDREN or List.PARENTS
	 * @return the specified list object
	 * @throws AssertionError if you specify unknown type of list to be returned
	 */
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
