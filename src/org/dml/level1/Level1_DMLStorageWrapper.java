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


package org.dml.level1;



import org.dml.storagewrapper.StorageException;



/**
 * 
 *
 */
public interface Level1_DMLStorageWrapper {
	
	/**
	 * returns the NodeJavaID associated with the given NodeID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisSymbol
	 * @return NodeJavaID or null if not found
	 * @throws StorageException
	 */
	public JavaID getJavaID( Symbol identifiedByThisSymbol )
			throws StorageException;
	
	/**
	 * returns the NodeID associated with the given NodeJavaID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisJavaID
	 * @return NodeID or null if not found;
	 * @throws StorageException
	 */
	public Symbol getSymbol( JavaID identifiedByThisJavaID )
			throws StorageException;
	
	/**
	 * @param fromJavaID
	 *            must not be already associated with a NodeID (1to1 max) or
	 *            else throws
	 * @return the created NodeID or throws bug if fromJID already had a NodeID
	 * @throws StorageException
	 */
	public Symbol createSymbol( JavaID fromJavaID ) throws StorageException;
	
	/**
	 * @param theJavaID
	 * @return the new or existing NodeID
	 * @throws StorageException
	 */
	public Symbol ensureSymbol( JavaID theJavaID ) throws StorageException;
	
	public Symbol newUniqueSymbol() throws StorageException;
}
