/**
 * File creation: Jun 7, 2009 6:42:00 PM
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


package org.dml.database.bdb;


/**
 * FIXME: not sure yet if we need this<br>
 * 
 */
public class DBMapTupleNodeJIDs extends DBMapTupleNodeIDs {
	
	/**
	 * @param dbName1
	 */
	public DBMapTupleNodeJIDs( String dbName1 ) {

		super( dbName1, mapJIDs2IDs );
	}
	
}
