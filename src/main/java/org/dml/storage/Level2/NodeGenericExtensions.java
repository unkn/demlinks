/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 12, 2011 2:40:33 AM
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
 *
 */
public abstract class NodeGenericExtensions
		extends NodeGenericCommon
// implements NodeGeneric
{
	
	// private final static RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl> extensionInstances;
	// static {
	// // just don't want this assignment(=) to shift all the fields far to the right due to lame'o'indentation
	// // that's why it's in a static block
	// extensionInstances = new RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl>();
	// }
	//
	//
	// protected synchronized static NodeGenericExtensions getExtensionInstanceForNodeImpl( final NodeGenericImpl nodeImpl ) {
	// assert Q.nn( nodeImpl );
	// NodeGenericExtensions existingInstance = extensionInstances.getKey( nodeImpl );
	// if ( null != existingInstance ) {
	// // returning a clone just to make sure we catch any == bugs or similars
	// existingInstance = existingInstance.clone();
	// }
	// return existingInstance;
	// }
	//
	//
	// protected synchronized static void putExtensionInstanceForNodeImpl( final NodeGenericExtensions newInstance,
	// final NodeGenericImpl impl ) {
	// assert Q.nn( newInstance );
	// assert Q.nn( impl );
	// assert null == getExtensionInstanceForNodeImpl( impl );
	// final boolean existed = extensionInstances.ensureExists( newInstance, impl );
	// assert !existed;
	// assert Z.equals_enforceExactSameClassTypesAndNotNull( getExtensionInstanceForNodeImpl( impl ), newInstance );
	// }
	
	// private final StorageGeneric storage;
	
	// self can be either an extension or an implementation
	private final NodeGeneric		_selfNode;
	
	// this can be only implementation:
	private final NodeGenericImpl	_selfNodeImpl;
	
	
	/**
	 * @param store
	 * @param selfNode
	 */
	public NodeGenericExtensions( final StorageGeneric store, final NodeGeneric selfNode ) {
		// XXX1309: constructor param order should be same; see other XXX1309
		super( store );
		assert null != store;
		assert null != selfNode;
		// storage = store;
		_selfNode = selfNode.clone();// this is cloned for hitting bug with `==` in later code
		_selfNodeImpl = getSelfImpl( selfNode );
	}
	
	
	// public final StorageGeneric getStorage() {
	// return storage;
	// }
	
	
	@Override
	public NodeGenericImpl getSelfImpl() {
		return _selfNodeImpl;
	}
	
	
	public static NodeGenericImpl getSelfImpl( final NodeGeneric from ) {
		NodeGeneric parser = from;
		// if ( parser instanceof EpicBase ) {
		// parser = ( (EpicBase)parser ).getSelfImpl();//this uses lot of stack
		// }
		while ( parser instanceof NodeGenericExtensions ) {
			parser = ( (NodeGenericExtensions)parser ).getSelf();
		}
		assert parser instanceof NodeGenericImpl;
		return (NodeGenericImpl)parser;
	}
	
	
	@Override
	public NodeGeneric getSelf() {
		return _selfNode;// would return the based-upon Node
	}
	
	
	/**
	 * @return it's int(not long) because cursor.count() returns int! bdb limitation i guess
	 */
	public int size() {
		return getStorage().countChildren( getSelf() );
	}
	
	
	public final boolean isEmpty() {
		return 0 == size();
	}
	
	
	public abstract boolean isValidChild( final NodeGeneric node );
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGenericCommon#equalsOverride(org.dml.storage.commons.NodeGenericCommon)
	 */
	@Override
	protected boolean equalsOverride( final NodeGenericCommon o ) {
		final NodeGenericExtensions obj = (NodeGenericExtensions)o;// this will always be of this type if we're here
		
		final boolean sameSelf = Z.equals_enforceSameBaseClassAndNotNull( obj.getSelf(), getSelf(), NodeGenericCommon.class );
		if ( !sameSelf ) {
			return false;
		}
		// if they are different classes they are not equal, due to not same type
		// ie. a Pointer and a Set
		Q.assumeSameExactClassElseThrow( this, obj );
		// XXX: this detects if same self is used in more than 1 type, which indicated bad usage! user's fault
		
		// it already is: should eventually use .equals() on storages here
		// it just is the case that they're the same ref so .equals() doesn't get called on these
		if ( !Z.equals_enforceExactSameClassTypesAndNotNull( obj.getStorage(), getStorage() ) ) {
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public int hashCode() {
		return getStorage().hashCode() + _selfNode.hashCode();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGeneric#getId()
	 */
	@Override
	public long getId() {
		return getSelf().getId();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGenericCommon#clone()
	 */
	@Override
	public NodeGenericExtensions clone() {
		return (NodeGenericExtensions)super.clone();
	}
}
