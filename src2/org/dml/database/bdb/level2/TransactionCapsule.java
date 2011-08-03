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

import com.sleepycat.db.*;



/**
 * 
 *
 */
public class TransactionCapsule {
	
	private Transaction			tx		= null;
	private TransactionConfig	txConf	= null;
	
	
	private TransactionCapsule() {
		//
	}
	
	
	/**
	 * @param bdb
	 * @return never null
	 * @throws DatabaseException
	 */
	public final static TransactionCapsule getNewTransaction( final Level1_Storage_BerkeleyDB bdb ) throws DatabaseException {
		
		RunTime.assumedNotNull( bdb );
		final TransactionCapsule txn = new TransactionCapsule();
		txn.txConf = new TransactionConfig();
		// txn.txConf.setNoSync( false );
		txn.txConf.setNoWait( true );
		// .setDurability( Durability.COMMIT_NO_SYNC );
		// txn.txConf.setReadCommitted( true );
		txn.txConf.setReadUncommitted( false );
		// txn.txConf.setSerializableIsolation( true );//actually, inherited from environment!
		// txn.txConf.setSync( true );
		// txn.txConf.setWriteNoSync( false );
		RunTime.assumedFalse( txn.txConf.getReadUncommitted() );
		txn.tx = bdb.getEnvironment().beginTransaction( null, txn.txConf );
		return txn;
	}
	
	
	/**
	 */
	public void abort() {
		try {
			tx.abort();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	/**
	 */
	public void commit() {
		try {
			tx.commit();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}// this may throw
	}
	
	
	/**
	 * @return Transaction
	 */
	public Transaction get() {
		RunTime.assumedNotNull( tx );
		return tx;
	}
}
