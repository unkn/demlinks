package org.demlinks.crap;

import javax.naming.CannotProceedException;

import org.demlinks.debug.Debug;
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;

public class Node {
	private NodeList parentsList;
	private NodeList childrenList;

	public Node() {
		createLists();
	}
	
	private void createLists() {
		parentsList = new NodeList();
		childrenList = new NodeList();
	}
	
	/**
	 * creates both links:<br>
	 * this -> child  : "this" will know it has a child "child"<br>
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
		Debug.nullException(child);
		boolean existed1 = this.internalAppendChild(child);
		boolean existed2 = child.internalAppendParent(this);
		if (existed1 ^ existed2) {
			//if either one existed, then inconsistent link detected
			//somewhere something made a boo boo
			throw new InconsistentTypeCode("inconsistent link detected");
			//TODO make own exception here
		}
		return existed1;//should be same value as existed2
	}
	
	/**
	 * parent <- this<br>
	 * parent -> this
	 * @throws InconsistentTypeCode
	 * @see {@link #appendChild(Node)}
	 */
	public boolean appendParent(Node parent) throws InconsistentTypeCode {
		Debug.nullException(parent);
		return parent.appendChild(this);
//		boolean existed1 = this.internalAppendParent(parent);
//		boolean existed2 = parent.internalAppendChild(this);
//		if (existed1 ^ existed2) {
//			//if either one existed, then inconsistent link detected
//			//somewhere something made a boo boo
//			throw new InconsistentTypeCode("inconsistent link detected");
//			//TODO make own exception here
//		}
//		return existed1;//should be same value as existed2
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
	 * this -> child ? AND:<br>
	 * this <- child
	 * @param child
	 * @return true if <tt>this</tt> has <tt>child</tt> in its children list  
	 * 		and <tt>child</tt> has <tt>this</tt> in its parents list 
	 * @throws InconsistentTypeCode is only one of the links exists
	 */
	public boolean hasChild(Node child) throws InconsistentTypeCode {
		Debug.nullException(child);
		boolean link1 = this.childrenList.hasNode(child);
		boolean link2 = child.parentsList.hasNode(this);//isn't that private field?
		if (link1 ^ link2) {
			throw new InconsistentTypeCode("inconsistent link detected");
		}
		return link1;
	}

	/**
	 * parent <- this ? AND:
	 * parent -> this
	 * @param parent
	 * @return true if <tt>this</tt> has <tt>parent</tt> in its parents list
	 * 		and <tt>parent</tt> has <tt>this</tt> in its children list 
	 * @throws InconsistentTypeCode 
	 * @see {@link #hasChild(Node)}
	 */
	public boolean hasParent(Node parent) throws InconsistentTypeCode {
		Debug.nullException(parent);
		return parent.hasChild(this);//counting on hasChild to check both links
//		boolean link1 = this.parentsList.hasNode(parent);
//		boolean link2 = parent.childrenList.hasNode(this);//isn't that private field?
//		if (link1 ^ link2) {
//			throw new InconsistentTypeCode("inconsistent link detected");
//		}
//		return link1;
	}

	/**
	 * @param child
	 * @return true if child existed before call
	 * @throws CannotProceedException 
	 */
	protected boolean internalRemoveChild(Node child) throws CannotProceedException {
		return this.childrenList.removeNode(child);
	}

	protected boolean internalRemoveParent(Node parent) throws CannotProceedException {
		return this.parentsList.removeNode(parent);
	}
	
	/**
	 * remove both links:<br>
	 * this -> child<br>
	 * this <- child
	 * @param child
	 * @return true if both links existed before call
	 * @throws CannotProceedException if one or both links still exist after call
	 * @throws InconsistentTypeCode if only one of the links existed before call, now neither should exist
	 */
	public boolean removeChild(Node child) throws CannotProceedException, InconsistentTypeCode {
		boolean link1 = this.internalRemoveChild(child);
		boolean link2 = child.internalRemoveParent(this);
		if (link1 ^ link2) {
			throw new InconsistentTypeCode("inconsistent link detected");
		}
		return link1;
	}
	
	
	/**
	 * @param parent
	 * @return
	 * @throws CannotProceedException
	 * @throws InconsistentTypeCode
	 * @see #removeChild(Node)
	 */
	public boolean removeParent(Node parent) throws CannotProceedException, InconsistentTypeCode {
		return parent.removeChild(this);
	}
	
	public Node getFirstChild() {
		return this.childrenList.getFirstNode();
	}
	
	public Node getLastChild() {
		return this.childrenList.getLastNode();
	}
	
	public Node getFirstParent() {
		return this.parentsList.getFirstNode();
	}
	
	public Node getLastParent() {
		return this.parentsList.getLastNode();
	}
	
	/**
	 * @param ofWhatNode is an existing child Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getChildNextOf(Node ofWhatNode) {
		Debug.nullException(ofWhatNode);
		return this.childrenList.getNodeAfter(ofWhatNode);
	}
	
	/**
	 * @param ofWhatNode is an existing child Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getChildPrevOf(Node ofWhatNode) {
		Debug.nullException(ofWhatNode);
		return this.childrenList.getNodeBefore(ofWhatNode);
	}
	
	/**
	 * @param ofWhatNode is an existing parent Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getParentNextOf(Node ofWhatNode) {
		Debug.nullException(ofWhatNode);
		return this.parentsList.getNodeAfter(ofWhatNode);
	}
	
	/**
	 * @param ofWhatNode is an existing parent Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getParentPrevOf(Node ofWhatNode) {
		Debug.nullException(ofWhatNode);
		return this.parentsList.getNodeBefore(ofWhatNode);
	}
}
