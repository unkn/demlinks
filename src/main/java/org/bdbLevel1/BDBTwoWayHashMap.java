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
package org.bdbLevel1;

import java.io.*;

import org.q.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 * KEY <-> DATA<br>
 * persistent 1-to-1 mapping between KEY and DATA<br>
 * inside berkeleyDB<br>
 * searchable by either of the two, ie. as if both were keys or rather as if they were both in 2 hashmaps<br>
 * 
 * @param <KEY>
 *            ie. String
 * @param <DATA>
 *            ie. Node
 */
public class BDBTwoWayHashMap {
	
	private static final String	secPrefix	= "secondary";
	private Database			priDb		= null;
	// this class is the only one that makes use of a secondary database
	private SecondaryDatabase	secDb		= null;
	private final Environment	_env;
	
	
	public BDBTwoWayHashMap( final BDBEnvironment env, final String dbName1 ) {
		this( env.getBDBEnv(), dbName1 );
	}
	
	
	/**
	 * 1-to-1 database<br>
	 * constructor
	 * 
	 * @param env
	 * @param dbName1
	 * 
	 */
	public BDBTwoWayHashMap( final Environment env, final String dbName1 ) {
		assert null != env;
		assert null != dbName1;
		assert !dbName1.isEmpty();
		_env = env;
		
		final SecondaryConfig secConf = new SecondaryConfig();
		secConf.setAllowCreate( true );
		secConf.setType( DatabaseType.HASH );// XXX: check if BTREE is better? ie. via some benchmark sometime in the future
		// secConf.setAllowPopulate( true );
		// secConf.setDeferredWrite( false );
		// secConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secConf.setExclusiveCreate( false );
		secConf.setImmutableSecondaryKey( false );
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
				assert data.getData().length == data.getSize();
				result.setData( data.getData() );
				result.setSize( data.getSize() );// this seems useless but let above assert check that for us
				
				// System.out.println( key + "!" + data + "!" + result );
				return true;
			}
		};
		secConf.setKeyCreator( keyCreator );
		
		final DatabaseConfig dbConf = new DatabaseConfig();
		dbConf.setAllowCreate( true );
		dbConf.setType( DatabaseType.BTREE );
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
				BDBTwoWayHashMap.secPrefix + dbName1,
				null,
				priDb,
				secConf );
		} catch ( final FileNotFoundException e ) {
			throw Q.rethrow( e );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
		
	}
	
	
	public void discard() {
		try {
			Q.info( "closing " + this.getClass().getSimpleName() + ": " + priDb.getDatabaseName() );
			if ( null != secDb ) {
				secDb.close();
			}
			if ( null != priDb ) {
				priDb.close();
			}
			// FIXME: above
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		} finally {
			secDb = null;
			priDb = null;
		}
	}
	
	
	/**
	 * @param name
	 * @param node
	 * @return true if already existed, false if it didn't;<br>
	 *         either way it does after this call
	 */
	public boolean createOrGet( final String name, final BDBNode node ) {
		assert null != name;
		assert null != node;
		
		final DatabaseEntry deKey = new DatabaseEntry();
		StringBinding.stringToEntry( name, deKey );
		final DatabaseEntry deData = new DatabaseEntry();
		LongBinding.longToEntry( node.getId(), deData );
		OperationStatus ret = null;
		try {
			ret = priDb.putNoOverwrite( BDBTransaction.getCurrentTransaction( _env ), deKey, deData );
		} catch ( final DatabaseException e ) {
			Q.rethrow( e );
		}
		return ret.equals( OperationStatus.KEYEXIST );
	}
	
	
	/**
	 * @param node
	 * @return null if not found
	 */
	public String getName( final BDBNode node ) {
		assert null != node;
		
		final DatabaseEntry deData = new DatabaseEntry();
		LongBinding.longToEntry( node.getId(), deData );
		
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
		
		final String key = StringBinding.entryToString( pKey );
		assert null != key;// should not be null here
		return key;
	}
	
	
	/**
	 * @param name
	 * @return null if not found
	 */
	public BDBNode getNode( final String name ) {
		assert null != name;
		
		final DatabaseEntry deKey = new DatabaseEntry();
		StringBinding.stringToEntry( name, deKey );
		
		
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
		
		// XXX: maybe reuse? instead of new-ing all the time - no reusing! to catch bugs is better new-ing; with reuse we may
		// even induce new bugs! for now, it's good that we catch usage of == instead of .equals()
		return new BDBNode( LongBinding.entryToLong( deData ) );
	}
	
}
