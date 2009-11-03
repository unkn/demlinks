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



import org.dml.tools.RunTime;
import org.references.Reference;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class MainLevel3 extends MainLevel2 {
	
	private VarLevel3	var3;
	
	public MainLevel3() {

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.temporary.tests.MainLevel2#initMainLevel(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void initMainLevel( MethodParams<Object> params ) {

		// TODO: should not modify contents of 'params'; maybe clone?
		MethodParams<Object> temporaryParams = params;
		if ( null == temporaryParams ) {
			// using defaults for this MainLevel1
			temporaryParams = defaults;
		}
		RunTime.assertNotNull( temporaryParams );
		


		// optional:
		Reference<Object> ref = temporaryParams.get( PossibleParams.varLevelAll );
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			// maybe use some defaults ie. homeDir value to default
			RunTime.assertTrue( null == var3 );
			var3 = new VarLevel3();// 1
			usingOwnVarLevel = true;// 2
			var3.init( temporaryParams );// 3
			
			synchronized ( temporaryLevel1Params ) {
				temporaryLevel1Params.set( PossibleParams.varLevelAll, var3 );
				temporaryParams = temporaryLevel1Params;
			}
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel3 ) ) {
				RunTime.badCall( "wrong type passed" );
			}
			var3 = (VarLevel3)obj;
		}
		
		super.initMainLevel( temporaryParams );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#done()
	 */
	@Override
	protected void done() {

		if ( !usingOwnVarLevel ) {// first
			var3 = null;
		}
		
		super.done();// second
	}
}
