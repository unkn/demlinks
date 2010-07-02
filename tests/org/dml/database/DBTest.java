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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class DBTest {
	
	Level1_Storage_BerkeleyDB	bdb;
	
	@Before
	public void setUp() throws DatabaseException {

		// StaticInstanceTracker
		MethodParams params;
		params = Factory.getNewInstance( MethodParams.class );
		
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, false );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		
		bdb = Factory.getNewInstance( Level1_Storage_BerkeleyDB.class, params );
		Factory.deInit( params );
		// Factory.reinit( params );
		// params.reInit();
		// params.deInit();
		
		// bdb = new Level1_Storage_BerkeleyDB();
		// params = MethodParams.getNew();
		// params.init( null );
		
		// bdb.init( params );
		// params.deInit();
		
	}
	
	@After
	public void tearDown() {

		// bdb.deInit();
		Factory.deInit( bdb );
		RunTime.clearThrow();
	}
	
	@Test
	public void testInitDeInit() {

		// @Before and @After kicking in;
	}
	
	@Test
	public void testReInit() {

		Factory.deInit( bdb );
		Factory.reInit( bdb );
		
	}
	

}
