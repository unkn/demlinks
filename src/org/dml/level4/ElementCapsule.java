/**
 * File creation: Nov 20, 2009 6:52:32 AM
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
import org.dml.level3.DomainPointer;
import org.dml.level3.Pointer;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.references.Position;



/**
 * 
 *
 */
public class ElementCapsule {
	
	private static final TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ElementCapsule>	allElementCapsuleInstances	= new TwoKeyHashMap<Level4_DMLEnvironment, Symbol, ElementCapsule>();
	private final Symbol																name;
	private final Level4_DMLEnvironment													envL4;
	private final DomainPointer															cachedPrev,
			cachedNext;
	private final Pointer																element;
	
	private final static void registerInstance( Level4_DMLEnvironment env,
			Symbol name, ElementCapsule newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allElementCapsuleInstances.ensure( env, name,
				newOne ) );
	}
	
	private final static ElementCapsule getInstance( Level4_DMLEnvironment env,
			Symbol name ) {

		RunTime.assumedNotNull( env, name );
		return allElementCapsuleInstances.get( env, name );
	}
	
	/**
	 * @param env
	 * @param x
	 * @return
	 */
	public static ElementCapsule getElementCapsule(
			Level4_DMLEnvironment envL4, Symbol existingSymbol ) {

		RunTime.assumedNotNull( envL4, existingSymbol );
		ElementCapsule existingOne = getInstance( envL4, existingSymbol );
		if ( null == existingOne ) {
			existingOne = new ElementCapsule( envL4, existingSymbol );
			registerInstance( envL4, existingSymbol, existingOne );
		}
		existingOne.assumedIsValidCapsule();
		return existingOne;
	}
	
	/**
	 * @param env_L4
	 * @param nameOfEC
	 *            existing EC, or a no-children Symbol to be made into EC<br>
	 *            if already existing EC, it must have 3 children ref2prev,
	 *            ref2next, and ref2element, they can be null though
	 */
	private ElementCapsule( Level4_DMLEnvironment env_L4, Symbol nameOfEC ) {

		RunTime.assumedNotNull( nameOfEC, env_L4 );
		RunTime.assumedTrue( env_L4.isInited() );
		name = nameOfEC;
		envL4 = env_L4;
		int count = envL4.countTerminals( nameOfEC );
		if ( count == 0 ) {
			envL4.ensureVector( envL4.allElementCapsules_Symbol, name );
			// create new EC
			cachedPrev = envL4.getNewNullDomainPointer( envL4.allElementCapsules_Symbol );
			cachedNext = envL4.getNewNullDomainPointer( envL4.allElementCapsules_Symbol );
			element = envL4.getNewNullPointer();
			RunTime.assumedFalse( envL4.ensureVector( name,
					cachedPrev.getAsSymbol() ) );
			RunTime.assumedFalse( envL4.ensureVector( name,
					element.getAsSymbol() ) );
			RunTime.assumedFalse( envL4.ensureVector( name,
					cachedNext.getAsSymbol() ) );
			
			RunTime.assumedFalse( envL4.ensureVector(
					envL4.allPrevElementCapsules_Symbol,
					cachedPrev.getAsSymbol() ) );
			RunTime.assumedFalse( envL4.ensureVector(
					envL4.allNextElementCapsules_Symbol,
					cachedNext.getAsSymbol() ) );
			RunTime.assumedFalse( envL4.ensureVector(
					envL4.allElementsOfEC_Symbol, element.getAsSymbol() ) );
		} else {
			RunTime.assumedTrue( count == 3 );
			Symbol ref2Prev = envL4.findCommonTerminalForInitials(
					envL4.allPrevElementCapsules_Symbol, name );
			cachedPrev = envL4.getExistingDomainPointer( ref2Prev,
					envL4.allElementCapsules_Symbol, true );
			
			Symbol ref2Next = envL4.findCommonTerminalForInitials(
					envL4.allNextElementCapsules_Symbol, name );
			cachedNext = envL4.getExistingDomainPointer( ref2Next,
					envL4.allElementCapsules_Symbol, true );
			
			Symbol elem = envL4.findCommonTerminalForInitials(
					envL4.allElementsOfEC_Symbol, name );
			element = envL4.getExistingPointer( elem, true );
		}
		this.assumedIsValidCapsule();
	}
	
	/**
	 * @param name2
	 * @return
	 */
	public void assumedIsValidCapsule() {

		RunTime.assumedNotNull( name );
		// make sure it is an EC ie. AllECs->name
		// it could not have either Prev or Next though
		RunTime.assumedTrue( envL4.isVector( envL4.allElementCapsules_Symbol,
				name ) );
		
		int size = envL4.countTerminals( name );
		RunTime.assumedTrue( 3 == size );
		
		Symbol ref2Prev = envL4.findCommonTerminalForInitials(
				envL4.allPrevElementCapsules_Symbol, name );
		RunTime.assumedNotNull( ref2Prev );
		DomainPointer tmpPrev = envL4.getExistingDomainPointer( ref2Prev,
				envL4.allElementCapsules_Symbol, true );
		RunTime.assumedTrue( tmpPrev == cachedPrev );
		
		Symbol ref2Next = envL4.findCommonTerminalForInitials(
				envL4.allNextElementCapsules_Symbol, name );
		RunTime.assumedNotNull( ref2Next );
		DomainPointer tmpNext = envL4.getExistingDomainPointer( ref2Next,
				envL4.allElementCapsules_Symbol, true );
		RunTime.assumedTrue( tmpNext == cachedNext );
		
		Symbol elem = envL4.findCommonTerminalForInitials(
				envL4.allElementsOfEC_Symbol, name );
		RunTime.assumedNotNull( elem );
		Pointer tmpElement = envL4.getExistingPointer( elem, true );
		RunTime.assumedTrue( tmpElement == element );
	}
	
	/**
	 * @return
	 */
	public boolean isAlone() {

		boolean ret1 = ( this.getSideCapsule( Position.FIRST ) == null );
		boolean ret2 = ( this.getSideCapsule( Position.LAST ) == null );
		if ( ret1 != ret2 ) {
			RunTime.bug( "they should be same value" );
		}
		return ret1;
	}
	
	/**
	 * @param opposite
	 * @return
	 */
	public ElementCapsule getSideCapsule( Position pos ) {

		Symbol the = null;
		switch ( pos ) {
		case FIRST:
		case BEFORE:
			the = cachedPrev.getPointee();
			break;
		case LAST:
		case AFTER:
			the = cachedNext.getPointee();
			break;
		default:
			RunTime.bug( "shouldn't be here" );
		}
		if ( null != the ) {
			// FIXME: a lot of new
			return new ElementCapsule( envL4, the );
		} else {
			return null;
		}
	}
	
	/**
	 * @param pos
	 * @param newPointer
	 */
	public void setCapsule( Position pos, ElementCapsule newPointer ) {

		switch ( pos ) {
		case FIRST:
		case BEFORE:
			if ( null == newPointer ) {
				cachedPrev.pointTo( null );
			} else {
				cachedPrev.pointTo( newPointer.getAsSymbol() );
			}
			break;
		case LAST:
		case AFTER:
			if ( null == newPointer ) {
				cachedNext.pointTo( null );
			} else {
				cachedNext.pointTo( newPointer.getAsSymbol() );
			}
			break;
		default:
			RunTime.bug( "cannot reach this" );
		}
	}
	
	/**
	 * @return
	 */
	public Symbol getAsSymbol() {

		this.assumedIsValidCapsule();
		RunTime.assumedNotNull( name );
		return name;
	}
	
}
