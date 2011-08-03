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



import org.dml.JUnits.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.method.*;



/**
 * 
 *
 */
public class SymbolsTest {
	
	Symbol					n1, n2, n3;
	Level010_DMLEnvironment	dml;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		final MethodParams params = MethodParams.getNew();
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// dml = new Level010_DMLEnvironment();
		// dml.init( params );
		dml = Factory.getNewInstanceAndInit( Level010_DMLEnvironment.class, params );
		// params.deInit();
		// Factory.deInit( params );
	}
	
	
	@After
	public void tearDown() {
		
		if ( null != dml ) {
			Factory.deInitIfAlreadyInited( dml );
		}
		// dml.deInit();
	}
	
	
	@Test
	public void test1() {
		
		n1 = dml.ensureSymbol( JavaID.ensureJavaIDFor( "A" ) );
		n2 = dml.ensureSymbol( JavaID.ensureJavaIDFor( "B" ) );
		
		System.out.println( n1 + "!" + n2 );
		
	}
}
