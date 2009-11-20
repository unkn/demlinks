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


package org.dml.database.bdb.level1;



import java.io.File;

import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTracker;
import org.javapart.logger.Log;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

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
public class Level1_Storage_BerkeleyDB extends StaticInstanceTracker {
	
	private String												envHomeDir;
	private final EnvironmentConfig								environmentConfig			= new EnvironmentConfig();
	private Environment											env							= null;
	private DBMap_JavaIDs_To_Symbols							db_JavaID_To_Symbol			= null;
	

	// a database where all sequences will be stored:(only 1 db per bdb env)
	private Database											seqDb						= null;
	private final static String									seqDb_NAME					= "db5_AllSequences";
	private DatabaseConfig										seqDbConf					= null;
	
	// we keep track of open stuffs just in case we need to emergency shutdown
	// ie. on Exception
	private final ListOfUniqueNonNullObjects<Sequence>			allSequenceInstances		= new ListOfUniqueNonNullObjects<Sequence>();
	private final ListOfUniqueNonNullObjects<Database>			allOpenPrimaryDatabases		= new ListOfUniqueNonNullObjects<Database>();
	private final ListOfUniqueNonNullObjects<SecondaryDatabase>	allOpenSecondaryDatabases	= new ListOfUniqueNonNullObjects<SecondaryDatabase>();
	
	private static final String									dbNAME_JavaID_To_NodeID		= "map(JavaID<->NodeID)";
	private final static String									UNINITIALIZED_STRING		= "uninitializedString";
	
	/**
	 * singleton
	 * 
	 * @return the database handling the one to one mapping between JavaIDs and
	 *         NodeIDs
	 * @throws DatabaseException
	 */
	public DBMap_JavaIDs_To_Symbols getDBMap_JavaIDs_To_Symbols()
			throws DatabaseException {

		if ( null == db_JavaID_To_Symbol ) {
			db_JavaID_To_Symbol = new DBMap_JavaIDs_To_Symbols( this,
					dbNAME_JavaID_To_NodeID );
			RunTime.assumedNotNull( db_JavaID_To_Symbol );
		}
		return db_JavaID_To_Symbol;
	}
	
	

	/**
	 * constructor, don't forget to call init(..);
	 */
	public Level1_Storage_BerkeleyDB() {

		super();
	}
	
	@Override
	protected void start( MethodParams<Object> params ) {

		// super.start(params);
		envHomeDir = params.getExString( PossibleParams.homeDir );
		Log.entry( envHomeDir );
		if ( (Boolean)params.getEx( PossibleParams.jUnit_wipeDB ) ) {
			Log.special( "destroying previous environment, before we beging..." );
			this.internalWipeEnv();
		}
		try {
			this.getEnvironment();
		} catch ( DatabaseException e ) {
			// FIXME: can't mod method signature
			e.printStackTrace();
			throw new StorageException( e );
		}// forces env open or create
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done( MethodParams<Object> params ) {

		if ( null != db_JavaID_To_Symbol ) {
			db_JavaID_To_Symbol = db_JavaID_To_Symbol.deInit();
		}
		this.deInitSeqSystem_silent();// first
		this.closeAllOpenDatabases_silent();// second
		this.closeDBEnvironment();// last
		
		Reference<Object> killWhenDoneRef = params.get( PossibleParams.jUnit_wipeDBWhenDone );
		if ( null != killWhenDoneRef ) {
			if ( (Boolean)killWhenDoneRef.getObject() ) {
				Log.special( "destroying environment, we're done..." );
				this.internalWipeEnv();
			}
		}
		
		// super.done();
	}
	
	// =============================================
	
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
	 * @return singleton of the BDB Environment
	 * @throws DatabaseException
	 */
	public final Environment getEnvironment() throws DatabaseException {

		if ( null == env ) {
			// make new now:
			this.firstTimeCreateEnvironment();
			RunTime.assumedNotNull( env );
		}
		
		return env;
	}
	
	
	// /**
	// * @param input
	// * @param output
	// */
	// public final static void stringToEntry( String input, DatabaseEntry
	// output ) {
	//
	// RunTime.assertNotNull( input, output );
	// StringBinding.stringToEntry( input, output );
	// }
	//	
	// /**
	// * @param input
	// * @return
	// */
	// public final static String entryToString( DatabaseEntry input ) {
	//
	// RunTime.assertNotNull( input );
	// return StringBinding.entryToString( input );
	// }
	


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

		RunTime.assumedNotNull( thisSeqName, allSequencesConfig );
		// allSequencesConfig can be null though, to use BDB defaults
		
		// init once:
		DatabaseEntry deKey = new DatabaseEntry();
		StringBinding.stringToEntry( thisSeqName, deKey );
		Sequence seq = this.getSeqsDB().openSequence( null, deKey,
				allSequencesConfig );
		if ( allSequenceInstances.addFirstQ( seq ) ) {
			RunTime.bug( "couldn't have already existed!" );
		}
		RunTime.assumedNotNull( seq );
		return seq;
	}
	
