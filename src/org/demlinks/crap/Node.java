package org.demlinks.crap;

import org.omg.CORBA.ORBPackage.InconsistentTypeCode;

public class Node {
	NodeList parentsList;
	NodeList childrenList;

	public Node() {
		parentsList = new NodeList();
		childrenList = new NodeList();
	}

	/**
	 * creates both links:
	 * this -> child  : "this" will know it has a child "child"
	 * this <- child  : and the child "child" will know it has a parent "this"
	 * @param child
	 * @return true if child existed before call and hence it could be anywhere
	 *         in the list not necessarily at the end of it; false if it didn't
	 *         exist and it is now (after call) at the end(appended) of the list
	 *         also note that if child existed then so did "this"(the parent) 
	 *         for the child
	 * @throws InconsistentTypeCode if half of the link existed before call
	 */
	public boolean appendChild(Node child) throws InconsistentTypeCode {
		boolean existed1 = this.internalAppendChild(child);
		boolean existed2 = child.internalAppendParent(this);
		if (existed1 ^ existed2) {
			//if either one existed, then inconsistent link detected
			//somewhere something made a boo boo
			throw new InconsistentTypeCode("inconsistent link detected");
		}
		return existed1;//should be same value as existed2
	}
	
	/**
	 * @param child
	 * @return true if child didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendChild(Node child) {
		return this.childrenList.appendNode(child);
	}
	
	/**
	 * @param parent
	 * @return true if parent didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendParent(Node parent) {
		return this.parentsList.appendNode(parent);
	}

	/**
	 * this -> child ?
	 * 
	 * @param child
	 * @return true if <tt>this</tt> has <tt>child</tt> in its children list   
	 */
	public boolean hasChild(Node child) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * parent <- this ?
	 * 
	 * @param parent
	 * @return true if <tt>this</tt> has <tt>parent</tt> in its parents list
	 */
	public boolean hasParent(Node parent) {
		// TODO Auto-generated method stub
		return false;
	}

}
