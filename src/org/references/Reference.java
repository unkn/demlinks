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



public class Reference<Obj> {
	
	private Reference<Obj>	prev;
	private Obj				object;
	private Reference<Obj>	next;
	
	// constructor
	public Reference() {

		this.initAsDead();
	}
	
	// clone constructor
	public Reference( Reference<Obj> cloneThis ) {

		this.initAsDead();
		RunTime.assertNotNull( cloneThis );
		this.prev = cloneThis.prev;
		this.next = cloneThis.next;
		this.object = cloneThis.object;
	}
	
	public boolean equals( Reference<Obj> compareObj ) {

		if ( ( this.prev == compareObj.prev )
				&& ( this.next == compareObj.next )
				&& ( this.object == compareObj.object ) ) {
			return true;
		}
		return false;
		// I thought compareObj.prev is private, and yet I'm still able to
		// access it O_o
	}
	
	public void setObject( Obj toObject ) {// even if null
	
		this.object = toObject;
	}
	
	public boolean isAlone() {

		return ( ( this.prev == null ) && ( this.next == null ) );
	}
	
	public Reference<Obj> getPrev() {

		return this.prev;
	}
	
	public void setPrev( Reference<Obj> prevRef ) {

		this.prev = prevRef;
	}
	
	public Reference<Obj> getNext() {

		return this.next;
	}
	
	public void setNext( Reference<Obj> nextRef ) {

		this.next = nextRef;
	}
	
	/**
	 * @return the object that this reference refers to
	 */
	public Obj getObject() {

		return this.object;
	}
	
	/**
	 * signal that the reference has been removed/destroyed from the list
	 */
	public void destroy() {

		this.initAsDead();
	}
	
	/**
	 * after this call, isDead() would return true
	 */
	private void initAsDead() {

		this.next = this.prev = null;
		this.object = null;
	}
	
	/**
	 * @return true if this reference is nolonger used in the list
	 */
	public boolean isDead() {

		return ( this.isAlone() && ( null == this.object ) );
	}
}
