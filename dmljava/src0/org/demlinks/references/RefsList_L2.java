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

package org.demlinks.references;

import org.demlinks.javathree.*;
import org.q.*;



/**
 * handles the NodeRef list at the Node level
 * 
 */
public class RefsList_L2<E> extends RefsList_L1<E> {
	
	/**
	 * @param location
	 * @return
	 */
	public E removeObject( final Location location ) {
		final Reference<E> nr = getNodeRefAt( location );
		if ( null != nr ) {
			final E nod = nr.getObject();
			if ( removeRef( nr ) ) {
				return nod;
			}
		}
		return null;
	}
	
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean containsObject( final E obj ) {
		assert Q.nn( obj );
		return ( null != this.getRef( obj ) );
	}
	
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via
	 * this method
	 * 
	 * @param obj
	 * @return
	 */
	public Reference<E> newRef( final E obj ) {
		assert Q.nn( obj );
		final Reference<E> n = new Reference<E>();
		n.setObject( obj );
		return n;
	}
	
	
	/**
	 * @param obj
	 * @return
	 */
	public Reference<E> getRef( final E obj ) {
		return getRef_L0( obj );
	}
	
	
	/**
	 * @param obj
	 * @return
	 */
	public final Reference<E> getRef_L0( final E obj ) {
		assert Q.nn( obj );
		Reference<E> parser = getFirstRef();
		while ( null != parser ) {
			if ( obj.equals( parser.getObject() ) ) {
				break;
			}
			parser = parser.getNext();
		}
		return parser;
	}
	
	
	/**
	 * @return
	 */
	public E getFirstObject() {
		if ( getFirstRef() != null ) {
			return getFirstRef().getObject();
		}
		return null;
	}
	
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean addLast( final E obj ) {
		Reference<E> nr = getRef( obj );
		if ( null == nr ) {
			nr = newRef( obj );
		}
		return addLast( nr );
	}
	
	
	// TODO addFirst
	// TODO insert(Node, Location);
	// TODO insert(Node, Location, Node);
	// TODO replace(Node, Node);
	// TODO replace(Node, Location);
	// TODO replace(Node, Location, Node);
	// find+replace current, is not an option
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	public boolean removeObject( final E obj ) {
		final Reference<E> nr = getRef( obj );
		if ( null == nr ) {
			return false;
		}
		return removeRef( nr );
	}
	
}
