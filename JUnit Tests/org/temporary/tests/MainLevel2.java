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
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class MainLevel2 extends MainLevel1 {
	
	@VarLevel
	protected VarLevel2	var2;
	
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
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.temporary.tests.MainLevel1#initMainLevel(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void initMainLevel( MethodParams<Object> params ) {

		super.initMainLevel( this.internalInit( var2, params ) );
		// MethodParams<Object> refToParams = params;
		// if ( null == refToParams ) {
		// // empty means use defaults
		// refToParams = emptyParamList;
		// }
		// RunTime.assertNotNull( refToParams );
		//		
		//
		//
		// // optional param, but the top level will supply this if toplevel
		// exists
		// // or the user will supply this if it is so desired but he will be
		// // responsible for it being inited/deinited
		// Reference<Object> ref = refToParams.get( PossibleParams.varLevelAll
		// );
		// if ( null == ref ) {
		// // no VarLevel1 given thus must use defaults for VarLevel1
		// // maybe use some defaults ie. homeDir value to default
		// if ( null == var2 ) {
		// var2 = new VarLevel2();// 1
		// }
		// usingOwnVarLevel = true;// 2
		//			
		//
		// // TODO avoid new-ing this every time; clone does the new
		// MethodParams<Object> moo = this.getDefaults().getClone();
		// // using defaults but overwriting them with params
		// moo.mergeWith( refToParams, true );
		// var2.init( moo );// 3
		// moo.deInit();
		//			
		// // set this for Level1
		// synchronized ( temporaryLevel1Params ) {
		// temporaryLevel1Params.set( PossibleParams.varLevelAll, var2 );
		// }
		// refToParams = temporaryLevel1Params;
		// } else {
		// Object obj = ref.getObject();
		// RunTime.assertNotNull( obj );
		// if ( !( obj instanceof VarLevel2 ) ) {
		// // cannot be under VarLevel2, can be above tho
		// RunTime.badCall( "wrong type passed" );
		// }
		// var2 = (VarLevel2)obj;
		// }
		//		
		
		// this.internalInit2of2();
		// super.initMainLevel( refToParams );
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
	 * @see org.temporary.tests.MainLevel1#checkVarLevelX(java.lang.Object)
	 */
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof VarLevel2 ) ) {
			// cannot be under VarLevel2, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel1#newVarLevelX()
	 */
	@Override
	protected Object newVarLevelX() {

		var2 = new VarLevel2();
		return var2;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel1#setVarLevelX(java.lang.Object)
	 */
	@Override
	protected void setVarLevelX( Object obj ) {

		var2 = (VarLevel2)obj;
		super.setVarLevelX( obj );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel1#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return var2;
	}
}
