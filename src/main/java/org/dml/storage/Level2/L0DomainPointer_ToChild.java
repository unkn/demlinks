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
package org.dml.storage.Level2;

import org.dml.storage.commons.*;
import org.q.*;
import org.toolza.*;



/**
 * allows unset aka null<br>
 * pointee must be child of domain<br>
 */
public class L0DomainPointer_ToChild
		extends L0Pointer_ToChild
{
	
	private final NodeGeneric		_domainNode;
	private final StorageGeneric	env;
	
	
	public L0DomainPointer_ToChild( final StorageGeneric env1, final NodeGeneric selfNode, final NodeGeneric domainNode ) {
		super( env1, selfNode );
		assert null != env1;
		env = env1;
		assert null != domainNode;
		_domainNode = domainNode.clone();
		// XXX: don't check if existing child is valid because it will not work in constructor, need static get
	}
	
	
	/**
	 * null allowed<br>
	 * and child must be part of domain<br>
	 * 
	 * @param childNode
	 *            null or a longIdent
	 * @return true if valid for this
	 */
	@Override
	public boolean isValidChild( final NodeGeneric childNode ) {
		// valid if one of:
		// 1. is null
		// 2. not be the same as the domain && is child of domain
		return ( ( null == childNode ) || ( ( !getDomain().equals( childNode ) ) && ( env.isVector( getDomain(), childNode ) ) ) );
	}
	
	
	public NodeGeneric getDomain() {
		return _domainNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.Level2.EpicBase#equalsOverride(org.dml.storage.Level2.EpicBase)
	 */
	@Override
	protected boolean equalsOverride( final EpicBase obj ) {
		if ( !super.equalsOverride( obj ) ) {
			return false;
		}
		assert obj.getClass() == this.getClass();// redundant
		assert Z.equals_enforceExactSameClassTypesAndNotNull( getDomain(), ( (L0DomainPointer_ToChild)obj ).getDomain() ) : Q
			.badCall( "same self but different domains, user did a boobo somewhere" );
		return true;
	}
}
