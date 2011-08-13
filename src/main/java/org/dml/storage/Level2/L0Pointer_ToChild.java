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
 * a pointer is an initial that points to one or none child<br>
 * can even point to self, no constrains ffs<br>
 * this can be modified in other places by other methods and we don't check for integrity, ie. it can become a set of children,
 * I don't give a floop<br>
 */
public class L0Pointer_ToChild
		extends NodeGenericExtensions
{
	
	public L0Pointer_ToChild( final StorageGeneric env1, final NodeGeneric selfNode ) {
		super( env1, new L0Set_OfChildren( env1, selfNode ) );
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
