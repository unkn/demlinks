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


package org.dml.storagewrapper;



import org.dml.database.bdb.BerkeleyDB;
import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.tools.RunTime;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class BerkeleyDBStorageLevel3 extends BerkeleyDBStorageLevel2 implements
		StorageWrapperLevel3 {
	
	private BerkeleyDB	bdb	= null;
	
	@Override
	public final NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisNodeID );
		try {
			return bdb.getDBMapJIDsToNodeIDs().getNodeJID(
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
			return bdb.getDBMapJIDsToNodeIDs().getNodeID( identifiedByThisJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	@Override
	public final NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		RunTime.assertNotNull( fromJID );
		try {
			return bdb.getDBMapJIDsToNodeIDs().createNodeID( fromJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	
	@Override
	public final NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		try {
			return bdb.getDBMapJIDsToNodeIDs().ensureNodeID( theJID );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
	
	
	/**
	 * @param envHomeDir
	 * @throws StorageException
	 */
	public BerkeleyDBStorageLevel3( String envHomeDir ) throws StorageException {

		this( envHomeDir, false );
	}
	
	/**
	 * construct
	 * 
	 * @param envHomeDir
	 * @throws StorageException
	 */
	public BerkeleyDBStorageLevel3( String envHomeDir,
			boolean internalDestroyBeforeInit ) throws StorageException {

		this.init( envHomeDir, internalDestroyBeforeInit );
	}
	
	/**
	 * @param envHomeDir
	 * @param internalDestroyBeforeInit
	 * @throws StorageException
	 */
	private void init( String envHomeDir, boolean internalDestroyBeforeInit )
			throws StorageException {

		RunTime.assertNotNull( envHomeDir );
		try {
			bdb = new BerkeleyDB( envHomeDir, internalDestroyBeforeInit );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
	
	

	/**
	 * no throwing
	 */
	@Override
	public final void deInit() {

		bdb.deInit();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.storagewrapper.StorageWrapper#ensureGroup(org.dml.level2.NodeID,
	 * org.dml.level2.NodeID)
	 */
	@Override
	public boolean ensureGroup( NodeID first, NodeID second )
			throws StorageException {

		RunTime.assertNotNull( first, second );
		try {
			return bdb.getDBMapTupleNodeIDs().ensureGroup( first, second );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
}// end of class
