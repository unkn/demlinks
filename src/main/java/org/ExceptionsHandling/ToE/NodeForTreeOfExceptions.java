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


package org.ExceptionsHandling.ToE;



import org.q.*;
import org.toolza.*;



/**
 *
 */
public class NodeForTreeOfExceptions
{
	
	// two nodes are equal if they sport the same exception compared with equals (which likely is == )
	private final Throwable		exception;
	// the state doesn't matter when comparing two nodes
	private StateOfAnException	state;
	
	
	// private static HashMap<Throwable, NodeForTreeOfExceptions> allInstances = new HashMap<Throwable,
	// NodeForTreeOfExceptions>();
	
	
	public synchronized void setState( final StateOfAnException newState ) {
		assert Q.nn( newState );
		state = newState;
	}
	
	
	/**
	 * @return state of the exception
	 */
	public synchronized StateOfAnException getState() {
		return state;
	}
	
	
	public NodeForTreeOfExceptions( final Throwable exception1, final StateOfAnException state1 ) {
		assert Q.nn( exception1 );
		assert Q.nn( state1 );
		exception = exception1;
		state = state1;
	}
	
	
	public synchronized Throwable getException() {
		assert Q.nn( exception );
		return exception;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		final String className =
			getException().getClass().getCanonicalName() + " /// " + Integer.toHexString( exception.hashCode() );
		String msg = null;
		if ( getException().getCause() == null ) {
			msg = getException().getMessage();
		}
		if ( msg == null ) {
			return className;
		} else {
			// return className
			// + " /// \""
			// + msg
			// + "\"";
			return msg + " /// \"" + className + "\"";
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object o ) {
		assert Q.nn( o );
		if ( !Z.areSameClass_canNotBeNull( this, o ) )
		// if ( this.getClass() != o.getClass() )
		{
			// I know it could be subclass so I should've used instanceof but I want to know when subclass is used
			Q.bug( "compared to different class of subclass of `this`" );
		}
		final NodeForTreeOfExceptions obj = (NodeForTreeOfExceptions)o;
		return Z.equalsByReference_enforceNotNull( getException(), obj.getException() );
		// getException().equals(
		// obj.getException() );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getException().hashCode();
	}
}
