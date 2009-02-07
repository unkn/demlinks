package org.demlinks.crap;


import org.demlinks.debug.Debug;

public class NodeListCursor {

	NodeList myList = null;
	
	public NodeListCursor(NodeList list) {
		this.myList = list;
	}
	
	public void goTo(Position position) {
		// TODO Auto-generated method stub
		Debug.nullException(position);
	}

	/**
	 * @return true if current is not null
	 */
	public boolean hasCurrent() {
		// TODO Auto-generated method stub
		return getCurrent() != null;
	}

	/**
	 * go to prev Node in list
	 * @return true if succeeded and hasCurrent() after call 
	 */
	public boolean prev() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * go to next Node in list
	 * @return true if call succeeded and hasCurrent() is true after call
	 */
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return the current Node under the cursor<br>
	 * 			null is there isn't any
	 */
	public Node getCurrent() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return true if hasCurrent() returns false
	 */
	public boolean isUndefined() {
		// TODO Auto-generated method stub
		return !hasCurrent();
	}

}
