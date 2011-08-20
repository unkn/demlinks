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
 * domain is enforced only if asserts are enabled and only for java calls ie. in java environment<br>
 * does not allow domain or self to be added to set<br>
 * does not check for integrity if domainSet already exists when constructed<br>
 * does not allow: self to equal domain<br>
 */
public class Extension_DomainSet_OfChildren
		extends Extension_Set_OfChildren
		implements IExtension_DomainSet
{
	
	private final NodeGeneric	_domainNode;
	
	
	/**
	 * @param env1
	 * @param selfNode
	 * @param domainNode
	 */
	public Extension_DomainSet_OfChildren( final NodeGeneric selfNode, final NodeGeneric domainNode ) {
		super( selfNode );
		assert null != domainNode;
		assert !Z.equals_enforceExactSameClassTypesAndNotNull( selfNode, domainNode );
		_domainNode = domainNode.clone();// cloned to catch `==` bugs somewhere (do use .equals)
		assert Z.equals_enforceExactSameClassTypesAndNotNull( getDomain(), domainNode );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGenericCommon#isStillValid()
	 */
	@Override
	public boolean isStillValid() {
		final boolean ret1 = super.isStillValid();
		if ( !ret1 ) {
			return ret1;
		}
		
		// checking all current children, wow this is going to be expensive
		final IteratorGeneric_OnChildNodes iter = getIterator();
		iter.goFirst();
		NodeGeneric cur;
		while ( ( cur = iter.getCurrent() ) != null ) {
			if ( !isValidChild( cur ) ) {
				throw Q.bug( "inconsistency detected `" + cur + "` is not a valid child for instance class=`" + this.getClass()
					+ "`" );
				// return false;
			}
			iter.goNext();
		}
		return true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simpler.SetOfChildren#isValidChild(java.lang.Long)
	 */
	@Override
	public boolean isValidChild( final NodeGeneric childNode ) {
		return ( ( super.isValidChild( childNode ) ) && ( isInDomain( childNode ) ) );
	}
	
	
	@Override
	public boolean isInDomain( final NodeGeneric childNode ) {
		// assertIsStillValid();
		return ( ( !getDomain().equals( childNode ) ) && ( getStorage().isVector( getDomain(), childNode ) ) );
	}
	
	
	@Override
	public final NodeGeneric getDomain() {
		assertIsStillValid();
		return _domainNode;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.Level2.L0Set_OfChildren#overriddenEquals(org.dml.storage.Level2.EpicEquals)
	 */
	@Override
	protected boolean equalsOverride( final NodeGenericCommon obj ) {
		// assertIsStillValid();//this is already called in equals()
		if ( !super.equalsOverride( obj ) ) {
			return false;
		}
		Q.assumeSameExactClassElseThrow( this, obj );
		// assert obj.getClass() == this.getClass();
		// assert obj instanceof DomainSet_OfChildren :
		// "user is comparing to a super instance, as oppose to same or subclass";
		assert Z.equals_enforceExactSameClassTypesAndNotNull( _domainNode, ( (Extension_DomainSet_OfChildren)obj )._domainNode ) : Q
			.badCall( "same self but different domains, user did a boobo somewhere" );
		return true;
	}
}
