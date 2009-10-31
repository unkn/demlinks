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



import java.util.NoSuchElementException;

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
public class ListOfUniqueNonNullObjects<E> extends ListOfObjects<E> {
	
	public ListOfUniqueNonNullObjects() {

		super();
	}
	
	// /** unused, yet
	// * @param position only FIRST/LAST
	// * @return the object that was removed, or null is none was
	// */
	// public E removeObject(Position position) {
	// Debug.nullException(position);
	// Reference<E> nr = getNodeRefAt(position);
	// if (null != nr) {
	// E nod = nr.getObject();
	// if (removeRef(nr)) {
	// return nod;
	// }
	// }
	// return null;
	// }
	/**
	 * @param obj
	 * @return
	 */
	@Override
	public boolean containsObject( E obj ) {

		RunTime.assertNotNull( obj );
		return super.containsObject( obj );
	}
	
	/**
	 * @param obj
	 * @param index
	 *            0 based index
	 * @return true if obj is at index
	 */
	@Override
	public boolean containsObjectAtPos( E obj, int index ) {

		RunTime.assertNotNull( obj );
		return super.containsObjectAtPos( obj, index );
	}
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via
	 * this method
	 * 
	 * @param obj
	 * @return new reference to <tt>obj</tt>
	 */
	@Override
	public ChainedReference<E> newRef( E obj ) {

		RunTime.assertNotNull( obj );
		return super.newRef( obj );
	}
	
	/**
	 * @param obj
	 * @return null or the reference containing the obj
	 */
	@Override
	public ChainedReference<E> getRef( E obj ) {

		RunTime.assertNotNull( obj );
		return super.getRef( obj );
	}
	
	/**
	 * @param pos
	 *            only FIRST/LAST
	 * @return null or the object at specified position
	 */
	@Override
	public E getObjectAt( Position pos ) {

		RunTime.assertNotNull( pos );
		E obj = null;
		try {
			obj = super.getObjectAt( pos );
		} catch ( NoSuchElementException nsee ) {
			return null;
		}
		return obj;
	}
	
	/**
	 * @param index
	 *            0 based index
	 * @return null(aka not found) or the object at index
	 */
	@Override
	public E getObjectAt( int index ) {

		RunTime.assertNotNull( index );
		E obj = null;
		try {
			obj = super.getObjectAt( index );
		} catch ( NoSuchElementException nsee ) {
			return null;
		}
		return obj;
	}
	
	/**
	 * @param pos
	 *            BEFORE/AFTER...of...
	 * @param objPos
	 *            ...which object
	 * @return null or the object
	 */
	@Override
	public E getObjectAt( Position pos, E objPos ) {

		RunTime.assertNotNull( pos, objPos );
		E obj = null;
		try {
			obj = super.getObjectAt( pos, objPos );
		} catch ( NoSuchElementException nsee ) {
			return null;
		}
		return obj;
	}
	
	/**
	 * @param obj
	 *            that doesn't already exist; not null
	 * @return true if object already existed and wasn't re-added or moved to
	 *         end
	 */
	@Override
	public ChainedReference<E> addLast( E obj ) {

		RunTime.assertNotNull( obj );
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

		RunTime.assertNotNull( obj );
		
		ChainedReference<E> ref = this.getRef( obj );
		if ( null != ref ) {
			// already exists, not added/moved
			return ref;
		}
		return super.addFirst( obj );
	}
	
	/**
	 * @param node
	 * @param location
	 * @return ref to the object that existed, or was just added
	 */
	@Override
	public ChainedReference<E> insert( E obj, Position position ) {

		RunTime.assertNotNull( obj, position );
		switch ( position ) {
		case FIRST:
			return this.addFirst( obj );
		case LAST:
			return this.addLast( obj );
		default:
			throw new AssertionError( "undefined location here." );
		}
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
		return super.insert( newObj, pos, posObj );
	}
	
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	@Override
	public boolean removeObject( E obj ) {

		RunTime.assertNotNull( obj );
		return super.removeObject( obj );
	}
	
	// TODO add replace methods
}
