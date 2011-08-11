/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 4, 2011 5:32:00 PM
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
package org.toolza;

import org.q.*;



/**
 *
 */
public abstract class Z
{
	
	public static boolean equalsWithExactSameClassTypes_enforceNotNull( final Object o1, final Object o2 ) {
		assert Z.areSameClass_canNotBeNull( o1, o2 );
		return equalsWithCompatClasses_enforceNotNull( o1, o2 );
	}
	
	
	public static boolean equalsWithCompatClasses_enforceNotNull( final Object o1, final Object o2 ) {
		assert Q.nn( o1 );
		assert Q.nn( o2 );
		if ( o1 == o2 ) {
			return true;
		}
		// ok, different references which are also both not null
		if ( haveCompatibleClasses_canNotBeNull( o1, o2 ) ) {
			final boolean ret1 = o1.equals( o2 );
			if ( !areSameClass_canNotBeNull( o1, o2 ) ) {
				final boolean ret2 = o2.equals( o1 );
				assert !( ret1 ^ ret2 ) : Q.bug( "two incompatible .equals() defined for each of the object's classes: o1("
					+ o1.getClass() + ") and o2(" + o2.getClass() + ")" );
			}
			return ret1;// == ret2
		} else {// two diff instances of two diff subclasses of the same base class
				// ie. X extends B and Y extends B so B->X and B->Y but instances of X and Y should return false
				// when compared, usually inside HashMap<B> when they are keys
				// but don't throw here
				// can't throw here and then catch when testing TestThrows.java it will fail for some reason
			throw Q.badCall( "comparing incompatible objects based on their classes o1=" + o1.getClass() + ", o2="
				+ o2.getClass() );
			// return false;// not reached
		}
	}
	
	
	/**
	 * if they are non-null, this method enforces that they have compatible classes, ie. derived from same BaseParent
	 * 
	 * @param o1
	 *            can be null
	 * @param o2
	 *            can be null
	 * @return uses .equals() but handles well if they are `null`<br>
	 *         true if both are `null` or they are o1.equals(o2)<br>
	 */
	public static boolean equalsWithCompatClasses_allowsNull( final Object o1, final Object o2 ) {
		if ( null == o1 ) {
			return null == o2;// true=both null and thus equal; false=o1 is null but o2 isn't thus different
		} else {// o1 isn't null
			if ( null == o2 ) {
				return false;
			} else {// o2 isn't null as well as o1
				if ( o1 == o2 ) {// same reference
					return true;
				} else {// different instances
					return equalsWithCompatClasses_enforceNotNull( o1, o2 );
				}
			}
		}
	}// equals
	
	
	/**
	 * @param o1
	 * @param o2
	 * @return true if same exact class<br>
	 *         false if different classes, even if compatible ones, or parent/child
	 * @throws NullPointerException
	 *             if any of them are null
	 */
	public static boolean areSameClass_canNotBeNull( final Object o1, final Object o2 ) {
		assert Q.nn( o1 );
		assert Q.nn( o2 );
		final boolean equals = o1.getClass().equals( o2.getClass() );// null would be detected here
		final boolean refEquals = o1.getClass() == o2.getClass();
		assert !( equals ^ refEquals );// consistency check, both should have the same value either true or false
		return equals;
	}
	
	
	/**
	 * checks if the two objects are part of the same hierarchy ie. parent/child or child/parent or same/same class type
	 * 
	 * @param o1
	 * @param o2
	 * @return true of o1 is same or super of o2;<br>
	 *         or if o2 is same or super of o1;<br>
	 *         false otherwise
	 */
	public static boolean haveCompatibleClasses_canNotBeNull( final Object o1, final Object o2 ) {
		assert Q.nn( o1 );
		assert Q.nn( o2 );
		if ( !areSameClass_canNotBeNull( o1, o2 ) ) {
			return ( o1.getClass().isAssignableFrom( o2.getClass() ) ) || o2.getClass().isAssignableFrom( o1.getClass() );
		}
		return true;// same class
	}
	
	
	/**
	 * compares by reference<br>
	 * doesn't allow comparing to `null`<br>
	 * since it's by reference it automatically means THEY HAVE THE SAME CLASS, or rather THEY ARE BOTH THE SAME INSTANCE<br>
	 * 
	 * @param o1
	 *            cannot be null
	 * @param o2
	 *            cannot be null
	 * @return true/false or throws if null
	 * 
	 */
	public static boolean equalsByReference_enforceNotNull( final Object o1, final Object o2 ) {
		// assert null != o1;
		if ( null == o1 ) {
			Q.badCall( "first element is null" );
		}
		if ( null == o2 ) {
			Q.badCall( "comparing to `null` ; second element is null" );
		}
		return o1 == o2;
	}
	
	
	public static boolean equalsByReference_allowsNull( final Object o1, final Object o2 ) {
		return o1 == o2;
	}
	
}
