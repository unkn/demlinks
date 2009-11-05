/**
 * File creation: Nov 5, 2009 10:17:24 PM
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


package org.dml.level1;



import org.dml.storagewrapper.StorageException;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class LevelAll_DMLStorage_BerkeleyDB extends
		StaticInstanceTrackerWithMethodParams implements
		LevelAll_DMLStorageWrapper {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#ensureGroup(org.dml.level1.NodeID
	 * , org.dml.level1.NodeID)
	 */
	@Override
	public boolean ensureGroup( NodeID first, NodeID second )
			throws StorageException {

		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * 
	 * org.dml.level1.Level1_DMLStorageWrapper#createNodeID(org.dml.level1.NodeJID
	 * )
	 */
	@Override
	public NodeID createNodeID( NodeJID fromJID ) throws StorageException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * 
	 * org.dml.level1.Level1_DMLStorageWrapper#ensureNodeID(org.dml.level1.NodeJID
	 * )
	 */
	@Override
	public NodeID ensureNodeID( NodeJID theJID ) throws StorageException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_DMLStorageWrapper#getNodeID(org.dml.level1.NodeJID)
	 */
	@Override
	public NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_DMLStorageWrapper#getNodeJID(org.dml.level1.NodeID)
	 */
	@Override
	public NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException {

		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.tools.StaticInstanceTrackerWithMethodParamsInterface#init(org
	 * .references.method.MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		// TODO Auto-generated method stub
		
	}
	
}
