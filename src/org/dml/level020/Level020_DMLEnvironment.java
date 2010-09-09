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
import org.dml.level010.Level010_DMLEnvironment;
import org.dml.level010.Symbol;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;
import org.dml.tracking.Factory;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * handling Vectors
 * 
 */
public class Level020_DMLEnvironment
		extends
		Level010_DMLEnvironment
		implements
		Level020_DMLStorageWrapper
{
	
	@VarLevel
	private final Level020_DMLStorageWrapper	storage	= null;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level010.Level010_DMLEnvironment#internal_allocDefaultStorage(org.references.method.MethodParams)
	 */
	@Override
	protected
			void
			internal_allocDefaultStorage(
											MethodParams params )
	{
		
		// don't call super!
		// Level020_DMLStorage_BerkeleyDB stor = new Level020_DMLStorage_BerkeleyDB();
		// stor.init( params );
		Level020_DMLStorage_BerkeleyDB stor = Factory.getNewInstanceAndInit(
																				Level020_DMLStorage_BerkeleyDB.class,
																				params );
		params.set(
					PossibleParams.varLevelAll,
					stor );
	}
	

	@Override
	public
			boolean
			ensureVector(
							Symbol first,
							Symbol second )
	{
		
		RunTime.assumedNotNull(
								first,
								second );
		return storage.ensureVector(
										first,
										second );
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
						Symbol first,
						Symbol second )
	{
		
		RunTime.assumedNotNull(
								first,
								second );
		return storage.isVector(
									first,
									second );
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
		return storage.removeVector(
										initial,
										terminal );
	}
	

	@Override
	public
			SymbolIterator
			getIterator_on_Initials_of(
										Symbol terminalObject )
	{
		
		RunTime.assumedNotNull( terminalObject );
		return storage.getIterator_on_Initials_of( terminalObject );
	}
	

	@Override
	public
			SymbolIterator
			getIterator_on_Terminals_of(
											Symbol initialObject )
	{
		
		RunTime.assumedNotNull( initialObject );
		return storage.getIterator_on_Terminals_of( initialObject );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#countInitials(org.dml.level1.
	 * Symbol)
	 */
	@Override
	public
			long
			countInitials(
							Symbol ofTerminalObject )
	{
		
		RunTime.assumedNotNull( ofTerminalObject );
		return storage.countInitials( ofTerminalObject );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#countTerminals(org.dml.level1
	 * .Symbol)
	 */
	@Override
	public
			long
			countTerminals(
							Symbol ofInitialObject )
	{
		
		RunTime.assumedNotNull( ofInitialObject );
		return storage.countTerminals( ofInitialObject );
	}
	

	/**
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
		return storage.findCommonTerminalForInitials(
														initial1,
														initial2 );
	}
	

}
