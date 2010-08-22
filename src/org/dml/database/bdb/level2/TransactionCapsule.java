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
import org.dml.tools.RunTime;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;



/**
 * 
 *
 */
public class TransactionCapsule
{
	
	private Transaction			tx		= null;
	private TransactionConfig	txConf	= null;
	
	
	private TransactionCapsule()
	{
		//
	}
	

	/**
	 * @param bdb
	 * @return never null
	 * @throws DatabaseException
	 */
	public final static
			TransactionCapsule
			getNewTransaction(
								Level1_Storage_BerkeleyDB bdb )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( bdb );
		TransactionCapsule txn = new TransactionCapsule();
		try
		{
			txn.txConf = new TransactionConfig();
			// txn.txConf.setNoSync( false );
			txn.txConf.setNoWait(
									true ).setDurability(
															Durability.COMMIT_NO_SYNC );
			// txn.txConf.setReadCommitted( true );
			txn.txConf.setReadUncommitted( false );
			txn.txConf.setSerializableIsolation( true );
			// txn.txConf.setSync( true );
			// txn.txConf.setWriteNoSync( false );
			RunTime.assumedFalse( txn.txConf.getReadUncommitted() );
			txn.tx = bdb.getEnvironment().beginTransaction(
															null,
															txn.txConf );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
		return txn;
	}
	

	/**
	 * @return null
	 */
	public
			TransactionCapsule
			abort()
	{
		try
		{
			tx.abort();
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
		return null;
	}
	

	/**
	 * @return null
	 * @throws DatabaseException
	 */
	public
			TransactionCapsule
			commit()
					throws DatabaseException
	{
		try
		{
			tx.commit();
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
		return null;
	}
	

	/**
	 * @return
	 */
	public
			Transaction
			get()
	{
		RunTime.assumedNotNull( tx );
		return tx;
	}
}
