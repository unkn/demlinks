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

import java.io.*;
import java.util.concurrent.locks.*;

import org.dml.storage.berkeleydb.commons.*;
import org.dml.storage.commons.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.je.*;



/**
 */
public class StorageBDBJE
		extends Base_StorageGeneric
{
	
	private static final int							BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	
	private final Environment							env;
	
	// two-way mapping between these two:
	private final BDBJETwoWayHashMap_StringName2Node	db_Name2Node;
	// Bad Address errors can be seen from using "<->" in names because they are stored as filenames
	private final static String							NAME_of_db_for_Name2Node		= "map(nameString2nodeLong)";
	
	// this is used to generate new unique LongIdents, based on unique long numbers
	private final BDBJE_Named_UniqueNumberGenerator		sequence;
	private static final String							NAMEofSEQ_longIdents			= "sequenceforgeneratinguniquelongs";
	// delta difference between `generated longs which are unique`
	private static final int							longIdents_Delta				= +1;
	
	
	// a database where all sequences will be stored:(only 1 db per bdb env)
	private final Database								dbOfSequences;
	private final static String							NAME_dbOfSequences				= "dbthatstoresallsequences";
	
	private static final long							MIN_ForLongs					= 0l;
	private static final long							START_VALUE_ForLongs			= StorageBDBJE.MIN_ForLongs;
	
	// imposing a silly limit, for now:
	private static final long							MAX_ForLongs					= 4123123123l;
	
	
	private final BDBJE_SetOfNodes						dbSet;
	private final static String							DBNAME_OneNode_to_ManyNodes		= "mapOne2Many(nodeLong2nodeLong)";
	
	
	// DUR and LOCK and CURSORCONFIG are tightly connected, well not really anymore...
	public static final Durability						DUR								= Durability.COMMIT_NO_SYNC;
	// COMMIT_NO_SYNC;
	/*
	 * NO_SYNC
	 * Do not write or synchronously flush the log on transaction commit.
	 * 
	 * SYNC
	 * Write and synchronously flush the log on transaction commit.
	 * 
	 * WRITE_NO_SYNC
	 * Write but do not synchronously flush the log on transaction commit.
	 */
	public final static boolean							ENABLE_TRANSACTIONS				= true;
	
	public static final LockMode						LOCK							= LockMode.DEFAULT;
	// ENABLE_TRANSACTIONS ? LockMode.RMW
	// : LockMode.DEFAULT;
	// XXX: should never use READ_COMMITED or READ_UNCOMMITTED
	public static final LockMode						CURSORLOCK						= LockMode.RMW;
	// DEFAULT;
	// RMW;
	
	public static final CursorConfig					CURSORCONFIG					= CursorConfig.DEFAULT;
	// FIXME: with DEFAULT: BDB0697 Write attempted on read-only cursor; and CDB enabled
	// WRITECURSOR;
	// DEFAULT;
	// new CursorConfig().setReadUncommitted( true ); we don't want RU or RC here
	
	// stuff to prevent(least we can do) new-ing more than one BerkeleyEnv:
	private static boolean								once							= false;
	private final static ReentrantLock					rl								= new ReentrantLock();
	
	
	private volatile boolean							shuttingDown					= false;
	private volatile Thread								shutdownThread					= null;
	private final Timer									timer							= new Timer( Timer.TYPE.MILLIS );
	
	
	/**
	 * constructor
	 * 
	 * @param envHomeDir1
	 * @param deleteFirst
	 *            used in JUnit only<br>
	 */
	@SuppressWarnings( "unused" )
	public StorageBDBJE( final String envHomeDir1, final boolean deleteFirst ) {
		L.tryLock( StorageBDBJE.rl );
		try {
			if ( StorageBDBJE.once ) {
				Q.badCall( "only allowed one time! and no singletons" );
			} else {
				StorageBDBJE.once = true;
			}
		} finally {
			StorageBDBJE.rl.unlock();
		}
		assert null != envHomeDir1;
		assert !envHomeDir1.isEmpty();
		
		if ( deleteFirst ) {
			internalWipeEnv( new File( envHomeDir1 ) );// deleting this env from disk
		}
		
		final EnvironmentConfig envConf = new EnvironmentConfig();
		
		envConf.setAllowCreate( true );
		
		envConf.setLocking( true && ENABLE_TRANSACTIONS );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		// envConf.setDurability( DUR );
		envConf.setTxnNoSync( false );// XXX: should be false for consistency
		envConf.setTxnWriteNoSync( true );// can't use both
		envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		
		envConf.setTxnSerializableIsolation( ENABLE_TRANSACTIONS );// heh
		envConf.setSharedCache( false );// XXX: set this to true when allowing multiple opens on same environ
		
		// XXX: these 3 lines enable debugging output:
		// final Logger parent = Logger.getLogger( "com.sleepycat.je" );
		// parent.setLevel( Level.ALL );
		// envConf.setConfigParam( EnvironmentConfig.CONSOLE_LOGGING_LEVEL, "CONFIG" );
		
		final File file = new File( envHomeDir1 );
		if ( file.mkdirs() ) {
			Q.info( file + " path was just created, it didn't exist previously!" );
		}
		env = new Environment( file, envConf );
		Runtime.getRuntime().addShutdownHook( shutdownThread = new Thread()
		{
			
			@SuppressWarnings( "synthetic-access" )
			@Override
			public void run() {
				shuttingDown = true;
				System.err.println( this.getClass() + " is about to shut down the bdb database" );
				timer.start();
				shutdown();
				timer.stop();
				System.err.println( this.getClass() + " shutting down complete " + timer.getDeltaPrintFriendly() );
			}
		} );
		
		assert null != env;
		final BDBJETransaction ourTxn = BDBJETransaction.beginChild( env );
		try {
			// init once first time:
			final DatabaseConfig sequenceDbConf = new DatabaseConfig();// seq != sec(ondary) I always read that
			sequenceDbConf.setAllowCreate( true );
			sequenceDbConf.setDeferredWrite( false );
			sequenceDbConf.setKeyPrefixing( false );// no more prefixing
			sequenceDbConf.setSortedDuplicates( false );// false here
			sequenceDbConf.setTransactional( ENABLE_TRANSACTIONS );
			assert null != StorageBDBJE.NAME_dbOfSequences;
			assert StorageBDBJE.NAME_dbOfSequences.length() > 0;
			dbOfSequences = env.openDatabase( ourTxn.getTransaction(), StorageBDBJE.NAME_dbOfSequences, sequenceDbConf );
			assert null != dbOfSequences;
			
			db_Name2Node = new BDBJETwoWayHashMap_StringName2Node( env, StorageBDBJE.NAME_of_db_for_Name2Node );
			
			sequence =
				new BDBJE_Named_UniqueNumberGenerator(
					this,
					StorageBDBJE.NAMEofSEQ_longIdents,
					StorageBDBJE.MIN_ForLongs,
					StorageBDBJE.START_VALUE_ForLongs,
					StorageBDBJE.MAX_ForLongs,
					false );
			
			dbSet = new BDBJE_SetOfNodes( env, StorageBDBJE.DBNAME_OneNode_to_ManyNodes );
			
			ourTxn.success();
		} finally {
			ourTxn.finished();
		}
	}
	
	
	protected Environment getBDBEnv() {
		return env;
	}
	
	
	protected Database getDBOfSequences() {
		assert null != dbOfSequences;
		return dbOfSequences;
	}
	
	
	
	@Override
	public boolean equals( final Object obj ) {
		throw Q.ni();
	}
	
	
	@Override
	public int hashCode() {
		throw Q.ni();
	}
	
	
	/**
	 * failing to close env aka deInit(), will not write last writes to the database<br>
	 */
	@Override
	public void shutdown( final boolean delete ) {
		L.tryLock( StorageBDBJE.rl );
		try {
			assert StorageBDBJE.once : "you're not supposed to call this more than once; ie. it won't silently ignore";
			StorageBDBJE.once = false;
			if ( !shuttingDown ) {
				assert null != shutdownThread;
				final boolean ret = Runtime.getRuntime().removeShutdownHook( shutdownThread );
				assert ret;
			}
			// ==============
			
			// ==============
			// if-s needed just in case constructor failed and program exit cause shutdown hook to execute deInit
			File homeDir = null;
			
			try {
				if ( delete ) {
					homeDir = env.getHome();
				}
				
				if ( null != dbSet ) {
					dbSet.close();
				}
				
				// 2nd
				if ( null != db_Name2Node ) {
					db_Name2Node.discard();
				}
				
				// close symbol generator and it's sequence
				if ( null != sequence ) {
					sequence.close();
				}
				
				// this.closeAnyPriDB( this.db_Sequences );
				if ( null != dbOfSequences ) {
					dbOfSequences.close();
				}
				
				
				
			} catch ( final DatabaseException e ) {
				throw Q.rethrow( e );
			} finally {
				// last
				assert null != env;
				try {
					env.close();
				} catch ( final DatabaseException e ) {
					throw Q.rethrow( e );
				}
				// don't need to set it to null after close, BDB can handle such cases when attempt to use already closed env
				Q.info( "BerkeleyDB env closed" );
				if ( delete ) {
					internalWipeEnv( homeDir );
				}
			}
			
		} finally {
			StorageBDBJE.rl.unlock();
		}
	}
	
	
	/**
	 * intended to be used for JUnit testing when a clean start is required ie.
	 * no leftovers from previous JUnits or runs in the database<br>
	 * this should wipe entire home folder for that environment (which is
	 * supposedly everything incl. DBs)<br>
	 * 
	 * @param envHomeDir
	 */
	private final void internalWipeEnv( final File envHomeDir ) {
		assert null != envHomeDir;
		F.delFileOrTree( envHomeDir );
	}
	
	
	// ============================= L1 below: handles mapping Node to its ID(aka long)
	
	/**
	 * the Long must already exist else null is returned<br>
	 * this doesn't create a new Long for the supplied ID<br>
	 * remember there's a one to one mapping between a ID and a Long
	 * 
	 * 
	 * @param byName
	 * @return null if not found;
	 */
	@Override
	public NodeBDB getNode( final String byName ) {
		assert null != byName;
		return db_Name2Node.getNode( byName );
	}
	
	
	/**
	 * @param node
	 * @return can be null
	 */
	@Override
	public String getName( final NodeGeneric node ) {
		assert null != node;
		assert node.getClass() == NodeBDB.class;
		final NodeBDB bNode = (NodeBDB)node;
		
		return db_Name2Node.getName( bNode );// can be null
	}
	
	
	public void createNameForNode( final String name, final NodeBDB node ) {
		assert null != name;
		assert null != node;
		if ( ensure_NameForNode( name, node ) ) {
			Q.badCall( "already existed map(name,node) vector: map(`" + name + "`,`" + node + "`" );
		}
	}
	
	
	/**
	 * makes sure `name<->node` vector exists<br>
	 * 
	 * @param name
	 * @param node
	 * @return true if already existed
	 */
	public boolean ensure_NameForNode( final String name, final NodeBDB node ) {
		assert null != name;
		assert null != node;
		
		Boolean ret = null;
		final NodeBDB existingNode = getNode( name );
		final String existingName = getName( node );
		
		final boolean nameAlreadyInAVector = ( null != existingNode );
		final boolean nodeAlreadyInAVector = ( null != existingName );
		if ( nameAlreadyInAVector ^ nodeAlreadyInAVector ) {
			// xor 0^0=0 1^1=0 1^0=1 0^1=1
			// so if here, only one of id/sym is already in a vector which means, it's a different vector
			// which means we cannot make a new vector so fail
			assert nameAlreadyInAVector != nodeAlreadyInAVector;
			
			Q
				.badCall( "only one of stringID or longID exists in a vector which means they are not each part of the same vector"
					+ "Expected(stringId<->longId): `"
					+ name
					+ "`<->`"
					+ node
					+ "` but found: `"
					+ ( nameAlreadyInAVector ? name + "`<->`" + existingNode : existingName + "`<->`" + node ) + "`" );
		} else {// both are in a vector OR none of them is
			if ( nameAlreadyInAVector ) {
				// means both are in a vector(unsure if same vector), let's check if it's the same vector as expected
				assert nameAlreadyInAVector == nodeAlreadyInAVector;
				
				if ( ( existingNode.equals( node ) ) && ( existingName.equals( name ) ) ) {
					// both // are // in // the // same // one // vector, // as // expected
					ret = Boolean.TRUE;
				} else {
					Q.badCall( "Both are each in a different vectors. Expected: `" + name + "`<->`" + node
						+ "` but found these two(stringId<->longId): `" + name + "`<->`" + existingNode + "` AND `"
						+ existingName + "`<->`" + node + "`" );
				}
			} else {
				// both were in no vector, we need to create it
				// vector doesn't exist, we make it for the first time
				final boolean tempRet = db_Name2Node.ensureExists( name, node );
				assert !tempRet : Q.bug( "should not have already existed" );
				ret = Boolean.FALSE;// meaning vector did not already exist, we had to create it
			}// else
		}// else
		
		assert ( null != ret ) : Q.bug( "not possible" );
		return ret.booleanValue();
	}
	
	
	
	/**
	 * that is, without associating it with a name !<br>
	 * it will never be null, it will throw before that happens<br>
	 * 
	 * @return long
	 */
	@Override
	public final NodeBDB createNewUniqueNode() {
		return NodeBDB.getBDBNodeInstance( sequence.getNextUniqueLong( StorageBDBJE.longIdents_Delta ) );
	}
	
	
	/**
	 * make sure this name has a Node (any even if just now created for it) mapped to it<br>
	 * can only be 1-to-1 mapped<br>
	 * 
	 * @param name
	 * @return the node, ie. never null
	 */
	@Override
	public NodeBDB createOrGetNode( final String name ) {
		assert null != name;
		final BDBJETransaction txn = BDBJETransaction.beginChild( getBDBEnv() );
		NodeBDB node = null;
		try {
			node = getNode( name );
			if ( null == node ) {
				// name is not yet associated, so we can create new Node for it to associate with.
				node = createNewUniqueNode();
				final boolean tempRet = ensure_NameForNode( name, node );
				assert !tempRet : Q.bug( "impossible to have already existed!" );
			}
			txn.success();
		} finally {
			txn.finished();
		}
		assert null != node;
		return node;
	}
	
	
	// ============================= L2 below: handles only Nodes
	
	/**
	 * it will throw if already exists!<br>
	 * 
	 * @param initialNode
	 * @param childNode
	 */
	@Override
	public void makeVector( final NodeGeneric initialNode, final NodeGeneric childNode ) {
		assert null != initialNode;
		assert null != childNode;
		assert initialNode.getClass() == NodeBDB.class;
		assert childNode.getClass() == NodeBDB.class;
		final NodeBDB iNode = (NodeBDB)initialNode;
		final NodeBDB tNode = (NodeBDB)childNode;
		dbSet.createNewVectorOrThrow( iNode, tNode );
	}
	
	
	@Override
	public boolean ensureVector( final NodeGeneric initialNode, final NodeGeneric childNode ) {
		assert null != initialNode;
		assert null != childNode;
		return dbSet.ensureVector( initialNode, childNode );
	}
	
	
	@Override
	public boolean isVector( final NodeGeneric initialNode, final NodeGeneric childNode ) {
		assert null != initialNode;
		assert null != childNode;
		assert initialNode.getClass() == NodeBDB.class;
		assert childNode.getClass() == NodeBDB.class;
		final NodeBDB iNode = (NodeBDB)initialNode;
		final NodeBDB tNode = (NodeBDB)childNode;
		return dbSet.isVector( iNode, tNode );
	}
	
	
	@Override
	public IteratorGeneric_OnChildNodes getIterator_on_Initials_of( final NodeGeneric childNode ) {
		assert null != childNode;
		return dbSet.getIterator_on_Initials_of( childNode );
	}
	
	
	@Override
	public IteratorGeneric_OnChildNodes getIterator_on_Children_of( final NodeGeneric initialNode ) {
		assert null != initialNode;
		return dbSet.getIterator_on_Children_of( initialNode );
	}
	
	
	@Override
	public int countInitials( final NodeGeneric ofChildNode ) {
		assert null != ofChildNode;
		return dbSet.countInitials( ofChildNode );
	}
	
	
	@Override
	public int countChildren( final NodeGeneric ofInitialNode ) {
		assert null != ofInitialNode;
		return dbSet.countChildren( ofInitialNode );
	}
	
	
	/**
	 * A->X<br>
	 * B->X<br>
	 * find X<br>
	 * 
	 * @param initialNode1
	 * @param initialNode2
	 * @return
	 */
	@Override
	public NodeGeneric findCommonChildForInitials( final NodeGeneric initialNode1, final NodeGeneric initialNode2 ) {
		assert null != initialNode1;
		assert null != initialNode2;
		return dbSet.findCommonChildForInitials( initialNode1, initialNode2 );
	}
	
	
	@Override
	public boolean removeVector( final NodeGeneric initialNode, final NodeGeneric childNode ) {
		assert null != initialNode;
		assert null != childNode;
		return dbSet.removeVector( initialNode, childNode );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.generic.env.EnvAdapter#beginTxn()
	 */
	@Override
	public TransactionGeneric beginTransaction() {
		return BDBJETransaction.beginChild( getBDBEnv() );
	}
	
	
}
