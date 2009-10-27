/**
 * File creation: Oct 24, 2009 12:08:08 PM
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


package org.references;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;



/**
 * 
 *
 */
public class ReferenceTest {
	
	Reference<String>	es;
	Reference<String>	ess, ess2;
	static String		str		= "stuff";
	final String		str2	= "stuffy";
	String				str3;
	
	

	@Test
	public void test1() {

		es = new Reference<String>();
		
		es.setObject( str );
		assertTrue( str == es.getObject() );
		

	}
	
	@SuppressWarnings( "unchecked" )
	@Test
	public void testStream() throws IOException, ClassNotFoundException {

		ess = new Reference<String>();
		ess.setObject( str );
		assertTrue( str == ess.getObject() );
		
		File randomFile = File.createTempFile( "JUnit", "test", new File( "."
				+ File.separator ) );
		FileOutputStream fos = new FileOutputStream( randomFile );
		ObjectOutputStream out = new ObjectOutputStream( fos );
		out.writeObject( ess );
		out.close();
		
		FileInputStream fis = new FileInputStream( randomFile );
		ObjectInputStream in = new ObjectInputStream( fis );
		ess2 = null;
		ess2 = (Reference<String>)in.readObject();
		in.close();
		randomFile.delete();
		assertNotNull( ess2 );
		assertTrue( str.equals( ess2.getObject() ) );
		assertTrue( ess.equalsByContent( ess2 ) );
		assertTrue( ess.hashCode() == ess2.hashCode() );
	}
	
	@Test
	public void testEmpty() {

		Reference<String> one, two;
		one = new Reference<String>();
		two = new Reference<String>();
		assertTrue( one.equalsByContent( two ) );
		assertTrue( one.getObject() == two.getObject() );
		assertNull( one.getObject() );
		

		assertTrue( one.hashCode() == one.hashCode() );
		assertFalse( one.hashCode() == two.hashCode() );
		

		// same string objects and thus contents
		assertTrue( one.equals( one ) );
		one.setObject( str );
		assertTrue( one.equals( one ) );
		assertFalse( one.equals( two ) );
		assertTrue( one.hashCode() == one.hashCode() );
		assertFalse( one.hashCode() == two.hashCode() );
		
		two.setObject( str );
		assertTrue( one.hashCode() == two.hashCode() );
		assertTrue( one.equalsByContent( two ) );
		assertFalse( one == two );
		two.setObject( null );
		assertNull( two.getObject() );
		
		// same string content but different string objects:
		assertFalse( str == str2 );
		str3 = str2.substring( 0, str2.length() - 1 );
		assertFalse( str == str3 );
		assertFalse( str2 == str3 );
		assertTrue( str.equals( str3 ) );
		assertTrue( str.hashCode() == str3.hashCode() );
		two.setObject( str3 );
		assertTrue( two.getObject() == str3 );
		assertTrue( one != two );
		assertTrue( one.equalsByContent( two ) );
		assertTrue( one.hashCode() == two.hashCode() );
		
		// this tests the list
		ListOfObjects<Reference<String>> list = new ListOfObjects<Reference<String>>();
		assertFalse( list.addFirst( one ) );
		assertFalse( list.addLast( two ) );// false= it was added
		assertTrue( one != two );
		assertTrue( one.equalsByContent( two ) );// same content
		assertFalse( one.equals( two ) );// different references
		assertTrue( list.size() == 2 );
	}
}
