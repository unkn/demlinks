/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 12, 2011 2:51:44 PM
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
package org.dml.storage.commons;

import org.dml.storage.Level2.*;
import org.q.*;
import org.toolza.*;



/**
 *
 */
public abstract class NodeGenericCommon
		implements NodeGeneric
{
	
	private final StorageGeneric	storage;
	
	
	/**
	 * @param stor
	 */
	public NodeGenericCommon( final StorageGeneric stor ) {
		storage = stor;
		assert Q.nn( storage );
	}
	
	
	@Override
	public final StorageGeneric getStorage() {
		return storage;
	}
	
	
	@Override
	public final boolean equals( final Object obj ) {
		// this will work if SubClass.equals calls super.equals
		if ( null == obj ) {
			return false;
		}
		if ( this == obj ) {
			return true;
		}
		
		if ( !Z.isSameOrDescendantOfClass_throwIfNull( obj, NodeGenericCommon.class ) ) {
			if ( !Z.isSameOrDescendantOfClass_throwIfNull( obj, NodeGeneric.class ) ) {
				throw Q.bug( "totally incompat classes for:\n " + this.getClass() + "\n " + obj.getClass() );
			}
			// silently allowing comparison when different NodeGeneric implementations
			// ie. just in case user added them into a HashMap but they are not compatible though
			// they'd be compatible if they would be same as the current class we're in now (NodeGenericCommon unless I renamed
			// it)
			return false;
		}
		
		final NodeGenericCommon ngc = (NodeGenericCommon)obj;
		// fixed I think: maybe below equals may need some relaxing for class types
		if ( !Z.equalsSimple_enforceNotNull( getStorage(), ngc.getStorage() ) ) {
			return false;// different storages means different nodes
		}
		// if (this.getClass().equals( NodeGenericImpl.class )) &&(obj.getClass().equals( NodeGenericImpl.class ))) {
		//
		// }
		
		final boolean isImpl1 = Z.isSameOrDescendantOfClass_throwIfNull( this, NodeGenericImpl.class );
		final boolean isImpl2 = Z.isSameOrDescendantOfClass_throwIfNull( obj, NodeGenericImpl.class );
		if ( ( isImpl1 ) && ( isImpl2 ) ) {
			// they are both implementations, we compare their IDs then
			final NodeGenericImpl impl = (NodeGenericImpl)obj;
			final boolean r1 = equalsOverride( impl );
			final boolean r2 = impl.equalsOverride( this );
			assert ( !( r1 ^ r2 ) ) : "incompatible equalsOverride() encountered, each returned opposite result\n"
				+ "Participating classes:\n" + this.getClass() + "\n" + impl.getClass();
			return r1;
			// Z.e
			// Q.bug( "you forgot to implement an equals() specific to implementations only\n"
			// + "because their equals would've taken over, instead of getting here" );
		} else {
			if ( ( isImpl1 ^ isImpl2 ) ) {// one of the is, and one isn't
				// ^ is xor; 1^1=0 0^0=0 so we're here because: 1^0=1 and 0^1=1
				// if any of them is an implementation, then we must compare implementations by using their .equals
				// but not both are impl. at the same time, we call their equals
				return Z.equals_enforceSameBaseClassAndNotNull(
					getSelfImpl(),
					( (NodeGenericCommon)obj ).getSelfImpl(),
					NodeGenericImpl.class );
			}// else neither of them is that kind of descendant
		}
		// so they must then both be descendants of extensions:
		assert Z.isSameOrDescendantOfClass_throwIfNull( this, NodeGenericExtensions.class );
		assert Z.isSameOrDescendantOfClass_throwIfNull( obj, NodeGenericExtensions.class );
		final NodeGenericExtensions o = (NodeGenericExtensions)obj;
		// assert Z.areSameClass_canNotBeNull( o1, o2 )
		// if ( !Z.haveCompatibleClasses_canNotBeNull( this, obj ) ) {
		// if ( !Z.isDescendatOfClass_canNotBeNull( NodeGeneric.class, obj ) ) {
		// throw Q.bug( "totally incompat classes for\n" + this.getClass() + "\n" + obj.getClass() );
		// }
		//
		// return Z.equalsWithCompatClasses_allowsNull( getSelf(), obj );
		// // obsolete comment?: silently allowing comparison when different type of classes ie. Interger and String
		// }
		// // so if we're here they've compatible classes
		// // if not same class, but compatible we'll check if using the same self - cause that's a bad usage scenario
		// assert Z.isDescendatOfClass_throwIfNull( obj, NodeGenericCommon.class );
		// final NodeGenericCommon o = (NodeGenericCommon)obj;
		
		return equalsOverride( o );
	}
	
	
	/**
	 * don't forget to call super.{@link #equalsOverride(EpicBase)};<br>
	 * 
	 * @param o
	 * @return
	 */
	protected abstract boolean equalsOverride( final NodeGenericCommon o );
	
	
	// {
	// // so basically here gets called if the classes of this and o are compatible but if these two return equal via their own
	// // equals means they are two diff instances but with same class type? or something anyway if true, they need to be same
	// // class type because subclasses are considered to be different type and cannot use the same self node ie. for Pointer
	// // and Set both cannot use same self
	// return // Q.returnParamButIfTrueAssertSameClass0(
	// Z.equals_enforceSameBaseClassAndNotNull( o.getSelf(), getSelf(), NodeGenericCommon.class );
	// // this,
	// // o );
	// }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
	@Override
	public NodeGenericCommon clone() {// throws CloneNotSupportedException {
		// throw Q.cantClone();
		try {
			final NodeGenericCommon c = (NodeGenericCommon)super.clone();
			assert c != this;
			assert c.equals( this );
			return c;
		} catch ( final CloneNotSupportedException e ) {
			throw Q.cantClone( e );
		}
	}
	
}
