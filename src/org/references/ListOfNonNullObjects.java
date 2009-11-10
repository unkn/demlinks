/**
 * File creation: Nov 9, 2009 8:09:26 PM
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


package org.references;



import java.util.NoSuchElementException;

import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class ListOfNonNullObjects<E> extends ListOfObjects<E> {
	
	/**
	 * not by content comparison ie. not .equals() instead it's "=="
	 * 
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
	protected ChainedReference<E> newRef( E obj ) {

		RunTime.assertNotNull( obj );// must not be null
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
		RunTime.assertNotNull( obj );
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
		RunTime.assertNotNull( obj );
		return obj;
	}
	
	
	/**
	 * @param pos
	 *            BEFORE/AFTER...of...
	 * @param objPos
	 *            ...which object
	 * @return null or the object
	 */
	public E getObjectAt( Position pos, E objPos ) {

		RunTime.assertNotNull( pos, objPos );
		E ret = null;
		ChainedReference<E> refPos = this.getRef( objPos );
		if ( null == refPos ) {
			// couldn't find objPos
			RunTime.badCall( "position object not found" );
		}
		// ie. what's the ref that's BEFORE(pos) ref1(refPos) ?
		ChainedReference<E> ref = this.getRefAt( pos, refPos );
		if ( null == ref ) {
			ret = null;// not found
		} else {
			ret = ref.getObject();
			RunTime.assertNotNull( ret );
		}
		return ret;
	}
	
	
	/**
	 * @param obj
	 *            not null; can already exist in list
	 * 
	 * @return the ref
	 */
	@Override
	public ChainedReference<E> addLast( E obj ) {

		RunTime.assertNotNull( obj );
		return super.addLast( obj );
	}
	
	/**
	 * @param obj
	 *            non null object that could already exist in list
	 * 
	 * @return the ref to the object
	 */
	@Override
	public ChainedReference<E> addFirst( E obj ) {

		RunTime.assertNotNull( obj );
		return super.addFirst( obj );
	}
	
	
	/**
	 * @param node
	 * @param location
	 * @return ref to the object that was just added (even if it existed, new
	 *         one added)
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
			RunTime.bug( "undefined location here." );
		}
		return null;// not reached
	}
	
	/**
	 * @param newObj
	 *            can't be null; can already exist;
	 * @param pos
	 * @param posObj
	 * @return false always
	 */
	public boolean insert( E newObj, Position pos, E posObj ) {

		RunTime.assertNotNull( newObj, pos, posObj );
		
		ChainedReference<E> posRef = this.getRef( posObj );
		if ( null == posRef ) {
			// posObj non existent? stop some bugs by throwing exception
			throw new NoSuchElementException();
		}
		ChainedReference<E> newRef = this.newRef( newObj );
		boolean ret = this.insertRefAt( newRef, pos, posRef );
		RunTime.assertFalse( ret );
		return ret;
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
}
