/**
 * File creation: Oct 24, 2009 9:41:38 PM
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

import org.dml.error.BadCallError;
import org.dml.tools.Encapsulated;
import org.junit.Test;



/**
 * 
 *
 */
public class ObjectListerTest {
	
	ObjectLister		l, l2, l3;
	LimitedObjectLister	lol;
	
	@Test
	public void test1() {

		l = new ObjectLister();
		String s = "one";
		Encapsulated<String> es = new Encapsulated<String>();
		es.encapsulateThis( s );
		l.addFirst( s );
		l.append( es );
		
		String s2 = "two";
		String s3 = "three";
		l2 = new ObjectLister();
		l3 = new ObjectLister();
		l2.addFirst( s2 );
		l3.addFirst( s3 );
		
		lol = new LimitedObjectLister( 2 );
		lol.addFirst( s );
		lol.addFirst( s2 );
		boolean bced = false;
		try {
			lol.append( s3 );
		} catch ( BadCallError bce ) {
			bced = true;
		} finally {
			assertTrue( bced );
		}
		
	}
	
	
	@Test
	public void test2() {

		StringToJIDLink a = new StringToJIDLink();
		JID j = new JID();
		String str = "strONe";
		j.encapsulateThis( str );
		a.setFirst( str );
		a.setSecond( j );
		System.out.println( a.toString() );
	}
}
