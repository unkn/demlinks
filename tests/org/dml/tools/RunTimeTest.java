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
 * File creation: Jul 1, 2010 11:06:04 PM
 */


package org.dml.tools;



import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * tests here are supposed to fail, it's supposed to show how exceptions are thrown and shown in eclipse<br>
 * 
 */
public class RunTimeTest {
	
	@Before
	public void setUp() {

		RunTime.clearThrow();
	}
	
	@After
	public void tearDown() {

		RunTime.clearThrow();
	}
	
	@Test
	public void testChainedException1() {

		// this will successfully chain all 3 throwables and eclipse will show them on the exact lines they happen
		try {
			RunTime.thro( new Error( "err" ) );
		} catch ( Throwable e ) {
			try {
				throw new IOException( "rogue java method that we cannot control throws this" );
			} catch ( Throwable f ) {
				RunTime.thro( f ); // mustn't chain these like: new Exception( f ) );
			}
		} finally {
			RunTime.thro( new Exception( "final" ) );
		}
	}
	
	@Test
	public void testChainedException2() {

		// this will not show the Error one
		try {
			throw new Error( "err1" );// this gets overridden by IOException below, which is java normal
		} catch ( Throwable e ) {
			try {
				throw new IOException( "rogue java method that we cannot control throws this" );
			} catch ( Throwable f ) {
				RunTime.thro( f ); // mustn't chain these like: new Exception( f ) );
			}
		} finally {
			RunTime.thro( new Exception( "final" ) );
		}
	}
	
	@Test
	public void testChainedException3() {

		// this only shows the first and last throwed ones
		try {
			RunTime.thro( new Error( "err" ) );
		} catch ( Throwable e ) {
			e.printStackTrace();
			RunTime.thro( e );// this will not be shown
		} finally {
			RunTime.thro( new Exception( "final" ) );
		}
	}
	
	@Test
	public void testChainedException4() throws Throwable {

		// this will successfully chain all 3 throwables and eclipse will show them on the exact lines they happen
		try {
			RunTime.thro( new Error( "err" ) );
		} catch ( Throwable e ) {
			RunTime.thro( new RuntimeException( "rte", e ) );// and this is chained
		} finally {
			RunTime.thro( new Exception( "final" ) );
		}
	}
}
