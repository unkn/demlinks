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

import org.dml.storage.*;
import org.q.*;



/**
 * cannot have null elements in set for obvious reasons ie. null cannot be stored in database though user can define some value
 * to act as null but that's on a higher level<br>
 * allows self to be added to set, ie. circular allower<br>
 */
public class L0Set_OfChildren
{
	
	protected final StorageGeneric	env;
	private final NodeGeneric		_selfNode;
	
	
	/**
	 * set may already exist ie. have children<br>
	 * 
	 * @param env1
	 * @param selfNode
	 *            non-null<br>
	 *            will be cloned(for now)<br>
	 */
	public L0Set_OfChildren( final StorageGeneric env1, final NodeGeneric selfNode ) {
		assert null != env1;
		assert null != selfNode;
		env = env1;
		_selfNode = selfNode.clone();// this is cloned for hitting bug with `==` in later code
	}
	
	
	public NodeGeneric getSelf() {
		return _selfNode;
	}
	
	
	/**
	 * @param node
	 * @return true if already existed
	 */
	public boolean ensureIsAddedToSet( final NodeGeneric node ) {
		assert isValidChild( node );
		return env.ensureVector( _selfNode, node );
	}
	
	
	public boolean contains( final NodeGeneric longIdent ) {
		// assert this.isValidChild( longIdent ); no
		return env.isVector( _selfNode, longIdent );
	}
	
	
	/**
	 * @return it's int(not long) because cursor.count() returns int! bdb limitation i guess
	 */
	public int size() {
		return env.countChildren( _selfNode );
	}
	
	
	public boolean isEmpty() {
		return 0 == size();
	}
	
	
	public boolean remove( final NodeGeneric node ) {
		assert isValidChild( node );
		return env.removeVector( _selfNode, node );
	}
	
	
	public boolean isValidChild( final NodeGeneric node ) {
		return ( null != node );// allowing add self to set //&& ( !self.equals( longIdent ) );
	}
	
	
	/**
	 * don't forget to call iter.close() when done<br>
	 * 
	 * @return
	 */
	public IteratorGeneric_OnChildNodes getIterator() {
		return env.getIterator_on_Children_of( _selfNode );
	}
	
	
	public void clearAll() {
		// Q.ni();
		// TODO: implement this in another way, ie. by deleting that key? because deleting all its children kind of does that
		// exact thing, but slower, yes?
		final IteratorGeneric_OnChildNodes iter = getIterator();
		try {
			iter.deleteAll();
			assert iter.size() == 0;
			iter.success();
		} finally {
			iter.finished();
		}
		assert isEmpty();
	}
	
	
	@Override
	public boolean equals( final Object obj ) {
		// this will work if SubClass.equals calls super.equals
		if ( null == obj ) {
			return false;
		}
		
		assert Q.assumeSameFamilyClasses( this, obj );
		
		// if they are equal, they must not be different classes
		return Q.returnParamButIfTrueAssertSameClass( ( (L0Set_OfChildren)obj )._selfNode.equals( _selfNode ), this, obj );
	}
	
	
	@Override
	public int hashCode() {
		return _selfNode.hashCode();
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// FIXME: use Q.showEx for the case when this is thrown in try and overwritten in finally, to be seen on console
		throw new CloneNotSupportedException( "not implemented" );
	}
}
