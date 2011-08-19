/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 17, 2011 2:01:06 AM
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

import java.util.*;

import org.JUnitCommons.*;
import org.dml.storage.berkeleydb.generics.*;
import org.dml.storage.commons.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.q.*;
import org.toolza.Timer;



/**
 *
 */
@RunWith( Parameterized.class )
public abstract class BaseTest_for_Storage
		extends JUnitHooker
{
	
	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList( new Object[][] {
			// XXX: add all possible storage types here, so they can be tested
			{
				StorageType.BDB, BDBStorageSubType.JE
			}, {
				StorageType.BDB, BDBStorageSubType.JNI
			}
		} );
	}
	
	private final StorageType		type;
	private final BDBStorageSubType	subType;
	public StorageGeneric			storage;
	private final Timer				t	= new Timer( Timer.TYPE.MILLIS );
	
	
	public BaseTest_for_Storage( final StorageType type1, final BDBStorageSubType subType1 ) {
		type = type1;
		subType = subType1;
	}
	
	
	
	@Before
	public final void setUp() {
		t.start();
		try {
			storage = setUpStorage( type, subType );
			overridden_setUp();
			assert Q.nn( storage );
		} finally {
			t.stop();
			System.out.println( "setUp: " + t );
		}
	}
	
	
	public static StorageGeneric setUpStorage( final StorageType type1, final BDBStorageSubType subType1 ) {
		final StorageConfig cfg = new StorageConfig();
		cfg.setBDBType( subType1 );
		cfg.setHomeDir( JUnitConstants.ENVIRONMENT_STORE_DIR + type1 + "_" + subType1 );
		cfg.setDeleteBefore( true );
		return StorageFactory.getStorage( type1, cfg );
		// env = StorageBDBGeneric.getBDBStorage( BDBStorageSubType.JE, JUnitConstants.ENVIRONMENT_STORE_DIR, true );
	}
	
	
	@After
	public final void tearDown() {
		t.start();
		try {
			overridden_tearDown();
			if ( null != storage ) {
				storage.shutdown( true );
			}
		} finally {
			t.stop();
			System.out.println( "tearDown: " + t );
		}
	}
	
	
	public void overridden_tearDown() {
	}
	
	
	public void overridden_setUp() {
	}
}
