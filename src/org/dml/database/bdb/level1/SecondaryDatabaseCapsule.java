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
import org.javapart.logger.Log;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;



/**
 * encapsulates the SecondaryConfig and the SecondaryDatabase objects into one<br>
 * also makes sure the database isn't open unless it's needed<br>
 * once opened it stays open until silentClose() is called<br>
 */
public class SecondaryDatabaseCapsule extends StaticInstanceTracker {
	
	private String						secDbName;
	private SecondaryDatabase			secDb	= null;
	private SecondaryConfig				secDbConf;
	private Database					primaryDb;
	private Level1_Storage_BerkeleyDB	bdbL1;
	
	/**
	 * @param string
	 */
	public SecondaryDatabaseCapsule() {

		super();
	}
	
	/**
	 * @param params
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		// compulsory
		bdbL1 = (Level1_Storage_BerkeleyDB)params.getEx( PossibleParams.level1_BDBStorage );
		RunTime.assertNotNull( bdbL1 );
		
		// compulsory
		secDbName = params.getExString( PossibleParams.dbName );
		RunTime.assertNotNull( secDbName );
		RunTime.assertFalse( secDbName.isEmpty() );
		
		// compulsory
		primaryDb = (Database)params.getEx( PossibleParams.priDb );
		RunTime.assertNotNull( primaryDb );
		
		// dbConf is optional / can be null
		Reference<Object> ref = params.get( PossibleParams.secDbConfig );
		if ( null != ref ) {
			secDbConf = (SecondaryConfig)ref.getObject();
		} else {
			secDbConf = null;// use BDB defaults
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		this.silentClose();
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	public SecondaryDatabase getSecDB() throws DatabaseException {

		if ( null == secDb ) {
			// first time init:
			secDb = bdbL1.openAnySecDatabase( secDbName, primaryDb, secDbConf );
			RunTime.assertNotNull( secDb );
			// Runtime.getRuntime().addShutdownHook(null); bad idea:
			// concurrently called
		}
		return secDb;
	}
	
	@Override
	protected void finalize() throws Throwable {

		Log.entry( "in finalize() for secDbName:" + secDbName );
		this.silentClose();
		super.finalize();
	}
	
	/**
	 * 
	 */
	public void silentClose() {

		Log.entry();
		secDb = bdbL1.silentCloseAnySecDB( secDb );
	}
	
}
