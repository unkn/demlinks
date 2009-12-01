/**
 * File creation: Jun 3, 2009 12:32:27 PM
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


package org.dml.database.bdb.level1;



import org.dml.error.BugError;
import org.dml.level1.JavaID;
import org.dml.level1.Symbol;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.DatabaseException;



/**
 *this adds a Sequence for NodeID generation (ie. get a new unique NodeID)<br>
 *and the methods that use NodeID and NodeJavaID objects<br>
 *lookup by either NodeJavaID or NodeID<br>
 */
public class DBMap_JavaIDs_To_Symbols extends OneToOneDBMap<JavaID, Symbol> {
	
	
	/**
	 * @param dbName1
	 * @throws DatabaseException
	 */
	public DBMap_JavaIDs_To_Symbols( Level1_Storage_BerkeleyDB bdb1,
			String dbName1 ) throws DatabaseException {

		super( bdb1, dbName1, JavaID.class,
				AllTupleBindings.getBinding( JavaID.class ), Symbol.class,
				AllTupleBindings.getBinding( Symbol.class ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.database.bdb.level1.OneToOneDBMap#done(org.references.method.
	 * MethodParams)
	 */
	@Override
	public void deInit() {

		Log.entry( "deinit " + this.getClass().getSimpleName() + " with name: "
				+ dbName );
		super.deInit();
	}
	
	/**
	 * the Symbol must already exist else null is returned<br>
	 * this doesn't create a new Symbol for the supplied JavaID<br>
	 * remember there's a one to one mapping between a JavaID and a Symbol
	 * 
	 * @param fromJavaID
	 * @return null if not found;
	 * @throws DatabaseException
	 */
	public Symbol getSymbol( JavaID fromJavaID ) throws DatabaseException {

		RunTime.assumedNotNull( fromJavaID );
		Symbol sym = this.getData( fromJavaID );
		return sym;// can be null
	}
	
	

	/**
	 * @param fromJavaID
	 * @return
	 * @throws DatabaseException
	 * @throws BugError
	 */
	public Symbol createSymbol( JavaID fromJavaID ) throws DatabaseException {

		if ( null != this.getSymbol( fromJavaID ) ) {
			// already exists
			RunTime.bug( "bad programming, the JavaID is already associated with one NodeID !" );// throws
		}
		// doesn't exist, make it:
		return this.internal_makeNewSymbol( fromJavaID );
	}
	
	/**
	 * the fromJavaID must not already be mapped to another NodeID before
	 * calling
	 * this method!
	 * 
	 * @param fromJavaID
	 * @return the new NodeID mapped to fromJavaID<br>
	 *         never null
	 * @throws DatabaseException
	 */
	private final Symbol internal_makeNewSymbol( JavaID fromJavaID )
			throws DatabaseException {

		RunTime.assumedNotNull( fromJavaID );
		Symbol uniqueSymbol = bdb.getUniqueSymbolsGenerator().getNewUniqueSymbol();
		RunTime.assumedNotNull( uniqueSymbol );
		if ( this.ensureVector( fromJavaID, uniqueSymbol ) ) {
			RunTime.bug( "should not have already existed" );
		}
		return uniqueSymbol;
	}
	
	/**
	 * makes sure the one to one mapping between the two exists, if it doesn't
	 * it will after call
	 * 
	 * @param javaID
	 * @param symbol
	 * @return true if already existed
	 * @throws DatabaseException
	 */
	public final boolean ensureVector( JavaID javaID, Symbol symbol )
			throws DatabaseException {

		RunTime.assumedNotNull( javaID, symbol );
		
		return this.link( javaID, symbol );
	}
	
	/**
	 * one to one mapping between the two<br>
	 * must not already exist, can only exist once
	 * 
	 * @param javaID
	 * @param symbol
	 * @throws DatabaseException
	 * @throws Bug
	 *             if already exists
	 */
	public final void newVector( JavaID javaID, Symbol symbol )
			throws DatabaseException {

		RunTime.assumedNotNull( javaID, symbol );
		
		if ( this.link( javaID, symbol ) ) {
			// already exists
			RunTime.bug( "Already exists. Use ensureVector() if you're not sure if it may exist already or not." );
		}
	}
	
	/**
	 * get or create and get, a NodeID from the given JavaID
	 * 
	 * @param fromJavaID
	 * @return
	 * @throws DatabaseException
	 */
	public Symbol ensureSymbol( JavaID fromJavaID ) throws DatabaseException {

		Symbol nid = this.getSymbol( fromJavaID );
		if ( null == nid ) {
			// no NodeID for JavaID yet, make new one
			nid = this.internal_makeNewSymbol( fromJavaID );
		}
		RunTime.assumedNotNull( nid );// this is stupid
		return nid;
	}
	
	/**
	 * @param fromSymbol
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public JavaID getJavaID( Symbol fromSymbol ) throws DatabaseException {

		RunTime.assumedNotNull( fromSymbol );
		JavaID jid = this.getKey( fromSymbol );
		// RunTime.assertNotNull( jid );
		return jid;
	}
	
}
