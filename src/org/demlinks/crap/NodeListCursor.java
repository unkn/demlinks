package org.demlinks.crap;

import java.util.NoSuchElementException;

import org.demlinks.debug.Debug;

public class NodeListCursor {

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
	 * @return the current Node under the cursor
	 * @throws NoSuchFieldException if current is not defined
	 */
	public Node getCurrent() throws NoSuchFieldException {
		// TODO Auto-generated method stub
		throw new NoSuchFieldException();
		//TODO make own exception here
		//return null;
	}

	/**
	 * @return true if hasCurrent() returns false
	 */
	public boolean isUndefined() {
		// TODO Auto-generated method stub
		return !hasCurrent();
	}

}
