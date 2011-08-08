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
 * a pointer is an initial that points to one or none terminal<br>
 * can even point to self, no constrains ffs<br>
 * this can be modified in other places by other methods and we don't check for integrity, ie. it can become a set of terminals,
 * I don't give a floop<br>
 */
public class L0Pointer_ToTerminal
{
	
	private final L0Set_OfTerminals	setOf1Element;
	
	
	public L0Pointer_ToTerminal( final StorageBDBNative env1, final NodeGeneric selfNode ) {
		setOf1Element = new L0Set_OfTerminals( env1, selfNode );
	}
	
	
	public NodeGeneric getSelf() {
		return setOf1Element.getSelf();
	}
	
	
	/**
	 * @param toWhatTerminalNode
	 *            can be null
	 */
	public void setPointee( final NodeGeneric toWhatTerminalNode ) {
		setOf1Element.clearAll();// removes prev if any
		assert setOf1Element.isEmpty();
		
		if ( null != toWhatTerminalNode ) {
			final boolean result = setOf1Element.ensureIsAddedToSet( toWhatTerminalNode );
			assert !result : Q.bug( "should not have existed!" );
			assert setOf1Element.size() == 1;
		}
	}
	
	
	/**
	 * @return null if none
	 */
	public NodeGeneric getPointeeTerminal() {
		final int size = setOf1Element.size();
		assert ( size == 0 ) || ( size == 1 ) : Q.bug( "inconsistency fail, this pointer `" + getSelf()
			+ "` must point to 0 or 1 terminals only" );
		if ( size == 0 ) {
			return null;
		}
		
		// get first one (should be only one)
		final IteratorOnTerminalNodesGeneric iter = setOf1Element.getIterator();
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
		
		assert Q.assumeSameFamilyClasses( this, obj );
		
		// if they are equal, they must not be different classes
		return Q.returnParamButIfTrueAssertSameClass(
			( (L0Pointer_ToTerminal)obj ).setOf1Element.equals( setOf1Element ),
			this,
			obj );
	}
	
	
	@Override
	public int hashCode() {
		return setOf1Element.hashCode();
	}
}
