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
 * self->A,B,C<br>
 * some1->A<br>
 * some2->B<br>
 * some3->C<br>
 * where some* is key<br>
 * and A,B,C are values<br>
 * and self is self, it's not key<br>
 * basically, it's like having multiple pointers with the same/common domain==self<br>
 * 
 * XXX: unsolved issue in many places is, that if the underlying Node gets removed then any in-java instances using it are not
 * notified and are now likely invalid!<br>
 * so if they can't get notified, then maybe they should check that itself (the in-java instance) is still valid with the
 * underlying database
 * before doing any work/delegating calls to the database; well this should be done in like a transaction or sequentially, to
 * ensure atomicity between the self-check and delegated db calls<br>
 * thing is, doing this in transactions will cause deadlocks like crazy, ie. parsing from 1..n in 1 thread and parsing from n..1
 * in another; locking is no good, some sort of multiversioning like git would be preferred
 */
public class L0HashMap_OfNodes
{
	
	private final StorageGeneric			storage;
	private final NodeGeneric				_selfNode;
	
	
	private final Extension_Set_OfChildren	selfAsSet;
	
	
	public L0HashMap_OfNodes( final NodeGeneric selfNode ) {
		// assert null != stor;
		assert null != selfNode;
		storage = selfNode.getStorage();
		assert Q.nn( storage );
		_selfNode = selfNode.clone();// this is cloned for hitting bug with `==` in later code
		selfAsSet = new Extension_Set_OfChildren( _selfNode );
		
		// checking to make sure all values have (at least) one key
		final IteratorGeneric_OnChildNodes iter = selfAsSet.getIterator();
		try {
			NodeGeneric curNode = iter.goFirst();
			while ( null != curNode ) {
				if ( storage.countParents( curNode ) <= 0 ) {
					throw Q.badCall( "dangling value (node=" + curNode + ", its name=" + storage.getName( curNode )
						+ ") doesn't have a key assoc. with it" );
				}
				curNode = iter.goNext();
			}
			
			iter.success();
		} finally {
			iter.finished();
		}
	}
	
	
	public NodeGeneric getValue_akaChild( final NodeGeneric forKey_akaParentNode ) {
		assert null != forKey_akaParentNode;
		return storage.findCommonChildForParents( _selfNode, forKey_akaParentNode );
	}
	
	
	/**
	 * @param parentNode
	 * @param childNode
	 * @return true if already existed!
	 */
	public boolean put( final NodeGeneric parentNode, final NodeGeneric childNode ) {
		final boolean ret1 = selfAsSet.ensureIsAddedToSet( childNode );// env.ensureVector( self, childLong );
		final boolean ret2 = storage.ensureVector( parentNode, childNode );
		assert ret1 ^ ret2 : Q.badCall( "both should've been either false or true; but they differed!" );
		return ret1;
	}
	
	
	/**
	 * it will throw if any already existed<br>
	 * 
	 * @param parentNode
	 * @param childNode
	 */
	public void putAsNewOrThrow( final NodeGeneric parentNode, final NodeGeneric childNode ) {
		final boolean ret = put( parentNode, childNode );
		if ( ret ) {
			Q.badCall( "already existed!" );
		}
	}
	
	
	public int size() {
		return selfAsSet.size();
	}
}
