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



package org.references;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class ObjRefsListTest {
	
	ListOfUniqueNonNullObjects<String>	ol;
	
	@Before
	public void setUp() {

		ol = new ListOfUniqueNonNullObjects<String>();
	}
	
	@After
	public void tearDown() {

		ol = null;
	}
	
	
	@Test
	public void test1() {

		assertTrue( ol.isEmpty() );
		assertFalse( ol.addFirstQ( "second" ) );
		assertFalse( ol.addFirstQ( "first" ) );
		ol.addLast( "last" );
		ol.insert( "antelast", Position.BEFORE, ol.getObjectAt( Position.LAST ) );
		ol.insert( "middle", Position.AFTER, ol.getObjectAt( 1 ) );
		String iter = ol.getObjectAt( Position.FIRST );
		while ( iter != null ) {
			System.out.println( iter );
			iter = ol.getObjectAt( Position.AFTER, iter );
		}
		String ante = "ante";
		String last = "laste".substring( 0, 4 );
		assertTrue( last.equals( ol.getObjectAt( Position.LAST ) ) );
		assertFalse( last == ol.getObjectAt( Position.LAST ) );
		assertTrue( ( ante + last ).equals( ante + last ) );
	}
}
