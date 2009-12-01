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


package org.dml.database.bdb.level1;



import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTracker;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;



/**
 * encapsulates the DatabaseConfig and the Database objects into one<br>
 * also makes sure the database isn't open unless it's needed<br>
 * once opened it stays open until silentClose() is called<br>
 */
public class DatabaseCapsule extends StaticInstanceTracker {
	
	private String						dbName;
	private Database					db		= null;
	private DatabaseConfig				dbConf	= null;
	private Level1_Storage_BerkeleyDB	bdbL1;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done( MethodParams<Object> params ) {

		if ( null != db ) {
			db = bdbL1.closePriDB_silent( db );
		}
	}
	
	/**
	 * @param string
	 */
	public DatabaseCapsule() {

		super();
	}
	
	/**
	 * @param params
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		// compulsory
		bdbL1 = (Level1_Storage_BerkeleyDB)params.getEx( PossibleParams.level1_BDBStorage );
		RunTime.assumedNotNull( bdbL1 );
		
		// compulsory
		dbName = params.getExString( PossibleParams.dbName );
		RunTime.assumedNotNull( dbName );
		RunTime.assumedFalse( dbName.isEmpty() );
		
		// dbConf is optional / can be null
		Reference<Object> ref = params.get( PossibleParams.priDbConfig );
		if ( null != ref ) {
			dbConf = (DatabaseConfig)ref.getObject();
		} else {
			dbConf = null;// use BDB defaults
		}
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	public Database getDB() throws DatabaseException {

		RunTime.assumedTrue( this.isInited() );
		if ( null == db ) {
			// first time init:
			db = bdbL1.openAnyDatabase( dbName, dbConf );
			RunTime.assumedNotNull( db );
		}
		return db;
	}
	

}
