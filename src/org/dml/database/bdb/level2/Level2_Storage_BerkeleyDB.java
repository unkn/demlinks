/**
 * File creation: May 31, 2009 7:46:58 PM
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


package org.dml.database.bdb.level2;



import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.RunTime;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class Level2_Storage_BerkeleyDB extends Level1_Storage_BerkeleyDB {
	
	private DBMapTupleNodeIDs	dbTupleNIDs			= null;
	private final static String	dbTupleNIDs_NAME	= "tuple(NodeID<->NodeID)";
	
	
	/**
	 * @return
	 */
	public DBMapTupleNodeIDs getDBMapTupleNodeIDs() {

		if ( null == dbTupleNIDs ) {
			dbTupleNIDs = new DBMapTupleNodeIDs( this, dbTupleNIDs_NAME );
			RunTime.assertNotNull( dbTupleNIDs );
		}
		return dbTupleNIDs;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB#done()
	 */
	@Override
	protected void done( MethodParams<Object> params ) {

		if ( null != dbTupleNIDs ) {
			dbTupleNIDs = (DBMapTupleNodeIDs)dbTupleNIDs.silentClose();
		}
		super.done( params );
	}
}// class
