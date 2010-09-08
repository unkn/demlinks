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



import org.dml.database.bdb.level1.AllTupleBindings;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.level010.TheStoredSymbol;
import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.references.method.MethodParams;

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
 * 
 * fixed... Symbols here don't need to have a JavaID associated with them<br>
 */
public class DBMapSymbolsTuple
		extends
		OneToManyDBMap<TheStoredSymbol, TheStoredSymbol>
{
	
	/**
	 * constructor
	 */
	public DBMapSymbolsTuple()
	{
		
		super(
				TheStoredSymbol.class,
				AllTupleBindings.getBinding( TheStoredSymbol.class ),
				TheStoredSymbol.class,
				AllTupleBindings.getBinding( TheStoredSymbol.class ) );
	}
	

	/**
	 * obviously initial and terminal must already exist as Symbols associated
	 * with
	 * JavaIDs<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 * @return true if existed already; false if it didn't exist before call
	 * @throws DatabaseException
	 */
	@Override
	public
			boolean
			ensureVector(
							TheStoredSymbol initialNode,
							TheStoredSymbol terminalNode )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		
		return super.ensureVector(
									initialNode,
									terminalNode );
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
	public
			boolean
			isVector(
						TheStoredSymbol initialNode,
						TheStoredSymbol terminalNode )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		
		return super.isVector(
								initialNode,
								terminalNode );
	}
	

	@Override
	public
			BDBVectorIterator<TheStoredSymbol, TheStoredSymbol>
			getIterator_on_Initials_of(
										TheStoredSymbol terminalObject )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( terminalObject );
		return super.getIterator_on_Initials_of( terminalObject );
	}
	

	@Override
	public
			BDBVectorIterator<TheStoredSymbol, TheStoredSymbol>
			getIterator_on_Terminals_of(
											TheStoredSymbol initialObject )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( initialObject );
		return super.getIterator_on_Terminals_of( initialObject );
	}
	

	@Override
	public
			int
			countInitials(
							TheStoredSymbol ofTerminalObject )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( ofTerminalObject );
		return super.countInitials( ofTerminalObject );
	}
	

	@Override
	public
			int
			countTerminals(
							TheStoredSymbol ofInitialObject )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull( ofInitialObject );
		return super.countTerminals( ofInitialObject );
	}
	

	/**
	 * @param initial1
	 * @param initial2
	 * @return
	 * @throws DatabaseException
	 */
	@Override
	public
			TheStoredSymbol
			findCommonTerminalForInitials(
											TheStoredSymbol initial1,
											TheStoredSymbol initial2 )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull(
								initial1,
								initial2 );
		return super.findCommonTerminalForInitials(
													initial1,
													initial2 );
	}
	

	/**
	 * @param initial
	 * @param terminal
	 * @return true if existed
	 * @throws DatabaseException
	 */
	@Override
	public
			boolean
			removeVector(
							TheStoredSymbol initial,
							TheStoredSymbol terminal )
					throws DatabaseException
	{
		
		RunTime.assumedNotNull(
								initial,
								terminal );
		return super.removeVector(
									initial,
									terminal );
	}
	

}
