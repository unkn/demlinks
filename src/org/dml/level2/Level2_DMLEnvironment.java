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

import org.dml.level1.Level1_DMLEnvironment;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;
import org.temporary.tests.PossibleParams;



/**
 * 
 *
 */
public class Level2_DMLEnvironment extends Level1_DMLEnvironment implements
		Level2_DMLStorageWrapper {
	
	private Level2_DMLStorageWrapper	storageL2				= null;
	
	private final static String			DEFAULT_BDB_ENV_PATH	= "."
																		+ File.separator
																		+ "bin"
																		+ File.separator
																		+ "mainEnv"
																		+ File.separator;
	
	// temporary:
	// private String envHomeDir = null;
	
	// private boolean wipeEnvFirst = false;
	
	/**
	 * construct, don't forget to call init(with param/s)
	 */
	public Level2_DMLEnvironment() {

		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * org.dml.level1.Level1_DMLEnvironment#init(org.references.method.MethodParams
	 * )
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		super.init( this.internalInit( storageL2, params ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_DMLEnvironment#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return (StaticInstanceTrackerWithMethodParams)storageL2;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#getDefaults()
	 */
	@Override
	protected MethodParams<Object> getDefaults() {

		MethodParams<Object> def = super.getDefaults();
		def.set( PossibleParams.homeDir, DEFAULT_BDB_ENV_PATH );
		def.set( PossibleParams.wipeDB, false );
		return def;
	}
	
	// /**
	// * @param envHomeDir1
	// * @param wipeEnvFirst1
	// * @throws StorageException
	// */
	// private void Level2_DMLinit( String envHomeDir1, boolean wipeEnvFirst1 )
	// throws StorageException {
	//
	// RunTime.assertNotNull( envHomeDir1, wipeEnvFirst1 );
	// envHomeDir = envHomeDir1;
	// wipeEnvFirst = wipeEnvFirst1;
	// inited = true;
	// }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_DMLEnvironment#newVarLevelX()
	 */
	@Override
	protected Object newVarLevelX() {

		storageL2 = new Level2_BerkeleyDBStorage();
		return storageL2;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_DMLEnvironment#setVarLevelX(java.lang.Object)
	 */
	@Override
	protected void setVarLevelX( Object toValue ) {

		storageL2 = (Level2_DMLStorageWrapper)toValue;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_DMLEnvironment#checkVarLevelX(java.lang.Object)
	 */
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level2_DMLStorageWrapper ) ) {
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	// /**
	// * @param envHomeDir1
	// * @param wipeEnvFirst1
	// * this should be false, unless inside a JUnit; will delete all
	// * data
	// * @throws StorageException
	// */
	// public void init( String envHomeDir1, boolean wipeEnvFirst1 )
	// throws StorageException {
	//
	// this.Level2_DMLinit( envHomeDir1, wipeEnvFirst1 );
	// super.init();// this will call start() from this class
	// }
	
	// /**
	// * @param envHomeDir1
	// * @throws StorageException
	// */
	// public void init( String envHomeDir1 ) throws StorageException {
	//
	// this.init( envHomeDir1, false );
	// }
	//	
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
		return storageL2.getNodeJID( nodeID );
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
		return storageL2.ensureNodeID( theJID );
	}
	
	/**
	 * @param identifiedByThisJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		return storageL2.getNodeID( identifiedByThisJID );
	}
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		return storageL2.createNodeID( fromJID );
	}
	
}
