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

package org.demlinks.javathree;


/**
 *  at this level the Node objects are given String IDs<br>
 *	such that a String ID can be referring to only one Node object<br>
 *  so there's an 1 to 1 mapping between ID and Node<br>
 *	a Node will exist only if it has at least one link or rather is part of the link<br>
 *	a link is a tuple of Nodes; link is imaginary so to speak<br>
 *	parentID -> childID means: the Node object identified by parentID will have its children list contain the Node object identified by childID<br> 
 *	parentID <- childID means: the Node identified by childID will have its parents list contain the Node object identified by parentID<br>
 *
 */
public class Environment {
	//fields
	private IDToNodeMap allIDNodeTuples; // unique elements
	
	//constructor
	/**
	 * Environment containing ID to Node mappings<br>
	 * ID is {@link String} identifier
	 * Node is a {@link NodeLevel0} object
	 */
	public Environment() {
		super();
		allIDNodeTuples = new IDToNodeMap();
	}
	
	//methods
	
	/**
	 * @return the Node object that's mapped to the ID, if it doesn't exist in the Environment then null
	 */
	public Node getNode(Id nodeID) {
		Debug.nullException(nodeID);
		return allIDNodeTuples.getNode(nodeID);
	}

	/**
	 * @return the ID that is mapped to the Node object, in this environment, or null if there's no such mapping
	 */
	public Id getID(Node node) {
		return allIDNodeTuples.getID(node);
	}
	
	/**
	 * @param nodeID
	 * @param nodeObject
	 * @return false if id was already mapped to a node; true if it wasn't
	 * @throws Exception
	 */
	private boolean internalMapIDToNode(Id nodeID, Node nodeObject) {
		return allIDNodeTuples.put(nodeID, nodeObject);
	}
	
	private void internalUnMapID(Id nodeID) {
		allIDNodeTuples.removeID(nodeID);
	}
	
	/**
	 * @return number of Nodes in the environment
	 */
	public int size() {
		return allIDNodeTuples.size();
	}
	
	/**
	 * this will create a new Node object and map it to the given ID<br>
	 * unless it already exists<br>
	 * 
	 * @param nodeID supposedly unused ID
	 * @return if the ID is already mapped then it will return its respective Node object
	 * @throws Exception
	 */
	public Node ensureNode(Id nodeID) {
		Node n = getNode(nodeID);
		if (null == n) {
			n = new Node();
			if (!internalMapIDToNode(nodeID, n)) {
				throw new AssertionError("overwritten something, which is impossible");
			}
		}
		return n;
	}

	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return
	 */
	private boolean internalLink(Node parentNode, Node childNode) {
		//this method is here to prevent the ie. test suite calling link(node, node)
		//assumes both Nodes exist and are not null params, else expect exceptions
		boolean ret1 = parentNode.linkForward(childNode);
		boolean ret2 = childNode.linkBackward(parentNode);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1;
	}
	
	/**
	 * this will link the two nodes identified by those IDs<br>
	 * if there is no Node for the specified ID it will be created and mapped to it<br>
	 * parentID -> childID (the Node object identified by parentID will have its children list contain the Node object identified by childID)<br> 
	 * parentID <- childID (the Node identified by childID will have its parents list contain the Node object identified by parentID)<br>
	 * @param parentID
	 * @param childID
	 * @throws Exception if ID to Node mapping fails
	 * @transaction protected
	 */
	public boolean link(Id parentID, Id childID) throws Exception {
		//1.it will create empty Node objects if they don't already exist
		//2.map them to IDs
		//3.THEN link them
		
		boolean parentCreated = false;
		Node parentNode = getNode(parentID);//fetch existing Node
		if (null == parentNode) {
			//ah there was no existing Node object with that ID
			//we create a new one
			parentNode = ensureNode(parentID);
//			new Node(this);
			parentCreated = true;
//			internalMapIDToNode(parentID, parentNode);
		}
		
		boolean childCreated = false;
		Node childNode = getNode(childID);//fetch existing Node identified by childID
		if (null == childNode) {
			//nothing existing? create one
			childNode = ensureNode(childID);
			childCreated = true;
			//internalMapIDToNode(childID, childNode);
		}
		
		boolean ret=false;
		try {
			ret = internalLink(parentNode, childNode);//link the Node objects
		} catch (Exception e) {
			try {
				if (parentCreated) {
					//if it was a new Node we just created above then we need to map ID to Node
//					internalUnMapIDToNode(parentID, parentNode);
					removeNode(parentID);
				}

				if (childCreated) {
					removeNode(childID);
				}
			} catch (Exception f) {
				e.printStackTrace();
				throw new AssertionError(f);
			}
			throw e;
		}
		return ret;
	}
	
	/**
	 * remove the mapping between Node and its ID<br>
	 * basically it will unmap the ID from the Node object only if the Node object has no children and no parents
	 * @param nodeID
	 * @return the removed Node
	 */
	public Node removeNode(Id nodeID) {
		Node n = getNode(nodeID);
		if (n == null) {
			throw new AssertionError("attempt to remove a non-existing node ID");
		}
		if (!n.isAlone()) {
			throw new AssertionError("attempt to remove a non-empty node. Clear its lists first!");
		}
		internalUnMapID(nodeID);
		return n;
	}
		
	/**
	 * @param parentID
	 * @param childID
	 * @return
	 */
	public boolean isLink(Id parentID, Id childID) {
		Debug.nullException(parentID, childID);
		Node parentNode = this.getNode(parentID);
		Node childNode = this.getNode(childID);
		if ( (null != parentNode) && (null != childNode) ) {
			return internalIsLink(parentNode, childNode);
		}
		//parent OR child doesn't exist hence neither the link
		return false;
	}

	/**
	 * parentNode -> childNode<br>
	 * parentNode <- childNode<br>
	 * @param parentNode
	 * @param childNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	private boolean internalIsLink(Node parentNode, Node childNode) {
		Debug.nullException(parentNode, childNode);
		boolean one = parentNode.isLinkForward(childNode);
		boolean two = childNode.isLinkBackward(parentNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}
	
	public boolean unLink(Id parentId, Id childId) {
		Debug.nullException(parentId, childId);
		Node parentNode = this.getNode(parentId);
		Node childNode = this.getNode(childId);
		if ((null != parentNode) && (null != childNode)) {
			return internalUnLink(parentNode, childNode);
		}
		return false;
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return true if link existed before call; false if it didn't exist before call; either way it no longer exists after call
	 */
	private boolean internalUnLink(Node parentNode, Node childNode) {
		Debug.nullException(parentNode, childNode);
		boolean one = parentNode.unLinkForward(childNode);
		boolean two = childNode.unLinkBackward(parentNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}
}
