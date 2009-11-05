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
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
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

public class MainLevel1 extends MainLevel0 {
	
	
	private VarLevel1	var1	= null;
	
	

	public MainLevel1() {

		super();
	}
	
	
	@Override
	public void initMainLevel( MethodParams<Object> params ) {

		super.initMainLevel( this.internalInit( var1, params ) );
	}
	
	
	@Override
	protected void setVarLevelX( Object toValue ) {

		var1 = (VarLevel1)toValue;
	}
	
	@Override
	protected Object newVarLevelX() {

		var1 = new VarLevel1();
		return var1;
	}
	
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof VarLevel1 ) ) {
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel0#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return var1;
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
	 * @see org.temporary.tests.MainLevel0#start()
	 */
	@Override
	protected void start() {

		System.out.println( this.getName() + " Initing..." );
		super.start();
	}
	
	/**
	 * @return the var
	 */
	public VarLevel1 junitGetVar() {

		RunTime.assertTrue( var1 == this.getVarLevelX() );
		return (VarLevel1)this.getVarLevelX();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.MainLevel0#done()
	 */
	@Override
	protected void done() {

		System.out.println( this.getName() + " deiniting..." );
		super.done();
	}
}
