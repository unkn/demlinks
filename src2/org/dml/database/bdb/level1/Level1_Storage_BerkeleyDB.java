/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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

import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.dml.tracking.Log;
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
import com.sleepycat.je.Durability;
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
public class Level1_Storage_BerkeleyDB
		extends
		Initer
{
	
	private String												envHomeDir;
	
	// it seems ok to share this between all environments
	private EnvironmentConfig									environmentConfig			= null;
	
	private Environment											env							= null;
	private DBMap_JavaIDs_To_Symbols							db_JavaID_To_Symbol			= null;
	
	
	// a database where all sequences will be stored:(only 1 db per bdb env)
	private Database											seqDb						= null;
	private final static String									seqDb_NAME					= "db5_AllSequences";
	private DatabaseConfig										seqDbConf					= null;
	
	// we keep track of open stuffs just in case we need to emergency shutdown
	// ie. on Exception
	private final ListOfUniqueNonNullObjects<Sequence>			allSequenceInstances		= new ListOfUniqueNonNullObjects<Sequence>();
	// LIFO: allOpenPrimaryDatabases
	private final ListOfUniqueNonNullObjects<Database>			allOpenPrimaryDatabases		= new ListOfUniqueNonNullObjects<Database>();
	private final ListOfUniqueNonNullObjects<SecondaryDatabase>	allOpenSecondaryDatabases	= new ListOfUniqueNonNullObjects<SecondaryDatabase>();
	private UniqueSymbolsGenerator								symGen						= null;
	
	private final static String									dbNAME_JavaID_To_NodeID		= "map(JavaID<->NodeID)";
	private final static String									UNINITIALIZED_STRING		= "uninitializedString";
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public
			boolean
			equals(
					Object obj )
	{
		if ( null != obj )
		{
			if ( super.equals( obj ) )
			{
				return true;
			}
			else
			{
				if ( this.getClass() == obj.getClass() )
				{
					Log.warn( "reached the problematic issue of same environment in different instances" );
					// FIXME: TODO: find a way to give true if two different env instances are open on same directory
					// basically we can have two bdb environments on same dir thus accessing same Symbols and these must
					// be detected as equal environments
					Level1_Storage_BerkeleyDB l1obj = (Level1_Storage_BerkeleyDB)obj;
					RunTime.assumedTrue( this.isInitedSuccessfully() );
					RunTime.assumedTrue( l1obj.isInitingOrInited() );
					RunTime.assumedTrue( l1obj.isInitedSuccessfully() );
					RunTime.assumedNotNull( l1obj.env );
					RunTime.assumedNotNull( env );
					RunTime.assumedTrue( env != l1obj.env );
					RunTime.assumedNotNull( environmentConfig );
					// RunTime.assumedNotNull( Level1_Storage_BerkeleyDB.environmentConfig );
					// RunTime.assumedTrue( environmentConfig == Level1_Storage_BerkeleyDB.environmentConfig );
					// FIXME: need a better wat to check folder ie. it may be C:\\something or c:\\something\A\.. or
					// different case
					return envHomeDir.equals( l1obj.envHomeDir );
					// Log.thro( "reached" );
				}
			}
		}
		return false;
	}
	
	
	/**
	 * singleton
	 * 
	 * @return the database handling the one to one mapping between JavaIDs and
	 *         Symbols
	 * @throws DatabaseException
	 */
	public
			DBMap_JavaIDs_To_Symbols
			getDBMap_JavaIDs_To_Symbols()
					throws DatabaseException
	{
		
		if ( null == db_JavaID_To_Symbol )
		{
			MethodParams params = MethodParams.getNew();
			params.set(
						PossibleParams.level1_BDBStorage,
						this );
			params.set(
						PossibleParams.dbName,
						dbNAME_JavaID_To_NodeID );
			db_JavaID_To_Symbol = Factory.getNewInstanceAndInit(
																	DBMap_JavaIDs_To_Symbols.class,
																	params );
			// new DBMap_JavaIDs_To_Symbols(
			// );
			// Factory.initWithoutParams( db_JavaID_To_Symbol );
			// db_JavaID_To_Symbol = new DBMap_JavaIDs_To_Symbols( this, dbNAME_JavaID_To_NodeID );
			// db_JavaID_To_Symbol.init( null );
			RunTime.assumedNotNull( db_JavaID_To_Symbol );
		}
		else
		{
			Factory.reInitIfNotInited( db_JavaID_To_Symbol );
			// FIXME: init once on start()
			
			// if ( !db_JavaID_To_Symbol.isInited() ) {
			// Factory.reInit( db_JavaID_To_Symbol );
			// // db_JavaID_To_Symbol.reInit();
			// }
		}
		RunTime.assumedTrue( db_JavaID_To_Symbol.isInitedSuccessfully() );
		return db_JavaID_To_Symbol;
	}
	
	
	/**
	 * constructor, don't forget to call Factory.init(..);
	 */
	public Level1_Storage_BerkeleyDB()
	{
		
		super();
	}
	
	
	@Override
	protected
			void
			start(
					MethodParams params )
	{
		
		envHomeDir = params.getExString( PossibleParams.homeDir );
		Log.entry( envHomeDir );
		if ( (Boolean)params.getEx( PossibleParams.jUnit_wipeDB ) )
		{
			Log.special( "destroying previous environment, before we begin..." );
			this.internalWipeEnv();
		}
		this.getEnvironment();
		
	}
	
	
	public
			UniqueSymbolsGenerator
			getUniqueSymbolsGenerator()
	{
		
		if ( null == symGen )
		{
			symGen = Factory.getNewInstanceAndInitWithoutMethodParams(
																		UniqueSymbolsGenerator.class,
																		this );
			// symGen = new UniqueSymbolsGenerator( this );
			// symGen.init( null );
		}
		else
		{
			Factory.reInitIfNotInited( symGen );
			// FIXME: init all on start
			
			// if ( !symGen.isInited() ) {
			// Factory.reInitIfNotInited( symGen );
			// //symGen.reInit();
			// }
		}
		RunTime.assumedTrue( symGen.isInitedSuccessfully() );
		return symGen;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected
			void
			done(
					MethodParams params )
	{
		
		if ( null != db_JavaID_To_Symbol )
		{
			Factory.deInit_WithPostponedThrows( db_JavaID_To_Symbol );
		}
		if ( null != symGen )
		{
			Factory.deInit_WithPostponedThrows( symGen );
		}
		try
		{
			this.deInitSeqSystem_silent();// first
		}
		catch ( Throwable t )
		{
			// postpone
			RunTime.throPostponed( t );
		}
		try
		{
			this.closeAllOpenDatabases();// second
		}
		catch ( Throwable t )
		{
			// postpone
			RunTime.throPostponed( t );
		}
		
		try
		{
			this.closeDBEnvironment();// last
		}
		catch ( Throwable t )
		{
			// postpone
			RunTime.throPostponed( t );
		}
		
		
		Reference<Object> killWhenDoneRef = params.get( PossibleParams.jUnit_wipeDBWhenDone );
		if ( null != killWhenDoneRef )
		{// param existed
			if ( (Boolean)killWhenDoneRef.getObject() )
			{
				Log.special( "destroying environment from storage, we're probably inside JUnit..." );
				try
				{
					this.internalWipeEnv();
				}
				catch ( Throwable t )
				{
					// postpone
					RunTime.throPostponed( t );
				}
			}
		}
		
		
		RunTime.throwAllThatWerePostponed();
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
	private
			void
			internalWipeEnv()
	{
		
		File dir = new File(
								envHomeDir );
		String[] allThoseInDir = dir.list();
		if ( null != allThoseInDir )
		{
			for ( String element : allThoseInDir )
			{
				File n = new File(
									envHomeDir
											+ File.separator
											+ element );
				if ( !n.isFile() )
				{
					continue;
				}
				if ( ( !n.getPath().matches(
												".*\\.jdb" ) )
						&& ( !( n.getPath().matches( ".*\\.lck" ) ) ) )
				{
					continue;
				}
				Log.special( "removing "
								+ n.getPath() );
				if ( !n.delete() )
				{
					Log.warn( "Failed removing "
								+ n.getAbsolutePath() );
				}
			}
		}
	}
	
	
	/**
	 * @return singleton of the BDB Environment
	 * @throws DatabaseException
	 */
	public final
			Environment
			getEnvironment()
	{
		
		if ( null == env )
		{
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
	private final
			void
			firstTimeCreateEnvironment()
	{
		if ( null == environmentConfig )
		{
			environmentConfig = new EnvironmentConfig();
			
			environmentConfig.setAllowCreate( true );
			environmentConfig.setLocking( true );
			environmentConfig.setTransactional(
												true ).setDurability(
																		Durability.COMMIT_NO_SYNC );
			environmentConfig.setTxnSerializableIsolation( true );
			environmentConfig.setSharedCache( false );
			// environmentConfig.setConfigParam( EnvironmentConfig.TRACE_LEVEL, "OFF" );
			// environmentConfig.setConfigParam( EnvironmentConfig.TRACE_CONSOLE, "false" );
			// environmentConfig.setConfigParam( EnvironmentConfig.TRACE_FILE, "false" );
			// environmentConfig.setConfigParam( EnvironmentConfig.TRACE_DB, "false" );
		}
		// perform other environment configurations
		File file = new File(
								envHomeDir );
		try
		{
			file.mkdirs();
			env = new Environment(
									file,
									environmentConfig );
		}
		catch ( Throwable t )
		{
			Log.thro( "when creating BerkeleyDB Environment: "
						+ t.getMessage() );
			RunTime.throWrapped( t );
		}
		
	}
	
	
	/**
	 * silently closing SecondaryDatabase
	 * no throws
	 * 
	 * @param secDb
	 */
	public final
			void
			closeAnySecDB(
							SecondaryDatabase secDb )
	{
		
		Log.entry();
		if ( null != secDb )
		{
			
			String secDbName = UNINITIALIZED_STRING;
			try
			{
				try
				{
					secDbName = secDb.getDatabaseName();
					
				}
				finally
				{
					secDb.close();
					Log.mid( "closed SecDB with name: "
								+ secDbName );
				}
			}
			catch ( Throwable t )
			{
				Log.thro( "failed closing SecDB with specified name: '"
							+ secDbName );
				RunTime.throWrapped( t );// wrap and re-throw now
			}
			finally
			{
				RunTime.assumedFalse( allOpenSecondaryDatabases.isEmpty() );
				if ( !allOpenSecondaryDatabases.removeObject( secDb ) )
				{
					RunTime.bug( "should've existed" );
				}
			}
		}
		else
		{
			Log.mid( "wasn't open SecDB" );
		}
	}
	
	
	/**
	 * 
	 */
	private final
			void
			closeDBEnvironment()
	{
		
		if ( null != env )
		{
			try
			{
				env.close();
				Log.exit( "BerkeleyDB env closed" );
			}
			catch ( Throwable t )
			{
				Log.thro( "failed BerkeleyDB environment close:"
							+ t.getLocalizedMessage() );
				// ignore
				RunTime.throWrapped( t );
			}
			finally
			{
				env = null;
			}
		}
		else
		{
			Log.mid( "BerkeleyDB env wasn't open" );
		}
	}
	
	
	/**
	 * new instance of Sequence, keeping track of it inside BerkeleyDB class
	 * just in case we need to shutdown all when Exception detected
	 * 
	 * @param thisSeqName
	 * @param allSequencesConfig
	 * 
	 * @return never null
	 * @throws DatabaseException
	 */
	public
			Sequence
			getNewSequence(
							String thisSeqName,
							SequenceConfig allSequencesConfig )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull(
								thisSeqName,
								allSequencesConfig );
		// allSequencesConfig can be null though, to use BDB defaults
		
		// init once:
		DatabaseEntry deKey = new DatabaseEntry();
		StringBinding.stringToEntry(
										thisSeqName,
										deKey );
		Sequence seq = this.getSeqsDB().openSequence(
														null,
														deKey,
														allSequencesConfig );
		if ( allSequenceInstances.addObjectAtPosition(
														Position.FIRST,
														seq ) )
		{
			RunTime.bug( "couldn't have already existed!" );
		}
		RunTime.assumedNotNull( seq );
		return seq;
	}
	
	
	/**
	 * @param thisSeq
	 * @param thisSeqName
	 * @return null
	 */
	public
			Sequence
			closeAnySeq(
							Sequence thisSeq,
							String thisSeqName )
	{
		
		Log.entry( "attempting to close sequence: "
					+ thisSeqName );
		if ( null != thisSeq )
		{
			try
			{
				thisSeq.close();
				Log.exit( "closed seq with name: "
							+ thisSeqName );
			}
			catch ( Throwable t )
			{
				Log.thro( "failed closing seq with specified name: '"
							+ thisSeqName );
				RunTime.throWrapped( t );// wrap around and don't postpone
			}
			finally
			{
				RunTime.assumedFalse( allSequenceInstances.isEmpty() );
				if ( !allSequenceInstances.removeObject( thisSeq ) )
				{
					RunTime.bug( "should've existed" );// this will not be postponed
				}
			}
		}
		else
		{
			Log.mid( "seq was already closed with name: "
						+ thisSeqName );
		}
		
		return null;
	}
	
	
	/**
	 * @throws anything
	 */
	private final
			void
			closeAllSequences()
	{
		
		Log.entry();
		Sequence iter;
		// int count = 0;
		while ( null != ( iter = allSequenceInstances.getObjectAt( Position.FIRST ) ) )
		{
			// FIXME: (maybe this comment is outdated?)the bad part is that whoever owns iter cannot set it to
			// NULL ie. inside a DBSequence instance, but I'm guessing that
			// there won't be any calls to DBSequence.done() before
			// closeEnvironment() finishes anyway; same goes for DatabaseCapsule
			// and SecondaryDatabaseCapsule
			try
			{
				this.closeAnySeq(
									iter,
									"autoclosing..." );// we don't know the name here
			}
			catch ( Throwable t )
			{
				RunTime.throPostponed( t );
				// postpone all, until all sequence instances are closed
			}
			finally
			{
				// but we won't postpone these since these signal bugs in the 'shutdown engine'
				RunTime.assumedFalse( allSequenceInstances.isEmpty() );
				if ( !allSequenceInstances.removeObject( iter ) )
				{
					RunTime.bug( "should've existed before" );
				}
			}
			// count++;
		}
		// System.out.println( count );
		RunTime.assumedTrue( allSequenceInstances.isEmpty() );
		Log.exit();
		RunTime.throwAllThatWerePostponed();
	}
	
	
	/**
	 * closing all sequences first, then the BerkeleyDB holding them
	 */
	private
			void
			closeAllSequencesAndTheirDB()
	{
		
		Log.entry();
		try
		{
			if ( !allSequenceInstances.isEmpty() )
			{
				this.closeAllSequences();
			}
		}
		catch ( Throwable t )
		{
			// postpone
			RunTime.throPostponed( t );
		}
		finally
		{
			// don't postpone these, since this signal that something's wrong with 'shutdown/cleanup engine'
			if ( !allSequenceInstances.isEmpty() )
			{
				RunTime.bug( "should be empty now" );
			}
		}
		
		try
		{
			if ( null != seqDb )
			{
				this.closeAnyPriDB( seqDb );
				seqDb = null;
			}
		}
		catch ( Throwable t )
		{
			// postpone
			RunTime.throPostponed( t );
		}
		
		// final:
		RunTime.throwAllThatWerePostponed();
	}
	
	
	/**
	 * safely closes all active sequences and the database holding them<br>
	 * for the current environment only
	 */
	public final
			void
			deInitSeqSystem_silent()
	{
		
		Log.entry();
		this.closeAllSequencesAndTheirDB();
		
	}
	
	
	/**
	 * closing secondary then primary databases
	 */
	private
			void
			closeAllOpenDatabases()
	{
		
		Log.entry();
		// close secondaries first!
		SecondaryDatabase iterSec;
		while ( null != ( iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST ) ) )
		{
			try
			{
				this.closeAnySecDB( iterSec );
			}
			catch ( Throwable t )
			{
				// postpone
				RunTime.throPostponed( t );
			}
			finally
			{
				if ( allOpenSecondaryDatabases.removeObject( iterSec ) )
				{
					RunTime.bug( "should've already been removed by above cmd" );
				}
			}
			// iterSec = allOpenSecondaryDatabases.getObjectAt( Position.FIRST
			// );
		}
		
		// closing primaries: LIFO manner
		Database iter;
		while ( null != ( iter = allOpenPrimaryDatabases.getObjectAt( Position.FIRST ) ) )
		{
			try
			{
				this.closeAnyPriDB( iter );
			}
			catch ( Throwable t )
			{
				// postpone only these
				RunTime.throPostponed( t );
			}
			finally
			{
				// don't postpone the following:
				if ( allOpenPrimaryDatabases.removeObject( iter ) )
				{
					RunTime.bug( "should've already been removed by above cmd" );
				}
			}
		}
		
		RunTime.assumedTrue( allOpenSecondaryDatabases.isEmpty() );
		RunTime.assumedTrue( allOpenPrimaryDatabases.isEmpty() );
		
		RunTime.throwAllThatWerePostponed();
	}
	
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	protected
			Database
			getSeqsDB()
					throws DatabaseException
	{
		
		if ( null == seqDb )
		{
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
	private final
			Database
			openSeqDB()
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( seqDb_NAME );
		RunTime.assumedFalse( seqDb_NAME.isEmpty() );
		
		if ( null == seqDbConf )
		{
			// init once:
			seqDbConf = new DatabaseConfig();
			seqDbConf.setAllowCreate( true );
			seqDbConf.setDeferredWrite( false );
			seqDbConf.setKeyPrefixing( true );
			seqDbConf.setSortedDuplicates( false );//
			seqDbConf.setTransactional( true );
		}
		
		return this.openAnyDatabase(
										seqDb_NAME,
										seqDbConf );
	}
	
	
	/**
	 * @param dbName
	 * @param dbConf
	 * @return the db
	 * @throws DatabaseException
	 */
	public
			Database
			openAnyDatabase(
								String dbName,
								DatabaseConfig dbConf )
	{
		
		Log.entry( dbName );
		RunTime.assumedNotNull(
								dbName,
								dbConf );
		// should not use this openDatabase() method anywhere else
		Database db = this.getEnvironment().openDatabase(
															null,
															dbName,
															dbConf/* could be null to use defaults, but no */
		);
		if ( allOpenPrimaryDatabases.addObjectAtPosition(
															Position.FIRST,
															db ) )
		{
			RunTime.bug( "couldn't have already existed!" );
		}
		RunTime.assumedNotNull( allOpenPrimaryDatabases.getRef( db ) );
		return db;
		// this should be the only method doing open on any database in this
		// environment
	}
	
	
	/**
	 * @param secDbName
	 * @param primaryDb
	 * @param secDbConf
	 * @return the secondary db
	 * @throws DatabaseException
	 */
	public
			SecondaryDatabase
			openAnySecDatabase(
								String secDbName,
								Database primaryDb,
								SecondaryConfig secDbConf )
					throws DatabaseException
	{
		
		Log.entry( secDbName );
		SecondaryDatabase secDb = this.getEnvironment().openSecondaryDatabase(
																				null,
																				secDbName,
																				primaryDb,
																				secDbConf );
		if ( allOpenSecondaryDatabases.addObjectAtPosition(
															Position.FIRST,
															secDb ) )
		{
			RunTime.bug( "couldn't have already existed" );
		}
		return secDb;
	}
	
	
	/**
	 * silently closing database
	 * no throws
	 * 
	 * @param db
	 *            just for information
	 */
	public final
			void
			closeAnyPriDB(
							Database db )
	{
		
		Log.entry();
		if ( null != db )
		{
			
			String dbname = UNINITIALIZED_STRING;
			try
			{
				try
				{
					dbname = db.getDatabaseName();
					Log.mid( "closing dbname: "
								+ dbname );
				}
				finally
				{
					db.close();// the only place this should be used is this line
					RunTime.assumedNotNull( db );
					Log.mid( "closed BerkeleyDB with name: "
								+ dbname );
				}
			}
			catch ( Throwable t )
			{
				Log.thro( "failed closing BerkeleyDB with specified name: '"
							+ dbname
							+ "; reason: "
							+ t.getLocalizedMessage() );
				RunTime.throWrapped( t );// wrap around and throw now
			}
			finally
			{
				RunTime.assumedNotNull( db );
				RunTime.assumedFalse( allOpenPrimaryDatabases.isEmpty() );
				if ( !allOpenPrimaryDatabases.removeObject( db ) )
				{
					RunTime.bug( "should've succeeded" );
				}
			}
		}
		else
		{
			Log.mid( "close db was called on null db object" );
		}
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public
			int
			hashCode()
	{
		RunTime.assumedNotNull( envHomeDir );
		return envHomeDir.hashCode();
	}
	
	
	
}// class
