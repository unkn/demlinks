/**
 * File creation: Oct 31, 2009 7:28:10 AM
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


package org.temporary.tests;



import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class MethodParamsTest {
	
	MethodParams	mp;
	ParamName		p1, p2, p3;
	
	@Before
	public void setUp() {

		p1 = new ParamName();
		p2 = new ParamName();
		p3 = new ParamName();
		mp = new MethodParams();
	}
	
	@Test
	public void test1() {

		String s1 = "s1";
		int i3 = 10;
		mp.set( p1, s1 );
		mp.set( p2, null );
		mp.set( p3, i3 );
		
		assertTrue( null == mp.get( p2, true ) );
		assertTrue( i3 == (int)mp.get( p3, true ) );
		assertTrue( s1 == mp.get( p1, true ) );
	}
}
