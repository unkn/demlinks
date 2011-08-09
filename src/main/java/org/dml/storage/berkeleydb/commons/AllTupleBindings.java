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
package org.dml.storage.berkeleydb.commons;

import java.util.*;

import org.q.*;

import com.sleepycat.bind.tuple.*;



public class AllTupleBindings
{
	
	@SuppressWarnings( "rawtypes" )
	private static final Map<Class, TupleBinding>	nonPrimitives	= new HashMap<Class, TupleBinding>();
	static {
		// add to this list any objects that you expect to store into a BDB
		// dbase, allows overriding existing ones from TupleBinding
		// addNonPrimitiveRestrictedToSameClass( BDBNode.class, new BDBNodeBinding() );
		addNonPrimitiveAllowingSubclass( NodeBDB.class, new Binding_BDBNode() );
	}
	
	
	// parametric method erm, generic method see generics-tutorial.pdf on inet
	/**
	 * associate class with this binding<br>
	 * 
	 * @param cls
	 *            SUB a subclass
	 * @param binding
	 *            TupleBinding\<BASE\> where BASE is the base class of class cls/SUB<br>
	 *            TupleBinding\<(base of cls) or (cls)\>
	 */
	public static <BASE, SUB extends BASE> void addNonPrimitiveAllowingSubclass( final Class<SUB> cls,
																					final TupleBinding<BASE> binding ) {
		final int formerSize = nonPrimitives.size();
		final TupleBinding<?> previousBinding = nonPrimitives.put( cls, binding );
		assert null == previousBinding : "a value for class `" + cls + "`already existed value==`" + previousBinding + "`";
		assert ( nonPrimitives.size() == ( formerSize + 1 ) );
	}
	
	
	// /**
	// * * NOTE: that using a subclass of class is not supposed to work because that binding for the base class doesn't know
	// about
	// * fields of the subclass to serialize them into the database<br>
	// * thus each class must have its own Binding, or at least if you're using subclasses they must have their own binding<br>
	// * WARNING: that note is likely NOT valid anymore! due to some thinking, it seems you can override a base
	// TupleBinding<BASE> to
	// consider that SUB's fields in computation
	// so you could use the same TupleBinding<BASE> class to subclass it and use an instance of that for the SUB class
	// * @param cls
	// * @param binding
	// */
	// public static <T> void addNonPrimitiveRestrictedToSameClass( final Class<T> cls, final TupleBinding<T> binding ) {
	// addNonPrimitiveRestrictedToSameClass( cls, binding );
	// }
	
	
	/**
	 * @param cls
	 * @return the binding specific for this class
	 */
	@SuppressWarnings( {
		"rawtypes",// eclipse bug (more or less)
		"unchecked"
	} )
	public static <T> TupleBinding<T> getBinding( final Class<T> cls ) {
		TupleBinding t = nonPrimitives.get( cls );// first
		if ( null == t ) {
			t = TupleBinding.getPrimitiveBinding( cls );// second
			if ( null == t ) {
				throw Q.bug( "TupleBinding not yet defined for class '" + cls.getSimpleName()
					+ "' you may want to add it using addNonPrimitive()!" );
			}
		}
		// assert t instanceof TupleBinding<T>;
		return t;
	}
}
