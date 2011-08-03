/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */



package org.dml.database.bdb.level2;



import org.dml.database.bdb.level1.*;
import org.dml.tools.*;
import org.q.*;

import com.sleepycat.bind.*;
import com.sleepycat.db.*;



/**
 * iterates on 'data' of a key->data in a BDB-primary-dbase ie. not secondaryDB<br>
 * key=initial<br>
 * data=terminal<br>
 * we iterate only on terminals<br>
 * the iteration order is not guaranteed, ie. the terminals are stored as in a
 * Set, not as in an ordered list; however in practice they're sorted
 * alphabetically so to speak, but shouldn't count on this!<br>
 * //FIXME: maybe make a way to not throw DatabaseException from here, because this is being used in dml environment
 * TODO: make it thread safe
 * 
 * @param <InitialType>
 *            this is the "parent"
 * @param <TerminalType>
 *            iterates on these
 */
public class BDBVectorIterator<InitialType, TerminalType> implements VectorIterator<TerminalType> {
	
	private final Database						db;
	private final InitialType					initialObject;								// key
																							
	private final EntryBinding<InitialType>		initialBinding;
	private final EntryBinding<TerminalType>	terminalBinding;
	private Cursor								cursor					= null;
	
	private TerminalType						currentTerminalObject	= null;
	private final Level1_Storage_BerkeleyDB		bdbL1;
	private TransactionCapsule					txn						= null;
	private DatabaseEntry						deKey					= null;
	private DatabaseEntry						deData					= null;
	
	// only read locks (I hope)
	private static final LockMode				Locky					= LockMode.DEFAULT;
	
	
	/**
	 * DON'T forget to call {@link #close()} when done using this iterator!
	 * 
	 * @param bdb_L1
	 * @param whichPriDB
	 * @param initialObject1
	 * @param initialBinding1
	 * @param terminalBinding1
	 */
	public BDBVectorIterator( final Level1_Storage_BerkeleyDB bdb_L1, final Database whichPriDB,
			final InitialType initialObject1, final EntryBinding<InitialType> initialBinding1,
			final EntryBinding<TerminalType> terminalBinding1 ) {
		
		RunTime.assumedNotNull( bdb_L1, whichPriDB, initialObject1, initialBinding1, terminalBinding1 );
		bdbL1 = bdb_L1;
		db = whichPriDB;
		initialObject = initialObject1;
		initialBinding = initialBinding1;
		terminalBinding = terminalBinding1;
		deKey = new DatabaseEntry();
		initialBinding.objectToEntry( initialObject, deKey );
		deData = new DatabaseEntry();
		// TODO add transaction parameter and if null then make own tx
		// maybe this won't work as expected; think again
	}
	
	
	@Override
	public Level1_Storage_BerkeleyDB getBDBL1() {
		RunTime.assumedNotNull( bdbL1 );
		return bdbL1;
	}
	
	
	private final Cursor getCursor() throws DatabaseException {
		
		if ( null == cursor ) {
			txn = TransactionCapsule.getNewTransaction( this.getBDBL1() );
			cursor = db.openCursor( txn.get(), CursorConfig.READ_COMMITTED );
		}
		RunTime.assumedNotNull( cursor );
		return cursor;
	}
	
	
	/**
	 */
	@Override
	public void goFirst() {
		
		deData.setSize( 0 );
		RunTime.assumedTrue( deData.getOffset() == 0 );
		OperationStatus ret;
		try {
			ret = this.getCursor().getSearchKey( deKey, deData, Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( OperationStatus.SUCCESS == ret ) {
			this.setNow( terminalBinding.entryToObject( deData ) );
		} else {
			this.setNow( null );
		}
	}
	
	
	@Override
	public void goTo( final TerminalType terminal ) {
		
		terminalBinding.objectToEntry( terminal, deData );
		RunTime.assumedTrue( deData.getOffset() == 0 );
		OperationStatus ret;
		try {
			ret = this.getCursor().getSearchBoth( deKey, deData, Locky );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( OperationStatus.SUCCESS == ret ) {
			this.setNow( terminalBinding.entryToObject( deData ) );
		} else {
			this.setNow( null );
		}
	}
	
	
	@Override
	public TerminalType now() {
		
		return currentTerminalObject;
	}
	
	
	private void setNow( final TerminalType newNow ) {
		
		currentTerminalObject = newNow;// null allowed
	}
	
	
	@Override
	public void goNext() {
		
		if ( null != this.now() ) {
			RunTime.assumedTrue( deData.getOffset() == 0 );
			OperationStatus ret;
			try {
				ret = this.getCursor().getNextDup( deKey, deData, Locky );
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			}
			if ( OperationStatus.SUCCESS == ret ) {
				this.setNow( terminalBinding.entryToObject( deData ) );
			} else {
				this.setNow( null );
			}
		} else {
			RunTime.badCall( "called goNext() while now() was null" );
		}
	}
	
	
	@Override
	public void goPrev() {
		
		if ( null != this.now() ) {
			RunTime.assumedTrue( deData.getOffset() == 0 );
			OperationStatus ret;
			try {
				ret = this.getCursor().getPrevDup( deKey, deData, Locky );
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			}
			if ( OperationStatus.SUCCESS == ret ) {
				this.setNow( terminalBinding.entryToObject( deData ) );
			} else {
				this.setNow( null );
			}
		} else {
			RunTime.badCall( "called goPrev() while now() was null" );
		}
	}
	
	
	@Override
	public long count() {
		if ( this.now() == null ) {
			this.goFirst();
			if ( null == this.now() ) {
				return 0;
			}
			int ret;
			try {
				ret = this.getCursor().count();
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			}
			this.setNow( null );
			return ret;
		} else {
			try {
				return this.getCursor().count();
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			}
		}
	}
	
	
	@Override
	public final void close() {
		
		if ( null != cursor ) {
			try {
				cursor.close();
			} catch ( final Throwable t ) {
				RunTime.throPostponed( t );
				try {
					txn.abort();
				} finally {
					txn = null;
				}
				RunTime.throwAllThatWerePostponed();
			} finally {
				cursor = null;
			}
			try {
				txn.commit();
			} finally {
				txn = null;
			}
		}
	}
	
	
	@Override
	public boolean delete() {
		
		if ( null == this.now() ) {
			return false;
		}
		OperationStatus ret;
		try {
			ret = this.getCursor().delete();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		this.setNow( null );
		RunTime.assumedTrue( OperationStatus.SUCCESS == ret );
		return OperationStatus.SUCCESS == ret;
	}
	
	// TODO: goLast() is too expensive to implement, due to BDB not giving support for it
}
