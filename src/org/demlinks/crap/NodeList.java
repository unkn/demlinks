package org.demlinks.crap;

import javax.naming.CannotProceedException;

import org.demlinks.debug.Debug;

public class NodeList {

	/**
	 * @param nodeToAppend
	 * @return true if node didn't previously exist;
	 * 			false if node existed and hence it could be anywhere in the list
	 * 			not really at the end of it, as the function name implies;
	 * 			it won't be moved to end either
	 */
	public boolean appendNode(Node nodeToAppend) {
		Debug.nullException(nodeToAppend);//why not assert? because param(ie. nodeToAppend) could be dynamically set on runtime
		// TODO Auto-generated method stub
		return false;
	}
	
	public Node getFirstNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Node getLastNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		return this.size() == 0;
	}

	public Node getNodeAfter(Node node) {
		Debug.nullException(node);
		// TODO Auto-generated method stub
		return null;
	}

	public Node getNodeBefore(Node node) {
		Debug.nullException(node);
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNode(Node node) {
		Debug.nullException(node);
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param whichNode
	 * @param whatPos
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
	 * @param node2
	 * @return true if node existed before call, not anymore after the call
	 * 			false if failed to delete because it didn't exist
	 * @throws CannotProceedException if node still exists after call, couldn't delete it
	 */
	public boolean removeNode(Node node) throws CannotProceedException {
		Debug.nullException(node);
		throw new CannotProceedException();
		//TODO make own exception here
		// TODO Auto-generated method stub
		//return false;
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
