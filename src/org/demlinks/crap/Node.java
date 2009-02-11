
package org.demlinks.crap;

import javax.naming.CannotProceedException;

import org.demlinks.debug.Debug;
import org.demlinks.errors.BadCallError;
import org.demlinks.exceptions.InconsistentLinkException;

public class Node {
	
	private NodeList	parentsList;
	private NodeList	childrenList;
	
	public Node() {
		this.createLists();
	}
	
	private void createLists() {
		this.parentsList = new NodeList();
		this.childrenList = new NodeList();
	}
	
	/**
	 * creates both links:<br>
	 * this -> child : "this" will know it has a child "child"<br>
	 * this <- child : and the child "child" will know it has a parent "this"
	 * 
	 * @param child
	 * @return true if child existed before call and hence it could be anywhere
	 *         in the list not necessarily at the end of it; false if it didn't
	 *         exist and it is now (after call) at the end(appended) of the list
	 *         also note that if child existed then so did "this"(the parent)
	 *         for the child
	 * @throws InconsistentLinkException
	 *             if half of the link existed before call
	 */
	public boolean appendChild( Node child ) throws InconsistentLinkException {
		Debug.nullException( child );
		boolean existed1 = this.internalAppendChild( child );
		boolean existed2 = child.internalAppendParent( this );
		if ( existed1 ^ existed2 ) {
			// if either one existed, then inconsistent link detected
			// somewhere something made a boo boo
			throw new InconsistentLinkException( "inconsistent link detected" );
			// maybe undo-ing here before throwing is not a good idea
		}
		return existed1;// should be same value as existed2
	}
	
	/**
	 * parent <- this<br>
	 * parent -> this
	 * 
	 * @throws InconsistentLinkException
	 * @see {@link #appendChild(Node)}
	 */
	public boolean appendParent( Node parent ) throws InconsistentLinkException {
		Debug.nullException( parent );
		return parent.appendChild( this );
	}
	
	/**
	 * @param child
	 * @return true if child didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendChild( Node child ) {
		return this.childrenList.appendNode( child );
	}
	
	/**
	 * @param parent
	 * @return true if parent didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendParent( Node parent ) {
		return this.parentsList.appendNode( parent );
	}
	
	/**
	 * this -> child ? AND:<br>
	 * this <- child
	 * 
	 * @param child
	 * @return true if <tt>this</tt> has <tt>child</tt> in its children list and
	 *         <tt>child</tt> has <tt>this</tt> in its parents list
	 * @throws InconsistentLinkException
	 *             is only one of the links exists
	 */
	public boolean hasChild( Node child ) throws InconsistentLinkException {
		Debug.nullException( child );
		boolean link1 = this.childrenList.hasNode( child );
		boolean link2 = child.parentsList.hasNode( this );// isn't that private
		// field?
		if ( link1 ^ link2 ) {
			throw new InconsistentLinkException( "inconsistent link detected" );
		}
		return link1;
	}
	
	/**
	 * parent <- this ? AND: parent -> this
	 * 
	 * @param parent
	 * @return true if <tt>this</tt> has <tt>parent</tt> in its parents list and
	 *         <tt>parent</tt> has <tt>this</tt> in its children list
	 * @throws InconsistentLinkException
	 * @see {@link #hasChild(Node)}
	 */
	public boolean hasParent( Node parent ) throws InconsistentLinkException {
		Debug.nullException( parent );
		return parent.hasChild( this );// counting on hasChild to check both
	}
	
	/**
	 * @param child
	 * @return true if child existed before call
	 * @throws CannotProceedException
	 */
	protected boolean internalRemoveChild( Node child ) {
		return this.childrenList.removeNode( child );
	}
	
	protected boolean internalRemoveParent( Node parent ) {
		return this.parentsList.removeNode( parent );
	}
	
