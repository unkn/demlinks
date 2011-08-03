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



package org.dml.level040;



import org.dml.level010.*;
import org.dml.level020.*;
import org.dml.level025.*;
import org.dml.tools.*;
import org.dml.tracking.*;
import org.references.*;



/**
 * list of NodeIDs in which order matters and it's known<br>
 * should be able to hold any number of NodeIDs even if they repeat inside the
 * list<br>
 * the order of insertion is kept<br>
 * this will be a double linked list represented in DMLEnvironment<br>
 * this is level 4<br>
 * NULL elements are allowed, settable in constructor<br>
 * DUPS are allowed also, settable in constructor<br>
 * once set at construction, you cannot change them for current instance<br>
 */
public class ListOrderedOfSymbols extends ListOrderedOfElementCapsules implements OrderedList {
	
	private static final TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfSymbols>	allListOOSInstances	=
																															new TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfSymbols>();
	
	// we keep these also so we can check if the db ones were modified since this was instanced in java
	private final boolean																			cachedAllowNull;
	private final boolean																			cachedAllowDUPs;
	
	
	/**
	 * don't use this constructor directly<br>
	 * to be used for new list
	 * 
	 * @param passedEnv
	 * @param passedSelf
	 *            must already exist
	 * @param expectedAllowNull1
	 *            must already exist for self
	 * @param expectedAllowDUPs1
	 *            must already exist for self
	 */
	private ListOrderedOfSymbols( final Level040_DMLEnvironment passedEnv, final DomainSet passedSelf,
			final boolean expectedAllowNull1, final boolean expectedAllowDUPs1 ) {
		
		super( passedEnv, passedSelf );
		// RunTime.assumedNotNull( expectedAllowNull1, expectedAllowDUPs1 );
		
		// those 2 cannot already exist
		// RunTime.assumedFalse( this.internal_getRealAllowDUPs() );
		// RunTime.assumedFalse( this.internal_getRealAllowNull() );
		
		cachedAllowDUPs = expectedAllowDUPs1;
		cachedAllowNull = expectedAllowNull1;
		// this.internal_setRealAllowDUPs( expectedAllowDUPs1 );
		// this.internal_setRealAllowNull( expectedAllowNull1 );
		//
		// RunTime.assumedTrue( this.internal_hasNameSetRight() );
		// RunTime.assumedTrue( ListOrderedOfSymbols.isListOrderedOfSymbols( env, self.getAsSymbol() ) );
	}
	
	
	// /**
	// * to be used for existing list
	// *
	// * @param passedEnv
	// * @param name1
	// */
	// private ListOrderedOfSymbols( Level040_DMLEnvironment passedEnv, DomainSet name1 ) {
	//
	// super( passedEnv, name1 );
	// RunTime.assumedNotNull( env, self );
	// RunTime.assumedTrue( passedEnv == env );
	// RunTime.assumedTrue( name1 == self );
	// cachedAllowDUPs = this.internal_getRealAllowDUPs();
	// cachedAllowNull = this.internal_getRealAllowNull();
	// }
	
