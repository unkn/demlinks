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



import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;
import org.references.method.MethodParams;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;



/**
 * parses data of a key->data primary dbase<br>
 * key=initial<br>
 * data=terminal<br>
 * we iterate only on terminals<br>
 * the iteration order is not guaranteed, ie. the terminals are stored as in a
 * Set, not as in an ordered list; however in practice they're sorted
 * alphabetically so to speak, but shouldn't count on this!<br>
 * //FIXME: maybe make a way to not throw DatabaseException from here, because this is being used in dml environment
 */
public class BDBVectorIterator<InitialType, TerminalType>
		extends
		Initer
{
	
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
	
	
	public BDBVectorIterator(
			Level1_Storage_BerkeleyDB bdb_L1,
			Database whichPriDB,
			InitialType initialObject1,
			EntryBinding<InitialType> initialBinding1,
			EntryBinding<TerminalType> terminalBinding1 )
	{
		
		RunTime.assumedNotNull(
								bdb_L1,
								whichPriDB,
								initialObject1,
								initialBinding1,
								terminalBinding1 );
		bdbL1 = bdb_L1;
		db = whichPriDB;
		initialObject = initialObject1;
		initialBinding = initialBinding1;
		terminalBinding = terminalBinding1;
		deKey = new DatabaseEntry();
		initialBinding.objectToEntry(
										initialObject,
										deKey );
		deData = new DatabaseEntry();
		// TODO add transaction parameter and if null then make own tx
		// maybe this won't work as expected; think again
	}
	

	private final
			Cursor
			getCursor()
					throws DatabaseException
	{
		
		if ( null == cursor )
		{
			txn = TransactionCapsule.getNewTransaction( bdbL1 );
			cursor = db.openCursor(
									txn.get(),
									CursorConfig.READ_COMMITTED );
		}
		RunTime.assumedNotNull( cursor );
		return cursor;
	}
	

	/**
	 * @throws DatabaseException
	 */
	public
			void
			goFirst()
					throws DatabaseException
	{
		
		deData.setSize( 0 );
		RunTime.assumedTrue( deData.getOffset() == 0 );
		OperationStatus ret = this.getCursor().getSearchKey(
																deKey,
																deData,
																Locky );
		if ( OperationStatus.SUCCESS == ret )
		{
			this.setNow( terminalBinding.entryToObject( deData ) );
		}
		else
		{
			this.setNow( null );
		}
	}
	

	public
			void
			goTo(
					TerminalType terminal )
					throws DatabaseException
	{
		
		terminalBinding.objectToEntry(
										terminal,
										deData );
		RunTime.assumedTrue( deData.getOffset() == 0 );
		OperationStatus ret = this.getCursor().getSearchBoth(
																deKey,
																deData,
																Locky );
		if ( OperationStatus.SUCCESS == ret )
		{
			this.setNow( terminalBinding.entryToObject( deData ) );
		}
		else
		{
			this.setNow( null );
		}
	}
	

	public
			TerminalType
			now()
	{
		
		return currentTerminalObject;
	}
	

	private
			void
			setNow(
					TerminalType newNow )
	{
		
		currentTerminalObject = newNow;// null allowed
	}
	

	public
			void
			goNext()
					throws DatabaseException
	{
		
		if ( null != this.now() )
		{
			RunTime.assumedTrue( deData.getOffset() == 0 );
			OperationStatus ret = this.getCursor().getNextDup(
																deKey,
																deData,
																Locky );
			if ( OperationStatus.SUCCESS == ret )
			{
				this.setNow( terminalBinding.entryToObject( deData ) );
			}
			else
			{
				this.setNow( null );
			}
		}
		else
		{
			RunTime.badCall( "called goNext() while now() was null" );
		}
	}
	

	public
			void
			goPrev()
					throws DatabaseException
	{
		
		if ( null != this.now() )
		{
			RunTime.assumedTrue( deData.getOffset() == 0 );
			OperationStatus ret = this.getCursor().getPrevDup(
																deKey,
																deData,
																Locky );
			if ( OperationStatus.SUCCESS == ret )
			{
				this.setNow( terminalBinding.entryToObject( deData ) );
			}
			else
			{
				this.setNow( null );
			}
		}
		else
		{
			RunTime.badCall( "called goPrev() while now() was null" );
		}
	}
	

	/**
	 * @return
	 */
	public
			int
			count()
	{
		try
		{
			if ( this.now() == null )
			{
				this.goFirst();
				if ( null == this.now() )
				{
					return 0;
				}
				int ret = this.getCursor().count();
				this.setNow( null );
				return ret;
			}
			else
			{
				return this.getCursor().count();
			}
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
			return 0;// dummy
		}
	}
	

	@Override
	protected
			void
			done(
					MethodParams params )
	{
		
		try
		{
			this.close();
		}
		catch ( Throwable t )
		{
			Log.thro( t.getLocalizedMessage() );
			RunTime.throWrapped( t );
		}
		
	}
	

	private final
			void
			close()
	{
		
		if ( null != cursor )
		{
			try
			{
				cursor.close();
			}
			catch ( Throwable t )
			{
				RunTime.throPostponed( t );
				txn = txn.abort();
				RunTime.throwAllThatWerePosponed();
			}
			finally
			{
				cursor = null;
			}
			try
			{
				txn = txn.commit();
			}
			catch ( Throwable t )
			{
				RunTime.throWrapped( t );
			}
		}
	}
	

	@Override
	protected
			void
			start(
					MethodParams params )
	{
		
		if ( null != params )
		{
			RunTime.badCall( "not accepting any parameters here" );
		}
		// try {
		// this.goFirst();// init cursor
		// // if ( this.now() == null ) {
		// // RunTime.bug( "cursor init failed" );
		// // }
		// throw new StorageException( de );
		// }
	}
	

	/**
	 * @throws DatabaseException
	 * 
	 */
	public
			boolean
			delete()
					throws DatabaseException
	{
		
		if ( null == this.now() )
		{
			return false;
		}
		OperationStatus ret = this.getCursor().delete();
		this.setNow( null );
		RunTime.assumedTrue( OperationStatus.SUCCESS == ret );
		return OperationStatus.SUCCESS == ret;
	}
	
	// goLast() is too expensive to implement, due to BDB not giving support for
	// it
}
