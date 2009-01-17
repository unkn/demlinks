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

package org.demlinks.references;

public class Reference<Obj> {
	private Reference<Obj> prev;
	private Obj object;
	private Reference<Obj> next;
	
	
	//constructor
	public Reference() {
		initAsDead();
	}
	
	public void setObject(Obj toObject) {//even if null
		object = toObject;
	}
	
	public boolean isAlone() {
		return ((prev == null) && (next == null));
	}
	
	public Reference<Obj> getPrev() {
		return prev;
	}

	public void setPrev(Reference<Obj> prev) {
		this.prev = prev;
	}

	public Reference<Obj> getNext() {
		return next;
	}

	public void setNext(Reference<Obj> next) {
		this.next = next;
	}

	/**
	 * @return the object that this reference refers to
	 */
	public Obj getObject() {
		return object;
	}

	/**
	 * signal that the reference has been removed/destroyed from the list
	 */
	public void destroy() {
		initAsDead();
	}
	
	/**
	 * after this call, isDead() would return true
	 */
	private void initAsDead() {
		next = prev = null;
		object = null;
	}
	
	/**
	 * @return true if this reference is nolonger used in the list
	 */
	public boolean isDead() {
		return (isAlone() && (null == object));
	}
	
	
}
