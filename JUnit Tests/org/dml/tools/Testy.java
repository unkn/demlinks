/**
 * File creation: Oct 20, 2009 12:36:58 AM
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



import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
public class Testy extends StaticInstanceTracker {
	
	String	home;
	
	public void show() {

		System.out.println( this.getName() + " shows home=" + home );
	}
	
	public String getHome() {

		return home;
	}
	
	public static Testy getNew() {

		Testy t = new Testy();
		
		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.homeDir, "one/" + new Object() );
		t.init( params );
		params.deInit();
		
		return t;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done( MethodParams<Object> params ) {

		System.out.println( this.getName() + " is done." );
		home = null;
	}
	
	/**
	 * @return
	 */
	public String getName() {

		return this.getClass().getName();
	}
	
	@Override
	protected void start( MethodParams<Object> params ) {

		System.out.println( this.getName() + " start()" );
		RunTime.assumedNotNull( params );
		RunTime.assumedTrue( params.size() > 0 );
		home = params.getExString( PossibleParams.homeDir );
	}
	
}
