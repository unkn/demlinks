/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.references2;

import org.references.*;



/**
 * 
 * @param <TOOBJ>
 *            cannot be null, and cannot exist multiple times in the tree at least not if .equals() find it
 * 
 */
public class TreeOfNonNullUniqueObjects<TOOBJ>
{
	
	private TreeOfNodes										treeSingleton;
	
	
	private RAMTwoWayHashMapOfNonNullUniques<TOOBJ, Node>	map_Obj2Node;
	
	
	/**
	 * constructor
	 * 
	 * @param rootObj
	 */
	public TreeOfNonNullUniqueObjects( final TOOBJ rootObj ) {
		assert null != rootObj;
		associate( rootObj, getTree().getRootNode() );
	}
	
	
	/**
	 * @return
	 */
	private synchronized TreeOfNodes getTree() {
		if ( null == treeSingleton ) {
			treeSingleton = new TreeOfNodes();
		}
		assert null != treeSingleton;
		return treeSingleton;
	}
	
	
	public synchronized TOOBJ getRootUserObject() {
		final TOOBJ ret = getObjectFor( getTree().getRootNode() );
		assert null != ret;
		return ret;
	}
	
	
	// /**
	// * @param userObj
	// * usually a `new`-ed instance of `TOOBJ` used to search for this object
	// * @return null if not found
	// */
	// public synchronized
	// TOOBJ
	// getUserObject(
	// TOOBJ userObj )
	// {
	// ListOfUniqueNonNullObjects<Node> list =
	// getListFor( userObj );
	// if ( list.size() == 0 )
	// {
	// return null;// not found, no Nodes associated with that object
	// }
	// Node anyNode =
	// list.getObjectAt( Position.LAST );
	// TOOBJ ret =
	// getObjectFor( anyNode );
	// // now ret and userObj are .equals() but not necessarily `==` by reference
	// ret,
	// userObj ) );
	// // because when you call this you usually make a `new` userObj to search for that and you find it as `ret`
	// return ret;
	// }
	
	
	public synchronized Node getRootNode() {
		final Node node = getNodeFor( getRootUserObject() );
		return node;
	}
	
	
	/**
	 * one object can be associated with more than one node<br>
	 * but one node can only be associated with only one object<br>
	 * 
	 * @param object
	 *            an object which may already be associated with other nodes
	 * @param node
	 *            unique, not already associated with any object
	 */
	private synchronized void associate( final TOOBJ object, final Node node ) {
		assert null != object;
		assert null != node;
		assert null == getObjectFor( node );// not other object is associated with that node
		getMap().ensureExists( object, node );
	}
	
	
	/**
	 * make new or get existing list of Nodes associated with that object<br>
	 * 
	 * @param object
	 *            never null
	 * @return null if none
	 */
	private synchronized Node getNodeFor( final TOOBJ object ) {
		assert null != object;
		final Node node = getMap().getData( object );
		return node;
	}
	
	
	/**
	 * any object has only one connection to another Node, same for Node<br>
	 * 
	 * @param node
	 * @return null if none
	 */
	public synchronized TOOBJ getObjectFor( final Node node ) {
		assert null != node;
		final TOOBJ obj = getMap().getKey( node );
		return obj;
	}
	
	
	private synchronized RAMTwoWayHashMapOfNonNullUniques<TOOBJ, Node> getMap() {
		if ( null == map_Obj2Node ) {
			map_Obj2Node = new RAMTwoWayHashMapOfNonNullUniques<TOOBJ, Node>();
		}
		assert null != map_Obj2Node;
		return map_Obj2Node;
	}
	
	
	// private synchronized
	// HashMap<Node, TOOBJ>
	// getNode2Obj_Map()
	// {
	// if ( null == map_Node2Obj )
	// {
	// map_Node2Obj =
	// new HashMap<Node, TOOBJ>();
	// }
	// return map_Node2Obj;
	// }
	
	
	/**
	 * for now it assumes you know what you're doing and throws if index doesn't exist<br>
	 * 
	 * @param parent
	 * @param index
	 * @return
	 */
	public synchronized TOOBJ getChildAtIndex( final TOOBJ parent, final int index ) {
		assert null != parent;
		final Node parentNode = getNodeFor( parent );
		final Node child = getTree().getChildAtIndex( parentNode, index );
		final TOOBJ objChild = getObjectFor( child );
		assert null != objChild;
		return objChild;
	}
	
	
	/**
	 * @param parent
	 * @return
	 */
	public synchronized int getChildCount0( final TOOBJ parent ) {
		assert null != parent;
		final Node parentNode = getNodeFor( parent );
		assert null != parent;
		return getTree().getChildCount0( parentNode );
	}
	
	
	/**
	 * @param parent
	 * @param child
	 * @return
	 */
	public synchronized int getIndexOfChild0( final TOOBJ parent, final TOOBJ child ) {
		assert null != parent;
		assert null != child;
		final Node pNode = getNodeFor( parent );
		assert null != pNode;
		final Node cNode = getNodeFor( child );
		assert null != cNode;
		return getTree().getIndexOfChild0( pNode, cNode );
	}
	
	
	/**
	 * @param child
	 *            must be new!
	 * @param existingParent
	 * @param pos
	 * @return the inserted Node, never null; else throws
	 */
	public synchronized void addChildInParentAtPos( final TOOBJ child, final TOOBJ existingParent, final Position pos ) {
		assert null != existingParent;
		assert null != child;
		final Node parentNode = getNodeFor( existingParent );
		assert null != parentNode;
		final TOOBJ parentObj = getObjectFor( parentNode );
		assert null != parentObj;
		Node childNode = getNodeFor( child );
		assert null == childNode;// doesn't already exist
		childNode = getTree().addChildInParentAtPos( parentNode, pos );
		assert null != childNode;
		associate( child, childNode );
	}
	
	
	// /**
	// * @param childObj
	// * @param existingNodeParent
	// * @param pos
	// * @return
	// */
	// public synchronized
	// MethodReturnsForTree
	// isChildAtPos(
	// TOOBJ childObj,
	// Node existingNodeParent,
	// Position pos )
	// {
	// TOOBJ existingChildObj =
	// getUserObject( childObj );
	// if ( null == existingChildObj )
	// {
	// return MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST;
	// }
	// else
	// {
	// ListOfUniqueNonNullObjects<Node> allNodesForChild =
	// getListFor( existingChildObj );
	// if ( allNodesForChild.size() == 0 )
	// {
	// return MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST;
	// }
	// else
	// {
	// Node parser =
	// allNodesForChild.getObjectAt( Position.FIRST );
	// int index;
	// while ( null != parser )
	// {
	// if ( existingNodeParent.containsChildAtPos(
	// parser,
	// pos ) )
	// {
	// return MethodReturnsForTree.SUCCESS_BUT_ALREADY_EXISTED_WHERE_EXPECTED;
	// }
	// else
	// {
	// if ( existingNodeParent.containsChildAtPos( parser ) )
	// {
	// return MethodReturnsForTree.HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_POSITION_IN_SAME_PARENT;
	// }
	// }
	// // get next, can be null
	// parser =
	// allNodesForChild.getObjectAt(
	// Position.AFTER,
	// parser );
	// }
	// // it was definitely not in this parent
	// return MethodReturnsForTree.HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_PARENT;
	// }
	// }
	// }
	
}
