/**
 * File creation: May 30, 2009 8:20:44 PM
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


package org.dml.level1;



import org.dml.tools.RunTime;



/**
 * is stored in Storage<br>
 * so basically, Symbol is a long, in Storage
 * and it's one2one associated with a SymbolJavaID (which is basically a String
 * in
 * java)<br>
 * it's not really important what Symbol is, rather it's important this one2one
 * association between the two<br>
 * 
 * in Storage, a Sequence is used to generate a new Symbol that won't equate
 * with any other already existent<br>
 * 
 * ----ignoring the above, I say:
 * so this is a java representation of a symbol that's stored in a dbase, which
 * means it is read-only , caching the symbol on the database side, so that it
 * can be used or referenced on this side<br>
 * Therefore when you want to create a symbol, it will be created for you on the
 * database side, and then a Symbol here in java will be associated with that to
 * represent it. You should not be able to create the symbol here first, then in
 * dbase.<br>
 */
public class Symbol {
	
	private final long	selfInBDB;	// in BDB
									
	/**
	 * constructor, call only internally
	 * 
	 * @param iD
	 */
	private Symbol( long iD ) {

		selfInBDB = iD;
	}
	
	/**
	 * the only one calling this should be the BDB subsystem<br>
	 * not the user<br>
	 * with a few exceptions for JUnit tests<br>
	 * 
	 * @param BDBSymbol
	 *            given by the BDB dbase knowing that it's unique
	 * @return
	 */
	public static Symbol internalNewSymbolRepresentationFor( long BDBSymbol ) {

		RunTime.assumedNotNull( BDBSymbol );
		return new Symbol( BDBSymbol );
	}
	
	
	@Override
	public String toString() {

		return this.getClass().getSimpleName() + ":"
				+ String.valueOf( selfInBDB );
	}
	
	/**
	 * compares by content if refs are different<br>
	 * equals always compares by content
	 * 
	 * @param nid
	 * @return
	 */
	@Override
	public boolean equals( Object nid ) {

		RunTime.assumedNotNull( nid );
		if ( ( !this.getClass().isAssignableFrom( nid.getClass() ) )
				|| ( this.getClass() != nid.getClass() ) ) {
			RunTime.bug( "you passed a different type parameter; must be a bug somewhere" );
		}
		if ( ( super.equals( nid ) ) || // ( this.getAsString().equals( (
				// (NodeID)nid ).getAsString() ) ) ) {
				( ( (Symbol)nid ).selfInBDB == selfInBDB ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public long internalGetForBDBBinding() {

		return selfInBDB;
	}
}
