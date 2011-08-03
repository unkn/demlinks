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



package org.dml.database.bdb.level1;



import org.dml.error.*;
import org.dml.level010.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.references.method.*;

import com.sleepycat.db.*;



/**
 * this adds a Sequence for NodeID generation (ie. get a new unique NodeID)<br>
 * and the methods that use NodeID and NodeJavaID objects<br>
 * lookup by either NodeJavaID or NodeID<br>
 */
public class DBMap_JavaIDs_To_Symbols extends OneToOneDBMap<JavaID, TheStoredSymbol> {
	
	
	/**
	 */
	public DBMap_JavaIDs_To_Symbols()
	// Level1_Storage_BerkeleyDB bdb1,
	// String dbName1 )
	{
		
		super( JavaID.class, AllTupleBindings.getBinding( JavaID.class ), TheStoredSymbol.class, AllTupleBindings
			.getBinding( TheStoredSymbol.class ) );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.database.bdb.level1.OneToOneDBMap#done(org.references.method.
	 * MethodParams)
	 */
	@Override
	protected void done( final MethodParams params ) {
		
		Log.entry( "deinit " + this.getClass().getSimpleName() + " with name: " + dbName );
		super.done( params );
	}
	
	
	/**
	 * the Symbol must already exist else null is returned<br>
	 * this doesn't create a new Symbol for the supplied JavaID<br>
	 * remember there's a one to one mapping between a JavaID and a Symbol
	 * 
	 * @param fromJavaID
	 * @return null if not found;
	 */
	public Symbol getSymbol( final JavaID fromJavaID ) {
		
		RunTime.assumedNotNull( fromJavaID );
		final TheStoredSymbol tsSym = getData( fromJavaID );
		if ( null == tsSym ) {
			return null;
		}
		final Symbol sym = Symbol.getNew( getBDBL1(), tsSym );
		return sym;
	}
	
	
	
	/**
	 * @param fromJavaID
	 * @return
	 * @throws DatabaseException
	 * @throws BugError
	 */
	public Symbol createSymbol( final JavaID fromJavaID ) throws DatabaseException
	// FIXME: remove all throws especially throws DatabaseException
	{
		
		if ( null != getSymbol( fromJavaID ) ) {
			// already exists
			RunTime.bug( "bad programming, the JavaID is already associated with one NodeID !" );// throws
		}
		// doesn't exist, make it:
		return internal_makeNewSymbol( fromJavaID );
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
	private final Symbol internal_makeNewSymbol( final JavaID fromJavaID ) throws DatabaseException {
		
		RunTime.assumedNotNull( fromJavaID );
		final Symbol uniqueSymbol = getBDBL1().getUniqueSymbolsGenerator().getNewUniqueSymbol();
		RunTime.assumedNotNull( uniqueSymbol );
		if ( ensureVector( fromJavaID, uniqueSymbol ) ) {
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
	 */
	public final boolean ensureVector( final JavaID javaID, final Symbol symbol ) {
		
		RunTime.assumedNotNull( javaID, symbol );
		
		return link( javaID, symbol.getTheStoredSymbol() );
	}
	
	
	/**
	 * one to one mapping between the two<br>
	 * must not already exist, can only exist once
	 * 
	 * @param javaID
	 * @param symbol
	 * @throws Bug
	 *             if already exists
	 */
	public final void newVector( final JavaID javaID, final Symbol symbol ) {
		
		RunTime.assumedNotNull( javaID, symbol );
		
		if ( link( javaID, symbol.getTheStoredSymbol() ) ) {
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
	public Symbol ensureSymbol( final JavaID fromJavaID ) throws DatabaseException {
		
		Symbol nid = getSymbol( fromJavaID );
		if ( null == nid ) {
			// no NodeID for JavaID yet, make new one
			nid = internal_makeNewSymbol( fromJavaID );
		}
		RunTime.assumedNotNull( nid );// this is stupid
		return nid;
	}
	
	
	/**
	 * @param fromSymbol
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public JavaID getJavaID( final Symbol fromSymbol ) {
		
		RunTime.assumedNotNull( fromSymbol );
		final JavaID jid = getKey( fromSymbol.getTheStoredSymbol() );
		// RunTime.assertNotNull( jid );
		return jid;
	}
	
}
