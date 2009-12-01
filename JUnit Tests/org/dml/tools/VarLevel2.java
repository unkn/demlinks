/**
 * File creation: Oct 23, 2009 8:43:36 AM
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



import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class VarLevel2 extends VarLevel1 implements VarLevel2Interface {
	
	String	homeDir;
	
	// public void init( String homeDir1 ) {
	//
	// homeDir = homeDir1;
	// inited = true;
	// super.init();
	// }
	
	public void showHome() {

		RunTime.assumedTrue( this.isInited() );
		System.out.println( this.getName() + "'s home is: " + homeDir );
	}
	
	
	/**
	 * @param params
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		RunTime.assumedNotNull( params );
		super.start( params );
		homeDir = params.getExString( PossibleParams.homeDir );
		
	}
}
