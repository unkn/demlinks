/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 16, 2011 11:46:18 AM
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
package org.dml.storage.commons;

import org.dml.storage.berkeleydb.generics.*;
import org.q.*;



/**
 *
 */
public class StorageConfig
{
	
	private BDBStorageSubType	subtype;
	private String				envHome;
	private boolean				deleteBefore;
	
	
	public StorageConfig() {
	}
	
	
	public void setBDBType( final BDBStorageSubType type ) {
		// assert Q.nn( type );
		subtype = type;
	}
	
	
	private boolean isValidHome( final String home ) {
		if ( ( null == home ) || ( home.isEmpty() ) ) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public void setHomeDir( final String home ) {
		assert isValidHome( home );
		envHome = home;
	}
	
	
	public void setDeleteBefore( final boolean yn ) {
		deleteBefore = yn;
	}
	
	
	public String getHomeDir() {
		assert isValidHome( envHome ) : "home wasn't set";
		return envHome;
	}
	
	
	public boolean getDeleteBefore() {
		return deleteBefore;
	}
	
	
	public BDBStorageSubType getBDBType() {
		assert Q.nn( subtype );
		return subtype;
	}
}
