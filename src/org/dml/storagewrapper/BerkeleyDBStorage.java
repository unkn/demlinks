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



/**
 * should throw only StorageException
 * 
 */
public class BerkeleyDBStorage {
	
	/**
	 * returns the NodeJID associated with the given NodeID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisNodeID
	 * @return NodeJID
	 * @throws StorageException
	 */
	public final static NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisNodeID );
		try {
			return BerkeleyDB.getDBMapJIDsToNodeIDs().getNodeJID(
					identifiedByThisNodeID );
		} catch ( DatabaseException ex ) {
			throw new StorageException( ex );
		}
	}
	
	/**
	 * returns the NodeID associated with the given NodeJID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisJID
	 * @return NodeID
	 * @throws StorageException
	 */
	public final static NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisJID );
		try {
			return BerkeleyDB.getDBMapJIDsToNodeIDs().getNodeID(
					identifiedByThisJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public final static NodeID createNodeID( NodeJID fromJID )
			throws StorageException {

		RunTime.assertNotNull( fromJID );
		try {
			return BerkeleyDB.getDBMapJIDsToNodeIDs().createNodeID( fromJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	/**
	 * @param theJID
	 * @return
	 * @throws StorageException
	 */
	public final static NodeID ensureNodeID( NodeJID theJID )
			throws StorageException {

		RunTime.assertNotNull( theJID );
		try {
			return BerkeleyDB.getDBMapJIDsToNodeIDs().ensureNodeID( theJID );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
	
	/**
	 * no throwing
	 */
	public static final void init() {

		BerkeleyDB.initAll();
	}
	
	/**
	 * no throwing
	 */
	public static final void deInit() {

		BerkeleyDB.deInitAll();
	}
}// end of class
