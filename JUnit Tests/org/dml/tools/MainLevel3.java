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


package org.dml.tools;



import org.dml.tools.VarLevel;
import org.references.method.MethodParams;
import org.temporary.tests.PossibleParams;



/**
 * 
 *
 */
public class MainLevel3 extends MainLevel2 {
	
	@VarLevel
	private VarLevel3	var3;
	
	public MainLevel3() {

		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#getDefaults()
	 */
	@Override
	protected MethodParams<Object> getDefaults() {

		MethodParams<Object> ret = super.getDefaults();
		
		// the following will overwrite prev param set in Level2
		ret.set( PossibleParams.homeDir, "level3HOMEDir" );
		
		return ret;
	}
	
}
