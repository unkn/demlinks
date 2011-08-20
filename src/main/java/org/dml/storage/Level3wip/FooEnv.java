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
package org.dml.storage.Level3wip;

import org.dml.storage.Level2.*;
import org.dml.storage.berkeleydb.native_via_jni.*;
import org.dml.storage.commons.bdbLevel1.*;



public class FooEnv extends StorageBDBNative {
	
	private static final String				allLOOLWFF_StringID							= "allListsOrderedOfLongsWithFastFind";
	public final Long						allLOOLWFF_LongID							=
																							ensureNodeForName( allLOOLWFF_StringID );
	public final Extension_Set_OfChildren	allLOOLWFF_Set								= new Extension_Set_OfChildren(
																							this,
																							allLOOLWFF_LongID );
	
	private static final String				allLOOL_StringID							= "allListsOrderedOfLongs";
	public final Long						allLOOL_LongID								= ensureNodeForName( allLOOL_StringID );
	public final Extension_Set_OfChildren	allLOOL_Set									= new Extension_Set_OfChildren(
																							this,
																							allLOOL_LongID );
	
	private static final String				allSetsOfLOOLWWF_StringID					=
																							"allSets that are part of ListsOrderedOfLongsWithFastFind";
	public final Long						allSetsOfLOOLWWF_LongID						=
																							ensureNodeForName( allSetsOfLOOLWWF_StringID );
	public final Extension_Set_OfChildren	allSetsOfLOOLWWF_Set						= new Extension_Set_OfChildren(
																							this,
																							allSetsOfLOOLWWF_LongID );
	
	private static final String				allHeadsForLOOL_StringID					= "allHeads for LOOL";
	public final Long						allHeadsForLOOL_LongID						=
																							ensureNodeForName( allHeadsForLOOL_StringID );
	private static final String				allElementCapsulesForLOOL_StringID			=
																							"set of all ElementCapsules for all LOOLs";
	public final Long						allElementCapsulesForLOOL_LongID			=
																							ensureNodeForName( allElementCapsulesForLOOL_StringID );
	
	private static final String				allPtrToPrevForElementCapsules_StringID		= "allPtrToPrevForElementCapsules";
	public final Long						allPtrToPrevForElementCapsules_LongID		=
																							ensureNodeForName( allPtrToPrevForElementCapsules_StringID );
	
	private static final String				allPtrToNextForElementCapsules_StringID		= "allPtrToNextForElementCapsules";
	public final Long						allPtrToNextForElementCapsules_LongID		=
																							ensureNodeForName( allPtrToNextForElementCapsules_StringID );
	
	private static final String				allPtrToElementForElementCapsules_StringID	= "allPtrToElementForElementCapsules";
	public final Long						allPtrToElementForElementCapsules_LongID	=
																							ensureNodeForName( allPtrToElementForElementCapsules_StringID );
	
	
	public FooEnv( final String envHomeDir1, final boolean deleteFirst ) {
		super( envHomeDir1, deleteFirst );// this is executed first, then the fields are inited!
	}
	
	
	// public static void main( final String[] args ) {// test
	// final FooEnv f = new FooEnv( Consts4JUnit.BDB_ENV_PATH, true );
	// f.deInit();
	// }
}
