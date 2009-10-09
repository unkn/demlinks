/**
 * File creation: Jun 6, 2009 10:13:37 PM
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


package org.dml.database.bdb;






/**
 * 
 *
 */
public class TransactionCapsule {
	
	private Transaction			tx;
	private TransactionConfig	txConf;
	
	private TransactionCapsule() {

	}
	
	/**
	 * @return never null
	 * @throws DatabaseException
	 */
	public final static TransactionCapsule getNewTransaction()
			throws DatabaseException {

		TransactionCapsule txn = new TransactionCapsule();
		
		txn.txConf = new TransactionConfig();
		txn.txConf.setNoSync( false );
		txn.txConf.setNoWait( true );
		// txn.txConf.setReadCommitted( true );
		txn.txConf.setReadUncommitted( false );
		txn.txConf.setSerializableIsolation( true );
		txn.txConf.setSync( true );
		txn.txConf.setWriteNoSync( false );
		
		txn.tx = BerkeleyDB.getEnvironment().beginTransaction( null, txn.txConf );
		
		return txn;
	}
	
	/**
	 * @return null
	 * @throws DatabaseException
	 */
	public TransactionCapsule abort() throws DatabaseException {

		tx.abort();
		return null;
	}
	
	/**
	 * @return null
	 * @throws DatabaseException
	 */
	public TransactionCapsule commit() throws DatabaseException {

		tx.commit();
		return null;
	}
	
	/**
	 * @return
	 */
	public Transaction get() {

		return tx;
	}
}
