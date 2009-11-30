/**
 * File creation: Oct 19, 2009 11:39:43 PM
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
import org.references.method.MethodParams;



/**
 * handling pointer(s) (that is a vector with 0 or 1 terminals)
 * 
 */
public class Level3_DMLEnvironment extends Level2_DMLEnvironment implements
		Level3_DMLStorageWrapper {
	
	@Override
	protected void start( MethodParams<Object> params ) {

		// this method is not needed, but it's here for clarity
		super.start( params );
	}
	
	// TODO add methods to handle DomainPointer
	
	public Pointer getExistingPointer( Symbol name2, boolean allowNull ) {

		RunTime.assumedNotNull( name2, allowNull );
		return Pointer.getExistingPointer( this, name2, allowNull );
	}
	
	/**
	 * @return
	 */
	public Pointer getNewNonNullPointer( Symbol pointTo ) {

		RunTime.assumedNotNull( pointTo );
		return Pointer.getNewNonNullPointer( this, pointTo );
	}
	
	/**
	 * @return
	 */
	public Pointer getNewNullPointer() {

		return Pointer.getNewNullPointer( this );
	}
	
}
