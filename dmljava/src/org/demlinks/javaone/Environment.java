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


import org.omg.CORBA.ORBPackage.InconsistentTypeCode;



// at this level the Node objects are given String IDs
// such that a String ID can be referring to only one Node object
// a Node will exist only if it has at least one link
// a link is a tuple of Nodes; link is imaginary so to speak

public class Environment {
	//fields
	private TwoWayHashMap<String, Node> allIDNodeTuples; // unique elements
	
	//constructor
	public Environment() {
		allIDNodeTuples = new TwoWayHashMap<String, Node>();
	}
	
	//methods
	/**
	 * @param id
	 * @return
	 */
	public Node getNode(String id) {
		return allIDNodeTuples.getValue(id);
	}

	/**
	 * @param node
	 * @return
	 */
	public String getID(Node node) {
		return allIDNodeTuples.getKey(node);
	}
	
	/**
	 * Creates mutual links between the two Nodes (it maps String IDs to Nodes)<br>
	 * parent -> child<br>
	 * parent <- child
	 * @return same as {@link #link(Node, Node)}
	 * @throws InconsistentTypeCode if either of the two links, which form the transaction,
	 * already exited
	 */
	public boolean link(String parentID, String childID) throws InconsistentTypeCode {
		Node _par = ensureNode(parentID);
		Node _chi = ensureNode(childID);
		return link(_par, _chi);
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return <tt>true</tt> if link didn't exist but now after the call, it should<br>
	 * <tt>false</tt> if link already exits, nothing else done
	 * @throws InconsistentTypeCode 
	 */
	public boolean link(Node parentNode, Node childNode) throws InconsistentTypeCode {
		boolean newLink1 = parentNode.linkTo(childNode);
		boolean newLink2 = childNode.linkFrom(parentNode);
		if (newLink1 ^ newLink2) { //( (newLink1 == false) || (newLink2 == false) )
			// if just one of them is true, then inconsitency detected
			// can't already have either of the links w/o the other
			throw new InconsistentTypeCode();
		}
		return (newLink1 && newLink2); // both must be true
	}
	
	public boolean link(Node parentNode, String childID) throws Exception {
		Node _chi = ensureNode(childID);
		boolean ret;
		try {
			ret=link(parentNode, _chi);
		} catch (Exception e) {
			// TODO: handle exception, this would have to remove _chi if it wasn't already existent before ensureNode above executed
			// but we can't since we don't know if _chi existed before
			throw e;
		} 
		return ret;
	}
	//TODO: one more methods for link between Node and ID and between ID and Node
	
	/**
	 * make sure that node "id" exists in the allNodes list and points to a new or 
	 *  previous Node object
	 * @param id
	 * 
	 * this is basically allowing an empty Node to exist, hence it must after calling
	 * 	this method to ensure the new Node (if created) is linked to some other node
	 */
	private Node ensureNode(String id) {
		Node nod = getNode(id);
		if (null == nod) {
			nod = new Node();
			allIDNodeTuples.putKeyValue(id, nod);
		}
		return nod;
	}
	
	/**
	 * @return number of Nodes in the environment
	 */
	public int size() {
		return allIDNodeTuples.size();
	}

	/**
	 * remove the id from allIDNodeTuples only, it's assumed it's already empty
	 * ie. children/parents lists are empty ('cause only then should it be removed)
	 * doesn't recursively remove
	 * @param id
	 */
	private Node removeNode(String id) {
		return allIDNodeTuples.removeKey(id);
	}

	/**
	 * @param parentID
	 * @param childID
	 * @return see {@link #isLink(Node, Node)}
	 */
	public boolean isLink(String parentID, String childID) {
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		return isLink(_par, _chi);
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return
	 */
	public boolean isLink(Node parentNode, Node childNode) {
		if ( (null == parentNode) || (null == childNode) ) {
			return false;
		}
		return ( parentNode.isLinkTo(childNode) && childNode.isLinkFrom(parentNode));
	}
	
	/**
	 * @param parentID
	 * @param childID
	 * <br>see: {@link #unLink(Node, Node)}
	 * @throws InconsistentTypeCode 
	 */
	public void unLink(String parentID, String childID) throws InconsistentTypeCode {
		unLink(getNode(parentID), getNode(childID));
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @throws InconsistentTypeCode 
	 */
	public void unLink(Node parentNode, Node childNode) throws InconsistentTypeCode {
		if (!isLink(parentNode, childNode)) {
			return;
		}
		boolean removed1 = parentNode.unlinkTo(childNode);
		boolean removed2 = childNode.unlinkFrom(parentNode);
		if (parentNode.isDead()) {
			this.removeNode(this.getID(parentNode));
		}
		if (childNode.isDead()) {
			removeNode(getID(childNode));
		}
		if (removed1 ^ removed2) {
			// Basically we're here because one of the above removals didn't have an element to 
			// remove, in effect proving non-mutual link existed
			throw new InconsistentTypeCode();
		}
	}

	
}
