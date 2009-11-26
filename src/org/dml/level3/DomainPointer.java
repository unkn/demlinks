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



/**
 * will allow pointing only to terminals of domain<br>
 * basically this is a Vector with max. one terminal; can have 0 though
 */
public class DomainPointer extends Pointer {
	
	// allowed to point only to terminals of domain
	Symbol	domain	= null;
	
	/**
	 * @param l2dml
	 * @param selfName
	 */
	public DomainPointer( Level2_DMLEnvironment l2dml, Symbol selfName ) {

		super( l2dml, selfName );
	}
	
	/**
	 * @param newDomain
	 * @return the old Domain, if any, or null
	 */
	public Symbol setDomain( Symbol newDomain ) {

		RunTime.assumedNotNull( newDomain );
		this.assumedValid();
		RunTime.assumedFalse( self.equals( newDomain ) );
		Symbol old = this.getDomain();// or null
		Symbol pointee = this.getPointee();
		if ( null != pointee ) {
			// well we already have a pointee, we need to make sure it's from
			// the NEW domain
			if ( !this.isValidDomainPointeeTuple( newDomain, pointee ) ) {
				RunTime.badCall( "the new domain is incompatible with the already existing pointee. "
						+ "Maybe remove the pointee before you set the domain." );
			}
		}
		domain = newDomain;
		this.assumedValid();
		return old;
	}
	
	public boolean isValidDomainPointeeTuple( Symbol domain1, Symbol pointee ) {

		RunTime.assumedNotNull( domain1, pointee );
		RunTime.assumedFalse( self.equals( domain1 ) );
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
		RunTime.assumedFalse( self.equals( domain ) );
		Symbol pointee = this.getPointee();
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

		RunTime.assumedNotNull( toWhat );
		this.assumedValid();
		if ( !this.isValidDomainPointeeTuple( domain, toWhat ) ) {
			RunTime.badCall( "new pointee not from domain, you insipid bugger! :D" );
		}
		return super.pointTo( toWhat );
	}
	
}
