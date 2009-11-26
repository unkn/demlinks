/**
 * File creation: Oct 16, 2009 3:26:32 PM
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
import org.dml.level1.Level1_DMLStorageWrapper;
import org.dml.level1.Symbol;
import org.dml.storagewrapper.StorageException;



/**
 * 
 *
 */
public interface Level2_DMLStorageWrapper extends Level1_DMLStorageWrapper {
	
	/**
	 * @param first
	 * @param second
	 * @return
	 * @throws StorageException
	 */
	public boolean ensureVector( Symbol first, Symbol second )
			throws StorageException;
	
	
	public boolean isVector( Symbol first, Symbol second );
	
	public BDBVectorIterator<Symbol, Symbol> getIterator_on_Initials_of(
			Symbol terminalObject );
	
	/**
	 * @param initialObject
	 * @return already inited iterator, use deInit() when done
	 */
	public BDBVectorIterator<Symbol, Symbol> getIterator_on_Terminals_of(
			Symbol initialObject );
	
	public int countInitials( Symbol ofTerminalObject );
	
	public int countTerminals( Symbol ofInitialObject );
	
	public Symbol findCommonTerminalForInitials( Symbol initial1,
			Symbol initial2 );
	
	
	/**
	 * @param initial
	 * @param terminal
	 * @return true if existed
	 */
	boolean removeVector( Symbol initial, Symbol terminal );
}
