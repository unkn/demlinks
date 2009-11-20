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
	
	private DBMapSymbolsTuple	dbSymbolsTuple		= null;
	private final static String	dbSymbolsTuple_NAME	= "tuple(Symbol<->Symbol)";
	
	
	/**
	 * @return
	 */
	public DBMapSymbolsTuple getDBMapSymbolsTuple() {

		if ( null == dbSymbolsTuple ) {
			dbSymbolsTuple = new DBMapSymbolsTuple( this, dbSymbolsTuple_NAME );
			RunTime.assumedNotNull( dbSymbolsTuple );
		}
		return dbSymbolsTuple;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB#done()
	 */
	@Override
	protected void done( MethodParams<Object> params ) {

		if ( null != dbSymbolsTuple ) {
			dbSymbolsTuple = (DBMapSymbolsTuple)dbSymbolsTuple.silentClose();
		}
		super.done( params );
	}
}// class
