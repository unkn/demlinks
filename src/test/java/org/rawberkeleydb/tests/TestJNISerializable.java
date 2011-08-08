/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 6, 2011 5:34:02 PM
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
package org.rawberkeleydb.tests;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;
import org.q.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 * in conclusion, each txn sees data that is committed by other transactions, aka as db revisions advance
 * the current open transaction can see the data from the last revision, rather than the data from the same revision of the db
 * that was existing at the time the txn was created
 */
public class TestJNISerializable {
	
	// won't work for 100 or less, due to locks being granted on a per page(ie. 512bytes?) basis; ie. dbpage
	private static final int				HOWMANY	= 1000;
	private final TestBDBNativeAKAviaJNI	x		= new TestBDBNativeAKAviaJNI();
	
	
	@Before
	public void setUp() {
		x.setUp();
	}
	
	
	@After
	public void tearDown() throws DatabaseException {
		x.tearDown();
	}
	
	final DatabaseEntry	deKey	= new DatabaseEntry();
	final DatabaseEntry	deData	= new DatabaseEntry();
	private Transaction	parent;
	
	
	@Test
	public void test1AsSiblingsOfParentTxn() throws DatabaseException, FileNotFoundException {
		x.setupBDBNativeEnv();
		x.setupBDBNativeDb( DatabaseType.BTREE );
		parent = x.beginTxn( null );
		try {
			whole();
			x.commit( parent );
		} catch ( final Throwable t ) {
			x.abort( parent );
			Q.rethrow( t );
		}
	}
	
	
	@Test
	public void test2WithoutExplicitParentTxn() throws DatabaseException, FileNotFoundException {
		x.setupBDBNativeEnv();
		x.setupBDBNativeDb( DatabaseType.BTREE );
		parent = null;
		whole();
	}
	
	
	
	private void whole() throws DatabaseException {
		assertTrue( x.MVC );
		assertTrue( x.ENABLE_LOCKING );
		assertTrue( x.ENABLE_TRANSACTIONS );
		Transaction t = x.beginTxn( parent );
		int numAdded = 0;
		final int firstItem = 0;
		try {
			for ( numAdded = firstItem; numAdded < HOWMANY; numAdded++ ) {
				LongBinding.longToEntry( numAdded, deKey );
				LongBinding.longToEntry( numAdded, deData );
				OperationStatus ret = null;
				try {
					ret = x.priDb.putNoOverwrite( t, deKey, deData );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ret == OperationStatus.SUCCESS;
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			x.commit( t );
		} catch ( final Throwable t2 ) {
			x.abort( t );
			Q.rethrow( t2 );
		}
		
		t = x.beginTxn( parent );
		try {
			
			final DatabaseEntry firstKey = new DatabaseEntry();
			final DatabaseEntry firstData = new DatabaseEntry();
			LongBinding.longToEntry( firstItem, firstKey );
			final OperationStatus ret = x.priDb.get( t, firstKey, firstData, LockMode.RMW );
			assert ret == OperationStatus.SUCCESS;
			assert LongBinding.entryToLong( firstData ) == firstItem;
			
			final long last = numAdded - 1;
			parallelTxnChangesLastItemAndCommits( last );
			
			
			final DatabaseEntry lastKey = new DatabaseEntry();
			final DatabaseEntry lastData = new DatabaseEntry();
			LongBinding.longToEntry( last, lastKey );
			final OperationStatus ret3 = x.priDb.get( t, lastKey, lastData, LockMode.RMW );
			// XXX: bdb bug? if LockMode.RMW here, it will kill the lock:
			// BDB0068 DB_LOCK_DEADLOCK: Locker killed to resolve a deadlock: BDB0068 DB_LOCK_DEADLOCK: Locker killed to
			// resolve
			// a deadlock
			// but this works with: LockMode.DEFAULT
			// all this only when txndur is set aka DURABLE_TXNS set to true
			
			assert ret == OperationStatus.SUCCESS;
			try {
				if ( LongBinding.entryToLong( lastData ) != last ) {
					Q.bug( "as expected" );
				}
				Q.fail();// not expected
			} catch ( final BugError ae ) {
				// good as expected, this means:
				// the current txn "t" doesn't snapshot see the db
			}
			
			x.commit( t );
		} catch ( final Throwable t2 ) {
			x.abort( t );
			Q.rethrow( t2 );
		}
	}
	
	
	private void parallelTxnChangesLastItemAndCommits( final long last ) throws DatabaseException {
		final Transaction parallelTxn = x.beginTxn( parent );
		try {
			final DatabaseEntry lastKey = new DatabaseEntry();
			LongBinding.longToEntry( last, lastKey );
			DatabaseEntry lastData = new DatabaseEntry();
			OperationStatus ret2 = x.priDb.get( parallelTxn, lastKey, lastData, LockMode.RMW );
			assert ret2 == OperationStatus.SUCCESS;
			ret2 = null;
			assert LongBinding.entryToLong( lastData ) == last;
			
			final long newValue = HOWMANY + 1;
			LongBinding.longToEntry( newValue, lastData );
			lastData.setReadOnly( true );
			OperationStatus ret4 = x.priDb.put( parallelTxn, lastKey, lastData );
			assert ret4 == OperationStatus.SUCCESS;
			ret4 = null;
			
			lastData = new DatabaseEntry();
			ret2 = x.priDb.get( parallelTxn, lastKey, lastData, LockMode.RMW );
			assert ret2 == OperationStatus.SUCCESS;
			assert LongBinding.entryToLong( lastData ) == newValue;
			
			x.commit( parallelTxn );
		} catch ( final Throwable t2 ) {
			x.abort( parallelTxn );
			Q.rethrow( t2 );
		}
	}
}
