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
 * a pointer is an initial that points to one or none child<br>
 * can even point to self, no constrains ffs<br>
 * this can be modified in other places by other methods and we don't check for integrity, ie. it can become a set of children,
 * I don't give a floop<br>
 */
public class L0Pointer_ToChild
{
	
	private final L0Set_OfChildren	setOf1Element;
	
	
	public L0Pointer_ToChild( final StorageGeneric env1, final NodeGeneric selfNode ) {
		setOf1Element = new L0Set_OfChildren( env1, selfNode );
	}
	
	
	public NodeGeneric getSelf() {
		return setOf1Element.getSelf();
	}
	
	
	/**
	 * @param toWhatChildNode
	 *            can be null
	 */
	public void setPointee( final NodeGeneric toWhatChildNode ) {
		setOf1Element.clearAll();// removes prev if any
		assert setOf1Element.isEmpty();
		
		if ( null != toWhatChildNode ) {
			final boolean result = setOf1Element.ensureIsAddedToSet( toWhatChildNode );
			assert !result : Q.bug( "should not have existed!" );
			assert setOf1Element.size() == 1;
		}
	}
	
	
	/**
	 * @return null if none
	 */
	public NodeGeneric getPointeeChild() {
		final int size = setOf1Element.size();
		assert ( size == 0 ) || ( size == 1 ) : Q.bug( "inconsistency fail, this pointer `" + getSelf()
			+ "` must point to 0 or 1 children only" );
		if ( size == 0 ) {
			return null;
		}
		
		// get first one (should be only one)
		final IteratorGeneric_OnChildNodes iter = setOf1Element.getIterator();
		try {
			final NodeGeneric termNode = iter.goFirst();
			iter.success();
			return termNode;
		} finally {
			iter.finished();
			// FIXME: I'd use finally but if goFirst and close both throw, only the last throw will be seen ie. overwriting
			// this might not be avoidable, unless finished never throws? or bringing back aspectj hooked throws *puke*
		}
		
	}
	
	
	@Override
	public boolean equals( final Object obj ) {
		if ( null == obj ) {
			return false;
		}
		if ( this == obj ) {
			return true;
		}
		
		assert Z.haveCompatibleClasses_canNotBeNull( this, obj );
		
		return Z.equalsWithExactSameClassTypes_enforceNotNull( ( (L0Pointer_ToChild)obj ).setOf1Element, setOf1Element );
	}
	
	
	@Override
	public int hashCode() {
		return setOf1Element.hashCode();
	}
}
