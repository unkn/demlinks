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
import org.dml.storage.berkeleydb.commons.*;
import org.q.*;

import com.sleepycat.db.*;



/**
 * iterator on children!<br>
 * <br>
 * X,Y aka X->Y<br>
 * initial->child<br>
 * initial aka leftmost aka first aka initial aka X<br>
 * child aka rightmost aka last aka child aka Y<br>
 * XXX: if you implement insert, be aware that inserts as deletes must be executed in both databases, else inconsistencies!
 */
public class IterOnChildNodes_InOnePriDB
		implements TransactionGeneric
// implements GenericIteratorOnChildNodes
{
	
	private final Database				db;
	protected final NodeGeneric			_initialNode;										// key
																							
	private Cursor						cursor;
	private final BDBTransaction		txn;
	private boolean						failed			= true;
	
	private static final LockMode		Locky			= StorageBDBNative.CURSORLOCK;
	private static final CursorConfig	cursorConfig	= StorageBDBNative.CURSORCONFIG;
	
	
	/**
	 * @param whichPriDB
	 * @param initialNode
	 *            iteration is done on the children of this initial!
	 */
	public IterOnChildNodes_InOnePriDB( final Database whichPriDB, final NodeGeneric initialNode ) {
		assert null != whichPriDB;
		assert null != initialNode;
		
		db = whichPriDB;
		_initialNode = initialNode;
		
		try {
			txn = BDBTransaction.beginChild( db.getEnvironment() );
			cursor = db.openCursor( txn.getTransaction(), IterOnChildNodes_InOnePriDB.cursorConfig );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		
		assert null != cursor;
	}
	
	
	
	private DatabaseEntry getNewInitialObjectDataEntry() {
		// XXX: see if this can be shared, for current instance of `this` anyway - foo it, it's better as it is!
		final DatabaseEntry deKey = new DatabaseEntry();
		nodeToEntry( _initialNode, deKey );
		return deKey;
	}
	
	
	private static void nodeToEntry( final NodeGeneric readNode, final DatabaseEntry outDE ) {
		NodeBDB.binding.objectToEntry( readNode, outDE );
	}
	
	
	private static NodeGeneric entryToNode( final DatabaseEntry readDE ) {
		return NodeBDB.binding.entryToObject( readDE );
	}
	
	
	public NodeGeneric goFirst() {
		final DatabaseEntry deData = new DatabaseEntry();
		assert deData.getSize() == 0;
		assert deData.getOffset() == 0;
		OperationStatus ret;
		try {
			ret = cursor.
			// getFirst( - fail, it's first key in the database instead!
				getSearchKey( getNewInitialObjectDataEntry(), deData, IterOnChildNodes_InOnePriDB.Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( ret.equals( OperationStatus.SUCCESS ) ) {
			return entryToNode( deData );
		} else {
			return null;
		}
	}
	
	
	public NodeGeneric goTo( final NodeGeneric childNode ) {
		final DatabaseEntry deData = new DatabaseEntry();
		nodeToEntry( childNode, deData );
		assert deData.getOffset() == 0;
		assert deData.getSize() > 0;
		OperationStatus ret;
		try {
			ret = cursor.getSearchBoth( getNewInitialObjectDataEntry(), deData, IterOnChildNodes_InOnePriDB.Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( ret.equals( OperationStatus.SUCCESS ) ) {
			final NodeGeneric foundOne = entryToNode( deData );
			assert foundOne.equals( childNode );
			return foundOne;
		} else {
			return null;
		}
	}
	
	
	protected NodeGeneric getCurrent() {
		final DatabaseEntry deKey = new DatabaseEntry();
		final DatabaseEntry deData = new DatabaseEntry();
		
		OperationStatus ret = OperationStatus.NOTFOUND;
		try {
			ret = cursor.getCurrent( deKey, deData, IterOnChildNodes_InOnePriDB.Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		} catch ( final IllegalArgumentException iae ) {// this worked for bdbje: IllegalStateException ise ) {
			// ignore, which will cause prev ret value to be kept and null returned for this method
			// FIXME: fix this, to find another way, else much spam on console:
			// BDB0631 Cursor position must be set before performing this operation
			// but also it seems slower...
		}
		
		if ( ret.equals( OperationStatus.SUCCESS ) ) {
			assert _initialNode.equals( entryToNode( deKey ) );
			return entryToNode( deData );
		} else {
			return null;
		}
	}
	
	
	/**
	 * @return null if no next
	 */
	public NodeGeneric goNext() {
		assert null != getCurrent() : Q.badCall( "called goNext() while current was null" );
		final DatabaseEntry deData =
		// new DatabaseEntry();
			getNewCurrentObjectDataEntry();
		// assert deData.getOffset() == 0;
		OperationStatus ret;
		try {
			ret = cursor.getNextDup(
			// new DatabaseEntry()
				getNewInitialObjectDataEntry(),
				deData,
				IterOnChildNodes_InOnePriDB.Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( ret.equals( OperationStatus.SUCCESS ) ) {
			return entryToNode( deData );
		} else {
			return null;
		}
	}
	
	
	private DatabaseEntry getNewCurrentObjectDataEntry() {
		final DatabaseEntry deData = new DatabaseEntry();
		final NodeGeneric theNow = getCurrent();
		assert null != theNow;
		nodeToEntry( theNow, deData );
		assert deData.getOffset() == 0;
		
		return deData;
	}
	
	
	public NodeGeneric goPrev() {
		assert null != getCurrent() : Q.badCall( "called goPrev() while now() was null" );
		final DatabaseEntry deData = getNewCurrentObjectDataEntry();
		// assert deData.getOffset() == 0;
		OperationStatus ret;
		try {
			ret = cursor.getPrevDup( getNewInitialObjectDataEntry(), deData, IterOnChildNodes_InOnePriDB.Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( ret.equals( OperationStatus.SUCCESS ) ) {
			return entryToNode( deData );
		} else {
			return null;
		}
	}
	
	
	public int size() {
		final NodeGeneric now = getCurrent();
		if ( null == now ) {
			if ( null == goFirst() ) {
				return 0;// has no elements
			}
		}
		
		int ret;
		try {
			ret = cursor.count();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		
		if ( null != now ) {
			goTo( now );
			assert getCurrent().equals( now );
		} else {
			assert getCurrent() != null;// since it went First because cursor needed to be inited
		}
		return ret;
	}
	
	
	/**
	 * not supposed to be directly used ! due to delete happening in only one db, and we usually need it in both! <br>
	 * else inconsistency between the two databases ie. A->B deleted but B->A not<br>
	 */
	protected void delete() {
		// assert this.getClass() == IteratorOnChildNodes_InDualPriDBs.class : "just making sure you're not using delete()"
		// + " from here instead of from that class, which deletes both links in both databases" + " the current class is: "
		// + this.getClass(); actually this has to be allowed due to this being the class
		assert null != getCurrent() : Q.badCall( "called delete on no current item! ie. now() was null" );
		OperationStatus ret;
		try {
			ret = cursor.delete();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		assert null == getCurrent();
		assert ret.equals( OperationStatus.SUCCESS );
	}
	
	
	@Override
	public void success() {
		failed = false;
	}
	
	
	@Override
	public void failure() {
		failed = true;
	}
	
	
	// public void finished( final boolean success ) {
	// if ( success ) {
	// success();
	// } else {
	// failure();
	// }
	// finished();
	// }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.storage.GenericTransaction#finish()
	 */
	@Override
	public void finished() {
		assert !isClosed() : Q.badCall( "was already closed!" );
		try {
			try {
				cursor.close();
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			} finally {
				cursor = null;
			}
			Q.info( "closed cursor..." );
			
			// ==========
			if ( failed ) {
				txn.failure();
			} else {
				txn.success();
			}
		} finally {
			txn.finished();
		}
	}
	
	
	public boolean isClosed() {
		return cursor == null;
	}
	
	
	
	// XXX: goLast() is too expensive to implement, due to BDB not giving support for it, I'd have to parse all elements with
	// cursor to reach last one
}
