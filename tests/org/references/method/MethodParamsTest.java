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



package org.references.method;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dml.tracking.Factory;
import org.junit.Before;
import org.junit.Test;
import org.references.Reference;



/**
 * 
 *
 */
public class MethodParamsTest
{
	
	MethodParams	mp1, mp2, mp3;
	ParamID			paramString1;
	ParamID			paramNull2, paramInteger3, paramBoolean4;
	ParamID			compulsoryParam1	= ParamID.getNew( "compulsoryParam1" );
	ParamID			compulsoryParam2	= ParamID.getNew( "compulsoryParam2" );
	ParamID			optionalParam1		= ParamID.getNew( "optionalParam1" );
	
	
	@Test
	public
			void
			caller()
	{
		
		MethodParams params = MethodParams.getNew();
		
		assertTrue( 0 == params.size() );
		params.set(
					compulsoryParam1,
					null );
		assertTrue( 1 == params.size() );
		params.set(
					compulsoryParam2,
					null );
		assertTrue( 2 == params.size() );
		this.someMethod1( params );
		
		// params.deInit();
		// MethodParams.doneWith( params );
		// Factory.deInit( params );
	}
	

	public
			void
			someMethod1(
							MethodParams allParams )
	{
		
		assertNull( allParams.get( optionalParam1 ) );
		
		assertNotNull( allParams.get( compulsoryParam1 ) );
		assertNull( allParams.get(
									compulsoryParam1 ).getObject() );
		
		assertNotNull( allParams.get( compulsoryParam2 ) );
		assertNull( allParams.get(
									compulsoryParam2 ).getObject() );
	}
	

	@Before
	public
			void
			setUp()
	{
		
		paramString1 = ParamID.getNew( "paramString1" );
		paramNull2 = ParamID.getNew( "paramNull2" );
		paramInteger3 = ParamID.getNew( "paramInteger3" );
		paramBoolean4 = ParamID.getNew( "paramBoolean4" );
		mp1 = MethodParams.getNew();
		mp2 = MethodParams.getNew();
		mp3 = MethodParams.getNew();
	}
	

	@Test
	public
			void
			test1()
	{
		
		String string1 = "s1";
		Integer integer3 = 10;
		Boolean boolean4 = true;
		mp1.set(
					paramString1,
					string1 );
		mp1.set(
					paramNull2,
					null );
		mp1.set(
					paramInteger3,
					integer3 );
		mp1.set(
					paramBoolean4,
					boolean4 );
		
		assertFalse( null == mp1.get( paramNull2 ) );
		assertTrue( null == mp1.get(
										paramNull2 ).getObject() );
		assertTrue( integer3 == (Integer)mp1.get(
													paramInteger3 ).getObject() );
		assertTrue( string1 == mp1.get(
										paramString1 ).getObject() );
		assertTrue( boolean4 == mp1.get(
											paramBoolean4 ).getObject() );
		
		mp1.set(
					paramNull2,
					string1 );
		Object o = mp1.get(
							paramNull2 ).getObject();
		assertTrue( string1 == o );
	}
	

	@Test
	public
			void
			testMulti()
	{
		
		String s1 = "s1";
		String s2 = "s2";
		String s3 = "s3";
		mp1.set(
					paramString1,
					s1 );
		mp2.set(
					paramString1,
					s2 );
		mp3.set(
					paramString1,
					s3 );
		
		assertTrue( s1 == mp1.get(
									paramString1 ).getObject() );
		assertTrue( s2 == mp2.get(
									paramString1 ).getObject() );
		assertTrue( s3 == mp3.get(
									paramString1 ).getObject() );
		
		mp1.set(
					paramString1,
					null );
		

		assertNull( mp1.get(
								paramString1 ).getObject() );
		assertTrue( s2 == mp2.get(
									paramString1 ).getObject() );
		assertTrue( s3 == mp3.get(
									paramString1 ).getObject() );
		


		mp2.set(
					paramString1,
					null );
		assertNull( mp2.get(
								paramString1 ).getObject() );
		assertNull( mp1.get(
								paramString1 ).getObject() );
		assertTrue( s3 == mp3.get(
									paramString1 ).getObject() );
		

		mp3.set(
					paramString1,
					null );
		assertNull( mp2.get(
								paramString1 ).getObject() );
		assertNull( mp1.get(
								paramString1 ).getObject() );
		assertNull( mp3.get(
								paramString1 ).getObject() );
	}
	

	@Test
	public
			void
			testClone()
	{
		
		mp1.set(
					paramString1,
					null );
		mp1.set(
					paramBoolean4,
					true );
		assertTrue( 2 == mp1.size() );
		MethodParams clone = mp1.getClone();
		assertTrue( 2 == clone.size() );
		Reference<Object> ref = mp1.get( paramString1 );
		Reference<Object> cref = clone.get( paramString1 );
		assertNotNull( ref );
		assertNotNull( cref );
		assertTrue( ref != cref );
		System.out.println( ref.getObject() + "!1" );
		System.out.println( cref.getObject() + "!2" );
		assertTrue( null == null );
		assertTrue( ref.getObject() == cref.getObject() );
		
		ref = mp1.get( paramBoolean4 );
		cref = clone.get( paramBoolean4 );
		assertTrue( ref != cref );
		assertTrue( ref.getObject() == cref.getObject() );
		
		assertTrue( mp1 != clone );
		
		mp1.set(
					paramNull2,
					null );
		assertTrue( mp1.size() == 3 );
		assertTrue( 2 == clone.size() );
		mp1.clear();
		clone.clear();
		
		assertTrue( mp1.size() == 0 );
		assertTrue( clone.size() == 0 );
	}
	

	@Test
	public
			void
			testMerge()
	{
		
		assertTrue( mp1.size() == 0 );
		mp1.set(
					paramNull2,
					null );
		String some = "some";
		mp1.set(
					compulsoryParam2,
					some );
		assertTrue( mp2.size() == 0 );
		
		mp2.mergeWith(
						mp1,
						false );
		assertTrue( mp2.size() == mp1.size() );
		
		assertTrue( mp3.size() == 0 );
		mp3.set(
					paramNull2,
					new Object() );
		assertNotNull( mp3.getEx( paramNull2 ) );
		assertNull( mp1.getEx( paramNull2 ) );
		
		mp2.clear();
		assertTrue( mp2.size() == 0 );
		mp2 = mp3.getClone();
		mp3.mergeWith(
						mp1,
						false );
		assertNotNull( mp3.getEx( paramNull2 ) );
		mp3.mergeWith(
						mp1,
						true );
		assertNull( mp3.getEx( paramNull2 ) );
		
		assertNotNull( mp2.getEx( paramNull2 ) );
		mp2.mergeWith(
						mp1,
						true );
		assertNull( mp2.getEx( paramNull2 ) );
	}
}
