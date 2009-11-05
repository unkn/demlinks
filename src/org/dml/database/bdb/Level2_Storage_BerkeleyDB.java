/**
 * File creation: May 31, 2009 7:46:58 PM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.dml.database.bdb;



import java.io.File;

import org.dml.tools.RunTime;
import org.javapart.logger.Log;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;



/**
 * 
 *
 */
public class Level2_Storage_BerkeleyDB {
	
	private String												envHomeDir;
	private final EnvironmentConfig								environmentConfig			= new EnvironmentConfig();
	private Environment											env							= null;
	private DBMapJIDsToNodeIDs									dbJID2NID					= null;
	private DBMapTupleNodeIDs									dbTupleNIDs					= null;
	
	// a database where all sequences will be stored:(only 1 db per bdb env)
	private Database											seqDb						= null;
	private final static String									seqDb_NAME					= "db5_AllSequences";
	private DatabaseConfig										seqDbConf					= null;
	
	// we keep track of open stuffs just in case we need to emergency shutdown
	// ie. on Exception
	private final ListOfUniqueNonNullObjects<Sequence>			allSequenceInstances		= new ListOfUniqueNonNullObjects<Sequence>();
	private final ListOfUniqueNonNullObjects<Database>			allOpenPrimaryDatabases		= new ListOfUniqueNonNullObjects<Database>();
	private final ListOfUniqueNonNullObjects<SecondaryDatabase>	allOpenSecondaryDatabases	= new ListOfUniqueNonNullObjects<SecondaryDatabase>();
	private final static String									dbTupleNIDs_NAME			= "tuple(NodeID<->NodeID)";
	private static final String									dbJID2NID_NAME				= "map(JID<->NodeID)";
	private final static String									UNINITIALIZED_STRING		= "uninitializedString";
	
	/**
	 * singleton
	 * 
	 * @return the database handling the one to one mapping between JIDs and
	 *         NodeIDs
	 * @throws DatabaseException
	 */
	public DBMapJIDsToNodeIDs getDBMapJIDsToNodeIDs() throws DatabaseException {

		if ( null == dbJID2NID ) {
			dbJID2NID = new DBMapJIDsToNodeIDs( this, dbJID2NID_NAME );
			RunTime.assertNotNull( dbJID2NID );
		}
		return dbJID2NID;
	}
	
	/**
	 * @return
	 */
	public DBMapTupleNodeIDs getDBMapTupleNodeIDs() {

		if ( null == dbTupleNIDs ) {
			dbTupleNIDs = new DBMapTupleNodeIDs( this, dbTupleNIDs_NAME );
			RunTime.assertNotNull( dbTupleNIDs );
		}
		return dbTupleNIDs;
	}
	
	public Level2_Storage_BerkeleyDB( String envHomeDir1 ) throws DatabaseException {

		this.init( envHomeDir1, false );
	}
	
	/**
	 * @param envHomeDir2
	 * @param internalDestroyBeforeInit
	 * @throws DatabaseException
	 */
	public Level2_Storage_BerkeleyDB( String envHomeDir1,
			boolean internalDestroyBeforeInit ) throws DatabaseException {

		this.init( envHomeDir1, internalDestroyBeforeInit );
	}
	
	/**
	 * intended to be used for JUnit testing when a clean start is required ie.
	 * no leftovers from previous JUnits or runs in the database<br>
	 * this should wipe all logs and locks of BDB Environment (which is
	 * supposedly everything incl. DBs)<br>
	 * <br>
	 * <code>envHomeDir</code> must be set before calling this
	 */
	private void internalWipeEnv() {

		File dir = new File( envHomeDir );
		String[] allThoseInDir = dir.list();
		if ( null != allThoseInDir ) {
			for ( String element : allThoseInDir ) {
				File n = new File( envHomeDir + File.separator + element );
				if ( !n.isFile() ) {
					continue;
				}
				if ( ( !n.getPath().matches( ".*\\.jdb" ) )
						&& ( !( n.getPath().matches( ".*\\.lck" ) ) ) ) {
					continue;
				}
				Log.special( "removing " + n.getPath() );
				if ( !n.delete() ) {
					Log.warn( "Failed removing " + n.getAbsolutePath() );
				}
			}
		}
	}
	
