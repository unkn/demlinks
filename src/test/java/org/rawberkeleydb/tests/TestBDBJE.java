/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 6, 2011 10:25:22 AM
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

import java.io.*;
import java.util.concurrent.*;

import org.bdb.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.je.*;



/**
 *
 */
public class TestBDBJE {
	
	private static final boolean	ENABLE_TRANSACTIONS				= true;
	private static final long		BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	private static final String		secPrefix						= "secondary";
	private static final String		dbName							= "theDBFileName";
	// hash dbtype fails for 1000
	private static final int		HOWMANY							= 800;
	public static final LockMode	LOCK							= ENABLE_TRANSACTIONS ? LockMode.RMW : LockMode.DEFAULT;
	public static final Durability	DUR								= Durability.COMMIT_NO_SYNC;
	
	private Environment				env;
	private EnvironmentConfig		envConf;
	private File					storeDir;
	private SecondaryConfig			secAndPriConf;
	private Database				priDb;
	private SecondaryDatabase		secDb;
	private int						leftOverForAdd100				= 0;
	
	
	@Before
	public void setUp() {
		storeDir = new File( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR );
		F.delFileOrTree( storeDir );
		storeDir.mkdirs();
		envConf = new EnvironmentConfig();
		
	}
	
	
	private void setupBDBNativeDb() {
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( false );// not needed tho, only populated if sec is empty but pri isn't
		System.out.println( "Database type: BTREE" );
		// secAndPriConf.setChecksum( true );// this has virtually no impact
		// // secConf.setEncrypted( password )
		// secAndPriConf.setMultiversion( false );
		// secAndPriConf.setReverseSplitOff( false );
		// secAndPriConf.setTransactionNotDurable( false );
		// secAndPriConf.setUnsortedDuplicates( false );
		// secAndPriConf.setDeferredWrite( false );
		// secAndPriConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secAndPriConf.setExclusiveCreate( false );
		secAndPriConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secAndPriConf.setReadOnly( false );
		secAndPriConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secAndPriConf.setTransactional( ENABLE_TRANSACTIONS );
		
		assert !secAndPriConf.getSortedDuplicates();
		// assert !secAndPriConf.getUnsortedDuplicates();
		// assert !secAndPriConf.getReverseSplitOff();
		secAndPriConf.setKeyCreator( new SecondaryKeyCreator() {
			
			@Override
			public boolean createSecondaryKey( final SecondaryDatabase secondary, final DatabaseEntry key,
												final DatabaseEntry data, final DatabaseEntry result ) {
				
				// if ( data.getData().length != data.getSize() ) {
				// XXX: this happens with bdb native (ie. .dll version) but not with bdb je aka java edition version
				// Q.warn( "len=" + data.getData().length + " size=" + data.getSize() + " data=" + data );
				// }
				// assert data.getData().length == data.getSize() : "len=" + data.getData().length + " size=" + data.getSize()
				// + " data=" + data;
				// XXX: looks like length and size can differ ie. 8 vs 100, maybe latter is with padding
				result.setData( data.getData() );
				result.setSize( data.getSize() );// this seems useless but let above assert check that for us
				
				// System.out.println( key + "!" + data + "!" + result );
				return true;
			}
		} );
		
		
		
		// pri db
		try {
			priDb = env.openDatabase(
			// BETransaction.getCurrentTransaction( _env ),
				null,
				dbName,
				secAndPriConf/*
							 * using the same conf from secondary, but it will be treated as just a simple DatabaseConfig
							 * instead
							 */
			);
			secDb = env.openSecondaryDatabase( null,
			// BETransaction.getCurrentTransaction( _env ),
				secPrefix + dbName,
				priDb,
				secAndPriConf );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	private void setupBDBNativeEnv() throws DatabaseException {
		envConf.setAllowCreate( true );
		
		// envConf.setInitializeCDB( true );//this1of2
		// envConf.setCDBLockAllDatabases( true );//this2of2 are unique and go together
		
		envConf.setLocking( ENABLE_TRANSACTIONS );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds, TimeUnit.MICROSECONDS );
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		envConf.setDurability( DUR );
		// envConf.setTxnNoSync( true );// XXX: should be false for consistency
		// envConf.setTxnWriteNoSync( true );// can't use both
		// envConf.setTxnNotDurable( true );
		// envConf.setTxnNoWait( true );
		// envConf.setTxnSnapshot( ENABLE_TRANSACTIONS );
		envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds, TimeUnit.MICROSECONDS );
		//
		// // envConf.setLogDirectory( logDirectory )
		// envConf.setMaxLogFileSize( Integer.MAX_VALUE );//this allocated 2gig log
		// envConf.setMaxLogFileSize( 10 * 1024 * 1024 );// 10meg alloc
		
		// envConf.setInitializeRegions( false );// XXX: maybe experiment with this, unsure
		// envConf.setInitializeReplication( false );// for now
		// envConf.setInitialMutexes( 100 );// must investigate 10 is not enough!
		
		// envConf.setJoinEnvironment( false );
		//
		//
		// envConf.setPrivate( BerkEnv.once );// this fails true as long as we have that single-open constraint
		//
		//
		// envConf.setUseEnvironment( false );
		// envConf.setUseEnvironmentRoot( false );
		
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		timed.start();
		env = new Environment( storeDir, envConf );
		timed.stop();
		System.out.println( "environment open took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	@After
	public void tearDown() throws DatabaseException {
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		timed.start();
		if ( null != secDb ) {
			secDb.close();
		}
		if ( null != priDb ) {
			priDb.close();
		}
		
		if ( null != env ) {
			env.close();
		}
		timed.stop();
		System.out.println( "tearDown took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	private void add100( final boolean firstTime, final boolean cont ) throws DatabaseException {
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		final TransactionConfig txnConfig = new TransactionConfig();
		txnConfig.setNoWait( true );
		// txnConfig.setSnapshot( true );
		// txnConfig.set
		
		timed.start();
		Transaction t = null;
		if ( ENABLE_TRANSACTIONS ) {
			t = env.beginTransaction( null, txnConfig );
		}
		try {
			int initial = 0;
			if ( cont ) {
				initial = leftOverForAdd100;
			}
			int i;
			final int final_ = ( initial + HOWMANY );
			System.out.print( "adding from [" + initial + " to " + final_ + ") " );
			for ( i = initial; i < final_; i++ ) {
				final String key = "" + i;
				final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				dataBinding.objectToEntry( data, deData );
				OperationStatus ret = null;
				try {
					ret = priDb.putNoOverwrite( t, deKey, deData );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ( ( firstTime || cont ) && ( ret == OperationStatus.SUCCESS ) )
					|| ( ( !firstTime ) && ( ret == OperationStatus.KEYEXIST ) );
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			leftOverForAdd100 = i;
			// System.out.println( "leftover=" + leftOverForAdd100 );
			
			// System.out.println( "committing" );
			if ( null != t ) {
				t.commit();
			}
		} catch ( final Throwable t2 ) {
			if ( null != t ) {
				t.abort();
			}
			Q.rethrow( t2 );
		}
		timed.stop();
		System.out.println( "add100 executed in: " + timed.getDeltaPrintFriendly() );
	}
	
	
	private void check100() throws DatabaseException {
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		final TransactionConfig txnConfig = new TransactionConfig();
		txnConfig.setNoWait( true );
		txnConfig.setSnapshot( true );
		// txnConfig.set
		
		timed.start();
		Transaction t = null;
		if ( ENABLE_TRANSACTIONS ) {
			t = env.beginTransaction( null, txnConfig );
		}
		try {
			final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			
			System.out.print( "checking from 0 to " + leftOverForAdd100 + " " );
			for ( int i = 0; i < leftOverForAdd100; i++ ) {
				final String key = "" + i;
				final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				keyBinding.objectToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				dataBinding.objectToEntry( data, deData );
				OperationStatus ret = null;
				try {
					ret = priDb.get( t, deKey, deData, LOCK );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ret == OperationStatus.SUCCESS;
				final Long data2 = dataBinding.entryToObject( deData );
				assert data.equals( data2 );
				assert data != data2;
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			
			// System.out.println( "committing" );
			if ( null != t ) {
				t.commit();
			}
		} catch ( final Throwable t2 ) {
			if ( null != t ) {
				t.abort();
			}
			Q.rethrow( t2 );
		}
		timed.stop();
		System.out.println( "check100 executed in: " + timed.getDeltaPrintFriendly() );
	}
	
	
	@Test
	public void testBTree() throws DatabaseException, FileNotFoundException {
		// only BTree exists in BDB JE
		setupBDBNativeEnv();
		setupBDBNativeDb();
		part2();
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void part2() throws DatabaseException {
		leftOverForAdd100 = 0;
		final Timer t1 = new Timer( Timer.TYPE.MILLIS );
		t1.start();
		add100( true, true );
		add100( false, false );
		add100( false, false );
		add100( false, true );
		add100( false, true );
		add100( false, true );
		check100();
		t1.stop();
		System.out.println( "all above adds/check (aka part2) executed in " + t1.getDeltaPrintFriendly() );
	}
	
	
}
