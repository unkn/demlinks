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



package org.dml.level010;



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
 * means it is read-only , caching the symbol from the database side, so that it
 * can be used or referenced on this side in java<br>
 * Therefore when you want to create a symbol, it will be created for you on the
 * database side, and then a Symbol here in java will be associated with that to
 * represent it. You should not be able to create the symbol here first, then in
 * dbase.<br>
 * Thus, it is assumed that if the Symbol instance exists in java, then it also
 * exists or will exist safely (ie. when ensureVector(*,*) is called) in BDB or
 * storage<br>
 * 
 * Using same Symbol representation in java, for any symbols in any dml
 * environments. Ie. two dml environments use one Symbol here in java.<br>
 * 
 * If the Symbol exists as an instance in java, it means it already exists
 * inside the storage ie. in bdb<br>
 * but if we ever make a remove symbol from bdb, then we must also make some kind of Symbol.assumedValid() and make sure
 * to call that everywhere or so<br>
 * // TODO maybe an assumedValid that will check bdb to see if Symbol still exists
 * // TODO also a remove symbol in the environment
 * //TODO a list of symbol instances for each storage type ie. 1 type is bdb
 * 
 * - so basically the storage part can request a new symbol here in java, but the java part cannot request the new part
 * in
 * storage, in other words can't use a Symbol as a JavaID to retrieve the storage part from java. So you can refer to an
 * already existing storage part by using a Symbol (which already exists in java because either you used a request to
 * create
 * new part in storage and that returned the Symbol, or it requested a storage part by using a JavaID and this too
 * returned the Symbol)<br>
 * - Symbol is the bridge between Java and Storage; Symbol represents something from storage; Symbols are lost when app
 * shuts down<br>
 * - JavaID is the bridge between same data in java(ie. string) and Storage<br>
 * - JavaID represents the same something persistently; JavaIDs are not lost when app shuts down<br>
 */
public class Symbol
{
	
	
	public Symbol()
	{
		//
	}
	

	/**
	 * equals always compares by content BUT in this case it ONLY compares by
	 * references, because Symbol instances are not supposed to have the same
	 * contents, ever<br>
	 * 
	 * @param sym
	 * @return
	 */
	@Deprecated
	@Override
	public
			boolean
			equals(
					Object sym )
	{
		
		RunTime.badCall( "do not use .equals() you should only compare by reference == " );
		return false;
		// RunTime.assumedNotNull( sym );
		// if ( ( !this.getClass().isAssignableFrom( sym.getClass() ) )
		// || ( this.getClass() != sym.getClass() ) ) {
		// RunTime.bug(
		// "you passed a different type parameter; must be a bug somewhere" );
		// }
		// return this == sym;
	}
	
}
