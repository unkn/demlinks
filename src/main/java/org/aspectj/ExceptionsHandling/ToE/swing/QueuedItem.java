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

package org.aspectj.ExceptionsHandling.ToE.swing;



import org.aspectj.ExceptionsHandling.ToE.*;
import org.q.*;



/**
 *
 */
public class QueuedItem
{
	
	private final Throwable					ex;
	private final EnumQueueProcessorTypes	type;
	private final StateOfAnException		stateForMarkAs;
	
	
	public QueuedItem( final EnumQueueProcessorTypes type1, final Throwable exception, final StateOfAnException state ) {
		type = type1;
		ex = exception;
		stateForMarkAs = state;
		assert Q.nn( type );
		assert Q.nn( ex );
		assert Q.nn( stateForMarkAs );
	}
	
	
	public EnumQueueProcessorTypes getType() {
		assert Q.nn( type );
		return type;
	}
	
	
	public Throwable getEx() {
		assert Q.nn( ex );
		return ex;
	}
	
	
	public StateOfAnException getStateForMarkAs() {
		assert Q.nn( stateForMarkAs );
		return stateForMarkAs;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getType() + ":" + getEx() + "/" + getStateForMarkAs();
	}
	
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#equals(java.lang.Object)
	// */
	// @Override
	// public
	// boolean
	// equals(
	// Object obj )
	// {
	// if ( null == obj )
	// {
	// return false;
	// }
	// Q.assumedTrue( Z.areSameClass(
	// this,
	// obj ) );
	// if ( Z.equalsByReferenceAndSameClass(
	// this,
	// obj ) )
	// {
	// return true;
	// }
	// QueuedItem qi =
	// (QueuedItem)obj;
	//
	// return ( ( qi.getEx().equals( getEx() ) ) && ( qi.getType().equals( getType() ) ) );
	// }
	//
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#hashCode()
	// */
	// @Override
	// public
	// int
	// hashCode()
	// {
	// return ( getType().toString() + getEx().toString() ).hashCode();
	// }
}
