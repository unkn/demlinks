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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class MethodParamsTest {
	
	MethodParams<Object>	mp1, mp2, mp3;
	ParamName<Object>		paramString1, paramNull2, paramInteger3,
			paramBoolean4;
	
	@Before
	public void setUp() {

		paramString1 = new ParamName<Object>();
		paramNull2 = new ParamName<Object>();
		paramInteger3 = new ParamName<Object>();
		paramBoolean4 = new ParamName<Object>();
		mp1 = new MethodParams<Object>();
	}
	
	@Test
	public void test1() {

		String string1 = "s1";
		Integer integer3 = 10;
		Boolean boolean4 = true;
		mp1.set( paramString1, string1 );
		mp1.set( paramNull2, null );
		mp1.set( paramInteger3, integer3 );
		mp1.set( paramBoolean4, boolean4 );
		
		assertFalse( null == mp1.get( paramNull2 ) );
		assertTrue( null == mp1.get( paramNull2 ).getObject() );
		assertTrue( integer3 == (Integer)mp1.get( paramInteger3 ).getObject() );
		assertTrue( string1 == mp1.get( paramString1 ).getObject() );
		assertTrue( boolean4 == mp1.get( paramBoolean4 ).getObject() );
	}
}