	/**
	 * @param env
	 * @param passedSelf
	 * @return
	 */
	public static boolean isListOrderedOfSymbols( final Level040_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedEnv, passedSelf );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		return passedEnv.allListsOOS_Set.hasSymbol( passedSelf );
	}
	
	
	private static void internal_setAsListOrderedOfSymbols( final Level040_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedSelf, passedEnv );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		// was not set before
		RunTime.assumedFalse( passedEnv.allListsOOS_Set.addToSet( passedSelf ) );
	}
	
	
	/**
	 * @param allowDUPs
	 *            new value
	 * @return the previous value
	 */
	private static boolean internal_setRealAllowDUPs( final Level040_DMLEnvironment passedEnv, final DomainSet passedSelf,
														final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( passedEnv );
		return ListOrderedOfSymbols.internal_setRealAllow( passedEnv, passedSelf, passedEnv.allowDUPs_Set, allowDUPs );
	}
	
	
	/**
	 * @param allowNull
	 *            new value
	 * @return the previous value
	 */
	private static boolean internal_setRealAllowNull( final Level040_DMLEnvironment passedEnv, final DomainSet passedSelf,
														final boolean allowNull ) {
		
		RunTime.assumedNotNull( passedEnv );
		return ListOrderedOfSymbols.internal_setRealAllow( passedEnv, passedSelf, passedEnv.allowNull_Set, allowNull );
	}
	
	
	/**
	 * @param set
	 *            one of env.allowNull_Set or env.allowDUPs_Set
	 * @param allow
	 *            new value
	 * @return the previous value
	 */
	private static boolean internal_setRealAllow( final Level040_DMLEnvironment passedEnv, final DomainSet passedSelf,
													final SetOfTerminalSymbols set, final boolean allow ) {
		
		RunTime.assumedNotNull( passedEnv, set );
		if ( allow ) {
			return set.addToSet( passedSelf.getAsSymbol() );
		} else {
			// RunTime.assumedFalse( set.hasSymbol( self.getAsSymbol() ) );
			return set.remove( passedSelf.getAsSymbol() );
		}
	}
	
	
	private boolean internal_getRealAllowDUPs() {
		
		RunTime.assumedNotNull( env, self );
		return ListOrderedOfSymbols.getRealAllowDUPsFor( env, self.getAsSymbol() );
	}
	
	
	private boolean internal_getRealAllowNull() {
		
		RunTime.assumedNotNull( env, self );
		return ListOrderedOfSymbols.getRealAllowNullFor( env, self.getAsSymbol() );
	}
	
	
	public static boolean getRealAllowNullFor( final Level040_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedEnv, passedSelf );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		return passedEnv.allowNull_Set.hasSymbol( passedSelf );
	}
	
	
	public static boolean getRealAllowDUPsFor( final Level040_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedEnv, passedSelf );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		return passedEnv.allowDUPs_Set.hasSymbol( passedSelf );
	}
	
	
	private final static void registerInstance( final Level040_DMLEnvironment env, final DomainSet name,
												final ListOrderedOfSymbols newOne ) {
		
		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allListOOSInstances.ensure( env, name, newOne ) );
	}
	
	
	private final static ListOrderedOfSymbols getInstance( final Level040_DMLEnvironment env, final DomainSet name ) {
		
		RunTime.assumedNotNull( env, name );
		return allListOOSInstances.get( env, name );
	}
	
	
	/**
	 * @param passedEnv
	 * @param existingSymbol
	 *            must have 0 children
	 * @param allowNulls
	 * @param allowDUPs
	 * @return
	 */
	public static ListOrderedOfSymbols getNewListOOSymbols( final Level040_DMLEnvironment passedEnv,
															final Symbol existingSymbol, final boolean allowNulls,
															final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( passedEnv, existingSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( ListOrderedOfSymbols.isListOrderedOfSymbols( passedEnv, existingSymbol ) ) {
			RunTime.badCall( "existingSymbol is already a List, use getExisting or ensure instead!" );
		}
		
		final DomainSet existingDS = passedEnv.getAsDomainSet( existingSymbol, passedEnv.allElementCapsules_Set.getAsSymbol() );
		existingDS.assumedValid();
		RunTime.assumedTrue( existingDS.getAsSymbol() == existingSymbol );
		
		// ensure it's empty/new Symbol
		if ( existingDS.size() != 0 ) {
			// since it's new
			RunTime.badCall( "passed symbol must've been non-empty" );
		}
		RunTime.assumedNull( getInstance( passedEnv, existingDS ) );// couldn't have been already instantiated
		if ( ( passedEnv.allowDUPs_Set.hasSymbol( existingSymbol ) ) || ( passedEnv.allowNull_Set.hasSymbol( existingSymbol ) ) ) {
			RunTime.bug( "inconsistent, not supposed to be in such a state" );
		}
		
		// make new list
		RunTime.assumedFalse( ListOrderedOfSymbols.internal_setRealAllowNull( passedEnv, existingDS, allowNulls ) );
		RunTime.assumedFalse( ListOrderedOfSymbols.internal_setRealAllowDUPs( passedEnv, existingDS, allowDUPs ) );
		internal_setAsListOrderedOfSymbols( passedEnv, existingSymbol );
		final ListOrderedOfSymbols newOne = new ListOrderedOfSymbols( passedEnv, existingDS, allowNulls, allowDUPs );
		newOne.assumedValid();
		registerInstance( passedEnv, existingDS, newOne );
		return newOne;
	}
	
	
	/**
	 * this will not check for given allowNull and allowDUPs, it will just retrieve their existing values and use those.
	 * 
	 * @param passedEnv
	 * @param existingSymbol
	 *            must have been already a list
	 * @return
	 */
	public static ListOrderedOfSymbols getExistingListOOSymbols( final Level040_DMLEnvironment passedEnv,
																	final Symbol existingSymbol ) {
		
		RunTime.assumedNotNull( passedEnv, existingSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( !ListOrderedOfSymbols.isListOrderedOfSymbols( passedEnv, existingSymbol ) ) {
			RunTime.badCall( "existingSymbol did not already exist a a List" );
		}
		
		final DomainSet existingDS = passedEnv.getAsDomainSet( existingSymbol, passedEnv.allElementCapsules_Set.getAsSymbol() );
		existingDS.assumedValid();
		RunTime.assumedTrue( existingDS.getAsSymbol() == existingSymbol );
		
		ListOrderedOfSymbols existingListOOS = getInstance( passedEnv, existingDS );
		if ( null == existingListOOS ) {
			// yes it may exist as list but it was never instantiated in Java
			final boolean existingAllowNull = getRealAllowNullFor( passedEnv, existingSymbol );
			final boolean existingAllowDUPs = getRealAllowDUPsFor( passedEnv, existingSymbol );
			existingListOOS = new ListOrderedOfSymbols( passedEnv, existingDS, existingAllowNull, existingAllowDUPs );
			existingListOOS.assumedValid();
			registerInstance( passedEnv, existingDS, existingListOOS );
		} else {
			existingListOOS.assumedValid();
		}
		
		RunTime.assumedTrue( existingListOOS.getAsSymbol() == existingSymbol );
		return existingListOOS;
	}
	
	
	/**
	 * expected values must match or BadCallError exception<br>
	 * use this when you do know the values for allowNull and allowDUPs, so they can be checked against the existing
	 * ones for consistency (else throws)<br>
	 * 
	 * @param passedEnv
	 * @param existingSymbol
	 * @param allowNulls
	 *            expected value for allowNulls
	 * @param allowDUPs
	 *            expected value for allowDUPs
	 * @return
	 */
	public static ListOrderedOfSymbols getExistingListOOSymbols( final Level040_DMLEnvironment passedEnv,
																	final Symbol existingSymbol, final boolean allowNulls,
																	final boolean allowDUPs ) {
		
		// RunTime.assumedNotNull( allowDUPs, allowNulls );
		final ListOrderedOfSymbols ret = getExistingListOOSymbols( passedEnv, existingSymbol );
		RunTime.assumedNotNull( ret );
		if ( ret.isNullAllowed() != allowNulls ) {
			RunTime.badCall( "inconsistency detected. Existing list's allowNull was different than expected one." );
		}
		if ( ret.isDUPAllowed() != allowDUPs ) {
			RunTime.badCall( "inconsistency detected. Existing list's allowDUPs was different than expected one." );
		}
		return ret;
	}
	
	
	public static ListOrderedOfSymbols ensureListOOSymbols( final Level040_DMLEnvironment passedEnv,
															final Symbol existingSymbol, final boolean allowNulls,
															final boolean allowDUPs ) {
		
		RunTime.assumedNotNull( passedEnv, existingSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( isListOrderedOfSymbols( passedEnv, existingSymbol ) ) {
			// already is List, we use getExisting then
			return getExistingListOOSymbols( passedEnv, existingSymbol, allowNulls, allowDUPs );
		} else {
			// not existing, must use new then
			return getNewListOOSymbols( passedEnv, existingSymbol, allowNulls, allowDUPs );
		}
	}
	
	
	public boolean isDUPAllowed() {
		
		// the real and the cached must not change (at least) while the instance is active
		// imagine if the list has nulls and you change this to false w/o removing those nulls from list, you get
		// inconsistent list
		RunTime.assumedTrue( internal_getRealAllowDUPs() == cachedAllowDUPs );
		return cachedAllowDUPs;
	}
	
	
	/**
	 * @return
	 */
	public boolean isNullAllowed() {
		
		// the cached and the real must not change (at least) while the instance is active
		RunTime.assumedTrue( internal_getRealAllowNull() == cachedAllowNull );
		return cachedAllowNull;
	}
	
	
	// @Override
	// protected void internal_setName() {
	//
	// RunTime.assumedNotNull( self );
	// RunTime.assumedFalse( env.allListsOOS_Set.addToSet( self.getAsSymbol() ) );
	// }
	//
	@Override
	protected boolean isItself() {
		
		RunTime.assumedNotNull( self, env );
		RunTime.assumedTrue( env.isInitedSuccessfully() );
		return isListOrderedOfSymbols( env, self.getAsSymbol() );
	}
	
	
	
	@Override
	public void add( final Symbol whichSymbol, final Position where ) {
		
		RunTime.assumedNotNull( where );
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		switch ( where ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		if ( !isDUPAllowed() ) {
			// must not already exist
			if ( hasSymbol( whichSymbol ) ) {
				// exists already
				RunTime
					.badCall( "you tried to add an already existing Symbol to the list, whilst the list didn't support DUPs" );
			}
		}
		final ElementCapsule ec = ElementCapsule.getNewEmptyElementCapsule( env, env.newUniqueSymbol() );
		RunTime.assumedNull( ec.setElement( whichSymbol ) );
		this.add_ElementCapsule( where, ec );
		assumedValid();
	}
	
	
	@Override
	public void add( final Symbol whichSymbol, final Position pos, final Symbol posSymbol ) {
		
		RunTime.assumedNotNull( pos );
		RunTime.assumedFalse( isDUPAllowed() );// if posSymbol exists twice...
		
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol, posSymbol );
		}
		
		// if ( !this.isDUPAllowed() ) {
		// if ( this.hasSymbol( whichSymbol ) ) {
		// RunTime.badCall( "the Symbol already exists and the list doesn't do DUPs" );
		// }
		// }
		final ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "cannot find your posSymbol" );
		}
		
		final ElementCapsule newEC = ElementCapsule.getNewEmptyElementCapsule( env, env.newUniqueSymbol() );
		RunTime.assumedNull( newEC.setElement( whichSymbol ) );
		this.add_ElementCapsule( newEC, pos, posEC );
		assumedValid();
	}
	
	
	@Override
	public boolean hasSymbol( final Symbol whichSymbol ) {
		
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( whichSymbol );
		}
		RunTime.assumedFalse( isDUPAllowed() );
		// TODO we can make this easier, w/o parsing entire list
		return ( null != this.get_ElementCapsule( whichSymbol ) );
	}
	
	
	/**
	 * parses entire list, one by one <br>
	 * 
	 * @param posSymbol
	 * @return the already existing EC for the passed symbol; or null if not
	 *         found in list
	 */
	private ElementCapsule get_ElementCapsule( final Symbol posSymbol ) {
		
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( posSymbol );
		}
		// this method is not to be used while DUPs are allowed, because it will
		// only find first occurrence and this is not explicitly stated in
		// calling it
		RunTime.assumedFalse( isDUPAllowed() );
		
		self.assumedValid();
		// assuming posSymbol is already part of list, then it must have a
		// unique parent which has as parent allElementCapsules_Symbol
		// could be part of another list too, so two of those unique parents may
		// be already, and it may have other explicit parents
		ElementCapsule found = null;
		if ( ( isNullAllowed() ) || ( size() <= env.countInitials( posSymbol ) ) ) {
			// parse entire list looking for 'posSymbol'
			ElementCapsule iter = this.get_ElementCapsule( Position.FIRST );
			
			while ( null != iter ) {
				if ( iter.getElement() == posSymbol ) {
					if ( null != found ) {
						// found it again? since this is a no DUPs list => bug
						RunTime.bug( "a noDUPs list was detected to have dups" );
						// but then again, if dups are allowed this method
						// should
						// not be called?
					}
					found = iter;
					// doesn't break because we want to check consistency
				}
				iter = this.get_ElementCapsule( Position.AFTER, iter );
			}
		} else {
			Log.warn( "parsing from posSymbol upwards (ie. method 2)" );
			RunTime.assumedFalse( isNullAllowed() );
			RunTime.assumedNotNull( posSymbol );
			Symbol foundECAsSymbol = null;
			// this->EC->Ref2Elem->posSymbol
			// AllEC->EC
			// AllRef2Elems->Ref2Elem
			SymbolIterator iter = env.getIterator_on_Initials_of( posSymbol );
			try {
				iter.goFirst();
				while ( iter.now() != null ) {
					if ( env.allRef2ElementsInEC_Set.hasSymbol( iter.now() ) ) {
						// so far:
						// AllRef2Elems->Ref2Elem aka iter.now()->posSymbol
						// now we check all parents of iter.now() and we must
						// find two which are this list and AllEC
						SymbolIterator secIter = env.getIterator_on_Initials_of( iter.now() );
						try {
							secIter.goFirst();
							while ( secIter.now() != null ) {
								if ( env.isVector( self.getAsSymbol(), secIter.now() ) ) {
									if ( !env.allElementCapsules_Set.hasSymbol( secIter.now() ) ) {
										RunTime
											.bug( "a list may not contain anything other than EC children, yet we found a non EC -> inconsistency somewhere" );
									}
									// so far we have:
									// thisList->secIter.now()
									// AllEC->secIter.now()
									// secIter.now() ->iter.now()->posSymbol
									if ( foundECAsSymbol != null ) {
										RunTime.bug( "found 2 ! should be only 1" );
									}
									foundECAsSymbol = secIter.now();
								}
								secIter.goNext();
							}
						} finally {
							try {
								secIter.close();
							} finally {
								secIter = null;
							}
						}
						
					}
					iter.goNext();
				}
			} finally {
				try {
					iter.close();
				} finally {
					iter = null;
				}
			}
			if ( foundECAsSymbol != null ) {
				found = ElementCapsule.getExistingElementCapsule( env, foundECAsSymbol );
				found.assumedIsValidCapsule();
			}
		}
		
		return found;// null or it
	}
	
	
	@Override
	synchronized public Symbol get( final Position pos ) {
		
		final Symbol ret = internalGet( pos );
		assumedValid();
		return ret;
	}
	
	
	/**
	 * usable only when noDUPs are allowed, else throws
	 * 
	 * @param pos
	 * @param posSymbol
	 * @return
	 */
	@Override
	public Symbol get( final Position pos, final Symbol posSymbol ) {
		
		RunTime.assumedNotNull( pos );
		switch ( pos ) {// redundant checks
		case BEFORE:
		case AFTER:
			break;
		default:
			RunTime.badCall( "bad position" );
		}
		
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( posSymbol );
		}
		
		RunTime.assumedFalse( isDUPAllowed() );// don't call it when DUPS
		// allowed!
		
		// acquire posElementCapsule
		final ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "cannot find the posSymbol" );
		}
		
		final ElementCapsule foundEC = this.get_ElementCapsule( pos, posEC );
		Symbol fe = null;
		if ( null != foundEC ) {
			fe = foundEC.getElement();
			if ( !isNullAllowed() ) {
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
	private Symbol internalGet( final Position pos ) {
		
		RunTime.assumedNotNull( pos );
		switch ( pos ) {
		case FIRST:
		case LAST:
			break;
		default:
			RunTime.badCall( "unsupported position" );
		}
		final ElementCapsule ec = this.get_ElementCapsule( pos );
		Symbol ret = null;
		if ( null != ec ) {
			ret = ec.getElement();
			if ( !isNullAllowed() ) {
				RunTime.assumedNotNull( ret );
				RunTime.assumedTrue( self.hasSymbol( ec.getAsSymbol() ) );
			}
		}
		return ret;
	}
	
	
	@Override
	public void assumedValid() {
		
		super.assumedValid();
		if ( size() > 0 ) {
			if ( !isNullAllowed() ) {
				RunTime.assumedNotNull( internalGet( Position.FIRST ) );
				RunTime.assumedNotNull( internalGet( Position.LAST ) );
			}
		}
		
		RunTime.assumedTrue( internal_getRealAllowDUPs() == cachedAllowDUPs );
		RunTime.assumedTrue( internal_getRealAllowNull() == cachedAllowNull );
	}
	
	
	@Override
	protected void perItemCheck( final ElementCapsule item ) {
		
		super.perItemCheck( item );
		final Symbol elem = item.getElement();
		if ( !isNullAllowed() ) {
			RunTime.assumedNotNull( elem );
		}
		// TODO: can't really check for dups I guess, unless I make a new no
		// dups list and add all items to it
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#ensure(org.dml.level010.Symbol)
	 */
	@Override
	public boolean ensure( final Symbol whichSymbol ) {
		
		final boolean ret = hasSymbol( whichSymbol );
		if ( !ret ) {
			this.add( whichSymbol, Position.LAST );
		}
		RunTime.assumedTrue( hasSymbol( whichSymbol ) );
		return ret;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.references.Position, org.dml.level010.Symbol)
	 */
	@Override
	public Symbol remove( final Position pos, final Symbol posSymbol ) {
		
		RunTime.assumedNotNull( pos, posSymbol );
		final ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC ) {
			RunTime.badCall( "posSymbol not found" );
		}
		// ElementCapsule ec = this.get_ElementCapsule( pos, posEC );
		final ElementCapsule ret = super.removeEC( pos, posEC );
		if ( null != ret ) {
			return ret.getElement();
		} else {
			return null;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.references.Position)
	 */
	@Override
	public Symbol remove( final Position pos ) {
		
		RunTime.assumedNotNull( pos );
		// switch ( pos ) {
		// case FIRST:
		// case LAST:
		// break;
		// default:
		// RunTime.badCall( "unsupported position" );
		// }
		// ElementCapsule ec = this.get_ElementCapsule( pos );
		final ElementCapsule ret = super.removeEC( pos );
		if ( null != ret ) {
			RunTime.assumedFalse( self.hasSymbol( ret.getElement() ) );
			return ret.getElement();
		} else {
			return null;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.dml.level010.Symbol)
	 */
	@Override
	public boolean remove( final Symbol whichSymbol ) {
		
		RunTime.assumedNotNull( whichSymbol );
		final ElementCapsule ec = this.get_ElementCapsule( whichSymbol );
		RunTime.assumedTrue( hasElementCapsule( ec ) );
		RunTime.assumedTrue( ec.getElement() == whichSymbol );
		final boolean ret = super.remove( ec );
		RunTime.assumedFalse( hasElementCapsule( ec ) );
		return ret;
	}
	
	
	/**
	 * don't use this
	 */
	@Deprecated
	@Override
	public boolean remove( final ElementCapsule whichEC ) {
		
		return super.remove( whichEC );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.ListOrderedOfElementCapsules#removeEC(org.references.Position)
	 */
	@Deprecated
	@Override
	public ElementCapsule removeEC( final Position pos ) {
		
		return super.removeEC( pos );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.ListOrderedOfElementCapsules#removeEC(org.references.Position,
	 * org.dml.level040.ElementCapsule)
	 */
	@Deprecated
	@Override
	public ElementCapsule removeEC( final Position pos, final ElementCapsule posEC ) {
		
		return super.removeEC( pos, posEC );
	}
	
	// TODO JUnit for remove
}
