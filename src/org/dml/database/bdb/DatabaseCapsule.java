/**
 * File creation: Jun 1, 2009 2:01:51 PM
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
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;



/**
 * encapsulates the DatabaseConfig and the Database objects into one<br>
 * also makes sure the database isn't open unless it's needed<br>
 * once opened it stays open until silentClose() is called<br>
 */
public class DatabaseCapsule {
	
	private String				dbName;
	private Database			db		= null;
	private DatabaseConfig		dbConf	= null;
	private final BerkeleyDB	bdb;
	
	/**
	 * @param string
	 */
	public DatabaseCapsule( BerkeleyDB bdb1, String dbName1,
			DatabaseConfig dbConf1 ) {

		RunTime.assertNotNull( bdb1 );
		RunTime.assertNotNull( dbName1 );
		RunTime.assertFalse( dbName1.isEmpty() );
		
		bdb = bdb1;
		dbName = dbName1;
		dbConf = dbConf1;// can be null
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	public Database getDB() throws DatabaseException {

		if ( null == db ) {
			// first time init:
			db = bdb.openAnyDatabase( null, dbName, dbConf );
			RunTime.assertNotNull( db );
		}
		return db;
	}
	
	@Override
	protected void finalize() throws Throwable {

		Log.exit( "in finalize() for dbName:" + dbName );
		if ( null != db ) {
			Log.thro( "the DB object was lost but the DB wasn't closed!" );
		}
		this.silentClose();
		db = null;
		dbConf = null;
		dbName = null;
		
		super.finalize();
	}
	
	/**
	 * 
	 */
	public void silentClose() {

		db = bdb.silentCloseAnyDB( db );// , dbName );
	}
	
}
