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


import org.dml.storage.berkeleydb.native_via_jni.*;
import org.dml.storage.commons.*;
import org.q.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.je.*;



/**
 * a database that stores sets<br>
 * a one to many database<br>
 * left<->right<br>
 * the leftmost aka left is like the set name(can be only one), and can have many (different)items aka right(s)<br>
 * ie.<br>
 * A->B<br>
 * A->C<br>
 * A->D<br>
 * B->C<br>
 * C->D<br>
 * see B is both an left and a right, same as C<br>
 * can't have:<br>
 * A->B<br>
 * A->C<br>
 * A->B (again), this can't be happening, it will detect first A->B and thus say it already exists<br>
 * <br>
 * not yet decided, but allowing so far:
 * A->A<br>
 * B->B<br>
 * maybe this restriction will happen in a higher level ie. DomainSet or something<br>
 */
public class BDBJE_SetOfNodes
		implements Level2Generic
{
	
	private static final String	backwardSuffix	= "_backward_but_also_primary";
	private Database			priForwardDB	= null;
	private Database			priBackwardDB	= null;
	private final Environment	env;
	
	
	/**
	 * 1-to-many<br>
	 * 
	 * @param env1
	 * @param dbName1
	 */
	public BDBJE_SetOfNodes( final StorageBDBJE env1, final String dbName1 ) {
		this( env1.getBDBEnv(), dbName1 );
	}
	
	
	/**
	 * 1-to-many<br>
	 * 
	 * @param env1
	 * @param dbName1
	 */
	public BDBJE_SetOfNodes( final Environment env1, final String dbName1 ) {
		// XXX: here we must allow only Environment to be passed instead of BerkEnv, due to BerkEnv not yet being initialized
		// (ie. we're still in constructor) when we are called here, so giving access to the BerkEnv instance will potentially
		// allow usage of methods from it that will throw (ie. null) exceptions due to not yet being initialized
		Q.info( "opening 1toMany database: " + dbName1 );
		assert null != env1;
		env = env1;
		
		assert null != dbName1;
		assert !dbName1.isEmpty();
		
		final DatabaseConfig dbConf = new DatabaseConfig();// XXX:shared between both pri databases, is this
															// wise? appears so.
		dbConf.setAllowCreate( true );
		// dbConf.setDeferredWrite( false );// set this to false if you want SYNC or consistency
		// dbConf.setKeyPrefixing( false );
		// all duplicate-key but different data will be sorted for quick access, but this means you cannot store your
		// user-defined order, ie. consider A->B then A->C , B will always be before C no matter which operation went first
		// thus you cannot put A->C then A->B when parsing with iterator, thus consider A as a set containing B and C, w/o order
		dbConf.setSortedDuplicates( true );// must be true, also for quick finding; the order cannot be user-defined here!
		dbConf.setTransactional( StorageBDBJE.ENABLE_TRANSACTIONS );
		
		priForwardDB = env1.openDatabase( null/* BETransaction.getCurrentTransaction( env1 ) */, dbName1, dbConf );
		priBackwardDB = env1.openDatabase( null, dbName1 + BDBJE_SetOfNodes.backwardSuffix, dbConf );
	}
	
	
	public final String getDBName() {
		try {
			return priForwardDB.getDatabaseName();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	public void close() {
		Q.info( "closing " + this.getClass().getCanonicalName() + ": " + getDBName() );
		try {
			if ( null != priBackwardDB ) {
				priBackwardDB.close();
			}
			if ( null != priForwardDB ) {
				priForwardDB.close();
			}
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		}
	}
	
	
	@Override
	public boolean isVector( final NodeGeneric initialLong, final NodeGeneric childLong ) {
		assert null != initialLong;
		assert null != childLong;
		
		final DatabaseEntry keyEntry = new DatabaseEntry();
		LongBinding.longToEntry( initialLong.getId(), keyEntry );
		
		final DatabaseEntry dataEntry = new DatabaseEntry();
		LongBinding.longToEntry( childLong.getId(), dataEntry );
		
		OperationStatus ret1 = null, ret2 = null;
		// maybe a transaction here is unnecessary, however we don't want
		// another transaction (supposedly) to interlace between the two gets
		final BDBJETransaction txn = BDBJETransaction.beginChild( env );
		try {
			ret1 = priForwardDB.getSearchBoth( txn.getTransaction(), keyEntry, dataEntry, StorageBDBJE.LOCK );
			ret2 = priBackwardDB.getSearchBoth( txn.getTransaction(), dataEntry, keyEntry, StorageBDBJE.LOCK );
			txn.success();
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		} finally {
			txn.finished();
		}
		assert null != ret1;
		assert ret1.equals( ret2 ) : Q.bug( "one exists, the other doesn't; but should either both exist, or both not exist: "
			+ initialLong + "<->" + childLong );
		return ( ret1.equals( OperationStatus.SUCCESS ) );// ret2 is same
	}
	
	
	/**
	 * make sure that group (first,second) exist<br>
	 * notice that order matters, thus (second, first) is another grouping<br>
	 * this is like a new that doesn't throw if the group already exists<br>
	 * 
	 * @param initialLong
	 * @param childLong
	 * @return true if existed already; false if it didn't exist before call
	 */
	@Override
	public boolean ensureVector( final NodeGeneric initialLong, final NodeGeneric childLong ) {
		assert null != initialLong;
		assert null != childLong;
		
		return ( internal_makeVector( initialLong, childLong ).equals( OperationStatus.KEYEXIST ) );
	}
	
	
	/**
	 * it will throw if already exists!<br>
	 * 
	 * @param initialLong
	 * @param childLong
	 */
	@Override
	public void createNewVectorOrThrow( final NodeGeneric initialLong, final NodeGeneric childLong ) {
		assert null != initialLong;
		assert null != childLong;
		
		if ( ensureVector( initialLong, childLong ) ) {
			// final OperationStatus ret = internal_makeVector( initialLong, childLong );
			// if ( ret.equals( OperationStatus.KEYEXIST ) ) {
			throw Q.badCall( "you expected to create a new vector, and yet that vector already existed!" );
			// } else {
			// if ( !ret.equals( OperationStatus.SUCCESS ) ) {
			// Q.bug();
			// }
		}
	}
	
	
	/**
	 * @param initialLong
	 * @param childLong
	 * @return OperationStatus.SUCCESS or KEYEXIST
	 * @throws BugError
	 *             if inconsistency detected (ie. one link exists the other
	 *             doesn't)
	 */
	private OperationStatus internal_makeVector( final NodeGeneric initialLong, final NodeGeneric childLong ) {
		assert null != initialLong;
		assert null != childLong;
		
		final DatabaseEntry keyEntry_initial = new DatabaseEntry();
		LongBinding.longToEntry( initialLong.getId(), keyEntry_initial );
		
		final DatabaseEntry dataEntry_child = new DatabaseEntry();
		LongBinding.longToEntry( childLong.getId(), dataEntry_child );
		
		OperationStatus ret1 = null, ret2 = null;
		final BDBJETransaction txn = BDBJETransaction.beginChild( env );
		try {
			ret1 = priForwardDB.putNoDupData( txn.getTransaction(), keyEntry_initial, dataEntry_child );
			ret2 = priBackwardDB.putNoDupData( txn.getTransaction(), dataEntry_child, keyEntry_initial );
			txn.success();
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		} finally {
			txn.finished();
		}
		
		assert null != ret1;
		assert ( ret1.equals( ret2 ) ) : Q.bug( "one link exists and the other does not; should either both exist or neither" );
		assert ( ( ret1.equals( OperationStatus.SUCCESS ) ) || ( ret1.equals( OperationStatus.KEYEXIST ) ) ) : Q
			.bug( "was only expecting one of the two values" );
		return ret1;// which is same as ret2
	}
	
	
	/**
	 * @param initialObject
	 * @return iter
	 */
	@Override
	public IteratorGeneric_OnChildNodes getIterator_on_Children_of( final NodeGeneric initialObject ) {
		assert null != initialObject;
		
		return new IteratorOnChildNodes_InDualPriDBs_JE( priForwardDB, priBackwardDB, initialObject );
	}
	
	
	/**
	 * the parsed items are stored already in no particular order, ie. they are not supposedly ordered<br>
	 * though there may be order of insertion is kept, but there's no control given to user about order of items, thus consider
	 * this orderless<br>
	 * 
	 * @param ofChildObject
	 * @return iter
	 */
	@Override
	public IteratorGeneric_OnChildNodes getIterator_on_Initials_of( final NodeGeneric ofChildObject ) {
		assert null != ofChildObject;
		
		return new IteratorOnChildNodes_InDualPriDBs_JE( priBackwardDB, priForwardDB, ofChildObject );
	}
	
	
	@Override
	public int countInitials( final NodeGeneric ofChildObject ) {
		assert null != ofChildObject;
		
		final IterOnChildNodes_InOnePriDB_JE vi = new IterOnChildNodes_InOnePriDB_JE( priBackwardDB, ofChildObject );
		int count = -1;
		try {
			count = vi.size();
			vi.success();
		} finally {
			vi.finished();
		}
		return count;
	}
	
	
	@Override
	public int countChildren( final NodeGeneric ofInitialObject ) {
		assert null != ofInitialObject;
		
		final IterOnChildNodes_InOnePriDB_JE vi = new IterOnChildNodes_InOnePriDB_JE( priForwardDB, ofInitialObject );
		int count = -1;
		try {
			count = vi.size();
			vi.success();
		} finally {
			vi.finished();
		}
		return count;
	}
	
	
	/**
	 * must not be more than 1 found, else bug
	 * 
	 * @param initial1
	 * @param initial2
	 * @return null if not found
	 */
	@Override
	public NodeGeneric findCommonChildForInitials( final NodeGeneric initial1, final NodeGeneric initial2 ) {
		assert null != initial1;
		assert null != initial2;
		
		NodeGeneric found = null;
		// we choose the one with the least elements
		final IterOnChildNodes_InOnePriDB_JE iterFor1 = new IterOnChildNodes_InOnePriDB_JE( priForwardDB, initial1 );
		IterOnChildNodes_InOnePriDB_JE iterOnSmallest = iterFor1;
		NodeGeneric comparator = initial2;
		IterOnChildNodes_InOnePriDB_JE iterFor2 = null;
		try {
			iterFor2 = new IterOnChildNodes_InOnePriDB_JE( priForwardDB, initial2 );
			
			if ( iterFor1.size() > iterFor2.size() ) {
				iterOnSmallest = iterFor2;
				comparator = initial1;
			}
			
			// deInit the unused one
			if ( iterOnSmallest == iterFor2 ) {// by reference
				iterFor1.success();
				iterFor1.finished();
			} else {
				iterFor2.success();
				iterFor2.finished();
			}
			
			// parse iter1's elements and see if any is in iter2
			
			NodeGeneric now = iterOnSmallest.goFirst();
			while ( null != now ) {
				if ( isVector( comparator, now ) ) {
					// found one
					assert ( null == found ) : Q.bug( "supposed to be only one, but we found two" );
					found = now;
					// break; we don't break just in case we can find another one and thus find a bug=)
				}
				now = iterOnSmallest.goNext();
			}
			
			iterOnSmallest.success();
		} finally {
			try {
				iterOnSmallest.finished();
			} finally {
				iterOnSmallest = null;
			}
		}
		
		assert iterFor1.isClosed();
		assert ( null == iterFor2 ) || ( iterFor2.isClosed() );// this can be null
		return found;// can be null
	}
	
	
	/**
	 * @param initialObject
	 * @param childObject
	 * @return true if existed
	 */
	@Override
	public boolean removeVector( final NodeGeneric initialObject, final NodeGeneric childObject ) {
		
		assert null != initialObject;
		assert null != childObject;
		
		IteratorGeneric_OnChildNodes iter = getIterator_on_Children_of( initialObject );
		try {
			final NodeGeneric now = iter.goTo( childObject );
			if ( null == now ) {
				assert !isVector( initialObject, childObject );
				return false;// didn't exist
			} else {
				// found it
				iter.delete();
				iter.success();
				assert !isVector( initialObject, childObject );
				return true;
			}
		} finally {
			try {
				iter.finished();
			} finally {
				iter = null;
			}
			// assert !isVector( initialObject, childObject ); this will effin overwrite the thrown exception in try
		}
	}
}
