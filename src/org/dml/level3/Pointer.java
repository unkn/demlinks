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
import org.dml.level2.Level2_DMLEnvironment;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;

import com.sleepycat.je.DatabaseException;



/**
 * 
 */
public class Pointer {
	
	private static final TwoKeyHashMap<Level2_DMLEnvironment, Symbol, Pointer>	allPointerInstances	= new TwoKeyHashMap<Level2_DMLEnvironment, Symbol, Pointer>();
	protected final Level2_DMLEnvironment										envL2;
	protected final Symbol														self;
	private boolean																allowNull			= true;
	
	/**
	 * constructor, not to be used by user
	 * 
	 * @param l2DML
	 * @param self1
	 */
	protected Pointer( Level2_DMLEnvironment l2DML, Symbol self1 ) {

		RunTime.assumedNotNull( l2DML, self1 );
		RunTime.assumedTrue( l2DML.isInited() );
		envL2 = l2DML;
		self = self1;
	}
	
	public static Pointer getNewNullPointer( Level2_DMLEnvironment l2DML ) {

		RunTime.assumedNotNull( l2DML );
		Symbol name = l2DML.newUniqueSymbol();
		Pointer ret = new Pointer( l2DML, name );
		ret.setAllowNull( true );
		ret.assumedValid();
		RunTime.assumedFalse( allPointerInstances.ensure( l2DML, name, ret ) );
		return ret;
	}
	
	public static Pointer getNewNonNullPointer( Level2_DMLEnvironment l2DML,
			Symbol pointTo ) {

		RunTime.assumedNotNull( l2DML, pointTo );
		Symbol name = l2DML.newUniqueSymbol();
		Pointer ret = new Pointer( l2DML, name );
		ret.pointTo( pointTo );
		ret.setAllowNull( false );
		ret.assumedValid();
		RunTime.assumedFalse( allPointerInstances.ensure( l2DML, name, ret ) );
		return ret;
	}
	
	public static Pointer getExistingPointer( Level2_DMLEnvironment l2DML,
			Symbol name, boolean allowNull ) {

		RunTime.assumedNotNull( l2DML, name, allowNull );
		Pointer existingOne = allPointerInstances.get( l2DML, name );
		if ( null != existingOne ) {
			return existingOne;
		}
		// else make it
		Pointer ret = new Pointer( l2DML, name );
		// if false, it must already point to something
		ret.setAllowNull( allowNull );
		ret.assumedValid();
		RunTime.assumedFalse( allPointerInstances.ensure( l2DML, name, ret ) );
		return ret;
	}
	
	public boolean setAllowNull( boolean newValue ) {

		RunTime.assumedNotNull( newValue );
		boolean old = allowNull;
		allowNull = newValue;
		this.assumedValid();
		return old;
	}
	
	/**
	 * @param toWhat
	 *            new pointee
	 * @return the old pointee; even if toWhat is the same as old one;<br>
	 *         null only if there was no prev pointee
	 */
	public Symbol pointTo( Symbol toWhat ) {

		if ( !allowNull ) {
			RunTime.assumedNotNull( toWhat );
		}
		this.assumedValid();
		Symbol oldSym = this.getPointee();// null or it
		if ( null != oldSym ) {
			RunTime.assumedTrue( envL2.removeVector( self, oldSym ) );
			RunTime.assumedFalse( envL2.isVector( self, oldSym ) );
		}
		
		if ( null != toWhat ) {
			// the new one is not the same as the old one
			// a diff pointee then we set new pointer to it, after removing old
			RunTime.assumedFalse( envL2.ensureVector( self, toWhat ) );
			RunTime.assumedTrue( envL2.isVector( self, toWhat ) );
		}
		this.assumedValid();
		return oldSym;
	}
	
	
	public Symbol getPointee() {

		this.assumedValid();
		return this.internal_getPointee();
	}
	
	protected Symbol internal_getPointee() {

		Symbol ret = null;
		BDBVectorIterator<Symbol, Symbol> iter = envL2.getIterator_on_Terminals_of( self );
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

		// watch out for recursion
		RunTime.assumedNotNull( self );
		int size = envL2.countTerminals( self );
		if ( !allowNull ) {
			// must have 1 terminal
			RunTime.assumedTrue( 1 == size );
		} else {
			// has 0 or 1 terminals
			RunTime.assumedTrue( size <= 1 );
			RunTime.assumedTrue( size >= 0 );
		}
		
		// getPointee works
		if ( 1 == size ) {
			RunTime.assumedNotNull( this.internal_getPointee() );
		} else { // is 0
			RunTime.assumedNull( this.internal_getPointee() );
		}
	}
	
	/**
	 * @return
	 */
	public Symbol getAsSymbol() {

		this.assumedValid();
		return self;
	}
}
