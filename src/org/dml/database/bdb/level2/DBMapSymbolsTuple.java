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
import org.dml.level010.Symbol;
import org.dml.level010.TheStoredSymbol;
import org.dml.level020.SymbolIterator;
import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
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
		Initer
{
	
	OneToManyDBMap<TheStoredSymbol, TheStoredSymbol>	composition	= null;
	
	
	/**
	 * constructor
	 */
	public DBMapSymbolsTuple()
	{
		// super(
		// TheStoredSymbol.class,
		// AllTupleBindings.getBinding( TheStoredSymbol.class ),
		// TheStoredSymbol.class,
		// AllTupleBindings.getBinding( TheStoredSymbol.class ) );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.Initer#start(org.references.method.MethodParams)
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	protected
			void
			start(
					MethodParams params )
	{
		RunTime.assumedNotNull( params );
		composition = Factory.getNewInstanceAndInit(
														OneToManyDBMap.class,
														params,
														TheStoredSymbol.class,
														AllTupleBindings.getBinding( TheStoredSymbol.class ),
														TheStoredSymbol.class,
														AllTupleBindings.getBinding( TheStoredSymbol.class ) );
		RunTime.assumedNotNull( composition );
		RunTime.assumedTrue( composition.isInitedSuccessfully() );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.Initer#done(org.references.method.MethodParams)
	 */
	@Override
	protected
			void
			done(
					MethodParams params )
	{
		if ( this.isInitedSuccessfully() )
		{
			RunTime.assumedNotNull( composition );
			RunTime.assumedTrue( composition.isInitedSuccessfully() );
		}
		
		if ( null != composition )
		{
			try
			{
				Factory.deInit( composition );
			}
			finally
			{
				composition = null;
			}
		}
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
	public
			boolean
			ensureVector(
							Symbol initialNode,
							Symbol terminalNode )
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		RunTime.assumedTrue( this.isInitedSuccessfully() );
		
		return composition.ensureVector(
											initialNode.getTheStoredSymbol(),
											terminalNode.getTheStoredSymbol() );
	}
	

	/**
	 * obviously first and second must already exist as NodeIDs associated with
	 * JavaIDs<br>
	 * 
	 * @param initialNode
	 * @param terminalNode
	 * @return
	 * @throws StorageException
	 */
	public
			boolean
			isVector(
						Symbol initialNode,
						Symbol terminalNode )
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		RunTime.assumedTrue( this.isInitedSuccessfully() );
		return composition.isVector(
										initialNode.getTheStoredSymbol(),
										terminalNode.getTheStoredSymbol() );
	}
	

	public// BDBVectorIterator<Symbol, Symbol>
			SymbolIterator
			getIterator_on_Initials_of(
										Symbol terminalObject )
	{
		
		RunTime.assumedNotNull( terminalObject );
		RunTime.assumedTrue( this.isInitedSuccessfully() );
		SymbolIterator si = new SymbolIterator(
												composition.getIterator_on_Initials_of( terminalObject
														.getTheStoredSymbol() ) );
		return si;
	}
	

	public// BDBVectorIterator<TheStoredSymbol, TheStoredSymbol>
			SymbolIterator
			getIterator_on_Terminals_of(
											Symbol initialObject )
	{
		
		RunTime.assumedNotNull( initialObject );
		return new SymbolIterator(
									composition.getIterator_on_Terminals_of( initialObject.getTheStoredSymbol() ) );
	}
	

	public
			long
			countInitials(
							Symbol ofTerminalObject )
	{
		
		RunTime.assumedNotNull( ofTerminalObject );
		return composition.countInitials( ofTerminalObject.getTheStoredSymbol() );
	}
	

	public
			long
			countTerminals(
							Symbol ofInitialObject )
	{
		
		RunTime.assumedNotNull( ofInitialObject );
		return composition.countTerminals( ofInitialObject.getTheStoredSymbol() );
	}
	

	/**
	 * @param initial1
	 * @param initial2
	 * @return
	 * @throws DatabaseException
	 */
	public
			Symbol
			findCommonTerminalForInitials(
											Symbol initial1,
											Symbol initial2 )
	{
		
		RunTime.assumedNotNull(
								initial1,
								initial2 );
		TheStoredSymbol tss = composition.findCommonTerminalForInitials(
																			initial1.getTheStoredSymbol(),
																			initial2.getTheStoredSymbol() );
		return Symbol.getNew(
								composition.getBDBL1(),
								tss );
	}
	

	/**
	 * @param initial
	 * @param terminal
	 * @return true if existed
	 * @throws DatabaseException
	 */
	public
			boolean
			removeVector(
							Symbol initial,
							Symbol terminal )
	{
		
		RunTime.assumedNotNull(
								initial,
								terminal );
		return composition.removeVector(
											initial.getTheStoredSymbol(),
											terminal.getTheStoredSymbol() );
	}
	


}
