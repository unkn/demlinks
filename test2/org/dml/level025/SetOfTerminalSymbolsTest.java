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



package org.dml.level025;



import static org.junit.Assert.*;

import org.dml.error.*;
import org.dml.level010.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.junit.*;
import org.references.*;
import org.references.method.*;



/**
 * 
 *
 */
public class SetOfTerminalSymbolsTest {
	
	SetOfTerminalSymbols	set1;
	Symbol					sym;
	Level025_DMLEnvironment	env;
	
	
	@SuppressWarnings( "boxing" )
	@Before
	public void setUp() {
		
		final MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// env = new Level025_DMLEnvironment();
		// env.init( params );
		env = Factory.getNewInstanceAndInit( Level025_DMLEnvironment.class, params );
		// params.deInit();
		// Factory.deInit( params );
		
		sym = env.newUniqueSymbol();
		set1 = SetOfTerminalSymbols.getAsSet( env, sym );
	}
	
	
	@After
	public void tearDown() {
		
		if ( null != env ) {
			Factory.deInitIfAlreadyInited( env );
		}
		// env.deInit();
	}
	
	
	@Test
	public void test1() {
		
		assertTrue( set1.size() == 0 );
		
		final Symbol one = env.newUniqueSymbol();
		assertFalse( set1.addToSet( one ) );
		assertTrue( set1.size() == 1 );
		assertTrue( set1.addToSet( one ) );
		
		boolean threw = false;
		try {
			set1.addToSet( sym );
		} catch ( final Throwable t ) {
			if ( RunTime.isThisWrappedException_of_thisType( t, BadCallError.class ) ) {
				threw = true;
				RunTime.clearLastThrown_andAllItsWraps();
			}
		}
		assertTrue( threw );
		
		final Symbol two = env.newUniqueSymbol();
		assertTrue( two != one );
		assertFalse( set1.addToSet( two ) );
		assertTrue( set1.size() == 2 );
		
		final Symbol three = env.newUniqueSymbol();
		assertFalse( set1.addToSet( three ) );
		assertTrue( set1.size() == 3 );
		
		assertTrue( set1.getSide( Position.FIRST ) == one );
		
		assertNull( set1.getSideOf( Position.BEFORE, one ) );
		assertTrue( two == set1.getSideOf( Position.AFTER, one ) );
		assertTrue( three == set1.getSideOf( Position.AFTER, two ) );
		assertNull( set1.getSideOf( Position.AFTER, three ) );
		assertTrue( one == set1.getSideOf( Position.BEFORE, two ) );
		assertTrue( two == set1.getSideOf( Position.BEFORE, three ) );
		
		assertTrue( set1.remove( two ) );
		assertFalse( set1.hasSymbol( two ) );
		assertNull( set1.getSideOf( Position.BEFORE, two ) );
		assertNull( set1.getSideOf( Position.AFTER, two ) );
		assertNull( set1.getSideOf( Position.BEFORE, one ) );
		assertNull( set1.getSideOf( Position.AFTER, three ) );
		assertTrue( set1.getSideOf( Position.AFTER, one ) == three );
		assertTrue( set1.getSideOf( Position.BEFORE, three ) == one );
	}
}
