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



package org.dml.level030;



import org.dml.level010.Symbol;
import org.dml.level020.Level020_DMLEnvironment;
import org.dml.level025.SetOfTerminalSymbols;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.references.Position;



/**
 * seen as such only in Java<br>
 */
public class Pointer {
	
	private static final TwoKeyHashMap<Level030_DMLEnvironment, SetOfTerminalSymbols, Pointer>	allPointerInstances	= new TwoKeyHashMap<Level030_DMLEnvironment, SetOfTerminalSymbols, Pointer>();
	protected final Level020_DMLEnvironment														envL2;
	protected final SetOfTerminalSymbols														selfAsSet;
	protected boolean																			allowNull			= true;
	
	/**
	 * constructor, not to be used by user
	 * 
	 * @param l2DML
	 * @param passedSelf
	 */
	protected Pointer( Level030_DMLEnvironment l2DML, SetOfTerminalSymbols passedSelf ) {

		RunTime.assumedNotNull( l2DML, passedSelf );
		RunTime.assumedTrue( l2DML.isInited() );
		envL2 = l2DML;
		selfAsSet = passedSelf;
	}
	
	public static Pointer getNewNullPointer( Level030_DMLEnvironment passedEnv ) {

		RunTime.assumedNotNull( passedEnv );
		Symbol newSymbol = passedEnv.newUniqueSymbol();
		SetOfTerminalSymbols nameSet = passedEnv.getAsSet( newSymbol );
		Pointer ret = new Pointer( passedEnv, nameSet );
		ret.setAllowNull( true );
		ret.assumedValid();
		registerPointerInstance( passedEnv, nameSet, ret );
		return ret;
	}
	
	public static Pointer getNewNonNullPointer( Level030_DMLEnvironment passedEnv, Symbol pointTo ) {

		RunTime.assumedNotNull( passedEnv, pointTo );
		Symbol newSymbol = passedEnv.newUniqueSymbol();
		SetOfTerminalSymbols nameSet = passedEnv.getAsSet( newSymbol );
		Pointer ret = new Pointer( passedEnv, nameSet );
		ret.pointTo( pointTo );
		ret.setAllowNull( false );
		ret.assumedValid();
		registerPointerInstance( passedEnv, nameSet, ret );
		return ret;
	}
	
	public static Pointer getExistingPointer( Level030_DMLEnvironment passedEnv, Symbol passedSelf,
			boolean passedAllowNull ) {

		RunTime.assumedNotNull( passedEnv, passedSelf, passedAllowNull );
		
		SetOfTerminalSymbols nameSet = passedEnv.getAsSet( passedSelf );
		Pointer existingOne = getPointerInstance( passedEnv, nameSet );
		if ( null != existingOne ) {
			if ( existingOne.allowNull != passedAllowNull ) {
				RunTime.badCall( "already existing DP had different AllowNull setting" );
			}
			existingOne.assumedValid();
			return existingOne;
		}
		// else make it
		Pointer ret = new Pointer( passedEnv, nameSet );
		// if false, it must already point to something
		ret.setAllowNull( passedAllowNull );
		ret.assumedValid();
		// RunTime.assumedFalse( allPointerInstances.ensure( l2DML, name, ret )
		// );
		registerPointerInstance( passedEnv, nameSet, ret );
		return ret;
	}
	
	private final static void registerPointerInstance( Level030_DMLEnvironment env, SetOfTerminalSymbols name,
			Pointer newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allPointerInstances.ensure( env, name, newOne ) );
	}
	
	private final static Pointer getPointerInstance( Level030_DMLEnvironment env, SetOfTerminalSymbols name ) {

		RunTime.assumedNotNull( env, name );
		return allPointerInstances.get( env, name );
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
			RunTime.assumedTrue( selfAsSet.remove( oldSym ) );
			RunTime.assumedFalse( selfAsSet.hasSymbol( oldSym ) );
			// RunTime.assumedTrue( envL2.removeVector( selfAsSet, oldSym ) );
			// RunTime.assumedFalse( envL2.isVector( selfAsSet, oldSym ) );
		}
		
		if ( null != toWhat ) {
			// the new one is not the same as the old one
			// a diff pointee then we set new pointer to it, after removing old
			RunTime.assumedFalse( selfAsSet.addToSet( toWhat ) );
			RunTime.assumedTrue( selfAsSet.hasSymbol( toWhat ) );
		}
		this.assumedValid();
		return oldSym;
	}
	
	
	public Symbol getPointee() {

		this.assumedValid();
		return this.internal_getPointee();
	}
	
	protected Symbol internal_getPointee() {

		// Symbol ret = null;
		RunTime.assumedTrue( selfAsSet.size() <= 1 );
		return selfAsSet.getSide( Position.FIRST );
		// BDBVectorIterator<Symbol, Symbol> iter =
		// envL2.getIterator_on_Terminals_of( selfAsSet );
		// try {
		// try {
		// if ( iter.count() > 0 ) {
		// RunTime.assumedTrue( iter.count() == 1 );
		// iter.goFirst();
		// ret = iter.now();
		// }
		// } catch ( DatabaseException e ) {
		// throw new StorageException( e );
		// }
		// } finally {
		// iter.deInit();
		// }
		// return ret;
	}
	
	/**
	 * 
	 */
	public void assumedValid() {

		// watch out for recursion
		RunTime.assumedNotNull( selfAsSet );
		selfAsSet.assumedValid();
		
		int size = envL2.countTerminals( selfAsSet.getAsSymbol() );
		RunTime.assumedTrue( selfAsSet.size() == size );
		
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
		return selfAsSet.getAsSymbol();
	}
}
