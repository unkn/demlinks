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
package org.bdb;



import static org.junit.Assert.*;

import org.dml.storage.commons.*;
import org.junit.*;



public class DBSetTest
{
	
	private BDBSetOfNodes	o2m	= null;
	// the following two should be random unique names not already in the dbase
	// or else the tests may fail, that's ok the database is cleared before and after the tests!
	private NodeGeneric		_a;
	private NodeGeneric		_b;
	private NodeGeneric		_c;
	private StorageGeneric	env	= null;
	
	
	@Before
	public void setUp() {
		
		env = new StorageBDBNative( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, true );
		assert null != env;
		
		o2m = new BDBSetOfNodes( env, "one to many dbmap" );
		
		_a = env.createNewUniqueNode();
		_b = env.createNewUniqueNode();
		_c = env.createNewUniqueNode();
	}
	
	
	@After
	public void tearDown() {
		if ( null != o2m ) {
			o2m.close();
		}
		
		if ( null != env ) {
			env.shutdown( true );
		}
	}
	
	
	@Test
	public void testNoTransaction() {
		System.out.println( _a );
		System.out.println( _b );
		System.out.println( _c );
		System.out.println( o2m.getDBName() );
		// a->b a->c
		// b->a
		// c->b
		assertTrue( 0 == o2m.countInitials( _a ) );
		
		assertFalse( o2m.isVector( _a, _b ) );
		assertFalse( o2m.ensureVector( _a, _b ) );
		assertTrue( o2m.isVector( _a, _b ) );
		
		assertTrue( o2m.ensureVector( _a, _b ) );
		
		
		assertFalse( o2m.isVector( _b, _a ) );
		assertFalse( o2m.ensureVector( _b, _a ) );
		assertTrue( o2m.isVector( _b, _a ) );
		
		assertTrue( o2m.ensureVector( _b, _a ) );
		
		
		assertFalse( o2m.isVector( _a, _c ) );
		assertFalse( o2m.ensureVector( _a, _c ) );
		assertTrue( o2m.isVector( _a, _c ) );
		
		assertTrue( o2m.ensureVector( _a, _c ) );
		
		assertFalse( o2m.isVector( _c, _b ) );
		assertFalse( o2m.ensureVector( _c, _b ) );
		assertTrue( o2m.isVector( _c, _b ) );
		
		assertTrue( o2m.ensureVector( _c, _b ) );
		
		IteratorGeneric_OnChildNodes iter = o2m.getIterator_on_Initials_of( _c );
		assertNotNull( iter );
		try {
			NodeGeneric now = iter.goFirst();
			assert null != now;
			do {
				System.out.println( now + " -> _c" );
				assertTrue( now.equals( _a ) );
				assertTrue( now != _a );
				now = iter.goNext();
			} while ( null != now );
			iter.success();
		} finally {
			try {
				iter.finished();
			} finally {
				iter = null;
			}
		}
		
		iter = o2m.getIterator_on_Children_of( _c );
		try {
			NodeGeneric now = iter.goFirst();
			assert null != now;
			do {
				System.out.println( "_c -> " + now );
				assertTrue( now.equals( _b ) );
				assertTrue( now != _b );
				now = iter.goNext();
			} while ( null != now );
			
			iter.success();
		} finally {
			try {
				iter.finished();
			} finally {
				iter = null;
			}
		}
		
		System.out.println( "count=" + o2m.countInitials( _a ) );
		
		assertTrue( 1 == o2m.countInitials( _a ) );
		assertTrue( 2 == o2m.countChildren( _a ) );
		
	}
	
	
	@Test
	public void testCyclical() {
		assertFalse( o2m.isVector( _a, _a ) );
		assertFalse( o2m.ensureVector( _a, _a ) );
		assertTrue( o2m.isVector( _a, _a ) );
		assertTrue( o2m.ensureVector( _a, _a ) );
		
	}
	
	
}
