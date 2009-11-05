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



import org.dml.database.bdb.Level1_Storage_BerkeleyDB;
import org.dml.level1.NodeJID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.Level0;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level1_DMLStorage_BerkeleyDB extends Level0 implements
		Level1_DMLStorageWrapper {
	
	protected Level1_Storage_BerkeleyDB	bdbL1	= null;
	
	/**
	 * constructor, don't forget to call init(...)
	 */
	public Level1_DMLStorage_BerkeleyDB() {

		super();
	}
	
	@Override
	protected void setVarLevelX( Object toValue ) {

		bdbL1 = (Level1_Storage_BerkeleyDB)toValue;
	}
	
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return bdbL1;
	}
	
	@Override
	protected void newVarLevelX() {

		bdbL1 = new Level1_Storage_BerkeleyDB();
	}
	
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level1_Storage_BerkeleyDB ) ) {
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	

	// =============================================
	@Override
	public final NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException {

		RunTime.assertNotNull( identifiedByThisNodeID );
		try {
			return bdbL1.getDBMapJIDsToNodeIDs().getNodeJID(
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
			return bdbL1.getDBMapJIDsToNodeIDs().getNodeID( identifiedByThisJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	@Override
	public final NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		RunTime.assertNotNull( fromJID );
		try {
			return bdbL1.getDBMapJIDsToNodeIDs().createNodeID( fromJID );
		} catch ( DatabaseException dbe ) {
			throw new StorageException( dbe );
		}
	}
	
	
	@Override
	public final NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		try {
			return bdbL1.getDBMapJIDsToNodeIDs().ensureNodeID( theJID );
		} catch ( DatabaseException de ) {
			throw new StorageException( de );
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		bdbL1.deInit();
		bdbL1 = null;
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		if ( null == bdbL1 ) {
			// called init() which is not supported
			RunTime.badCall( "please don't use init() w/o params" );
		}
	}
	

}// end of class
