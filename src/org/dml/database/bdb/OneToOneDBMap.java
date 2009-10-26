/**
 * File creation: Jun 1, 2009 10:47:12 PM
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



import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryDatabase;



/**
 * It's like a TreeMap or HashMap?
 * key->data
 * lookup by either key or data
 * they're internally stored as primary and secondary databases:
 * key->data and data->key
 * key and data are both Strings
 */
public class OneToOneDBMap {
	
	private static final String			secPrefix	= "secondary";
	private DatabaseCapsule				forwardDB	= null;
	private SecondaryDatabaseCapsule	backwardDB	= null;
	protected String					dbName;
	protected final Level2_BerkeleyDB	bdb;
	
	/**
	 * constructor
	 * 
	 * @param dbName1
	 */
	public OneToOneDBMap( Level2_BerkeleyDB bdb1, String dbName1 ) {

		RunTime.assertNotNull( bdb1 );
		RunTime.assertNotNull( dbName1 );
		bdb = bdb1;
		dbName = dbName1;
	}
	
	/**
	 * this init doesn't need to be called from an external caller, it's called
	 * internally when needed
	 * 
	 * @throws DatabaseException
	 */
	private void internal_initBoth() throws DatabaseException {

		forwardDB = new DatabaseCapsule( bdb, dbName, new OneToOneDBConfig() );
		
		backwardDB = new SecondaryDatabaseCapsule( bdb, secPrefix + dbName,
				new OneToOneSecondaryDBConfig(), forwardDB.getDB() );
		
		// must make sure second BerkeleyDB is also open!! because all inserts
		// are done
		// via first BerkeleyDB
		backwardDB.getSecDB();
		
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private Database getForwardDB() throws DatabaseException {

		if ( null == forwardDB ) {
			this.internal_initBoth();
			RunTime.assertNotNull( forwardDB );
		}
		return forwardDB.getDB();
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private SecondaryDatabase getBackwardDB() throws DatabaseException {

		if ( null == backwardDB ) {
			this.internal_initBoth();
			RunTime.assertNotNull( backwardDB );
		}
		return backwardDB.getSecDB();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {

		Log.exit( "in finalize() for OneToOneDBMap: " + dbName );
		this.silentClose();
		backwardDB = null;
		forwardDB = null;
		dbName = null;
		
		super.finalize();
	}
	
	/**
	 * @return null
	 */
	public OneToOneDBMap silentClose() {

		Log.entry( "closing OneToOneDBMap: " + dbName );
		boolean one = false;
		boolean two = false;
		
		// we don't have to set these to null, because they can be getDB() again
		if ( null != backwardDB ) {
			backwardDB.silentClose();// first close this
		} else {
			one = true;
		}
		if ( null != forwardDB ) {
			forwardDB.silentClose();// then this
		} else {
			two = true;
		}
		
		if ( one ^ two ) {
			RunTime.Bug( "they should both be the same value, otherwise one of "
					+ "backwardDB and forwardDB was open and the other closed "
					+ "and we should've had both open always" );
		} else {
			if ( one ) {
				Log.warn( "close called on a not yet inited/open database" );
			}
		}
		
		return null;
	}
	
	/**
	 * @param string
	 * @param string2
	 * @throws DatabaseException
	 */
	public OperationStatus link( String key, String data )
			throws DatabaseException {

		// TODO FIXME key/data should be able to do any object
		DatabaseEntry deKey = new DatabaseEntry();
		DatabaseEntry deData = new DatabaseEntry();
		Level2_BerkeleyDB.stringToEntry( key, deKey );
		Level2_BerkeleyDB.stringToEntry( data, deData );
		OperationStatus ret = this.getForwardDB().putNoOverwrite( null, deKey,
				deData );
		return ret;
	}
	
	/**
	 * @param data
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public String getKey( String data ) throws DatabaseException {

		DatabaseEntry deData = new DatabaseEntry();
		Level2_BerkeleyDB.stringToEntry( data, deData );
		DatabaseEntry deKey = new DatabaseEntry();
		DatabaseEntry pKey = new DatabaseEntry();
		// deData=new DatabaseEntry(data.getBytes(BerkeleyDB.ENCODING));
		OperationStatus ret;
		ret = this.getBackwardDB().get( null, deData, pKey, deKey, null );
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		RunTime.assertTrue( deData.equals( deKey ) );
		
		return Level2_BerkeleyDB.entryToString( pKey );
	}
	
	/**
	 * @param string
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public String getData( String key ) throws DatabaseException {

		DatabaseEntry deKey = new DatabaseEntry();
		Level2_BerkeleyDB.stringToEntry( key, deKey );
		DatabaseEntry deData = new DatabaseEntry();
		OperationStatus ret;
		ret = this.getForwardDB().get( null, deKey, deData, null );
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		
		return Level2_BerkeleyDB.entryToString( deData );
	}
	

}
