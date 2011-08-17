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
package org.storage;



import static org.junit.Assert.*;

import java.util.*;

import org.dml.storage.Level2.*;
import org.dml.storage.berkeleydb.generics.*;
import org.dml.storage.commons.*;
import org.junit.*;
import org.q.*;
import org.toolza.Timer;



public class TestSetOfNodes
		extends BaseTest_for_Storage
{
	
	
	/**
	 * @param type1
	 * @param subType1
	 */
	public TestSetOfNodes( final StorageType type1, final BDBStorageSubType subType1 ) {
		super( type1, subType1 );
	}
	
	
	private L0Set_OfChildren	set1;
	private NodeGeneric			setInitial;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.storage.TestBase_for_Storage#overridden_setUp()
	 */
	@Override
	public void overridden_setUp() {
		// setStore();
		setInitial = storage.createNewUniqueNode();
		set1 = new L0Set_OfChildren( storage, setInitial );
	}
	
	
	// public abstract void setStore();
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.storage.TestBase_for_Storage#overridden_tearDown()
	 */
	@Override
	public void overridden_tearDown() {
		// empty
	}
	
	
	@Test
	public void testWithout() {
		final Timer t = new Timer( Timer.TYPE.MILLIS );
		t.start();
		assertTrue( set1.size() == 0 );
		
		final NodeGeneric one = storage.createNewUniqueNode();
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
		final NodeGeneric two = storage.createNewUniqueNode();
		assertNotNull( two );
		assertTrue( two != one );
		assertFalse( set1.ensureIsAddedToSet( two ) );
		assertTrue( set1.size() == ( startSize + 1 ) );
		assertTrue( storage.isVector( set1.getSelf(), two ) );
		
		final NodeGeneric three = storage.createNewUniqueNode();
		assertNotNull( three );
		assertFalse( set1.ensureIsAddedToSet( three ) );
		assertTrue( storage.isVector( set1.getSelf(), three ) );
		assertTrue( set1.size() == ( startSize + 2 ) );
		
		final IteratorGeneric_OnChildNodes iter = set1.getIterator();
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
		
		
		
		NodeGeneric now = iter.goFirst();
		while ( now != null ) {
			now = iter.goNext();
		}
		assertTrue( storage.isVector( setInitial, two ) );
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
		assertFalse( storage.isVector( setInitial, two ) );
		assertTrue( iter.size() == 2 );
		assertTrue( set1.size() == 2 );
		// assertTrue( this.set1.remove( two ) );
		final IteratorGeneric_OnChildNodes iter2 = iter;// this.set1.getIterator( );
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
		final NodeGeneric now2 = iter2.goTo( three );
		assertTrue( now != now2 );// diff instances, always returning new BerkNodeAdapter()
		assertTrue( now.equals( now2 ) );
		
		assertTrue( iter2.goPrev().equals( one ) );
		iter2.goTo( one );
		now = iter2.goNext();
		assertTrue( now.equals( three ) );
		
		assertTrue( iter2.size() == 2 );
		assertTrue( now.equals( three ) );
		assertTrue( storage.isVector( set1.getSelf(), three ) );
		assertTrue( iter2.size() == 2 );
		iter2.delete();// delete after count() !
		assertFalse( storage.isVector( set1.getSelf(), three ) );
		assertTrue( iter2.size() == 1 );
		
		iter2.success();
		iter2.finished();
		assertFalse( storage.isVector( set1.getSelf(), three ) );
		
		final NodeGeneric dsotInitial = storage.createNewUniqueNode();
		final L0DomainSet_OfChildren dsot = new L0DomainSet_OfChildren( storage, dsotInitial, setInitial );
		assertTrue( set1.getSelf().equals( setInitial ) );
		try {
			final L0DomainSet_OfChildren adsot = new L0DomainSet_OfChildren( storage, set1.getSelf(), setInitial );
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		final L0Set_OfChildren sot3 = new L0Set_OfChildren( storage, dsotInitial );
		assertTrue( sot3.getSelf().equals( dsot.getSelf() ) );
		// assertFalse( sot3.equals( dsot ) );
		// already used testEquals for this
		// assertFalse( dsot.equals( sot3 ) );
		
		final L0DomainSet_OfChildren dsot3 = new L0DomainSet_OfChildren( storage, dsot.getSelf(), dsot.getDomain() );
		final L0Set_OfChildren dsot4 = new L0DomainSet_OfChildren( storage, dsot.getSelf(), dsot.getDomain() );
		assertTrue( dsot != dsot4 );
		assertTrue( dsot3 != dsot4 );
		assertTrue( dsot3 != dsot );
		assertTrue( dsot.equals( dsot3 ) );
		assertTrue( dsot.equals( dsot4 ) );
		assertTrue( dsot4.equals( dsot ) );
		final L0DomainSet_OfChildren dsot5 =
			new L0DomainSet_OfChildren( storage, dsot.getSelf(), storage.createNewUniqueNode() );
		try {
			// same set but different domains?, which reminds me
			// XXX:we should not be able to `new` same set with two different domains; thing is we only detect this here when
			// equals happens between them; this is why we should keep constraints metadata in the database as opposed to fields
			dsot.equals( dsot5 );
			Q.fail();
		} catch ( final BadCallError bce ) {
			// good
			Q.markAsHandled( bce );
		}
		
		dsot.hashCode();
		final HashSet<L0Set_OfChildren> hm = new HashSet<L0Set_OfChildren>();
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
		assertFalse( storage.isVector( set1.getSelf(), three ) );
		try {
			dsot.ensureIsAddedToSet( three );// it's not in domain
			Q.fail();// asserts disabled?
		} catch ( final AssertionError ae ) {
			// right
		}
		assertFalse( dsot.isValidChild( three ) );
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
		final NodeGeneric l1 = storage.createNewUniqueNode();
		assertNotNull( l1 );
		final L0Set_OfChildren sos = new L0Set_OfChildren( storage, set1.getSelf() );
		assertFalse( set1.ensureIsAddedToSet( l1 ) );
		assertTrue( set1.size() == 1 );
		assertTrue( sos.size() == 1 );
		final L0Set_OfChildren sos2 = new L0Set_OfChildren( storage, set1.getSelf() );
		assertTrue( sos2.size() == 1 );
		
		assertTrue( sos.equals( sos2 ) );
		
		assertTrue( sos.equals( set1 ) );
		
		assertTrue( set1.equals( sos ) );
		
		assertTrue( sos != set1 );
		assertTrue( sos2 != set1 );
		assertTrue( sos != sos2 );
		
		final NodeGeneric domain = storage.createNewUniqueNode();
		L0Set_OfChildren dsos;
		// doesn't check integrity, thus not throws here:
		dsos = new L0DomainSet_OfChildren( storage, sos2.getSelf(), domain );
		
		assertFalse( storage.ensureVector( domain, l1 ) );
		dsos = new L0DomainSet_OfChildren( storage, sos2.getSelf(), domain );
		assertTrue( dsos.getSelf().equals( sos2.getSelf() ) );
		assertTrue( dsos.size() == 1 );
	}
	
	
	@Test
	public void testEquals() {
		final NodeGeneric domain = storage.createNewUniqueNode();
		final NodeGeneric self1 = storage.createNewUniqueNode();
		final L0DomainSet_OfChildren dsos = new L0DomainSet_OfChildren( storage, self1, domain );
		final L0Set_OfChildren sos = new L0Set_OfChildren( storage, self1 );
		
		assertFalse( domain.equals( dsos ) );
		assertFalse( dsos.equals( domain ) );
		
		assertFalse( sos.equals( domain ) );
		assertFalse( domain.equals( sos ) );
		
		assertTrue( sos.equals( self1 ) );
		assertTrue( self1.equals( sos ) );
		
		assertTrue( dsos.equals( self1 ) );
		assertTrue( self1.equals( dsos ) );
		
		// two diff class types using the same self, is detected when equals is performed:
		try {
			dsos.equals( sos );
			Q.fail();
		} catch ( final BadCallError bce ) {
			Q.markAsHandled( bce );
		}
		
		try {
			assertFalse( sos.equals( dsos ) );
			Q.fail();
		} catch ( final BadCallError bce ) {
			Q.markAsHandled( bce );
		}
		
		// ==============
		// now comparing two diff class types with diff self, will return fail w/o complaining
		// only if same self it would complain, because can use only one self for one type of class ie. Pointer and Set can't be
		// using same self; because their constraints would clash
		final NodeGeneric n2 = storage.createNewUniqueNode();
		final L0DomainSet_OfChildren x = new L0DomainSet_OfChildren( storage, n2, domain );
		assertFalse( x.equals( set1 ) );
		assertFalse( set1.equals( x ) );
		// =========== now comparing same class type with same self it's ok, two instances can use same self as long as they're
		// of the same type ie. both Set or both Pointer; as opposed to one Set and one Pointer
		final L0DomainSet_OfChildren x2 = new L0DomainSet_OfChildren( storage, n2, domain );
		assertTrue( x2.equals( x ) );
		assertTrue( x.equals( x2 ) );
	}
	
	
	@Test
	public void testPointer() {
		final NodeGeneric ptrInitial = storage.createNewUniqueNode();
		assertNotNull( ptrInitial );
		final L0Pointer_ToChild ptr = new L0Pointer_ToChild( storage, ptrInitial );
		assertFalse( storage.isVector( ptr, ptrInitial ) );
		assertTrue( ptr.getSelf() != ptrInitial );
		System.out.println( ptr.getSelfImpl().getClass() );
		System.out.println( ptr.getSelf().getClass() );
		System.out.println( ptrInitial.getClass() );
		final boolean ret = ptr.getSelf().equals( ptrInitial );
		assertTrue( ret );
		assertNull( ptr.getPointeeChild() );
		
		final NodeGeneric newL = storage.createNewUniqueNode();
		assertNotNull( newL );
		assertFalse( storage.ensureVector( ptrInitial, newL ) );
		
		// this isn't valid because it's using the same self as ptr
		final L0Pointer_ToChild ptr2 = new L0Pointer_ToChild( storage, ptrInitial );
		assertNotNull( ptr2.getPointeeChild() );
		assertTrue( ptr2.getPointeeChild().equals( newL ) );
		assertTrue( ptr2.getPointeeChild() != newL );
		assertTrue( ptr.getPointeeChild().equals( ptr2.getPointeeChild() ) );
		assertTrue( ptr.getPointeeChild() != ptr2.getPointeeChild() );
		assertTrue( ptr != ptr2 );
		assertTrue( ptr.equals( ptr2 ) );
		
		// this is not valid, since it uses same self as ptr2
		final L0DomainPointer_ToChild dptr = new L0DomainPointer_ToChild( storage, ptr2.getSelf(), set1.getSelf() );
		
		try {
			dptr.getPointeeChild();// detects that pointee is not in domain!
			Q.fail();
		} catch ( final AssertionError ae ) {// FIXME: AssertionError is way too generic
			// it detected!
			// Q.markAsHandled( ae ); oh i see here, since it's an assert thrown, is not added in tree
		}
		
		// XXX: yes comparing different types but HashMap does this all the time so we're allowing it w/o throws
		// XXX: but in this case, we throw because! they are using the same self, which will induce a bug later
		try {
			dptr.equals( ptr );
			Q.fail();
		} catch ( final BadCallError bce ) {
			Q.markAsHandled( bce );
		}
		try {
			ptr.equals( dptr );
			Q.fail();
		} catch ( final BadCallError bce ) {
			Q.markAsHandled( bce );
		}
		
		dptr.hashCode();
		final HashSet<L0Pointer_ToChild> hm = new HashSet<L0Pointer_ToChild>();
		assertTrue( hm.add( dptr ) );
		assertTrue( dptr.getSelf().equals( ptr.getSelf() ) );
		
		assert dptr.getSelf().equals( ptr.getSelf() );
		assertTrue( hm.add( ptr ) );
		
		assertTrue( hm.size() == 2 );
		assertTrue( hm.contains( dptr ) );
		assertTrue( hm.contains( ptr ) );
		// assertFalse( hm.add( ptr2 ) );
		// // assertTrue( hm.contains( ptr ) );
		// assertTrue( hm.contains( ptr2 ) );
		// assertTrue( ptr2 != ptr );
		// assertTrue( ptr2.equals( ptr ) );
		// assertTrue( hm.size() == 2 );
		
		assertFalse( set1.ensureIsAddedToSet( newL ) );
		
		assertTrue( dptr.getPointeeChild().equals( newL ) );
		
		final L0NonNullDomainPointer_ToChild nndptr =
			new L0NonNullDomainPointer_ToChild( storage, dptr.getSelf(), dptr.getDomain() );
		
		assertTrue( newL.equals( nndptr.getPointeeChild() ) );
		assertTrue( newL != nndptr.getPointeeChild() );
		assertTrue( dptr.getDomain().equals( nndptr.getDomain() ) );
		assertTrue( dptr.getDomain() != nndptr.getDomain() );
		
		dptr.setPointee( null );
		assertNull( dptr.getPointeeChild() );
		
		try {
			System.out.println( nndptr.getPointeeChild() );
			Q.fail();
		} catch ( final AssertionError ae ) {
			// good
		}
		
		final L0NonNullDomainPointer_ToChild nndptr2 =
			new L0NonNullDomainPointer_ToChild( storage, dptr.getSelf(), dptr.getDomain() );
		try {
			nndptr2.getPointeeChild();
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
		assertTrue( newL.equals( nndptr.getPointeeChild() ) );
		
	}
}
