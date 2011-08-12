/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 4, 2011 5:47:53 PM
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
package org.references;

import static org.junit.Assert.*;

import org.JUnitCommons.*;
import org.dml.storage.berkeleydb.generics.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;



/**
 *
 */
public class TestTwoWayHashMapOfNonNullUniques
		extends JUnitHooker
{
	
	private class KeyA
	{
		
		private final int	field;
		
		
		public KeyA( final int i ) {
			field = i;
		}
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( final Object obj ) {
			Q.nn( obj );// not allowing comparison to null; early bug catching i guess
			if ( Z.equalsByReference_allowsNull( this, obj ) ) {
				return true;
			}
			assert Z.areSameClass_canNotBeNull( this, obj );
			final KeyA o = (KeyA)obj;
			return field == o.field;
		}
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return field;
		}
	}
	
	
	@Test
	public void testDiffInstancesWhichEquals_AndSomeOfZ() {
		// using two different instances which are equal via .equals()
		
		final KeyA key1 = new KeyA( 10 );
		final KeyA key2 = new KeyA( 10 );
		assertTrue( key1 != key2 );
		assertTrue( key1.equals( key2 ) );
		assertTrue( Z.equalsWithCompatClasses_allowsNull( key1, key2 ) );
		assertFalse( Z.equalsByReference_enforceNotNull( key1, key2 ) );
		assertTrue( Z.areSameClass_canNotBeNull( key1, key2 ) );
		
		final RAMTwoWayHashMapOfNonNullUniques<KeyA, String> hm = new RAMTwoWayHashMapOfNonNullUniques<KeyA, String>();
		final String one = "one";
		final String two = "two";
		assertTrue( hm.isEmpty() );
		assertTrue( hm.size() == 0 );
		
		assertFalse( hm.ensureExists( key1, one ) );
		assertTrue( hm.size() == 1 );
		
		assertTrue( hm.ensureExists( key1, one ) );
		assertTrue( hm.size() == 1 );
		
		try {
			hm.ensureExists( key2, two );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
			Q.markAsHandled( bce );
		}
		assertTrue( hm.size() == 1 );
		
		assertTrue( Z.equalsByReference_enforceNotNull( hm.getData( key1 ), one ) );
		assertTrue( Z.equalsByReference_enforceNotNull( hm.getData( key2 ), one ) );
		
		assertTrue( hm.removeByKey( key2 ) );
		assertTrue( hm.isEmpty() );
		
		assertTrue( Z.equalsWithCompatClasses_allowsNull( null, null ) );
	}
	
	
	@Test
	public void testMany() {
		final RAMTwoWayHashMapOfNonNullUniques<Long, String> hm = new RAMTwoWayHashMapOfNonNullUniques<Long, String>();
		testMany( hm );
	}
	
	
	public static void testMany( final GenericTwoWayMapOfUniques<Long, String> hm ) {
		// doing it: we likely want to make this test for any instance not just RAM hash map...
		final long n = 1000;
		
		assertTrue( hm.isEmpty() );
		for ( long i = 0; i < n; i++ ) {
			final boolean ret = hm.ensureExists( new Long( i ), String.valueOf( i ) );
			assertFalse( ret );
		}
		final long s = hm.size();
		assertTrue( "" + s, s == n );
		assertTrue( "" + s, s == hm.size() );// on second call
		for ( long i = 0; i < n; i++ ) {
			final boolean ret = hm.ensureExists( new Long( i ), String.valueOf( i ) );
			assertTrue( ret );
		}
		assertTrue( hm.size() == n );
		assertFalse( hm.isEmpty() );
		
		if ( !GlobalBDB.isJE ) {// didn't implement this for JE yet
			hm.removeAll();
			assertTrue( hm.isEmpty() );
		}
		
		hm.discard();
		
		try {
			hm.discard();
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
			Q.markAsHandled( bce );
		}
		
		try {
			hm.getData( new Long( 1 ) );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
			Q.markAsHandled( bce );
		}
	}
}
