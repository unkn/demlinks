/**
 * File creation: Oct 20, 2009 12:36:25 AM
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


package org.dml.tools;



import org.javapart.logger.Log;
import org.references.ObjRefsList;
import org.references.Position;



/**
 * you must not use a constructor when creating new instance, instead implement
 * a getNew() static method and use init() inside it
 * 
 */
public abstract class StaticInstanceTracker {
	
	// LIFO list tracking
	private final static ObjRefsList<StaticInstanceTracker>	ALL_INSTANCES	= new ObjRefsList<StaticInstanceTracker>();
	private boolean											inited			= false;
	
	/**
	 * do not use this outside
	 */
	protected StaticInstanceTracker() {

	}
	
	/**
	 * LIFO manner deinit
	 */
	public static void deInitAll() {

		StaticInstanceTracker iter;
		while ( null != ( iter = ALL_INSTANCES.getObjectAt( Position.FIRST ) ) ) {
			iter.deInit();
		}
	}
	
	/**
	 * implement this done(), but use deInit() instead
	 */
	protected abstract void done();
	
	/**
	 * do not use <code>this</code> again after calling this method
	 */
	public void deInit() {

		if ( !inited ) {
			RunTime.Bug( this.toString() + " was never inited properly" );
		}
		this.done();
		inited = false;
		removeOldInstance( this );
	}
	
	public void init() {

		if ( inited ) {
			RunTime.Bug( "already inited" );
		}
		addNewInstance( this );
		inited = true;
	}
	
	private static void addNewInstance( StaticInstanceTracker instance ) {

		if ( ALL_INSTANCES.addFirst( instance ) ) {
			RunTime.Bug( "should not have existed" );
		}
	}
	
	private static void removeOldInstance( StaticInstanceTracker instance ) {

		Log.entry( instance.toString() );
		if ( !ALL_INSTANCES.removeObject( instance ) ) {
			RunTime.Bug( "should've existed" );
		}
	}
}
