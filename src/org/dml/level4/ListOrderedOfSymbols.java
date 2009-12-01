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
	private boolean																			allowNull			= false;
	private final boolean																	allowDUPs;
	
	public void setAllowNull( boolean allow ) {

		RunTime.assumedNotNull( allow );
		allowNull = allow;
	}
	
	/**
	 * don't use this constructor directly
	 * 
	 * @param envDML
	 * @param name1
	 */
	private ListOrderedOfSymbols( Level4_DMLEnvironment envDML, Symbol name1,
			boolean allowDUPs1 ) {

		super( envDML, name1 );
		RunTime.assumedNotNull( allowDUPs1 );
		allowDUPs = allowDUPs1;
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
			boolean allowDUPs ) {

		RunTime.assumedNotNull( envL4, existingSymbol, allowDUPs );
		ListOrderedOfSymbols existingOne = getInstance( envL4, existingSymbol );
		if ( null == existingOne ) {
			existingOne = new ListOrderedOfSymbols( envL4, existingSymbol,
					allowDUPs );
			registerInstance( envL4, existingSymbol, existingOne );
		}
		if ( existingOne.allowDUPs != allowDUPs ) {
			RunTime.BadCallError();
		}
		existingOne.assumedValid();
		return existingOne;
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
		if ( !allowNull ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		switch ( where ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		if ( !allowDUPs ) {
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
		if ( !allowNull ) {
			RunTime.assumedNotNull( whichSymbol, posSymbol );
		}
		
		if ( !allowDUPs ) {
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

		if ( !allowNull ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		RunTime.assumedFalse( allowDUPs );
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

		if ( !allowNull ) {
			RunTime.assumedNotNull( posSymbol );
		}
		// this method is not to be used while DUPs are allowed, because it will
		// only find first occurrence and this is not explicitly stated in
		// calling it
		RunTime.assumedFalse( allowDUPs );
		
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
		
		if ( !allowNull ) {
			RunTime.assumedNotNull( posSymbol );
		}
		
		RunTime.assumedFalse( allowDUPs );// don't call it when DUPS allowed!
		
		// acquire posElementCapsule
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "cannot find the posSymbol" );
		}
		
		ElementCapsule foundEC = this.get_ElementCapsule( pos, posEC );
		Symbol fe = null;
		if ( null != foundEC ) {
			fe = foundEC.getElement();
			if ( !allowNull ) {
				RunTime.assumedNotNull( fe );// consistency check,redundant
			}
		}
		return fe;// can be null;
	}
	
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
			RunTime.assumedNotNull( ret );
		}
		return ret;
	}
	
	@Override
	public void assumedValid() {

		super.assumedValid();
		if ( this.size() > 0 ) {
			RunTime.assumedNotNull( this.internalGet( Position.FIRST ) );
			RunTime.assumedNotNull( this.internalGet( Position.LAST ) );
		}
		// TODO check allowDUPS and allowNull compliance
	}
}
