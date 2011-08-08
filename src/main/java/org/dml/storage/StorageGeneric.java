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
package org.dml.storage;


/**
 *
 */
public interface StorageGeneric
{
	
	public TransactionGeneric beginTransaction();
	
	
	public void shutdown();
	
	
	public void shutdown( boolean delete );
	
	
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
	
	public void makeVector( final NodeGeneric initialNode, final NodeGeneric terminalNode );
	
	
	public boolean ensureVector( final NodeGeneric initialNode, final NodeGeneric terminalNode );
	
	
	/**
	 * is? initialNode->terminalNode<br>
	 * but terminalNode->initialNode is a different thing<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 * @return
	 */
	public boolean isVector( final NodeGeneric initialNode, final NodeGeneric terminalNode );
	
	
	
	public IteratorOnTerminalNodesGeneric getIterator_on_Initials_of( final NodeGeneric terminalNode );
	
	
	public IteratorOnTerminalNodesGeneric getIterator_on_Terminals_of( final NodeGeneric initialNode );
	
	
	public int countInitials( final NodeGeneric ofTerminalNode );
	
	
	public int countTerminals( final NodeGeneric ofInitialNode );
	
	
	/**
	 * A->X<br>
	 * B->X<br>
	 * find X<br>
	 * 
	 * @param initialNode1
	 * @param initialNode2
	 * @return X
	 */
	public NodeGeneric findCommonTerminalForInitials( final NodeGeneric initialNode1, final NodeGeneric initialNode2 );
	
	
	public boolean removeVector( final NodeGeneric initialNode, final NodeGeneric terminalNode );
}
