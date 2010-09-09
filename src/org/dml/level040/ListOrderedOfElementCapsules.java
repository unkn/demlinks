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



import org.dml.level010.Symbol;
import org.dml.level025.DomainSet;
import org.dml.level025.SetOfTerminalSymbols;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.references.Position;



/**
 * a list where the order is maintained<br>
 * list of ElementCapsules<br>
 * will yield the same list instance for the same env/symbol tuple<br>
 * 
 * self->EC1,EC2,EC3...
 * EC=ElementCapsule
 * 
 * the same EC cannot be used in 2 listsOfEC, else lots of inconsistencies will happen<br>
 */
public class ListOrderedOfElementCapsules
{
	
	private static final TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfElementCapsules>	allListOOECInstances	= new TwoKeyHashMap<Level040_DMLEnvironment, DomainSet, ListOrderedOfElementCapsules>();
	protected final Level040_DMLEnvironment																	env;
	protected final DomainSet																				self;
	
	
	/**
	 * don't explicitly use this constructor<br>
	 * it's considered that list always exists as List even if empty, when calling this<br>
	 * 
	 * @param passedEnv
	 * @param passedSymbol
	 *            represents the list (or IS the list)
	 */
	protected ListOrderedOfElementCapsules(
			Level040_DMLEnvironment passedEnv,
			DomainSet passedDS )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedDS );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		env = passedEnv;
		self = passedDS;
		// Symbol listSymbol = l3DMLEnvironment.getSymbol(
		// Level3_DMLEnvironment.listSymbolJavaID );
		// this.internal_setName();
		// this.assumedValid();
		RunTime.assumedTrue( this.isItself() ); // isListOrderedOfECs( env, self.getAsSymbol() ) );
	}
	

	private final static
			void
			registerInstance(
								Level040_DMLEnvironment env,
								DomainSet domainSet,
								ListOrderedOfElementCapsules newOne )
	{
		
		RunTime.assumedNotNull(
								env,
								domainSet,
								newOne );
		RunTime.assumedFalse( allListOOECInstances.ensure(
															env,
															domainSet,
															newOne ) );
	}
	

	private final static
			ListOrderedOfElementCapsules
			getInstance(
							Level040_DMLEnvironment env,
							DomainSet domainSet )
	{
		
		RunTime.assumedNotNull(
								env,
								domainSet );
		return allListOOECInstances.get(
											env,
											domainSet );
	}
	

	/**
	 * @param passedEnv
	 * @param existingSymbol
	 *            can be a list already, or just a new unique symbol to be
	 *            transformed into a list
	 * @return
	 */
	public static
			ListOrderedOfElementCapsules
			getListOOEC(
							Level040_DMLEnvironment passedEnv,
							Symbol existingSymbol )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								existingSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		DomainSet existingDS = passedEnv.getAsDomainSet(
															existingSymbol,
															passedEnv.allElementCapsules_Set.getAsSymbol() );
		existingDS.assumedValid();
		
		ListOrderedOfElementCapsules existingList = getInstance(
																	passedEnv,
																	existingDS );
		if ( null == existingList )
		{
			internal_setAsListOrderedOfECs(
											passedEnv,
											existingDS.getAsSymbol() );
			existingList = new ListOrderedOfElementCapsules(
																passedEnv,
																existingDS );
			existingList.assumedValid();
			registerInstance(
								passedEnv,
								existingDS,
								existingList );
		}
		else
		{
			existingList.assumedValid();
		}
		return existingList;
	}
	

	private static
			void
			internal_setAsListOrderedOfECs(
											Level040_DMLEnvironment passedEnv,
											Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedSelf,
								passedEnv );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		RunTime.assumedFalse( passedEnv.allListsOOEC_Set.addToSet( passedSelf ) );
	}
	

	// /**
	// * override this and don't call super()
	// */
	// protected void internal_setName() {
	//
	// RunTime.assumedNotNull( self, env );
	// RunTime.assumedTrue( env.isInited() );
	// RunTime.assumedFalse( env.allListsOOEC_Set.addToSet( self.getAsSymbol() ) );
	// }
	//
	/**
	 * override this and don't call super()
	 */
	protected
			boolean
			isItself()
	{
		
		RunTime.assumedNotNull(
								self,
								env );
		RunTime.assumedTrue( env.isInitedSuccessfully() );
		return ListOrderedOfElementCapsules.isListOrderedOfECs(
																env,
																self.getAsSymbol() );
	}
	

	public static
			boolean
			isListOrderedOfECs(
								Level040_DMLEnvironment passedEnv,
								Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		return passedEnv.allListsOOEC_Set.hasSymbol( passedSelf );
	}
	

	public
			void
			assumedValid()
	{
		
		RunTime.assumedNotNull(
								env,
								self );
		RunTime.assumedTrue( env.isInitedSuccessfully() );
		
		RunTime.assumedTrue( this.isItself() );
		self.assumedValid();// all terminals of self are ECs
		
		ElementCapsule first = this.get_ElementCapsule( Position.FIRST );
		ElementCapsule last = this.get_ElementCapsule( Position.LAST );
		


		if ( null != first )
		{
			RunTime.assumedTrue( env.isVector(
												self.getAsSymbol(),
												first.getAsSymbol() ) );
			RunTime.assumedTrue( this.hasElementCapsule( first ) );
			RunTime.assumedTrue( last != null );
		}
		else
		{
			RunTime.assumedTrue( this.isEmpty() );
		}
		
		if ( null != last )
		{
			RunTime.assumedTrue( env.isVector(
												self.getAsSymbol(),
												last.getAsSymbol() ) );
			RunTime.assumedTrue( this.hasElementCapsule( last ) );
			RunTime.assumedTrue( first != null );
		}
		
	}
	

	private
			void
			internal_dmlRegisterNewHeadOrTail(
												Position pos,
												ElementCapsule candidate )
	{
		
		RunTime.assumedNotNull(
								pos,
								candidate );
		candidate.assumedIsValidCapsule();
		this.internal_dmlRegisterNewHeadOrTailAllowNull(
															pos,
															candidate );
	}
	

	/**
	 * replaces old HEAD/TAIL with the new one
	 * 
	 * @param last
	 * @param candidate
	 */
	private
			ElementCapsule
			internal_dmlRegisterNewHeadOrTailAllowNull(
														Position pos,
														ElementCapsule candidate )
	{
		
		RunTime.assumedNotNull( pos );
		// candidate.assumedIsValidCapsule();
		SetOfTerminalSymbols headOrTail = null;
		switch ( pos )
		{
			case FIRST:
				headOrTail = env.allHeads_Set;
				break;
			case LAST:
				headOrTail = env.allTails_Set;
				break;
			default:
				RunTime.badCall( "bad position for this method" );
				break;
		}
		ElementCapsule oldCandidate = this.get_ElementCapsule( pos );
		// remove prev one
		if ( null != oldCandidate )
		{
			if ( !headOrTail.remove( oldCandidate.getAsSymbol() ) )
			{
				RunTime.bug( "get_ElementCapsule(pos) must be bugged then" );
			}
		}
		
		// new one
		if ( null != candidate )
		{
			RunTime.assumedFalse( headOrTail.addToSet( candidate.getAsSymbol() ) );
		}
		return oldCandidate;
	}
	

	/**
	 * @param theNew
	 * @param pos
	 * @param posElement
	 */
	public
			void
			add_ElementCapsule(
								ElementCapsule theNew,
								Position pos,
								ElementCapsule posElement )
	{
		
		RunTime.assumedNotNull(
								theNew,
								pos,
								posElement );
		posElement.assumedIsValidCapsule();
		theNew.assumedIsValidCapsule();
		RunTime.assumedTrue( theNew.isAlone() );
		// RunTime.assumedNotNull( theNew.getElement() );
		RunTime.assumedFalse( this.hasElementCapsule( theNew ) );
		this.assumedValid();
		switch ( pos )
		{
			case BEFORE:
			case AFTER:
				// implies list has at least 1 element even if this is posElement
				// 1. we must be sure posElement is already part of this list
				RunTime.assumedTrue( this.hasElementCapsule( posElement ) );
				// RunTime.assumedFalse( posElement.isAlone() ); could be alone
				
				// list always points to all ECs
				// RunTime.assumedFalse( env.ensureVector( asSymbol,
				// theNew.getAsSymbol() ) );
				RunTime.assumedFalse( self.addToSet( theNew.getAsSymbol() ) );
				// assuming pos is AFTER, for comments only
				// get oldnext
				ElementCapsule oldOne = posElement.getSideCapsule( pos );
				if ( null == oldOne )
				{
					// then posElement is last
					RunTime.assumedTrue( this.get_ElementCapsule( Position.getAsEdge( pos ) ) == posElement );
				}
				else
				{
					// it's not the last
					// oldnext.prev=theNew
					oldOne.setSideCapsule(
											Position.opposite( pos ),
											theNew );
				}
				

				// oldOne can be null here, from above, depending on case; and is ok
				theNew.setSideCapsule(
										pos,
										oldOne );// new.next=oldnext
				
				posElement.setSideCapsule(
											pos,
											theNew );// next=theNew
				
				// new.prev=posElement
				theNew.setSideCapsule(
										Position.opposite( pos ),
										posElement );
				if ( theNew.getSideCapsule( pos ) == null )
				{
					// no prev, then make it FIRST
					this.internal_dmlRegisterNewHeadOrTail(
															Position.getAsEdge( pos ),
															theNew );
				}
				break;
			default:
				RunTime.badCall( "bad position for this method" );
				break;
		}
		this.assumedValid();
	}
	

	/**
	 * @param posElement
	 * @return
	 */
	public
			boolean
			hasElementCapsule(
								ElementCapsule posElement )
	{
		
		RunTime.assumedNotNull( posElement );
		posElement.assumedIsValidCapsule();
		RunTime.assumedNotNull( self );
		// RunTime.assumedNotNull( posElement.getElement() );
		// return env.isVector( asSymbol, posElement.getAsSymbol() );
		return self.hasSymbol( posElement.getAsSymbol() );
	}
	

	/**
	 * @param pos
	 *            FIRST or LAST only
	 * @param theNew
	 */
	public
			void
			add_ElementCapsule(
								Position pos,
								ElementCapsule theNew )
	{
		
		// TODO reverse these 2 params ^^ up
		
		this.assumedValid();
		switch ( pos )
		{
			case FIRST:
			case LAST:
				RunTime.assumedNotNull( theNew );
				theNew.assumedIsValidCapsule();
				RunTime.assumedFalse( this.hasElementCapsule( theNew ) );
				RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
				// RunTime.assumedNotNull( theNew.getElement() );
				
				// list points to all ECs it has
				if ( self.addToSet( theNew.getAsSymbol() ) )
				{
					RunTime.bug( "the link shouldn't already exist" );
				}
				// assuming pos is FIRST
				// has a last
				ElementCapsule theOld = this.get_ElementCapsule( pos );// first
				if ( null != theOld )
				{
					// has at least 1 element in list
					RunTime.assumedNotNull( theOld );// current first exists!
					
					// first.prev is null
					RunTime.assumedNull( theOld.getSideCapsule( Position.getAsNear( pos ) ) );
					
					// redundant check:
					RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
					
					// newfirst.next=oldfirst
					theNew.setSideCapsule(
											Position.opposite( Position.getAsNear( pos ) ),
											theOld );
					// oldfirst.prev=newfirst
					theOld.setSideCapsule(
											Position.getAsNear( pos ),
											theNew );
					// setFirst=newfirst
					theNew.assumedIsValidCapsule();
				}
				else
				{
					// no first then no last
					// set as Tail aka last
					this.internal_dmlRegisterNewHeadOrTail(
															Position.opposite( pos ),
															theNew );
				}
				// new HEAD, set as first
				this.internal_dmlRegisterNewHeadOrTail(
														pos,
														theNew );// common
				break;
			
			default:
				RunTime.badCall( "bad position" );
				break;
		}
		this.assumedValid();
	}
	

	public
			long
			size()
	{
		
		long ret = self.size();
		
		// if this fails then self.size() is implemented wrongly
		RunTime.assumedTrue( env.countTerminals( self.getAsSymbol() ) == ret );
		
		RunTime.assumedTrue( ret >= 0 );
		return ret;
	}
	

	public
			boolean
			isEmpty()
	{
		
		RunTime.assumedTrue( this.get_ElementCapsule( Position.FIRST ) == null );
		RunTime.assumedTrue( this.get_ElementCapsule( Position.LAST ) == null );
		return this.size() == 0;
	}
	

	/**
	 * @param pos
	 *            FIRST /LAST
	 * @return
	 */
	public
			ElementCapsule
			get_ElementCapsule(
								Position pos )
	{
		
		RunTime.assumedNotNull( pos );
		SetOfTerminalSymbols sym = null;
		switch ( pos )
		{
			case FIRST:
				sym = env.allHeads_Set;
				break;
			case LAST:
				sym = env.allTails_Set;
				break;
			default:
				RunTime.badCall( "bad position for this method" );
				break;// unreachable code
		}
		Symbol x = env.findCommonTerminalForInitials(
														sym.getAsSymbol(),
														self.getAsSymbol() );
		if ( null != x )
		{// found one
			// existing ElementCapsule, wrap it in ElementCapsule type
			ElementCapsule ec = ElementCapsule.getExistingElementCapsule(
																			env,
																			x );
			return ec;
		}
		return null;// found none
	}
	

	public
			ElementCapsule
			get_ElementCapsule(
								Position pos,
								ElementCapsule posEC )
	{
		
		RunTime.assumedNotNull(
								pos,
								posEC );
		switch ( pos )
		{
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
	

	public
			Symbol
			getAsSymbol()
	{
		
		this.assumedValid();
		RunTime.assumedNotNull( self );
		RunTime.assumedNotNull( self.getAsSymbol() );
		return self.getAsSymbol();
	}
	

	/**
	 * 
	 */
	public
			void
			checkIntegrity()
	{
		
		// parse all
		int netSize = 0;
		ElementCapsule current = this.get_ElementCapsule( Position.FIRST );
		while ( null != current )
		{
			netSize++;
			this.perItemCheck( current );
			// RunTime.assumedNotNull( current.getElement() );
			current = this.get_ElementCapsule(
												Position.AFTER,
												current );
		}
		RunTime.assumedTrue( netSize == this.size() );
	}
	

	protected
			void
			perItemCheck(
							ElementCapsule item )
	{
		
		item.assumedIsValidCapsule();
		RunTime.assumedTrue( self.hasSymbol( item.getAsSymbol() ) );
	}
	

	/**
	 * @param pos
	 * @return the removed EC or null if nothing was removed
	 */
	public
			ElementCapsule
			removeEC(
						Position pos )
	{
		
		RunTime.assumedNotNull( pos );
		
		ElementCapsule toRemove = this.get_ElementCapsule( pos );
		if ( null != toRemove )
		{
			RunTime.assumedTrue( this.remove( toRemove ) );
		}
		return toRemove;// yeah can be null
	}
	

	/**
	 * @param pos
	 * @param posEC
	 * @return the removed EC or null if nothing was removed
	 */
	public
			ElementCapsule
			removeEC(
						Position pos,
						ElementCapsule posEC )
	{
		
		RunTime.assumedNotNull(
								pos,
								posEC );
		if ( !this.hasElementCapsule( posEC ) )
		{
			RunTime.badCall();
		}
		
		ElementCapsule toRemove = this.get_ElementCapsule(
															pos,
															posEC );
		if ( null != toRemove )
		{
			RunTime.assumedTrue( this.remove( toRemove ) );
			RunTime.assumedFalse( self.hasSymbol( toRemove.getAsSymbol() ) );
		}
		return toRemove;// yeah can be null
	}
	

	/**
	 * self->whichEC<br>
	 * maybe also:<br>
	 * allTails->whichEC<br>
	 * allHead->whichEC<br>
	 * 
	 * @param whichEC
	 * @return true if existed
	 */
	public
			boolean
			remove(
					ElementCapsule whichEC )
	{
		
		RunTime.assumedNotNull( whichEC );
		if ( !this.hasElementCapsule( whichEC ) )
		{
			return false;// already inexistent
		}
		whichEC.assumedIsValidCapsule();
		
		ElementCapsule prev = whichEC.getSideCapsule( Position.BEFORE );
		ElementCapsule next = whichEC.getSideCapsule( Position.AFTER );
		if ( ( null == prev )
				&& ( null == next ) )
		{
			// clear both head&tail
			// both head/tail were pointing to whichEC ofc, in this case
			// we now remove them, so the EClist is empty
			RunTime.assumedTrue( this.internal_dmlRegisterNewHeadOrTailAllowNull(
																					Position.FIRST,
																					null ) == whichEC );
			RunTime.assumedTrue( this.internal_dmlRegisterNewHeadOrTailAllowNull(
																					Position.LAST,
																					null ) == whichEC );
		}
		else
		{
			if ( null == prev )
			{
				// this one must be first then, consistency check:
				// RunTime.assumedTrue( this.get_ElementCapsule( Position.FIRST ) == whichEC );
				// need to modify Head points to next which isn't null
				RunTime.assumedNotNull( next );
				// whichEC was already head too
				RunTime.assumedTrue( this.internal_dmlRegisterNewHeadOrTailAllowNull(
																						Position.FIRST,
																						next ) == whichEC );
			}
			else
			{
				prev.setSideCapsule(
										Position.AFTER,
										next );// next can be null
			}
			if ( null == next )
			{
				// this one must be last then, consistency check:
				// RunTime.assumedTrue( this.get_ElementCapsule( Position.LAST ) == whichEC );
				// need to modify Tail to point to prev which isn't null
				RunTime.assumedNotNull( prev );
				// whichEC was tail already
				RunTime.assumedTrue( this.internal_dmlRegisterNewHeadOrTailAllowNull(
																						Position.LAST,
																						prev ) == whichEC );
			}
			else
			{
				next.setSideCapsule(
										Position.BEFORE,
										prev );// prev an be null
			}
		}// if
		
		RunTime.assumedTrue( self.remove( whichEC.getAsSymbol() ) );// existed
		
		return true;// existed and was removed
	}
	// TODO JUnit for remove
}
