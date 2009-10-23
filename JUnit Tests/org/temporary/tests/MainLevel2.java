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



/**
 * 
 *
 */
public class MainLevel2 extends MainLevel1 implements VarLevel2Interface {
	
	private VarLevel2Interface	var	= null;
	
	public MainLevel2() {

	}
	
	public void init( String homeDir ) {

		super.init();
	}
	
	@Override
	protected void VarNew() {

		var = new VarLevel2();
	}
	
	@Override
	protected void VarInit( VarLevel1Interface var1 ) {

		var = (VarLevel2Interface)var1;
		var.init( "mainLevel2homedir" );
	}
	
	@Override
	protected void VarDeInit() {

		if ( null != var ) {
			var.deInit();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.VarLevel2Interface#showHome()
	 */
	@Override
	public void showHome() {

		var.showHome();
		
	}
}
