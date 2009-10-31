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


package org.references.method;



import org.dml.tools.RunTime;
import org.references.ChainedReference;
import org.references.ListOfObjects;
import org.references.Position;
import org.references.Reference;



/**
 * 
 * a list of all parameters passed to a method<br>
 * supposedly formed of objects which are references to real instances like
 * String<br>
 * you may have the same object twice, acting as two different parameters, but
 * this object is in fact a referent to the real instance which is ie. String,
 * 
 * same ParamName cannot have two objects in the same MethodParams list<br>
 */
public class MethodParams<T> {
	
	// a list of instances ie. String, Integer, or even null(s) which can repeat
	// ie. A==B
	// objects of this list are the values
	private final ListOfObjects<T>	listOfParams	= new ListOfObjects<T>();
	
	
	protected int size() {

		return listOfParams.size();
	}
	
	/**
	 * this method will search for paramName and return it's value Object<br>
	 * 
	 * @param paramName
	 * @return null if not found; use .getObject() to get the value
	 */
	public Reference<T> get( ParamName<T> paramName ) {

		// what this does is get the list paramName and intersect it with
		// the MethodParams list and should find 0 or 1 elements in common, if
		// more than 1 then maybe throw BadCallError or Bug
		
		RunTime.assertNotNull( paramName );
		
		int foundCounter = 0;// should not exceed 1
		Reference<T> found = null;
		// parse listOfParams and check each element(the reference of each)
		// against ParamName list
		ChainedReference<T> iter = listOfParams.getRefAt( Position.FIRST );
		while ( null != iter ) {
			// paramName list can have only 1 reference from a MethodParams
			if ( paramName.contains( iter ) ) {
				foundCounter++;
				found = iter;// don't clone; new Reference<T>( iter );// clone
				// we could do a break; here but we want to make sure that it's
				// not found more than 1 times, that would mean Bug
				// can't have the same ParamName listed twice in the same params
				// list for same method
			}
			// go next
			iter = listOfParams.getRefAt( Position.AFTER, iter );
		}
		RunTime.assertTrue( foundCounter <= 1 );
		return found;
	}
	
	/**
	 * @param paramName
	 * @param value
	 *            can be null or an object that was already used as a parameter
	 *            one or more times
	 */
	public void set( ParamName<T> paramName, T value ) {

		RunTime.assertNotNull( paramName );
		
		Reference<T> ref = this.get( paramName );
		if ( null == ref ) {
			ref = listOfParams.addFirst( value );
			paramName.add( ref );
		} else {// already exists, must change
			ref.setObject( value );
		}
		
	}
	
	/**
	 * easy cast wrapper
	 * 
	 * @param paramName
	 * @return
	 */
	public String getString( ParamName<T> paramName ) {

		RunTime.assertNotNull( paramName );
		Reference<T> ref = this.get( paramName );
		if ( null == ref ) {
			return null;
		} else {
			return (String)ref.getObject();
		}
	}
}
