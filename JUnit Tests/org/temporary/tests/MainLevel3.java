/**
 * File creation: Oct 26, 2009 10:04:39 AM
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



import org.references.method.MethodParams;



/**
 * 
 *
 */
public class MainLevel3 extends MainLevel2 {
	
	private VarLevel3					var3;
	private final MethodParams<Object>	defaults	= new MethodParams<Object>();
	
	public MainLevel3() {

	}
	
	public void initLevel3() {

		defaults.set( PossibleParams.homeDir, "defaultHomeDir" );
		// defaults.set(PossibleParams.varLevel3, );
		this.initLevel3( defaults );
	}
	
	public void initLevel3( MethodParams<Object> ap ) {

		// last param is saying it must exist(true), if not just throw exception
		var3 = (VarLevel3)ap.getEx( PossibleParams.varLevel3 );
		if ( null != var3 ) {
			this.initLevel2( var3 );
		} else {
			var3 = new VarLevel3();
			var3.init( ap.getExString( PossibleParams.homeDir ) );
		}
	}
	
}
