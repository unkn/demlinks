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
import org.dml.error.BugError;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.q.*;
import org.references.*;
import org.references.method.*;

import com.sleepycat.bind.*;
import com.sleepycat.db.*;



/**
 * initial=key
 * terminal=data
 * vector= (initial -> terminal)
 * - one key, multiple data (but different data, within this key ie. no two
 * datums are equal [within a key])
 * - and we want to be able to lookup by either key or data, or both and we can
 * 
 * - stored as two primary databases (because the primary can't have dup data
 * while associated with secondary, although secondary can have dup data)
 * - and we can't store these as secondary because we can only delete from
 * secondaries
 * 
 * @param <InitialType>
 * @param <TerminalType>
 */
public class OneToManyDBMap<InitialType, TerminalType> extends Initer {
	
	private final Class<InitialType>			initialClass;
	private final Class<TerminalType>			terminalClass;
	private final EntryBinding<InitialType>		initialBinding;
	private final EntryBinding<TerminalType>	terminalBinding;
	
	private static final String					backwardSuffix	= "_backward";
	private DatabaseCapsule						forwardDB		= null;
	private DatabaseCapsule						backwardDB		= null;
	private String								dbName;
	private Level1_Storage_BerkeleyDB			bdbL1;
	
	
	// FIXME: everywhere make sure that on start() each fields are inited to their default values which is likely null
	// instead of leftovers from a previous start()
	
