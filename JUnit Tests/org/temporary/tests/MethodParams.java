/**
 * File creation: Oct 27, 2009 3:48:48 AM
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



import org.references.ListOfObjects;



/**
 * 
 * a list of all parameters passed to a method<br>
 * supposedly formed of objects which are references to real instances like
 * String<br>
 * you may have the same object twice, acting as two different parameters, but
 * this object is in fact a referent to the real instance which is ie. String,
 * so
 */
public class MethodParams {
	
	// the param values, not their name
	// can't have same ref twice, but 2 diff refs can point to same object
	private ListOfObjects<Object>	listOfParams;
	
	/**
	 * this method will search for paramName and return it's value Object<br>
	 * 
	 * @param paramName
	 * @param throwIfNotFound
	 * @return
	 */
	public Object get( Object paramName, boolean throwIfNotFound ) {

		// TODO what this does is get the list paramName and intersect it with
		// the MethodParams list and should find 0 or 1 elements in common, if
		// more than 1 then maybe throw BadCallError
		return null;
	}
}
