/**
 * File creation: Nov 25, 2009 12:42:28 AM
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


package org.dml.level3;



import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.level1.Symbol;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;

import com.sleepycat.je.DatabaseException;



/**
 * 
 */
public class Pointer {
	
	Level3_DMLEnvironment	envL3;
	Symbol					self;
	
	public Pointer( Level3_DMLEnvironment l3DML, Symbol selfName ) {

		RunTime.assumedNotNull( l3DML, selfName );
		RunTime.assumedTrue( l3DML.isInited() );
		envL3 = l3DML;
		self = selfName;
	}
	
	/**
	 * @param toWhat
	 *            new pointee
	 * @return the old pointee; even if toWhat is the same as old one;<br>
	 *         null only if there was no prev pointee
	 */
	public Symbol pointTo( Symbol toWhat ) {

		RunTime.assumedNotNull( toWhat );
		this.assumedValid();
		Symbol oldSym = this.getPointee();// null or it
		if ( !toWhat.equals( oldSym ) ) {
			// the new one is not the same as the old one
			// a diff pointee then we set new pointer to it, after removing old
			RunTime.assumedTrue( envL3.removeVector( self, oldSym ) );
			RunTime.assumedFalse( envL3.ensureVector( self, toWhat ) );
		}
		RunTime.assumedTrue( envL3.isVector( self, toWhat ) );
		this.assumedValid();
		return oldSym;
	}
	
	
	public Symbol getPointee() {

		Symbol ret = null;
		BDBVectorIterator<Symbol, Symbol> iter = envL3.getIterator_on_Terminals_of( self );
		try {
			try {
				if ( iter.count() > 0 ) {
					RunTime.assumedTrue( iter.count() == 1 );
					iter.goFirst();
					ret = iter.now();
				}
			} catch ( DatabaseException e ) {
				throw new StorageException( e );
			}
		} finally {
			iter.deInit();
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public void assumedValid() {

		// has 0 or 1 terminals
		int size = envL3.countTerminals( self );
		RunTime.assumedTrue( size <= 1 );
		RunTime.assumedTrue( size >= 0 );
		
		// getPointee works
		if ( 1 == size ) {
			RunTime.assumedNotNull( this.getPointee() );
		} else { // is 0
			RunTime.assumedNull( this.getPointee() );
		}
	}
}
