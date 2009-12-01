/**
 * File creation: Nov 25, 2009 12:40:36 AM
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



import org.dml.level1.Symbol;
import org.dml.level2.Level2_DMLEnvironment;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;



/**
 * will allow pointing only to terminals of domain<br>
 * basically this is a Vector with max. one terminal; can have 0 though
 */
public class DomainPointer extends Pointer {
	
	private static final TwoKeyHashMap<Level2_DMLEnvironment, Symbol, DomainPointer>	allDomainPointerInstances	= new TwoKeyHashMap<Level2_DMLEnvironment, Symbol, DomainPointer>();
	// allowed to point only to terminals of domain
	Symbol																				domain						= null;
	
	/**
	 * @param l2dml
	 * @param selfName
	 */
	protected DomainPointer( Level2_DMLEnvironment l2dml, Symbol selfName ) {

		super( l2dml, selfName );
	}
	
	/**
	 * @param domain2
	 * @param pointTo
	 * @return
	 */
	public static DomainPointer getNewNonNullPointer(
			Level2_DMLEnvironment l2DML, Symbol domain2, Symbol pointTo ) {

		RunTime.assumedNotNull( l2DML, domain2, pointTo );
		Symbol name = l2DML.newUniqueSymbol();
		DomainPointer ret = new DomainPointer( l2DML, name );
		ret.setDomain( domain2 );
		ret.pointTo( pointTo );
		ret.setAllowNull( false );
		ret.assumedValid();
		RunTime.assumedTrue( ret.getAsSymbol() == name );
		registerDPInstance( l2DML, name, ret );
		return ret;
	}
	
	private final static void registerDPInstance( Level2_DMLEnvironment env,
			Symbol name, DomainPointer newOne ) {

		RunTime.assumedNotNull( env, name, newOne );
		RunTime.assumedFalse( allDomainPointerInstances.ensure( env, name,
				newOne ) );
	}
	
	private final static DomainPointer getDPInstance(
			Level2_DMLEnvironment env, Symbol name ) {

		RunTime.assumedNotNull( env, name );
		return allDomainPointerInstances.get( env, name );
	}
	
	
	public static DomainPointer getNewNullPointer( Level2_DMLEnvironment l2DML,
			Symbol domain2 ) {

		RunTime.assumedNotNull( l2DML, domain2 );
		Symbol name = l2DML.newUniqueSymbol();
		DomainPointer ret = new DomainPointer( l2DML, name );
		ret.setDomain( domain2 );
		ret.setAllowNull( true );
		ret.assumedValid();
		RunTime.assumedTrue( ret.getAsSymbol() == name );
		registerDPInstance( l2DML, name, ret );
		return ret;
	}
	
	
	/**
	 * @param level3DMLEnvironment
	 * @param self
	 * @param domain2
	 * @return
	 */
	public static DomainPointer getExistingDomainPointer(
			Level2_DMLEnvironment level2DMLEnvironment, Symbol self,
			Symbol domain2, boolean allowNull ) {

		RunTime.assumedNotNull( level2DMLEnvironment, self, domain2, allowNull );
		DomainPointer existingOne = getDPInstance( level2DMLEnvironment, self );
		if ( null != existingOne ) {
			return existingOne;
		}
		DomainPointer ret = new DomainPointer( level2DMLEnvironment, self );
		ret.setDomain( domain2 );
		ret.setAllowNull( allowNull );
		ret.assumedValid();
		return ret;
	}
	
	/**
	 * @param newDomain
	 * @return the old Domain, if any, or null
	 */
	public Symbol setDomain( Symbol newDomain ) {

		RunTime.assumedNotNull( newDomain );
		RunTime.assumedFalse( self == newDomain );
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
		RunTime.assumedFalse( self == domain1 );
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
		RunTime.assumedFalse( self == domain );
		Symbol pointee = this.internal_getPointee();
		if ( null != pointee ) {
			RunTime.assumedTrue( this.isValidDomainPointeeTuple( domain,
					pointee ) );
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
