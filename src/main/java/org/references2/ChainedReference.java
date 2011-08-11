/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.references2;



import org.toolza.*;



/**
 * @param <OBJ>
 * 
 */
public class ChainedReference<OBJ>
		extends Reference<OBJ>
{
	
	// private boolean set =
	// false;
	private ChainedReference<OBJ>	prev	= null;
	// private Obj object;
	private ChainedReference<OBJ>	next	= null;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.Reference#internal_reset()
	 */
	@Override
	protected synchronized void internal_reset() {
		super.internal_reset();
		prev = null;
		next = null;
	}
	
	
	// constructor
	public ChainedReference() {
		super();
	}
	
	
	// /**
	// * this is a kind of cloning...
	// *
	// * @param cloneThis
	// */
	// public ChainedReference( ChainedReference<Obj> cloneThis ) {
	//
	// this.initAsDead();
	// RunTime.assertNotNull( cloneThis );
	// this.setPrev( cloneThis.getPrev() );
	// this.setNext( cloneThis.getNext() );
	// this.setObject( cloneThis.getObject() );
	// RunTime.assertTrue( this != cloneThis );// by ref
	// RunTime.assertTrue( this.equals( cloneThis ) );// by contents
	// }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	// @Override
	// public synchronized
	// boolean
	// equals(
	// // must only compare Object references not contents!
	// Object compareObj )
	// {
	// if (!super.equals( compareObj )) {
	// {
	// // if ( ( this.getPrev() == ( (ChainedReference<?>)compareObj ).getPrev() )
	// // && ( this.getNext() == ( (ChainedReference<?>)compareObj ).getNext() ) )
	// // {
	// // return true;
	// // }
	// }
	// return true;
	// }
	
	
	/**
	 * @param obj
	 */
	public ChainedReference( final OBJ obj ) {
		// / constructor
		super();
		setObject( obj );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.Reference#equals(java.lang.Object)
	 */
	@Override
	public synchronized boolean equals( final Object obj1 ) {
		return Z.equalsByReference_enforceNotNull( this, obj1 );// FIXME: maybe we need to allow null here?
	}
	
	
	public synchronized boolean isAlone() {
		
		return ( ( this.getPrev() == null ) && ( this.getNext() == null ) );
	}
	
	
	public synchronized ChainedReference<OBJ> getPrev() {
		
		return this.prev;
	}
	
	
	public synchronized void setPrev( final ChainedReference<OBJ> prevRef ) {
		
		this.prev = prevRef;
	}
	
	
	public synchronized ChainedReference<OBJ> getNext() {
		
		return this.next;
	}
	
	
	public synchronized void setNext( final ChainedReference<OBJ> nextRef ) {
		
		this.next = nextRef;
	}
	
	
	/**
	 * @return true if this reference is no longer used in the list
	 */
	public synchronized boolean isDead() {
		final boolean dead = !isSet();
		assert ( ( !dead ) || ( ( dead ) && ( isAlone() ) ) );
		// if ( dead )
		// {
		// }
		return dead;
	}
}
