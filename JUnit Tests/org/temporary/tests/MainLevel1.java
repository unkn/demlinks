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
 * 
 *
 */
public class MainLevel1 extends StaticInstanceTracker {
	
	private VarLevel1							var			= null;
	
	// defaults are no params, or no params means use defaults
	protected static final MethodParams<Object>	defaults	= new MethodParams<Object>();
	
	// var to see if we used init() instead of initMainLevel(...)
	private boolean								inited		= false;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	protected boolean							initedVL	= false;
	
	public MainLevel1() {

	}
	
	/**
	 * @param params
	 */
	public void initMainLevel( MethodParams<Object> params ) {

		if ( null == params ) {
			// using defaults for this MainLevel1
			params = defaults;
		}
		RunTime.assertNotNull( params );
		
		// optional:
		Reference<Object> ref = params.get( PossibleParams.varLevelAll );
		VarLevel1 varL1;
		if ( null == ref ) {
			// no VarLevel1 given thus must use defaults for VarLevel1
			RunTime.assertTrue( null == var );
			varL1 = new VarLevel1();
			varL1.init();
			initedVL = true;
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			if ( !( obj instanceof VarLevel1 ) ) {
				RunTime.BadCallError( "wrong type passed" );
			}
			varL1 = (VarLevel1)obj;
		}
		
		var = varL1;
		inited = true;
		this.init();
	}
	
	public void do1() {

		var.sayHello();
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

		inited = false;
		System.out.println( this.getName() + " deiniting..." );
		if ( null != var ) {
			// could be not yet inited due to throws in initMainLevel()
			if ( initedVL ) {
				// we inited it, then we deinit it
				var.deInit();
				initedVL = false;
				
			}
			var = null;
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
			// called init()
			RunTime.Bug( "please don't use init() w/o params" );
			// this.initMainLevel( null );this won't work, init() recursion
		}
	}
	
	/**
	 * @return the var
	 */
	public VarLevel1 junitGetVar() {

		return var;
	}
}
