/**
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


package org.dml.tools;


/**
 * these mustn't be disabled, because they're sometimes calling methods as
 * parameters<br>
 * uhm... nvm, it seems the methods in params will always get executed
 */
public class RunTime {
	
	public static void Bug() {

		Bug( "Bug detected." );
	}
	
	public static void Bug( String msg ) {

		throw new BugError( "Bug detected: " + msg );
	}
	
	public static void assertTrue( boolean b ) {

		if ( !b ) {
			throw new AssertionError( "expected true condition was false!" );
		}
	}
	
	public static void assertNotNull( Object... obj ) {

		for ( int i = 0; i < obj.length; i++ ) {
			if ( null == obj[i] ) {
				throw new AssertionError( "expected non-null object["
						+ ( i + 1 ) + "] was null!" );
			}
		}
	}
	
	public static void assertFalse( boolean b ) {

		if ( b ) {
			throw new AssertionError( "expected false condition was true!" );
		}
		
	}
	
}