	/**
	 * call before all
	 * 
	 * @throws DatabaseException
	 */
	private final void init( String envHomeDir1,
			boolean internalDestroyBeforeInit ) throws DatabaseException {

		// maybe it would be needed to set the envhome dir
		RunTime.assertNotNull( envHomeDir1 );
		envHomeDir = envHomeDir1;
		if ( internalDestroyBeforeInit ) {
			this.internalWipeEnv();
		}
		// Environment init isn't needed, only deInit();
		this.getEnvironment();// forces env open or create
		// DBSequence init isn't needed, only deInit()
		
		// getDBMapJIDsToNodeIDs() is initing that when needed
		
		// db1=db1.init();
		
	}
	
	
	/**
	 *call when all done
	 */
	public final void deInit() {

		if ( null != dbJID2NID ) {
			dbJID2NID = dbJID2NID.deInit();
		}
		this.deInitSeqSystem();// first
		this.silentCloseAllOpenDatabases();// second
		this.closeDBEnvironment();
	}
	
	/**
	 * @return singleton of the BDB Environment
	 * @throws DatabaseException
	 */
	public final Environment getEnvironment() throws DatabaseException {

		if ( null == env ) {
			// make new now:
			this.firstTimeCreateEnvironment();
			RunTime.assertNotNull( env );
		}
		
		return env;
	}
	
	
	/**
	 * @param input
	 * @param output
	 */
	public final static void stringToEntry( String input, DatabaseEntry output ) {

		RunTime.assertNotNull( input, output );
		StringBinding.stringToEntry( input, output );
	}
	
	/**
	 * @param input
	 * @return
	 */
	public final static String entryToString( DatabaseEntry input ) {

		RunTime.assertNotNull( input );
		return StringBinding.entryToString( input );
	}
	
	

	/**
	 * @throws DatabaseException
	 * 
	 */
	private final void firstTimeCreateEnvironment() throws DatabaseException {

		environmentConfig.setAllowCreate( true );
		environmentConfig.setLocking( true );
		environmentConfig.setTransactional( true );
		environmentConfig.setTxnNoSync( false );
		environmentConfig.setTxnSerializableIsolation( true );
		environmentConfig.setTxnWriteNoSync( false );
		environmentConfig.setSharedCache( false );
		environmentConfig.setConfigParam( EnvironmentConfig.TRACE_LEVEL, "FINE" );
		environmentConfig.setConfigParam( EnvironmentConfig.TRACE_CONSOLE,
				"false" );
		environmentConfig.setConfigParam( EnvironmentConfig.TRACE_FILE, "true" );
		environmentConfig.setConfigParam( EnvironmentConfig.TRACE_DB, "false" );
		
		// perform other environment configurations
		File file = new File( envHomeDir );
		try {
			file.mkdirs();
			env = new Environment( file, environmentConfig );
		} catch ( DatabaseException de ) {
			Log.thro( "when creating BerkeleyDB Environment: "
					+ de.getMessage() );
			throw de;
		}
		
	}
	
	

