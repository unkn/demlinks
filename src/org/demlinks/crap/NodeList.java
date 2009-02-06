package org.demlinks.crap;

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

}
