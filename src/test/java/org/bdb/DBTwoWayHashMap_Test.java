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

import org.JUnitCommons.*;
import org.dml.storage.berkeleydb.generics.*;
import org.dml.storage.commons.*;
import org.junit.*;



public class DBTwoWayHashMap_Test
		extends JUnitHooker
{
	
	private BDBTwoWayHashMap_StringName2Node	x	= null;
	private final String						_a	= "A";
	private final NodeBDB						_b	= NodeBDB.getBDBNodeInstance( 2l );
	private StorageBDBGeneric					env	= null;
	
	
	@Before
	public void setUp() {
		env = GlobalBDB.factory.getNewStorage( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, true );
		x = new BDBTwoWayHashMap_StringName2Node( env, "some 1-to-1 dbMap", StorageBDBGeneric.LOCK );
	}
	
	
	@After
	public void tearDown() {
		if ( null != x ) {
			x.discard();
		}
		
		if ( null != env ) {
			env.shutdown( true );
		}
	}
	
	
	@Test
	public void linkTest() {
		final TransactionGeneric txn = env.beginTransaction();
		try {
			assertFalse( x.ensureExists( _a, _b ) );
			assertTrue( x.getName( _b ).equals( _a ) );
			assertTrue( x.getNode( _a ).equals( _b ) );
			// different objects, same content
			assertTrue( x.getName( _b ) != x.getName( _b ) );
			
			assertTrue( _a != x.getName( _b ) );
			assertTrue( _b != x.getNode( _a ) );
			assertTrue( _b.equals( x.getNode( x.getName( _b ) ) ) );
			assertTrue( x.ensureExists( _a, _b ) );
			txn.success();
		} finally {
			txn.finished();
		}
	}
	
	
	@Test
	public void testMany() {
		final TransactionGeneric txn = env.beginTransaction();
		try {
			final GenericBDBTwoWayMapOfNNU<Long, String> y =
				new GenericBDBTwoWayMapOfNNU<Long, String>(
					env,
					"some 1-to-1 dbMap",
					Long.class,
					String.class,
					StorageBDBGeneric.LOCK );
			org.references.TestTwoWayHashMapOfNonNullUniques.testMany( y );
			txn.success();
		} finally {
			txn.finished();
		}
	}
	
}
