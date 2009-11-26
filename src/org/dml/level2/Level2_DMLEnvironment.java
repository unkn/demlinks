/**
 * File creation: Oct 19, 2009 11:39:43 PM
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


package org.dml.level2;



import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.level1.Level1_DMLEnvironment;
import org.dml.level1.Symbol;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;
import org.references.method.MethodParams;



/**
 * handling Vectors
 * 
 */
public class Level2_DMLEnvironment extends Level1_DMLEnvironment implements
		Level2_DMLStorageWrapper {
	
	@VarLevel
	private final Level2_DMLStorage_BerkeleyDB	storage	= null;
	
	@Override
	protected void start( MethodParams<Object> params ) {

		// this method is not needed, but it's here for clarity
		super.start( params );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#ensureGroup(org.dml.level1.NodeID
	 * , org.dml.level1.NodeID)
	 */
	@Override
	public boolean ensureVector( Symbol first, Symbol second )
			throws StorageException {

		RunTime.assumedNotNull( first, second );
		return storage.ensureVector( first, second );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level2.Level2_DMLStorageWrapper#isGroup(org.dml.level1.NodeID,
	 * org.dml.level1.NodeID)
	 */
	@Override
	public boolean isVector( Symbol first, Symbol second ) {

		RunTime.assumedNotNull( first, second );
		return storage.isVector( first, second );
	}
	
	@Override
	public boolean removeVector( Symbol first, Symbol second ) {

		RunTime.assumedNotNull( first, second );
		return storage.removeVector( first, second );
	}
	
	@Override
	public BDBVectorIterator<Symbol, Symbol> getIterator_on_Initials_of(
			Symbol terminalObject ) {

		RunTime.assumedNotNull( terminalObject );
		return storage.getIterator_on_Initials_of( terminalObject );
	}
	
	@Override
	public BDBVectorIterator<Symbol, Symbol> getIterator_on_Terminals_of(
			Symbol initialObject ) {

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
	public int countInitials( Symbol ofTerminalObject ) {

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
	public int countTerminals( Symbol ofInitialObject ) {

		RunTime.assumedNotNull( ofInitialObject );
		return storage.countTerminals( ofInitialObject );
	}
	
	/**
	 */
	public Symbol findCommonTerminalForInitials( Symbol initial1,
			Symbol initial2 ) {

		RunTime.assumedNotNull( initial1, initial2 );
		return storage.findCommonTerminalForInitials( initial1, initial2 );
	}
}
