/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.demlinks.javaone;

import java.nio.channels.AlreadyConnectedException;
import java.util.LinkedList;

// no DUPlicate elements
// ability to insert anywhere
// no nulls

public class LinkedListSet<E> extends LinkedList<E> {

//	LinkedListSet() {
//		super();
//	}
	
	private static void nullError(Object anyObject) {
		if (null == anyObject) {
			throw new AssertionError("should never be null");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E obj) {
		nullError(obj);
		if (!this.contains(obj)) {
			return super.add(obj);
		}
		return false;
	}
	
	@Override
	public void addFirst(E obj) throws AlreadyConnectedException {
		if (this.contains(obj)) {
			throw new AlreadyConnectedException();
		}
		super.addFirst(obj);
	}
	
	
	@Override
	public void addLast(E obj) throws AlreadyConnectedException {
		if (this.contains(obj)) {
			throw new AlreadyConnectedException();
		}
		super.addLast(obj);
	}
	
	@Override
	public boolean addAll(java.util.Collection<? extends E> c) {
		throw new UnknownError();
		//return false;
	}
	
	@Override
	public boolean addAll(int index, java.util.Collection<? extends E> c) {
		throw new UnknownError();
	}
	
	@Override
	public void add(int index, E element) {
		if (this.contains(element)) {
			throw new AlreadyConnectedException();
		}
		super.add(index, element);
	}
	
	@Override
	public boolean offer(E e) {
		if (this.contains(e)) {
			throw new AlreadyConnectedException();
		}
		return super.offer(e);
	};
	
	@Override
	public boolean offerFirst(E e) {
		if (this.contains(e)) {
			throw new AlreadyConnectedException();
		}
		return super.offerFirst(e);
	}
	
	@Override
	public boolean offerLast(E e) {
		if (this.contains(e)) {
			throw new AlreadyConnectedException();
		}
		return super.offerLast(e);
	}
	
	@Override
	public void push(E e) {
		if (this.contains(e)) {
			throw new AlreadyConnectedException();
		}
		super.push(e);
	}
	
	@Override
	public E set(int index, E element) {
		if (this.contains(element)) {
			throw new AlreadyConnectedException();
		}
		return super.set(index, element);
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380435324961393742L;

	// TODO: inserting element that already exists should move it, or fail but a move() func. should exist in case insert fails
}
