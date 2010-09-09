/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net> Copyright (C) 2005-2010 UnKn
 * <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with DeMLinks. If not, see
 * <http://www.gnu.org/licenses/>.
 */



package org.dml.level010;



import java.util.HashMap;

import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.RunTime;
import org.dml.tools.TwoWayHashMap;



/**
 * Always use .equals() although this will compare by refs<br>
 * use equals when sure 'this' is not null ie. this.equals(that)<br>
 * is stored in Storage<br>
 * so basically, Symbol is a long, in Storage and it's one2one associated with a SymbolJavaID (which is basically a
 * String in java)<br>
 * it's not really important what Symbol is, rather it's important this one2one association between the two<br>
 * 
 * in Storage, a Sequence is used to generate a new Symbol that won't equate with any other already existent<br>
 * 
 * ----ignoring the above, I say: so this is a java representation of a symbol that's stored in a dbase, which means it
 * is read-only , caching the symbol from the database side, so that it can be used or referenced on this side in java<br>
 * Therefore when you want to create a symbol, it will be created for you on the database side, and then a Symbol here
 * in java will be associated with that to represent it. You should not be able to create the symbol here first, then in
 * dbase.<br>
 * Thus, it is assumed that if the Symbol instance exists in java, then it also exists or will exist safely (ie. when
 * ensureVector(*,*) is called) in BDB or storage<br>
 * 
 * Using same Symbol representation in java, for any symbols in any dml environments. Ie. two dml environments use one
 * Symbol here in java.<br>
 * 
 * If the Symbol exists as an instance in java, it means it already exists inside the storage ie. in bdb<br>
 * but if we ever make a remove symbol from bdb, then we must also make some kind of Symbol.assumedValid() and make sure
 * to call that everywhere or so<br>
 * // TODO maybe an assumedValid that will check bdb to see if Symbol still exists // TODO also a remove symbol in the
 * environment //TODO a list of symbol instances for each storage type ie. 1 type is bdb
 * 
 * - so basically the storage part can request a new symbol here in java, but the java part cannot request the new part
 * in storage, in other words can't use a Symbol as a JavaID to retrieve the storage part from java. So you can refer to
 * an already existing storage part by using a Symbol (which already exists in java because either you used a request to
 * create new part in storage and that returned the Symbol, or it requested a storage part by using a JavaID and this
 * too returned the Symbol)<br>
 * - Symbol is the bridge between Java and Storage; Symbol represents something from storage; Symbols are lost when app
 * shuts down<br>
 * - JavaID is the bridge between same data in java(ie. string) and Storage<br>
 * - JavaID represents the same something persistently; JavaIDs are not lost when app shuts down<br>
 */
public class Symbol
{
	
	// in which this Symbol exists/was gotten from
	private final Level1_Storage_BerkeleyDB															bdbL1;
	// the stored symbol from bdb
	private final TheStoredSymbol																	tsSym;
	
	private static final HashMap<Level1_Storage_BerkeleyDB, TwoWayHashMap<Symbol, TheStoredSymbol>>	all_Symbols_from_BDBStorage	= new HashMap<Level1_Storage_BerkeleyDB, TwoWayHashMap<Symbol, TheStoredSymbol>>();
	
	
	/**
	 * private constructor
	 */
	private Symbol(
			Level1_Storage_BerkeleyDB storage,
			TheStoredSymbol theStoredSymbol )
	{
		RunTime.assumedNotNull(
								storage,
								theStoredSymbol );
		RunTime.assumedTrue( storage.isInitedSuccessfully() );
		bdbL1 = storage;
		tsSym = theStoredSymbol;
	}
	

