/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 5, 2011 12:04:27 AM
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

import org.references.*;

import com.sleepycat.db.*;



/**
 * @param <KEY>
 * @param <DATA>
 * 
 */
public class BDBTwoWayMap<KEY, DATA> extends BaseForTwoWayMapOfUniques<KEY, DATA> {
	
	public BDBTwoWayMap( final Environment env, final String dbName1 ) {
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_getKey(java.lang.Object)
	 */
	@Override
	protected KEY internalForOverride_getKey( final DATA data ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_getData(java.lang.Object)
	 */
	@Override
	protected DATA internalForOverride_getData( final KEY key ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_ensureExists(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean internalForOverride_ensureExists( final KEY key, final DATA data ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_removeByKey(java.lang.Object)
	 */
	@Override
	protected boolean internalForOverride_removeByKey( final KEY key ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_removeAll()
	 */
	@Override
	protected void internalForOverride_removeAll() {
		// TODO Auto-generated method stub
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_size()
	 */
	@Override
	protected int internalForOverride_size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
