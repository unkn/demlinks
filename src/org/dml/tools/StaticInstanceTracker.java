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
 * 
 * 1. implement start() and done() but call init() and deInit() instead; because
 * init() calls start() inside it; same for deInit() calling done()<br>
 * 2. use constructor to create, and then call init()<br>
 * 3. sometime when done with it, you have to use deInit()<br>
 * 4. use deInitAll() in a finally block just in case some exception is going
 * to shutdown the application
 * you can init() again but only if u previously used deInit()<br>
 * NEVER call init() inside a constructor<br>
 * FIXME: problem is that we need to pass params to init() but atm it's final
 */
public abstract class StaticInstanceTracker {
	
	// LIFO list tracking
	public final static ObjRefsList<StaticInstanceTracker>	ALL_INSTANCES	= new ObjRefsList<StaticInstanceTracker>();
	private boolean											inited			= false;
	private boolean											deInited		= true;
	
	public StaticInstanceTracker() {

	}
	
	/**
	 * LIFO manner deinit
	 */
	public final static void deInitAll() {

		Log.entry();
		StaticInstanceTracker iter;
		while ( null != ( iter = ALL_INSTANCES.getObjectAt( Position.FIRST ) ) ) {
			iter.deInit();
		}
		RunTime.assertTrue( ALL_INSTANCES.isEmpty() );
	}
	
	/**
	 * implement this start(), but use init() instead
	 */
	protected abstract void start();
	
	/**
	 * implement this done(), but use deInit() instead
	 */
	protected abstract void done();
	
	/**
	 * do not use <code>this</code> again after calling this method
	 * 
	 * @return
	 */
	public final void deInit() {

		if ( !inited ) {
			RunTime.BadCallError( this.toString()
					+ " was not already init()-ed" );
		}
		if ( deInited ) {
			
			RunTime.BadCallError( this + " was already deInit()-ed!" );
		}
		
		deInited = true;
		inited = false;
		removeOldInstance( this );
		this.done();
	}
	
	public final void init() {

		if ( inited || !deInited ) {
			RunTime.BadCallError( "already inited" );
		}
		addNewInstance( this );
		inited = true;
		deInited = false;
		this.start();
	}
	
	private final static void addNewInstance( StaticInstanceTracker instance ) {

		if ( ALL_INSTANCES.addFirst( instance ) ) {
			RunTime.Bug( "should not have existed" );
		}
	}
	
	private final static void removeOldInstance( StaticInstanceTracker instance ) {

		Log.entry( instance.toString() );
		if ( !ALL_INSTANCES.removeObject( instance ) ) {
			RunTime.Bug( "should've existed" );
		}
	}
}
