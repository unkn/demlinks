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

import org.dml.storage.*;
import org.q.*;



/**
 * allows unset aka null<br>
 * pointee must be child of domain<br>
 */
public class L0DomainPointer_ToTerminal
		extends L0Pointer_ToTerminal
{
	
	private final NodeGeneric		_domainNode;
	private final StorageGeneric	env;
	
	
	public L0DomainPointer_ToTerminal( final StorageGeneric env1, final NodeGeneric selfNode, final NodeGeneric domainNode ) {
		super( env1, selfNode );
		assert null != env1;
		env = env1;
		assert null != domainNode;
		_domainNode = domainNode.clone();
		// XXX: don't check if existing terminal is valid because it will not work in constructor, need static get
	}
	
	
	/**
	 * null allowed<br>
	 * and child must be part of domain<br>
	 * 
	 * @param terminalNode
	 *            null or a longIdent
	 * @return true if valid for this
	 */
	public boolean isValidTerminal( final NodeGeneric terminalNode ) {
		return ( ( null == terminalNode ) || ( ( !_domainNode.equals( terminalNode ) ) && ( env.isVector(
			_domainNode,
			terminalNode ) ) ) );
	}
	
	
	@Override
	public void setPointee( final NodeGeneric toWhatTerminalNode ) {
		assert isValidTerminal( toWhatTerminalNode );
		super.setPointee( toWhatTerminalNode );
	}
	
	
	@Override
	public NodeGeneric getPointeeTerminal() {
		final NodeGeneric terminal = super.getPointeeTerminal();
		assert isValidTerminal( terminal ) : "something else must've changed our pointee and made this inconsistent with our domain";
		return terminal;
	}
	
	
	public NodeGeneric getDomain() {
		return _domainNode;
	}
	
	
	@Override
	public boolean equals( final Object obj ) {
		if ( super.equals( obj ) ) {
			assert obj.getClass() == this.getClass();// redundant
			assert _domainNode.equals( ( (L0DomainPointer_ToTerminal)obj )._domainNode ) : Q
				.badCall( "same self but different domains, user did a boobo somewhere" );
			return true;
		} else {
			return false;
		}
	}
}
