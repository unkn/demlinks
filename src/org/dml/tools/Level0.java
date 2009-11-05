/**
 * File creation: Nov 5, 2009 9:07:12 PM
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



import org.references.Reference;
import org.references.method.MethodParams;
import org.temporary.tests.PossibleParams;



/**
 * 
 *
 */
public abstract class Level0 extends StaticInstanceTrackerWithMethodParams {
	
	private static MethodParams<Object>	defaults			= null;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean						usingOwnVarLevel	= false;
	
	// var to see if we used init() instead of initMainLevel(...); its only
	// purpose is to prevent init() usage
	private boolean						inited				= false;
	
	
	/**
	 * uses: PossibleParams.varLevelAll
	 * 
	 * @see org.dml.tools.StaticInstanceTrackerWithMethodParamsInterface#init(org
	 *      .references.method.MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		// allows null argument
		MethodParams<Object> mixedParams = this.getDefaults().getClone();
		if ( null != params ) {
			mixedParams.mergeWith( params, true );// prio on passed params
		}
		try {
			Reference<Object> ref = mixedParams.get( PossibleParams.varLevelAll );
			if ( null == ref ) {
				// not specified own storage
				if ( null == this.getVarLevelX() ) {
					this.newVarLevelX();
					// storageL1 = new Level2_BerkeleyDBStorage();
				}
				this.getVarLevelX().init( mixedParams );
				usingOwnVarLevel = true;
			} else {
				Object obj = ref.getObject();
				RunTime.assertNotNull( obj );
				this.checkVarLevelX( obj );
				this.setVarLevelX( obj );
				// it's already inited by caller (assumed)
			}
			inited = true;
			super.init();
		} finally {
			mixedParams.deInit();
		}
	}
	
	protected MethodParams<Object> getDefaults() {

		if ( null == defaults ) {
			defaults = new MethodParams<Object>();
			defaults.init();
			// FIXME: when's this deInited, true it doesn't really have to
			// deInit
		}
		return defaults;
	}
	
	abstract protected StaticInstanceTrackerWithMethodParams getVarLevelX();
	
	abstract protected void newVarLevelX();
	
	abstract protected void setVarLevelX( Object toValue );
	
	/**
	 * override this only in base class, ie. once
	 * 
	 * @param obj
	 */
	abstract protected void checkVarLevelX( Object obj );
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		if ( !inited ) {
			// called init() which is not supported
			RunTime.badCall( "please don't use init() w/o params" );
			// this.initMainLevel( null );this won't work, init() recursion
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		inited = false;// first
		
		if ( null != this.getVarLevelX() ) {
			// could be not yet inited due to throws in initMainLevel()
			if ( usingOwnVarLevel ) {
				// we inited it, then we deinit it
				usingOwnVarLevel = false;// 1 //this did the trick
				this.getVarLevelX().deInit();// 2
				// not setting it to null, since we might use it on the next
				// call
			} else {
				this.setVarLevelX( null );
			}
		}
	}
	

}
