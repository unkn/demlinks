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
 * this is level 4
 */
public class ListOrderedOfSymbols extends ListOrderedOfElementCapsules {
	
	private static final TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfSymbols>	allListOOSInstances	= new TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ListOrderedOfSymbols>();
	
	/**
	 * don't use this constructor directly
	 * 
	 * @param envDML
	 * @param name1
	 */
	private ListOrderedOfSymbols( Level4_DMLEnvironment envDML, Symbol name1 ) {

		super( envDML, name1 );
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
			Level4_DMLEnvironment envL4, Symbol existingSymbol ) {

		RunTime.assumedNotNull( envL4, existingSymbol );
		ListOrderedOfSymbols existingOne = getInstance( envL4, existingSymbol );
		if ( null == existingOne ) {
			existingOne = new ListOrderedOfSymbols( envL4, existingSymbol );
			registerInstance( envL4, existingSymbol, existingOne );
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

		RunTime.assumedNotNull( where, whichSymbol );
		switch ( where ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		ElementCapsule ec = this.getAsEC( whichSymbol );
		this.add_ElementCapsule( where, ec );
		this.assumedValid();
	}
	
	public void add( Symbol whichSymbol, Position pos, Symbol posSymbol ) {

		RunTime.assumedNotNull( whichSymbol, pos, posSymbol );
		ElementCapsule posEC = this.getAsEC( posSymbol );
		ElementCapsule newEC = this.getAsEC( whichSymbol );
		this.add_ElementCapsule( newEC, pos, posEC );
		this.assumedValid();
	}
	
	synchronized public Symbol get( Position pos ) {

		Symbol ret = this.internalGet( pos );
		this.assumedValid();
		return ret;
	}
	
	public Symbol get( Position pos, Symbol posSymbol ) {

		RunTime.assumedNotNull( pos, posSymbol );
		ElementCapsule posEC = this.getAsEC( posSymbol );
		ElementCapsule foundEC = this.get_ElementCapsule( pos, posEC );
		if ( null != foundEC ) {
			return foundEC.getAsSymbol();
		} else {
			return null;
		}
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
		if ( null != ec ) {
			return ec.getAsSymbol();
		} else {
			return null;
		}
	}
	
	private final ElementCapsule getAsEC( Symbol which ) {

		return ElementCapsule.getElementCapsule( env, which );
	}
	
	@Override
	public void assumedValid() {

		super.assumedValid();
		if ( this.size() > 0 ) {
			RunTime.assumedNotNull( this.internalGet( Position.FIRST ) );
			RunTime.assumedNotNull( this.internalGet( Position.LAST ) );
		}
	}
}
