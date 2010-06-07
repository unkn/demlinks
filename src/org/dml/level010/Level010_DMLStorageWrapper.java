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



import org.dml.storagewrapper.StorageException;



/**
 * 
 *
 */
public interface Level010_DMLStorageWrapper {
	
	public void deInit();
	
	/**
	 * there's a one to one mapping between Symbol and JavaID<br>
	 * given the Symbol return its JavaID<br>
	 * 
	 * @param symbol
	 * @return JavaID or null if not found
	 * @throws StorageException
	 */
	public JavaID getJavaID( Symbol identifiedByThisSymbol ) throws StorageException;
	
	/**
	 * returns the Symbol associated with the given JavaID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisJavaID
	 * @return NodeID or null if not found;
	 * @throws StorageException
	 */
	public Symbol getSymbol( JavaID identifiedByThisJavaID ) throws StorageException;
	
	/**
	 * @param fromJavaID
	 *            must not be already associated with a NodeID (1to1 max) or
	 *            else throws
	 * @return the created Symbol or throws bug if fromJID already had a Symbol
	 * @throws StorageException
	 */
	public Symbol createSymbol( JavaID fromJavaID ) throws StorageException;
	
	/**
	 * eget=ensure get<br>
	 * make a new one if it doesn't exist<br>
	 * but if exists don't complain<br>
	 * 
	 * @param theJavaID
	 *            this JavaID and this Node will be mapped 1 to 1
	 * @return never null
	 * @throws StorageException
	 */
	public Symbol ensureSymbol( JavaID theJavaID ) throws StorageException;
	
	/**
	 * @return a new Symbol without an associated JavaID
	 * @throws StorageException
	 */
	public Symbol newUniqueSymbol() throws StorageException;
	
	/**
	 * between Symbol X and javaID Y can be only one link<br>
	 * it is a one to one mapping between the two
	 * 
	 * @param symbol
	 * @param javaID
	 */
	public void newLink( Symbol symbol, JavaID javaID ) throws StorageException;
	
	/**
	 * between Symbol X and javaID Y can be only one link<br>
	 * it is a one to one mapping between the two
	 * 
	 * @param symbol
	 * @param javaID
	 * @return true if existed; throws if link existed with different params
	 *         (ie. symbol and another javaID, or javaID and another symbol)
	 */
	public boolean ensureLink( Symbol symbol, JavaID javaID ) throws StorageException;
}
