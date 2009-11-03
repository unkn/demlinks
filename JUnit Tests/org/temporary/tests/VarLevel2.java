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


package org.temporary.tests;



import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class VarLevel2 extends VarLevel1 implements VarLevel2Interface {
	
	String			homeDir;
	private boolean	inited	= false;
	
	public void init( String homeDir1 ) {

		homeDir = homeDir1;
		inited = true;
		super.init();
	}
	
	@Override
	protected void start() {

		if ( !inited ) {
			RunTime.BadCallError( "please don't use init() w/o params" );
		}
		super.start();
	}
	
	public void showHome() {

		RunTime.assertTrue( inited );
		System.out.println( this.getName() + "'s home is: " + homeDir );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.VarLevel1#done()
	 */
	@Override
	protected void done() {

		inited = false;
		super.done();
	}
}
