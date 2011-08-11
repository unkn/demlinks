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

package org.references2;



import org.q.*;
import org.toolza.*;



/**
 * can allow `null` objects<br>
 * 
 * @param <T>
 *            type of the referenced object
 */
public class Reference<T>
		extends Object
{
	
	// it's unset by default
	private boolean	set		= false;
	private T		object	= null;
	
	
	protected synchronized void internal_reset() {
		set = false;
		object = null;
	}
	
	
	public synchronized boolean isSet() {
		return set;
	}
	
	
	private synchronized void set() {
		assert !set;
		set = true;
	}
	
	
	public Reference( final T obj ) {
		super();
		setObject( obj );
	}
	
	
	public Reference() {
		internal_reset();
	}
	
	
	public synchronized void setObject( final T obj ) {
		object = obj;
		if ( !isSet() ) {
			set();
		}
	}
	
	
	/**
	 * @return the object that this reference refers to
	 */
	public synchronized T getObject() {
		assert isSet() : Q.badCall( "you assumed it was set" );
		return object;
	}
	
	
	
	/**
	 * erm ok, this is odd, we're actually comparing the `Reference`s objects not the object that they refer to<br>
	 * in other words, we're treating these are java objects aka Object, and they are different only if they are
	 * different instances<br>
	 * so they can be two diff instances referring to same object and they are still not equal<br>
	 * making a list of `Reference`s and wanting them to be unique implies always using different instances, otherwise
	 * making a list of Objects referred to by `Reference`s means using a TreeSet or similar and implement Comparable so
	 * that it compares the content aka objects referred to by `References` such that two diff Reference`s cannot point
	 * to the same object, but this is not a matter to be concerning us here<br>
	 * 
	 */
	@Override
	public synchronized boolean equals( final Object obj1 ) {
		return Z.equalsByReference_enforceNotNull( this, obj1 );
		// if ( null == obj1 )
		// {
		// Q.badCall( "comparing to `null`" );
		// // return false;// not reached
		// }
		// // if ( !Z.areSameClass(
		// // obj1,
		// // this ) )
		// // {
		// // // can use instanceof but I want to ensure it's same class, not potentially a subclass of
		// // Q.badCall( "comparing different classes even though one can be a subclass of the other" );
		// //
		// // }
		// return this == obj1;
		// boolean ret =
		// true;
		// if ( !super.equals( obj1 ) )
		// {
		// ret =
		// false;
		// // if ( null != obj1 )
		// // {
		// if ( Z.areSameClass(
		// obj1,
		// this ) )
		// {
		// Reference<?> that =
		// ( (Reference<?>)obj1 );
		// if ( !( this.isSet() ^ that.isSet() ) )
		// {// 1^1=0, 0^0=0, 0^1 || 1^0 == 1 and so !0=1 meaning only if both are equal we get into this if
		// if ( this.isSet() )
		// {// this means both have an object set even if this object is and can be `null`
		// T thisObj =
		// this.getObject();
		// // Type safety: Unchecked cast from Object to Reference<T> solved by using Reference<?>
		// Object thatO =
		// ( (Reference<?>)obj1 ).getObject();
		// ret =
		// Z.areEqual(
		// thisObj,
		// thatO );
		// }
		// else
		// {
		// // both have unset objects, and thus are equals so far
		// ret =
		// true;
		// }
		// }// else one of them has an object set and the other doesn't so the ret remains false
		// }
		// // }
		// }// ==
		//
		// if ( ret )
		// {
		// }
		// return ret;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public synchronized int hashCode() {
		// if ( isSet() )
		// {
		// if ( this.getObject() != null )
		// {
		// return this.getObject().hashCode();
		// }
		// }
		return super.hashCode();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		final String self = this.getClass().getCanonicalName();
		String obj = null;
		if ( isSet() ) {
			if ( null != this.getObject() ) {
				obj = this.getObject().toString();
			}
		}
		return self + ":isSet==" + isSet() + ":Obj==" + obj;
	}
	
	
	public synchronized void destroy()
	
	{
		internal_reset();
	}
}
