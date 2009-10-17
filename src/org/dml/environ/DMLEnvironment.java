/**
 * File creation: May 30, 2009 12:16:28 AM
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


package org.dml.environ;



import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.storagewrapper.BerkeleyDBStorage;
import org.dml.storagewrapper.StorageException;
import org.dml.storagewrapper.StorageWrapper;
import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class DMLEnvironment {
	
	public static final NodeJID		AllWords				= NodeJID.ensureJIDFor( "AllWords" );
	
	public static final String		BDB_ENVIRONMENT_HOMEDIR	= ".\\bin";
	public static StorageWrapper	Storage;
	
	public static final void init() throws StorageException {

		Storage = new BerkeleyDBStorage( BDB_ENVIRONMENT_HOMEDIR );
		// Storage.init( BDB_ENVIRONMENT_HOMEDIR );
	}
	
	public static final void deInit() {

		Storage.deInit();
	}
	
	/**
	 * there's a one to one mapping between NodeID and NodeJID<br>
	 * given the NodeID return its NodeJID<br>
	 * NodeIDs are on some kind of Storage<br>
	 * 
	 * @param nodeID
	 * @return NodeJID
	 * @throws StorageException
	 */
	public static NodeJID getJIDFor( NodeID nodeID ) throws StorageException {

		RunTime.assertNotNull( nodeID );
		return DMLEnvironment.Storage.getNodeJID( nodeID );
	}
	
	/**
	 * eget=ensure get<br>
	 * make a new one if it doesn't exist<br>
	 * but if exists don't complain<br>
	 * 
	 * @param theJID
	 *            this JID and this Node will be mapped 1 to 1
	 * @return never null
	 * @throws StorageException
	 */
	public static NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		return DMLEnvironment.Storage.ensureNodeID( theJID );
	}
	
	/**
	 * @param identifiedByThisJID
	 * @return
	 * @throws StorageException
	 */
	public static NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		return DMLEnvironment.Storage.getNodeID( identifiedByThisJID );
	}
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public static NodeID createNodeID( NodeJID fromJID )
			throws StorageException {

		return DMLEnvironment.Storage.createNodeID( fromJID );
	}
	
}
