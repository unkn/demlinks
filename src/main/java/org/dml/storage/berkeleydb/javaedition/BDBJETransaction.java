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
package org.dml.storage.berkeleydb.javaedition;

import org.dml.storage.commons.*;
import org.q.*;

import com.sleepycat.je.*;



/**
 * simulates nested transactions by using only one root transaction (currently for all threads the same one)<br>
 * but this does support the possibility of having real-nested transactions implemented, all modifs only need to go here in this
 * class<br>
 * FIXME: not yet thread-safe
 * FIXME: check if sibling transactions are doable, ie. multiple threads each using different Transaction instance at the same
 * time and if so, do ThreadLocal\<Transaction\> instead
 */
public class BDBJETransaction
		implements TransactionGeneric
{
	
	private static ThreadLocal<Transaction>	bdbTransactionSingleton	= new ThreadLocal<Transaction>()
																	{
																		
																		/*
																		 * (non-Javadoc)
																		 * 
																		 * @see java.lang.ThreadLocal#initialValue()
																		 */
																		@Override
																		protected Transaction initialValue() {
																			return null;
																		}
																	};
	
	
	private static Environment				_env					= null;
	private final static TransactionConfig	txnConfig				= new TransactionConfig();
	// .setDurability( BerkEnv.DUR )//
	// .setNoWait( true /* should be false */)
	// // should be false here, but then it may wait
	// // indefinitely
	// .setSerializableIsolation( true );
	
	static {
		// txnConfig.setSnapshot( true );
		// txnConfig.setNoWait( true );// should be false here, but then it may wait indefinitely
		// txnConfig.set
	}
	
	private volatile static int				depth					= -1;
	private boolean							failed					= true;
	private final int						currentTransactionDepth;
	private boolean							finished				= false;
	
	
	private void reinit() {
		bdbTransactionSingleton.set( null );
		_env = null;
	}
	
	
	public static Transaction getCurrentTransaction( final Environment env ) {
		handleEnv( env );
		if ( StorageBDBJE.ENABLE_TRANSACTIONS ) {
			assert null != bdbTransactionSingleton.get() : "you never called .begin(...) yet";
			return bdbTransactionSingleton.get();
		} else {
			return null;
		}
	}
	
	
	private static void handleEnv( final Environment env ) {
		assert null != env;
		if ( null == _env ) {
			_env = env;
		} else {// _env was already set
			assert _env == env : "trying to use transactions for two different environment at the same time,"
				+ " not yet implemented! tho should be easy to do; passedEnv=" + env + " alreadyEnv=" + _env;// TODO
		}
		// if asserts are disabled _env will still be null here, heh
	}
	
	
	/**
	 * starts a new child transaction, if no root then starts root<br>
	 * defaults to failed unless you call success() on it<br>
	 * TODO: since sibling transactions are supported(right?) then we should allow specifying the parent txn
	 * 
	 * @param env
	 * @return
	 * @throws DatabaseException
	 */
	public static BDBJETransaction beginChild( final Environment env ) {
		handleEnv( env );
		if ( StorageBDBJE.ENABLE_TRANSACTIONS ) {
			if ( null == bdbTransactionSingleton.get() ) {
				try {
					bdbTransactionSingleton.set( _env.beginTransaction( null, txnConfig ) );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
			}
			assert null != bdbTransactionSingleton.get();
		}
		return new BDBJETransaction();
	}
	
	
	/**
	 * private constructor
	 */
	private BDBJETransaction() {
		depth++;
		currentTransactionDepth = depth;
		Q.info( "beginning transaction at depth=" + currentTransactionDepth );
	}
	
	
	/**
	 * @return the berkeleydb transaction for passing it to the methods which actually require transactional usage
	 */
	public Transaction getTransaction() {
		return bdbTransactionSingleton.get();
	}
	
	
	@Override
	public void success() {
		assert !finished;
		failed = false;
	}
	
	
	/**
	 * if you call this on a child transaction, the parents/root will not be marked as failed!<br>
	 * it is assumed that either some exceptions(which caused this to fail) will propagate or you're manually IF-ing around(if
	 * you manually failed this), for the parent transactions to fail too<br>
	 */
	@Override
	public void failure() {
		assert !finished;
		failed = true;
	}
	
	
	/**
	 * commits or aborts the transaction, by default it aborts, unless last thing you called was success() as opposed to
	 * failure()[which is the default]
	 */
	@Override
	public void finished() {
		assert !finished;
		finished = true;
		assert depth >= 0;
		try {
			assert currentTransactionDepth == depth : "you're trying to finish() a transaction at a different depth"
				+ "(ie. you forgot to finish() the child transaction(s) first): " + "thisTxn's Depth="
				+ currentTransactionDepth + " the current depth=" + depth;
			
			if ( failed ) {
				if ( 0 == currentTransactionDepth ) {
					try {
						if ( StorageBDBJE.ENABLE_TRANSACTIONS ) {
							Q.info( "TXN: real abort" );
							bdbTransactionSingleton.get().abort();
						}
					} catch ( final DatabaseException e ) {
						Q.rethrow( e );
					} finally {
						reinit();
					}
				} else {
					Q.info( "TXN: simulated abort" );
				}
			} else {
				if ( 0 == currentTransactionDepth ) {
					try {
						if ( StorageBDBJE.ENABLE_TRANSACTIONS ) {
							Q.info( "TXN: real commit" );
							bdbTransactionSingleton.get().commit();
						}
					} catch ( final DatabaseException e ) {
						Q.rethrow( e );
					} finally {
						reinit();
					}
				} else {
					Q.info( "TXN: simulated commit" );
				}
			}
		} finally {
			if ( depth >= 0 ) {
				depth--;
			}
			assert depth >= -1;
		}
	}
	
	
}
