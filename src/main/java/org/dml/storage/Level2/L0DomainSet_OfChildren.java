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
 * domain is enforced only if asserts are enabled and only for java calls ie. in java environment<br>
 * does not allow domain or self to be added to set<br>
 * does not check for integrity if domainSet already exists when constructed<br>
 * does not allow: self to equal domain<br>
 */
public class L0DomainSet_OfChildren
		extends L0Set_OfChildren
{
	
	private final NodeGeneric	_domainNode;
	
	
	/**
	 * @param env1
	 * @param selfNode
	 * @param domainNode
	 */
	public L0DomainSet_OfChildren( final StorageGeneric env1, final NodeGeneric selfNode, final NodeGeneric domainNode ) {
		super( env1, selfNode );
		assert null != domainNode;
		assert !selfNode.equals( domainNode );
		_domainNode = domainNode.clone();// cloned to catch `==` bugs somewhere (do use .equals)
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simpler.SetOfChildren#isValidChild(java.lang.Long)
	 */
	@Override
	public boolean isValidChild( final NodeGeneric childNode ) {
		if ( ( super.isValidChild( childNode ) ) && ( isInDomain( childNode ) ) ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isInDomain( final NodeGeneric childNode ) {
		return ( ( !_domainNode.equals( childNode ) ) && ( getStorage().isVector( _domainNode, childNode ) ) );
	}
	
	
	public NodeGeneric getDomain() {
		return _domainNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.Level2.L0Set_OfChildren#overriddenEquals(org.dml.storage.Level2.EpicEquals)
	 */
	@Override
	protected boolean equalsOverride( final NodeGenericCommon obj ) {
		if ( !super.equalsOverride( obj ) ) {
			return false;
		}
		Q.assumeSameExactClassElseThrow( this, obj );
		// assert obj.getClass() == this.getClass();
		// assert obj instanceof DomainSet_OfChildren :
		// "user is comparing to a super instance, as oppose to same or subclass";
		assert Z.equals_enforceExactSameClassTypesAndNotNull( _domainNode, ( (L0DomainSet_OfChildren)obj )._domainNode ) : Q
			.badCall( "same self but different domains, user did a boobo somewhere" );
		return true;
	}
}
