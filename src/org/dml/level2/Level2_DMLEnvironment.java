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



import java.io.File;

import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.Level0;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.dml.tools.StaticInstanceTrackerWithMethodParamsInterface;
import org.references.method.MethodParams;
import org.temporary.tests.PossibleParams;



/**
 * 
 *
 */
public class Level2_DMLEnvironment extends Level0 implements
		Level1_DMLStorageWrapper,
		StaticInstanceTrackerWithMethodParamsInterface {
	
	// this is the last level(subclass) of storage that is expected to be set,
	// even if new() will be on a subclass closer to base
	protected Level1_DMLStorageWrapper	storage					= null;
	
	private final static String			DEFAULT_BDB_ENV_PATH	= "."
																		+ File.separator
																		+ "bin"
																		+ File.separator
																		+ "mainEnv"
																		+ File.separator;
	
	/**
	 * construct, don't forget to call init(with param/s)
	 */
	public Level2_DMLEnvironment() {

		super();
		
	}
	
	
	@Override
	protected MethodParams<Object> getDefaults() {

		MethodParams<Object> def = super.getDefaults();
		
		def.set( PossibleParams.homeDir, DEFAULT_BDB_ENV_PATH );
		def.set( PossibleParams.wipeDB, false );
		return def;
	}
	
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return (StaticInstanceTrackerWithMethodParams)storage;
	}
	
	@Override
	protected void newVarLevelX() {

		storage = new Level1_DMLStorage_BerkeleyDB();
	}
	
	@Override
	protected void setVarLevelX( Object toValue ) {

		storage = (Level1_DMLStorageWrapper)toValue;
	}
	
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level1_DMLStorageWrapper ) ) {
			// FIXME: is this working?
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
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
		return storage.getNodeJID( nodeID );
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
		return storage.ensureNodeID( theJID );
	}
	
	/**
	 * @param identifiedByThisJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		return storage.getNodeID( identifiedByThisJID );
	}
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		return storage.createNodeID( fromJID );
	}
	
}
