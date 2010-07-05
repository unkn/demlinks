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



package org.references;



import java.util.NoSuchElementException;

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
	 * creates a new Ref to be added to this list, but it's not added to list
	 * via
	 * this method
	 * 
	 * @param obj
	 *            that will be encapsulated into the ref
	 * @return new reference to <tt>obj</tt>
	 */
	protected ChainedReference<E> newRef( E obj ) {

		ChainedReference<E> n = new ChainedReference<E>();
		n.setObject( obj );// is no longer null
		return n;// should never return null
	}
	
	/**
	 * objects are not compared by content<br>
	 * compares by reference ie. ==<br>
	 * 
	 * @param containingThisObject
	 *            could be null apparently
	 * @return null or the reference containing the obj
	 */
	public ChainedReference<E> getRef( E containingThisObject ) {

		RunTime.assumedNotNull( containingThisObject );
		ChainedReference<E> parser = this.getLastRef();
		if ( null != parser ) {
			// check if it's last
			if ( parser.getObject() != containingThisObject ) {
				// it's not last then let's start parsing from first
				parser = this.getFirstRef();// can be null if empty
				// list
				while ( null != parser ) {
					if ( parser.getObject() == containingThisObject ) {
						break;
					}
					parser = parser.getNext();
					// so here it will eventually end-up checking last one again, np
				}// parsed all
			}// last
		}// null
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

		RunTime.assumedNotNull( pos );
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
	 * @param obj
	 *            can be null and can exist already(a new dup would be added)
	 * @return ChainedReference
	 */
	public ChainedReference<E> addLast( E obj ) {

		ChainedReference<E> nr = this.newRef( obj );
		if ( this.addLastRef( nr ) ) {
			RunTime.bug( "must not compare by contents" );
		}
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

		RunTime.assumedNotNull( obj, position );
		switch ( position ) {
		case FIRST:
			return this.addFirst( obj );
		case LAST:
			return this.addLast( obj );
		default:
			RunTime.bug( "undefined location here." );
		}
		return null;// unreachable but eclipse complains
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
		this.removeRef( nr );
		return true;
	}
	
	// TODO add replace methods
	

}
