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
import org.dml.tracking.Log;
import org.references.Reference;
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
 * 
 * @param <KeyType>
 * @param <DataType>
 */
public class OneToOneDBMap<KeyType, DataType>
		extends
		Initer
{
	
	private final Class<KeyType>			keyClass;
	private final Class<DataType>			dataClass;
	
	private final EntryBinding<KeyType>		keyBinding;
	private final EntryBinding<DataType>	dataBinding;
	
	private static final String				secPrefix	= "secondary";
	private DatabaseCapsule					forwardDB	= null;
	private SecondaryDatabaseCapsule		backwardDB	= null;
	protected String						dbName;
	private Level1_Storage_BerkeleyDB		bdbL1;
	
	
	/**
	 * constructor
	 * 
	 * 
	 * @param keyClass1
	 * @param keyBinding1
	 * @param dataClass1
	 * @param dataBinding1
	 */
	public OneToOneDBMap(
			// Level1_Storage_BerkeleyDB bdb1,
			// String dbName1,
			Class<KeyType> keyClass1,
			EntryBinding<KeyType> keyBinding1,
			Class<DataType> dataClass1,
			EntryBinding<DataType> dataBinding1 )
	{
		
		// RunTime.assumedNotNull( bdb1 );
		// RunTime.assumedNotNull( dbName1 );
		// bdbL1 = bdb1;
		// dbName = dbName1;
		keyClass = keyClass1;
		dataClass = dataClass1;
		keyBinding = keyBinding1;// AllTupleBindings.getBinding( keyClass );
		dataBinding = dataBinding1;// AllTupleBindings.getBinding( dataClass );
	}
	

	
	@Override
	protected
			void
			start(
					MethodParams params )
	{
		
		RunTime.assumedNotNull( params );
		
		bdbL1 = (Level1_Storage_BerkeleyDB)params.getEx( PossibleParams.level1_BDBStorage );
		if ( null == bdbL1 )
		{
			RunTime.badCall( "missing parameter" );
		}
		RunTime.assumedNotNull( bdbL1 );
		
		dbName = params.getExString( PossibleParams.dbName );// used for forwardDB and backwardDB also
		RunTime.assumedNotNull( dbName );
		RunTime.assumedFalse( dbName.isEmpty() );
		
		// open both DBs
		MethodParams iParams = params.getClone();
		// must not already be set/passed to us, so null return if no prev value was set for priDbConfig param
		RunTime.assumedNull( iParams.set(
											PossibleParams.priDbConfig,
											new OneToOneDBConfig() ) );
		
		forwardDB = Factory.getNewInstanceAndInit(
													DatabaseCapsule.class,
													iParams );
		RunTime.assumedNotNull( forwardDB );
		RunTime.assumedTrue( forwardDB.isInitedSuccessfully() );
		
		// secondary db
		RunTime.assumedNotNull( iParams.set(
												PossibleParams.dbName,
												secPrefix
														+ dbName ) );
		// FIXME: investigate if only one new OneToOneSecondaryDBConfig() is needed for all "OneToOneDBMap"s same for
		// above
		RunTime.assumedNull( iParams.set(
											PossibleParams.secDbConfig,
											new OneToOneSecondaryDBConfig() ) );
		RunTime.assumedNull( iParams.set(
											PossibleParams.priDb,
											forwardDB.getDB() ) );
		RunTime.assumedTrue( iParams.remove( PossibleParams.priDbConfig ) );// not needed can leave it on though
		
		backwardDB = Factory.getNewInstanceAndInit(
													SecondaryDatabaseCapsule.class,
													iParams );
		RunTime.assumedNotNull( backwardDB );
		RunTime.assumedTrue( backwardDB.isInitedSuccessfully() );
	}
	

	@Override
	protected
			void
			done(
					MethodParams params )
	{
		
		Log.entry( "deinit OneToOneDBMap: "
					+ dbName );
		
		OneToXDBMapCommonCode.theDone(
										this.isInitedSuccessfully(),
										new Reference<Initer>(
																forwardDB ),
										new Reference<Initer>(
																backwardDB ) );
	}// done
	
	
	protected
			Level1_Storage_BerkeleyDB
			getBDBL1()
	{
		RunTime.assumedNotNull( bdbL1 );
		return bdbL1;
	}
	

	/**
	 * @return
	 * @throws DatabaseException
	 */
	private
			Database
			getForwardDB()
					throws DatabaseException
	{
		RunTime.assumedNotNull( forwardDB );
		return forwardDB.getDB();
	}
	

	/**
	 * @return
	 * @throws DatabaseException
	 */
	private
			SecondaryDatabase
			getBackwardDB()
					throws DatabaseException
	{
		RunTime.assumedNotNull( backwardDB );
		return backwardDB.getSecDB();
	}
	

	/**
	 * @param key
	 * @param data
	 * @return true if already existed
	 */
	public
			boolean
			link(
					KeyType key,
					DataType data )
	{
		
		this.checkKey( key );
		this.checkData( data );
		
		DatabaseEntry deKey = new DatabaseEntry();
		keyBinding.objectToEntry(
									key,
									deKey );
		DatabaseEntry deData = new DatabaseEntry();
		dataBinding.objectToEntry(
									data,
									deData );
		OperationStatus ret = this.getForwardDB().putNoOverwrite(
																	null,
																	deKey,
																	deData );// this will auto put in
																				// secondary also
		// if ( OperationStatus.KEYEXIST == ret ) {
		// RunTime.bug(
		// "this is supposed to make a new unexisting key->data pair, apparently it failed!"
		// );
		// }
		return OperationStatus.KEYEXIST == ret;
	}
	

	private
			void
			checkData(
						DataType data )
	{
		
		RunTime.assumedNotNull( data );
		// 1of3
		if ( data.getClass() != dataClass )
		{
			RunTime.badCall( "shouldn't allow subclass of dataClass!! or else havoc" );
		}
	}
	

	private
			void
			checkKey(
						KeyType key )
	{
		
		RunTime.assumedNotNull( key );
		// shouldn't allow subclass of keyClass!! or else havoc, well data loss
		// since TupleBinding treats it as Base class, so assuming the subclass
		// has new fields they won't be stored/retreived from DB
		// 1of3
		if ( key.getClass() != keyClass )
		{
			RunTime.badCall( "shouldn't allow subclass of keyClass!! or else havoc" );
		}
	}
	

	/**
	 * @param data
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public
			KeyType
			getKey(
					DataType data )
	{
		
		this.checkData( data );
		
		// 2of3
		DatabaseEntry deData = new DatabaseEntry();
		dataBinding.objectToEntry(
									data,
									deData );
		
		DatabaseEntry deKey = new DatabaseEntry();
		DatabaseEntry pKey = new DatabaseEntry();
		// deData=new DatabaseEntry(data.getBytes(BerkeleyDB.ENCODING));
		OperationStatus ret;
		ret = this.getBackwardDB().get(
										null,
										deData,
										pKey,
										deKey,
										null );
		if ( OperationStatus.SUCCESS != ret )
		{
			return null;
		}
		RunTime.assumedTrue( deData.equals( deKey ) );
		
		// RunTime.assumedNotNull(pKey);
		// 3of3
		KeyType key = keyBinding.entryToObject( pKey );
		// should not be null here
		RunTime.assumedNotNull( key );
		this.checkKey( key );
		return key;// Level1_Storage_BerkeleyDB.entryToString( pKey );
	}
	

	/**
	 * @param key
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public
			DataType
			getData(
						KeyType key )
	{
		
		this.checkKey( key );
		
		// 2of3
		DatabaseEntry deKey = new DatabaseEntry();
		keyBinding.objectToEntry(
									key,
									deKey );
		// Level1_Storage_BerkeleyDB.stringToEntry( key, deKey );
		

		DatabaseEntry deData = new DatabaseEntry();
		OperationStatus ret;
		ret = this.getForwardDB().get(
										null,
										deKey,
										deData,
										null );
		if ( OperationStatus.SUCCESS != ret )
		{
			return null;
		}
		
		// 3of3
		DataType data = dataBinding.entryToObject( deData );
		// should not be null here
		RunTime.assumedNotNull( data );
		this.checkData( data );
		return data;// Level1_Storage_BerkeleyDB.entryToString( deData );
	}
	


}
