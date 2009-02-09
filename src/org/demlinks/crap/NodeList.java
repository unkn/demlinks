package org.demlinks.crap;

import org.demlinks.debug.Debug;
import org.demlinks.references.ObjRefsList;

/**
 * if you do node1.childrenList.add(node2) then this list 
 * won't execute node2.parentsList.add(node1) for you
 * maybe it should, we'll see
 */
public class NodeList extends ObjRefsList<Node> {

	protected NodeList() {
		super();
	}

	/**
	 * @param nodeToAppend
	 * @return true if node didn't previously exist;
	 * 			false if node existed and hence it could be anywhere in the list
	 * 			not really at the end of it, as the function name implies;
	 * 			it won't be moved to end either
	 */
	public boolean appendNode(Node nodeToAppend) {
		Debug.nullException(nodeToAppend);//why not assert? because param(ie. nodeToAppend) could be dynamically set on runtime
		return this.addLast(nodeToAppend);
		//nodeToAppend.getOpposingList().addLast(this.fatherNode);//in opposing list
	}
	
	
	/**
	 * @return null or the first Node in list
	 */
	public Node getFirstNode() {
		return this.getObjectAt(Position.FIRST);
	}
	
	public Node getLastNode() {
		return this.getObjectAt(Position.LAST);
	}

	public Node getNodeAfter(Node node) {
		Debug.nullException(node);
		return this.getObjectAt(Position.AFTER, node);
	}

	public Node getNodeBefore(Node node) {
		Debug.nullException(node);
		return this.getObjectAt(Position.BEFORE, node);
	}

	public boolean hasNode(Node node) {
		Debug.nullException(node);
		return this.containsObject(node);
	}

	/**
	 * @param whichNode
	 * @param whatPos only FIRST/LAST
	 * @return true if whichNode existed before, and it's now still there, not moved<br>
	 * 			false if, whichNode didn't exist and it's now exactly where specified
	 * 			by call
	 */
	public boolean insertNode(Node whichNode, Position whatPos) {
		Debug.nullException(whichNode, whatPos);
		// TODO Auto-generated method stub
		return false;
	}

	public boolean insertAfterNode(Node newNode, Node afterNode) {
		Debug.nullException(newNode, afterNode);
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param node to remove
	 * @return true if node existed before call, not anymore after the call
	 * 			false if failed to delete because it didn't exist
	 */
	public boolean removeNode(Node node) {
		Debug.nullException(node);
		
		boolean ret = this.removeObject(node);
		
		return ret;
	}

	/**
	 * @param newNode
	 * @param beforeNode
	 * @return true if newNode existed before call, and it's still there
	 * 			in the same place as before, 
	 * 			hence it wasn't moved before beforeNode<br>
	 * 		false is newNode didn't exist, but it does now and it's right before
	 * 		beforeNode
	 */
	public boolean insertBeforeNode(Node newNode, Node beforeNode) {
		Debug.nullException(newNode, beforeNode);
		// TODO Auto-generated method stub
		return false;
	}
	

}
