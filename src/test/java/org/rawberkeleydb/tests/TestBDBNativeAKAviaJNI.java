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

import org.bdb.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 *
 */
public class TestBDBNativeAKAviaJNI {
	
	// set all these 4 to true for consistency, but also lack of speed; all to false for max speed
	private static final boolean	ENABLE_TRANSACTIONS				= false;
	@SuppressWarnings( "unused" )
	private static final boolean	DURABLE_TXNS					= false ? ENABLE_TRANSACTIONS : false;
	private static final boolean	ENABLE_LOCKING					= false;
	@SuppressWarnings( "unused" )
	// only enabled when transactions are enabled, and if that first bool is true
	private static final boolean	MVC								= false ? ENABLE_TRANSACTIONS : false;
	
	
	// hash dbtype fails for 1000; 800 works though
	// if ie. 1800 then on hash, this err: BDB0689 theDBFileName page 10 is on free list with type 13;
	// if all those 4 above are set to false; that error doesn't happen
	private static final int		HOWMANY							= 1800;
	
	private static final long		BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	private static final String		secPrefix						= "secondary";
	private static final String		dbName							= "theDBFileName";
	
	@SuppressWarnings( "unused" )
	public static final LockMode	LOCKMODE						= ENABLE_TRANSACTIONS && ENABLE_LOCKING ? LockMode.RMW
																		: LockMode.DEFAULT;
	
	
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
	
	
	private void setupBDBNativeDb( final DatabaseType dbtype ) {
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( false );// not needed tho, only populated if sec is empty but pri isn't
		secAndPriConf.setType( dbtype );
		System.out.println( "Database type: " + dbtype );
		secAndPriConf.setChecksum( false );// this has virtually no impact
		// secConf.setEncrypted( password )
		secAndPriConf.setMultiversion( MVC );
		secAndPriConf.setReverseSplitOff( false );
		secAndPriConf.setTransactionNotDurable( DURABLE_TXNS );// XXX: normally false
		secAndPriConf.setUnsortedDuplicates( false );
		// secAndPriConf.setDeferredWrite( false );
		// secAndPriConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secAndPriConf.setExclusiveCreate( false );
		secAndPriConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secAndPriConf.setReadOnly( false );
		secAndPriConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secAndPriConf.setTransactional( ENABLE_TRANSACTIONS );
		
		assert !secAndPriConf.getSortedDuplicates();
		assert !secAndPriConf.getUnsortedDuplicates();
		assert !secAndPriConf.getReverseSplitOff();
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
				null,
				secAndPriConf/*
							 * using the same conf from secondary, but it will be treated as just a simple DatabaseConfig
							 * instead
							 */
			);
			secDb = env.openSecondaryDatabase( null,
			// BETransaction.getCurrentTransaction( _env ),
				secPrefix + dbName,
				null,
				priDb,
				secAndPriConf );
		} catch ( final FileNotFoundException e ) {
			throw Q.rethrow( e );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	private void setupBDBNativeEnv() throws FileNotFoundException, DatabaseException {
		envConf.setAllowCreate( true );
		envConf.setLockDown( false );
		envConf.setDirectDatabaseIO( true );// XXX: experiment with this!
		envConf.setDirectLogIO( true );// XXX: and this
		//
		// // envConf.setEncrypted( password )
		envConf.setOverwrite( false );
		
		envConf.setErrorStream( System.err );
		envConf.setErrorPrefix( "junitBDBJNI:" );
		
		// useless:
		envConf.setEventHandler( new EventHandlerAdapter() {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.sleepycat.db.EventHandlerAdapter#handlePanicEvent()
			 */
			@Override
			public void handlePanicEvent() {
				System.err.println( "panic event" );
			}
		} );
		
		envConf.setFeedbackHandler( new FeedbackHandler() {
			
			@Override
			public void recoveryFeedback( final Environment environment, final int percent ) {
				System.err.println( "recoveryFeedback" );
			}
			
			
			@Override
			public void upgradeFeedback( final Database database, final int percent ) {
				System.err.println( "upgradeFeedback" );
			}
			
			
			@Override
			public void verifyFeedback( final Database database, final int percent ) {
				System.err.println( "verifyFeedback" );
			}
		} );
		
		envConf.setHotbackupInProgress( false );
		envConf.setInitializeCache( true );// XXX: experiment with this
		// envConf.setInitializeCDB( true );//this1of2
		// envConf.setCDBLockAllDatabases( true );//this2of2 are unique and go together
		
		if ( ENABLE_LOCKING ) {
			envConf.setInitializeLocking( ENABLE_LOCKING );
		} else {
			envConf.setInitializeLocking( ENABLE_TRANSACTIONS );
		}
		envConf.setLockDetectMode( LockDetectMode.YOUNGEST );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		// final int x = 100;
		// envConf.setMaxLockers( x );
		// envConf.setMaxLockObjects( x );
		// envConf.setMaxLocks( x );
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		if ( ENABLE_TRANSACTIONS ) {
			// // envConf.setDurability( DUR );
			envConf.setTxnNoSync( true );// XXX: should be false for consistency
			// envConf.setTxnWriteNoSync( true );// can't use both
			envConf.setTxnNotDurable( true );
			envConf.setTxnNoWait( true );
			envConf.setTxnSnapshot( MVC );
			envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		}
		
		//
		envConf.setInitializeLogging( true );// XXX: set to true tho
		envConf.setLogInMemory( false );
		envConf.setLogAutoRemove( false );// set to true for smaller sized dbs
		envConf.setLogBufferSize( 0 );// XXX: uses defaults; can experiment with this
		envConf.setLogZero( false );// XXX:wow this totally writes 2gig of zeroes when true
		// // envConf.setLogDirectory( logDirectory )
		// envConf.setMaxLogFileSize( Integer.MAX_VALUE );//this allocated 2gig log
		// envConf.setMaxLogFileSize( 10 * 1024 * 1024 );// 10meg alloc
		envConf.setRunFatalRecovery( false );
		
		// envConf.setInitializeRegions( false );// XXX: maybe experiment with this, unsure
		// envConf.setInitializeReplication( false );// for now
		// envConf.setInitialMutexes( 100 );// must investigate 10 is not enough!
		
		// envConf.setJoinEnvironment( false );
		//
		envConf.setRunRecovery( ENABLE_TRANSACTIONS );
		envConf.setRegister( ENABLE_TRANSACTIONS );
		//
		envConf.setMessageStream( System.err );
		envConf.setMultiversion( true );// oh yeah xD
		//
		envConf.setNoLocking( false );
		envConf.setNoPanic( false );
		envConf.setNoMMap( false );
		//
		// envConf.setPrivate( BerkEnv.once );// this fails true as long as we have that single-open constraint
		//
		envConf.setReplicationInMemory( false );
		//
		envConf.setSystemMemory( true );// XXX: experiment, this fails
		//
		// // envConf.setTemporaryDirectory( temporaryDirectory )
		envConf.setThreaded( true );
		//
		// envConf.setUseEnvironment( false );
		// envConf.setUseEnvironmentRoot( false );
		
		envConf.setYieldCPU( false );// XXX: experiment with this
		// envConf.setVerbose( VerboseConfig.FILEOPS, true );
		envConf.setVerbose( VerboseConfig.DEADLOCK, true );
		envConf.setVerbose( VerboseConfig.RECOVERY, true );
		envConf.setVerbose( VerboseConfig.REGISTER, true );
		envConf.setVerbose( VerboseConfig.REPLICATION, true );
		
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
		beginTxn();
		addCheckTimer.start();
		try {
			// final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			// final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			int initial = 0;
			if ( cont ) {
				initial = leftOverForAdd100;
			}
			int i;
			final int final_ = ( initial + HOWMANY );
			System.out.print( "adding from [" + initial + " to " + final_ + ") " );
			for ( i = initial; i < final_; i++ ) {
				final String key = "" + i;
				// final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				LongBinding.longToEntry( i, deData );
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
			commit();
		} catch ( final Throwable t2 ) {
			abort();
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "add100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	private Transaction	t;
	
	
	private void beginTxn() throws DatabaseException {
		t = null;
		if ( ENABLE_TRANSACTIONS ) {
			final TransactionConfig txnConfig = new TransactionConfig();
			txnConfig.setNoWait( true );
			txnConfig.setSnapshot( MVC );
			// txnConfig.setSync( true );
			// txnConfig.set
			t = env.beginTransaction( null, txnConfig );
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void abort() throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.abort();
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void commit() throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.commit();
		}
	}
	
	final Timer	addCheckTimer	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void check100() throws DatabaseException {
		beginTxn();
		addCheckTimer.start();
		try {
			// final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			// final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			
			System.out.print( "checking from 0 to " + leftOverForAdd100 + " " );
			for ( int i = 0; i < leftOverForAdd100; i++ ) {
				final String key = "" + i;
				// final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				LongBinding.longToEntry( i, deData );
				OperationStatus ret = null;
				try {
					ret = priDb.get( t, deKey, deData, LOCKMODE );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ret == OperationStatus.SUCCESS;
				assert i == LongBinding.entryToLong( deData );
				// assert i != data2;
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			
			// System.out.println( "committing" );
			commit();
		} catch ( final Throwable t2 ) {
			abort();
			
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "check100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	
	@Test
	public void testBTree() throws DatabaseException, FileNotFoundException {
		setupBDBNativeEnv();
		setupBDBNativeDb( DatabaseType.BTREE );
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
	
	
	@Test
	public void testHash() throws DatabaseException, FileNotFoundException {
		setupBDBNativeEnv();
		setupBDBNativeDb( DatabaseType.HASH );
		part2();
	}
}
