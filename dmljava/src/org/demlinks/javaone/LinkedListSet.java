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

import java.util.LinkedList;

/**
 * a list of unique objects (no duplicates)<br>
 * no null objects<br>
 * ability to insert anywhere<br>
 *
 * @param <E> the list will have objects of this type
 */
public class LinkedListSet<E> extends LinkedList<E> {

	/* (non-Javadoc)
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E obj) {
		Environment.nullError(obj);
		if (!this.contains(obj)) {
			return super.add(obj);
		}
		return false;
	}
	
	@Override
	public void addFirst(E obj) {
		Environment.nullError(obj);
		if (!this.contains(obj)) {
			super.addFirst(obj);
		}
	}
	
	
	@Override
	public void addLast(E obj) {
		Environment.nullError(obj);
		if (!this.contains(obj)) {
			super.addLast(obj);
		}
	}
	
	@Override
	public boolean addAll(java.util.Collection<? extends E> c) {
		throw new UnknownError();
	}
	
	@Override
	public boolean addAll(int index, java.util.Collection<? extends E> c) {
		throw new UnknownError();
	}
	
	@Override
	public void add(int index, E element) {
		Environment.nullError(element);
		if (!this.contains(element)) {
			super.add(index, element);
		}
	}
	
	@Override
	public boolean offer(E e) {
		Environment.nullError(e);
		if (!this.contains(e)) {
			return super.offer(e);
		}
		return false;
	}
	
	@Override
	public boolean offerFirst(E e) {
		Environment.nullError(e);
		if (!this.contains(e)) {
			return super.offerFirst(e);
		}
		return false;
	}
	
	@Override
	public boolean offerLast(E e) {
		Environment.nullError(e);
		if (!this.contains(e)) {
			return super.offerLast(e);
		}
		return false;
	}
	
	@Override
	public void push(E e) {
		Environment.nullError(e);
		if (!this.contains(e)) {
			super.push(e);
		}
	}
	
	@Override
	public E set(int index, E element) {
		Environment.nullError(element);
		if (!this.contains(element)) {
			return super.set(index, element);
		}
		return null;
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380435324961393742L;

}
