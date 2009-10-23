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
import org.dml.tools.StaticInstanceTracker;



/**
 * 
 *
 */
public class MainLevel1 extends StaticInstanceTracker implements
		VarLevel1Interface {
	
	private VarLevel1Interface	var	= null;
	
	public MainLevel1() {

	}
	
	protected void VarNew() {

		var = new VarLevel1();
	}
	
	protected void VarInit( VarLevel1Interface var1 ) {

		// var1 = new VarLevel1();
		RunTime.assertNotNull( var1 );
		var = var1;
		var.init();
	}
	
	protected void VarDeInit() {

		var.deInit();
	}
	
	@Override
	public void start() {

		this.VarNew();
		this.VarInit( var );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		this.VarDeInit();
	}
}
