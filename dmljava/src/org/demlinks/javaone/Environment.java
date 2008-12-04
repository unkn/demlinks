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

import java.util.IdentityHashMap;



// at this level the Node objects are given String IDs
// such that a String ID can be referring to only one Node object
// a Node will exist only if it has at least one link
// a link is a tuple of Nodes; link is imaginary so to speak

public class Environment {
	//fields
	private IdentityHashMap<String, Node> allIDNodeTuples; //list of unique elements
	private IdentityHashMap<Node, String> allNodeIDTuples; //list of unique elements
	
	//constructor
	public Environment() {
		allIDNodeTuples = new IdentityHashMap<String, Node>();
		allNodeIDTuples = new IdentityHashMap<Node, String>();
	}
	
	//methods
	public Node getNode(String id) {
		return allIDNodeTuples.get(id);
	}

	public String getID(Node node) {
		return allNodeIDTuples.get(node);
	}
	
	/**
	 * parent -> child   also implied parent <- child connection
	 * @param parentID
	 * @param childID
	 * @return parentNode
	 */
	public Node link(String parentID, String childID) {
		Node _par = ensureNode(parentID);
		Node _chi = ensureNode(childID);
		_par.linkTo(_chi); // this will imply _chi <- _par connections
		return _par;
	}
	
	/**
	 * make sure that node "id" exists in the allNodes list and points to a new or 
	 *  previous Node object
	 * @param id
	 * 
	 * this is basicly allowing an empty Node to exist, hence it must after calling
	 * 	this func. to ensure the new Node (if created) is linked to someother node
	 */
	private Node ensureNode(String id) {
		Node nod = getNode(id);
		if (null == nod) {
			nod = new Node();
			allIDNodeTuples.put(id, nod);
			allNodeIDTuples.put(nod, id);
		}
		return nod;
	}
	
	public int size() {
		return allIDNodeTuples.size();
	}

	/**
	 * remove the id from allNodes only
	 * 
	 * @param id
	 */
	private Node removeNode(String id) {
		Node removedNode = allIDNodeTuples.remove(id);
		allNodeIDTuples.remove(removedNode);
		return removedNode;
		//removedNode.die();
		//removedNode = null; // hopefully helps the garbage collector ?
	}

	public boolean isLink(String parentID, String childID) {
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		if ( (null == _par) || (null == _chi) ) {
			return false;
		}
		return _par.isLinkTo(_chi); // implied _chi.isLinkFrom(_par) same thing
	}
	
	public boolean unlink(String parentID, String childID) {
		if (!isLink(parentID,childID)) {
			return false;
		}
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		_par.unlinkTo(_chi);
		if (_par.isDead()) {
			this.removeNode(parentID);
		}
		if (_chi.isDead()) {
			this.removeNode(childID);
		}
		return true;
	}
}
