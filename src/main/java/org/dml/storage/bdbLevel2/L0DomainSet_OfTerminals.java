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
package org.dml.storage.bdbLevel2;

import org.dml.storage.*;
import org.dml.storage.berkeleydb.native_via_jni.*;
import org.q.*;



/**
 * domain is enforced only if asserts are enabled and only for java calls ie. in java environment<br>
 * does not allow domain or self to be added to set<br>
 * does not check for integrity if domainset already exists when constructed<br>
 * does not allow: self to equal domain<br>
 */
public class L0DomainSet_OfTerminals
		extends L0Set_OfTerminals
{
	
	private final NodeGeneric	_domainNode;
	
	
	/**
	 * @param env1
	 * @param selfNode
	 * @param domainNode
	 */
	public L0DomainSet_OfTerminals( final StorageBDBNative env1, final NodeGeneric selfNode, final NodeGeneric domainNode ) {
		super( env1, selfNode );
		assert null != domainNode;
		assert !selfNode.equals( domainNode );
		_domainNode = domainNode.clone();// cloned to catch `==` bugs somewhere (do use .equals)
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simpler.SetOfTerminals#isValidTerminal(java.lang.Long)
	 */
	@Override
	public boolean isValidTerminal( final NodeGeneric terminalNode ) {
		if ( ( super.isValidTerminal( terminalNode ) ) && ( isInDomain( terminalNode ) ) ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isInDomain( final NodeGeneric terminalNode ) {
		return ( ( !_domainNode.equals( terminalNode ) ) && ( env.isVector( _domainNode, terminalNode ) ) );
	}
	
	
	public NodeGeneric getDomain() {
		return _domainNode;
	}
	
	
	@Override
	public boolean equals( final Object obj ) {
		// Q.badCall( "not yet implemented, tho below code is good" );
		if ( super.equals( obj ) ) {
			assert obj.getClass() == this.getClass();
			// assert obj instanceof DomainSet_OfTerminals :
			// "user is comparing to a super instance, as oppose to same or subclass";
			assert _domainNode.equals( ( (L0DomainSet_OfTerminals)obj )._domainNode ) : Q
				.badCall( "same self but different domains, user did a boobo somewhere" );
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// FIXME: use Q.showEx for the case when this is thrown in try and overwritten in finally, to be seen on console
		throw new CloneNotSupportedException( "not implemented" );
	}
}
