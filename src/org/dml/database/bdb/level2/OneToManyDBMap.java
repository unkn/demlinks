/**
 * File creation: Jun 4, 2009 7:38:27 PM
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


package org.dml.database.bdb.level2;



import org.dml.database.bdb.level1.DatabaseCapsule;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level1.OneToManyDBConfig;
import org.dml.error.BugError;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;



/**
 * -key and data are String
 * - one key, multiple data (but different data, within this key ie. no two
 * datums are equal [within a key])
 * - and we want to be able to lookup by either key or data, or both
 * 
 * - stored as two primary databases (because the primary can't have dup data
 * while associated with secondary, although secondary can have dup data)
 * - and we can't store these as secondary because we can only delete from
 * secondaries
 */
public class OneToManyDBMap {
	
	private static final String				backwardSuffix	= "_backward";
	private DatabaseCapsule					forwardDB		= null;
	private DatabaseCapsule					backwardDB		= null;
	private final String					dbName;
	private final Level1_Storage_BerkeleyDB	bdbL1;
	
	/**
	 * constructor
	 * 
	 * @param bdb1
	 * @param dbName1
	 */
	public OneToManyDBMap( Level1_Storage_BerkeleyDB bdb1, String dbName1 ) {

		RunTime.assertNotNull( bdb1 );
		RunTime.assertNotNull( dbName1 );
		bdbL1 = bdb1;
		dbName = dbName1;
	}
	
	protected Level1_Storage_BerkeleyDB getBDBL1() {

		return bdbL1;
	}
	
	/**
	 * @return
	 */
	public String getName() {

		return dbName;
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private Database getForwardDB() throws DatabaseException {

		if ( null == forwardDB ) {
			
			forwardDB = new DatabaseCapsule();
			MethodParams<Object> params = new MethodParams<Object>();
			params.init( null );
			params.set( PossibleParams.level1_BDBStorage, this.getBDBL1() );
			params.set( PossibleParams.dbName, dbName );
			params.set( PossibleParams.priDbConfig, new OneToManyDBConfig() );
			forwardDB.init( params );
			params.deInit();
			
			RunTime.assertNotNull( forwardDB );
		}
		return forwardDB.getDB();
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private Database getBackwardDB() throws DatabaseException {

		if ( null == backwardDB ) {
			MethodParams<Object> params = new MethodParams<Object>();
			backwardDB = new DatabaseCapsule();
			params.init( null );
			params.set( PossibleParams.level1_BDBStorage, this.getBDBL1() );
			params.set( PossibleParams.dbName, dbName + backwardSuffix );
			params.set( PossibleParams.priDbConfig, new OneToManyDBConfig() );
			backwardDB.init( params );
			params.deInit();
			// RunTime.assertNotNull( backwardDB );
		}
		return backwardDB.getDB();
	}
	
	/**
	 * @return null
	 */
	public OneToManyDBMap silentClose() {

		Log.entry( "closing OneToManyDBMap: " + dbName );
		
		if ( null != forwardDB ) {
			forwardDB.silentClose();
		}
		
		if ( null != backwardDB ) {
			backwardDB.silentClose();
		}
		

		return null;
	}
	
	
	/**
	 * notice that isGroup(first,second) is different than isGroup(second,first)<br>
	 * they are two different groups (order matters)
	 * 
	 * @param first
	 * @param second
	 * @return
	 * @throws DatabaseException
	 */
	public boolean isGroup( String first, String second )
			throws DatabaseException {

		RunTime.assertNotNull( first, second );
		return this.internal_isGroup( first, second );
	}
	
	/**
	 * @param first
	 * @param second
	 * @return
	 * @throws DatabaseException
	 */
	private boolean internal_isGroup( String first, String second )
			throws DatabaseException {

		// TODO: maybe a transaction here is unnecessary, however we don't want
		// another transaction (supposedly) to interlace between the two gets
		TransactionCapsule txc = TransactionCapsule.getNewTransaction( this.getBDBL1() );
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Level1_Storage_BerkeleyDB.stringToEntry( first, key );
		Level1_Storage_BerkeleyDB.stringToEntry( second, data );
		OperationStatus ret1, ret2;
		try {
			ret1 = this.getForwardDB().getSearchBoth( txc.get(), key, data,
					null );
			ret2 = this.getBackwardDB().getSearchBoth( txc.get(), data, key,
					null );
			if ( ( ( OperationStatus.SUCCESS == ret1 ) && ( OperationStatus.SUCCESS != ret2 ) )
					|| ( ( OperationStatus.SUCCESS != ret1 ) && ( OperationStatus.SUCCESS == ret2 ) ) ) {
				RunTime.bug( "one exists, the other doesn't; but should either both exist, or both not exist" );
			}
		} finally {
			txc.commit();// or abort
		}
		
		return ( OperationStatus.SUCCESS == ret1 );
	}
	
	/**
	 * make sure that group (first,second) exist<br>
	 * notice that order matters, thus (second, first) is another grouping<br>
	 * this is like a new that doesn't throw if the group already exists<br>
	 * 
	 * @param first
	 * @param second
	 * @return true if existed already; false if it didn't exist before call
	 * @throws DatabaseException
	 */
	public boolean ensureGroup( String first, String second )
			throws DatabaseException {

		RunTime.assertNotNull( first, second );
		boolean ret;
		ret = ( OperationStatus.KEYEXIST == this.internal_group( first, second ) );
		return ret;
	}
	
	/**
	 * @param first
	 * @param second
	 * @return OperationStatus.SUCCESS or KEYEXIST
	 * @throws DatabaseException
	 * @throws BugError
	 *             if inconsistency detected (ie. one link exists the other
	 *             doesn't)
	 */
	private OperationStatus internal_group( String first, String second )
			throws DatabaseException {

		RunTime.assertNotNull( first, second );
		TransactionCapsule txc = TransactionCapsule.getNewTransaction( this.getBDBL1() );
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		Level1_Storage_BerkeleyDB.stringToEntry( first, key );
		Level1_Storage_BerkeleyDB.stringToEntry( second, data );
		boolean commit = false;
		OperationStatus ret1, ret2;
		try {
			ret1 = this.getForwardDB().putNoDupData( txc.get(), key, data );
			ret2 = this.getBackwardDB().putNoDupData( txc.get(), data, key );
			if ( ( OperationStatus.SUCCESS == ret1 )
					&& ( OperationStatus.SUCCESS == ret2 ) ) {
				commit = true;
			} else {
				if ( ( ( OperationStatus.KEYEXIST == ret1 ) && ( OperationStatus.KEYEXIST != ret2 ) )
						|| ( ( OperationStatus.KEYEXIST != ret1 ) && ( OperationStatus.KEYEXIST == ret2 ) ) ) {
					RunTime.bug( "one link exists and the other does not" );
				}
			}
			
		} finally {
			if ( commit ) {
				txc.commit();
			} else {
				txc.abort();
			}
		}
		return ret1;
	}
}
