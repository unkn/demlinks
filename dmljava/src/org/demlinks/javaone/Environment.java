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



// at this level the Node objects are given String IDs
// such that a String ID can be referring to only one Node object
// a Node will exist only if it has at least one link
// a link is a tuple of Nodes; link is imaginary so to speak

public class Environment {
	//fields
	private TwoWayIdentityHashMap<String, Node> allIDNodeTuples; // unique elements
	
	//constructor
	public Environment() {
		allIDNodeTuples = new TwoWayIdentityHashMap<String, Node>();
	}
	
	//methods
	public Node getNode(String id) {
		return allIDNodeTuples.getValue(id);
	}

	public String getID(Node node) {
		return allIDNodeTuples.getKey(node);
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
			allIDNodeTuples.putKeyValue(id, nod);
		}
		return nod;
	}
	
	public int size() {
		return allIDNodeTuples.size();
	}

	/**
	 * remove the id from allIDNodeTuples only, it's assumed it's already empty
	 * ie. children/parents lists are empty ('cause only then should it be removed)
	 * 
	 * @param id
	 */
	private Node removeNode(String id) {
		return allIDNodeTuples.removeKey(id);
	}

	public boolean isLink(String parentID, String childID) {
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		if ( (null == _par) || (null == _chi) ) {
			return false;
		}
		return _par.isLinkTo(_chi); // implied _chi.isLinkFrom(_par) same thing
	}
	
	public boolean unLink(String parentID, String childID) {
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
