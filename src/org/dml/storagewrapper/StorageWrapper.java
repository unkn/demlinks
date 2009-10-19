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


package org.dml.storagewrapper;



import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;



/**
 * 
 *
 */
public interface StorageWrapper {
	
	/**
	 * returns the NodeJID associated with the given NodeID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisNodeID
	 * @return NodeJID
	 * @throws StorageException
	 */
	public NodeJID getNodeJID( NodeID identifiedByThisNodeID )
			throws StorageException;
	
	/**
	 * returns the NodeID associated with the given NodeJID<br>
	 * it's a 1 to 1 mapping<br>
	 * 
	 * @param identifiedByThisJID
	 * @return NodeID
	 * @throws StorageException
	 */
	public NodeID getNodeID( NodeJID identifiedByThisJID )
			throws StorageException;
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID createNodeID( NodeJID fromJID ) throws StorageException;
	
	/**
	 * @param theJID
	 * @return
	 * @throws StorageException
	 */
	public NodeID ensureNodeID( NodeJID theJID ) throws StorageException;
	
	/**
	 * @param first
	 * @param second
	 * @return
	 * @throws StorageException
	 */
	public boolean ensureGroup( NodeID first, NodeID second )
			throws StorageException;
	
	/**
	 * 
	 */
	public void deInit();
}
