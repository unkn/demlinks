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



import org.dml.tools.RunTime;
import org.references.Reference;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class MainLevel2 extends MainLevel1 {
	
	private VarLevel2							var2;
	// true if we did new var2
	// private final boolean defaultVar = false;
	protected static final MethodParams<Object>	temporaryLevel1Params	= new MethodParams<Object>();
	
	public MainLevel2() {

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.temporary.tests.MainLevel1#initMainLevel(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void initMainLevel( MethodParams<Object> params ) {

		if ( null == params ) {
			// using defaults for this MainLevel1
			params = defaults;
		}
		RunTime.assertNotNull( params );
		
		// optional:
		Reference<Object> ref = params.get( PossibleParams.varLevelAll );
		VarLevel2 varL2;
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			// maybe use some defaults ie. homeDir value to default
			RunTime.assertTrue( null == var2 );
			varL2 = new VarLevel2();
			
			Reference<Object> ref2 = params.get( PossibleParams.homeDir );
			if ( null == ref2 ) {
				// home not specified, using default
				varL2.init( "defaultHomeDir" );
			} else {
				// home was specified
				varL2.init( (String)ref2.getObject() );
			}
			initedVL = true;
			
			// set this for Level1
			synchronized ( temporaryLevel1Params ) {
				temporaryLevel1Params.set( PossibleParams.varLevelAll, varL2 );
				// defaultVar = true;
				super.initMainLevel( temporaryLevel1Params );
			}
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel2 ) ) {
				RunTime.BadCallError( "wrong type passed" );
			}
			varL2 = (VarLevel2)obj;
			// defaultVar = false;
			super.initMainLevel( params );
		}
		
		var2 = varL2;
		
		// if ( defaultVar ) {
		// params.remove( PossibleParams.varLevelAll );
		// }
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.temporary.tests.MainLevel1#initLevel1(org.temporary.tests.VarLevel1)
	 */
	// @Override
	// public void initLevel1( VarLevel1 varL1 ) {
	//
	// super.initLevel1( varL1 );
	// try {
	// var2 = (VarLevel2)var;
	// } catch ( ClassCastException cce ) {
	// throw new BadCallError(
	// "wrong method called, use initLevelX for the same X level" );
	// }
	// }
	
	/**
	 * 
	 */
	public void showHome() {

		var2.showHome();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel1#done()
	 */
	@Override
	protected void done() {

		super.done();
		var2 = null;
	}
}
