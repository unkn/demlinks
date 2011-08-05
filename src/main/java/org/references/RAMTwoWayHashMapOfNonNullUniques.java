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


package org.references;



import java.util.*;

import org.q.*;
import org.toolza.*;



/**
 * can locate the counterpart by either KEY or DATA
 * TODO: add multiverse transaction atomicity xD
 * 
 * @param <KEY>
 *            unique & never null
 * @param <DATA>
 *            unique & never null
 * 
 */
public class RAMTwoWayHashMapOfNonNullUniques<KEY, DATA> extends BaseFor_ThreadSafeTwoWayMapOfUniques<KEY, DATA> {
	
	private final HashMap<KEY, DATA>	mapKeyData	= new HashMap<KEY, DATA>();
	private final HashMap<DATA, KEY>	mapDataKey	= new HashMap<DATA, KEY>();
	
	
	/**
	 * @param key
	 * @return true if existed
	 */
	@Override
	protected boolean internalForOverride_removeByKey( final KEY key ) {
		assert Q.nn( key );
		final DATA data = mapKeyData.get( key );
		if ( null != data ) {
			// consistency check
			final KEY key2 = mapDataKey.get( data );
			assert Q.nn( key2 );
			// fixed: key2 and key here could potentially be two diff instances that give same equals, yes? which means must use
			// .equals not == (by ref.)
			// done: do a test case for this!
			assert Z.equalsWithCompatClasses_allowsNull( key, key2 );
			// indeed key and key2 can be two diff instances so they're != but via .equals() they are true/equal
			// XXX: memory transaction here? for the two removes, just in case they throw, else it's ok since synchronized mtd
			final DATA tempRetData = mapKeyData.remove( key );
			final KEY tempRetKey = mapDataKey.remove( data );
			
			assert Z.equalsByReference_enforceNotNull( tempRetData, data );
			assert Z.equalsByReference_enforceNotNull( tempRetKey, key2 );
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param key
	 * @return null if not found
	 */
	@Override
	protected DATA internalForOverride_getData( final KEY key ) {
		assert Q.nn( key );
		
		final DATA data = mapKeyData.get( key );
		if ( null != data ) {
			// consistency check
			final KEY key2 = mapDataKey.get( data );
			assert Z.equalsWithCompatClasses_allowsNull( key, key2 ) : Q.bug();
		}
		return data;// can be null
	}
	
	
	/**
	 * @param data
	 * @return null if not found
	 */
	@Override
	protected KEY internalForOverride_getKey( final DATA data ) {
		assert Q.nn( data );
		
		final KEY key = mapDataKey.get( data );
		if ( null != key ) {
			final DATA data2 = mapKeyData.get( key );
			assert Z.equalsWithCompatClasses_allowsNull( data, data2 ) : Q.bug();
		}
		return key;
	}
	
	
	/**
	 * @param key
	 * @param data
	 * @return true if already existed
	 */
	@Override
	protected boolean internalForOverride_ensureExists( final KEY key, final DATA data ) {
		assert Q.nn( key );
		assert Q.nn( data );
		
		DATA prevData = getData( key );
		if ( null != prevData ) {
			assert ( Z.equalsWithCompatClasses_allowsNull( prevData, data ) ) : Q
				.badCall( "You attempted to overwrite an already existing key-value with a new different value. Not acceptable. "
					+ "key=`" + key + "` newData=`" + data + "` olddata=`" + prevData + "` " );
			return true;// already existed, consistency check was already done in getData()
		}
		
		assert null == prevData;
		
		// XXX: transaction just in case any of the put() fails, if not, it's ok
		prevData = mapKeyData.put( key, data );
		final KEY prevKey = mapDataKey.put( data, key );
		
		assert null == prevData;
		assert null == prevKey;
		return false;// did not previously exist
	}
	
	
	@Override
	protected void internalForOverride_removeAll() {
		// XXX: transaction:
		mapKeyData.clear();
		mapDataKey.clear();
		
		assert isEmpty();
	}
	
	
	@Override
	protected int internalForOverride_size() {
		final int size = mapKeyData.size();
		assert mapDataKey.size() == size;
		return size;
	}
	
	
}
