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

import java.util.NoSuchElementException;

import org.demlinks.debug.Debug;
import org.demlinks.references.Reference;


/**
 *  at this level the Node objects are given String IDs<br>
 *	such that a String ID can be referring to only one Node object<br>
 *  so there's an 1 to 1 mapping between ID and Node<br>
 *	a Node will exist only if it has at least one link or rather is part of the link<br>
 *	a link is a tuple of Nodes; link is imaginary so to speak<br>
 *	sourceID -> destinationID means: the Node object identified by sourceID will have its forward list contain the Node object identified by destinationID<br> 
 *	sourceID <- destinationID means: the Node identified by destinationID will have its backwards list contain the Node object identified by sourceID<br>
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
	 * @param nodeID
	 * @return true if the node exists in this environment, doesn't matter if it has any forward/backwards
	 */
	public boolean isNode(Id nodeID) {
		Debug.nullException(nodeID);
		return (null != getNode(nodeID));
	}
	
	/**
	 * @return the Node object that's mapped to the ID, if it doesn't exist in the Environment then null
	 */
	private Node getNode(Id nodeID) {
		Debug.nullException(nodeID);
		return allIDNodeTuples.getNode(nodeID);
	}

	/**
	 * @return the ID that is mapped to the Node object, in this environment, or null if there's no such mapping
	 */
	private Id getID(Node node) {
		Debug.nullException(node);
		return allIDNodeTuples.getID(node);//should be useful when parsing
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
	private Node ensureNode(Id nodeID) {
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
	 * @param sourceNode
	 * @param destinationNode
	 * @return
	 */
	private boolean internalLinkForward(Node sourceNode, Node destinationNode) {
		//this method is here to prevent the ie. test suite calling link(node, node)
		//assumes both Nodes exist and are not null params, else expect exceptions
		boolean ret1 = sourceNode.linkForward(destinationNode);
		boolean ret2 = destinationNode.linkBackward(sourceNode);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1;
	}
	
	/**
	 * this will link the two nodes identified by those IDs<br>
	 * this will link forward sourceID to destinationID<br>
	 * and also link backward destinationID to sourceID<br>
	 * if there is no Node for the specified ID it will be created and mapped to it<br>
	 * there will be no linkBackward() because it would be just a matter of exchanging parameter places<br>
	 * sourceID -> destinationID (the Node object identified by sourceID will have its forward list contain the Node object identified by destinationID)<br> 
	 * sourceID <- destinationID (the Node identified by destinationID will have its backwards list contain the Node object identified by sourceID)<br>
	 * @param sourceID ie. backward
	 * @param destinationID ie. forward
	 * @throws Exception if ID to Node mapping fails
	 * @transaction protected
	 */
	public boolean linkForward(Id sourceID, Id destinationID) throws Exception {
		//1.it will create empty Node objects if they don't already exist
		//2.map them to IDs
		//3.THEN link them
		
		boolean sourceCreated = false;
		Node sourceNode = getNode(sourceID);//fetch existing Node
		if (null == sourceNode) {
			//ah there was no existing Node object with that ID
			//we create a new one
			sourceNode = ensureNode(sourceID);
			sourceCreated = true;
		}
		
		boolean destinationCreated = false;
		Node destinationNode = getNode(destinationID);//fetch existing Node identified by destinationID
		if (null == destinationNode) {
			//nothing existing? create one
			destinationNode = ensureNode(destinationID);
			destinationCreated = true;
		}
		
		boolean ret=false;
		try {
			ret = internalLinkForward(sourceNode, destinationNode);//link the Node objects
		} catch (Exception e) {
			try {
				if (sourceCreated) {
					removeNode(sourceID);
				}

				if (destinationCreated) {
					removeNode(destinationID);
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
	 * basically it will unmap the ID from the Node object only if the Node object has no forward and no backwards
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
	 * @param sourceID
	 * @param destinationID
	 * @return
	 */
	public boolean isLinkForward(Id sourceID, Id destinationID) {
		Debug.nullException(sourceID, destinationID);
		Node sourceNode = this.getNode(sourceID);
		Node destinationNode = this.getNode(destinationID);
		if ( (null != sourceNode) && (null != destinationNode) ) {
			return internalIsLinkForward(sourceNode, destinationNode);
		}
		//backward OR forward doesn't exist hence neither the link
		return false;
	}

	/**
	 * sourceNode -> destinationNode<br>
	 * sourceNode <- destinationNode<br>
	 * @param sourceNode
	 * @param destinationNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	private boolean internalIsLinkForward(Node sourceNode, Node destinationNode) {
		Debug.nullException(sourceNode, destinationNode);
		boolean one = sourceNode.isLinkForward(destinationNode);
		boolean two = destinationNode.isLinkBackward(sourceNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}
	
	public boolean unLinkForward(Id backwardId, Id forwardId) {
		Debug.nullException(backwardId, forwardId);
		Node sourceNode = this.getNode(backwardId);
		Node destinationNode = this.getNode(forwardId);
		if ((null != sourceNode) && (null != destinationNode)) {
			return internalUnLinkForward(sourceNode, destinationNode);
		}
		return false;
	}
	
	/**
	 * @param sourceNode
	 * @param destinationNode
	 * @return true if link existed before call; false if it didn't exist before call; either way it no longer exists after call
	 */
	private boolean internalUnLinkForward(Node sourceNode, Node destinationNode) {
		Debug.nullException(sourceNode, destinationNode);
		boolean one = sourceNode.unLinkForward(destinationNode);
		boolean two = destinationNode.unLinkBackward(sourceNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}

	public int getSize(Id nodeID, List list) {
		Node n = getNode(nodeID);
		if (null == n) {
			throw new NoSuchElementException("inexistent Node, in the environment");
		}
		return n.getList(list).size();
	}

	public NodeParser getParser(Id nodeID, List list, Location location) {
		Parser p = new Parser(nodeID, list, location);
		return p;
	}
	
	private class Parser implements NodeParser {

		Reference<Node> current = null;
		NodeRefsList nrl = null;
		//TODO parser for NodeRefsList
		//TODO remove L1 and L2 from NodeRefsList by generalizing to RefsList or so
		public Parser(Id nodeID, List list, Location location) {
			//this(nodeID, list, location, null);
			Debug.nullException(nodeID, list, location);
			Node n = getNode(nodeID);
			Debug.nullException(n);
			nrl = n.getList(list);
			Debug.nullException(nrl);
			current = nrl.getNodeRefAt(location);//could be null
		}

		@Override
		public Id getCurrentID() {
			if (current == null) {
				return null;
			}
			Node n = current.getObject();
			Id i = getID(n);
			return i;//could be null
		}

		@Override
		public void go(Location location) {
			//TODO when list is modified, add a variable that's incremented on add/replace/move in NodeRefsList_L1, copy it here
			current = nrl.getNodeRefAt(location, current);
		}
		
	}
}
