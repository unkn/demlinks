/**
 * File creation: Oct 23, 2009 8:41:50 AM
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



import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class MainLevel1 {
	
	protected VarLevel1	var	= null;
	
	public MainLevel1() {

	}
	
	/**
	 * @param var1
	 *            must be already .init() -ed
	 */
	public void initLevel1( VarLevel1 varL1 ) {

		RunTime.assertNotNull( varL1 );
		if ( !( varL1 instanceof VarLevel1 ) ) {
			RunTime.BadCallError( "wrong type passed" );
		}
		var = varL1;
	}
	
	/**
	 * using defaults
	 */
	public void initLevel1() {

		this.initLevel1( new VarLevel1() );
	}
	
	public void do1() {

		var.sayHello();
	}
	
	/**
	 * @return
	 */
	public String getName() {

		return this.getClass().getName();
	}
}
