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
import org.references.Reference;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class MainLevel1 {
	
	private VarLevel1				var			= null;
	protected MethodParams<Object>	defaults	= null;
	
	public MainLevel1() {

	}
	
	/**
	 * @return default parameters for this Level
	 */
	protected MethodParams<Object> getDefaults() {

		if ( null == defaults ) {
			defaults = new MethodParams<Object>();
		}
		return defaults;
	}
	
	/**
	 * @param params
	 */
	public void initMainLevel( MethodParams<Object> params ) {

		if ( null == params ) {
			// using defaults for this MainLevel1
			params = this.getDefaults();
		}
		RunTime.assertNotNull( params );
		
		// optional:
		Reference<Object> ref = params.get( PossibleParams.varLevelAll );
		VarLevel1 varL1;
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			varL1 = new VarLevel1();
			varL1.init();
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel1 ) ) {
				RunTime.BadCallError( "wrong type passed" );
			}
			varL1 = (VarLevel1)obj;
		}
		
		var = varL1;
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
