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


package org.dml.level2;



import org.dml.level1.Level1_DMLEnvironment;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class Level2_DMLEnvironment extends Level1_DMLEnvironment implements
		Level2_DMLStorageWrapper {
	
	

	protected Level2_DMLStorageWrapper	Storage			= null;
	
	private boolean						inited			= false;
	
	// temporary:
	private String						envHomeDir		= null;
	
	private boolean						wipeEnvFirst	= false;
	
	/**
	 * construct, don't forget to call init(with param/s)
	 */
	public Level2_DMLEnvironment() {

	}
	
	@Override
	protected void start() {

		// to prevent user from using just init(),
		// TODO: ? too bad we can't use a default init() w/o params
		if ( !inited ) {
			RunTime.BadCallError( "must use the other init()s" );
		}
		super.start();
	}
	
	@Override
	protected void done() {

		inited = false;
		super.done();
	}
	
	/**
	 * @param envHomeDir1
	 * @param wipeEnvFirst1
	 * @throws StorageException
	 */
	private void Level2_DMLinit( String envHomeDir1, boolean wipeEnvFirst1 )
			throws StorageException {

		RunTime.assertNotNull( envHomeDir1, wipeEnvFirst1 );
		envHomeDir = envHomeDir1;
		wipeEnvFirst = wipeEnvFirst1;
	}
	
	
	@Override
	protected void storageInit() throws StorageException {

		if ( null == Storage ) {
			Storage = new Level2_BerkeleyDBStorage();
			
		}
		Storage.init( envHomeDir, wipeEnvFirst );
		// won't call super, because we get rid of that level of storage since
		// it's missing new features
	}
	
	/**
	 * @param envHomeDir1
	 * @param wipeEnvFirst1
	 *            this should be false, unless inside a JUnit; will delete all
	 *            data
	 * @throws StorageException
	 */
	public void init( String envHomeDir1, boolean wipeEnvFirst1 )
			throws StorageException {

		this.Level2_DMLinit( envHomeDir1, wipeEnvFirst1 );
		inited = true;
		super.init();
	}
	
	/**
	 * @param envHomeDir1
	 * @throws StorageException
	 */
	public void init( String envHomeDir1 ) throws StorageException {

		this.init( envHomeDir1, false );
	}
	
	// ---------------------------------------------
	/**
	 * there's a one to one mapping between NodeID and NodeJID<br>
	 * given the NodeID return its NodeJID<br>
	 * NodeIDs are on some kind of Storage<br>
	 * 
	 * @param nodeID
	 * @return NodeJID
	 * @throws StorageException
	 */
	public NodeJID getNodeJID( NodeID nodeID ) throws StorageException {

		RunTime.assertNotNull( nodeID );
		return Storage.getNodeJID( nodeID );
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
	public NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		return Storage.ensureNodeID( theJID );
	}
	
	/**
	 * @param identifiedByThisJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		return Storage.getNodeID( identifiedByThisJID );
	}
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		return Storage.createNodeID( fromJID );
	}
	
}
