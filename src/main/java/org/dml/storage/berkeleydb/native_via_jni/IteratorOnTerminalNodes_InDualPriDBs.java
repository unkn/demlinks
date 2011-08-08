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


import org.dml.storage.*;

import com.sleepycat.db.*;



/**
 * Vector aka OrderedPair<br>
 * this takes care of both primary databases ie. on delete()<br>
 */
public class IteratorOnTerminalNodes_InDualPriDBs
		extends IterOnTerminalNodes_InOnePriDB
		implements IteratorOnTerminalNodesGeneric
{
	
	private final Database	reverse;
	
	
	/**
	 * iterates on
	 * iterates on Y-z while an X remains fixed<br>
	 * ie. ordered pair: X,Y aka X->Y<br>
	 * 
	 * @param priForwardDB
	 *            the database that contains initial which is connected to the terminal items below<br>
	 * @param priBackwardDB
	 *            the database that contains the terminal items we will be iterating upon<br>
	 * @param fixedInitialObjectInFirstDB
	 *            the initial in first DB, we iterate on this items' children(aka its terminals)<br>
	 */
	protected IteratorOnTerminalNodes_InDualPriDBs( final Database priForwardDB, final Database priBackwardDB,
			final NodeGeneric fixedInitialObjectInFirstDB ) {
		super( priForwardDB, fixedInitialObjectInFirstDB );
		assert fixedInitialObjectInFirstDB == _initialNode;
		assert null != priBackwardDB;
		reverse = priBackwardDB;
	}
	
	
	/**
	 * this, and any adds(if any, tho none exist yet), operate on both (pri)databases <br>
	 * ie. A->B && B<-A in the A<->B relationship, when deleting A->B it will also delete B<-A<br>
	 * note: A->B differs from B->A (notice the arrow)<br>
	 */
	@Override
	public void delete() {
		// initialObject->current
		final NodeGeneric current = super.getCurrent();
		super.delete();// del this: initialObject->current
		deleteFromReverse( current );
	}
	
	
	@Override
	public void deleteAll() {// we don't need this
		NodeGeneric cur;
		while ( ( cur = goFirst() ) != null ) {
			super.delete();
			deleteFromReverse( cur );
		}
		assert size() == 0;// but the reverse's size has nothing to do with this
	}
	
	
	/**
	 * @param delThisOne
	 */
	private void deleteFromReverse( final NodeGeneric delThisOne ) {
		assert null != delThisOne;
		final IterOnTerminalNodes_InOnePriDB iterReverse = new IterOnTerminalNodes_InOnePriDB( reverse, delThisOne );
		try {
			final NodeGeneric l = iterReverse.goTo( _initialNode );// pos on initialObject in this tuple:
																	// current->initialObject
			assert null != l : "the specified Node for deletion was not found";
			iterReverse.delete();// del this: current<-initialObject
			iterReverse.success();
		} finally {
			iterReverse.finished();
		}
	}
}
