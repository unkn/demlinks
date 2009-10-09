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
import org.omg.CORBA.Environment;



/**
 * 
 *
 */
public class BerkeleyDB {
	
	private static final String				DB_ENVIRONMENT_HOMEDIR	= "c:\\sometmp";
	private static final EnvironmentConfig	environmentConfig		= new EnvironmentConfig();
	private static Environment				env						= null;
	private static DBMapJIDsToNodeIDs		db1						= null;
	
	
	/**
	 * @return the database handling the one to one mapping between JIDs and
	 *         NodeIDs
	 */
	public static DBMapJIDsToNodeIDs getDBMapJIDsToNodeIDs() {

		if ( null == db1 ) {
			db1 = new DBMapJIDsToNodeIDs( "map(JID<->NodeID)" );
			RunTime.assertNotNull( db1 );
		}
		return db1;
	}
	
	/**
	 * call before all
	 */
	public static final void initAll() {

		// Environment init isn't needed, only deInit();
		// DBSequence init isn't needed, only deInit()
		
		// getDBMapJIDsToNodeIDs() is initing that when needed
		
		// db1=db1.init();
		
	}
	
	
	/**
	 *call when all done
	 */
	public static final void deInitAll() {

		if ( null != db1 ) {
			db1 = db1.deInit();
		}
		DBSequence.deInitAll();
		BerkeleyDB.closeDBEnvironment();
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	public static final Environment getEnvironment() throws DatabaseException {

		if ( null == env ) {
			// make new now:
			firstTimeCreateEnvironment();
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
	private static final void firstTimeCreateEnvironment()
			throws DatabaseException {

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
		File file = new File( DB_ENVIRONMENT_HOMEDIR );
		try {
			env = new Environment( file, environmentConfig );
		} catch ( DatabaseException de ) {
			Log.thro( "when creating BerkeleyDB Environment" );
			throw de;
		}
		
	}
	
	

	// /**
	// * @param db1
	// */
	// @SuppressWarnings( "unused" )
	// private static final void closeAnyDB( Database db1 ) {
	//
	// closeAnyDB( db1, "not specified" );
	// }
	
	/**
	 * silently closing database
	 * no throws
	 * 
	 * @return null
	 * @param db
	 */
	public static final Database silentCloseAnyDB( Database db, String dbname ) {

		if ( null != db ) {
			try {
				db.close();
				Log.mid( "closed BerkeleyDB with name: " + dbname );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing BerkeleyDB with specified name: '"
						+ dbname );
				// ignore
			}
		} else {
			Log.mid( "wasn't open BerkeleyDB with name: " + dbname );
		}
		return null;
	}
	
	/**
	 * silently closing SecondaryDatabase
	 * no throws
	 * 
	 * @return null
	 * @param secDb
	 */
	public static final SecondaryDatabase silentCloseAnySecDB(
			SecondaryDatabase secDb, String secDbName ) {

		if ( null != secDb ) {
			try {
				secDb.close();
				Log.mid( "closed SecDB with name: " + secDbName );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing SecDB with specified name: '"
						+ secDbName );
				// ignore
			}
		} else {
			Log.mid( "wasn't open SecDB with name: " + secDbName );
		}
		return null;
	}
	
	/**
	 * 
	 */
	public static final void closeDBEnvironment() {

		if ( null != env ) {
			try {
				env.close();
				Log.exit( "BerkeleyDB env closed" );
			} catch ( DatabaseException de ) {
				Log.thro( "failed BerkeleyDB environment close" );
				// ignore
			} finally {
				env = null;
			}
		} else {
			Log.mid( "BerkeleyDB env wasn't open" );
		}
	}
	


}// class
