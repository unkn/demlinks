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



import org.javapart.logger.Log;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.method.MethodParams;



/**
 * 1. implement start() and done() but call init() and deInit() instead; because
 * init() calls start() inside it; same for deInit() calling done()<br>
 * 2. use constructor to create, and then call init() don't call init() from
 * within the constructor<br>
 * NEVER call init() inside a constructor<br>
 * 3. sometime when done with it, you have to use deInit()<br>
 * 4. sometime when done with it, you have to use deInit()<br>
 * you can init() again but only if u previously used deInit()<br>
 * 5. you can use restart() which does deInit and init again with original
 * params<br>
 * 6. or use reInit() if you already used deInit(), and it will use the original
 * params to init it again<br>
 */
public abstract class StaticInstanceTracker {
	
	// LIFO list tracking all instances of ALL subclasses
	private final static ListOfUniqueNonNullObjects<StaticInstanceTracker>	ALL_INSTANCES	= new ListOfUniqueNonNullObjects<StaticInstanceTracker>();
	private boolean															inited			= false;
	private MethodParams													formerParams	= null;
	
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
	 * implement this start(), but use init() instead<br>
	 * the params here are already cloned from those passed to init(params)<br>
	 * isInited() will return true while in start() and it will remain true even if start() throws<br>
	 */
	protected abstract void start( MethodParams params );
	
	/**
	 * the params will be cloned (or copied) to be used by reInit()<br>
	 * 
	 * @param params
	 *            null or the params
	 */
	public final void init( MethodParams params ) {

		if ( this.isInited() ) {
			RunTime.badCall( "already inited, you must deInit() before calling init(...) again" );
		}
		addNewInstance( this );
		this.setInited( true );
		// try {
		
		if ( params != formerParams ) {
			// NOT called by reInit() or restart()
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
		// } finally {
		// this.setInited( true );
		// }
	}
	
	// public MethodParams<Object> getInitParams() {
	//
	// return formerParams;
	// }
	
	/**
	 * this will call deInit() and then init(params) where params are the last
	 * used params which were saved/cloned internally
	 */
	public final void restart() {

		this.deInit();
		this.init( formerParams );
	}
	
	/**
	 * reInit with original params, can only be used if not already inited
	 */
	public final void reInit() {

		if ( this.isInited() ) {
			RunTime.badCall( "already inited. Maybe you wanted to use restart()" );
		}
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
	 * 
	 * isInited() will be true while in done(), but even if done() throws, it will be set to false after a call to
	 * done() !!
	 */
	protected abstract void done( MethodParams params );
	
	/**
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
			try {
				removeOldInstance( this );
				this.done( formerParams );
				// formerParams are not managed here, only on init() ie. discarded
			} finally {
				this.setInited( false ); // ignore this:don't move this below .done() because .done() may throw
			}
		}
	}
	
	
	@Deprecated
	/**
	 * LIFO manner deinit, only for the same class type<br>
	 * instances with same class name as this instance, will be deinited<br>
	 */
	public final void deInitAllLikeMe() {

		Log.entry();
		StaticInstanceTracker iter = ALL_INSTANCES.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			StaticInstanceTracker next = ALL_INSTANCES.getObjectAt( Position.AFTER, iter );
			if ( this.getClass() == iter.getClass() ) {
				iter.deInit();
				// need to reparse list because maybe next item disappeared due
				// to prev deInit() call; ie. current deInit called a
				// child.deInit and thus 2 disappeared from ALL_INSTANCES list
				next = ALL_INSTANCES.getObjectAt( Position.FIRST );
			}
			
			iter = next;
		}
		// it can be not empty here, since it holds list of ALL subclasses'
		// instances
		// RunTime.assertTrue( ALL_INSTANCES.isEmpty() );
	}
	
	@Deprecated
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
		while ( null != ( iter = ALL_INSTANCES.getObjectAt( Position.FIRST ) ) ) {
			// System.out.println( iter.getClass().getSimpleName() + " / " +
			// iter
			// + "!!!!!!" + ALL_INSTANCES.size() );
			iter.deInit();
			// the list may decrease by more than 1 element, more might be
			// removed after the above call
		}
		RunTime.assumedTrue( ALL_INSTANCES.isEmpty() );
	}
	
	
	private final static void addNewInstance( StaticInstanceTracker instance ) {

		RunTime.assumedNotNull( instance );
		if ( ALL_INSTANCES.addFirstQ( instance ) ) {
			RunTime.bug( "should not have existed" );
		}
	}
	
	private final static void removeOldInstance( StaticInstanceTracker instance ) {

		Log.entry( instance.toString() );
		RunTime.assumedNotNull( instance );
		if ( !ALL_INSTANCES.removeObject( instance ) ) {
			RunTime.bug( "should've existed" );
		}
	}
	
}
