/**
 * File creation: May 29, 2009 11:36:12 PM
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



import java.util.HashSet;

import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class StaticInADynamicClass {
	
	final static HashSet<String>	all		= new HashSet<String>();
	private String					current	= null;
	
	public StaticInADynamicClass( String what ) {

		RunTime.assumedNotNull( what );
		current = what;
		all.add( current );
	}
	
	public String getCurrent() {

		return current;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return this.getCurrent() + " (" + super.toString() + ")";
	}
}
