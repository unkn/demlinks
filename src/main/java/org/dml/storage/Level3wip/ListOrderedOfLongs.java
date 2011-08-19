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



public class ListOrderedOfLongs implements OrderedList {
	
	protected final FooEnv					env;
	private final Long						self;
	private final L0HashMap_OfNodes	selfAsHashMap;
	
	
	// private ElementCapsule ecHead = null;
	
	
	public ListOrderedOfLongs( final FooEnv env1, final Long longIdentSelf ) {
		assert null != env1;
		assert null != longIdentSelf;
		env = env1;
		self = new Long( longIdentSelf.longValue() );// this is cloned for hitting bug with `==` in later code
		selfAsHashMap = new L0HashMap_OfNodes( env, self );
		env.allLOOL_Set.ensureIsAddedToSet( self );
		// if (!env.allLOOL_Set.contains( self )) {
		// env.allLOOL_Set.addToSet( longIdent )
		// }
		// if (env.isVector( , childLong ))
	}
	
	
	@Override
	public boolean ensure( final Long whichLongIdent ) {
		if ( contains( whichLongIdent ) ) {
			return true;
		} else {
			add( whichLongIdent, Position.LAST );
			return false;
		}
	}
	
	
	private ElementCapsule getExistingHeadOrNull() {
		final Long head = selfAsHashMap.getValue_akaChild( env.allHeadsForLOOL_LongID );
		if ( null != head ) {
			// if ( null != ecHead ) {
			// Q.warn( "the list changed without our awareness" );
			// // ecHead.destroy();
			// // ecHead = null;
			// }
			return ElementCapsule.getExisting( env, head );
		} else {
			return null;
		}
		// if ( null == head ) {
		// head = env.getNewUniqueLongIdent();
		// final boolean ret = selfAsHashMap.put( env.allHeadsForLOOL_LongID, head );
		// assert !ret : "could not have already existed!";
		// }
		//
		// assert null != head;
		// return ecHead;
	}
	
	
	// private Long makeHead() {
	// ElementCapsule ecHead = getExistingHead();
	// assert null == ecHead;
	// ecHead = new ElementCapsule( env, ecHead );
	// }
	
	
	@Override
	public boolean contains( final Long whichLongIdent ) {
		assert null != whichLongIdent;
		
		ElementCapsule parser = getExistingHeadOrNull();
		while ( null != parser ) {
			// if ( null != parser ) {// is not empty
			// else start parsing
			// do {
			final Long elem = parser.getElement_neverNull();
			// assert null != elem;
			if ( whichLongIdent.equals( elem ) ) {
				return true;
			}
			parser = parser.getNextCapsule();
			// } while ( null != ( parser = parser.getNextCapsule() ) );
		}
		return false;
	}
	
	
	public boolean isEmpty() {
		return null == getExistingHeadOrNull();
	}
	
	
	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public void add( final Long whichLongIdent, final Position pos ) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void add( final Long whichLongIdent, final Position pos, final Long posLong ) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public Long remove( final Position pos, final Long posLong ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Long remove( final Position pos ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public boolean remove( final Long whichLong ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public Long get( final Position first ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Long get( final Position pos, final Long posLong ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Long getSelf() {
		return self;
	}
}