	/**
	 * equals always compares by content BUT in this case it ONLY compares by references, because Symbol instances are
	 * not supposed to have the same contents, ever<br>
	 * 
	 * @param sym
	 */
	@Override
	public
			boolean
			equals(
					Object sym )
	{
		if ( null == sym )
		{
			RunTime
					.badCall( "you tried to compare to null, this may indicate bad programming somewhere; check it out!" );
			// return false;
		}
		else
		{
			if ( this.getClass() != sym.getClass() )
			{
				RunTime.badCall( "you tried to compare different class instances" );
			}
			else
			{
				if ( this == sym )
				{
					// same references => same Symbol
					return true;
				}
				else
				{
					// this means we have two different references, and they should be different in contents also!
					// but we do want to get warned when comparing Symbols from two different storages as this indicates
					// bad programming
					Symbol casted = (Symbol)sym;
					if ( this.getStorage() != casted.getStorage() )
					{
						RunTime
								.badCall( "you tried to compare two Symbols from different Storages, they may have the "
											+ "same stored contents ie. same long , but they are not supposed to be the same "
											+ "because they are in two different environments; maybe two Symbols with same "
											+ "JavaID would be equal at some higher level but not necessarily as Symbol r"
											+ "eferences/contents" );
					}
					else
					{
						// same storage then,
						if ( this.getTheStoredSymbol() == casted.getTheStoredSymbol() )
						{
							RunTime.bug( "same storage, and same tsSym? but different references? we're supposed"
											+ " to have only one instance for same contents Symbols" );
						}
						else
						{
							// same storage but different Symbols
							return false;
						}
					}
				}
			}
		}
		// RunTime.badCall( "do not use .equals() you should only compare by reference == " );
		RunTime.bug( "should be unreachable" );
		return false;
		// RunTime.assumedNotNull( sym );
		// if ( ( !this.getClass().isAssignableFrom( sym.getClass() ) )
		// || ( this.getClass() != sym.getClass() ) ) {
		// RunTime.bug(
		// "you passed a different type parameter; must be a bug somewhere" );
		// }
		// return this == sym;
	}
	

	public
			TheStoredSymbol
			getTheStoredSymbol()
	{
		RunTime.assumedNotNull( tsSym );
		return tsSym;
	}
	

	public
			Level1_Storage_BerkeleyDB
			getStorage()
	{
		RunTime.assumedNotNull( bdbL1 );
		return bdbL1;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public
			int
			hashCode()
	{
		/*
		 * It is not required that if two objects are unequal according to the java.lang.Object.equals(java.lang.Object)
		 * method, then calling the hashCode method on each of the two objects must produce distinct integer results.
		 * However, the programmer should be aware that producing distinct integer results for unequal objects may
		 * improve the performance of hash tables.
		 */
		return this.getStorage().hashCode()
				* 31
				+ this.getTheStoredSymbol().hashCode();
	}
	

	/**
	 * a new Symbol tightly connected to the storage from where it was taken from<br>
	 * 
	 * @param bdbL1
	 * @param tsSym
	 * @return never null; same Symbol instance for the same tsSym
	 */
	public static
			Symbol
			getNew(
					Level1_Storage_BerkeleyDB bdbL1,
					TheStoredSymbol tsSym )
	{
		RunTime.assumedNotNull(
								bdbL1,
								tsSym );
		RunTime.assumedTrue( bdbL1.isInitedSuccessfully() );
		TwoWayHashMap<Symbol, TheStoredSymbol> temp2WayHashMap = null;
		int count = 0;
		do
		{
			temp2WayHashMap = all_Symbols_from_BDBStorage.get( bdbL1 );
			if ( null == temp2WayHashMap )
			{
				if ( null != all_Symbols_from_BDBStorage.put(
																bdbL1,
																new TwoWayHashMap<Symbol, TheStoredSymbol>() ) )
				{
					RunTime.bug( "should not have existed, bugged get or what?" );
				}
				count++;
				if ( count > 1 )
				{
					RunTime.bug( "failed once, bug somewhere" );
				}
			}
		}
		while ( null == temp2WayHashMap );
		
		// even if tsSym is a different instance, as long as has same contents it will be found with getKey()
		Symbol existingOne = temp2WayHashMap.getKey( tsSym );
		if ( null == existingOne )
		{
			// did't already exist
			existingOne = new Symbol(
										bdbL1,
										tsSym );
			if ( temp2WayHashMap.ensure(
											existingOne,
											tsSym ) )
			{
				RunTime.bug( "could not have already existed!!" );
			}
		}
		
		return existingOne;
	}
}
