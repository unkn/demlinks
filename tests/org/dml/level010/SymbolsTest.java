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



package org.dml.level010;



import org.dml.JUnits.Consts;
import org.dml.storagewrapper.StorageException;
import org.dml.tracking.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class SymbolsTest
{
	
	Symbol					n1, n2, n3;
	Level010_DMLEnvironment	dml;
	
	
	@Before
	public
			void
			setUp()
					throws StorageException
	{
		
		MethodParams params = MethodParams.getNew();
		params.set(
					PossibleParams.homeDir,
					Consts.BDB_ENV_PATH );
		params.set(
					PossibleParams.jUnit_wipeDB,
					true );
		params.set(
					PossibleParams.jUnit_wipeDBWhenDone,
					true );
		// dml = new Level010_DMLEnvironment();
		// dml.init( params );
		dml = Factory.getNewInstanceAndInit(
												Level010_DMLEnvironment.class,
												params );
		// params.deInit();
		// Factory.deInit( params );
	}
	

	@After
	public
			void
			tearDown()
	{
		
		Factory.deInit( dml );
		// dml.deInit();
	}
	

	@Test
	public
			void
			test1()
					throws StorageException
	{
		
		n1 = dml.ensureSymbol( JavaID.ensureJavaIDFor( "A" ) );
		n2 = dml.ensureSymbol( JavaID.ensureJavaIDFor( "B" ) );
		
		System.out.println( n1 + "!" + n2 );
		
	}
}
