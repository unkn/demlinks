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
	private TwoWayHashMap<String, Node> allIDNodeTuples; // unique elements
	
	//constructor
	/**
	 * Environment containing ID to Node mappings<br>
	 * ID is {@link String} identifier
	 * Node is a {@link Node} object
	 */
	public Environment() {
		allIDNodeTuples = new TwoWayHashMap<String, Node>();
	}
	
	//methods

	/**
	 * @return the Node object that's mapped to the ID, if it doesn't exist in the Environment then null
	 */
	public Node getNode(String nodeID) {
		return allIDNodeTuples.getValue(nodeID);
	}

	/**
	 * @return the ID that is mapped to the Node object, in this environment, or null if there's no such mapping
	 */
	public String getID(Node node) {
		return allIDNodeTuples.getKey(node);
	}
	
	private void internalMapIDToNode(String nodeID, Node nodeObject) throws Exception {
		allIDNodeTuples.putKeyValue(nodeID, nodeObject);
	}
	
	private void internalUnMapIDToNode(String nodeID, Node node) {
		allIDNodeTuples.removeKey(nodeID);
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
	public static void nullError(Object... anyObject) {
		for (int i = 0; i < anyObject.length; i++) {
			if (null == anyObject[i]) {
				throw new NullPointerException("should never be null:"+anyObject[i]+" [i]");
			}
		}
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
	public void link(String parentID, String childID) throws Exception {
		//1.it will create empty Node objects if they don't already exist
		//2.link them
		//3.and THEN map them to IDs
		
		boolean parentCreated = false;
		Node parentNode = getNode(parentID);//fetch existing Node
		if (null == parentNode) {
			//ah there was no existing Node object with that ID
			//we create a new one
			parentNode = new Node(this);
			parentCreated = true;
			internalMapIDToNode(parentID, parentNode);
		}
		
		boolean childCreated = false;
		Node childNode = getNode(childID);//fetch existing Node identified by childID
		if (null == childNode) {
			//nothing existing? create one
			childNode = new Node(this);
			childCreated = true;
			internalMapIDToNode(childID, childNode);
		}
		
		try {
			internalLink(parentNode, childNode);//link the Node objects
		} catch (Exception e) {
			try {
				if (parentCreated) {
					//if it was a new Node we just created above then we need to map ID to Node
					internalUnMapIDToNode(parentID, parentNode);
				}

				if (childCreated) {
					internalUnMapIDToNode(childID, childNode);
				}
			} catch (Exception f) {
				e.printStackTrace();
				throw new AssertionError(f);
			}
			throw e;
		}
	}
	
	/**
	 * @param parentID
	 * @param childNode
	 * @throws Exception
	 * @see #link(String, String)
	 */
	public void link(String parentID, Node childNode) throws Exception {
		boolean parentCreated = false;
		Node parentNode = getNode(parentID);//fetch existing Node
		if (null == parentNode) {
			//ah there was no existing Node object with that ID
			//we create a new one
			parentNode = new Node(this);
			parentCreated = true;
			internalMapIDToNode(parentID, parentNode);
		}
		
		if (null == getID(childNode)) {
			throw new AssertionError("childNode isn't mapped in this environment");
		}
		
		try {
			internalLink(parentNode, childNode);
		} catch (Exception e) {
			try {
				if (parentCreated) {
					//if it was a new Node we just created above then we need to map ID to Node
					internalUnMapIDToNode(parentID, parentNode);
				}
			}catch (Exception f) {
				e.printStackTrace();
				throw new AssertionError(f);
			}
			throw e;
		}
	}
	public void link(Node parentNode, String childID) throws Exception {
		boolean childCreated = false;
		Node childNode = getNode(childID);//fetch existing Node identified by ID
		if (null == childNode) {
			//nothing existing? create one
			childNode = new Node(this);
			childCreated = true;
			internalMapIDToNode(childID, childNode);
		}
		
		if (null == getID(parentNode)) {
			throw new AssertionError("parentNode isn't mapped in this environment");
		}

		try {
			internalLink(parentNode, childNode);
		} catch (Exception e) {
			try {
				if (childCreated) {
					internalUnMapIDToNode(childID, childNode);
				}
			}catch (Exception f) {
				throw new AssertionError(f);
			}
			throw e;
		}
	}
	public void link(Node parentNode, Node childNode) {
		if ( (null == getID(parentNode)) || (null == getID(childNode)) ) {
			throw new AssertionError("one or both nodes are not mapped within this environment");
		}
		internalLink(parentNode, childNode);
	}
	
	private void internalLink(Node parentNode, Node childNode) {
		//assumes both Nodes exist and are not null params, else except exceptions
		parentNode.linkTo(childNode);//we assume this also links childNode to parentNode  
	}

	/**
	 * parentNode -> childNode<br>
	 * parentNode <- childNode<br>
	 * @param parentNode
	 * @param childNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	public boolean isLink(Node parentNode, Node childNode) {
		return parentNode.isLinkTo(childNode);
	}
	public boolean isLink(Node parentNode, String childID) {
		return parentNode.isLinkTo(childID);
	}
	public boolean isLink(String parentID, Node childNode) {
		return childNode.isLinkFrom(parentID);
	}
	public boolean isLink(String parentID, String childID) {
		Node parentNode = this.getNode(parentID);
		if (null != parentNode) {
			//at least the node exists
			return isLink(parentNode, childID);
		} else {
			//parent doesn't exist hence neither the link
			return false;
		}
	}

}
