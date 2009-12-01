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


package org.dml.level4;



import org.dml.level1.Symbol;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.references.Position;



/**
 * a list where the order is maintained<br>
 * list of ElementCapsules<br>
 * will yield the same list instance for the same env/symbol tuple
 */
public class ListOrderedOfElementCapsules {
	
	private static final TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfElementCapsules>	allListOOECInstances	= new TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfElementCapsules>();
	Level4_DMLEnvironment																			env;
	Symbol																							name;
	
	/**
	 * don't explicitly use this constructor
	 * 
	 * @param envDML
	 * @param name1
	 */
	protected ListOrderedOfElementCapsules( Level4_DMLEnvironment envDML,
			Symbol name1 ) {

		RunTime.assumedNotNull( envDML, name1 );
		RunTime.assumedTrue( envDML.isInited() );
		env = envDML;
		name = name1;
		// Symbol listSymbol = l3DMLEnvironment.getSymbol(
		// Level3_DMLEnvironment.listSymbolJavaID );
		this.internal_setName();
		this.assumedValid();
	}
	
	private final static void registerInstance( Level4_DMLEnvironment env,
			Symbol name, ListOrderedOfElementCapsules newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allListOOECInstances.ensure( env, name, newOne ) );
	}
	
	private final static ListOrderedOfElementCapsules getInstance(
			Level4_DMLEnvironment env, Symbol name ) {

		RunTime.assumedNotNull( env, name );
		return allListOOECInstances.get( env, name );
	}
	
	/**
	 * @param envL4
	 * @param existingSymbol
	 *            can be a list already, or just a new unique symbol to be
	 *            transformed into a list
	 * @return
	 */
	public static ListOrderedOfElementCapsules getListOOEC(
			Level4_DMLEnvironment envL4, Symbol existingSymbol ) {

		RunTime.assumedNotNull( envL4, existingSymbol );
		ListOrderedOfElementCapsules existingOne = getInstance( envL4,
				existingSymbol );
		if ( null == existingOne ) {
			existingOne = new ListOrderedOfElementCapsules( envL4,
					existingSymbol );
			registerInstance( envL4, existingSymbol, existingOne );
		}
		existingOne.assumedValid();
		return existingOne;
	}
	
	/**
	 * override this and don't call super()
	 */
	protected void internal_setName() {

		env.ensureVector( env.listOrderedOfElementCapsules_Symbol, name );
	}
	
	/**
	 * override this and don't call super()
	 */
	protected boolean internal_hasNameSetRight() {

		return env.isVector( env.listOrderedOfElementCapsules_Symbol, name );
	}
	
	public void assumedValid() {

		// TODO more to add here
		RunTime.assumedTrue( this.internal_hasNameSetRight() );
		ElementCapsule first = this.get_ElementCapsule( Position.FIRST );
		ElementCapsule last = this.get_ElementCapsule( Position.LAST );
		if ( null != first ) {
			RunTime.assumedTrue( env.isVector( name, first.getAsSymbol() ) );
			RunTime.assumedTrue( this.hasElementCapsule( first ) );
			RunTime.assumedTrue( last != null );
		} else {
			RunTime.assumedTrue( this.isEmpty() );
		}
		if ( null != last ) {
			RunTime.assumedTrue( env.isVector( name, last.getAsSymbol() ) );
			RunTime.assumedTrue( this.hasElementCapsule( last ) );
			RunTime.assumedTrue( first != null );
		}
		
		// TODO maybe check that all terminals of 'name' are ECs
	}
	
	/**
	 * replaces old HEAD/TAIL with the new one
	 * 
	 * @param last
	 * @param candidate
	 */
	private void internal_dmlRegisterNewHeadOrTail( Position pos,
			ElementCapsule candidate ) {

		RunTime.assumedNotNull( candidate, pos );
		candidate.assumedIsValidCapsule();
		Symbol parent = null;
		switch ( pos ) {
		case FIRST:
			parent = env.allHeads_Symbol;
			break;
		case LAST:
			parent = env.allTails_Symbol;
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;
		}
		ElementCapsule oldCandidate = this.get_ElementCapsule( pos );
		// envL3.findCommonTerminalForInitials( parent,
		// oldCandidate.getAsSymbol() );
		if ( null != oldCandidate ) {
			if ( !env.removeVector( parent, oldCandidate.getAsSymbol() ) ) {
				RunTime.bug( "get_ElementCapsule(pos) must be bugged then" );
			}
		}
		// new one
		RunTime.assumedFalse( env.ensureVector( parent, candidate.getAsSymbol() ) );
		this.assumedValid();
	}
	
	/**
	 * @param theNew
	 * @param pos
	 * @param posElement
	 */
	public void add_ElementCapsule( ElementCapsule theNew, Position pos,
			ElementCapsule posElement ) {

		RunTime.assumedNotNull( theNew, pos, posElement );
		posElement.assumedIsValidCapsule();
		theNew.assumedIsValidCapsule();
		RunTime.assumedTrue( theNew.isAlone() );
		RunTime.assumedFalse( this.hasElementCapsule( theNew ) );
		switch ( pos ) {
		case BEFORE:
		case AFTER:
			// implies list has at least 1 element even if this is posElement
			// 1. we must be sure posElement is already part of this list
			RunTime.assumedTrue( this.hasElementCapsule( posElement ) );
			// RunTime.assumedFalse( posElement.isAlone() ); could be alone
			
			// list always points to all ECs
			RunTime.assumedFalse( env.ensureVector( name, theNew.getAsSymbol() ) );
			// assuming pos is AFTER, for comments only
			// get oldnext
			ElementCapsule oldOne = posElement.getSideCapsule( pos );
			if ( null == oldOne ) {
				// then posElement is last
				RunTime.assumedTrue( this.get_ElementCapsule( Position.getAsEdge( pos ) ) == posElement );
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
			if ( theNew.getSideCapsule( pos ) == null ) {
				// no prev, then make it FIRST
				this.internal_dmlRegisterNewHeadOrTail(
						Position.getAsEdge( pos ), theNew );
			}
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

		RunTime.assumedNotNull( posElement );
		return env.isVector( name, posElement.getAsSymbol() );
	}
	
	/**
	 * @param pos
	 *            FIRST or LAST only
	 * @param theNew
	 */
	public void add_ElementCapsule( Position pos, ElementCapsule theNew ) {

		switch ( pos ) {
		case FIRST:
		case LAST:
			RunTime.assumedNotNull( theNew );
			theNew.assumedIsValidCapsule();
			RunTime.assumedFalse( this.hasElementCapsule( theNew ) );
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
				RunTime.assumedNull( theOld.getSideCapsule( Position.getAsNear( pos ) ) );
				
				// redundant check:
				RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
				
				// newfirst.next=oldfirst
				theNew.setCapsule(
						Position.opposite( Position.getAsNear( pos ) ), theOld );
				// oldfirst.prev=newfirst
				theOld.setCapsule( Position.getAsNear( pos ), theNew );
				// setFirst=newfirst
				theNew.assumedIsValidCapsule();
			}
			// new HEAD
			this.internal_dmlRegisterNewHeadOrTail( pos, theNew );// common
			// list points to all ECs it has
			if ( env.ensureVector( name, theNew.getAsSymbol() ) ) {
				RunTime.bug( "the link shouldn't already exist" );
			}
			break;
		
		default:
			RunTime.badCall( "bad position" );
			break;
		}
	}
	
	public int size() {

		return env.countTerminals( name );
	}
	
	public boolean isEmpty() {

		RunTime.assumedTrue( this.get_ElementCapsule( Position.FIRST ) == null );
		RunTime.assumedTrue( this.get_ElementCapsule( Position.LAST ) == null );
		return this.size() == 0;
	}
	
	/**
	 * @param pos
	 *            FIRST /LAST
	 * @return
	 */
	public ElementCapsule get_ElementCapsule( Position pos ) {

		RunTime.assumedNotNull( pos );
		Symbol sym = null;
		switch ( pos ) {
		case FIRST:
			sym = env.allHeads_Symbol;
			break;
		case LAST:
			sym = env.allTails_Symbol;
			break;
		default:
			RunTime.badCall( "bad position for this method" );
			break;// unreachable code
		}
		Symbol x = env.findCommonTerminalForInitials( sym, name );
		if ( null != x ) {// found one
			// existing ElementCapsule, wrap it in ElementCapsule type
			ElementCapsule ec = ElementCapsule.getElementCapsule( env, x );
			return ec;
		}
		return null;// found none
	}
	
	public ElementCapsule get_ElementCapsule( Position pos, ElementCapsule posEC ) {

		RunTime.assumedNotNull( pos, posEC );
		switch ( pos ) {
		case BEFORE:
		case AFTER:
			RunTime.assumedTrue( this.hasElementCapsule( posEC ) );
			return posEC.getSideCapsule( pos );
		default:
			RunTime.badCall( "bad position for this method" );
			break;// unreachable code
		}
		return null;// found none
	}
	
	public Symbol getAsSymbol() {

		this.assumedValid();
		RunTime.assumedNotNull( name );
		return name;
	}
}
