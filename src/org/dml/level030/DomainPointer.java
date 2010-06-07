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
import org.dml.level025.SetOfTerminalSymbols;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;



/**
 * will allow pointing only to terminals of domain<br>
 * basically this is a Vector with max. one terminal; can have 0 though<br>
 * the rules are enforced only in java, the db will have no knowledge of the
 * domain or AllowNull<br>
 */
public class DomainPointer extends Pointer {
	
	private static final TwoKeyHashMap<Level030_DMLEnvironment, SetOfTerminalSymbols, DomainPointer>	allDomainPointerInstances	= new TwoKeyHashMap<Level030_DMLEnvironment, SetOfTerminalSymbols, DomainPointer>();
	// allowed to point only to terminals of domain
	Symbol																								domain						= null;
	
	/**
	 * @param passedEnv
	 * @param passedSelf
	 */
	protected DomainPointer( Level030_DMLEnvironment passedEnv, SetOfTerminalSymbols passedSelf ) {

		super( passedEnv, passedSelf );
	}
	
	/**
	 * @param passedDomain
	 * @param pointTo
	 * @return
	 */
	public static DomainPointer getNewNonNullDomainPointer( Level030_DMLEnvironment passedEnv, Symbol passedDomain,
			Symbol pointTo ) {

		RunTime.assumedNotNull( passedEnv, passedDomain, pointTo );
		Symbol newSymbol = passedEnv.newUniqueSymbol();
		SetOfTerminalSymbols newSet = passedEnv.getAsSet( newSymbol );
		DomainPointer ret = new DomainPointer( passedEnv, newSet );
		ret.setDomain( passedDomain );
		ret.pointTo( pointTo );
		ret.setAllowNull( false );
		ret.assumedValid();
		RunTime.assumedTrue( ret.getAsSymbol() == newSymbol );
		registerDPInstance( passedEnv, newSet, ret );
		return ret;
	}
	
	private final static void registerDPInstance( Level030_DMLEnvironment env, SetOfTerminalSymbols name,
			DomainPointer newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allDomainPointerInstances.ensure( env, name, newOne ) );
	}
	
	private final static DomainPointer getDPInstance( Level030_DMLEnvironment env, SetOfTerminalSymbols name ) {

		RunTime.assumedNotNull( env, name );
		return allDomainPointerInstances.get( env, name );
	}
	
	
	public static DomainPointer getNewNullDomainPointer( Level030_DMLEnvironment passedEnv, Symbol passedDomain ) {

		RunTime.assumedNotNull( passedEnv, passedDomain );
		Symbol name = passedEnv.newUniqueSymbol();
		SetOfTerminalSymbols newSet = passedEnv.getAsSet( name );
		DomainPointer ret = new DomainPointer( passedEnv, newSet );
		ret.setDomain( passedDomain );
		ret.setAllowNull( true );
		ret.assumedValid();
		RunTime.assumedTrue( ret.getAsSymbol() == name );
		registerDPInstance( passedEnv, newSet, ret );
		return ret;
	}
	
	
	/**
	 * @param level3DMLEnvironment
	 * @param passedSelf
	 * @param passedDomain
	 * @return
	 */
	public static DomainPointer getExistingDomainPointer( Level030_DMLEnvironment passedEnv, Symbol passedSelf,
			Symbol passedDomain, boolean passedAllowNull ) {

		RunTime.assumedNotNull( passedEnv, passedSelf, passedDomain, passedAllowNull );
		
		SetOfTerminalSymbols existingSet = passedEnv.getAsSet( passedSelf );
		DomainPointer existingOne = getDPInstance( passedEnv, existingSet );
		if ( null != existingOne ) {
			if ( existingOne.allowNull != passedAllowNull ) {
				RunTime.badCall( "already existing DP had different AllowNull setting" );
			}
			if ( existingOne.domain != passedDomain ) {
				RunTime.badCall( "already existed for this Symbol, but with different domain" );
			}
			existingOne.assumedValid();
			return existingOne;
		}
		DomainPointer ret = new DomainPointer( passedEnv, existingSet );
		ret.setDomain( passedDomain );
		ret.setAllowNull( passedAllowNull );
		ret.assumedValid();
		registerDPInstance( passedEnv, existingSet, ret );
		return ret;
	}
	
	/**
	 * @param newDomain
	 * @return the old Domain, if any, or null
	 */
	public Symbol setDomain( Symbol newDomain ) {

		RunTime.assumedNotNull( newDomain );
		RunTime.assumedFalse( selfAsSet.getAsSymbol() == newDomain );
		Symbol old = this.getDomain();// or null
		if ( null != old ) {
			// first time set domain
			Symbol pointee = this.getPointee();
			if ( null != pointee ) {
				// well we already have a pointee, we need to make sure it's
				// from
				// the NEW domain
				if ( !this.isValidDomainPointeeTuple( newDomain, pointee ) ) {
					RunTime.badCall( "the new domain is incompatible with the already existing pointee. "
							+ "Maybe remove the pointee before you set the domain." );
				}
			}
		}
		domain = newDomain;
		this.assumedValid();
		return old;
	}
	
	public boolean isValidDomainPointeeTuple( Symbol domain1, Symbol pointee ) {

		RunTime.assumedNotNull( domain1 );
		if ( !allowNull ) {
			RunTime.assumedNotNull( pointee );
		}
		RunTime.assumedFalse( selfAsSet.getAsSymbol() == domain1 );
		return envL2.isVector( domain1, pointee );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level3.Pointer#assumedValid()
	 */
	@Override
	public void assumedValid() {

		super.assumedValid();
		RunTime.assumedFalse( selfAsSet.getAsSymbol() == domain );
		Symbol pointee = this.internal_getPointee();
		if ( null != pointee ) {
			RunTime.assumedTrue( this.isValidDomainPointeeTuple( domain, pointee ) );
		}
	}
	
	/**
	 * @return
	 */
	public Symbol getDomain() {

		return domain;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level3.Pointer#pointTo(org.dml.level1.Symbol)
	 */
	@Override
	public Symbol pointTo( Symbol toWhat ) {

		this.assumedValid();
		if ( !allowNull ) {
			RunTime.assumedNotNull( toWhat );
			if ( !this.isValidDomainPointeeTuple( domain, toWhat ) ) {
				RunTime.badCall( "new pointee not from domain, you insipid bugger! :D" );
			}
		}
		
		return super.pointTo( toWhat );
	}
	

}
