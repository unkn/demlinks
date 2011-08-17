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
package org.dml.storage.commons;

import org.q.*;



/**
 * node implementations should extend this<br>
 */
public abstract class NodeGenericImpl
		extends NodeGenericCommon
// implements NodeGenericC// , Cloneable
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGeneric#getSelfImpl()
	 */
	@Override
	public final NodeGenericImpl getSelfImpl() {
		// this didn't work due to interface being used(so it remains final here!): not final method, so to allow subclasses to
		// cast internally; we don't
		// want callers to have to cast every time!
		return this;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGenericCommon#getSelf()
	 */
	@Override
	public final NodeGeneric getSelf() {
		return this;// would always return itself for Node implementations
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGenericCommon#equalsOverride(org.dml.storage.commons.NodeGenericCommon)
	 */
	@Override
	protected boolean equalsOverride( final NodeGenericCommon o ) {
		Q.assumeSameExactClassElseThrow( this, o );
		// assert Z.areSameClass_canNotBeNull( this, o ) : "we were expecting same classes\n" + "Participating classes:\n"
		// + this.getClass() + "\n" + o.getClass();
		return getId() == o.getId();
		// FIXME: actually two nodes from two different storages are not equals even if their IDs are
		// XXX: this is not really good when BerkEnv is allowed to have multiple instances, ie. same long could be from 2 diff
		// berkeleydb environments; but for now we know BerkEnv can have only one instance so when comparing any 2 nodes will
		// always be from same berkeley environment! tho in the m.i. case we'd have to store the BerkEnv instance too...;
	}
	
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#equals(java.lang.Object)
	// */
	// @Override
	// public boolean equals( final Object obj ) {
	
	// if ( null == obj ) {
	// return false;
	// }
	// if ( this == obj ) {
	// return true;
	// }
	// Q.assumeSameExactClassElseThrow( this, obj );
	// final NodeGeneric n = (NodeGeneric)obj;
	// return getId() == n.getId();
	// }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Q.ni();
		return (int)getId();
	}
	
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#clone()
	// */
	// @Override
	// public NodeGenericImpl clone() {
	// // try {
	// return (NodeGenericImpl)super.clone();
	// // } catch ( final CloneNotSupportedException e ) {
	// // throw Q.cantClone( e );
	// // // throw Q.bug( "not possible", e );
	// // }
	// }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + ":" + getId() + "]";
	}
}
