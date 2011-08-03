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



import org.dml.level010.*;
import org.dml.level025.*;
import org.dml.tools.*;



/**
 * handling pointer(s) (that is a vector with 0 or 1 terminals)
 * 
 */
public class Level030_DMLEnvironment extends Level025_DMLEnvironment {
	
	public Pointer getExistingPointer( final Symbol name2, final boolean allowNull ) {
		
		RunTime.assumedNotNull( name2 );
		return Pointer.getExistingPointer( this, name2, allowNull );
	}
	
	
	/**
	 * @return
	 */
	public Pointer getNewNonNullPointer( final Symbol pointTo ) {
		
		RunTime.assumedNotNull( pointTo );
		return Pointer.getNewNonNullPointer( this, pointTo );
	}
	
	
	/**
	 * @return
	 */
	public Pointer getNewNullPointer() {
		
		return Pointer.getNewNullPointer( this );
	}
	
	
	/**
	 * @param domain
	 * @param pointTo
	 * @return
	 */
	public DomainPointer getNewNonNullDomainPointer( final Symbol domain, final Symbol pointTo ) {
		
		RunTime.assumedNotNull( domain, pointTo );
		return DomainPointer.getNewNonNullDomainPointer( this, domain, pointTo );
	}
	
	
	/**
	 * @param domain
	 * @return
	 */
	public DomainPointer getNewNullDomainPointer( final Symbol domain ) {
		
		RunTime.assumedNotNull( domain );
		return DomainPointer.getNewNullDomainPointer( this, domain );
	}
	
	
	public DomainPointer getExistingDomainPointer( final Symbol self, final Symbol domain, final boolean allowNull ) {
		
		RunTime.assumedNotNull( self, domain );
		return DomainPointer.getExistingDomainPointer( this, self, domain, allowNull );
	}
	
}
