/**
 * File creation: May 30, 2009 8:20:44 PM
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



import org.dml.level1.NodeJID;
import org.dml.storagewrapper.Storage;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;



/**
 * is stored in Storage<br>
 * 
 */
public class NodeID {
	
	
	private final long	itself;
	
	/**
	 * @param fromJID
	 * @return
	 * @throws StorageException
	 */
	public static NodeID createNode( NodeJID fromJID ) throws StorageException {

		return Storage.createNodeID( fromJID );
	}
	
	/**
	 * @param identifiedByThisJID
	 * @return
	 * @throws StorageException
	 */
	public static NodeID getNode( NodeJID identifiedByThisJID )
			throws StorageException {

		return Storage.getNodeID( identifiedByThisJID );
	}
	
	/**
	 * eget=ensure get<br>
	 * make a new one if it doesn't exist<br>
	 * but if exists don't complain<br>
	 * 
	 * @param theJID
	 *            this JID and this Node will be mapped 1 to 1
	 * @return never null
	 * @throws StorageException
	 */
	public static NodeID ensureNode( NodeJID theJID ) throws StorageException {

		RunTime.assertNotNull( theJID );
		return Storage.ensureNodeID( theJID );
	}
	
	/**
	 * constructor, call only internally
	 * 
	 * @param iD
	 */
	public NodeID( long iD ) {

		itself = iD;
	}
	
	/**
	 * constructor, call only internally (maybe also within the package)<br>
	 * must be a long inside that string
	 * 
	 * @param iD
	 *            a long expressed as a string
	 * @throws NumberFormatException
	 */
	public NodeID( String iD ) {

		itself = Long.parseLong( iD );
		// itself = Long.valueOf( iD );
	}
	
	/**
	 * @return the string representation of this NodeID, usually long to string
	 *         transformation
	 */
	public String getAsString() {

		return String.valueOf( itself );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return this.getClass().getSimpleName() + ":" + this.getAsString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( NodeID nid ) {

		RunTime.assertNotNull( nid );
		if ( !super.equals( nid ) ) {
			if ( this.getAsString().equals( nid.getAsString() ) ) {
				return true;
			}
		}
		return false;
	}
}