	/**
	 * constructor
	 * 
	 * @param initialClass1
	 * @param initialBinding1
	 * @param terminalClass1
	 * @param terminalBinding1
	 */
	public OneToManyDBMap(
			// Level1_Storage_BerkeleyDB bdb1,
			// String dbName1,
			final Class<InitialType> initialClass1, final EntryBinding<InitialType> initialBinding1,
			final Class<TerminalType> terminalClass1, final EntryBinding<TerminalType> terminalBinding1 ) {
		
		RunTime.assumedNotNull(
		// bdb1,
		// dbName1,
			initialClass1,
			initialBinding1,
			terminalClass1,
			terminalBinding1 );
		// bdbL1 = bdb1;
		// dbName = dbName1;
		initialClass = initialClass1;
		terminalClass = terminalClass1;
		initialBinding = initialBinding1;// AllTupleBindings.getBinding(
		// initialClass );
		terminalBinding = terminalBinding1;// AllTupleBindings.getBinding(
		// terminalClass );
	}
	
	
	@Override
	protected void start( final MethodParams params2 ) {
		
		RunTime.assumedNotNull( params2 );
		
		bdbL1 = (Level1_Storage_BerkeleyDB)params2.getEx( PossibleParams.level1_BDBStorage );
		if ( null == bdbL1 ) {
			RunTime.badCall( "missing parameter" );
		}
		RunTime.assumedNotNull( bdbL1 );
		
		dbName = params2.getExString( PossibleParams.dbName );// used for forwardDB and backwardDB also
		RunTime.assumedNotNull( dbName );
		RunTime.assumedFalse( dbName.isEmpty() );
		
		final MethodParams iParams = params2.getClone();
		// FIXME: investigate if only one new OneToManyDBConfig() is needed for all "OneToManyDBMap"s
		RunTime.assumedNull( iParams.set( PossibleParams.priDbConfig, new OneToManyDBConfig() ) );
		forwardDB = Factory.getNewInstanceAndInit( DatabaseCapsule.class, iParams );
		
		RunTime.assumedNotNull( forwardDB );
		RunTime.assumedTrue( forwardDB.isInitedSuccessfully() );
		
		RunTime.assumedNotNull( iParams.set( PossibleParams.dbName, dbName + backwardSuffix ) );
		// FIXME: investigate if only one new OneToManyDBConfig() is needed for all "OneToManyDBMap"s same for
		// above
		RunTime.assumedNotNull( iParams.set( PossibleParams.priDbConfig, new OneToManyDBConfig() ) );
		// backwardDB.init( params );
		backwardDB = Factory.getNewInstanceAndInit( DatabaseCapsule.class, iParams );
		RunTime.assumedNotNull( backwardDB );
		RunTime.assumedTrue( backwardDB.isInitedSuccessfully() );
	}
	
	
	@Override
	protected void done( final MethodParams params ) {
		
		Log.entry( "done OneToManyDBMap: " + dbName );
		
		OneToXDBMapCommonCode.theDone( isInitedSuccessfully(), new Reference<Initer>( forwardDB ), new Reference<Initer>(
			backwardDB ) );
	}
	
	
	protected Level1_Storage_BerkeleyDB getBDBL1() {
		RunTime.assumedNotNull( bdbL1 );
		return bdbL1;
	}
	
	
	/**
	 * @return
	 */
	public String getDBName() {
		RunTime.assumedNotNull( dbName );
		return dbName;
	}
	
	
	/**
	 * @return
	 */
	private Database getForwardDB() {
		RunTime.assumedNotNull( forwardDB );
		RunTime.assumedTrue( forwardDB.isInitedSuccessfully() );
		final Database d = forwardDB.getDB();
		RunTime.assumedNotNull( d );
		return d;
	}
	
	
	/**
	 * @return
	 */
	private Database getBackwardDB() {
		RunTime.assumedNotNull( backwardDB );
		RunTime.assumedTrue( backwardDB.isInitedSuccessfully() );
		final Database d = backwardDB.getDB();
		RunTime.assumedNotNull( d );
		return d;
	}
	
	
	private void checkData( final TerminalType data ) {
		
		RunTime.assumedNotNull( data );
		// 1of3
		if ( data.getClass() != terminalClass ) {
			RunTime.badCall( "shouldn't allow subclass of dataClass!! or else havoc" );
		}
	}
	
	
	private void checkKey( final InitialType key ) {
		
		RunTime.assumedNotNull( key );
		// 1of3
		if ( key.getClass() != initialClass ) {
			RunTime.badCall( "shouldn't allow subclass of keyClass!! or else havoc" );
		}
	}
	
	
	/**
	 * @param initialObject
	 * @param terminalObject
	 * @return true if vector existed
	 * @throws DatabaseException
	 */
	public boolean isVector( final InitialType initialObject, final TerminalType terminalObject ) throws DatabaseException {
		this.checkKey( initialObject );
		this.checkData( terminalObject );
		
		// maybe a transaction here is unnecessary, however we don't want
		// another transaction (supposedly) to interlace between the two gets
		TransactionCapsule txc = TransactionCapsule.getNewTransaction( this.getBDBL1() );
		
		final DatabaseEntry keyEntry = new DatabaseEntry();
		initialBinding.objectToEntry( initialObject, keyEntry );
		
		final DatabaseEntry dataEntry = new DatabaseEntry();
		terminalBinding.objectToEntry( terminalObject, dataEntry );
		
		OperationStatus ret1 = null, ret2 = null;
		try {
			ret1 = this.getForwardDB().getSearchBoth( txc.get(), keyEntry, dataEntry, null );
			ret2 = this.getBackwardDB().getSearchBoth( txc.get(), dataEntry, keyEntry, null );
			// if ( ( ( OperationStatus.SUCCESS == ret1 ) && ( OperationStatus.SUCCESS != ret2 ) )
			// || ( ( OperationStatus.SUCCESS != ret1 ) && ( OperationStatus.SUCCESS == ret2 ) ) )
			if ( ( OperationStatus.SUCCESS == ret1 ) ^ ( OperationStatus.SUCCESS == ret2 ) ) {
				RunTime.bug( "one exists, the other doesn't; but should either both exist, or both not exist" );
			}
		} catch ( final Throwable t ) {
			RunTime.throPostponed( t );
			try {
				txc.abort();// this may throw
			} finally {
				txc = null;
			}
			RunTime.throwAllThatWerePostponed();
		}
		
		try {
			txc.commit();
		} finally {
			txc = null;
		}
		return ( OperationStatus.SUCCESS == ret1 );// ret2 is same
	}
	
	
	/**
	 * make sure that group (first,second) exist<br>
	 * notice that order matters, thus (second, first) is another grouping<br>
	 * this is like a new that doesn't throw if the group already exists<br>
	 * 
	 * @param initialObject
	 * @param terminalObject
	 * @return true if existed already; false if it didn't exist before call
	 * @throws DatabaseException
	 */
	public boolean ensureVector( final InitialType initialObject, final TerminalType terminalObject ) {
		
		RunTime.assumedNotNull( initialObject, terminalObject );
		return ( OperationStatus.KEYEXIST == this.internal_makeVector( initialObject, terminalObject ) );
	}
	
	
	/**
	 * @param initialObject
	 * @param terminalObject
	 * @return OperationStatus.SUCCESS or KEYEXIST
	 * @throws DatabaseException
	 * @throws BugError
	 *             if inconsistency detected (ie. one link exists the other
	 *             doesn't)
	 */
	private OperationStatus internal_makeVector( final InitialType initialObject, final TerminalType terminalObject ) {
		this.checkKey( initialObject );
		this.checkData( terminalObject );
		
		TransactionCapsule txc;
		try {
			txc = TransactionCapsule.getNewTransaction( this.getBDBL1() );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		
		final DatabaseEntry keyEntry = new DatabaseEntry();
		initialBinding.objectToEntry( initialObject, keyEntry );
		
		final DatabaseEntry dataEntry = new DatabaseEntry();
		terminalBinding.objectToEntry( terminalObject, dataEntry );
		
		boolean commit = false;
		OperationStatus ret1 = null, ret2 = null;
		try {
			ret1 = this.getForwardDB().putNoDupData( txc.get(), keyEntry, dataEntry );
			ret2 = this.getBackwardDB().putNoDupData( txc.get(), dataEntry, keyEntry );
			if ( ( OperationStatus.SUCCESS == ret1 ) && ( OperationStatus.SUCCESS == ret2 ) ) {
				commit = true;
			} else {
				// if ( ( ( OperationStatus.KEYEXIST == ret1 ) && ( OperationStatus.KEYEXIST != ret2 ) )
				// || ( ( OperationStatus.KEYEXIST != ret1 ) && ( OperationStatus.KEYEXIST == ret2 ) ) )
				if ( ( OperationStatus.KEYEXIST == ret1 ) ^ ( OperationStatus.KEYEXIST == ret2 ) ) {
					RunTime.bug( "one link exists and the other does not; should either both exist or neither" );
				}
			}
			
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		} finally {
			try {
				if ( commit ) {
					txc.commit();
				} else {
					txc.abort();
				}
			} finally {
				txc = null;
			}
		}
		
		return ret1;// which is same as ret2
	}
	
	
	/**
	 * @param a
	 * @return
	 * @throws DatabaseException
	 */
	public BDBVectorIterator<InitialType, TerminalType> getIterator_on_Terminals_of( final InitialType initialObject ) {
		
		// DatabaseEntry keyEntry = new DatabaseEntry();
		// initialBinding.objectToEntry( initialObject, keyEntry );
		BDBVectorIterator<InitialType, TerminalType> ret;
		ret =
			new BDBVectorIterator<InitialType, TerminalType>(
				this.getBDBL1(),
				this.getForwardDB(),
				initialObject,
				initialBinding,
				terminalBinding );
		// ret.init( null );
		// Factory.getNewInstanceAndInitWithoutParams( BDBVectorIterator.class, this.getBDBL1(), this.getForwardDB(),
		// initialObject,
		// initialBinding, terminalBinding );
		// Factory.initWithoutParams( ret );
		return ret;
		
	}
	
	
	public BDBVectorIterator<TerminalType, InitialType> getIterator_on_Initials_of( final TerminalType terminalObject )
	// FIXME: see where DatabaseException doesn't need to be declared as thrown
	{
		
		BDBVectorIterator<TerminalType, InitialType> ret;
		ret =
			new BDBVectorIterator<TerminalType, InitialType>(
				this.getBDBL1(),
				this.getBackwardDB(),
				terminalObject,
				terminalBinding,
				initialBinding );
		// ret.init( null );
		// Factory.getNewInstanceAndInitWithoutParams( BDBVectorIterator.class, this.getBDBL1(), this.getBackwardDB(),
		// terminalObject, terminalBinding, initialBinding );
		// Factory.initWithoutParams( ret );
		return ret;
	}
	
	
	public long countInitials( final TerminalType ofTerminalObject ) {
		
		final BDBVectorIterator<TerminalType, InitialType> vi = this.getIterator_on_Initials_of( ofTerminalObject );
		long count = -1;
		try {
			count = vi.count();
		} finally {
			vi.close();
		}
		return count;
	}
	
	
	public long countTerminals( final InitialType ofInitialObject ) {
		
		final BDBVectorIterator<InitialType, TerminalType> vi = this.getIterator_on_Terminals_of( ofInitialObject );
		long count = -1;
		try {
			count = vi.count();
		} finally {
			vi.close();
		}
		return count;
	}
	
	
	/**
	 * must not be more than 1 found, else bug
	 * 
	 * @param initial1
	 * @param initial2
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public TerminalType findCommonTerminalForInitials( final InitialType initial1, final InitialType initial2 )
			throws DatabaseException {
		
		this.checkKey( initial1 );
		this.checkKey( initial2 );
		TerminalType found = null;
		BDBVectorIterator<InitialType, TerminalType> iter1 = this.getIterator_on_Terminals_of( initial1 );
		BDBVectorIterator<InitialType, TerminalType> iterator = iter1;
		InitialType comparator = initial2;
		BDBVectorIterator<InitialType, TerminalType> iter2 = null;
		try {
			iter2 = this.getIterator_on_Terminals_of( initial2 );
			try {
				if ( iter1.count() > iter2.count() ) {
					iterator = iter2;
					comparator = initial1;
				}
			} finally {
				// deInit the unused one
				if ( iterator == iter2 ) {
					try {
						iter1.close();
					} finally {
						iter1 = null;
					}
				} else {
					try {
						iter2.close();
						
					} finally {
						iter2 = null;
					}
				}
			}
			
			// parse iter1's elements and see if any is in iter2
			
			iterator.goFirst();
			TerminalType now = iterator.now();
			while ( null != now ) {
				if ( this.isVector( comparator, now ) ) {
					// found one
					if ( null != found ) {
						RunTime.bug( "supposed to be only one, but we found two" );
					} else {
						found = now;
					}
				}
				iterator.goNext();
				now = iterator.now();
			}
		} finally {
			try {
				iterator.close();
			} finally {
				iterator = null;
			}
		}
		// RunTime.assumedFalse( iter1.isInitingOrInited() );
		// RunTime.assumedFalse( iter2.isInitingOrInited() );
		return found;
	}
	
	
	/**
	 * @param initialObject
	 * @param terminalObject
	 * @return true if existed
	 * @throws DatabaseException
	 */
	public boolean removeVector( final InitialType initialObject, final TerminalType terminalObject ) throws DatabaseException {
		
		RunTime.assumedNotNull( initialObject, terminalObject );
		
		BDBVectorIterator<InitialType, TerminalType> iter = this.getIterator_on_Terminals_of( initialObject );
		boolean found1 = false;
		try {
			iter.goFirst();
			while ( null != iter.now() ) {
				if ( iter.now() == terminalObject ) {
					// found it
					found1 = true;
					break;
				}
				iter.goNext();
			}
			
			
			if ( found1 ) {
				BDBVectorIterator<TerminalType, InitialType> reverseIter = this.getIterator_on_Initials_of( terminalObject );
				boolean found2 = false;
				try {
					reverseIter.goFirst();
					while ( null != reverseIter.now() ) {
						if ( reverseIter.now() == initialObject ) {
							// found it
							found2 = true;
							break;
						}
						reverseIter.goNext();
					}
					RunTime.assumedTrue( found2 );// must have!
					// TODO encompass in a transaction
					RunTime.assumedNotNull( reverseIter.now() );
					final long size1 = iter.count();
					final long size2 = reverseIter.count();
					RunTime.assumedNotNull( reverseIter.now() );
					RunTime.assumedTrue( iter.delete() );
					RunTime.assumedNotNull( reverseIter.now() );
					RunTime.assumedTrue( reverseIter.delete() );
					RunTime.assumedTrue( ( size1 - 1 ) == iter.count() );
					RunTime.assumedTrue( ( size2 - 1 ) == reverseIter.count() );
				} finally {
					try {
						reverseIter.close();
					} finally {
						reverseIter = null;
					}
				}
			}
			
		} finally {
			try {
				iter.close();
			} finally {
				iter = null;
			}
		}
		RunTime.assumedFalse( this.isVector( initialObject, terminalObject ) );
		return found1;
	}
	
	
}
