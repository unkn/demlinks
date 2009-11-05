/**
 * File creation: Oct 19, 2009 11:39:43 PM
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
import org.dml.level1.LevelAll_DMLStorageWrapper;
import org.dml.level1.NodeID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class Level2_DMLEnvironment extends Level1_DMLEnvironment implements
		Level2_DMLStorageWrapper {
	
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level2_DMLStorageWrapper ) ) {
			// FIXME: is this working?
			// cannot be under VarLevel1, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	@Override
	protected void newVarLevelX() {

		storage = (LevelAll_DMLStorageWrapper)new Level2_DMLStorage_BerkeleyDB();
	}
	
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

		RunTime.assertNotNull( first, second );
		return storage.ensureGroup( first, second );
	}
}
