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


package org.demlinks.node;



import org.demlinks.references.*;
import org.q.*;



/**
 * if you do node1.childrenList.add(node2) then this list won't execute
 * node2.parentsList.add(node1) for you maybe it should, we'll see
 */
public class NodeList extends ObjRefsList<Node> {
	
	public NodeList() {
		
		super();
	}
	
	
	/**
	 * @param nodeToAppend
	 * @return true if node didn't previously exist; false if node existed and
	 *         hence it could be anywhere in the list not really at the end of
	 *         it, as the function name implies; it won't be moved to end either
	 */
	public boolean appendNode( final Node nodeToAppend ) {
		
		assert Q.nn( nodeToAppend );// why not assert? because param(ie.
		// nodeToAppend) could be
		// dynamically set on runtime
		return this.addLast( nodeToAppend );
		// nodeToAppend.getOpposingList().addLast(this.fatherNode);//in opposing
		// list
	}
	
	
	/**
	 * @return null or the first Node in list
	 */
	public Node getFirstNode() {
		
		return this.getObjectAt( Position.FIRST );
	}
	
	
	public Node getLastNode() {
		
		return this.getObjectAt( Position.LAST );
	}
	
	
	public Node getNodeAfter( final Node node ) {
		
		assert Q.nn( node );
		return this.getObjectAt( Position.AFTER, node );
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return null or the node at index
	 */
	public Node getNodeAt( final int index ) {
		
		// assert null != index );
		return this.getObjectAt( index );
	}
	
	
	public Node getNodeBefore( final Node node ) {
		
		assert Q.nn( node );
		return this.getObjectAt( Position.BEFORE, node );
	}
	
	
	public boolean hasNode( final Node node ) {
		
		assert Q.nn( node );
		return containsObject( node );
	}
	
	
	/**
	 * @param node
	 * @param index
	 *            0 based index
	 * @return true if node is at index
	 */
	public boolean hasNodeAtPos( final Node node, final int index ) {
		
		// assert null != node, index );
		return containsObjectAtPos( node, index );
	}
	
	
	/**
	 * @param whichNode
	 * @param whatPos
	 *            only FIRST/LAST
	 * @return true if whichNode existed before, and it's now still there, not
	 *         moved<br>
	 *         false if, whichNode didn't exist and it's now exactly where
	 *         specified by call
	 */
	public boolean insertNode( final Node whichNode, final Position whatPos ) {
		
		assert Q.nn( whichNode );// null != whichNode;
		assert Q.nn( whatPos );
		return this.insert( whichNode, whatPos );
	}
	
	
	/**
	 * @param newNode
	 * @param afterNode
	 * @return true if newNode existed and it wasn't moved as specified<br>
	 *         false if everything went well
	 * @see #insertBeforeNode(Node, Node)
	 */
	public boolean insertAfterNode( final Node newNode, final Node afterNode ) {
		
		assert Q.nn( newNode );
		assert Q.nn( afterNode );
		return this.insert( newNode, Position.AFTER, afterNode );
	}
	
	
	/**
	 * @param newNode
	 *            is inserted before "beforeNode"
	 * @param beforeNode
	 *            will be after "newNode" when call is done
	 * @return true if newNode existed before call, and it's still there in the
	 *         same place as before, hence it wasn't moved before beforeNode<br>
	 *         false is newNode didn't exist, but it does now and it's right
	 *         before beforeNode
	 */
	public boolean insertBeforeNode( final Node newNode, final Node beforeNode ) {
		
		assert Q.nn( newNode );
		assert Q.nn( beforeNode );
		return this.insert( newNode, Position.BEFORE, beforeNode );
	}
	
	
	/**
	 * @param node
	 *            to remove
	 * @return true if node existed before call, not anymore after the call
	 *         false if failed to delete because it didn't exist
	 */
	public boolean removeNode( final Node node ) {
		
		assert Q.nn( node );
		final boolean ret = removeObject( node );
		return ret;
	}
	
	
	public void removeAll() {
		
		Node n;
		while ( ( n = getFirstNode() ) != null ) {
			removeObject( n );
		}
	}
}
