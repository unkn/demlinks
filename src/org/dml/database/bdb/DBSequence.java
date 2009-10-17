/**
 * File creation: Jun 3, 2009 12:41:32 PM
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



import org.dml.tools.RunTime;
import org.javapart.logger.Log;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;



/**
 * it's a BerkeleyDB database that stores any and all Sequences<br>
 * 
 * we need a BerkeleyDB to store sequences for all other databases<br>
 * each sequence is gotten via a key, and it's only one key->data pair per
 * sequence
 * that is, a sequence used for another database will only have one key and
 * one data in this BerkeleyDB
 */
public class DBSequence {
	
	

	// FIXME: SequenceConfig will be kept the same for all Sequence -s, for
	// now;that is same settings not same variable
	private final SequenceConfig	allSequencesConfig	= new MySequenceConfig();
	


	private static final String		seqPrefix			= (char)0 + "_preseq_"
																+ (char)0;
	private static final String		seqSuffix			= (char)255
																+ "_postseq_"
																+ (char)255;
	
	// non-static follows:
	private Sequence				thisSeq				= null;
	private String					thisSeqName			= null;
	private final BerkeleyDB		bdb;
	
	
	/**
	 * private constructor, use getSeq() instead
	 * 
	 * @param seqName
	 */
	public DBSequence( BerkeleyDB bdb1, String seqName ) {

		RunTime.assertNotNull( bdb1 );
		RunTime.assertNotNull( seqName );
		RunTime.assertFalse( seqName.isEmpty() );
		bdb = bdb1;
		thisSeqName = seqPrefix + seqName + seqSuffix;
	}
	
	

	@Override
	protected void finalize() {

		this.silentCloseSeq();
	}
	
	

	/**
	 * @return null
	 */
	public DBSequence silentCloseSeq() {

		Log.entry( "attempting to close sequence: " + thisSeqName );
		if ( null != thisSeq ) {
			try {
				thisSeq.close();
				Log.exit( "closed seq with name: " + thisSeqName );
			} catch ( DatabaseException de ) {
				Log.thro( "failed closing seq with specified name: '"
						+ thisSeqName );
				// ignore
			} finally {
				thisSeq = null;
			}
		} else {
			Log.mid( "seq was already closed with name: " + thisSeqName );
		}
		
		return null;
	}
	
	
	/**
	 * @return never null
	 * @throws DatabaseException
	 */
	public Sequence getSequence() throws DatabaseException {

		if ( null == thisSeq ) {
			// init once:
			DatabaseEntry deKey = new DatabaseEntry();
			BerkeleyDB.stringToEntry( thisSeqName, deKey );
			thisSeq = bdb.getSeqsDB().openSequence( null, deKey,
					allSequencesConfig );
			RunTime.assertNotNull( thisSeq );
		}
		return thisSeq;
	}
	
}
