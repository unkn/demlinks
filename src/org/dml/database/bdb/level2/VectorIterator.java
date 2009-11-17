/**
 * File creation: Nov 17, 2009 4:39:48 PM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
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



import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.RunTime;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;



/**
 * parses data of a key->data primary dbase
 * 
 */
public class VectorIterator<InitialType, TerminalType> {
	
	private final Database						db;
	private final InitialType					initialObject;					// key
																				
	private final EntryBinding<InitialType>		initialBinding;
	private final EntryBinding<TerminalType>	terminalBinding;
	private Cursor								cursor					= null;
	
	private TerminalType						currentTerminalObject	= null;
	private final Level1_Storage_BerkeleyDB		bdbL1;
	private TransactionCapsule					txn						= null;
	private DatabaseEntry						deKey					= null;
	private DatabaseEntry						deData					= null;
	
	public VectorIterator( Level1_Storage_BerkeleyDB bdb_L1,
			Database whichPriDB, InitialType initialObject1,
			EntryBinding<InitialType> initialBinding1,
			EntryBinding<TerminalType> terminalBinding1 ) {

		RunTime.assertNotNull( bdb_L1, whichPriDB, initialObject1,
				initialBinding1, terminalBinding1 );
		bdbL1 = bdb_L1;
		db = whichPriDB;
		initialObject = initialObject1;
		initialBinding = initialBinding1;
		terminalBinding = terminalBinding1;
		deKey = new DatabaseEntry();
		initialBinding.objectToEntry( initialObject, deKey );
		deData = new DatabaseEntry();
	}
	
	private final Cursor getCursor() throws DatabaseException {

		if ( null == cursor ) {
			txn = TransactionCapsule.getNewTransaction( bdbL1 );
			cursor = db.openCursor( txn.get(), CursorConfig.READ_COMMITTED );
		}
		RunTime.assertNotNull( cursor );
		return cursor;
	}
	
	public final void close() throws DatabaseException {

		if ( null != cursor ) {
			try {
				cursor.close();
			} catch ( DatabaseException e ) {
				txn.abort();
				throw e;
			}
			txn.commit();
		}
	}
	
	/**
	 * you have to call this first, if you call goNext() you get exception
	 * 
	 * @throws DatabaseException
	 */
	public void goFirst() throws DatabaseException {

		deData.setSize( 0 );
		RunTime.assertTrue( deData.getOffset() == 0 );
		OperationStatus ret = this.getCursor().getSearchKey( deKey, deData,
				LockMode.RMW );
		if ( OperationStatus.SUCCESS == ret ) {
			currentTerminalObject = terminalBinding.entryToObject( deData );
		} else {
			currentTerminalObject = null;
		}
	}
	
	public TerminalType now() {

		return currentTerminalObject;
	}
	
	public void goNext() throws DatabaseException {

		RunTime.assertTrue( deData.getOffset() == 0 );
		OperationStatus ret = this.getCursor().getNextDup( deKey, deData,
				LockMode.RMW );
		if ( OperationStatus.SUCCESS == ret ) {
			currentTerminalObject = terminalBinding.entryToObject( deData );
		} else {
			currentTerminalObject = null;
		}
	}
	
	public void goPrev() throws DatabaseException {

		RunTime.assertTrue( deData.getOffset() == 0 );
		OperationStatus ret = this.getCursor().getPrevDup( deKey, deData,
				LockMode.RMW );
		if ( OperationStatus.SUCCESS == ret ) {
			currentTerminalObject = terminalBinding.entryToObject( deData );
		} else {
			currentTerminalObject = null;
		}
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	public int count() throws DatabaseException {

		this.goFirst();// FIXME:
		return this.getCursor().count();
	}
	
	// goLast() is too hard to implement, due to BDB not giving support for it
}
