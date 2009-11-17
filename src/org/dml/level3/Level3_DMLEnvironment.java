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


package org.dml.level3;



import org.dml.level1.JavaID;
import org.dml.level1.Symbol;
import org.dml.level2.Level2_DMLEnvironment;
import org.references.method.MethodParams;



/**
 * 
 * handling Lists
 */
public class Level3_DMLEnvironment extends Level2_DMLEnvironment {
	
	private static final JavaID	listJavaID		= JavaID.ensureJavaIDFor( "AllLists" );
	public Symbol				allListsSymbol	= null;
	
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
	}
	
	public ListID newList( Symbol listName ) {

		ListID list = new ListID( this, listName );
		return list;
	}
}
