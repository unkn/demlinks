/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



import org.dml.database.bdb.level1.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.references.method.*;



/**
 * 
 *
 */
public class Level2_Storage_BerkeleyDB extends Level1_Storage_BerkeleyDB {
	
	private DBMapSymbolsTuple	dbSymbolsTuple		= null;
	private final static String	dbSymbolsTuple_NAME	= "tuple(Symbol2Symbol)";
	
	
	/**
	 * @return never null
	 */
	public DBMapSymbolsTuple getDBMapSymbolsTuple() {
		RunTime.assumedTrue( isInitedSuccessfully() );
		RunTime.assumedNotNull( dbSymbolsTuple );
		RunTime.assumedTrue( dbSymbolsTuple.isInitedSuccessfully() );
		return dbSymbolsTuple;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB#done()
	 */
	@Override
	protected void done( final MethodParams params ) {
		if ( isInitedSuccessfully() ) {
			RunTime.assumedNotNull( dbSymbolsTuple );
		}
		
		if ( null != dbSymbolsTuple ) {
			// dbSymbolsTuple.deInit();
			try {
				Factory.deInit_WithPostponedThrows( dbSymbolsTuple );
			} finally {
				dbSymbolsTuple = null;
			}
		}
		// the above must be deInit-ed first
		super.done( params );// last
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB#start(org.references.method.MethodParams)
	 */
	@Override
	protected void start( final MethodParams params ) {
		RunTime.assumedNotNull( params );
		
		super.start( params );// first
		
		final MethodParams iParams = MethodParams.getNew();// params.getClone();
		RunTime.assumedNull( iParams.set( PossibleParams.level1_BDBStorage, this ) );
		RunTime.assumedNull( iParams.set( PossibleParams.dbName, dbSymbolsTuple_NAME ) );
		
		dbSymbolsTuple = Factory.getNewInstanceAndInit( DBMapSymbolsTuple.class, iParams );
		RunTime.assumedNotNull( dbSymbolsTuple );
		RunTime.assumedTrue( dbSymbolsTuple.isInitedSuccessfully() );
		
	}
}// class
