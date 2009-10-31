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


package org.references.method;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
	ParamName<Object>		compulsoryParam1	= new ParamName<Object>();
	ParamName<Object>		compulsoryParam2	= new ParamName<Object>();
	ParamName<Object>		optionalParam1		= new ParamName<Object>();
	
	@Test
	public void caller() {

		MethodParams<Object> params = new MethodParams<Object>();
		
		assertTrue( 0 == params.size() );
		params.set( compulsoryParam1, null );
		assertTrue( 1 == params.size() );
		params.set( compulsoryParam2, null );
		assertTrue( 2 == params.size() );
		this.someMethod1( params );
	}
	
	public void someMethod1( MethodParams<Object> allParams ) {

		assertNull( allParams.get( optionalParam1 ) );
		
		assertNotNull( allParams.get( compulsoryParam1 ) );
		assertNull( allParams.get( compulsoryParam1 ).getObject() );
		
		assertNotNull( allParams.get( compulsoryParam2 ) );
		assertNull( allParams.get( compulsoryParam2 ).getObject() );
	}
	
	@Before
	public void setUp() {

		paramString1 = new ParamName<Object>();
		paramNull2 = new ParamName<Object>();
		paramInteger3 = new ParamName<Object>();
		paramBoolean4 = new ParamName<Object>();
		mp1 = new MethodParams<Object>();
		mp2 = new MethodParams<Object>();
		mp3 = new MethodParams<Object>();
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
		
		mp1.set( paramNull2, string1 );
		Object o = mp1.get( paramNull2 ).getObject();
		assertTrue( string1 == o );
	}
	
	@Test
	public void testMulti() {

		String s1 = "s1";
		String s2 = "s2";
		String s3 = "s3";
		mp1.set( paramString1, s1 );
		mp2.set( paramString1, s2 );
		mp3.set( paramString1, s3 );
		
		assertTrue( s1 == mp1.get( paramString1 ).getObject() );
		assertTrue( s2 == mp2.get( paramString1 ).getObject() );
		assertTrue( s3 == mp3.get( paramString1 ).getObject() );
		
		mp1.set( paramString1, null );
		

		assertNull( mp1.get( paramString1 ).getObject() );
		assertTrue( s2 == mp2.get( paramString1 ).getObject() );
		assertTrue( s3 == mp3.get( paramString1 ).getObject() );
		


		mp2.set( paramString1, null );
		assertNull( mp2.get( paramString1 ).getObject() );
		assertNull( mp1.get( paramString1 ).getObject() );
		assertTrue( s3 == mp3.get( paramString1 ).getObject() );
		

		mp3.set( paramString1, null );
		assertNull( mp2.get( paramString1 ).getObject() );
		assertNull( mp1.get( paramString1 ).getObject() );
		assertNull( mp3.get( paramString1 ).getObject() );
	}
}
