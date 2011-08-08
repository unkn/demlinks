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



public interface OrderedList {
	
	/**
	 * makes sure the longIdent exists, if it doesn't then append it<br>
	 * 
	 * @param whichLongIdent
	 * @return true if already existed and thus nothing was done to change anything (which means it may not be positioned last)
	 */
	public boolean ensure( Long whichLongIdent );
	
	
	public boolean contains( Long whichLongIdent );
	
	
	// void assumedValid();
	
	
	public long size();
	
	
	public void add( Long whichLongIdent, Position pos );
	
	
	/**
	 * cannot be used when DUPs are allowed
	 * 
	 */
	public void add( Long whichLongIdent, Position pos, Long posLong );
	
	
	/**
	 * @return null if didn't exist, else the Symbol which was removed
	 */
	public Long remove( Position pos, Long posLong );
	
	
	/**
	 * @param pos
	 * @return null if didn't exist, else the Symbol which was removed
	 */
	public Long remove( Position pos );
	
	
	/**
	 * @return true if existed
	 */
	public boolean remove( Long whichLong );
	
	
	/**
	 * @param first
	 */
	public Long get( Position first );
	
	
	/**
	 * can't use this when DUPs are allowed ie. posLong may exist twice, so which one r u referring to?
	 * 
	 */
	public Long get( Position pos, Long posLong );
	
	
	public Long getSelf();
}
