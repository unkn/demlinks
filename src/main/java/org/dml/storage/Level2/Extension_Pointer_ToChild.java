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



/**
 * a pointer is an parent that points to one or none child<br>
 * can even point to self, no constrains ffs<br>
 * this can be modified in other places by other methods and we don't check for integrity, ie. it can become a set of children,
 * I don't give a floop<br>
 */
public class Extension_Pointer_ToChild
		extends NodeGenericExtensions
		implements IExtension_Pointer
{
	
	// public synchronized static Extension_Pointer_ToChild createNew_PointerToChild( final StorageGeneric storage,
	// final NodeGeneric selfNode ) {
	// assert Q.nn( storage );
	// assert Q.nn( selfNode );
	// final NodeGenericImpl impl = selfNode.getSelfImpl();
	// // assert isNoExtensionAllocatedForNodeImpl( impl );
	// final NodeGenericExtensions existingInstance = getExtensionInstanceForNodeImpl( impl );
	// if ( null != existingInstance ) {
	// Q.badCall( "already existed, cannot exclusively create!" );
	// }
	// final Extension_Pointer_ToChild newInstance = new Extension_Pointer_ToChild( storage, selfNode );
	// putExtensionInstanceForNodeImpl( newInstance, impl );
	// assert Z.equals_enforceExactSameClassTypesAndNotNull( getExtensionInstanceForNodeImpl( impl ), newInstance );
	// return newInstance;
	// }
	//
	//
	// public synchronized static Extension_Pointer_ToChild getExisting_PointerToChild( final StorageGeneric storage,
	// final NodeGeneric selfNode ) {
	// assert Q.nn( storage );
	// assert Q.nn( selfNode );
	// final Extension_Pointer_ToChild existingInstance =
	// internal_get_Extension( storage, selfNode, Extension_Pointer_ToChild.class );
	// if ( null == existingInstance ) {
	// throw Q.badCall( "cannot exclusively get, it didn't already exist!" );
	// } else {
	// return existingInstance;
	// }
	// }
	//
	//
	// @SuppressWarnings( "unchecked" )
	// protected synchronized static <T extends NodeGenericExtensions> T
	// internal_get_Extension( final StorageGeneric storage, final NodeGeneric selfNode,
	// final Class<T> expectedExtensionClass ) {
	// assert Q.nn( storage );
	// assert Q.nn( selfNode );
	// final NodeGenericImpl impl = selfNode.getSelfImpl();
	// final NodeGenericExtensions existingInstance = getExtensionInstanceForNodeImpl( impl );
	// if ( null != existingInstance ) {
	// assert Z.isSameOrDescendantOfClass_throwIfNull( existingInstance, expectedExtensionClass ) : "this node `"
	// + selfNode
	// + "` "
	// + "was used for a different Extension namely for `"
	// + existingInstance
	// + "`\n"
	// + "thus you cannot use this same node for a different extension type - because this would be a bad usage: a node "
	// + "is supposed to represent only one of these extensions ie. can't be a pointer and a set at the same time because"
	// + "treating it as a set would most likely violate pointer constrains ie. by adding more than 1 children";
	// return (T)existingInstance;
	// } else {
	// return null;
	// }
	// }
	//
	//
	// public synchronized static Extension_Pointer_ToChild getOrCreate_PointerToChild( final StorageGeneric storage,
	// final NodeGeneric selfNode ) {
	// assert Q.nn( storage );
	// assert Q.nn( selfNode );
	// Extension_Pointer_ToChild existingInstance =
	// internal_get_Extension( storage, selfNode, Extension_Pointer_ToChild.class );
	// if ( null == existingInstance ) {
	// existingInstance = createNew_PointerToChild( storage, selfNode );
	// assert Q.nn( existingInstance );
	// }
	// return existingInstance;
	// }
	
	
	/**
	 * constructor, supposed to be accessible only by subclasses
	 * 
	 * @param storage
	 * @param selfNode
	 *            can be an Implementation or an Extension(which is based upon an implementation, ofc)
	 */
	protected Extension_Pointer_ToChild( final StorageGeneric storage, final NodeGeneric selfNode ) {
		super( storage, new L0Set_OfChildren( storage, selfNode ) );
		// XXX1309: constructor param order should be same; see other XXX1309
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.Level2.EpicBase#getSelf()
	 */
	@Override
	public L0Set_OfChildren getSelf() {
		return (L0Set_OfChildren)super.getSelf();
	}
	
	
	/**
	 * @param toWhatChildNode
	 *            can be null
	 */
	@Override
	public void setPointee( final NodeGeneric toWhatChildNode ) {
		assert isValidChild( toWhatChildNode );
		getSelf().clearAll();// removes prev if any
		assert getSelf().isEmpty();
		
		if ( null != toWhatChildNode ) {
			final boolean existed = getSelf().ensureIsAddedToSet( toWhatChildNode );
			assert !existed : Q.bug( "should not have existed!" );
			assert getSelf().size() == 1;
		}
	}
	
	
	/**
	 * @return null if none
	 */
	@Override
	public NodeGeneric getPointeeChild() {
		final int size = getSelf().size();
		assert ( size == 0 ) || ( size == 1 ) : Q.bug( "inconsistency fail, this pointer `" + getSelf()
			+ "` must point to 0 or 1 children only" );
		NodeGeneric termNode = null;
		if ( size > 0 ) {
			// get first one (should be only one)
			final IteratorGeneric_OnChildNodes iter = getSelf().getIterator();
			try {
				termNode = iter.goFirst();
				iter.success();
				
			} finally {
				iter.finished();
				// FIXME: I'd use finally but if goFirst and close both throw, only the last throw will be seen ie. overwriting
				// this might not be avoidable, unless finished never throws? or bringing back aspectj hooked throws *puke*
			}
		}
		assert isValidChild( termNode ) : "something else must've changed our pointee and made this inconsistent with our domain";
		return termNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.Level2.EpicBase#isValidChild(org.dml.storage.commons.NodeGeneric)
	 */
	@Override
	public boolean isValidChild( final NodeGeneric node ) {
		return true;
	}
	
}
