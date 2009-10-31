/**
 * File creation: Oct 29, 2009 4:19:51 PM
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


/**
 * 
 *
 */
public class LateStaticBindings {
	
	
	public static void main( String[] args ) {

		A a = new A();
		B b = new B();
		A.test();
	}
}



class A {
	
	public static void who() {

		System.out.println( "A" );
	}
	
	public static void test() {

		who();// B.who();
	}
}



class B extends A {
	
	public static void who() {

		System.out.println( "B" );
	}
}
