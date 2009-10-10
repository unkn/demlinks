/**
 * File creation: Jun 3, 2009 12:41:32 PM
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



import java.util.HashSet;

import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;



/**
 * it's a BerkeleyDB database that stores any and all Sequences<br>
 * 
 * we need a BerkeleyDB to store sequences for all other databases<br>
 * each sequence is gotten via a key, and it's only one key->data pair per
 * sequence
 * that is, a sequence used for another database will only have one key and
 * one data in this BerkeleyDB
 */
public class DBSequence {
	
	
	private final static String					db_NAME				= "db5_AllSequences";
	// a database where all sequences will be stored:
	private static Database						db					= null;
	
	// FIXME: SequenceConfig will be kept the same for all Sequence -s, for now
	private static SequenceConfig				allSequencesConfig	= new MySequenceConfig();
	
	private final static HashSet<DBSequence>	ALL_INSTANCES		= new HashSet<DBSequence>();
	private static DatabaseConfig				dbConf				= null;
	private static final String					seqPrefix			= (char)0
																			+ "_preseq_"
																			+ (char)0;
	private static final String					seqSuffix			= (char)255
																			+ "_postseq_"
																			+ (char)255;
	
	// non-static follows:
	private Sequence							thisSeq				= null;
	private String								thisSeqName			= null;
	
	

	/**
	 * private constructor, use getSeq() instead
	 * 
	 * @param seqName
	 */
	private DBSequence( String seqName ) {

		RunTime.assertNotNull( seqName );
		RunTime.assertFalse( seqName.isEmpty() );
		
		thisSeqName = seqPrefix + seqName + seqSuffix;
	}
	
	/**
	 * new instance of DBSequence
	 * 
	 * @param seqName1
	 *            name of the Sequence
	 * @return
	 */
	public static final DBSequence newDBSequence( String seqName1 ) {

		DBSequence dbs = new DBSequence( seqName1 );
		if ( !ALL_INSTANCES.add( dbs ) ) {
			RunTime.Bug( "couldn't have already existed!" );
		}
		RunTime.assertNotNull( dbs );
		return dbs;
	}
	
	/**
	 * 
	 */
	private static final void silentCloseAllSequences() {

		Log.entry();
		for ( DBSequence dbs : ALL_INSTANCES ) {
			dbs.silentCloseSeq();
			ALL_INSTANCES.remove( dbs );
		}
		RunTime.assertTrue( ALL_INSTANCES.isEmpty() );
	}
	
	/**
	 * closing all sequences first, then the BerkeleyDB
	 */
	private static void silentCloseDBAndAllSequences() {

		
		if ( !ALL_INSTANCES.isEmpty() ) {
			silentCloseAllSequences();
		}
		
		if ( !ALL_INSTANCES.isEmpty() ) {
			// BUG, avoiding throw because it's silent
			Log.bug( "should be empty now" );
		}
		
		if ( null != db ) {
			db = BerkeleyDB.silentCloseAnyDB( db, db_NAME );
		} else {
			Log.warn( "close() called on a not yet inited/open database" );
		}
	}
	
	/**
	 * safely closes all active sequences and the database holding them
	 */
	public static final void deInitAll() {

		silentCloseDBAndAllSequences();
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private static Database getSeqsDB() throws DatabaseException {

		if ( null == db ) {
			// init first time:
			db = openSeqDB();
			RunTime.assertNotNull( db );
		}
		return db;
	}
	
	@Override
	protected void finalize() {

		this.silentCloseSeq();
		if ( ALL_INSTANCES.isEmpty() ) {
			silentCloseDBAndAllSequences();
		}
	}
	
	/**
	 * one time open the database containing all stored sequences, and future
	 * ones
	 * 
	 * @param dbName
	 * @return
	 * @throws DatabaseException
	 */
	private static final Database openSeqDB() throws DatabaseException {

		RunTime.assertNotNull( db_NAME );
		RunTime.assertFalse( db_NAME.isEmpty() );
		
		if ( null == dbConf ) {
			// init once:
			dbConf = new DatabaseConfig();
			dbConf.setAllowCreate( true );
			dbConf.setDeferredWrite( false );
			dbConf.setKeyPrefixing( true );
			dbConf.setSortedDuplicates( false );//
			dbConf.setTransactional( true );
		}
		
		return BerkeleyDB.getEnvironment().openDatabase( null, db_NAME, dbConf );
	}
	
	
	/**
	 * @return null
	 */
	public DBSequence silentCloseSeq() {

		Log.entry( "attempting to close sequence: " + thisSeqName );
		if ( null != thisSeq ) {
			try {
				thisSeq.close();
				Log.exit( "closed seq with name: " + thisSeqName );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing seq with specified name: '"
						+ thisSeqName );
				// ignore
			} finally {
				thisSeq = null;
			}
		} else {
			Log.mid( "seq was already closed with name: " + thisSeqName );
		}
		
		return null;
	}
	
	
	/**
	 * @return never null
	 * @throws DatabaseException
	 */
	public Sequence getSequence() throws DatabaseException {

		if ( null == thisSeq ) {
			// init once:
			DatabaseEntry deKey = new DatabaseEntry();
			BerkeleyDB.stringToEntry( thisSeqName, deKey );
			thisSeq = getSeqsDB().openSequence( null, deKey, allSequencesConfig );
			RunTime.assertNotNull( thisSeq );
		}
		return thisSeq;
	}
	
}