	/**
	 * silently closing SecondaryDatabase
	 * no throws
	 * 
	 * @return null
	 * @param secDb
	 */
	public final SecondaryDatabase silentCloseAnySecDB( SecondaryDatabase secDb ) {

		Log.entry();
		if ( null != secDb ) {
			String secDbName = UNINITIALIZED_STRING;
			try {
				secDbName = secDb.getDatabaseName();
				secDb.close();
				Log.mid( "closed SecDB with name: " + secDbName );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing SecDB with specified name: '"
						+ secDbName );
				// ignore
			} finally {
				if ( !allOpenSecondaryDatabases.removeObject( secDb ) ) {
					RunTime.bug( "should've existed" );
				}
			}
		} else {
			Log.mid( "wasn't open SecDB" );
		}
		return null;
	}
	
	/**
	 * 
	 */
	private final void closeDBEnvironment() {

		if ( null != env ) {
			try {
				env.close();
				Log.exit( "BerkeleyDB env closed" );
			} catch ( DatabaseException de ) {
				Log.thro( "failed BerkeleyDB environment close:"
						+ de.getLocalizedMessage() );
				// ignore
			} finally {
				env = null;
			}
		} else {
			Log.mid( "BerkeleyDB env wasn't open" );
		}
	}
	
	
	/**
	 * new instance of Sequence, keeping track of it inside BerkeleyDB class
	 * just in case we need to shutdown all when Exception detected
	 * 
	 * @return never null
	 * @throws DatabaseException
	 */
	public Sequence getNewSequence( String thisSeqName,
			SequenceConfig allSequencesConfig ) throws DatabaseException {

		// if ( null == thisSeq ) {
		// init once:
		DatabaseEntry deKey = new DatabaseEntry();
		Level2_Storage_BerkeleyDB.stringToEntry( thisSeqName, deKey );
		Sequence seq = this.getSeqsDB().openSequence( null, deKey,
				allSequencesConfig );
		if ( allSequenceInstances.addFirstQ( seq ) ) {
			RunTime.bug( "couldn't have already existed!" );
		}
		RunTime.assertNotNull( seq );
		return seq;
		// RunTime.assertNotNull( thisSeq );
		// }
		// return thisSeq;
	}
	
	/**
	 * @return null
	 */
	public Sequence silentCloseAnySeq( Sequence thisSeq, String thisSeqName ) {

		Log.entry( "attempting to close sequence: " + thisSeqName );
		// System.err.println( allSequenceInstances.size() );
		if ( null != thisSeq ) {
			try {
				thisSeq.close();
				Log.exit( "closed seq with name: " + thisSeqName );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing seq with specified name: '"
						+ thisSeqName );
				// ignore
			} finally {
				if ( !allSequenceInstances.removeObject( thisSeq ) ) {
					RunTime.bug( "should've existed" );
				}
			}
		} else {
			Log.mid( "seq was already closed with name: " + thisSeqName );
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private final void silentCloseAllSequences() {

		Log.entry();
		Sequence iter;
		// int count = 0;
		while ( null != ( iter = allSequenceInstances.getObjectAt( Position.FIRST ) ) ) {
			// FIXME: the bad part is that whoever owns iter cannot set it to
			// NULL ie. inside a DBSequence instance, but I'm guessing that
			// there won't be any calls to DBSequence.done() before
			// closeEnvironment() finishes anyway; same goes for DatabaseCapsule
			// and SecondaryDatabaseCapsule
			this.silentCloseAnySeq( iter, "autoclosing..." );// we don't know
			// the name here
			if ( allSequenceInstances.removeObject( iter ) ) {
				RunTime.bug( "should've already been removed by above statement" );
			}
			// count++;
		}
		// System.out.println( count );
		RunTime.assertTrue( allSequenceInstances.isEmpty() );
		Log.exit();
	}
	
	/**
	 * closing all sequences first, then the BerkeleyDB holding them
	 */
	private void silentCloseAllSequencesAndTheirDB() {

		Log.entry();
		if ( !allSequenceInstances.isEmpty() ) {
			this.silentCloseAllSequences();
		}
		
		if ( !allSequenceInstances.isEmpty() ) {
			// BUG, avoiding throw because it's silent
			Log.bug( "should be empty now" );
		}
		
		if ( null != seqDb ) {
			seqDb = this.silentClosePriDB( seqDb );// , seqDb_NAME );
			// } else {
			// Log.warn( "close() called on a not yet inited/open database" );
		}
	}
	
	/**
	 * safely closes all active sequences and the database holding them<br>
	 * for the current environment only
	 */
	public final void deInitSeqSystem() {

		Log.entry();
		this.silentCloseAllSequencesAndTheirDB();
		
	}
	
	/**
	 * closing secondary then primary databases
	 */
	private void silentCloseAllOpenDatabases() {

		Log.entry();
		// close secondaries first!
		SecondaryDatabase iterSec;
		while ( null != ( iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST ) ) ) {
			this.silentCloseAnySecDB( iterSec );
			if ( allOpenSecondaryDatabases.removeObject( iterSec ) ) {
				RunTime.bug( "should've already been removed by above cmd" );
			}
			iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST );
		}
		
		// closing primaries:
		Database iter = allOpenPrimaryDatabases.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			this.silentClosePriDB( iter );
			if ( allOpenPrimaryDatabases.removeObject( iter ) ) {
				RunTime.bug( "should've already been removed by above cmd" );
			}
			iter = allOpenPrimaryDatabases.getObjectAt( Position.FIRST );
		}
	}
	
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	protected Database getSeqsDB() throws DatabaseException {

		if ( null == seqDb ) {
			// init first time:
			seqDb = this.openSeqDB();
			RunTime.assertNotNull( seqDb );
		}
		return seqDb;
	}
	
	/**
	 * one time open the database containing all stored sequences, and future
	 * ones
	 * 
	 * @param dbName
	 * @return
	 * @throws DatabaseException
	 */
	private final Database openSeqDB() throws DatabaseException {

		RunTime.assertNotNull( seqDb_NAME );
		RunTime.assertFalse( seqDb_NAME.isEmpty() );
		
		if ( null == seqDbConf ) {
			// init once:
			seqDbConf = new DatabaseConfig();
			seqDbConf.setAllowCreate( true );
			seqDbConf.setDeferredWrite( false );
			seqDbConf.setKeyPrefixing( true );
			seqDbConf.setSortedDuplicates( false );//
			seqDbConf.setTransactional( true );
		}
		
		return this.openAnyDatabase( seqDb_NAME, seqDbConf );
	}
	
	
	/**
	 * @param env2
	 * @param object
	 * @param dbName
	 * @param dbConf
	 * @return
	 * @throws DatabaseException
	 */
	public Database openAnyDatabase( String dbName, DatabaseConfig dbConf )
			throws DatabaseException {

		Log.entry( dbName );
		// should not use this openDatabase() method anywhere else
		Database db = this.getEnvironment().openDatabase( null, dbName, dbConf );
		if ( allOpenPrimaryDatabases.addFirstQ( db ) ) {
			RunTime.bug( "couldn't have already existed!" );
		}
		return db;
		// this should be the only method doing open on any database in this
		// environment
	}
	
	/**
	 * @param secDbName
	 * @param primaryDb
	 * @param secDbConf
	 * @return
	 * @throws DatabaseException
	 */
	public SecondaryDatabase openAnySecDatabase( String secDbName,
			Database primaryDb, SecondaryConfig secDbConf )
			throws DatabaseException {

		Log.entry( secDbName );
		SecondaryDatabase secDb = this.getEnvironment().openSecondaryDatabase(
				null, secDbName, primaryDb, secDbConf );
		if ( allOpenSecondaryDatabases.addFirstQ( secDb ) ) {
			RunTime.bug( "couldn't have already existed" );
		}
		return secDb;
	}
	
	/**
	 * silently closing database
	 * no throws
	 * 
	 * @return null
	 * @param db
	 *            just for information
	 */
	public final Database silentClosePriDB( Database db ) {

		Log.entry();
		if ( null != db ) {
			String dbname = UNINITIALIZED_STRING;
			try {
				dbname = db.getDatabaseName();
				Log.mid( "closing dbname: " + dbname );
				db.close();// the only place this should be used is this line
				Log.mid( "closed BerkeleyDB with name: " + dbname );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing BerkeleyDB with specified name: '"
						+ dbname );
				// ignore
			} finally {
				if ( !allOpenPrimaryDatabases.removeObject( db ) ) {
					RunTime.bug( "should've succeeded" );
				}
			}
		} else {
			Log.mid( "close db was called on null db object" );
		}
		
		return null;
	}
	
}// class
