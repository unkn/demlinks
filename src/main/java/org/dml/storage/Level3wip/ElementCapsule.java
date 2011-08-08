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
package org.dml.storage.Level3wip;

import org.dml.storage.Level2.*;
import org.q.*;



public class ElementCapsule {
	
	private final FooEnv					env;
	private final Long						self;
	private final L0HashMap_OfLongs	selfAsHashMap;
	private L0Pointer_ToChild		ptr2Prev;
	private L0Pointer_ToChild		ptr2Element;
	private L0Pointer_ToChild		ptr2Next;
	
	
	public static ElementCapsule getExisting( final FooEnv env1, final Long self1 ) {
		assert null != env1;
		assert null != self1;
		final ElementCapsule ec = new ElementCapsule( env1, self1 );
		ec.initAsExisting();
		return ec;
	}
	
	
	public static ElementCapsule getNew( final FooEnv env1, final Long self1, final Long element ) {
		assert null != env1;
		assert null != self1;
		assert null != element;
		final ElementCapsule ec = new ElementCapsule( env1, self1 );
		
		ec.env.createNewVectorOrThrow( ec.env.allElementCapsulesForLOOL_LongID, ec.self );
		
		// element;
		final int size = ec.selfAsHashMap.size();
		assert ( size == 0 ) : "size=" + size;
		// create all
		ec.selfAsHashMap.putAsNewOrThrow( ec.env.allPtrToPrevForElementCapsules_LongID, ec.env.getNewUniqueNode_NeverNull() );
		ec.selfAsHashMap.putAsNewOrThrow( ec.env.allPtrToNextForElementCapsules_LongID, ec.env.getNewUniqueNode_NeverNull() );
		final Long ptr2Elem = ec.env.getNewUniqueNode_NeverNull();
		ec.selfAsHashMap.putAsNewOrThrow( ec.env.allPtrToElementForElementCapsules_LongID, ptr2Elem );
		ec.env.createNewVectorOrThrow( ptr2Elem, element );
		
		ec.initAsExisting();
		return ec;
	}
	
	
	private void initAsExisting() {
		assert env.isVector( env.allElementCapsulesForLOOL_LongID, self );
		
		// they all exist by now
		assert selfAsHashMap.size() == 3;
		
		final Long ptr2Prev_long = selfAsHashMap.getValue_akaChild( env.allPtrToPrevForElementCapsules_LongID );
		assert null != ptr2Prev_long;
		ptr2Prev = new L0Pointer_ToChild( env, ptr2Prev_long );
		// at this point it can be non-null, since we don't know if we just created it or it already exited!
		// this can be null, if if the only capsule in the list (ie. both first and last; aka head/tail)
		
		final Long ptr2Next_long = selfAsHashMap.getValue_akaChild( env.allPtrToNextForElementCapsules_LongID );
		assert null != ptr2Next_long;
		ptr2Next = new L0Pointer_ToChild( env, ptr2Next_long );
		// this can be null, if if the only capsule in the list (ie. both first and last; aka head/tail)
		
		final Long ptr2Element_long = selfAsHashMap.getValue_akaChild( env.allPtrToElementForElementCapsules_LongID );
		assert null != ptr2Element_long;
		ptr2Element = new L0Pointer_ToChild( env, ptr2Element_long );
		// assert null != getElement_neverNull() : "the existing capsule must point to an element, it can't be null(yet it is)";
		getElement_neverNull();
	}
	
	
	/**
	 * constructor<br>
	 * only call this if it's existing!<br>
	 * 
	 * @param env1
	 * @param self1
	 */
	private ElementCapsule( final FooEnv env1, final Long self1 ) {
		assert null != env1;
		assert null != self1;
		env = env1;
		self = self1;
		
		selfAsHashMap = new L0HashMap_OfLongs( env, self );
	}
	
	
	
	public void setPrevCapsule( final ElementCapsule nullOrCapsule ) {
		ptr2Prev.setPointee( null == nullOrCapsule ? null : nullOrCapsule.self );
	}
	
	
	public void setNextCapsule( final ElementCapsule nullOrCapsule ) {
		ptr2Next.setPointee( null == nullOrCapsule ? null : nullOrCapsule.self );
	}
	
	
	public void setElement( final Long nonNullElement ) {
		assert null != nonNullElement;
		ptr2Element.setPointee( nonNullElement );
	}
	
	
	public ElementCapsule getNextCapsule() {
		final Long p = ptr2Next.getPointeeChild();
		if ( null == p ) {
			return null;
		} else {
			return ElementCapsule.getExisting( env, p );
		}
	}
	
	
	public ElementCapsule getPrevCapsule() {
		final Long p = ptr2Prev.getPointeeChild();
		if ( null == p ) {
			return null;
		} else {
			return ElementCapsule.getExisting( env, p );
		}
	}
	
	
	public Long getElement_neverNull() {
		final Long e = ptr2Element.getPointeeChild();
		if ( null == e ) {
			throw Q.badCall( "was null" );
		} else {
			return e;
		}
	}
	
	
	// /**
	// * don't use `this` after this call!<br>
	// */
	// public void destroy() {
	// setPrevCapsule( null );
	// assert null == getPrevCapsule();
	// setNextCapsule( null );
	// assert null == getNextCapsule();
	// ptr2Element.setPointee( null );
	// assert null == getElement();
	//
	// self = null;
	// env = null;
	// }
	
}
