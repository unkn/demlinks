/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */

package org.demlinks.javathree;

import org.q.*;



// at this level the Nodes don't have IDs, they're just java objects

/**
 * Node is an object that contains two lists of Node objects<br>
 * called parents and children
 * It only knows to do operations for itself (ie. it won't also link parent to child is a child to parent link is requested via
 * linkFrom)
 * linkTo(child) makes sure only this.childList.has(child) but nothing else like child.parentList.has(this) - that's in
 * NodeLevel1
 */
public class Node0 {
	
	// if both lists are empty the node could still exist (in the Environment)
	// lists should never be null
	protected NodeRefsList	backwardList	= null; // list of all Nodes that point to <this>
	protected NodeRefsList	forwardList		= null; // list of all Nodes that <this> points to
													
													
	// constructor
	public Node0() {
		backwardList = new NodeRefsList();
		forwardList = new NodeRefsList();
		assert Q.nn( backwardList );
		assert Q.nn( forwardList );
	}
	
	
	private boolean putNodeInList( final Node0 node, final NodeRefsList list ) {
		assert Q.nn( node );
		assert Q.nn( list );
		return list.addLast( node );
	}
	
	
	/**
	 * @param node
	 * @param list
	 * @return true if existed; removed regardless of return
	 */
	private boolean unPutNodeInList( final Node0 node, final NodeRefsList list ) {
		assert Q.nn( node );
		assert Q.nn( list );
		return list.removeObject( node );
	}
	
	
	/**
	 * Makes sure that after the call <tt>this</tt> Node object has <tt>destinationNode</tt> in its children list.<br>
	 * 
	 * @param destinationNode
	 *            the Node object that will be a child for <tt>this</tt> Node
	 * @return true if the link didn't exist before call
	 */
	public boolean linkForward( final Node0 destinationNode ) {
		return putNodeInList( destinationNode, getList( List.FORWARD ) );
	}
	
	
	/**
	 * we will no longer point to <tt>destinationNode</tt>
	 * 
	 * @param destinationNode
	 *            Node that will be removed from being a child of <tt>this</tt> Node
	 * @return true if existed; removed after call anyway
	 */
	public boolean unLinkForward( final Node0 destinationNode ) {
		return unPutNodeInList( destinationNode, getList( List.FORWARD ) );
	}
	
	
	/**
	 * ensures there's a link from sourceNode to <tt>this</tt> Node<br>
	 * 
	 * @param sourceNode
	 *            the node that will point to us
	 * @return true if the link didn't exist before call
	 */
	public boolean linkBackward( final Node0 sourceNode ) {
		return putNodeInList( sourceNode, getList( List.BACKWARD ) );
	}
	
	
	/**
	 * <tt>sourceNode</tt> will no longer point to <tt>this</tt><br>
	 * 
	 * @param sourceNode
	 *            Node that will be removed from being a parent of <tt>this</tt> Node
	 * @return
	 */
	public boolean unLinkBackward( final Node0 sourceNode ) {
		return unPutNodeInList( sourceNode, getList( List.BACKWARD ) );
	}
	
	
	/**
	 * checks if <tt>this</tt> points to <tt>destinationNode</tt>
	 * 
	 * @param destinationNode
	 * @return true is so
	 */
	public boolean isLinkForward( final Node0 destinationNode ) {
		assert Q.nn( destinationNode );
		final boolean ret = getList( List.FORWARD ).containsObject( destinationNode );
		return ret;
	}
	
	
	/**
	 * checks if <tt>this</tt> is pointed by <tt>sourceNode</tt>
	 * 
	 * @param sourceNode
	 * @return true if so
	 * @see #isLinkForward(Node0)
	 */
	public boolean isLinkBackward( final Node0 sourceNode ) {
		assert Q.nn( sourceNode );
		final boolean ret = getList( List.BACKWARD ).containsObject( sourceNode );
		return ret;
	}
	
	
	/**
	 * @return true if the Node has no children and no parents
	 */
	public final boolean isAlone() {
		return ( ( getList( List.FORWARD ).isEmpty() ) && ( getList( List.BACKWARD ).isEmpty() ) );
	}
	
	
	/**
	 * @param list
	 *            List.CHILDREN or List.PARENTS
	 * @return the specified list object
	 * @throws BadCallError
	 *             if you specify unknown type of list to be returned
	 */
	public final NodeRefsList getList( final List list ) {
		switch ( list ) {
		case FORWARD:
			assert Q.nn( forwardList );
			return forwardList;
		case BACKWARD:
			assert Q.nn( backwardList );
			return backwardList;
		default:
			throw Q.badCall( "Unhandled list type: " + list );
		}
	}
	
	
	
} // END of class
