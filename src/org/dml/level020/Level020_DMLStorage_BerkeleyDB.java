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



package org.dml.level020;



import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.database.bdb.level2.Level2_Storage_BerkeleyDB;
import org.dml.level010.Level010_DMLStorage_BerkeleyDB;
import org.dml.level010.Symbol;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level020_DMLStorage_BerkeleyDB
		extends
		Level010_DMLStorage_BerkeleyDB
		implements
		Level020_DMLStorageWrapper
{
	
	@VarLevel
	private final Level2_Storage_BerkeleyDB	bdb	= null;
	
	
	@Override
	public
			boolean
			ensureVector(
							Symbol initialNode,
							Symbol terminalNode )
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		try
		{
			return bdb.getDBMapSymbolsTuple().ensureVector(
															initialNode,
															terminalNode );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
			return false;
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#isGroup(org.dml.level1.NodeID,
	 * org.dml.level1.NodeID)
	 */
	@Override
	public
			boolean
			isVector(
						Symbol initialNode,
						Symbol terminalNode )
	{
		
		RunTime.assumedNotNull(
								initialNode,
								terminalNode );
		try
		{
			return bdb.getDBMapSymbolsTuple().isVector(
														initialNode,
														terminalNode );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	@Override
	public
			BDBVectorIterator<Symbol, Symbol>
			getIterator_on_Initials_of(
										Symbol terminalObject )
	{
		
		RunTime.assumedNotNull( terminalObject );
		try
		{
			return bdb.getDBMapSymbolsTuple().getIterator_on_Initials_of(
																			terminalObject );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#getIterator_on_Terminals_of(org
	 * .dml.level1.Symbol)
	 */
	@Override
	public
			BDBVectorIterator<Symbol, Symbol>
			getIterator_on_Terminals_of(
											Symbol initialObject )
	{
		
		RunTime.assumedNotNull( initialObject );
		try
		{
			return bdb.getDBMapSymbolsTuple().getIterator_on_Terminals_of(
																			initialObject );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	@Override
	public
			int
			countInitials(
							Symbol ofTerminalObject )
	{
		
		RunTime.assumedNotNull( ofTerminalObject );
		try
		{
			return bdb.getDBMapSymbolsTuple().countInitials(
																ofTerminalObject );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	@Override
	public
			int
			countTerminals(
							Symbol ofInitialObject )
	{
		
		RunTime.assumedNotNull( ofInitialObject );
		try
		{
			return bdb.getDBMapSymbolsTuple().countTerminals(
																ofInitialObject );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	/**
	 * @param initial1
	 * @param initial2
	 * @return
	 */
	@Override
	public
			Symbol
			findCommonTerminalForInitials(
											Symbol initial1,
											Symbol initial2 )
	{
		
		RunTime.assumedNotNull(
								initial1,
								initial2 );
		try
		{
			return bdb.getDBMapSymbolsTuple().findCommonTerminalForInitials(
																				initial1,
																				initial2 );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	

	@Override
	public
			boolean
			removeVector(
							Symbol initial,
							Symbol terminal )
	{
		
		RunTime.assumedNotNull(
								initial,
								terminal );
		try
		{
			return bdb.getDBMapSymbolsTuple().removeVector(
															initial,
															terminal );
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
	}
	


}// end of class
