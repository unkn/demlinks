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
package org.storage;


import static org.junit.Assert.*;

import org.JUnitCommons.*;
import org.dml.storage.berkeleydb.commons.*;
import org.dml.storage.berkeleydb.generics.*;
import org.dml.storage.commons.*;
import org.junit.*;



public class TestNodeName
		extends JUnitHooker
{
	
	private StorageGeneric	env;
	
	
	@Before
	public void setUp() {
		// FIXME: X12 must use another way, more generic to ask which storage to use/init
		env = StorageBDBGeneric.getBDBStorage( BDBStorageSubType.JE, JUnitConstants.ENVIRONMENT_STORE_DIR, true );
	}
	
	
	@After
	public void tearDown() {
		if ( null != env ) {
			env.shutdown( true );
		}
	}
	
	
	@Test
	public void test1() {
		final NodeGeneric lNew = env.createNewUniqueNode();
		assertNotNull( lNew );
		assertFalse( lNew.equals( env.createNewUniqueNode() ) );
		final String strId = "boo";
		final NodeGeneric longId = env.createOrGetNode( strId );
		assertNotNull( longId );
		assertTrue( longId.equals( env.createOrGetNode( strId ) ) );
		
	}
	
}
