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
import org.dml.level1.Level1_DMLStorage_BerkeleyDB;
import org.dml.level1.NodeID;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level2_DMLStorage_BerkeleyDB extends Level1_DMLStorage_BerkeleyDB
		implements Level2_DMLStorageWrapper {
	
	@VarLevel
	private final Level1_Storage_BerkeleyDB	bdb	= null;
	
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
