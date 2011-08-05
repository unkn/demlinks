/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 5, 2011 12:04:27 AM
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
package org.bdbLevel1;

import java.io.*;

import org.q.*;
import org.references.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 * NNU=non-null uniques
 * KEY <-> DATA<br>
 * persistent 1-to-1 mapping between KEY and DATA<br>
 * inside berkeleyDB<br>
 * searchable by either of the two, ie. as if both were keys or rather as if they were both in 2 hashmaps<br>
 * 
 * XXX: this CANNOT allow subclasses of KEY/DATA; only specific classes are allowed! because we can't know what type of object
 * we stored in the database so to make sure we use THAT `specific for that class type` binding when converting it back to java
 * object<br>
 * 
 * @param <KEY>
 *            ie. String
 * @param <DATA>
 *            ie. Node
 */
public class GenericBDBTwoWayMapOfNNU<KEY, DATA> extends BaseFor_ThreadSafeTwoWayMapOfUniques<KEY, DATA> {
	
	private static final String		secPrefix	= "secondary";
	private final Database			priDb;
	// this class is the only one that makes use of a secondary database
	private final SecondaryDatabase	secDb;
	private final Environment		_env;
	private TupleBinding<KEY>		_keyBinding;
	private TupleBinding<DATA>		_dataBinding;
	private final Class<KEY>		_keyClass;
	private final Class<DATA>		_dataClass;
	
	
	public GenericBDBTwoWayMapOfNNU( final BDBEnvironment env, final String dbName1, final Class<KEY> keyClass,
			final Class<DATA> dataClass ) {
		this( env.getBDBEnv(), dbName1, keyClass, dataClass );
	}
	
	
	/**
	 * 1-to-1 database<br>
	 * constructor<br>
	 * 
	 * @param env
	 * @param dbName1
	 * @param keyClass
	 *            specific class, subclasses are not allowed!
	 * @param dataClass
	 *            specific class, subclasses are not allowed!
	 */
	public GenericBDBTwoWayMapOfNNU( final Environment env, final String dbName1, final Class<KEY> keyClass,
			final Class<DATA> dataClass
	// final TupleBinding<KEY> keyBinding,
	// final TupleBinding<DATA> dataBinding
	) {
		_env = env;
		assert Q.nn( _env );
		
		assert Q.nn( dbName1 );
		assert !dbName1.isEmpty();
		
		_keyClass = keyClass;
		_dataClass = dataClass;
		assert Q.nn( _keyClass );
		assert Q.nn( _dataClass );
		_keyBinding = AllTupleBindings.getBinding( _keyClass );
		_dataBinding = AllTupleBindings.getBinding( _dataClass );
		assert Q.nn( _keyBinding );
		assert Q.nn( _dataBinding );
		
		
		final SecondaryConfig secConf = new SecondaryConfig();
		secConf.setAllowCreate( true );
		secConf.setType( DatabaseType.HASH );// XXX: check if BTREE is better? ie. via some benchmark sometime in the future
		// secConf.setAllowPopulate( true );
		// secConf.setDeferredWrite( false );
		// secConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secConf.setExclusiveCreate( false );
		secConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secConf.setReadOnly( false );
		secConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secConf.setTransactional( BDBEnvironment.ENABLE_TRANSACTIONS );
		
		assert !secConf.getSortedDuplicates();
		final SecondaryKeyCreator keyCreator = new SecondaryKeyCreator() {
			
			@Override
			public boolean createSecondaryKey( final SecondaryDatabase secondary, final DatabaseEntry key,
												final DatabaseEntry data, final DatabaseEntry result ) {
				
				// if this differs, then we need perhaps to set it to result
				// also
				// assert data.getData().length == data.getSize() : "len=" + data.getData().length + " size=" + data.getSize()
				// + " data=" + data;
				// XXX: looks like length and size can differ ie. 8 vs 100, maybe latter is with padding
				result.setData( data.getData() );
				result.setSize( data.getSize() );// this seems useless but let above assert check that for us
				
				// System.out.println( key + "!" + data + "!" + result );
				return true;
			}
		};
		secConf.setKeyCreator( keyCreator );
		
		final DatabaseConfig dbConf = new DatabaseConfig();
		dbConf.setAllowCreate( true );
		dbConf.setType( DatabaseType.HASH );// XXX: check if BTREE is better?
		// dbConf.setDeferredWrite( false );
		// dbConf.setKeyPrefixing( false );
		dbConf.setSortedDuplicates( false );// must be false!
		dbConf.setTransactional( BDBEnvironment.ENABLE_TRANSACTIONS );
		
		assert !dbConf.getSortedDuplicates();
		
		// pri db
		try {
			priDb = env.openDatabase(
			// BETransaction.getCurrentTransaction( _env ),
				null,
				dbName1,
				null,
				dbConf );
			secDb = env.openSecondaryDatabase( null,
			// BETransaction.getCurrentTransaction( _env ),
				secPrefix + dbName1,
				null,
				priDb,
				secConf );
		} catch ( final FileNotFoundException e ) {
			throw Q.rethrow( e );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForThreadSafeTwoWayMapOfUniques#internalForOverride_discard()
	 */
	@Override
	protected void internalForOverride_discard() {
		try {
			Q.info( "closing " + this.getClass().getSimpleName() + ": " + priDb.getDatabaseName() );
			if ( null != secDb ) {
				// XXX: no need to make this field null anymore, since discard() makes sure can't call any methods anymore
				secDb.close();
			}
			if ( null != priDb ) {
				priDb.close();
			}
			// FIXME: above, any close() could throw, ie. the first one; but the second one must be reached regardless
			// so FIXME: maybe implement postponed throws again?
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		}
	}
	
	
	private void checkDataIsValid( final DATA data ) {
		assert Q.nn( data );
		if ( data.getClass() != _dataClass ) {
			// XXX: do not try to implement allowing subclasses and mixture of those; it won't work, read this class' javadoc
			Q.badCall( "shouldn't allow subclass of dataClass current=`" + data.getClass() + "` expected=`" + _dataClass
				+ "`!! because\n" + " the binding only knows how to transform the base class, "
				+ "so it might miss any of the subclass' extra data/fields" );
		}
	}
	
	
	private void checkKeyIsValid( final KEY key ) {
		
		assert Q.nn( key );
		// shouldn't allow subclass of keyClass!! or else havoc, well data loss
		// since TupleBinding treats it as Base class, so assuming the subclass
		// has new fields they won't be stored/retreived from DB
		// more importantly, retrieving won't know which object it was stored if allowing any subclasses
		if ( key.getClass() != _keyClass ) {
			// XXX: do not try to implement allowing subclasses and mixture of those; it won't work, read this class' javadoc
			Q.badCall( "shouldn't allow subclass of keyClass current=`" + key.getClass() + "` expected=`" + _keyClass
				+ "`!! \n" + "because the binding only knows how to transform the base class, "
				+ "so it might miss any of the subclass' extra data/fields" );
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_getKey(java.lang.Object)
	 */
	@Override
	protected KEY internalForOverride_getKey( final DATA data ) {
		checkDataIsValid( data );
		// assert null != data;
		
		final DatabaseEntry deData = new DatabaseEntry();
		_dataBinding.objectToEntry( data, deData );
		// LongBinding.longToEntry( node.getId(), deData );
		
		final DatabaseEntry deKey = new DatabaseEntry();
		final DatabaseEntry pKey = new DatabaseEntry();
		// deData=new DatabaseEntry(data.getBytes(BerkeleyDB.ENCODING));
		OperationStatus ret;
		try {
			ret = secDb.get( BDBTransaction.getCurrentTransaction( _env ), deData, pKey, deKey, BDBEnvironment.LOCK );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		assert deData.equals( deKey );
		
		final KEY key = _keyBinding.entryToObject( pKey );
		checkKeyIsValid( key );
		// StringBinding.entryToString( pKey );
		// assert Q.nn( key );// should not be null here
		return key;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_getData(java.lang.Object)
	 */
	@Override
	protected DATA internalForOverride_getData( final KEY key ) {
		checkKeyIsValid( key );
		
		final DatabaseEntry deKey = new DatabaseEntry();
		_keyBinding.objectToEntry( key, deKey );
		// StringBinding.stringToEntry( name, deKey );
		
		
		final DatabaseEntry deData = new DatabaseEntry();
		OperationStatus ret;
		try {
			ret = priDb.get( BDBTransaction.getCurrentTransaction( _env ), deKey, deData, BDBEnvironment.LOCK );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		if ( OperationStatus.SUCCESS != ret ) {
			return null;
		}
		
		final DATA data = _dataBinding.entryToObject( deData );
		checkDataIsValid( data );
		return data;
		// return new BDBNode( LongBinding.entryToLong( deData ) );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_ensureExists(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean internalForOverride_ensureExists( final KEY key, final DATA data ) {
		checkKeyIsValid( key );
		checkDataIsValid( data );
		// assert null != key;
		// assert null != data;
		
		final DatabaseEntry deKey = new DatabaseEntry();
		_keyBinding.objectToEntry( key, deKey );
		// StringBinding.stringToEntry( key, deKey );
		final DatabaseEntry deData = new DatabaseEntry();
		_dataBinding.objectToEntry( data, deData );
		// LongBinding.longToEntry( data.getId(), deData );
		OperationStatus ret = null;
		try {
			ret = priDb.putNoOverwrite( BDBTransaction.getCurrentTransaction( _env ), deKey, deData );
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		}
		return ret.equals( OperationStatus.KEYEXIST );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_removeByKey(java.lang.Object)
	 */
	@Override
	protected boolean internalForOverride_removeByKey( final KEY key ) {
		throw Q.ni();// TODO: implement?
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_removeAll()
	 */
	@Override
	protected void internalForOverride_removeAll() {
		throw Q.ni();// TODO: implement?
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.BaseForTwoWayMapOfUniques#internalForOverride_size()
	 */
	@Override
	protected int internalForOverride_size() {
		throw Q.ni();// TODO: implement?
	}
	
}
