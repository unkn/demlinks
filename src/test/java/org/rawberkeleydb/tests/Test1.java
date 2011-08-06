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
import org.bdbLevel1.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 *
 */
public class Test1 {
	
	private static final boolean	ENABLE_TRANSACTIONS				= true;
	private static final long		BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	private static final String		secPrefix						= "secondary";
	private static final String		dbName							= "avalidfilename";
	private Environment				env;
	private EnvironmentConfig		envConf;
	private File					storeDir;
	private SecondaryConfig			secAndPriConf;
	private Database				priDb;
	private SecondaryDatabase		secDb;
	
	
	@Before
	public void setUp() throws FileNotFoundException, DatabaseException {
		storeDir = new File( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR );
		storeDir.mkdirs();
		envConf = new EnvironmentConfig();
		
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
		
		envConf.setInitializeLocking( ENABLE_TRANSACTIONS );
		envConf.setLockDetectMode( LockDetectMode.YOUNGEST );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		// final int x = 100;
		// envConf.setMaxLockers( x );
		// envConf.setMaxLockObjects( x );
		// envConf.setMaxLocks( x );
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		// // envConf.setDurability( DUR );
		// envConf.setTxnNoSync( true );// XXX: should be false for consistency
		envConf.setTxnWriteNoSync( true );// can't use both
		envConf.setTxnNotDurable( true );
		envConf.setTxnNoWait( true );
		envConf.setTxnSnapshot( ENABLE_TRANSACTIONS );
		envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
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
		envConf.setRunRecovery( true );
		envConf.setRegister( true );
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
		// envConf.setSystemMemory( true );// XXX: experiment, this fails
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
		
		env = new Environment( storeDir, envConf );
		
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( true );// not needed tho, only populated if sec is empty but pri isn't
		secAndPriConf.setType( DatabaseType.HASH );// XXX: check if BTREE is better? ie. via some benchmark sometime in the
													// future
		secAndPriConf.setChecksum( true );
		// secConf.setEncrypted( password )
		secAndPriConf.setMultiversion( false );
		secAndPriConf.setReverseSplitOff( false );
		secAndPriConf.setTransactionNotDurable( false );
		secAndPriConf.setUnsortedDuplicates( false );
		// secAndPriConf.setDeferredWrite( false );
		// secAndPriConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secAndPriConf.setExclusiveCreate( false );
		secAndPriConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secAndPriConf.setReadOnly( false );
		secAndPriConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secAndPriConf.setTransactional( BDBEnvironment.ENABLE_TRANSACTIONS );
		
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
	
	
	@After
	public void tearDown() throws DatabaseException {
		if ( null != secDb ) {
			secDb.close();
		}
		if ( null != priDb ) {
			priDb.close();
		}
		
		if ( null != env ) {
			env.close();
		}
		F.delFileOrTree( storeDir );
	}
	
	
	@Test
	public void test1() {
		final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
		final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
		
		final int n = 100;// 100 takes 0.8->1 seconds
		for ( int i = 0; i < n; i++ ) {
			final String key = "" + i;
			final Long data = new Long( i );
			final DatabaseEntry deKey = new DatabaseEntry();
			keyBinding.objectToEntry( key, deKey );
			final DatabaseEntry deData = new DatabaseEntry();
			dataBinding.objectToEntry( data, deData );
			OperationStatus ret = null;
			try {
				ret = priDb.putNoOverwrite( null, deKey, deData );
			} catch ( final DatabaseException e ) {
				Q.rethrow( e );
			}
			// System.out.println( ret );
			assert ret == OperationStatus.SUCCESS;
			// ret.equals( OperationStatus.KEYEXIST )
		}// for
	}
}
