/**
 * File creation: Oct 27, 2009 6:20:06 PM
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


package org.references.method;



import org.dml.tools.RunTime;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Reference;



/**
 * elements of this list don't point to instances like String or null<br>
 * elements of this list point to references from another list<br>
 *each object in this list is a Reference<Object> from one or more
 * {MethodParams ListOfObjects<Object> list}
 */
public class ParamName<T> {
	
	// all the values that this ParamName has, which means each value is in a
	// different MethodParams instance
	ListOfUniqueNonNullObjects<Reference<T>>	listOfValues	= new ListOfUniqueNonNullObjects<Reference<T>>();
	
	/**
	 * 
	 * you're attaching this ParamName to a value pointed to by
	 * <code>refToValue</code>
	 * 
	 * @param refToValue
	 */
	protected void add( Reference<T> refToValue ) {

		RunTime.assumedNotNull( refToValue );
		RunTime.assumedFalse( listOfValues.containsObject( refToValue ) );
		listOfValues.addFirst( refToValue );
	}
	
	/**
	 * @param refToValue
	 * @return true if existed; either way after call it's removed
	 */
	protected boolean remove( Reference<T> refToValue ) {

		RunTime.assumedNotNull( refToValue );
		return listOfValues.removeObject( refToValue );
	}
	
	/**
	 * @param refToValue
	 * @return
	 */
	protected boolean contains( Reference<T> refToValue ) {

		return listOfValues.containsObject( refToValue );
	}
	
}
