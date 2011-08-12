/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 12, 2011 2:40:33 AM
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
 *
 */
public abstract class EpicBase
		implements NodeGeneric
{
	
	private final StorageGeneric	storage;
	private final NodeGeneric		_selfNode;
	
	
	public EpicBase( final StorageGeneric store, final NodeGeneric selfNode ) {
		assert null != store;
		assert null != selfNode;
		storage = store;
		_selfNode = selfNode.clone();// this is cloned for hitting bug with `==` in later code
	}
	
	
	public final StorageGeneric getStorage() {
		return storage;
	}
	
	
	public NodeGeneric getSelf() {
		return _selfNode;
	}
	
	
	/**
	 * @return it's int(not long) because cursor.count() returns int! bdb limitation i guess
	 */
	public int size() {
		return getStorage().countChildren( getSelf() );
	}
	
	
	public final boolean isEmpty() {
		return 0 == size();
	}
	
	
	public abstract boolean isValidChild( final NodeGeneric node );
	
	
	@Override
	public final boolean equals( final Object obj ) {
		// this will work if SubClass.equals calls super.equals
		if ( null == obj ) {
			return false;
		}
		if ( this == obj ) {
			return true;
		}
		if ( !Z.haveCompatibleClasses_canNotBeNull( this, obj ) ) {
			return false;// silently allowing comparison when different type of classes ie. Interger and String
		}
		// so if we're here they've compatible classes
		// if not same class, but compatible we'll check if using the same self - cause that's a bad usage scenario
		final EpicBase o = (EpicBase)obj;
		if ( !Z.equals_enforceExactSameClassTypesAndNotNull( o.storage, storage ) ) {
			return false;// silently allowing comparison when different storages... ie. BDBJE and BDBJNI or BDBJE and RAMStorage
			// or even two diff instances of BDBJE which do not .equals() according to their own .equals()
		}
		
		return equalsOverride( o );
	}
	
	
	/**
	 * don't forget to call super.{@link #equalsOverride(EpicBase)};<br>
	 * 
	 * @param o
	 * @return
	 */
	protected boolean equalsOverride( final EpicBase o ) {
		// so basically here gets called if the classes of this and o are compatible but if these two return equal via their own
		// equals means they are two diff instances but with same class type? or something anyway if true, they need to be same
		// class type because subclasses are considered to be different type and cannot use the same self node ie. for Pointer
		// and Set both cannot use same self
		return Q.returnParamButIfTrueAssertSameClass0(
			Z.equals_enforceCompatibleClassesAndNotNull( o.getSelf(), getSelf() ),
			this,
			o );
	}
	
	
	@Override
	public int hashCode() {
		return storage.hashCode() + _selfNode.hashCode();
	}
	
	
	@Override
	public EpicBase clone() {// throws CloneNotSupportedException {
		// throw Q.cantClone();
		try {
			return (EpicBase)super.clone();
		} catch ( final CloneNotSupportedException e ) {
			throw Q.cantClone( e );
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.commons.NodeGeneric#getId()
	 */
	@Override
	public long getId() {
		return getSelf().getId();
	}
}
