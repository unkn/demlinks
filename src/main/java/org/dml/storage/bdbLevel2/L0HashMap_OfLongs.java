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
import org.dml.storage.bdbLevel1.*;
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
public class L0HashMap_OfLongs
{
	
	private final BDBStorage		env;
	private final GenericNode		_selfNode;
	
	
	private final L0Set_OfTerminals	selfAsSet;
	
	
	public L0HashMap_OfLongs( final BDBStorage env1, final GenericNode selfNode ) {
		assert null != env1;
		assert null != selfNode;
		env = env1;
		_selfNode = selfNode.clone();// this is cloned for hitting bug with `==` in later code
		selfAsSet = new L0Set_OfTerminals( env, _selfNode );
		
		// checking to make sure all values have (at least) one key
		final GenericIteratorOnTerminalNodes iter = selfAsSet.getIterator();
		try {
			GenericNode cur = iter.goFirst();
			while ( null != cur ) {
				if ( env.countInitials( cur ) <= 0 ) {
					throw Q.badCall( "dangling value (long=" + cur + ", its stringId=" + env.getName( cur )
						+ ") doesn't have a key assoc. with it" );
				}
				cur = iter.goNext();
			}
			
			iter.success();
		} finally {
			iter.finished();
		}
	}
	
	
	public GenericNode getValue_akaTerminal( final GenericNode forKey_akaInitialNode ) {
		assert null != forKey_akaInitialNode;
		return env.findCommonTerminalForInitials( _selfNode, forKey_akaInitialNode );
	}
	
	
	/**
	 * @param initialNode
	 * @param terminalNode
	 * @return true if already existed!
	 */
	public boolean put( final GenericNode initialNode, final GenericNode terminalNode ) {
		final boolean ret1 = selfAsSet.ensureIsAddedToSet( terminalNode );// env.ensureVector( self, terminalLong );
		final boolean ret2 = env.ensureVector( initialNode, terminalNode );
		assert ret1 ^ ret2 : Q.badCall( "both should've been either false or true; but they differed!" );
		return ret1;
	}
	
	
	/**
	 * it will throw if any already existed<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 */
	public void putAsNewOrThrow( final GenericNode initialNode, final GenericNode terminalNode ) {
		final boolean ret = put( initialNode, terminalNode );
		if ( ret ) {
			Q.badCall( "already existed!" );
		}
	}
	
	
	public int size() {
		return selfAsSet.size();
	}
}
