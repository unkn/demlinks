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



import org.q.*;
import org.toolza.*;



/**
 * Nodes cannot repeat in the list of children(/parents)<br>
 * but the same Node cannot be in both children and parents list that would cause recursion<br>
 * a Node doesn't keep track of any user objects, they only keep track of other Nodes, basically forming a tree with
 * multiple parents<br>
 */
public class Node// FIXME: rename this
{
	
	private final TreeOfNodes					parentTree;
	private ListOfUniqueNonNullObjects<Node>	parents;
	private ListOfUniqueNonNullObjects<Node>	childrenSingleton;
	
	
	// public synchronized void
	public Node( final TreeOfNodes partOfThisTree ) {
		assert null != partOfThisTree;
		parentTree = partOfThisTree;
	}
	
	
	private ListOfUniqueNonNullObjects<Node> getChildrenList() {
		if ( null == childrenSingleton ) {
			childrenSingleton = new ListOfUniqueNonNullObjects<Node>();
		}
		assert null != childrenSingleton;
		return childrenSingleton;
	}
	
	
	public TreeOfNodes getTree() {
		assert null != parentTree;
		return parentTree;
	}
	
	
	public Node getChildAtIndex( final int index ) {
		return getChildrenList().getObjectAt( index );
	}
	
	
	private boolean assumedSameTree( final Node node ) {
		assert null != node;
		// compare by contents heh I mean, via this.equals()
		if ( !Z.equalsWithExactSameClassTypes_enforceNotNull( getTree(), node.getTree() ) ) {
			Q.badCall( "parent node is part of a different tree" );
		}
		return true;
	}
	
	
	public int getChildCount0() {
		return getChildrenList().size();
	}
	
	
	public int getIndexOfChild0( final Node child ) {
		assert null != child;
		assert assumedSameTree( child );
		// getTree(),
		// child.getTree() ) );
		return getChildrenList().getIndexOfObject( child );
	}
	
	
	public boolean containsChildAtPos( final Node child, final Position pos ) {
		assert null != child;
		assert null != pos;
		assert assumedSameTree( child );
		return Z.equalsWithExactSameClassTypes_enforceNotNull( getChildrenList().getObjectAt( pos ), child );
	}
	
	
	/**
	 * @param childNode
	 * @param pos
	 * @return
	 */
	public synchronized void addChildAtPos( final Node childNode, final Position pos ) {
		assert null != childNode;
		assert null != pos;
		assert assumedSameTree( childNode );
		if ( Z.equalsWithExactSameClassTypes_enforceNotNull( this, childNode ) ) {
			Q.badCall( "parent and child are the same, but this is not allowed!" );
		}
		if ( getChildrenList().containsObject( childNode ) ) {
			if ( !Z.equalsWithExactSameClassTypes_enforceNotNull( getChildrenList().getObjectAt( pos ), childNode ) ) {
				Q.badCall( "object already exists but not in the same place" );
			} else {
				Q.badCall( "object already exists in parent in exactly the same place" );
			}
		}
		final boolean existed = getChildrenList().addObjectAtPosition( pos, childNode );
		assert !existed;
	}
	
	
	/**
	 * @param child
	 * @return
	 */
	public synchronized boolean containsChildAtPos( final Node child ) {
		assert null != child;
		assert assumedSameTree( child );
		return getChildrenList().containsObject( child );
	}
}
