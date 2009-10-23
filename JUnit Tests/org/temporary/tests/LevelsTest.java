/**
 * File creation: Oct 23, 2009 9:45:39 AM
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



import org.dml.error.BadCallError;
import org.junit.Test;



/**
 * 
 *
 */
public class LevelsTest {
	
	MainLevel1	ml1;
	MainLevel2	ml2;
	
	@Test
	public void test1() {

		ml1 = new MainLevel1();
		ml2 = new MainLevel2();
		
		VarLevel1 v1 = new VarLevel1();
		v1.init();
		
		ml1.init( v1 );
		
		try {
			
			VarLevel2 v2 = new VarLevel2();
			try {
				v2.init();
			} catch ( BadCallError bce ) {
				v2.deInit();
				v2.init( "zHOME" );
			}
			
			ml2.init( v2 );
			
			ml1.sayHello();
			ml2.sayHello();
			ml2.showHome();
			
		} finally {
			ml1.silentDeInit();
			ml2.silentDeInit();
		}
	}
}
