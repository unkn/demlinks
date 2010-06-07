/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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
public class MainLevel2 extends MainLevel1 {
	
	@VarLevel
	private VarLevel2	var2;
	
	// true if we did new var2
	// private final boolean defaultVar = false;
	

	public MainLevel2() {

		super();
		
	}
	
	@Override
	protected MethodParams<Object> getDefaults() {

		MethodParams<Object> def = super.getDefaults();
		def.set( PossibleParams.homeDir, "level2defaultHOME" );
		
		return def;
	}
	
	
	/**
	 * 
	 */
	public void showHome() {

		var2.showHome();
		this.do1();
	}
	
}
