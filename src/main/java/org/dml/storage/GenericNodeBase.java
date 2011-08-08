/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 8, 2011 7:11:12 PM
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

import org.q.*;



/**
 * multiple java instances of this can exist but for the same underlying node (aka long)<br>
 * they're to be compared via .equals()<br>
 */
public abstract class GenericNodeBase
		implements NodeGeneric// , Cloneable
{
	
	private final long	longNode;
	
	
	/**
	 * get new node instance for this (long) identifier<br>
	 * but this instance is just a wrapper(for that long) inside java<br>
	 * 
	 * @param fromLong
	 */
	protected GenericNodeBase( final long fromLong ) {
		longNode = fromLong;
	}
	
	
	@Override
	public long getId() {
		return longNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj ) {
		// XXX: this is not really good when BerkEnv is allowed to have multiple instances, ie. same long could be from 2 diff
		// berkeleydb environments; but for now we know BerkEnv can have only one instance so when comparing any 2 nodes will
		// always be from same berkeley environment! tho in the m.i. case we'd have to store the BerkEnv instance too...;
		if ( null == obj ) {
			return false;
		}
		Q.assumeSameExactClassElseThrow( this, obj );
		final NodeGeneric n = (NodeGeneric)obj;
		return getId() == n.getId();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Q.ni();
		return (int)longNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GenericNodeBase clone() {
		try {
			return (GenericNodeBase)super.clone();
		} catch ( final CloneNotSupportedException e ) {
			throw Q.bug( "not possible", e );
		}
	}
	
}
