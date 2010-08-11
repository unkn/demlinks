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



package org.dml.tools;



import org.references.method.MethodParams;



/**
 * 
 *
 */
public class ProperSubClassingOfSIT extends Initer {
	
	public ProperSubClassingOfSIT() {

		// should be empty
	}
	
	@Override
	protected void start( MethodParams params ) {

		System.err.println( this.getClass().getCanonicalName() + " is initializing." );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@SuppressWarnings( "unused" )
	@Override
	protected void done( MethodParams params ) {

		System.err.println( this.getClass().getCanonicalName() + " is preparing to finish....step 1/2" );
		if ( true ) {
			throw new RuntimeException();
		}
		System.err.println( this.getClass().getCanonicalName() + " is done. step 2/2" );
	}
	
	/**
	 * 
	 */
	public void exec() {

		System.err.println( this.getClass().getCanonicalName() + " is executing...." );
		
	}
	
}
