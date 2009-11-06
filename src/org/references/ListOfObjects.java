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

import org.dml.error.BadCallError;
import org.dml.tools.RunTime;



/**
 * handles the RefsList list at the Object level ie. accepting only Object
 * parameters where Object is the type of element stored in list; that Object is
 * E DOES YES allow adding of NULL or DUPlicate Objects<br>
 */
public class ListOfObjects<E> extends ListOfReferences<E> {
	
	public ListOfObjects() {

		super();
	}
	
	/**
	 * doesn't compare by content
	 * 
	 * @param obj
	 * @return
	 */
	public boolean containsObject( E obj ) {

		return ( null != this.getRef( obj ) );
	}
	
	/**
	 * @param obj
	 * @param index
	 *            0 based index
	 * @return true if obj is at index
	 */
	public boolean containsObjectAtPos( E obj, int index ) {

		ChainedReference<E> ref = this.getRefAtIndex( index );
		if ( null == ref ) {
			return false;// doesn't contain it
		}
		return ( ref.getObject() == obj );
	}
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via
	 * this method
	 * 
	 * @param obj
	 * @return new reference to <tt>obj</tt>
	 */
	public ChainedReference<E> newRef( E obj ) {

		ChainedReference<E> n = new ChainedReference<E>();
		n.setObject( obj );// is no longer null
		return n;// should never return null
	}
	
	/**
	 * doesn't compare by content<br>
	 * compares by reference ie. ==
	 * 
	 * @param obj
	 *            could be null apparently
	 * @return null or the reference containing the obj
	 */
	public ChainedReference<E> getRef( E obj ) {

		ChainedReference<E> parser = this.getFirstRef();
		while ( null != parser ) {
			if ( parser.getObject() == obj ) {
				break;
			}
			parser = parser.getNext();
		}
		return parser;
	}
	
	/**
	 * @param index
	 *            0 based index
	 * @return null or the ref at position 'index'
	 * @exception BadCallError
	 *                if index out of bounds
	 */
	public ChainedReference<E> getRefAtIndex( int index ) {

		if ( ( index < 0 ) || ( index >= this.size() ) ) {
			throw new BadCallError( "out of bounds" );
		}
		
		ChainedReference<E> parser = this.getFirstRef();
		int pos = 0;
		while ( null != parser ) {
			if ( index == pos ) {
				break;
			}
			parser = parser.getNext();
			pos++;
		}
		return parser;
	}
	
	/**
	 * @param pos
	 *            only FIRST/LAST
	 * @return the object at specified position(which can be null)
	 * @throws NoSuchElementException
	 *             if no such object is found
	 */
	public E getObjectAt( Position pos ) {

		RunTime.assertNotNull( pos );
		ChainedReference<E> ref = this.getRefAt( pos );
		if ( null == ref ) {
			throw new NoSuchElementException();
		}
		return ref.getObject();
	}
	
	/**
	 * @param index
	 *            0 based index
	 * @return the object at index which could be a null object
	 * @throws NoSuchElementException
	 *             if no such object is found
	 */
	public E getObjectAt( int index ) {

		ChainedReference<E> ref = this.getRefAtIndex( index );
		if ( null == ref ) {
			throw new NoSuchElementException();
		}
		return ref.getObject();
	}
	
	/**
	 * @param pos
	 *            BEFORE/AFTER...of...
	 * @param objPos
	 *            ...which object
	 * @return the object(which can be null)
	 * @throws NoSuchElementException
	 */
	public E getObjectAt( Position pos, E objPos ) {

		RunTime.assertNotNull( pos );
		ChainedReference<E> refPos = this.getRef( objPos );
		if ( null == refPos ) {
			// couldn't find objPos
			throw new BadCallError( "position object not found" );
		}
		// ie. what's the ref that's BEFORE(pos) ref1(refPos) ?
		ChainedReference<E> ref = this.getRefAt( pos, refPos );
		if ( null == ref ) {
			throw new NoSuchElementException();
		}
		return ref.getObject();
	}
	
	/**
	 * @param obj
	 *            can be null and can exist already(a new dup would be added)
	 * @return ChainedReference
	 */
	public ChainedReference<E> addLast( E obj ) {

		ChainedReference<E> nr = this.newRef( obj );
		this.addLastRef( nr );
		return nr;
	}
	
	/**
	 * @param obj
	 *            that could already exist in list; even null
	 * @return ChainedReference to this object
	 */
	public ChainedReference<E> addFirst( E obj ) {

		ChainedReference<E> nr = this.newRef( obj );
		if ( this.addFirstRef( nr ) ) {
			RunTime.bug( "must not compare by contents" );
		}
		return nr;
	}
	
	/**
	 * @param node
	 * @param location
	 * @return true if object existed before call
	 */
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
	 * @return false
	 * @throws NoSuchElementException
	 *             if posObj doesn't exist
	 */
	public boolean insert( E newObj, Position pos, E posObj ) {

		RunTime.assertNotNull( pos );
		ChainedReference<E> posRef = this.getRef( posObj );
		if ( null == posRef ) {
			// posObj non existent? stop some bugs by throwing exception
			throw new NoSuchElementException();
		}
		ChainedReference<E> newRef = this.newRef( newObj );
		this.insertObjAt( newRef, pos, posRef );
		return false;
	}
	
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	public boolean removeObject( E obj ) {

		ChainedReference<E> nr = this.getRef( obj );
		if ( null == nr ) {
			return false;
		}
		return this.removeRef( nr );
	}
	
	// TODO add replace methods
}
