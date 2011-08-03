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



package org.dml.level040;



import org.dml.level010.*;
import org.dml.level025.*;
import org.dml.level030.*;
import org.dml.tools.*;
import org.references.method.*;



/**
 * 
 * handling Lists
 */
public class Level040_DMLEnvironment extends Level030_DMLEnvironment {
	
	private static final JavaID	allowNull_JavaID				= JavaID.ensureJavaIDFor( "allowNull" );
	public SetOfTerminalSymbols	allowNull_Set					= null;
	private static final JavaID	allowDUPs_JavaID				= JavaID.ensureJavaIDFor( "allowDUPs" );
	public SetOfTerminalSymbols	allowDUPs_Set					= null;
	
	private static final JavaID	allListsOOSWFF_JavaID			= JavaID
																	.ensureJavaIDFor( "AllListOrderedOfSymbolsWithFastFind" );
	public SetOfTerminalSymbols	allListsOOSWFF_Set				= null;
	private static final JavaID	allListsOOS_JavaID				= JavaID.ensureJavaIDFor( "AllListOrderedOfSymbols" );
	public SetOfTerminalSymbols	allListsOOS_Set					= null;
	private static final JavaID	allListsOOEC_JavaID				= JavaID.ensureJavaIDFor( "AllListOrderedOfElementCapsules" );
	public SetOfTerminalSymbols	allListsOOEC_Set				= null;
	private static final JavaID	allHeads_JavaID					= JavaID.ensureJavaIDFor( "allHEADs" );
	public SetOfTerminalSymbols	allHeads_Set					= null;
	private static final JavaID	allTails_JavaID					= JavaID.ensureJavaIDFor( "allTAILs" );
	public SetOfTerminalSymbols	allTails_Set					= null;
	private static final JavaID	allElementCapsules_JavaID		= JavaID.ensureJavaIDFor( "allElementCapsules" );
	public SetOfTerminalSymbols	allElementCapsules_Set			= null;
	private static final JavaID	allPrevElementCapsules_JavaID	= JavaID.ensureJavaIDFor( "allPrevElementCapsules" );
	public SetOfTerminalSymbols	allPrevElementCapsules_Set		= null;
	private static final JavaID	allNextElementCapsules_JavaID	= JavaID.ensureJavaIDFor( "allNextElementCapsules" );
	public SetOfTerminalSymbols	allNextElementCapsules_Set		= null;
	private static final JavaID	allRef2ElementsInEC_JavaID		= JavaID.ensureJavaIDFor( "allRef2Elements in ElementCapsules" );
	public SetOfTerminalSymbols	allRef2ElementsInEC_Set			= null;
	
	
	@Override
	protected void start( final MethodParams params ) {
		
		super.start( params );
		initGeneralSymbols();
	}
	
	
	/**
	 * protected so we can override, else would've been private
	 */
	protected void initGeneralSymbols() {
		
		// persistent across restarts, and whatever the Symbol is, it is
		// identifiable by the same JavaID no matter what
		allowNull_Set = getAsSet( ensureSymbol( allowNull_JavaID ) );
		allowDUPs_Set = getAsSet( ensureSymbol( allowDUPs_JavaID ) );
		
		allListsOOSWFF_Set = getAsSet( ensureSymbol( allListsOOSWFF_JavaID ) );
		allListsOOS_Set = getAsSet( ensureSymbol( allListsOOS_JavaID ) );
		allListsOOEC_Set = getAsSet( ensureSymbol( allListsOOEC_JavaID ) );
		allHeads_Set = getAsSet( ensureSymbol( allHeads_JavaID ) );
		allTails_Set = getAsSet( ensureSymbol( allTails_JavaID ) );
		allElementCapsules_Set = getAsSet( ensureSymbol( allElementCapsules_JavaID ) );
		allPrevElementCapsules_Set = getAsSet( ensureSymbol( allPrevElementCapsules_JavaID ) );
		allNextElementCapsules_Set = getAsSet( ensureSymbol( allNextElementCapsules_JavaID ) );
		allRef2ElementsInEC_Set = getAsSet( ensureSymbol( allRef2ElementsInEC_JavaID ) );
	}
	
	
	// public ListOrderedOfSymbols getAsListOOS( Symbol listName, boolean allowNull, boolean allowDUPs ) {
	//
	// RunTime.assumedNotNull( listName, allowNull, allowDUPs );
	// ListOrderedOfSymbols list = ListOrderedOfSymbols.getAsListOOSymbols( this, listName, allowNull, allowDUPs );
	// return list;
	// }
	
	/**
	 * use this when you don't know the values for allowNull and allowDUPs
	 * 
	 * @param listName
	 * @return
	 */
	public ListOrderedOfSymbols getExistingListOOS( final Symbol listName ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbols list = ListOrderedOfSymbols.getExistingListOOSymbols( this, listName );
		return list;
	}
	
	
	/**
	 * use this when you do know the values for allowNull and allowDUPs, so they can be checked against the existing
	 * ones for consistency (else throws)
	 * 
	 * @param listName
	 * @param expectedAllowNull
	 * @param expectedAllowDUPs
	 * @return
	 */
	public ListOrderedOfSymbols getExistingListOOS( final Symbol listName, final boolean expectedAllowNull,
													final boolean expectedAllowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbols list =
			ListOrderedOfSymbols.getExistingListOOSymbols( this, listName, expectedAllowNull, expectedAllowDUPs );
		return list;
	}
	
	
	public ListOrderedOfSymbols getNewListOOS( final Symbol listName, final boolean allowNull, final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbols list = ListOrderedOfSymbols.getNewListOOSymbols( this, listName, allowNull, allowDUPs );
		return list;
	}
	
	
	/**
	 * if it doesn't exist is created<br>
	 * if it does exist is checked for passed allowNull/allowDUPs for consistency and it's retrieved<br>
	 * 
	 * @param listName
	 * @param allowNull
	 * @param allowDUPs
	 * @return
	 */
	public ListOrderedOfSymbols ensureListOOS( final Symbol listName, final boolean allowNull, final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbols list = ListOrderedOfSymbols.ensureListOOSymbols( this, listName, allowNull, allowDUPs );
		return list;
	}
	
	
	/**
	 * get existing
	 * 
	 * @param listName
	 * @param allowDUPs
	 *            expected value for this.
	 * @return
	 */
	public ListOrderedOfSymbolsWithFastFind getExistingListOOSWFF( final Symbol listName, final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbolsWithFastFind list =
			ListOrderedOfSymbolsWithFastFind.getExistingListOOSWFF( this, listName, allowDUPs );
		return list;
	}
	
	
	/**
	 * existing but with unknown params ie. the allowDUPs is unknown at least.
	 * 
	 * @param listName
	 * @return
	 */
	public ListOrderedOfSymbolsWithFastFind getExistingListOOSWFF( final Symbol listName ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbolsWithFastFind list = ListOrderedOfSymbolsWithFastFind.getExistingListOOSWFF( this, listName );
		return list;
	}
	
	
	public ListOrderedOfSymbolsWithFastFind getNewListOOSWFF( final Symbol listName, final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbolsWithFastFind list =
			ListOrderedOfSymbolsWithFastFind.getNewListOOSWFF( this, listName, allowDUPs );
		return list;
	}
	
	
	public ListOrderedOfSymbolsWithFastFind ensureListOOSWFF( final Symbol listName, final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( listName );
		final ListOrderedOfSymbolsWithFastFind list =
			ListOrderedOfSymbolsWithFastFind.ensureListOOSWFF( this, listName, allowDUPs );
		return list;
	}
}
