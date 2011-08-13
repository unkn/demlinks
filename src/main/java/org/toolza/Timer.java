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
package org.toolza;

import org.q.*;



public class Timer
{
	
	public enum TYPE {
			MILLIS,
			NANOS;
		
		@Override
		public String toString() {
			switch ( this ) {
			case MILLIS:
				return "ms";
			case NANOS:
				return "ns";
				// compiler will err if you forgot to add value here
			}
			throw Q.bug( "unreachable but compiler complains" );
		}
	}
	
	private final TYPE	type;
	private long		startTime	= 0;
	private long		stopTime;
	
	
	public Timer( final TYPE type1 ) {
		type = type1;
	}
	
	
	public void start() {
		switch ( type ) {// FIXME: avoid switch, rather plugin the related implementation in constructor
		case MILLIS:
			startTime = System.currentTimeMillis();
			break;
		case NANOS:
			startTime = System.nanoTime();
			break;
		// default: // actually the compiler will error for us when this happens
		// throw Q.bug( "you forgot to add the new case to the switch" );
		}
	}
	
	
	public void stop() {
		assert 0 != startTime : "was not started";
		switch ( type ) {
		case MILLIS:
			stopTime = System.currentTimeMillis();
			break;
		case NANOS:
			stopTime = System.nanoTime();
			break;
		}
	}
	
	
	public void end() {
		stop();
	}
	
	
	public long getDelta() {
		return stopTime - startTime;
	}
	
	
	public String getDeltaCommaDelimitedNumber() {
		return A.number( getDelta() );
	}
	
	
	public String getDeltaPrintFriendly() {
		return A.number( getDelta() ) + " " + getTypeRepresentation();
	}
	
	
	public String getTypeRepresentation() {
		return type.toString();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDeltaPrintFriendly();
	}
}
