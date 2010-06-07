/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



package org.references.method;



import org.dml.tools.RunTime;



/**
 * just an ID that will uniquely identify a specific parameter<br>
 * its value is not stored here<br>
 */
public class ParamName {
	
	private final String	selfName;	// only for debugging purposes!
										
	/**
	 * dummy constructor, private to prevent init
	 */
	private ParamName() {

		selfName = null;
	};
	
	private ParamName( String name ) {

		RunTime.assumedNotNull( name );
		selfName = name;
	}
	
	// FIXME: throw if 'name' already used when calling getNew! which means keep static list of all names
	public static ParamName getNew( String name ) {

		return new ParamName( name );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return this.getClass().getCanonicalName() + ":" + selfName;
	}
	
}
