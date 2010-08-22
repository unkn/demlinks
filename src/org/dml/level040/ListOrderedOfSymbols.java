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



import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.level010.Symbol;
import org.dml.level025.DomainSet;
import org.dml.level025.SetOfTerminalSymbols;
import org.dml.storagewrapper.StorageException;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.dml.tracking.Factory;
import org.javapart.logger.Log;
import org.references.Position;

import com.sleepycat.je.DatabaseException;



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
public class ListOrderedOfSymbols
		extends
		ListOrderedOfElementCapsules
		implements
		OrderedList
{
	
	private static final TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfSymbols>	allListOOSInstances	= new TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfSymbols>();
	
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
	private ListOrderedOfSymbols(
			Level040_DMLEnvironment passedEnv,
			DomainSet passedSelf,
			boolean expectedAllowNull1,
			boolean expectedAllowDUPs1 )
	{
		
		super(
				passedEnv,
				passedSelf );
		RunTime.assumedNotNull(
								expectedAllowNull1,
								expectedAllowDUPs1 );
		
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
	public static
			boolean
			isListOrderedOfSymbols(
									Level040_DMLEnvironment passedEnv,
									Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInited() );
		return passedEnv.allListsOOS_Set.hasSymbol( passedSelf );
	}
	

	private static
			void
			internal_setAsListOrderedOfSymbols(
												Level040_DMLEnvironment passedEnv,
												Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedSelf,
								passedEnv );
		RunTime.assumedTrue( passedEnv.isInited() );
		// was not set before
		RunTime.assumedFalse( passedEnv.allListsOOS_Set.addToSet( passedSelf ) );
	}
	

	/**
	 * @param allowDUPs
	 *            new value
	 * @return the previous value
	 */
	private static
			boolean
			internal_setRealAllowDUPs(
										Level040_DMLEnvironment passedEnv,
										DomainSet passedSelf,
										boolean allowDUPs )
	{
		
		RunTime.assumedNotNull( passedEnv );
		return ListOrderedOfSymbols.internal_setRealAllow(
															passedEnv,
															passedSelf,
															passedEnv.allowDUPs_Set,
															allowDUPs );
	}
	

	/**
	 * @param allowNull
	 *            new value
	 * @return the previous value
	 */
	private static
			boolean
			internal_setRealAllowNull(
										Level040_DMLEnvironment passedEnv,
										DomainSet passedSelf,
										boolean allowNull )
	{
		
		RunTime.assumedNotNull( passedEnv );
		return ListOrderedOfSymbols.internal_setRealAllow(
															passedEnv,
															passedSelf,
															passedEnv.allowNull_Set,
															allowNull );
	}
	

	/**
	 * @param set
	 *            one of env.allowNull_Set or env.allowDUPs_Set
	 * @param allow
	 *            new value
	 * @return the previous value
	 */
	private static
			boolean
			internal_setRealAllow(
									Level040_DMLEnvironment passedEnv,
									DomainSet passedSelf,
									SetOfTerminalSymbols set,
									boolean allow )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								set,
								allow );
		if ( allow )
		{
			return set.addToSet( passedSelf.getAsSymbol() );
		}
		else
		{
			// RunTime.assumedFalse( set.hasSymbol( self.getAsSymbol() ) );
			return set.remove( passedSelf.getAsSymbol() );
		}
	}
	

	private
			boolean
			internal_getRealAllowDUPs()
	{
		
		RunTime.assumedNotNull(
								env,
								self );
		return ListOrderedOfSymbols.getRealAllowDUPsFor(
															env,
															self.getAsSymbol() );
	}
	

	private
			boolean
			internal_getRealAllowNull()
	{
		
		RunTime.assumedNotNull(
								env,
								self );
		return ListOrderedOfSymbols.getRealAllowNullFor(
															env,
															self.getAsSymbol() );
	}
	

	public static
			boolean
			getRealAllowNullFor(
									Level040_DMLEnvironment passedEnv,
									Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInited() );
		return passedEnv.allowNull_Set.hasSymbol( passedSelf );
	}
	

	public static
			boolean
			getRealAllowDUPsFor(
									Level040_DMLEnvironment passedEnv,
									Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInited() );
		return passedEnv.allowDUPs_Set.hasSymbol( passedSelf );
	}
	

	private final static
			void
			registerInstance(
								Level040_DMLEnvironment env,
								DomainSet name,
								ListOrderedOfSymbols newOne )
	{
		
		RunTime.assumedNotNull(
								env,
								name,
								newOne );
		RunTime.assumedFalse( allListOOSInstances.ensure(
															env,
															name,
															newOne ) );
	}
	

	private final static
			ListOrderedOfSymbols
			getInstance(
							Level040_DMLEnvironment env,
							DomainSet name )
	{
		
		RunTime.assumedNotNull(
								env,
								name );
		return allListOOSInstances.get(
										env,
										name );
	}
	

	/**
	 * @param passedEnv
	 * @param existingSymbol
	 *            must have 0 children
	 * @param allowNulls
	 * @param allowDUPs
	 * @return
	 */
	public static
			ListOrderedOfSymbols
			getNewListOOSymbols(
									Level040_DMLEnvironment passedEnv,
									Symbol existingSymbol,
									boolean allowNulls,
									boolean allowDUPs )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								existingSymbol,
								allowNulls,
								allowDUPs );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		if ( ListOrderedOfSymbols.isListOrderedOfSymbols(
															passedEnv,
															existingSymbol ) )
		{
			RunTime.badCall( "existingSymbol is already a List, use getExisting or ensure instead!" );
		}
		
		DomainSet existingDS = passedEnv.getAsDomainSet(
															existingSymbol,
															passedEnv.allElementCapsules_Set.getAsSymbol() );
		existingDS.assumedValid();
		RunTime.assumedTrue( existingDS.getAsSymbol() == existingSymbol );
		
		// ensure it's empty/new Symbol
		if ( existingDS.size() != 0 )
		{
			// since it's new
			RunTime.badCall( "passed symbol must've been non-empty" );
		}
		RunTime.assumedNull( getInstance(
											passedEnv,
											existingDS ) );// couldn't have been already instantiated
		if ( ( passedEnv.allowDUPs_Set.hasSymbol( existingSymbol ) )
				|| ( passedEnv.allowNull_Set.hasSymbol( existingSymbol ) ) )
		{
			RunTime.bug( "inconsistent, not supposed to be in such a state" );
		}
		
		// make new list
		RunTime.assumedFalse( ListOrderedOfSymbols.internal_setRealAllowNull(
																				passedEnv,
																				existingDS,
																				allowNulls ) );
		RunTime.assumedFalse( ListOrderedOfSymbols.internal_setRealAllowDUPs(
																				passedEnv,
																				existingDS,
																				allowDUPs ) );
		internal_setAsListOrderedOfSymbols(
											passedEnv,
											existingSymbol );
		ListOrderedOfSymbols newOne = new ListOrderedOfSymbols(
																passedEnv,
																existingDS,
																allowNulls,
																allowDUPs );
		newOne.assumedValid();
		registerInstance(
							passedEnv,
							existingDS,
							newOne );
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
	public static
			ListOrderedOfSymbols
			getExistingListOOSymbols(
										Level040_DMLEnvironment passedEnv,
										Symbol existingSymbol )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								existingSymbol );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		if ( !ListOrderedOfSymbols.isListOrderedOfSymbols(
															passedEnv,
															existingSymbol ) )
		{
			RunTime.badCall( "existingSymbol did not already exist a a List" );
		}
		
		DomainSet existingDS = passedEnv.getAsDomainSet(
															existingSymbol,
															passedEnv.allElementCapsules_Set.getAsSymbol() );
		existingDS.assumedValid();
		RunTime.assumedTrue( existingDS.getAsSymbol() == existingSymbol );
		
		ListOrderedOfSymbols existingListOOS = getInstance(
															passedEnv,
															existingDS );
		if ( null == existingListOOS )
		{
			// yes it may exist as list but it was never instantiated in Java
			boolean existingAllowNull = getRealAllowNullFor(
																passedEnv,
																existingSymbol );
			boolean existingAllowDUPs = getRealAllowDUPsFor(
																passedEnv,
																existingSymbol );
			existingListOOS = new ListOrderedOfSymbols(
														passedEnv,
														existingDS,
														existingAllowNull,
														existingAllowDUPs );
			existingListOOS.assumedValid();
			registerInstance(
								passedEnv,
								existingDS,
								existingListOOS );
		}
		else
		{
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
	public static
			ListOrderedOfSymbols
			getExistingListOOSymbols(
										Level040_DMLEnvironment passedEnv,
										Symbol existingSymbol,
										boolean allowNulls,
										boolean allowDUPs )
	{
		
		RunTime.assumedNotNull(
								allowDUPs,
								allowNulls );
		ListOrderedOfSymbols ret = getExistingListOOSymbols(
																passedEnv,
																existingSymbol );
		RunTime.assumedNotNull( ret );
		if ( ret.isNullAllowed() != allowNulls )
		{
			RunTime.badCall( "inconsistency detected. Existing list's allowNull was different than expected one." );
		}
		if ( ret.isDUPAllowed() != allowDUPs )
		{
			RunTime.badCall( "inconsistency detected. Existing list's allowDUPs was different than expected one." );
		}
		return ret;
	}
	

	public static
			ListOrderedOfSymbols
			ensureListOOSymbols(
									Level040_DMLEnvironment passedEnv,
									Symbol existingSymbol,
									boolean allowNulls,
									boolean allowDUPs )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								existingSymbol,
								allowDUPs,
								allowNulls );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		if ( isListOrderedOfSymbols(
										passedEnv,
										existingSymbol ) )
		{
			// already is List, we use getExisting then
			return getExistingListOOSymbols(
												passedEnv,
												existingSymbol,
												allowNulls,
												allowDUPs );
		}
		else
		{
			// not existing, must use new then
			return getNewListOOSymbols(
										passedEnv,
										existingSymbol,
										allowNulls,
										allowDUPs );
		}
	}
	

	public
			boolean
			isDUPAllowed()
	{
		
		// the real and the cached must not change (at least) while the instance is active
		// imagine if the list has nulls and you change this to false w/o removing those nulls from list, you get
		// inconsistent list
		RunTime.assumedTrue( this.internal_getRealAllowDUPs() == cachedAllowDUPs );
		return cachedAllowDUPs;
	}
	

	/**
	 * @return
	 */
	public
			boolean
			isNullAllowed()
	{
		
		// the cached and the real must not change (at least) while the instance is active
		RunTime.assumedTrue( this.internal_getRealAllowNull() == cachedAllowNull );
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
	protected
			boolean
			isItself()
	{
		
		RunTime.assumedNotNull(
								self,
								env );
		RunTime.assumedTrue( env.isInited() );
		return isListOrderedOfSymbols(
										env,
										self.getAsSymbol() );
	}
	

	
	@Override
	public
			void
			add(
					Symbol whichSymbol,
					Position where )
	{
		
		RunTime.assumedNotNull( where );
		if ( !this.isNullAllowed() )
		{
			RunTime.assumedNotNull( whichSymbol );
		}
		switch ( where )
		{
			case FIRST:
			case LAST:
				break;
			default:
				RunTime.badCall( "unsupported position" );
		}
		if ( !this.isDUPAllowed() )
		{
			// must not already exist
			if ( this.hasSymbol( whichSymbol ) )
			{
				// exists already
				RunTime
						.badCall( "you tried to add an already existing Symbol to the list, whilst the list didn't support DUPs" );
			}
		}
		ElementCapsule ec = ElementCapsule.getNewEmptyElementCapsule(
																		env,
																		env.newUniqueSymbol() );
		RunTime.assumedNull( ec.setElement( whichSymbol ) );
		this.add_ElementCapsule(
									where,
									ec );
		this.assumedValid();
	}
	

	@Override
	public
			void
			add(
					Symbol whichSymbol,
					Position pos,
					Symbol posSymbol )
	{
		
		RunTime.assumedNotNull( pos );
		RunTime.assumedFalse( this.isDUPAllowed() );// if posSymbol exists twice...
		
		if ( !this.isNullAllowed() )
		{
			RunTime.assumedNotNull(
									whichSymbol,
									posSymbol );
		}
		
		// if ( !this.isDUPAllowed() ) {
		// if ( this.hasSymbol( whichSymbol ) ) {
		// RunTime.badCall( "the Symbol already exists and the list doesn't do DUPs" );
		// }
		// }
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC )
		{
			RunTime.badCall( "cannot find your posSymbol" );
		}
		
		ElementCapsule newEC = ElementCapsule.getNewEmptyElementCapsule(
																			env,
																			env.newUniqueSymbol() );
		RunTime.assumedNull( newEC.setElement( whichSymbol ) );
		this.add_ElementCapsule(
									newEC,
									pos,
									posEC );
		this.assumedValid();
	}
	

	@Override
	public
			boolean
			hasSymbol(
						Symbol whichSymbol )
	{
		
		if ( !this.isNullAllowed() )
		{
			RunTime.assumedNotNull( whichSymbol );
		}
		RunTime.assumedFalse( this.isDUPAllowed() );
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
	private
			ElementCapsule
			get_ElementCapsule(
								Symbol posSymbol )
	{
		
		if ( !this.isNullAllowed() )
		{
			RunTime.assumedNotNull( posSymbol );
		}
		// this method is not to be used while DUPs are allowed, because it will
		// only find first occurrence and this is not explicitly stated in
		// calling it
		RunTime.assumedFalse( this.isDUPAllowed() );
		
		self.assumedValid();
		// assuming posSymbol is already part of list, then it must have a
		// unique parent which has as parent allElementCapsules_Symbol
		// could be part of another list too, so two of those unique parents may
		// be already, and it may have other explicit parents
		ElementCapsule found = null;
		if ( ( this.isNullAllowed() ) || ( this.size() <= env.countInitials( posSymbol ) ) )
		{
			// parse entire list looking for 'posSymbol'
			ElementCapsule iter = this.get_ElementCapsule( Position.FIRST );
			
			while ( null != iter )
			{
				if ( iter.getElement() == posSymbol )
				{
					if ( null != found )
					{
						// found it again? since this is a no DUPs list => bug
						RunTime.bug( "a noDUPs list was detected to have dups" );
						// but then again, if dups are allowed this method
						// should
						// not be called?
					}
					found = iter;
					// doesn't break because we want to check consistency
				}
				iter = this.get_ElementCapsule(
												Position.AFTER,
												iter );
			}
		}
		else
		{
			Log.warn( "parsing from posSymbol upwards (ie. method 2)" );
			RunTime.assumedFalse( this.isNullAllowed() );
			RunTime.assumedNotNull( posSymbol );
			Symbol foundECAsSymbol = null;
			// this->EC->Ref2Elem->posSymbol
			// AllEC->EC
			// AllRef2Elems->Ref2Elem
			BDBVectorIterator<Symbol, Symbol> iter = env.getIterator_on_Initials_of( posSymbol );
			try
			{
				iter.goFirst();
				while ( iter.now() != null )
				{
					if ( env.allRef2ElementsInEC_Set.hasSymbol( iter.now() ) )
					{
						// so far:
						// AllRef2Elems->Ref2Elem aka iter.now()->posSymbol
						// now we check all parents of iter.now() and we must
						// find two which are this list and AllEC
						BDBVectorIterator<Symbol, Symbol> secIter = env.getIterator_on_Initials_of( iter.now() );
						try
						{
							secIter.goFirst();
							while ( secIter.now() != null )
							{
								if ( env.isVector(
													self.getAsSymbol(),
													secIter.now() ) )
								{
									if ( !env.allElementCapsules_Set.hasSymbol( secIter.now() ) )
									{
										RunTime
												.bug( "a list may not contain anything other than EC children, yet we found a non EC -> inconsistency somewhere" );
									}
									// so far we have:
									// thisList->secIter.now()
									// AllEC->secIter.now()
									// secIter.now() ->iter.now()->posSymbol
									if ( foundECAsSymbol != null )
									{
										RunTime.bug( "found 2 ! should be only 1" );
									}
									foundECAsSymbol = secIter.now();
								}
								secIter.goNext();
							}
						}
						finally
						{
							Factory.deInit( secIter );
							// secIter.deInit();
						}
						
					}
					iter.goNext();
				}
			}
			catch ( Throwable t )
			{
				RunTime.throWrapped( t );
			}
			finally
			{
				Factory.deInit( iter );
				// iter.deInit();
			}
			if ( foundECAsSymbol != null )
			{
				found = ElementCapsule.getExistingElementCapsule(
																	env,
																	foundECAsSymbol );
				found.assumedIsValidCapsule();
			}
		}
		
		return found;// null or it
	}
	

	@Override
	synchronized public
			Symbol
			get(
					Position pos )
	{
		
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
	@Override
	public
			Symbol
			get(
					Position pos,
					Symbol posSymbol )
	{
		
		RunTime.assumedNotNull( pos );
		switch ( pos )
		{// redundant checks
			case BEFORE:
			case AFTER:
				break;
			default:
				RunTime.badCall( "bad position" );
		}
		
		if ( !this.isNullAllowed() )
		{
			RunTime.assumedNotNull( posSymbol );
		}
		
		RunTime.assumedFalse( this.isDUPAllowed() );// don't call it when DUPS
		// allowed!
		
		// acquire posElementCapsule
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC )
		{
			RunTime.badCall( "cannot find the posSymbol" );
		}
		
		ElementCapsule foundEC = this.get_ElementCapsule(
															pos,
															posEC );
		Symbol fe = null;
		if ( null != foundEC )
		{
			fe = foundEC.getElement();
			if ( !this.isNullAllowed() )
			{
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
	private
			Symbol
			internalGet(
							Position pos )
	{
		
		RunTime.assumedNotNull( pos );
		switch ( pos )
		{
			case FIRST:
			case LAST:
				break;
			default:
				RunTime.badCall( "unsupported position" );
		}
		ElementCapsule ec = this.get_ElementCapsule( pos );
		Symbol ret = null;
		if ( null != ec )
		{
			ret = ec.getElement();
			if ( !this.isNullAllowed() )
			{
				RunTime.assumedNotNull( ret );
				RunTime.assumedTrue( self.hasSymbol( ec.getAsSymbol() ) );
			}
		}
		return ret;
	}
	

	@Override
	public
			void
			assumedValid()
	{
		
		super.assumedValid();
		if ( this.size() > 0 )
		{
			if ( !this.isNullAllowed() )
			{
				RunTime.assumedNotNull( this.internalGet( Position.FIRST ) );
				RunTime.assumedNotNull( this.internalGet( Position.LAST ) );
			}
		}
		
		RunTime.assumedTrue( this.internal_getRealAllowDUPs() == cachedAllowDUPs );
		RunTime.assumedTrue( this.internal_getRealAllowNull() == cachedAllowNull );
	}
	

	@Override
	protected
			void
			perItemCheck(
							ElementCapsule item )
	{
		
		super.perItemCheck( item );
		Symbol elem = item.getElement();
		if ( !this.isNullAllowed() )
		{
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
	public
			boolean
			ensure(
					Symbol whichSymbol )
	{
		
		boolean ret = this.hasSymbol( whichSymbol );
		if ( !ret )
		{
			this.add(
						whichSymbol,
						Position.LAST );
		}
		RunTime.assumedTrue( this.hasSymbol( whichSymbol ) );
		return ret;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.references.Position, org.dml.level010.Symbol)
	 */
	@Override
	public
			Symbol
			remove(
					Position pos,
					Symbol posSymbol )
	{
		
		RunTime.assumedNotNull(
								pos,
								posSymbol );
		ElementCapsule posEC = this.get_ElementCapsule( posSymbol );
		if ( null == posEC )
		{
			RunTime.badCall( "posSymbol not found" );
		}
		// ElementCapsule ec = this.get_ElementCapsule( pos, posEC );
		ElementCapsule ret = super.removeEC(
												pos,
												posEC );
		if ( null != ret )
		{
			return ret.getElement();
		}
		else
		{
			return null;
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.references.Position)
	 */
	@Override
	public
			Symbol
			remove(
					Position pos )
	{
		
		RunTime.assumedNotNull( pos );
		// switch ( pos ) {
		// case FIRST:
		// case LAST:
		// break;
		// default:
		// RunTime.badCall( "unsupported position" );
		// }
		// ElementCapsule ec = this.get_ElementCapsule( pos );
		ElementCapsule ret = super.removeEC( pos );
		if ( null != ret )
		{
			RunTime.assumedFalse( self.hasSymbol( ret.getElement() ) );
			return ret.getElement();
		}
		else
		{
			return null;
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.OrderedList#remove(org.dml.level010.Symbol)
	 */
	@Override
	public
			boolean
			remove(
					Symbol whichSymbol )
	{
		
		RunTime.assumedNotNull( whichSymbol );
		ElementCapsule ec = this.get_ElementCapsule( whichSymbol );
		RunTime.assumedTrue( this.hasElementCapsule( ec ) );
		RunTime.assumedTrue( ec.getElement() == whichSymbol );
		boolean ret = super.remove( ec );
		RunTime.assumedFalse( this.hasElementCapsule( ec ) );
		return ret;
	}
	

	/**
	 * don't use this
	 */
	@Deprecated
	@Override
	public
			boolean
			remove(
					ElementCapsule whichEC )
	{
		
		return super.remove( whichEC );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level040.ListOrderedOfElementCapsules#removeEC(org.references.Position)
	 */
	@Deprecated
	@Override
	public
			ElementCapsule
			removeEC(
						Position pos )
	{
		
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
	public
			ElementCapsule
			removeEC(
						Position pos,
						ElementCapsule posEC )
	{
		
		return super.removeEC(
								pos,
								posEC );
	}
	
	// TODO JUnit for remove
}
