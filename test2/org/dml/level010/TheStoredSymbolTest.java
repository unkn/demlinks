/**
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
 * 
 * 
 * File creation: Sep 9, 2010 2:11:38 PM
 */


package org.dml.level010;



import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;



/**
 * 
 *
 */
public class TheStoredSymbolTest {
	
	@Test
	public void testEqualityBetweenDifferentInstancesWithSameContents_akaLong() {
		final Long l = new Long( 123012l );
		final TheStoredSymbol tss = TheStoredSymbol.getNew( l );// instance1
		TheStoredSymbol.junitClearCache();
		final TheStoredSymbol tss2 = TheStoredSymbol.getNew( l );// instance2 different than instance1
		final TheStoredSymbol tss3 = TheStoredSymbol.getNew( l );// instance2 yes same as instance2
		// all instances have same contents ergo .equals and hashCode should report same values in this session
		assertTrue( tss != tss2 );
		assertTrue( tss.equals( tss2 ) );
		assertTrue( tss2.equals( tss ) );
		assertTrue( tss.hashCode() == tss2.hashCode() );
		final HashSet<TheStoredSymbol> hm = new HashSet<TheStoredSymbol>();
		assertTrue( hm.size() == 0 );
		assertTrue( hm.add( tss ) );
		assertTrue( hm.size() == 1 );
		assertFalse( hm.add( tss2 ) );
		assertTrue( hm.size() == 1 );
		
		assertTrue( tss != tss3 );
		assertTrue( tss2 == tss3 );
		assertTrue( tss.equals( tss3 ) );
		assertTrue( tss2.equals( tss3 ) );
		assertTrue( tss3.equals( tss ) );
		assertTrue( tss3.equals( tss2 ) );
		assertTrue( tss.hashCode() == tss2.hashCode() );
		assertTrue( tss.hashCode() == tss3.hashCode() );
		assertFalse( hm.add( tss3 ) );
		assertTrue( hm.size() == 1 );
		
		hm.clear();
		// test for places swap
		assertTrue( hm.add( tss2 ) );
		assertTrue( hm.size() == 1 );
		assertFalse( hm.add( tss ) );
		assertTrue( hm.size() == 1 );
		assertFalse( hm.add( tss3 ) );
		assertTrue( hm.size() == 1 );
		hm.clear();
		assertTrue( hm.add( tss3 ) );
		assertTrue( hm.size() == 1 );
		assertFalse( hm.add( tss ) );
		assertTrue( hm.size() == 1 );
		assertFalse( hm.add( tss2 ) );
		assertTrue( hm.size() == 1 );
		
		assertTrue( tss.getLong() == l );
		assertTrue( tss2.getLong() == l );
		assertTrue( tss3.getLong() == l );
	}
}
