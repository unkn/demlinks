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

package org.aspectj.ExceptionsHandling.ToE;



import org.q.*;
import org.toolza.*;



/**
 *
 */
public class NodeForTreeOfCalls
{
	
	private final NodeForTreeOfExceptions	exception;
	private final StackTraceElement			call;
	
	
	public NodeForTreeOfCalls( final NodeForTreeOfExceptions exception1, final StackTraceElement call1 ) {
		assert Q.nn( exception1 );
		assert Q.nn( call1 );
		exception = exception1;
		call = call1;
	}
	
	
	public synchronized NodeForTreeOfExceptions getExceptionNode() {
		assert Q.nn( exception );
		return exception;
	}
	
	
	/**
	 * @return state of the call
	 */
	public synchronized StateOfAnException getState() {
		return getExceptionNode().getState();
	}
	
	
	public synchronized StackTraceElement getCall() {
		assert Q.nn( call );
		return call;
	}
	
	
	// private synchronized static
	// String
	// spaces(
	// int within,
	// String msg )
	// {
	// return String.format(
	// "%-"
	// + within
	// + "s",
	// msg );
	// }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		final StackTraceElement call1 = getCall();
		return A.spaces( 30, call1.getFileName() + ":" + call1.getLineNumber() ) + " /// "
			+ A.spaces( 30, call1.getMethodName() ) + " /// " + A.spaces( 40, call1.getClassName() ) + " /// "
			+ getExceptionNode();
		// return getCall().toString();// getCall().getClass().toString();
		// + " "
		// + Integer.toHexString( exception.hashCode() );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj ) {
		return Z.equalsByReference_enforceNotNull( this, obj );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
