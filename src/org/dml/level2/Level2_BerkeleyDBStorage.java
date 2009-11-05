/**
 * File creation: Jun 17, 2009 6:54:03 PM
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


package org.dml.level2;



import org.dml.database.bdb.Level2_BerkeleyDB;
import org.dml.level1.Level1_BerkeleyDBStorage;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level2_BerkeleyDBStorage extends Level1_BerkeleyDBStorage
		implements Level2_DMLStorageWrapper {
	
	private Level2_BerkeleyDB	bdbL2	= null;
	
	// private String envHomeDir;
	// private boolean internalDestroyBeforeInit;
	
	/**
	 * constructor, don't forget to call init(...)
	 */
	public Level2_BerkeleyDBStorage() {

		super();
	}
	
	@Override
	public final NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisNodeID );
		try {
			return bdbL2.getDBMapJIDsToNodeIDs().getNodeJID(
					identifiedByThisNodeID );
		} catch ( DatabaseException ex ) {
			throw new StorageException( ex );
		}
	}
	
	@Override
	public final NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisJID );
		try {
			return bdbL2.getDBMapJIDsToNodeIDs().getNodeID( identifiedByThisJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	@Override
	public final NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		RunTime.assertNotNull( fromJID );
		try {
			return bdbL2.getDBMapJIDsToNodeIDs().createNodeID( fromJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	
	@Override
	public final NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		try {
			return bdbL2.getDBMapJIDsToNodeIDs().ensureNodeID( theJID );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#init(org.references.method.MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		super.init( this.internalInit( bdbL2, params ) );
	}
	
	// /**
	// * @param envHomeDir1
	// * @throws StorageException
	// */
	// public void init( String envHomeDir1 ) throws StorageException {
	//
	// this.init( envHomeDir1, false );
	// }
	//	
	// /**
	// * @param envHomeDir1
	// * @param internalDestroyBeforeInit1
	// * @throws StorageException
	// */
	// @Override
	// public void init( String envHomeDir1, boolean internalDestroyBeforeInit1
	// )
	// throws StorageException {
	//
	// RunTime.assertNotNull( envHomeDir1 );
	// envHomeDir = envHomeDir1;
	// internalDestroyBeforeInit = internalDestroyBeforeInit1;
	// super.init();
	// }
	//	
	// /**
	// * override this in subclasses without calling super<br>
	// * this method is called by start() which in turn is called by init()
	// */
	// @Override
	// protected void storageInit() {
	//
	// if ( null == bdb ) {
	// bdb = new Level2_BerkeleyDB();
	// }
	//		
	// try {
	// bdb.init( envHomeDir, internalDestroyBeforeInit );
	// } catch ( DatabaseException de ) {
	// throw new StorageException( de );
	// }
	// }
	//	
	// /**
	// * override this in subclasses without calling super<br>
	// * this method is called by done() which in turn is called by deInit()
	// */
	// @Override
	// protected void storageDeInit() {
	//
	// bdb.deInit();
	// }
	//	
	// /**
	// * no throwing
	// */
	// @Override
	// public final void done() {
	//
	//		
	// super.done();
	// }
	//	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_BerkeleyDBStorage#setVarLevelX(java.lang.Object)
	 */
	@Override
	protected void setVarLevelX( Object toValue ) {

		bdbL2 = (Level2_BerkeleyDB)toValue;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_BerkeleyDBStorage#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return bdbL2;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_BerkeleyDBStorage#newVarLevelX()
	 */
	@Override
	protected Object newVarLevelX() {

		bdbL2 = new Level2_BerkeleyDB();
		return bdbL2;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_BerkeleyDBStorage#checkVarLevelX(java.lang.Object)
	 */
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level2_BerkeleyDB ) ) {
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
}// end of class
