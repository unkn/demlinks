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
	public Environment() {
		allIDNodeTuples = new TwoWayHashMap<String, Node>();
	}
	
	//methods
	/**
	 * @param nodeID
	 * @return
	 */
	public Node getNode(String nodeID) {
		emptyError(nodeID);
		return allIDNodeTuples.getValue(nodeID);
	}

	/**
	 * includes {@link #nullError(Object)}
	 * @param anyString
	 */
	private static void emptyError(String anyString) {
		nullError(anyString);
		if (anyString.isEmpty()) {
			throw new AssertionError("should never be empty");
		}
	}

	/**
	 * @param nodeID
	 * @return
	 */
	public boolean isNode(String nodeID) {
		emptyError(nodeID);
		return ( null != getNode(nodeID) );
	}
	
	/**
	 * @param node
	 * @return
	 */
	public boolean isNode(Node node) {
		nullError(node);
		return ( null != getID(node) );
	}
	
	/**
	 * @param node
	 * @return
	 */
	public String getID(Node node) {
		nullError(node);
		return allIDNodeTuples.getKey(node);
	}
	
	/**
	 * Creates mutual links between the two Nodes (it maps String IDs to Nodes)<br>
	 * parent -> child<br>
	 * parent <- child
	 * @return same as {@link #link(Node, Node)}
	 * @throws Exception 
	 * @transaction protected
	 */
	public boolean link(String parentID, String childID) throws Exception {
		nullError(parentID);
		emptyError(parentID);
		nullError(childID);
		emptyError(childID);
		boolean chiExisted=true;
		boolean parExisted=true;
		boolean ret;
		Node _chi=null;
		Node _par=null;
		try {
		//begin transaction
			chiExisted = null != (_chi=getNode(childID));
			if (!chiExisted) {
				//gets created
				_chi = newNode(childID);
				nullError(_chi);
			}
			
			parExisted = null != (_par=getNode(parentID));
			if (!parExisted) {
				_par = newNode(parentID);
				nullError(_par);
			}

			ret=link(_par, _chi);
		} catch (Exception e) {
			if (!chiExisted) {
				if (null == _chi) {
					throw new AssertionError("chiExisted==false hence _chi must be non-null");
				}
				//if didn't exist this means we just created it above and since we're gonna be throwing then let us rollback()
				if (_chi != removeNode(childID)) {
					//it's probably null signaling that it didn't remove!
					throw new AssertionError("this shouldn't happen");
				}
			}
			if (!parExisted) {
				if (null == _chi) {
					throw new AssertionError("parExisted==false hence _par must be non-null");
				}
				if (_par != removeNode(parentID)) {
					throw new AssertionError("should've existed before calling remove!");
				}
			}
			throw e;
		}
		
		return ret;
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return <tt>true</tt> if link didn't exist but now after the call, it should<br>
	 * <tt>false</tt> if link already exits, nothing else done
	 * @throws Exception 
	 */
	public Node link(Node parentNode, Node childNode) throws Exception {
		nullError(parentNode);
		nullError(childNode);
		boolean newLink1=false;
		boolean newLink2=false;
		try { //begin transaction
			newLink1 = parentNode.linkTo(childNode);//true = it didn't previously exist; false= it already did exist;any = exists now after call
			newLink2 = childNode.linkFrom(parentNode);
		} catch (Exception e) {
			//so in effect we should undo before throwing (up) again
			if (newLink1) {
				//must delete it
				if (!parentNode.unlinkTo(childNode)) {
					//false = didn't exist before call, impossible because newLink1=true hence we passed over linkTo which should've created it
					// so if we're here, it was created but it wasn't deleted because it was already gone
					throw new AssertionError("attempted to delete a node that didn't exist to start with. This is serious.");
				}
			}
			if (newLink2) {
				//must delete 2
				if (!childNode.linkFrom(parentNode)) {
					throw new AssertionError("another attempt to delete somenode that didn't exist but it should've existed");
				}
			}
			throw e;
		}
		
		if (newLink1 ^ newLink2) { //( (newLink1 == false) || (newLink2 == false) )
			// if just one of them is true, then inconsistency detected
			// can't already have either of the links w/o the other
			throw new AssertionError("one of the 'small' links already existed without the other");
		}
		return (newLink1 && newLink2); // both must be true, or they're both false if we're here
	}
	
	/**
	 * @param parentNode
	 * @param childID
	 * @return childNode
	 * @throws Exception
	 */
	public Node link(Node parentNode, String childID) throws Exception {
		nullError(parentNode);
		nullError(childID);
		emptyError(childID);
		
		Node _chi = getNode(childID);
		_chi=link(parentNode, _chi);
		mapNode(childID, _chi);
		return _chi;
	}
	//TODO: one more methods for link between Node and ID and between ID and Node
	
	/**
	 * make sure that node "id" exists in the allNodes list and points to a new or 
	 *  previous Node object
	 * @param nodeID
	 * 
	 * this is basically allowing an empty Node to exist, hence it must after calling
	 * 	this method to ensure the new Node (if created) is linked to some other node
	 * @throws Exception 
	 * @transaction protected
	 */
//	private Node ensureNode(String nodeID) throws Exception {
//		Node nod = getNode(nodeID);
//		if (null == nod) {
//			nod = newNode(nodeID);
//		}
//		return nod;
//	}
	
	
//	/**
//	 * better not call this unless u're sure nodeID doesn't exist
//	 * @param nodeID
//	 * @return
//	 * @throws Exception when nodeID existed before<br>
//	 * @transaction protected
//	 */
//	private Node newNode(String nodeID) throws Exception {
//		//lucky it's possible to create ID-Node tuple without being in a link, or else Nodes won't exist unless in a link and so
//		//the nodeID would have to be passed to the childrenList of some Node object and this list would add this nodeID after
//		//it created the ID-Node tuple, and ofc if exception, undo (ID-Node tuple creation - unless it already was there)
//		nullError(nodeID);
//		emptyError(nodeID);
//		Node nod = new Node();
//		if (isNode(nod)) {
//			throw new AssertionError("impossible, this node is new, couldn't've existed");
//		}
//		if (isNode(nodeID)) {
//			throw new Exception("already existing nodeID="+nodeID);
//		}
//		mapNode(nodeID, nod);
//		//allIDNodeTuples.putKeyValue(nodeID, nod); // if this excepts then the "nod" variable will get destroyed by Java anyway
//		return nod;
//	}
	
	/**
	 * @param nodeID
	 * @param node object should already exist outside of call
	 * @throws Exception if the node already exists, either the ID or the Node object in the ID-Node tuple list
	 * @return node
	 */
	protected Node mapNode(String nodeID, Node node) throws Exception {
		nullError(node);
		if (node.isDead()) {
			throw new AssertionError("we shouldn't map empty nodes like that");
		}
		emptyError(nodeID);
		if (isNode(node) || isNode(nodeID)) {
			throw new Exception("one of them already exists, nodeID="+nodeID);
		}
		allIDNodeTuples.putKeyValue(nodeID, node);
		return node;
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
	 * @param nodeID
	 * @throws Exception 
	 */
	private Node removeNode(String nodeID) throws Exception {
		nullError(nodeID);
		emptyError(nodeID);
		return allIDNodeTuples.removeKey(nodeID);
	}

	/**
	 * @param parentID
	 * @param childID
	 * @return see {@link #isLink(Node, Node)}
	 * @throws Exception 
	 */
	public boolean isLink(String parentID, String childID) throws Exception {
		nullError(parentID);
		nullError(childID);
		emptyError(parentID);
		emptyError(childID);
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		if ((null == _par) || (null == _chi) ) {
			return false;
		}
		return isLink(_par, _chi);
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return
	 * @throws Exception 
	 */
	public boolean isLink(Node parentNode, Node childNode) throws Exception {
		nullError(parentNode);
		nullError(childNode);
		return ( parentNode.isLinkTo(childNode) && childNode.isLinkFrom(parentNode));
	}
	
//	private static void nullExcept(Object any) {
//		if (null == any) {
//			throw new NullPointerException("bad programming?");
//		}
//	}
	
	/**
	 * @param anyObject
	 */
	private static void nullError(Object anyObject) {
		if (null == anyObject) {
			throw new AssertionError("should never be null");
		}
	}

	/**
	 * @param parentID
	 * @param childID
	 * <br>see: {@link #unLink(Node, Node)}
	 * @throws Exception 
	 * @returns see {@link #unLink(Node, Node)}
	 */
	public boolean unLink(String parentID, String childID) throws Exception {
		nullError(parentID);
		nullError(childID);
		emptyError(parentID);
		emptyError(childID);
		Node _par = getNode(parentID);
		Node _chi = getNode(childID);
		if ((null == _par) || (null == _chi)) {
			return false;//never existed
		}
		return unLink(_par, _chi);
	}
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @throws Exception 
	 * @returns true = existed and now it's removed<br>
	 * false = didn't exist and hence it still doesn't
	 */
	public boolean unLink(Node parentNode, Node childNode) throws Exception {
		nullError(parentNode);
		nullError(childNode);
		if (!isLink(parentNode, childNode)) {
			return false;
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
			throw new AssertionError();
		}
		return true;
	}

	
}