	/**
	 * remove both links:<br>
	 * this -> child<br>
	 * this <- child
	 * 
	 * @param child
	 * @return true if both links existed before call
	 * @throws CannotProceedException
	 *             if one or both links still exist after call
	 * @throws InconsistentLinkException
	 *             if only one of the links existed before call, now neither
	 *             should exist
	 */
	public boolean removeChild( Node child ) throws InconsistentLinkException {
		boolean link1 = this.internalRemoveChild( child );
		boolean link2 = child.internalRemoveParent( this );
		if ( link1 ^ link2 ) {
			throw new InconsistentLinkException( "inconsistent link detected" );
		}
		return link1;
	}
	
	/**
	 * @param parent
	 * @return
	 * @throws InconsistentLinkException
	 * @see #removeChild(Node)
	 */
	public boolean removeParent( Node parent ) throws InconsistentLinkException {
		return parent.removeChild( this );
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
	 * @param ofWhatNode
	 *            is an existing child Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getChildNextOf( Node ofWhatNode ) {
		Debug.nullException( ofWhatNode );
		return this.childrenList.getNodeAfter( ofWhatNode );
	}
	
	/**
	 * @param ofWhatNode
	 *            is an existing child Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getChildPrevOf( Node ofWhatNode ) {
		Debug.nullException( ofWhatNode );
		return this.childrenList.getNodeBefore( ofWhatNode );
	}
	
	/**
	 * @param ofWhatNode
	 *            is an existing parent Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getParentNextOf( Node ofWhatNode ) {
		Debug.nullException( ofWhatNode );
		return this.parentsList.getNodeAfter( ofWhatNode );
	}
	
	/**
	 * @param ofWhatNode
	 *            is an existing parent Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getParentPrevOf( Node ofWhatNode ) {
		Debug.nullException( ofWhatNode );
		return this.parentsList.getNodeBefore( ofWhatNode );
	}
	
	/**
	 * @return number of parents for this node
	 */
	public int numParents() {
		return this.parentsList.size();
	}
	
	/**
	 * @return number of children for this node
	 */
	public int numChildren() {
		return this.childrenList.size();
	}
	
	/**
	 * @param inWhichList
	 *            children or parents list
	 * @param whatNewNode
	 *            node that doesn't exist in "inWhichList"
	 * @param pos
	 *            BEFORE or AFTER
	 * @param posNode
	 *            a Node in "inWhichList" list, that "pos" is referring to
	 * @return
	 */
	private boolean internalInsert( NodeList inWhichList, Node whatNewNode,
			Position pos, Node posNode ) {
		Debug.nullException( inWhichList, whatNewNode, pos, posNode );
		// some people are paranoid here :-"
		if ( ( inWhichList != this.childrenList )
				&& ( inWhichList != this.parentsList ) ) {
			throw new BadCallError( "invalid list specified" );
		}
		if ( ( pos != Position.BEFORE ) && ( pos != Position.AFTER ) ) {
			throw new BadCallError( "undefined location within this context" );
		}
		return inWhichList.insert( whatNewNode, pos, posNode );
	}
	
	public boolean insertChildAfter( Node newChild, Node afterWhatChildNode ) {
		Debug.nullException( newChild, afterWhatChildNode );
		return this.internalInsert( this.childrenList, newChild,
				Position.AFTER, afterWhatChildNode );
	}
	
	public boolean insertChildBefore( Node newChild, Node beforeWhatChildNode ) {
		Debug.nullException( newChild, beforeWhatChildNode );
		return this.internalInsert( this.childrenList, newChild,
				Position.BEFORE, beforeWhatChildNode );
	}
	
	public boolean insertParentAfter( Node newParent, Node afterWhatParentNode ) {
		Debug.nullException( newParent, afterWhatParentNode );
		return this.internalInsert( this.parentsList, newParent,
				Position.AFTER, afterWhatParentNode );
	}
	
	public boolean insertParentBefore( Node newParent, Node beforeWhatParentNode ) {
		Debug.nullException( newParent, beforeWhatParentNode );
		return this.internalInsert( this.parentsList, newParent,
				Position.BEFORE, beforeWhatParentNode );
	}
}
