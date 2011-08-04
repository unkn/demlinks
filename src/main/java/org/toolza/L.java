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



import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import org.q.*;



/**
 *
 */
public class L {
	
	public static final int			TIMEOUT_TO_GET_LOCK					= 10;
	public static final TimeUnit	TIMEUNIT_FOR_TIMEOUT_TO_GET_LOCK	= TimeUnit.SECONDS;
	
	
	/**
	 * usually I use .lock() instead of this one<br>
	 * 
	 * @param rl
	 * @return {@code true} if the lock was acquired<br>
	 *         {@code false} if the waiting time elapsed before the lock was acquired
	 */
	public static boolean tryLock( final Lock rl ) {
		assert null != rl;
		try {
			return rl.tryLock( TIMEOUT_TO_GET_LOCK, TIMEUNIT_FOR_TIMEOUT_TO_GET_LOCK );
		} catch ( final InterruptedException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	/**
	 * it timeout then it will throw<br>
	 * L.TIMEOUT_TO_GET_LOCK seconds<br>
	 * 
	 * @param rl
	 */
	public static void lockWithTimeOut( final Lock rl ) {
		assert null != rl;
		try {
			if ( !rl.tryLock( TIMEOUT_TO_GET_LOCK, TIMEUNIT_FOR_TIMEOUT_TO_GET_LOCK ) ) {
				Q.bug( "acquire lock timed out" );
			}
		} catch ( final InterruptedException e ) {
			Q.rethrow( e );
		}
	}
}
