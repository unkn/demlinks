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

import java.util.*;

import org.bdbLevel1.*;
import org.bdbLevel2.*;
import org.junit.*;
import org.q.*;
import org.toolza.Timer;



public class TestSetOfNodes {
	
	private BDBEnvironment		env;
	private L0Set_OfTerminals	set1;
	private BDBNode				setInitial;
	
	
	@Before
	public void setUp() {
		final Timer t = new Timer( Timer.TYPE.MILLIS );
		t.start();
		env = new BDBEnvironment( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, true );
		setInitial = env.createNewUniqueNode();
		set1 = new L0Set_OfTerminals( env, setInitial );
		t.stop();
		System.out.println( "setUp: " + t.getDeltaPrintFriendly() );
	}
	
	
	@After
	public void tearDown() {
		final Timer t = new Timer( Timer.TYPE.MILLIS );
		t.start();
		if ( null != env ) {
			env.shutdown( true );
		}
		t.stop();
		System.out.println( "tearDown: " + t.getDeltaPrintFriendly() );
	}
	
	
	@Test
	public void testWithout() {
		final Timer t = new Timer( Timer.TYPE.MILLIS );
		t.start();
		assertTrue( set1.size() == 0 );
		
		final BDBNode one = env.createNewUniqueNode();
		assertNotNull( one );
		assertFalse( set1.ensureIsAddedToSet( one ) );
		assertTrue( set1.size() == 1 );
		assertTrue( set1.ensureIsAddedToSet( one ) );
		
		assertTrue( setInitial.equals( set1.getSelf() ) );
		assertTrue( setInitial != set1.getSelf() );// they diff due to clone
		
		assertFalse( set1.contains( setInitial ) );// didn't already exit
		final boolean ret = set1.ensureIsAddedToSet( setInitial );// add itself to set ie. circular IS allowed
		assertFalse( ret );// didn't already exit
		assertTrue( set1.contains( setInitial ) );
		assertTrue( set1.remove( setInitial ) );
		assertFalse( set1.contains( setInitial ) );
		
		final int startSize = set1.size();
		final BDBNode two = env.createNewUniqueNode();
		assertNotNull( two );
		assertTrue( two != one );
		assertFalse( set1.ensureIsAddedToSet( two ) );
		assertTrue( set1.size() == ( startSize + 1 ) );
		assertTrue( env.isVector( set1.getSelf(), two ) );
		
		final BDBNode three = env.createNewUniqueNode();
		assertNotNull( three );
		assertFalse( set1.ensureIsAddedToSet( three ) );
		assertTrue( env.isVector( set1.getSelf(), three ) );
		assertTrue( set1.size() == ( startSize + 2 ) );
		
		final IteratorOnTerminalNodes_InDualPriDBs iter = set1.getIterator();
		assertTrue( iter.goFirst().equals( one ) );
		
		assertNull( iter.goPrev() );// even tho errored here,
		// cursor pos is unchanged
		
		iter.goTo( one );
		assertTrue( two.equals( iter.goNext() ) );
		
		iter.goTo( two );
		assertTrue( three.equals( iter.goNext() ) );
		
		assertNull( iter.goNext() );
		
		iter.goTo( two );
		assertTrue( one.equals( iter.goPrev() ) );
		
		iter.goTo( three );
		assertTrue( two.equals( iter.goPrev() ) );
		
		
		
		BDBNode now = iter.goFirst();
		while ( now != null ) {
			now = iter.goNext();
		}
		assertTrue( env.isVector( setInitial, two ) );
		// InitialInitialInitial: remove while iter is still active will fail, due to lock!
		// threw = false;
		// try {
		set1.remove( two );
		// } catch ( final Throwable t ) {
		// if ( Q.isBareException( t, LockNotAvailableException.class ) ) {
		// threw = true;
		// } else {
		// Q.rethrow( t );
		// }
		// }
		// assertTrue( threw );
		// iter.goTo( two );
		// iter.delete();
		// iter.close();
		assertFalse( env.isVector( setInitial, two ) );
		assertTrue( iter.size() == 2 );
		assertTrue( set1.size() == 2 );
		// assertTrue( this.set1.remove( two ) );
		final IteratorOnTerminalNodes_InDualPriDBs iter2 = iter;// this.set1.getIterator( );
		// iter2.goFirst();
		// while ( iter2.now() != null ) {
		// System.out.println( "iter2=" + iter2.now() );
		// iter2.goNext();
		// }
		
		assertNull( iter2.goTo( two ) );
		iter2.goTo( one );
		assertNull( iter2.goPrev() );
		now = iter2.goTo( three );
		assertNull( iter2.goNext() );
		final BDBNode now2 = iter2.goTo( three );
		assertTrue( now != now2 );// diff instances, always returning new BerkNodeAdapter()
		assertTrue( now.equals( now2 ) );
		
		assertTrue( iter2.goPrev().equals( one ) );
		iter2.goTo( one );
		now = iter2.goNext();
		assertTrue( now.equals( three ) );
		
		assertTrue( iter2.size() == 2 );
		assertTrue( now.equals( three ) );
		assertTrue( env.isVector( set1.getSelf(), three ) );
		assertTrue( iter2.size() == 2 );
		iter2.delete();// delete after count() !
		assertFalse( env.isVector( set1.getSelf(), three ) );
		assertTrue( iter2.size() == 1 );
		
		
		iter2.finished( true );
		assertFalse( env.isVector( set1.getSelf(), three ) );
		
		final BDBNode dsotInitial = env.createNewUniqueNode();
		final L0DomainSet_OfTerminals dsot = new L0DomainSet_OfTerminals( env, dsotInitial, setInitial );
		assertTrue( set1.getSelf().equals( setInitial ) );
		try {
			final L0DomainSet_OfTerminals adsot = new L0DomainSet_OfTerminals( env, set1.getSelf(), setInitial );
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		final L0Set_OfTerminals sot3 = new L0Set_OfTerminals( env, dsotInitial );
		assertTrue( sot3.getSelf().equals( dsot.getSelf() ) );
		try {
			sot3.equals( dsot );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
		}
		
		try {
			dsot.equals( sot3 );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
		}
		
		final L0DomainSet_OfTerminals dsot3 = new L0DomainSet_OfTerminals( env, dsot.getSelf(), dsot.getDomain() );
		final L0Set_OfTerminals dsot4 = new L0DomainSet_OfTerminals( env, dsot.getSelf(), dsot.getDomain() );
		assertTrue( dsot != dsot4 );
		assertTrue( dsot3 != dsot4 );
		assertTrue( dsot3 != dsot );
		assertTrue( dsot.equals( dsot3 ) );
		assertTrue( dsot.equals( dsot4 ) );
		assertTrue( dsot4.equals( dsot ) );
		final L0DomainSet_OfTerminals dsot5 = new L0DomainSet_OfTerminals( env, dsot.getSelf(), env.createNewUniqueNode() );
		try {
			// same set but different domains?, which reminds me
			// XXX:we should not be able to `new` same set with two different domains; thing is we only detect this here when
			// equals happens between them; this is why we should keep constraints metadata in the database as opposed to fields
			dsot.equals( dsot5 );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
		}
		
		dsot.hashCode();
		final HashSet<L0Set_OfTerminals> hm = new HashSet<L0Set_OfTerminals>();
		assertTrue( hm.add( dsot ) );
		assertTrue( hm.add( set1 ) );
		assertTrue( hm.size() == 2 );
		assertFalse( hm.add( dsot ) );
		assertFalse( hm.add( set1 ) );
		
		assertTrue( hm.size() == 2 );
		assertTrue( hm.contains( dsot ) );
		assertFalse( hm.add( dsot3 ) );
		assertTrue( hm.contains( dsot ) );
		assertTrue( hm.contains( dsot3 ) );
		assertTrue( dsot3 != dsot );
		assertTrue( dsot3.equals( dsot ) );
		assertTrue( hm.size() == 2 );
		
		assertTrue( dsot.getSelf().equals( dsotInitial ) );
		assertTrue( dsot.getSelf() != dsotInitial );// diff due to cloned
		assertTrue( dsot.getDomain() != setInitial );// diff due to cloned
		assertTrue( dsot.getDomain().equals( setInitial ) );
		
		assertFalse( dsot.ensureIsAddedToSet( one ) );
		assertTrue( dsot.contains( one ) );
		
		assertFalse( dsot.contains( three ) );
		assertFalse( set1.contains( three ) );
		assertFalse( env.isVector( set1.getSelf(), three ) );
		try {
			dsot.ensureIsAddedToSet( three );// it's not in domain
			Q.fail();// asserts disabled?
		} catch ( final AssertionError ae ) {
			// right
		}
		assertFalse( dsot.isValidTerminal( three ) );
		assertFalse( dsot.contains( three ) );
		
		try {
			dsot.ensureIsAddedToSet( two );// it's not in domain either
			Q.fail();
		} catch ( final AssertionError ae ) {
			// right
		}
		assertFalse( dsot.contains( two ) );
		
		assertFalse( set1.ensureIsAddedToSet( two ) );
		
		assertFalse( dsot.ensureIsAddedToSet( two ) );
		assertTrue( dsot.contains( two ) );
		
		// can add self, but now self is NOT in domain! so NO
		try {
			dsot.ensureIsAddedToSet( dsot.getSelf() );
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		assertFalse( dsot.contains( dsot.getSelf() ) );
		
		assertFalse( set1.ensureIsAddedToSet( dsotInitial ) );
		
		// can add self, if self is in domain! YES now
		assertFalse( dsot.ensureIsAddedToSet( dsot.getSelf() ) );
		assertTrue( dsot.contains( dsot.getSelf() ) );
		
		try {
			dsot.ensureIsAddedToSet( dsot.getDomain() );// cannot add domain to set
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		assertFalse( dsot.contains( dsot.getDomain() ) );
		
		assertTrue( set1.size() == 3 );
		set1.clearAll();
		assertTrue( set1.size() == 0 );
		assertTrue( set1.isEmpty() );
		
		set1.clearAll();
		assertTrue( set1.size() == 0 );
		assertTrue( set1.isEmpty() );
		t.stop();
		System.out.println( t.getDeltaPrintFriendly() );
	}
	
	
	@Test
	public void testAlreadyExisting() {
		final BDBNode l1 = env.createNewUniqueNode();
		assertNotNull( l1 );
		final L0Set_OfTerminals sos = new L0Set_OfTerminals( env, set1.getSelf() );
		assertFalse( set1.ensureIsAddedToSet( l1 ) );
		assertTrue( set1.size() == 1 );
		assertTrue( sos.size() == 1 );
		final L0Set_OfTerminals sos2 = new L0Set_OfTerminals( env, set1.getSelf() );
		assertTrue( sos2.size() == 1 );
		
		assertTrue( sos.equals( sos2 ) );
		
		assertTrue( sos.equals( set1 ) );
		
		assertTrue( set1.equals( sos ) );
		
		assertTrue( sos != set1 );
		assertTrue( sos2 != set1 );
		assertTrue( sos != sos2 );
		
		final BDBNode domain = env.createNewUniqueNode();
		L0Set_OfTerminals dsos;
		// doesn't check integrity, thus not throws here:
		dsos = new L0DomainSet_OfTerminals( env, sos2.getSelf(), domain );
		
		assertFalse( env.ensureVector( domain, l1 ) );
		dsos = new L0DomainSet_OfTerminals( env, sos2.getSelf(), domain );
		assertTrue( dsos.getSelf().equals( sos2.getSelf() ) );
		assertTrue( dsos.size() == 1 );
		
		try {
			// can't compare a super with the subclass
			dsos.equals( set1 );// actually calls DomainSet_OfTerminals.equals
			Q.fail();
		} catch ( final BadCallError ae ) {
			// the way
		}
	}
	
	
	@Test
	public void testPointer() {
		final BDBNode ptrInitial = env.createNewUniqueNode();
		assertNotNull( ptrInitial );
		final L0Pointer_ToTerminal ptr = new L0Pointer_ToTerminal( env, ptrInitial );
		assertTrue( ptr.getSelf().equals( ptrInitial ) );
		assertTrue( ptr.getSelf() != ptrInitial );
		assertNull( ptr.getPointeeTerminal() );
		
		final BDBNode newL = env.createNewUniqueNode();
		assertNotNull( newL );
		assertFalse( env.ensureVector( ptrInitial, newL ) );
		
		// this isn't valid because it's using the same self as ptr
		final L0Pointer_ToTerminal ptr2 = new L0Pointer_ToTerminal( env, ptrInitial );
		assertNotNull( ptr2.getPointeeTerminal() );
		assertTrue( ptr2.getPointeeTerminal().equals( newL ) );
		assertTrue( ptr2.getPointeeTerminal() != newL );
		assertTrue( ptr.getPointeeTerminal().equals( ptr2.getPointeeTerminal() ) );
		assertTrue( ptr.getPointeeTerminal() != ptr2.getPointeeTerminal() );
		assertTrue( ptr != ptr2 );
		assertTrue( ptr.equals( ptr2 ) );
		
		// this is not valid, since it uses same self as ptr2
		final L0DomainPointer_ToTerminal dptr = new L0DomainPointer_ToTerminal( env, ptr2.getSelf(), set1.getSelf() );
		
		try {
			dptr.getPointeeTerminal();// detects that pointee is not in domain!
			Q.fail();
		} catch ( final AssertionError ae ) {
			// it detected!
		}
		
		try {
			dptr.equals( ptr );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
		}
		
		try {
			ptr.equals( dptr );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
		}
		
		dptr.hashCode();
		final HashSet<L0Pointer_ToTerminal> hm = new HashSet<L0Pointer_ToTerminal>();
		assertTrue( hm.add( dptr ) );
		assertTrue( dptr.getSelf().equals( ptr.getSelf() ) );
		
		assert dptr.getSelf().equals( ptr.getSelf() );
		try {
			hm.add( ptr );
			Q.fail();// bad
		} catch ( final BadCallError bce ) {
			// good
		}
		
		assertTrue( hm.size() == 1 );
		assertTrue( hm.contains( dptr ) );
		try {
			hm.contains( ptr );
			Q.fail();// bad
		} catch ( final BadCallError bce ) {
			// good
		}
		// assertFalse( hm.add( ptr2 ) );
		// // assertTrue( hm.contains( ptr ) );
		// assertTrue( hm.contains( ptr2 ) );
		// assertTrue( ptr2 != ptr );
		// assertTrue( ptr2.equals( ptr ) );
		// assertTrue( hm.size() == 2 );
		
		assertFalse( set1.ensureIsAddedToSet( newL ) );
		
		assertTrue( dptr.getPointeeTerminal().equals( newL ) );
		
		final L0NonNullDomainPointer_ToTerminal nndptr =
			new L0NonNullDomainPointer_ToTerminal( env, dptr.getSelf(), dptr.getDomain() );
		
		assertTrue( newL.equals( nndptr.getPointeeTerminal() ) );
		assertTrue( newL != nndptr.getPointeeTerminal() );
		assertTrue( dptr.getDomain().equals( nndptr.getDomain() ) );
		assertTrue( dptr.getDomain() != nndptr.getDomain() );
		
		dptr.setPointee( null );
		assertNull( dptr.getPointeeTerminal() );
		
		try {
			nndptr.getPointeeTerminal();
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		
		final L0NonNullDomainPointer_ToTerminal nndptr2 =
			new L0NonNullDomainPointer_ToTerminal( env, dptr.getSelf(), dptr.getDomain() );
		try {
			nndptr2.getPointeeTerminal();
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		
		try {
			nndptr2.setPointee( null );
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		
		nndptr2.setPointee( newL );
		assertTrue( newL.equals( nndptr.getPointeeTerminal() ) );
		
	}
}
