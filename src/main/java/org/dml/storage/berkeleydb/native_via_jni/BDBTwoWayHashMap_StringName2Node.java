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
package org.dml.storage.berkeleydb.native_via_jni;

import org.dml.storage.berkeleydb.commons.*;

import com.sleepycat.db.*;



/**

 */
public class BDBTwoWayHashMap_StringName2Node
		extends GenericBDBTwoWayMapOfNNU<String, BDBNode>
{
	
	
	
	public BDBTwoWayHashMap_StringName2Node( final StorageBDBNative env, final String dbName1 ) {
		this( env.getBDBEnv(), dbName1 );
	}
	
	
	public BDBTwoWayHashMap_StringName2Node( final Environment env, final String dbName1 ) {
		super( env, dbName1, String.class, BDBNode.class );
	}
	
	
	/**
	 * @param name
	 * @param node
	 * @return true if already existed, false if it didn't;<br>
	 *         either way it does after this call
	 */
	public boolean ensureMapExists( final String name, final BDBNode node ) {
		// null checks are done in base class
		return ensureExists( name, node );
	}
	
	
	/**
	 * @param node
	 * @return null if not found
	 */
	public String getName( final BDBNode node ) {
		// null checks are done in base class
		return getKey( node );
	}
	
	
	/**
	 * @param name
	 * @return null if not found
	 */
	public BDBNode getNode( final String name ) {
		// null checks are done in base class
		return getData( name );
	}
	
	
	
}
