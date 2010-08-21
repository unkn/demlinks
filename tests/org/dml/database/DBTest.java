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



package org.dml.database;



import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.javapart.logger.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class DBTest
{
	
	Level1_Storage_BerkeleyDB	bdb;
	
	
	@Before
	public
			void
			setUp()
					throws Exception
	{
		
		Log.entry();
		// System.out.println( "setUp:" );
		// StaticInstanceTracker
		MethodParams params = MethodParams.getNew();
		// params = Factory.getNewInstanceAndInitWithoutParams( MethodParams.class );
		
		params.set(
					PossibleParams.homeDir,
					Consts.BDB_ENV_PATH );
		params.set(
					PossibleParams.jUnit_wipeDB,
					false );
		params.set(
					PossibleParams.jUnit_wipeDBWhenDone,
					true );
		// RunTime.thro( new Exception( "testy" ) );
		bdb = Factory.getNewInstanceAndInit(
												Level1_Storage_BerkeleyDB.class,
												params );
		// bdb._deInit();
		// Factory.deInit( params );
		//
		// Factory.reinit( params );
		// params.reInit();
		// params.deInit();
		
		// bdb = new Level1_Storage_BerkeleyDB();
		// params = MethodParams.getNew();
		// params.init( null );
		
		// bdb.init( params );
		// params.deInit();
		Log.exit();
	}
	

	@After
	public
			void
			tearDown()
	{
		
		// System.out.println( "tearDown:" );
		// RunTime.clearThrowChain();
		Log.entry();
		// bdb.deInit();
		Factory.deInitIfAlreadyInited( bdb );
		// Factory.tzt();
		
		// throw new RuntimeException();
		// try {
		// Factory.deInitAll();
		// } finally {
		// / Factory.tzt();
		// }
		
		// RunTime.clearThrowChain();
		Log.exit();
	}
	

	@Test
	public
			void
			testInitDeInit()
	{
		
		// @Before and @After kicking in;
	}
	

	@Test
	public
			void
			testReInit()
	{
		
		Factory.deInit( bdb );
		Factory.reInit_aka_InitAgain_WithOriginalPassedParams( bdb );
		RunTime.throwAllThatWerePosponed();
	}
	

	@Test
	public
			void
			testRestart()
	{
		
		Factory.restart_aka_DeInitAndInitAgain_WithOriginalPassedParams( bdb );
	}
	

}
