/**
 * File creation: Oct 19, 2009 3:26:40 AM
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


package org.temporary.bdb;



import org.dml.JUnits.Consts;
import org.dml.database.bdb.Level1_Storage_BerkeleyDB;
import org.dml.level1.NodeJID;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class BDBTest {
	
	public static void main( String[] args ) throws DatabaseException {

		Level1_Storage_BerkeleyDB b = new Level1_Storage_BerkeleyDB( Consts.BDB_ENV_PATH, true );
		try {
			b.getDBMapJIDsToNodeIDs().createNodeID(
					NodeJID.ensureJIDFor( "duh" ) );
		} finally {
			b.deInit();
		}
		System.out.println( "end." );
	}
}
