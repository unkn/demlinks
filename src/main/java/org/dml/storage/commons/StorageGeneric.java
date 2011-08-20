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
package org.dml.storage.commons;


/**
 *
 */
public interface StorageGeneric
{
	
	public TransactionGeneric beginTransaction();
	
	
	public void shutdown();
	
	
	public void shutdown( boolean delete );
	
	
	public void addHook( StorageHookImplementation code );
	
	
	public boolean isStillValid();
	
	
	public void assertIsStillValid();
	
	
	// =============== Level 1: name and node
	/**
	 * @param forNode
	 * @return can be null if node has no associated name
	 */
	public String getName( final NodeGeneric forNode );
	
	
	public NodeGeneric getNode( final String name );
	
	
	public NodeGeneric createOrGetNode( final String name );
	
	
	/**
	 * @return never null
	 */
	public NodeGeneric createNewUniqueNode();
	
	
	// =============== Level 2: tuple of nodes
	
	public void makeVector( final NodeGeneric parentNode, final NodeGeneric childNode );
	
	
	public boolean ensureVector( final NodeGeneric parentNode, final NodeGeneric childNode );
	
	
	/**
	 * is? parentNode->childNode<br>
	 * but childNode->parentNode is a different thing<br>
	 * 
	 * @param parentNode
	 * @param childNode
	 * @return
	 */
	public boolean isVector( final NodeGeneric parentNode, final NodeGeneric childNode );
	
	
	
	public IteratorGeneric_OnChildNodes getIterator_on_Parents_of( final NodeGeneric childNode );
	
	
	public IteratorGeneric_OnChildNodes getIterator_on_Children_of( final NodeGeneric parentNode );
	
	
	public int countParents( final NodeGeneric ofChildNode );
	
	
	public int countChildren( final NodeGeneric ofParentNode );
	
	
	/**
	 * A->X<br>
	 * B->X<br>
	 * find X<br>
	 * 
	 * @param parentNode1
	 * @param parentNode2
	 * @return X
	 */
	public NodeGeneric findCommonChildForParents( final NodeGeneric parentNode1, final NodeGeneric parentNode2 );
	
	
	public boolean removeVector( final NodeGeneric parentNode, final NodeGeneric childNode );
}
