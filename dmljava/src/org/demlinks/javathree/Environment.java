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
		allIDNodeTuples = new IDToNodeMap();
	}
	
	//methods

	/**
	 * @return the Node object that's mapped to the ID, if it doesn't exist in the Environment then null
	 */
	public Node getNode(String nodeID) {
		nullException(nodeID);
		return allIDNodeTuples.getNode(nodeID);
	}

	/**
	 * @return the ID that is mapped to the Node object, in this environment, or null if there's no such mapping
	 */
	public String getID(Node node) {
		return allIDNodeTuples.getID(node);
	}
	
	private void internalMapIDToNode(String nodeID, Node nodeObject) throws Exception {
		allIDNodeTuples.put(nodeID, nodeObject);
	}
	
	private void internalUnMapID(String nodeID) {
		allIDNodeTuples.removeID(nodeID);
	}
	
	private void internalUnMapNode(Node node) {
		allIDNodeTuples.removeNode(node);
	}
	
	/**
	 * @return number of Nodes in the environment
	 */
	public int size() {
		return allIDNodeTuples.size();
	}

	/**
	 * @param anyObject one or more objects to be tested if they're null, if so then we throw AssertionError
	 */
	public static void nullException(Object... anyObject) {
		for (int i = 0; i < anyObject.length; i++) {
			if (null == anyObject[i]) {
				throw new NullPointerException("should never be null:"+anyObject[i]+" ["+i+"]");
			}
		}
	}
	
	/**
	 * this will create a new Node object and map it to the given ID<br>
	 * unless it already exists<br>
	 * 
	 * @param nodeID supposedly unused ID
	 * @return if the ID is already mapped then it will return its respective Node object
	 * @throws Exception
	 */
	public Node ensureNode(String nodeID) throws Exception {
		Node n = getNode(nodeID);
		if (null == n) {
			n = new Node();
			internalMapIDToNode(nodeID, n);
		}
		return n;
	}

	/**
	 * this will link the two nodes identified by those IDs<br>
	 * if there is no Node for the specified ID it will be created and mapped to it<br>
	 * parentID -> childID (the Node object identified by parentID will have its children list contain the Node object identified by childID)<br> 
	 * parentID <- childID (the Node identified by childID will have its parents list contain the Node object identified by parentID)<br>
	 * @param parentID
	 * @param childID
	 * @throws Exception if ID to Node mapping fails
	 */
	public boolean link(String parentID, String childID) throws Exception {
		//1.it will create empty Node objects if they don't already exist
		//2.link them
		//3.and THEN map them to IDs
		
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
					removeNode(parentNode);
				}

				if (childCreated) {
					removeNode(childNode);
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
	public Node removeNode(String nodeID) {
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
	 * @param node
	 * @return
	 * @see #removeNode(String)
	 */
	public String removeNode(Node node) {
		String id = getID(node);
		if (null == id) { //doesn't exist
			throw new AssertionError("attempt to remove a node object that doesn't exist in this environment");
		}
		//exists
		if (!node.isAlone()) {
			throw new AssertionError("attempt to remove an existing node that still has children/parents");
		}
		//exists and is alone
		internalUnMapNode(node);
		return id;
	}

	/**
	 * @param parentID
	 * @param childNode
	 * @throws Exception
	 * @see #link(String, String)
	 */
	public boolean link(String parentID, Node childNode) throws Exception {
		String childID = getID(childNode);
		if (null == childID) {
			throw new AssertionError("childNode isn't mapped in this environment");
		}
		return link(parentID, childID);
	}
	
	/**
	 * @param parentNode
	 * @param childID
	 * @throws Exception
	 */
	public boolean link(Node parentNode, String childID) throws Exception {
		String parentID = getID(parentNode);
		if (null == parentID) {
			throw new AssertionError("parentNode isn't mapped in this environment");
		}
		return link(parentID, childID);
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 */
	public boolean link(Node parentNode, Node childNode) {
		if ( (null == getID(parentNode)) || (null == getID(childNode)) ) {
			throw new AssertionError("one or both nodes are not mapped within this environment");
		}
		return internalLink(parentNode, childNode);
	}
	
	private boolean internalLink(Node parentNode, Node childNode) {
		//assumes both Nodes exist and are not null params, else expect exceptions
		boolean ret1 = parentNode.linkTo(childNode);
		boolean ret2 = childNode.linkFrom(parentNode);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1;
	}

	/**
	 * parentNode -> childNode<br>
	 * parentNode <- childNode<br>
	 * @param parentNode
	 * @param childNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	public boolean isLink(Node parentNode, Node childNode) {
		nullException(parentNode, childNode);
		boolean one = parentNode.isLinkTo(childNode);
		boolean two = childNode.isLinkFrom(parentNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}
	
	/**
	 * @param parentNode
	 * @param childID
	 * @return
	 */
	public boolean isLink(Node parentNode, String childID) {
		nullException(parentNode, childID);
		Node childNode = getNode(childID);
		if (null != childNode) {
			return isLink(parentNode, childNode);
		}
		return false;
	}
	
	/**
	 * @param parentID
	 * @param childNode
	 * @return
	 */
	public boolean isLink(String parentID, Node childNode) {
		nullException(parentID, childNode);
		Node parentNode = getNode(parentID);
		if (null != parentNode) {
			return isLink(parentNode, childNode);
		}
		return false;
	}
	
	/**
	 * @param parentID
	 * @param childID
	 * @return
	 */
	public boolean isLink(String parentID, String childID) {
		nullException(parentID, childID);
		Node parentNode = this.getNode(parentID);
		if (null != parentNode) {
			return isLink(parentNode, childID);
		}
		//parent doesn't exist hence neither the link
		return false;
	}

}
