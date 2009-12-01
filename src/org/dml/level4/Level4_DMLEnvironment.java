/**
 * File creation: Oct 19, 2009 11:38:38 PM
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


package org.dml.level4;



import org.dml.level1.JavaID;
import org.dml.level1.Symbol;
import org.dml.level3.Level3_DMLEnvironment;
import org.dml.tools.RunTime;
import org.references.method.MethodParams;



/**
 * 
 * handling Lists
 */
public class Level4_DMLEnvironment extends Level3_DMLEnvironment {
	
	private static final JavaID	listJavaID							= JavaID.ensureJavaIDFor( "AllListOrderedOfSymbols" );
	public Symbol				allListsSymbol						= null;
	private static final JavaID	listOrderedOfElementCapsules_JavaID	= JavaID.ensureJavaIDFor( "AllListOrderedOfElementCapsules" );
	public Symbol				listOrderedOfElementCapsules_Symbol	= null;
	private static final JavaID	allHeads_JavaID						= JavaID.ensureJavaIDFor( "allHEADs" );
	public Symbol				allHeads_Symbol						= null;
	private static final JavaID	allTails_JavaID						= JavaID.ensureJavaIDFor( "allTAILs" );
	public Symbol				allTails_Symbol						= null;
	private static final JavaID	allElementCapsules_JavaID			= JavaID.ensureJavaIDFor( "allElementCapsules" );
	public Symbol				allElementCapsules_Symbol			= null;
	private static final JavaID	allPrevElementCapsules_JavaID		= JavaID.ensureJavaIDFor( "allPrevElementCapsules" );
	public Symbol				allPrevElementCapsules_Symbol		= null;
	private static final JavaID	allNextElementCapsules_JavaID		= JavaID.ensureJavaIDFor( "allNextElementCapsules" );
	public Symbol				allNextElementCapsules_Symbol		= null;
	private static final JavaID	allElementsOfEC_JavaID				= JavaID.ensureJavaIDFor( "allElements of ElementCapsules" );
	public Symbol				allElementsOfEC_Symbol				= null;
	
	@Override
	protected void start( MethodParams<Object> params ) {

		super.start( params );
		this.initGeneralSymbols();
	}
	
	/**
	 * 
	 */
	private void initGeneralSymbols() {

		// persistent across restarts, and whatever the Symbol is, it is
		// identifiable by the same JavaID no matter what
		allListsSymbol = this.ensureSymbol( listJavaID );
		listOrderedOfElementCapsules_Symbol = this.ensureSymbol( listOrderedOfElementCapsules_JavaID );
		allHeads_Symbol = this.ensureSymbol( allHeads_JavaID );
		allTails_Symbol = this.ensureSymbol( allTails_JavaID );
		allElementCapsules_Symbol = this.ensureSymbol( allElementCapsules_JavaID );
		allPrevElementCapsules_Symbol = this.ensureSymbol( allPrevElementCapsules_JavaID );
		allNextElementCapsules_Symbol = this.ensureSymbol( allNextElementCapsules_JavaID );
		allElementsOfEC_Symbol = this.ensureSymbol( allElementsOfEC_JavaID );
	}
	
	public ListOrderedOfSymbols getAsList( Symbol listName, boolean allowNull,
			boolean allowDUPs ) {

		RunTime.assumedNotNull( listName, allowNull, allowDUPs );
		ListOrderedOfSymbols list = ListOrderedOfSymbols.getListOOSymbols(
				this, listName, allowNull, allowDUPs );
		return list;
	}
	
}
