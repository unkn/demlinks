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
package org.bdbLevel1;

import org.bdb.exceptions.*;
import org.q.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 * a BerkeleyDB Sequence that is associated with a name, and also obviously with its long<br>
 * this will help you generate the next unique number (unique within this same named Sequence)<br>
 */
public class BDBNamedSequence {
	
	private Sequence	thisSeq	= null;
	
	
	/**
	 * @param environ
	 * @param seqName
	 * @param min
	 *            usually 0
	 * @param initialValue
	 *            can differ from min and skip the range from min to initialValue until the wrap occurs, if wrap enabled
	 * @param max
	 *            like {@link Long#MAX_VALUE}
	 * @param wrap
	 *            true if wraps around when reaches max value, it wraps to min
	 */
	public BDBNamedSequence( final BDBEnvironment environ, final String seqName, final long min, final long initialValue,
			final long max, final boolean wrap ) {
		assert null != environ;
		assert null != seqName;
		
		
		assert null != seqName;
		assert !seqName.isEmpty();
		
		final SequenceConfig sequenceConf = new SequenceConfig();
		sequenceConf.setAllowCreate( true );
		sequenceConf.setAutoCommitNoSync( false );
		sequenceConf.setExclusiveCreate( false );
		
		// when seq.get(txn,x); "The txn handle must be null if the sequence handle was opened with a non-zero cache size."
		sequenceConf.setCacheSize( 0 );
		// thus if it's 0, we can use transaction, if it's >0 we must use null transactions only :)
		
		sequenceConf.setWrap( wrap );// no wrap else it would overwrite or rather get/use existing stuff
		
		// Configures a sequence range. This call is only effective when the sequence is being created.
		sequenceConf.setRange( min, max );
		sequenceConf.setInitialValue( initialValue );
		
		Q.info( "opening sequence with name `" + seqName + "`" );
		final DatabaseEntry deKey = new DatabaseEntry();
		StringBinding.stringToEntry( seqName, deKey );
		
		// XXX: must use no transaction(hence null) else the sequence must be closed prior to the txn-closing
		try {
			thisSeq = environ.getDBOfSequences().openSequence( null, deKey, sequenceConf );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		// XXX:returns always a new instance, even if all params are same! and .equals doesn't return true ever, since it
		// compares by reference
		
		assert null != thisSeq;
	}
	
	
	public void close() {
		if ( null != thisSeq ) {
			try {
				thisSeq.close();
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			} finally {
				thisSeq = null;
			}
		}
	}
	
	
	/**
	 * if first time then 0 will be returned; else each time is incremented by incrementDelta<br>
	 * should never return the same number, ie. it will return a diff number on each call<br>
	 * 
	 * @param incrementDelta
	 * @return Long
	 */
	public long getNextUniqueLong( final int incrementDelta ) {
		try {
			return thisSeq.get( null, incrementDelta );
		} catch ( final IllegalArgumentException iae ) {
			// this means probably: BDB4011 Sequence overflow
			throw new SequenceOverflow();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	@Override
	public boolean equals( final Object obj ) {
		Q.badCall( "not implemented" );
		return false;
	}
	
	
	@Override
	public int hashCode() {
		Q.badCall( "not implemented" );
		return 0;
	}
}
