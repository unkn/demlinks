/**
 * File creation: Oct 19, 2009 11:30:51 PM
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
 * list of NodeIDs in which order matters and it's known<br>
 * should be able to hold any number of NodeIDs even if they repeat inside the
 * list<br>
 * the order of insertion is kept<br>
 * this will be a double linked list represented in DMLEnvironment<br>
 * this is level 4<br>
 * NULL elements are yes allowed use setAllowNull(bool),<br>
 * DUPS are allowed also, settable(?) in constructor<br>
 */
public class ListOrderedOfSymbols extends ListOrderedOfElementCapsules {
	
	private static final TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfSymbols>	allListOOSInstances	= new TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfSymbols>();
	private final boolean																	allowNull;
	private final boolean																	allowDUPs;
	
	/**
	 * don't use this constructor directly
	 * 
	 * @param envDML
	 * @param name1
	 */
	private ListOrderedOfSymbols( Level4_DMLEnvironment envDML, Symbol name1,
			boolean allowNull1, boolean allowDUPs1 ) {

		super( envDML, name1 );
		RunTime.assumedNotNull( allowDUPs1 );
		allowDUPs = allowDUPs1;
		allowNull = allowNull1;
	}
	
	private final static void registerInstance( Level4_DMLEnvironment env,
			Symbol name, ListOrderedOfSymbols newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allListOOSInstances.ensure( env, name, newOne ) );
	}
	
	private final static ListOrderedOfSymbols getInstance(
			Level4_DMLEnvironment env, Symbol name ) {

		RunTime.assumedNotNull( env, name );
		return allListOOSInstances.get( env, name );
	}
	
	/**
	 * @param envL4
	 * @param existingSymbol
	 *            can be a list already, or just a new unique symbol to be
	 *            transformed into a list
	 * @return
	 */
	public static ListOrderedOfSymbols getListOOSymbols(
			Level4_DMLEnvironment envL4, Symbol existingSymbol,
			boolean allowNulls, boolean allowDUPs ) {

		RunTime.assumedNotNull( envL4, existingSymbol, allowDUPs );
		ListOrderedOfSymbols existingOne = getInstance( envL4, existingSymbol );
		if ( null == existingOne ) {
			existingOne = new ListOrderedOfSymbols( envL4, existingSymbol,
					allowNulls, allowDUPs );
			registerInstance( envL4, existingSymbol, existingOne );
		}
		if ( existingOne.isDUPAllowed() != allowDUPs ) {
			RunTime.badCall( "that Symbol was already a list with different setting for allowDUPs" );
		}
		if ( existingOne.isNullAllowed() != allowNulls ) {
			RunTime.badCall( "that Symbol was already a list with different setting for allowNulls" );
		}
		existingOne.assumedValid();
		return existingOne;
	}
	
	public boolean isDUPAllowed() {

		return allowDUPs;
	}
	
	@Override
	protected void internal_setName() {

		env.ensureVector( env.allListsSymbol, name );
	}
	
	@Override
	protected boolean internal_hasNameSetRight() {

		return env.isVector( env.allListsSymbol, name );
	}
	
	

	synchronized public void add( Position where, Symbol whichSymbol ) {

		RunTime.assumedNotNull( where );
		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		switch ( where ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		if ( !this.isDUPAllowed() ) {
			// must not already exist
			if ( this.hasSymbol( whichSymbol ) ) {
				// exists already
				RunTime.badCall( "you tried to add an already existing Symbol to the list, whilst the list didn't support DUPs" );
			}
		}
		ElementCapsule ec = ElementCapsule.getElementCapsule( env,
				env.newUniqueSymbol() );
		RunTime.assumedNull( ec.setElement( whichSymbol ) );
		this.add_ElementCapsule( where, ec );
		this.assumedValid();
	}
	
	public void add( Symbol whichSymbol, Position pos, Symbol posSymbol ) {

		RunTime.assumedNotNull( pos );
		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol, posSymbol );
		}
		
		if ( !this.isNullAllowed() ) {
			if ( this.hasSymbol( whichSymbol ) ) {
				RunTime.badCall( "the Symbol already exists and the list doesn't do DUPs" );
			}
		}
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "cannot find your posSymbol" );
		}
		
		ElementCapsule newEC = ElementCapsule.getElementCapsule( env,
				env.newUniqueSymbol() );
		RunTime.assumedNull( newEC.setElement( whichSymbol ) );
		this.add_ElementCapsule( newEC, pos, posEC );
		this.assumedValid();
	}
	
	public boolean hasSymbol( Symbol whichSymbol ) {

		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		RunTime.assumedFalse( this.isDUPAllowed() );
		return ( null != this.get_ElementCapsule( whichSymbol ) );
	}
	
	/**
	 * parses entire list, one by one <br>
	 * 
	 * @param posSymbol
	 * @return the already existing EC for the passed symbol; or null if not
	 *         found in list
	 */
	private ElementCapsule get_ElementCapsule( Symbol posSymbol ) {

		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( posSymbol );
		}
		// this method is not to be used while DUPs are allowed, because it will
		// only find first occurrence and this is not explicitly stated in
		// calling it
		RunTime.assumedFalse( this.isDUPAllowed() );
		
		ElementCapsule iter = this.get_ElementCapsule( Position.FIRST );
		ElementCapsule found = null;
		while ( null != iter ) {
			if ( iter.getElement() == posSymbol ) {
				if ( null != found ) {
					// found it again? since this is a no DUPs list => bug
					RunTime.bug( "a noDUPs list was detected to have dups" );
					// but then again, if dups are allowed this method should
					// not be called?
				}
				found = iter;
				// doesn't break because we want to check consistency
			}
			iter = this.get_ElementCapsule( Position.AFTER, iter );
		}
		return found;
	}
	
	synchronized public Symbol get( Position pos ) {

		Symbol ret = this.internalGet( pos );
		this.assumedValid();
		return ret;
	}
	
	/**
	 * usable only when noDUPs are allowed, else throws
	 * 
	 * @param pos
	 * @param posSymbol
	 * @return
	 */
	public Symbol get( Position pos, Symbol posSymbol ) {

		RunTime.assumedNotNull( pos );
		switch ( pos ) {// redundant checks
		case BEFORE:
		case AFTER:
			break;
		default:
			RunTime.badCall( "bad position" );
		}
		
		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( posSymbol );
		}
		
		RunTime.assumedFalse( this.isDUPAllowed() );// don't call it when DUPS
		// allowed!
		
		// acquire posElementCapsule
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "cannot find the posSymbol" );
		}
		
		ElementCapsule foundEC = this.get_ElementCapsule( pos, posEC );
		Symbol fe = null;
		if ( null != foundEC ) {
			fe = foundEC.getElement();
			if ( !this.isNullAllowed() ) {
				RunTime.assumedNotNull( fe );// consistency check,redundant
			}
		}
		return fe;// can be null;
	}
	
	/**
	 * @param pos
	 * @return the Symbol or null if Symbol is null in an allow null list; or
	 *         null if not found in an non-null allowing list
	 */
	private Symbol internalGet( Position pos ) {

		RunTime.assumedNotNull( pos );
		switch ( pos ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		ElementCapsule ec = this.get_ElementCapsule( pos );
		Symbol ret = null;
		if ( null != ec ) {
			ret = ec.getElement();
			if ( !this.isNullAllowed() ) {
				RunTime.assumedNotNull( ret );
			}
		}
		return ret;
	}
	
	@Override
	public void assumedValid() {

		super.assumedValid();
		if ( this.size() > 0 ) {
			if ( !this.isNullAllowed() ) {
				RunTime.assumedNotNull( this.internalGet( Position.FIRST ) );
				RunTime.assumedNotNull( this.internalGet( Position.LAST ) );
			}
		}
	}
	
	/**
	 * @return
	 */
	public boolean isNullAllowed() {

		return allowNull;
	}
	
	@Override
	protected void perItemCheck( ElementCapsule item ) {

		super.perItemCheck( item );
		Symbol elem = item.getElement();
		if ( !this.isNullAllowed() ) {
			RunTime.assumedNotNull( elem );
		}
		// TODO: can't really check for dups I guess, unless I make a new no
		// dups list and add all items to it
	}
}
