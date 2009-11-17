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


package org.dml.database.bdb.level1;



import org.dml.error.BugError;
import org.dml.level1.Symbol;
import org.dml.level1.SymbolJavaID;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;



/**
 *this adds a Sequence for NodeID generation (ie. get a new unique NodeID)<br>
 *and the methods that use NodeID and NodeJavaID objects<br>
 *lookup by either NodeJavaID or NodeID<br>
 */
public class DBMap_JavaIDs_To_NodeIDs extends
		OneToOneDBMap<SymbolJavaID, Symbol> {
	
	private DBSequence			seq			= null;
	private String				seq_KEYNAME	= null;
	private final static int	SEQ_DELTA	= 1;
	
	/**
	 * @param dbName1
	 * @throws DatabaseException
	 */
	public DBMap_JavaIDs_To_NodeIDs( Level1_Storage_BerkeleyDB bdb1,
			String dbName1 ) throws DatabaseException {

		super( bdb1, dbName1, SymbolJavaID.class, Symbol.class );
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
	public OneToOneDBMap<SymbolJavaID, Symbol> silentClose() {

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
	 * @return a long that doesn't exist yet (and never will, even if
	 *         exceptions occur)
	 * @throws DatabaseException
	 */
	private long getUniqueLong() throws DatabaseException {

		return this.getDBSeq().getSequence().get( null, SEQ_DELTA );
	}
	
	/**
	 * the NodeID must already exist else null is returned<br>
	 * this doesn't create a new NodeID for the supplied JavaID<br>
	 * remember there's a one to one mapping between a JavaID and a NodeID
	 * 
	 * @param fromJavaID
	 * @return null if not found;
	 * @throws DatabaseException
	 */
	public Symbol getSymbol( SymbolJavaID fromJavaID ) throws DatabaseException {

		RunTime.assertNotNull( fromJavaID );
		return this.internal_getSymbolFromJavaID( fromJavaID );
	}
	
	

	/**
	 * @param fromJavaID
	 * @return
	 * @throws DatabaseException
	 * @throws BugError
	 */
	public Symbol createSymbol( SymbolJavaID fromJavaID )
			throws DatabaseException {

		if ( null != this.internal_getSymbolFromJavaID( fromJavaID ) ) {
			// already exists
			RunTime.bug( "bad programming, the JavaID is already associated with one NodeID !" );// throws
		}
		// doesn't exist, make it:
		return this.internal_makeNewSymbol( fromJavaID );
	}
	
	/**
	 * the fromJavaID must not already be mapped to another NodeID before
	 * calling
	 * this method!
	 * 
	 * @param fromJavaID
	 * @return the new NodeID mapped to fromJavaID<br>
	 *         never null
	 * @throws DatabaseException
	 */
	private final Symbol internal_makeNewSymbol( SymbolJavaID fromJavaID )
			throws DatabaseException {

		RunTime.assertNotNull( fromJavaID );
		Symbol nid = new Symbol( this.getUniqueLong() );
		if ( OperationStatus.SUCCESS != this.makeVector( fromJavaID, nid ) ) {
			RunTime.bug( "should've succeeded, maybe fromJavaID already existed?" );
		}
		RunTime.assertNotNull( nid );
		return nid;
	}
	
	/**
	 * get or create and get, a NodeID from the given JavaID
	 * 
	 * @param fromJavaID
	 * @return
	 * @throws DatabaseException
	 */
	public Symbol ensureSymbol( SymbolJavaID fromJavaID )
			throws DatabaseException {

		Symbol nid = this.internal_getSymbolFromJavaID( fromJavaID );
		if ( null == nid ) {
			// no NodeID for JavaID yet, make new one
			nid = this.internal_makeNewSymbol( fromJavaID );
		}
		RunTime.assertNotNull( nid );// this is stupid
		return nid;
	}
	
	/**
	 * @param fromJavaID
	 *            the JavaID identifying the returned NodeID
	 * @return null if not found; or the NodeID as NodeID object if found
	 * @throws DatabaseException
	 */
	private Symbol internal_getSymbolFromJavaID( SymbolJavaID fromJavaID )
			throws DatabaseException {

		RunTime.assertNotNull( fromJavaID );
		// String nidAsStr =
		Symbol nid = this.getData( fromJavaID );
		// if ( null == nidAsStr ) {
		// return null;
		// }
		// NodeID nid = new NodeID( nidAsStr );
		// RunTime.assertNotNull( nid );
		return nid;
	}
	
	/**
	 * @param fromSymbol
	 * @return null if not found
	 * @throws DatabaseException
	 */
	public SymbolJavaID getSymbolJavaID( Symbol fromSymbol )
			throws DatabaseException {

		RunTime.assertNotNull( fromSymbol );
		SymbolJavaID jid = this.getKey( fromSymbol );
		// RunTime.assertNotNull( jid );
		return jid;
	}
	
	/**
	 * @return null
	 */
	public DBMap_JavaIDs_To_NodeIDs deInit() {

		this.silentClose();
		return null;
	}
	

}
