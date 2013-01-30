/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Jan 18, 2013 4:54:44 AM
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
package org.temporary.tests.forjava;

import clojure.lang.*;



/**
 *
 */
public class ClojureTest
{
	
	final static Namespace	NS_clojure_core	= RT.CLOJURE_NS;
	final static String		clojure_core	= NS_clojure_core.getName().getName();
	final static boolean	replaceRoot		= true;
	
	
	public static void main( final String[] args ) {
		// final Symbol nssym_wor = Symbol.intern( clojure_core, "*warn-on-reflection*" );
		// final Var wor = Var.find( sym_wor );
		// Var.pushThreadBindings( RT.mapUniqueKeys( wor, RT.T ) );
		final Symbol sym_wor = Symbol.intern( "*warn-on-reflection*" );
		Var.intern( NS_clojure_core, sym_wor, RT.T, replaceRoot );
		
		final Symbol sym_ourNS = Symbol.intern( "nsrandom.test" );
		// final Namespace ourNS = Namespace.findOrCreate( sym_ourNS );
		
		final Var in_ns = RT.var( "clojure.core", "in-ns" );
		final Var refer = RT.var( "clojure.core", "refer" );
		// in_ns.invoke( sym_ourNS );
		// refer.invoke( NS_clojure_core.getName() );
		// RT.var( RT.CLOJURE_NS.toString(), "something" );
		
		final Var prn = Var.find( Symbol.intern( clojure_core, "println" ) );
		final Symbol cver = Symbol.intern( clojure_core, "*clojure-version*" );
		final Var v = Var.find( cver );
		prn.invoke( "the value:", v.deref(), "\nthe var:", v, "\nthe symbol:", cver );// thanks bbloom for deref()
		// prn.invoke( "current namespace:", Var.find( Symbol.intern( NS_clojure_core.getMapping( "*ns*") ) ).deref() );
		prn.invoke( "current namespace:", RT.CURRENT_NS.deref() );
		// output:
		// the value: {:major 1, :minor 5, :incremental 0, :qualifier RC2}
		// the var: #'clojure.core/*clojure-version*
		// the symbol: clojure.core/*clojure-version*
	}
}
