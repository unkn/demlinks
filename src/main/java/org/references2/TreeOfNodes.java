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
 *
 */
public class TreeOfNodes
{
	
	private Node	rootNode;
	
	
	public Node getRootNode() {
		if ( null == rootNode ) {
			rootNode = new Node( this );
		}
		assert null != rootNode;
		return rootNode;
	}
	
	
	/**
	 * @param parent
	 * @param index
	 * @return never null
	 */
	public Node getChildAtIndex( final Node parent, final int index ) {
		assert null != parent;
		assert assumedSameTree( parent );
		final Node child = parent.getChildAtIndex( index );
		assert null != child;
		return child;
	}
	
	
	public int getChildCount0( final Node parent ) {
		assert null != parent;
		assert assumedSameTree( parent );
		return parent.getChildCount0();
	}
	
	
	private boolean assumedSameTree( final Node node ) {
		assert null != node;
		// compare by contents heh I mean, via this.equals()
		if ( !Z.equals_enforceExactSameClassTypesAndNotNull( this, node.getTree() ) ) {
			Q.badCall( "parent node is part of a different tree" );
		}
		return true;
	}
	
	
	public int getIndexOfChild0( final Node parent, final Node child ) {
		assert null != parent;
		assert null != child;
		assert assumedSameTree( parent );
		assert assumedSameTree( child );
		return parent.getIndexOfChild0( child );
	}
	
	
	/**
	 * @param childNode
	 * @param existingParent
	 * @param pos
	 * @return
	 */
	public synchronized Node addChildInParentAtPos( final Node existingParent, final Position pos ) {
		assert null != existingParent;
		assert null != pos;
		assert assumedSameTree( existingParent );
		final Node childNode = new Node( this );
		assert assumedSameTree( childNode );
		existingParent.addChildAtPos( childNode, pos );
		// TODO: check if added right
		return childNode;
	}
	
}