	/**
	 * @return null
	 */
	public Sequence closeAnySeq_silent( Sequence thisSeq, String thisSeqName ) {

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
	private final void closeAllSequences_silent() {

		Log.entry();
		Sequence iter;
		// int count = 0;
		while ( null != ( iter = allSequenceInstances.getObjectAt( Position.FIRST ) ) ) {
			// FIXME: the bad part is that whoever owns iter cannot set it to
			// NULL ie. inside a DBSequence instance, but I'm guessing that
			// there won't be any calls to DBSequence.done() before
			// closeEnvironment() finishes anyway; same goes for DatabaseCapsule
			// and SecondaryDatabaseCapsule
			this.closeAnySeq_silent( iter, "autoclosing..." );// we don't know
			// the name here
			if ( allSequenceInstances.removeObject( iter ) ) {
				RunTime.bug( "should've already been removed by above statement" );
			}
			// count++;
		}
		// System.out.println( count );
		RunTime.assumedTrue( allSequenceInstances.isEmpty() );
		Log.exit();
	}
	
	/**
	 * closing all sequences first, then the BerkeleyDB holding them
	 */
	private void closeAllSequencesAndTheirDB_silent() {

		Log.entry();
		if ( !allSequenceInstances.isEmpty() ) {
			this.closeAllSequences_silent();
		}
		
		if ( !allSequenceInstances.isEmpty() ) {
			// BUG, avoiding throw because it's silent; nevermind that
			RunTime.bug( "should be empty now" );
		}
		
		if ( null != seqDb ) {
			seqDb = this.closePriDB_silent( seqDb );
		}
	}
	
	/**
	 * safely closes all active sequences and the database holding them<br>
	 * for the current environment only
	 */
	public final void deInitSeqSystem_silent() {

		Log.entry();
		this.closeAllSequencesAndTheirDB_silent();
		
	}
	
	/**
	 * closing secondary then primary databases
	 */
	private void closeAllOpenDatabases_silent() {

		Log.entry();
		// close secondaries first!
		SecondaryDatabase iterSec;
		while ( null != ( iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST ) ) ) {
			this.silentCloseAnySecDB( iterSec );
			if ( allOpenSecondaryDatabases.removeObject( iterSec ) ) {
				RunTime.bug( "should've already been removed by above cmd" );
			}
			// iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST
			// );
		}
		
		// closing primaries:
		Database iter;
		while ( null != ( iter = allOpenPrimaryDatabases.getObjectAt( Position.FIRST ) ) ) {
			this.closePriDB_silent( iter );
			if ( allOpenPrimaryDatabases.removeObject( iter ) ) {
				RunTime.bug( "should've already been removed by above cmd" );
			}
			// iter = allOpenPrimaryDatabases.getObjectAt( Position.FIRST );
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
			RunTime.assumedNotNull( seqDb );
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

		RunTime.assumedNotNull( seqDb_NAME );
		RunTime.assumedFalse( seqDb_NAME.isEmpty() );
		
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
	public final Database closePriDB_silent( Database db ) {

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
						+ dbname + "; reason: " + de.getLocalizedMessage() );
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
