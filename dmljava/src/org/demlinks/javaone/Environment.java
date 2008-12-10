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
		return allIDNodeTuples.getValue(nodeID);
	}

	public boolean isNode(String nodeID) {
		return ( null != getNode(nodeID) );
	}
	
	public boolean isNode(Node node) {
		return ( null != getID(node) );
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
	 * @throws Exception 
	 * @transaction protected
	 */
	public boolean link(String parentID, String childID) throws Exception {
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
				nullCheck(_chi);
			}
			
			parExisted = null != (_par=getNode(parentID));
			if (!parExisted) {
				_par = newNode(parentID);
				nullCheck(_par);
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
	public boolean link(Node parentNode, Node childNode) throws Exception {
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
	 * @return
	 * @throws Exception
	 */
	public boolean link(Node parentNode, String childID) throws Exception {
		boolean existed=true;//so we won't have to remove it, in the catch block assuming try would've been before Node _chi;
		//begin transaction
			Node _chi;
			existed = null != (_chi=getNode(childID));
			if (!existed) {
				//gets created
				_chi = newNode(childID);
			}
		boolean ret;
		try {
			ret=link(parentNode, _chi);
		} catch (Exception e) {
			//handle exception, this would have to remove _chi if it wasn't already existent before ensureNode above executed
			if (!existed) {
				//hence it was created above by newNode() thus we have to remove it here if we were to undo changes aka rollback
				if (_chi != removeNode(childID)) {
					//it's probably null signaling that it didn't remove!
					//e.printStackTrace();
					throw new AssertionError("this shouldn't happen");
				}
			}
			throw e;
		}
		//end transaction
		return ret;
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
	
	
	/**
	 * better not call this unless u're sure nodeID doesn't exist
	 * @param nodeID
	 * @return
	 * @throws Exception when nodeID existed before<br>
	 * @transaction protected
	 */
	private Node newNode(String nodeID) throws Exception {
		Node nod = new Node();
		allIDNodeTuples.putKeyValue(nodeID, nod); // if this excepts then the "nod" variable will get destroyed by Java anyway
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
	 * @throws Exception 
	 */
	private Node removeNode(String id) throws Exception {
		return allIDNodeTuples.removeKey(id);
	}

	/**
	 * @param parentID
	 * @param childID
	 * @return see {@link #isLink(Node, Node)}
	 * @throws Exception 
	 */
	public boolean isLink(String parentID, String childID) throws Exception {
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
		nullCheck(parentNode);
		nullCheck(childNode);
		return ( parentNode.isLinkTo(childNode) && childNode.isLinkFrom(parentNode));
	}
	
	private static void nullCheck(Object any) {
		if (null == any) {
			throw new NullPointerException("bad programming?");
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
		nullCheck(parentNode);
		nullCheck(childNode);
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
