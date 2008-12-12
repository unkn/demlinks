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
	
	@SuppressWarnings("unused")
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
	 */
	public void link(String parentID, String childID) {
		//this will link the two nodes identified by those IDs:
		//the Node identified by parentID will get in its children list the node identified by childID
		//the Node identified by childID will get in its parents list the node identified by parentID
		
	}

}
