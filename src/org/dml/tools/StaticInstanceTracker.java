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
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.method.MethodParams;



/**
 * 
 * 1. implement start() and done() but call init() and deInit() instead; because
 * init() calls start() inside it; same for deInit() calling done()<br>
 * 2. use constructor to create, and then call init() don't call init() from
 * within the constructor<br>
 * 3. sometime when done with it, you have to use deInit()<br>
 * 4. use deInitAll() in a finally block just in case some exception is going
 * to shutdown the application
 * you can init() again but only if u previously used deInit()<br>
 * NEVER call init() inside a constructor<br>
 * FIXME: problem is that we need to pass params to init() but atm it's final
 */
public abstract class StaticInstanceTracker {
	
	// LIFO list tracking all instances of ALL subclasses
	private final static ListOfUniqueNonNullObjects<StaticInstanceTracker>	ALL_INSTANCES	= new ListOfUniqueNonNullObjects<StaticInstanceTracker>();
	private boolean															inited			= false;
	private MethodParams<Object>											formerParams	= null;
	
	/**
	 * @param inited1
	 *            the inited to set
	 */
	private final void setInited( boolean inited1 ) {

		inited = inited1;
	}
	
	

	/**
	 * @return the inited
	 */
	public final boolean isInited() {

		return inited;
	}
	
	

	/**
	 * constructor
	 */
	public StaticInstanceTracker() {

	}
	
	

	/**
	 * implement this start(), but use init() instead
	 */
	protected abstract void start( MethodParams<Object> params );
	
	/**
	 * the params will be cloned (or copied) to be used by reInit()
	 * 
	 * @param params
	 *            null or the params
	 */
	public final void init( MethodParams<Object> params ) {

		if ( this.isInited() ) {
			RunTime.badCall( "already inited, you must deInit() before calling init(...) again" );
		}
		addNewInstance( this );
		this.setInited( true );
		
		if ( params != formerParams ) {
			// NOT called by reInit()
			if ( null != formerParams ) {
				// was used before, we discard the one before
				formerParams.deInit();
				formerParams = null;
			}
			
			if ( null != params ) {// we get a copy of passed params
				// this does init(null) inside
				formerParams = params.getClone();
			} // else is null
		} // else called by reInit() we don't mod them
		this.start( formerParams );
	}
	
	/**
	 * this will call deInit() and then init(params) where params are the last
	 * used params which were saved/cloned internally
	 */
	public final void reInit() {

		this.deInit();
		this.init( formerParams );
	}
	
	/**
	 * implement this done(), but use deInit() instead<br>
	 * the parameters that were passed to init(params) will be passed to this
	 * done(...) and yes they were saved(or cloned)<br>
	 * deInit() is passing them to done() not you<br>
	 * but this means you can access them in your own done(..) implementation<br>
	 * try to not modify the contents of params... since they will be used on
	 * reInit() or well maybe it won't matter anymore<br>
	 */
	protected abstract void done( MethodParams<Object> params );
	
	/**
	 * do not use <code>this</code> again after calling this method
	 * 
	 * @return
	 */
	public final void deInit() {

		if ( !this.isInited() ) {
			RunTime.badCall( this.toString() + " was not already init()-ed" );
		}
		
		this.deInitSilently();
	}
	
	/**
	 * this will not except if already deInit()-ed
	 * 
	 * @see #deInit()
	 */
	public final void deInitSilently() {

		if ( this.isInited() ) {
			this.setInited( false );
			removeOldInstance( this );
			this.done( formerParams );
			// formerParams are not managed here, only on init() ie. discarded
		}
	}
	
	
	/**
	 * LIFO manner deinit, only for the same class type<br>
	 * instances with same class name as this instance, will be deinited<br>
	 */
	public final void deInitAllLikeMe() {

		Log.entry();
		StaticInstanceTracker iter = ALL_INSTANCES.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			StaticInstanceTracker next = ALL_INSTANCES.getObjectAt(
					Position.AFTER, iter );
			if ( this.getClass() == iter.getClass() ) {
				iter.deInit();
				// need to reparse list because maybe next item disappeared due
				// to prev deInit() call
				next = ALL_INSTANCES.getObjectAt( Position.FIRST );
			}
			
			iter = next;
		}
		// it can be not empty here, since it holds list of ALL subclasses'
		// instances
		// RunTime.assertTrue( ALL_INSTANCES.isEmpty() );
	}
	
	/**
	 * LIFO manner deinit, for ALL class types<br>
	 * you may consider this as silent, as it won't deInit those that are
	 * already deInited since they're not in the list anymore<br>
	 * this will deinit all instances that were ever inited, no matter how
	 * different they are; different name classes included<br>
	 * *Be careful using this in other places than before quitting the program,
	 * because ie. if u have an instance that inits another instance and the
	 * latter gets deInit-ed first then the former when deInit-ing will fail
	 * with 'not already inited' when it tries to deInit what it inited<br>
	 */
	public static final void deInitAllThatExtendMe() {

		Log.entry();
		StaticInstanceTracker iter;
		while ( null != ( iter = ALL_INSTANCES.getObjectAt( Position.LAST ) ) ) {
			// System.out.println( iter.getClass().getSimpleName() + " / " +
			// iter
			// + "!!!!!!" + ALL_INSTANCES.size() );
			iter.deInit();
		}
		RunTime.assertTrue( ALL_INSTANCES.isEmpty() );
	}
	
	
	private final static void addNewInstance( StaticInstanceTracker instance ) {

		if ( ALL_INSTANCES.addFirstQ( instance ) ) {
			RunTime.bug( "should not have existed" );
		}
	}
	
	private final static void removeOldInstance( StaticInstanceTracker instance ) {

		Log.entry( instance.toString() );
		if ( !ALL_INSTANCES.removeObject( instance ) ) {
			RunTime.bug( "should've existed" );
		}
	}
	
}
