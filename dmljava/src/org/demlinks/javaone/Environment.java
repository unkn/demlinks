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
	
	@SuppressWarnings("unused")
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
				throw new AssertionError("should never be null:"+anyObject[i]+" [i]");
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
			parentNode = new Node();
			parentCreated = true;
		}
		
		boolean childCreated = false;
		Node childNode = getNode(childID);//fetch existing Node identified by childID
		if (null == childNode) {
			//nothing existing? create one
			childNode = new Node();
			childCreated = true;
		}
		
		link(parentNode, childNode);//link the Node objects
		
		if (parentCreated) {
			//if it was a new Node we just created above then we need to map ID to Node
			internalMapIDToNode(parentID, parentNode);
		}
		
		if (childCreated) {
			internalMapIDToNode(childID, childNode);
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
			parentNode = new Node();
			parentCreated = true;
		}
		
		link(parentNode, childNode);
		
		if (parentCreated) {
			//if it was a new Node we just created above then we need to map ID to Node
			internalMapIDToNode(parentID, parentNode);
		}
	}
	public void link(Node parentNode, String childID) throws Exception {
		boolean childCreated = false;
		Node childNode = getNode(childID);//fetch existing Node identified by childID
		if (null == childNode) {
			//nothing existing? create one
			childNode = new Node();
			childCreated = true;
		}
		
		link(parentNode, childNode);
		
		if (childCreated) {
			internalMapIDToNode(childID, childNode);
		}
	}
	public void link(Node parentNode, Node childNode) {
		//if we're here, either other link() methods above called us, and both nodes now exist except they could be not mapped if created by other sister link() method
		// OR main() called us and we don't know if nodes exist
		//so we have to check if they exist:
		// but we can't use getID(node) because they may not be mapped yet (by sister link() methods that called us)
		Environment.nullError(parentNode, childNode);//at least make sure they're not null instead of objects
		//so we don't know if any other sister link() called us to assume the nodes will exist when we return to caller(sister link() method) and the caller maps them
		//hence we should make an internalLink(Node,Node) that our sisters and this link() will call that will assume Node(s) exist
	}

	/**
	 * parentNode -> childNode<br>
	 * parentNode <- childNode<br>
	 * @param parentNode
	 * @param childNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	public boolean isLink(Node parentNode, Node childNode) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isLink(Node parentNode, String childID) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isLink(String parentID, Node childNode) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isLink(String parentID, String childID) {
		// TODO Auto-generated method stub
		return false;
	}

}
