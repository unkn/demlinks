/**
 * File creation: Jun 3, 2009 12:32:27 PM
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


package org.dml.database.bdb;



import org.dml.error.BugError;
import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;



/**
 *this adds a Sequence for NodeID generation (ie. get a new unique NodeID)<br>
 *and the methods that use NodeID and NodeJID objects<br>
 *lookup by either NodeJID or NodeID<br>
 */
public class DBMapJIDsToNodeIDs extends OneToOneDBMap {
	
	private DBSequence			seq			= null;
	private String				seq_KEYNAME	= null;
	private final static int	SEQ_DELTA	= 1;
	
	/**
	 * @param dbName1
	 * @throws DatabaseException
	 */
	public DBMapJIDsToNodeIDs( Level2_BerkeleyDB bdb1, String dbName1 )
			throws DatabaseException {

		super( bdb1, dbName1 );
		seq_KEYNAME = dbName1;
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private final DBSequence getDBSeq() throws DatabaseException {

		if ( null == seq ) {
			// init once:
			seq = new DBSequence( bdb, seq_KEYNAME );
			RunTime.assertNotNull( seq );
		}
		return seq;
	}
	
	@Override
	public OneToOneDBMap silentClose() {

		Log.entry( "closing " + this.getClass().getSimpleName()
				+ " with name: " + dbName );
		
		// close seq
		if ( null != seq ) {
			seq = seq.done();
		}
		
		// close DBs
		return super.silentClose();
	}
	
	/**
	 * @return a NodeID that doesn't exist yet (and never will, even if
	 *         exceptions occur)
	 * @throws DatabaseException
	 */
	private long getUniqueNodeID() throws DatabaseException {

		return this.getDBSeq().getSequence().get( null, SEQ_DELTA );
	}
	
	/**
	 * the NodeID must already exist else null is returned<br>
	 * this doesn't create a new NodeID for the supplied JID<br>
	 * remember there's a one to one mapping between a JID and a NodeID
	 * 
	 * @param theJID
	 * @return null if not found;
	 * @throws DatabaseException
	 */
	public NodeID getNodeID( NodeJID fromJID ) throws DatabaseException {

		RunTime.assertNotNull( fromJID );
		return this.internal_getNodeIDFromJID( fromJID );
	}
	
	

	/**
	 * @param fromJID
	 * @return
	 * @throws DatabaseException
	 * @throws BugError
	 */
	public NodeID createNodeID( NodeJID fromJID ) throws DatabaseException {

		if ( null != this.internal_getNodeIDFromJID( fromJID ) ) {
			// already exists
			RunTime.bug( "bad programming" );// throws
		}
		// doesn't exist, make it:
		return this.internal_makeNewNodeID( fromJID );
	}
	
	/**
	 * the fromJID must not already be mapped to another NodeID before calling
	 * this method!
	 * 
	 * @param fromJID
	 * @return the new NodeID mapped to fromJID<br>
	 *         never null
	 * @throws DatabaseException
	 */
	private final NodeID internal_makeNewNodeID( NodeJID fromJID )
			throws DatabaseException {

		RunTime.assertNotNull( fromJID );
		NodeID nid = new NodeID( this.getUniqueNodeID() );
		if ( OperationStatus.SUCCESS != this.internal_Link( fromJID, nid ) ) {
			RunTime.bug( "should've succeeded, maybe JID already existed?" );
		}
		RunTime.assertNotNull( nid );
		return nid;
	}
	
	/**
	 * @param thisJID
	 * @param withThisNID
	 * @return
	 * @throws DatabaseException
	 */
	private OperationStatus internal_Link( NodeJID thisJID, NodeID withThisNID )
			throws DatabaseException {

		RunTime.assertNotNull( thisJID, withThisNID );
		return this.link( thisJID.getAsString(), withThisNID.getAsString() );
	}
	
	/**
	 * get or create and get, a NodeID from the given JID
	 * 
	 * @param fromJID
	 * @return
	 * @throws DatabaseException
	 */
	public NodeID ensureNodeID( NodeJID fromJID ) throws DatabaseException {

		NodeID nid = this.internal_getNodeIDFromJID( fromJID );
		if ( null == nid ) {
			// no NodeID for JID yet, make new one
			nid = this.internal_makeNewNodeID( fromJID );
		}
		RunTime.assertNotNull( nid );// this is stupid
		return nid;
	}
	
	/**
	 * @param fromJID
	 *            the JID identifying the returned NodeID
	 * @return null if not found; or the NodeID as NodeID object if found
	 * @throws DatabaseException
	 */
	private NodeID internal_getNodeIDFromJID( NodeJID fromJID )
			throws DatabaseException {

		RunTime.assertNotNull( fromJID );
		String nidAsStr = this.getData( fromJID.getAsString() );
		if ( null == nidAsStr ) {
			return null;
		}
		NodeID nid = new NodeID( nidAsStr );
		return nid;
	}
	
	/**
	 * @param fromNodeID
	 * @return null if not found
	 * @throws DatabaseException
	 */
	/**
	 * @param nodeID
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public NodeJID getNodeJID( NodeID fromNodeID ) throws DatabaseException {

		RunTime.assertNotNull( fromNodeID );
		String jidAsStr = this.getKey( fromNodeID.getAsString() );
		if ( null == jidAsStr ) {
			return null;
		}
		NodeJID jid = NodeJID.ensureJIDFor( jidAsStr );
		RunTime.assertNotNull( jid );
		return jid;
	}
	
	/**
	 * @return null
	 */
	public DBMapJIDsToNodeIDs deInit() {

		this.silentClose();
		return null;
	}
	

}
