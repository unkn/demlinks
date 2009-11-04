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
import org.javapart.logger.Log;
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
	protected static MethodParams<Object>		defaults				= null;
	
	public MainLevel2() {

	}
	
	protected MethodParams<Object> getDefaults() {

		if ( null == defaults ) {
			defaults = new MethodParams<Object>();
		}
		
		defaults.set( PossibleParams.homeDir, "level2defaultHOME" );
		
		return defaults;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.temporary.tests.MainLevel1#initMainLevel(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void initMainLevel( MethodParams<Object> params ) {

		MethodParams<Object> refToParams = params;
		if ( null == refToParams ) {
			// empty means use defaults
			refToParams = emptyParamList;
		}
		RunTime.assertNotNull( refToParams );
		


		// optional:
		Reference<Object> ref = refToParams.get( PossibleParams.varLevelAll );
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			// maybe use some defaults ie. homeDir value to default
			// RunTime.assertTrue( null == var2 );
			if ( null == var2 ) {
				var2 = new VarLevel2();// 1 // TODO don't set to null on deInit
			}
			usingOwnVarLevel = true;// 2
			

			// TODO avoid new-ing this every time; clone does the new
			MethodParams<Object> moo = this.getDefaults().getClone();
			moo.mergeWith( refToParams, true );
			// TODO mix with defaults overwriting with params
			var2.init( moo );// 3
			moo.clear();
			
			// set this for Level1
			synchronized ( temporaryLevel1Params ) {
				temporaryLevel1Params.set( PossibleParams.varLevelAll, var2 );
			}
			refToParams = temporaryLevel1Params;
		} else {
			if ( usingOwnVarLevel ) {
				Log.warn( "lost old instance" );
				usingOwnVarLevel = false;
			}
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel2 ) ) {
				RunTime.badCall( "wrong type passed" );
			}
			// varL2 = (VarLevel2)obj;
			var2 = (VarLevel2)obj;
		}
		


		super.initMainLevel( refToParams );
	}
	
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

		if ( !usingOwnVarLevel ) {// first
			var2 = null;
		}
		super.done();// second
	}
}
