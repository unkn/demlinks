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



/**
 * self->list1
 * self->set1
 * allListsOrderedOfLongs->list1
 * allListsOrderedOfLongsWithFastFind->self
 * 
 */
public class ListOrderedOfLongsWithFastFind implements OrderedList {
	
	protected final FooEnv					env;
	private final Long						self;
	private final L0HashMap_OfNodes	selfAsHashMap;
	private final ListOrderedOfLongs		orderedList;
	private final L0Set_OfChildren	ffSet;			// fast find set
															
															
															
	public ListOrderedOfLongsWithFastFind( final FooEnv env1, final Long longIdentSelf ) {
		assert null != env1;
		assert null != longIdentSelf;
		env = env1;
		self = new Long( longIdentSelf.longValue() );// this is cloned for hitting bug with `==` in later code
		selfAsHashMap = new L0HashMap_OfNodes( env, self );
		
		// env.allLOOLWFF_Set->self
		if ( env.allLOOLWFF_Set.contains( self ) ) {
			// then it already exists, more or less, as LOOLWFF
		} else {
			final boolean ret = env.allLOOLWFF_Set.ensureIsAddedToSet( self );
			assert !ret : Q.bug( "could not have already existed, else the `if` before is bugged" );
		}
		
		final int size = selfAsHashMap.size();
		assert ( size == 0 ) || ( size == 2 ) : "size can be one of two 0,2 but was size=" + size;
		
		if ( size == 0 ) {
			// we need to create those
			selfAsHashMap.put( env.allLOOL_LongID, env.getNewUniqueNode_NeverNull() );// orderedList
			selfAsHashMap.put( env.allSetsOfLOOLWWF_LongID, env.getNewUniqueNode_NeverNull() );// ffSet
		}
		// they exist now, we're getting them
		
		assert selfAsHashMap.size() == 2;
		// env.allLOOL_LongID->olSelf
		// self->olSelf
		final Long olSelf = selfAsHashMap.getValue_akaChild( env.allLOOL_LongID );
		assert null != olSelf;
		orderedList = new ListOrderedOfLongs( env, olSelf );
		
		// env.allSetsOfLOOLWWF_LongID->ffSet
		final Long ffSelf = selfAsHashMap.getValue_akaChild( env.allSetsOfLOOLWWF_LongID );
		assert null != ffSelf;
		ffSet = new L0Set_OfChildren( env, ffSelf );
	}
	
	
	
	@Override
	public boolean ensure( final Long whichLongIdent ) {
		if ( ffSet.contains( whichLongIdent ) ) {
			return true;
		}
		final boolean ret = ffSet.ensureIsAddedToSet( whichLongIdent );
		assert !ret : "couldn't alredy exist, else .contains() is bugged";
		orderedList.add( whichLongIdent, Position.LAST );
		return false;
	}
	
	
	
	@Override
	public boolean contains( final Long whichLongIdent ) {
		assert null != whichLongIdent;
		return ffSet.contains( whichLongIdent );
	}
	
	
	
	@Override
	public long size() {
		return orderedList.size();
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
