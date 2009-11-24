/**
 * File creation: Nov 20, 2009 5:29:32 AM
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
import org.references.Position;

import com.sleepycat.je.DatabaseException;



/**
 * a list where the order is maintained<br>
 * list of ElementCapsules<br>
 * 
 */
public class ListOrderedOfElementCapsules {
	
	Level3_DMLEnvironment	envL3;
	Symbol					name;
	
	public ListOrderedOfElementCapsules( Level3_DMLEnvironment l3_DMLEnv,
			Symbol name1 ) {

		RunTime.assumedNotNull( l3_DMLEnv, name1 );
		envL3 = l3_DMLEnv;
		
		name = name1;
		// Symbol listSymbol = l3DMLEnvironment.getSymbol(
		// Level3_DMLEnvironment.listSymbolJavaID );
		this.internal_setName();
	}
	
	/**
	 * override this and don't call super()
	 */
	protected void internal_setName() {

		envL3.ensureVector( envL3.listOrderedOfElementCapsules_Symbol, name );
	}
	
	/**
	 * override this and don't call super()
	 */
	protected boolean internal_hasNameSetRight() {

		return envL3.isVector( envL3.listOrderedOfElementCapsules_Symbol, name );
	}
	
	public boolean isValid() {

		// TODO more to add here
		return this.internal_hasNameSetRight();
	}
	
	/**
	 * @param last
	 * @param candidate
	 */
	private void internal_dmlRegisterNewHeadOrTail( Position pos,
			ElementCapsule candidate ) {

		RunTime.assumedNotNull( candidate, pos );
		RunTime.assumedTrue( candidate.isValidCapsule() );
		Symbol parent = null;
		switch ( pos ) {
		case FIRST:
			parent = envL3.allHeads_Symbol;
			break;
		case LAST:
			parent = envL3.allTails_Symbol;
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;
		}
		ElementCapsule oldCandidate = this.get_ElementCapsule( pos );
		// envL3.findCommonTerminalForInitials( parent,
		// oldCandidate.getAsSymbol() );
		if ( null != oldCandidate ) {
			envL3.removeVector( parent, oldCandidate.getAsSymbol() );
		}
		// new one
		envL3.ensureVector( parent, candidate.getAsSymbol() );
	}
	
	public void add_ElementCapsule( ElementCapsule theNew, Position pos,
			ElementCapsule posElement ) {

		RunTime.assumedNotNull( theNew, pos, posElement );
		RunTime.assumedTrue( posElement.isValidCapsule() );
		RunTime.assumedTrue( theNew.isValidCapsule() );
		RunTime.assumedTrue( theNew.isAlone() );
		switch ( pos ) {
		case BEFORE:
		case AFTER:
			// implies list has at least 1 element even if this is posElement
			// 1. we must be sure posElement is already part of this list
			RunTime.assumedTrue( this.hasElementCapsule( posElement ) );
			// RunTime.assumedFalse( posElement.isAlone() ); could be alone
			
			// assuming pos is AFTER
			// get oldnext
			ElementCapsule oldOne = posElement.getSideCapsule( pos );
			if ( null == oldOne ) {
				// then posElement is last
				RunTime.assumedTrue( this.get_ElementCapsule( pos ) == posElement );
			} else {
				// it's not the last
				// oldnext.prev=theNew
				oldOne.setCapsule( Position.opposite( pos ), theNew );
			}
			

			// oldOne can be null here, from above, depending on case; and is ok
			theNew.setCapsule( pos, oldOne );// new.next=oldnext
			
			posElement.setCapsule( pos, theNew );// next=theNew
			
			// new.prev=posElement
			theNew.setCapsule( Position.opposite( pos ), posElement );
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;
		}
		
	}
	
	/**
	 * @param posElement
	 * @return
	 */
	public boolean hasElementCapsule( ElementCapsule posElement ) {

		// TODO Auto-generated method stub
		return false;
	}
	
	public void add_ElementCapsule( ElementCapsule theNew, Position pos ) {

		switch ( pos ) {
		case FIRST:
		case LAST:
			RunTime.assumedNotNull( theNew );
			RunTime.assumedTrue( theNew.isValidCapsule() );
			RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
			
			// assuming pos is FIRST
			if ( this.isEmpty() ) {
				// no first/last
				// set as Tail
				this.internal_dmlRegisterNewHeadOrTail(
						Position.opposite( pos ), theNew );
			} else {// not empty list
				// has a last
				ElementCapsule theOld = this.get_ElementCapsule( pos );// first
				RunTime.assumedNotNull( theOld );// current first exists!
				
				// first.prev is null
				RunTime.assumedNull( theOld.getSideCapsule( pos ) );
				
				// redundant check:
				RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
				
				// newfirst.next=oldfirst
				theNew.setCapsule( Position.opposite( pos ), theOld );
				// oldfirst.prev=newfirst
				theOld.setCapsule( pos, theNew );
				// setFirst=newfirst
				
			}
			this.internal_dmlRegisterNewHeadOrTail( pos, theNew );// common
			break;
		
		default:
			RunTime.badCall( "bad position" );
			break;
		}
	}
	
	public int size() {

		BDBVectorIterator<Symbol, Symbol> iter = envL3.getIterator_on_Terminals_of( name );
		int ret;
		try {
			ret = iter.count();
		} catch ( DatabaseException e ) {
			throw new StorageException( e );
		} finally {
			iter.deInit();
		}
		return ret;
	}
	
	public boolean isEmpty() {

		RunTime.assumedTrue( this.get_ElementCapsule( Position.FIRST ) == null );
		RunTime.assumedTrue( this.get_ElementCapsule( Position.LAST ) == null );
		return this.size() == 0;
	}
	
	/**
	 * @param pos
	 *            FIRST or BEFORE / LAST or AFTER
	 * @return
	 */
	public ElementCapsule get_ElementCapsule( Position pos ) {

		RunTime.assumedNotNull( pos );
		Symbol sym = null;
		switch ( pos ) {
		case FIRST:
		case BEFORE:
			sym = envL3.allHeads_Symbol;
			break;
		case LAST:
		case AFTER:
			sym = envL3.allTails_Symbol;
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;// unreachable code
		}
		Symbol x = envL3.findCommonTerminalForInitials( sym, name );
		if ( null != x ) {// found one
			ElementCapsule ec = new ElementCapsule( envL3, x );
			return ec;
		}
		return null;// found none
	}
	
	public ElementCapsule get_ElementCapsule( Position pos, ElementCapsule posEC ) {

		RunTime.assumedNotNull( pos, posEC );
		switch ( pos ) {
		case BEFORE:
			break;
		case AFTER:
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;// unreachable code
		}
		// TODO
		return null;// found none
	}
}
