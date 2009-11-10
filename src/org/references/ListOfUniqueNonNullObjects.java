/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
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


package org.references;



import org.dml.tools.RunTime;



/**
 * handles the RefsList list at the Object level ie. accepting only Object
 * parameters where Object is the type of element stored in list; that Object is
 * E DOES NOT allow adding of NULL or DUPlicate Objects<br>
 * DUPlicate objects are those that == OR .equals(), in other words if two
 * different objects ie. a != b, have same contents ie. a.equals(b) == true then
 * if a is already in list, b can't be added (it already exists as a)<br>
 * for that to work, you need to override .equals() or else it defaults to ==
 */
public class ListOfUniqueNonNullObjects<E> extends ListOfNonNullObjects<E> {
	
	public ListOfUniqueNonNullObjects() {

		super();
	}
	
	

	/**
	 * @param obj
	 *            that doesn't already exist; not null
	 * @return the ref
	 */
	@Override
	public ChainedReference<E> addLast( E obj ) {

		RunTime.assertNotNull( obj );// redundant
		ChainedReference<E> ref = this.getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			return ref;
		}
		return super.addLast( obj );
	}
	
	/**
	 * @param obj
	 *            that doesn't already exist; not allowing null
	 * @return the ref to the object, if it existed; or the new ref if it didn't
	 *         exist
	 */
	@Override
	public ChainedReference<E> addFirst( E obj ) {

		RunTime.assertNotNull( obj );// redundant
		
		ChainedReference<E> ref = this.getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			return ref;
		}
		return super.addFirst( obj );
	}
	
	/**
	 * @param obj
	 *            not null, not already existing;
	 * @return true if existed and nothing was changed; false if it didn't
	 *         exist, but it does now
	 */
	public boolean addFirstQ( E obj ) {

		RunTime.assertNotNull( obj );
		
		ChainedReference<E> ref = this.getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			return true;
		}
		RunTime.assertNotNull( super.addFirst( obj ) );
		
		return false;// didn't exist
	}
	
	/**
	 * @param newObj
	 * @param pos
	 * @param posObj
	 * @return true if newObj already exists, and nothing is done with it<br>
	 *         false is all went according to call
	 */
	@Override
	public boolean insert( E newObj, Position pos, E posObj ) {

		RunTime.assertNotNull( newObj, pos, posObj );
		ChainedReference<E> newRef = this.getRef( newObj );
		if ( null != newRef ) {
			// already exists, not added/moved
			return true;
		}
		
		return super.insert( newObj, pos, posObj );// false
	}
	

	// TODO add replace methods
}
