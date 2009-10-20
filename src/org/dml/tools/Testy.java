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



/**
 * 
 *
 */
public class Testy extends StaticInstanceTracker {
	
	String	home;
	
	public void show() {

		System.out.println( this.getName() + " shows home=" + home );
	}
	
	public boolean init( String home1 ) {

		home = home1;
		// if something throws before below init() ...
		super.init();
		return true;
	}
	
	public static Testy getNew() {

		Testy t = new Testy();
		t.init( "one/" + new Object() );
		return t;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		System.out.println( this.getName() + " is done." );
	}
	
	/**
	 * @return
	 */
	public String getName() {

		return this.getClass().getName();
	}
	
}
