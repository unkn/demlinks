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



package org.dml.database.bdb.level1;



import org.dml.level010.Symbol;
import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.references.method.MethodParams;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class UniqueSymbolsGenerator extends Initer {
	
	// it's null only once even if reInit() is called later
	private DBSequence			seq						= null;
	
	private final String		seq_UniqueSymbolsPuller	= "pulling unique Symbols";
	
	// increment-by value, when fetching new unique Symbols >0
	private final static int	SEQ_DELTA				= 1;
	
	Level1_Storage_BerkeleyDB	bdbL1;
	
	public UniqueSymbolsGenerator( Level1_Storage_BerkeleyDB bdbLevel1 ) {

		RunTime.assumedNotNull( bdbLevel1 );
		RunTime.assumedTrue( bdbLevel1.isInited() );
		bdbL1 = bdbLevel1;
	}
	
	/**
	 * @return
	 * @throws DatabaseException
	 */
	private final DBSequence getDBSeq() throws DatabaseException {

		if ( null == seq ) {
			// init once:
			seq = Factory.getNewInstanceAndInitWithoutMethodParams( DBSequence.class, bdbL1, seq_UniqueSymbolsPuller );
			// seq = new DBSequence( bdbL1, seq_UniqueSymbolsPuller );
			// seq.init( null );
			
		} else {
			Factory.reInitIfNotInited( seq );
			// if ( !seq.isInited() ) {
			// seq.reInit();
			// }
		}
		RunTime.assumedNotNull( seq );
		return seq;
	}
	
	/**
	 * @return a long that doesn't exist yet (and never will, even if
	 *         exceptions occur)
	 * @throws DatabaseException
	 */
	private long getUniqueLong() throws DatabaseException {

		return this.getDBSeq().getSequence().get( null, SEQ_DELTA );
	}
	
	public final Symbol getNewUniqueSymbol() throws DatabaseException {

		// this new Symbol is not saved anywhere in the database, but it's
		// ensured that it will not be created again, so it's unique even if you
		// don't save it in the database later
		long l = this.getUniqueLong();
		RunTime.assumedTrue( l < 4123123123l );// just one silly limit, it can go way much higher (forgot how much tho)
		Symbol sym = Bridge_SymbolAndBDB.newSymbolFrom( l );
		RunTime.assumedNotNull( sym );
		return sym;
	}
	
	@Override
	protected void done( MethodParams params ) {

		// close seq
		if ( null != seq ) {
			// seq = seq.done();
			Factory.deInit( seq );
			// don't null it
		}
	}
	
	@Override
	protected void start( MethodParams params ) {

		RunTime.assumedNull( params );
	}
}
