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
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;



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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.temporary.tests.MainLevel2#initMainLevel(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		super.init( this.preInit( params ) );
		
		// // should not modify contents of 'params';
		// MethodParams<Object> referenceToParams = params;
		// if ( null == referenceToParams ) {
		// // using defaults for this MainLevel1
		// referenceToParams = emptyParamList;
		// }
		// RunTime.assertNotNull( referenceToParams );
		//		
		//
		//
		// // optional:
		// Reference<Object> ref = referenceToParams.get(
		// PossibleParams.varLevelAll );
		// if ( null == ref ) {
		// // no VarLevel1 given thus must use defaults for VarLevel1
		// // maybe use some defaults ie. homeDir value to default
		// // RunTime.assertTrue( null == var3 );isn't always null
		// // here
		// if ( null == var3 ) {
		// // if we're here, this means we previously used init with
		// // default var, else we previously used a given var from params
		// var3 = new VarLevel3();// 1
		// }
		// usingOwnVarLevel = true;// 2
		//			
		// MethodParams<Object> moo = this.getDefaults().getClone();
		// moo.mergeWith( referenceToParams, true );
		// var3.init( moo );// 3
		// moo.deInit();
		// // TODO mix moo with temporaryLevel1Params ?? or not
		//			
		// synchronized ( temporaryLevel1Params ) {
		// temporaryLevel1Params.set( PossibleParams.varLevelAll, var3 );
		// referenceToParams = temporaryLevel1Params;
		// }
		// } else {
		// Object obj = ref.getObject();
		// RunTime.assertNotNull( obj );
		// if ( !( obj instanceof VarLevel3 ) ) {
		// RunTime.badCall( "wrong type passed" );
		// }
		// var3 = (VarLevel3)obj;
		// }
		//		
		// super.initMainLevel( referenceToParams );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#checkVarLevelX(java.lang.Object)
	 */
	@Override
	protected void checkVarLevelX( Object obj ) {

		
		if ( !( obj instanceof VarLevel3 ) ) {
			// cannot be under VarLevel3, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#newVarLevelX()
	 */
	@Override
	protected void newVarLevelX() {

		var3 = new VarLevel3();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#setVarLevelX(java.lang.Object)
	 */
	@Override
	protected void setAllVarLevelX( Object obj ) {

		var3 = (VarLevel3)obj;
		super.setAllVarLevelX( obj );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel2#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return var3;
	}
}
