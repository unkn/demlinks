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
import org.references.Reference;
import org.references.method.MethodParams;



/**
 * 1. can use initMainLevel and deInit multiple times
 * 2. can use initMainLevel(null) or with empty params to use defaults which
 * means own VarLevel variable inited/deInited with the MainLevel, and also can
 * pass parameters to the init of this VarLevel if it supports
 * 3. when using own VarLevel, this won't be new-ed on each call to init, unless
 * it's intermixed with a passed VarLevel then the first own VarLevel was
 * clearly forgotten
 * 4. can use supplied VarLevel, which must be inited/deInited by caller
 * 5. call to init() w/o params (not initMainLevel()) is prevented
 */
public class MainLevel1 extends StaticInstanceTracker {
	
	private VarLevel1						var1				= null;
	
	// defaults are no params, or no params means use defaults
	protected static MethodParams<Object>	emptyParamList		= null;
	
	// var to see if we used init() instead of initMainLevel(...)
	private boolean							inited				= false;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	protected boolean						usingOwnVarLevel	= false;
	
	public MainLevel1() {

		// since this is static:
		if ( null == emptyParamList ) {
			emptyParamList = new MethodParams<Object>();
			emptyParamList.init();
			// FIXME: when is this deInited? should be when last instance is
			// deInited, but can't compare class names, could be Level3 and
			// Level2 classes, but we can't deInit on last Level3.deInit
			// true that a deInit is not really needed, but as a concept...when?
		}
	}
	
	/**
	 * @param params
	 */
	public void initMainLevel( MethodParams<Object> params ) {

		if ( null == params ) {
			// using defaults for this MainLevel1
			params = emptyParamList;
		}
		RunTime.assertNotNull( params );
		
		// optional:
		Reference<Object> ref = params.get( PossibleParams.varLevelAll );
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			if ( null == var1 ) {
				var1 = new VarLevel1();// first
			}
			usingOwnVarLevel = true;// second
			var1.init();// third
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel1 ) ) {
				RunTime.badCall( "wrong type passed" );
			}
			var1 = (VarLevel1)obj;
		}
		
		inited = true;// first
		this.init();// second
	}
	
	public void do1() {

		var1.sayHello();
	}
	
	/**
	 * @return
	 */
	public String getName() {

		return this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		inited = false;// first
		System.out.println( this.getName() + " deiniting..." );
		if ( null != var1 ) {
			// could be not yet inited due to throws in initMainLevel()
			if ( usingOwnVarLevel ) {
				// we inited it, then we deinit it
				usingOwnVarLevel = false;// 1 //this did the trick
				var1.deInit();// 2
				// not setting it to null, since we might use it on the next
				// call
			} else {
				var1 = null;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		System.out.println( this.getName() + " Initing..." );
		if ( !inited ) {
			// called init() which is not supported
			RunTime.badCall( "please don't use init() w/o params" );
			// this.initMainLevel( null );this won't work, init() recursion
		}
	}
	
	/**
	 * @return the var
	 */
	public VarLevel1 junitGetVar() {

		return var1;
	}
}
