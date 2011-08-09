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

import org.dml.storage.berkeleydb.commons.*;
import org.dml.storage.berkeleydb.native_via_jni.*;
import org.junit.*;
import org.q.*;



public class TestBDBEnvironment
{
	
	@Test
	public void testUNG() {
		StorageBDBNative env = null;
		try {
			env = new StorageBDBNative( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, true );
			
			final String sameName = "some name";
			final int delta = +1;
			final long max = 10;
			final long min = -201;
			final long initialValue = -6;
			final BDB_Named_UniqueNumberGenerator ung =
				new BDB_Named_UniqueNumberGenerator( env, sameName, min, initialValue, max, false );
			long l1 = ung.getNextUniqueLong( delta );
			
			assertTrue( initialValue == l1 );
			final BDB_Named_UniqueNumberGenerator ung2 =
				new BDB_Named_UniqueNumberGenerator( env, sameName, min, initialValue, max, false );
			final long l2 = ung2.getNextUniqueLong( delta );
			// System.out.println( ung + " / " + ung2 );
			assertTrue( ung2 != ung );// they are same internally in BDB though.
			// assertTrue( ung2.equals( ung ) );
			
			try {
				ung2.equals( ung );// not implemented
				fail( "should've thrown" );// FIXME: i forgot why this is bad; and throw null; is better
			} catch ( final BadCallError bce ) {
				// this is right
			}
			
			final long expected = ( initialValue + ( 1 * delta ) );
			assertTrue( "got:" + l2 + " instead of " + expected, expected == l2 );
			assertTrue( l1 != l2 );
			assertTrue( ( l1 + delta ) == l2 );
			l1 = ung.getNextUniqueLong( delta );
			assertTrue( l1 == ( initialValue + ( 2 * delta ) ) );
			
			l1 = ung.getNextUniqueLong( delta );
			assertTrue( l1 == ( initialValue + ( 3 * delta ) ) );
			
			for ( int i = 0; i < ( ( max - 3 ) - initialValue ); i++ ) {
				l1 = ung.getNextUniqueLong( delta );
				// System.out.println( "l1==" + l1 );
				assertTrue( l1 == ( ( ( 4 + i ) * delta ) + initialValue ) );
			}
			
			try {
				l1 = ung.getNextUniqueLong( delta );
				Q.fail();
			} catch ( final Throwable soe ) {
				// right
				if ( !Q.isBareException( soe, SequenceOverflow.class ) ) {
					Q.rethrow( soe );
				}
			}
			
		} finally {
			if ( null != env ) {
				env.shutdown();
			}
		}
		StorageBDBNative env3 = null;
		StorageBDBNative env5 = null;
		try {
			env3 = new StorageBDBNative( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, false );
			try {
				env5 = new StorageBDBNative( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, false );
				Q.fail();
			} catch ( final Throwable t ) {
				if ( Q.isBareException( t, BadCallError.class ) ) {
				} else {
					Q.rethrow( t );
				}
			}
			
		} finally {
			if ( null != env3 ) {
				env3.shutdown( true );
			}
		}
		assertTrue( null == env5 );
	}
	
	
}
