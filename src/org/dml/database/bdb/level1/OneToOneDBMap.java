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



import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.javapart.logger.Log;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryDatabase;



/**
 * vector means it has a sense,
 * ie. key is first aka initial
 * and data is second aka terminal
 * 
 * It's like a TreeMap or HashMap?
 * key->data
 * lookup by either key or data
 * they're internally stored as primary and secondary databases:
 * key->data and data->key
 */
public class OneToOneDBMap<KeyType, DataType> extends Initer {
	
	private final Class<KeyType>				keyClass;
	private final Class<DataType>				dataClass;
	
	private final EntryBinding<KeyType>			keyBinding;
	private final EntryBinding<DataType>		dataBinding;
	
	private static final String					secPrefix	= "secondary";
	private DatabaseCapsule						forwardDB	= null;
	private SecondaryDatabaseCapsule			backwardDB	= null;
	protected String							dbName;
	protected final Level1_Storage_BerkeleyDB	bdb;
	
	/**
	 * constructor
	 * 
	 * @param dbName1
	 */
	public OneToOneDBMap( Level1_Storage_BerkeleyDB bdb1, String dbName1, Class<KeyType> keyClass1,
			EntryBinding<KeyType> keyBinding1, Class<DataType> dataClass1, EntryBinding<DataType> dataBinding1 ) {

		RunTime.assumedNotNull( bdb1 );
		RunTime.assumedNotNull( dbName1 );
		bdb = bdb1;
		dbName = dbName1;
		keyClass = keyClass1;
		dataClass = dataClass1;
		keyBinding = keyBinding1;// AllTupleBindings.getBinding( keyClass );
		dataBinding = dataBinding1;// AllTupleBindings.getBinding( dataClass );
	}
	
	/**
	 * this init doesn't need to be called from an external caller, it's called
	 * internally when needed
	 * 
	 * @throws DatabaseException
	 */
	private void internal_initBoth() throws DatabaseException {

		// forwardDB = new DatabaseCapsule();
		MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.level1_BDBStorage, bdb );
		params.set( PossibleParams.dbName, dbName );
		params.set( PossibleParams.priDbConfig, new OneToOneDBConfig() );
		forwardDB = Factory.getNewInstanceAndInit( DatabaseCapsule.class, params );
		// forwardDB.init( params );
		


		// backwardDB = new SecondaryDatabaseCapsule();
		params.set( PossibleParams.dbName, secPrefix + dbName );
		params.set( PossibleParams.secDbConfig, new OneToOneSecondaryDBConfig() );
		params.set( PossibleParams.priDb, forwardDB.getDB() );
		params.remove( PossibleParams.priDbConfig );// not needed
		backwardDB = Factory.getNewInstanceAndInit( SecondaryDatabaseCapsule.class, params );
		// backwardDB.init( params );
		// params.deInit();
		Factory.deInit( params );
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
			RunTime.assumedNotNull( forwardDB );
		} else {
			Factory.reInitIfNotInited( forwardDB );
			// if ( !forwardDB.isInited() ) {
			// // forwardDB.reInit();
			// Factory.reInit_aka_InitAgain_WithOriginalPassedParams( forwardDB );
			// }
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
			RunTime.assumedNotNull( backwardDB );
		} else {
			Factory.reInitIfNotInited( backwardDB );
			// if ( !backwardDB.isInited() ) {
			// backwardDB.reInit();
			// }
		}
		return backwardDB.getSecDB();
	}
	
	/**
	 * @param key
	 * @param data
	 * @return true if already existed
	 * @throws DatabaseException
	 */
	public boolean link( KeyType key, DataType data ) throws DatabaseException {

		this.checkKey( key );
		this.checkData( data );
		
		DatabaseEntry deKey = new DatabaseEntry();
		keyBinding.objectToEntry( key, deKey );
		DatabaseEntry deData = new DatabaseEntry();
		dataBinding.objectToEntry( data, deData );
		OperationStatus ret = this.getForwardDB().putNoOverwrite( null, deKey, deData );// this will auto put in
																						// secondary also
		// if ( OperationStatus.KEYEXIST == ret ) {
		// RunTime.bug(
		// "this is supposed to make a new unexisting key->data pair, apparently it failed!"
		// );
		// }
		return OperationStatus.KEYEXIST == ret;
	}
	
	private void checkData( DataType data ) {

		RunTime.assumedNotNull( data );
		// 1of3
		if ( data.getClass() != dataClass ) {
			RunTime.badCall( "shouldn't allow subclass of dataClass!! or else havoc" );
		}
	}
	
	private void checkKey( KeyType key ) {

		RunTime.assumedNotNull( key );
		// shouldn't allow subclass of keyClass!! or else havoc, well data loss
		// since TupleBinding treats it as Base class, so assuming the subclass
		// has new fields they won't be stored/retreived from DB
		// 1of3
		if ( key.getClass() != keyClass ) {
			RunTime.badCall( "shouldn't allow subclass of keyClass!! or else havoc" );
		}
	}
	
	/**
	 * @param data
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public KeyType getKey( DataType data ) throws DatabaseException {

		this.checkData( data );
		
		// 2of3
		DatabaseEntry deData = new DatabaseEntry();
		dataBinding.objectToEntry( data, deData );
		
		DatabaseEntry deKey = new DatabaseEntry();
		DatabaseEntry pKey = new DatabaseEntry();
		// deData=new DatabaseEntry(data.getBytes(BerkeleyDB.ENCODING));
		OperationStatus ret;
		ret = this.getBackwardDB().get( null, deData, pKey, deKey, null );
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		RunTime.assumedTrue( deData.equals( deKey ) );
		
		// 3of3
		KeyType key = keyBinding.entryToObject( pKey );
		// should not be null here
		RunTime.assumedNotNull( key );
		this.checkKey( key );
		return key;// Level1_Storage_BerkeleyDB.entryToString( pKey );
	}
	
	/**
	 * @param string
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public DataType getData( KeyType key ) throws DatabaseException {

		this.checkKey( key );
		
		// 2of3
		DatabaseEntry deKey = new DatabaseEntry();
		keyBinding.objectToEntry( key, deKey );
		// Level1_Storage_BerkeleyDB.stringToEntry( key, deKey );
		

		DatabaseEntry deData = new DatabaseEntry();
		OperationStatus ret;
		ret = this.getForwardDB().get( null, deKey, deData, null );
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		
		// 3of3
		DataType data = dataBinding.entryToObject( deData );
		// should not be null here
		RunTime.assumedNotNull( data );
		this.checkData( data );
		return data;// Level1_Storage_BerkeleyDB.entryToString( deData );
	}
	
	@Override
	protected void done( MethodParams params ) {

		Log.entry( "deinit OneToOneDBMap: " + dbName );
		boolean one = false;
		boolean two = false;
		
		// we don't have to set these to null, because they can be getDB() again
		if ( null != backwardDB ) {
			// backwardDB.deInit();
			Factory.deInit( backwardDB );// first close this
			one = true;
		}
		if ( null != forwardDB ) {
			// forwardDB.deInit();
			Factory.deInit( forwardDB );// then this
			two = true;
		}
		
		if ( one != two ) {
			RunTime.bug( "they should both be the same value, otherwise one of "
					+ "backwardDB and forwardDB was open and the other closed "
					+ "and we should've had both open always" );
		} else {
			if ( one ) {
				Log.warn( "close called on a not yet inited/open database" );
			}
		}
	}
	
	@Override
	protected void start( MethodParams params ) {

		RunTime.assumedNull( params );
	}
	

}
