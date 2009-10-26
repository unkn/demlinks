/**
 * File creation: Oct 23, 2009 8:43:20 AM
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
import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class MainLevel2 extends MainLevel1 {
	
	private VarLevel2	var2;
	
	public MainLevel2() {

	}
	
	
	public void initLevel2() {

		// maybe use some defaults ie. homeDir value to default
		this.initLevel2( "defaultHomeDir" );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel1#init(org.temporary.tests.VarLevel1)
	 */
	public void initLevel2( VarLevel2 varL2 ) {

		RunTime.assertNotNull( varL2 );
		if ( !( varL2 instanceof VarLevel2 ) ) {
			RunTime.BadCallError( "wrong type passed" );
		}
		// var2 = varL2;
		this.initLevel1( varL2 );
	}
	
	/**
	 * particularized init to support private variable (ie. inside the class)
	 * 
	 * @param homeDir
	 */
	public void initLevel2( String homeDir ) {

		VarLevel2 var21 = new VarLevel2();
		// var2 = (VarLevel2)var1;
		var21.init( homeDir );
		this.initLevel1( var21 );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.temporary.tests.MainLevel1#initLevel1(org.temporary.tests.VarLevel1)
	 */
	@Override
	public void initLevel1( VarLevel1 varL1 ) {

		super.initLevel1( varL1 );
		try {
			var2 = (VarLevel2)var;
		} catch ( ClassCastException cce ) {
			throw new BadCallError(
					"wrong method called, use initLevelX for the same X level" );
		}
	}
	
	/**
	 * 
	 */
	public void showHome() {

		var2.showHome();
	}
}
