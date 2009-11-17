/**
 * File creation: Jun 4, 2009 7:47:37 PM
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



import org.dml.database.bdb.level1.AllTupleBindings;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.level1.Symbol;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;

import com.sleepycat.je.DatabaseException;



/**
 * - tuple of Symbols are two symbol in a group, clearly knowing which one is
 * first(ie. left one) and which is the second(aka last or right one) ie.
 * vector(initial, terminal)<br>
 * - any Symbol can be associated with any Symbol (even with itself)<br>
 * - a Symbol can be associated with more than one Symbol<br>
 * - the first Symbol is the one being associated with; the second Symbol<br>
 * - the first is initial; the second is terminal; of a vector<br>
 * ie.<br>
 * A->D<br>
 * A->B<br>
 * A->C<br>
 * insertion order is irrelevant as there will be no order(well it's actually
 * sorted but should not be counted on, it's sorted by BDB internally for easy
 * search/find). The only thing you'd need to know here
 * is whether the tuple exists or not. And to parse all initial/terminal symbols<br>
 */
public class DBMapSymbolsTuple extends OneToManyDBMap<Symbol, Symbol> {
	
	/**
	 * constructor
	 * 
	 * @param dbName1
	 *            the name of the database that will hold the tuples
	 * @param db
	 *            the database holding the JavaIDs to NodeIDs 1to1 mappings,
	 *            that's currently BerkeleyDB.getDBMapJavaIDsToNodeIDs()
	 */
	public DBMapSymbolsTuple( Level1_Storage_BerkeleyDB bdb1, String dbName1 ) {

		super( bdb1, dbName1, Symbol.class,
				AllTupleBindings.getBinding( Symbol.class ), Symbol.class,
				AllTupleBindings.getBinding( Symbol.class ) );
	}
	
	/**
	 * obviously first and second must already exist as NodeIDs associated with
	 * JavaIDs<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 * @return true if existed already; false if it didn't exist before call
	 * @throws DatabaseException
	 */
	@Override
	public boolean ensureVector( Symbol initialNode, Symbol terminalNode )
			throws DatabaseException {

		RunTime.assertNotNull( initialNode, terminalNode );
		
		// checking that both NodeIDs exist already which means there are two
		// JavaIDs associated with them
		this.throwIfNotExist( initialNode );
		this.throwIfNotExist( terminalNode );
		
		return super.ensureVector( initialNode, terminalNode );
	}
	
	/**
	 * obviously first and second must already exist as NodeIDs associated with
	 * JavaIDs<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 * @return
	 * @throws StorageException
	 * @throws DatabaseException
	 */
	@Override
	public boolean isVector( Symbol initialNode, Symbol terminalNode )
			throws DatabaseException {

		RunTime.assertNotNull( initialNode, terminalNode );
		
		this.throwIfNotExist( initialNode );
		this.throwIfNotExist( terminalNode );
		
		return super.isVector( initialNode, terminalNode );
	}
	
	/**
	 * @param nid
	 * @throws StorageException
	 * @throws DatabaseException
	 */
	private void throwIfNotExist( Symbol nid ) throws DatabaseException {

		RunTime.assertNotNull( nid );
		if ( !this.existsSymbol( nid ) ) {
			RunTime.bug( "NodeID doesn't exist, and it SHOULD exist! it's NODE ID not JavaID" );
		}
	}
	
	private boolean existsSymbol( Symbol whichSymbol ) throws DatabaseException {

		return ( null != this.getBDBL1().getDBMap_JavaIDs_To_Symbols().getJavaID(
				whichSymbol ) );
	}
	

}
