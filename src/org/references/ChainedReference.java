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



public class ChainedReference<Obj> extends Reference<Obj> {
	
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6660991694460126538L;
	private ChainedReference<Obj>	prev;
	// private Obj object;
	private ChainedReference<Obj>	next;
	
	// constructor
	public ChainedReference() {

		super();
		this.initAsDead();
	}
	
	/**
	 * this is a kind of cloning...
	 * 
	 * @param cloneThis
	 */
	public ChainedReference( ChainedReference<Obj> cloneThis ) {

		this.initAsDead();
		RunTime.assertNotNull( cloneThis );
		this.setPrev( cloneThis.getPrev() );
		this.setNext( cloneThis.getNext() );
		this.setObject( cloneThis.getObject() );
	}
	
	public boolean equals( ChainedReference<Obj> compareObj ) {

		if ( ( super.equals( compareObj ) )
				|| ( ( this.getPrev() == compareObj.getPrev() )
						&& ( this.getNext() == compareObj.getNext() ) && ( this.getObject() == compareObj.getObject() ) ) ) {
			return true;
		}
		return false;
		// I thought compareObj.prev is private, and yet I'm still able to
		// access it O_o
	}
	
	public boolean isAlone() {

		return ( ( this.getPrev() == null ) && ( this.getNext() == null ) );
	}
	
	public ChainedReference<Obj> getPrev() {

		return this.prev;
	}
	
	public void setPrev( ChainedReference<Obj> prevRef ) {

		this.prev = prevRef;
	}
	
	public ChainedReference<Obj> getNext() {

		return this.next;
	}
	
	public void setNext( ChainedReference<Obj> nextRef ) {

		this.next = nextRef;
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

		this.setNext( null );
		this.setPrev( null );
		this.setObject( null );
	}
	
	/**
	 * @return true if this reference is no longer used in the list
	 */
	public boolean isDead() {

		return ( this.isAlone() && ( null == this.getObject() ) );
	}
}
