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


package org.temporary.tests;



import org.dml.tools.RunTime;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.Reference;



/**
 * elements of this list don't point to instances like String or null<br>
 * elements of this list point to references from another list<br>
 *each object in this list is a Reference<Object> from one or more
 * {MethodParams ListOfObjects<Object> list}
 */
public class ParamName {
	
	ListOfUniqueNonNullObjects<Reference<Object>>	listOfValues	= new ListOfUniqueNonNullObjects<Reference<Object>>();
	
	/**
	 * 
	 * you're attaching this ParamName to a value pointed to by
	 * <code>refToValue</code>
	 * 
	 * @param refToValue
	 * @return true if ref already existed in list and wasn't re-added or moved
	 *         to end
	 */
	public boolean add( Reference<Object> refToValue ) {

		RunTime.assertNotNull( refToValue );
		return listOfValues.addFirst( refToValue );
	}
	
	/**
	 * @param refToValue
	 * @return true if existed; either way after call it's removed
	 */
	public boolean remove( Reference<Object> refToValue ) {

		RunTime.assertNotNull( refToValue );
		return listOfValues.removeObject( refToValue );
	}
	
	/**
	 * @param value
	 *            object
	 * @return reference to that value, or null
	 */
	public Reference<Object> get( Object value ) {

		RunTime.assertNotNull( value );
		
		Reference<Object> iter = listOfValues.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			if ( value.equals( iter ) ) {
				return iter;
			}
			iter = listOfValues.getObjectAt( Position.AFTER, iter );
		}
		return null;
	}
}
