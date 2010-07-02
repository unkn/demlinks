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
public abstract class StaticInstanceTracker extends Initer {
	
	// LIFO list tracking all instances of ALL subclasses
	private final static ListOfUniqueNonNullObjects<StaticInstanceTracker>	ALL_INSTANCES	= new ListOfUniqueNonNullObjects<StaticInstanceTracker>();
	
	

	/**
	 * constructor
	 */
	public StaticInstanceTracker() {

	}
	
	
	@Override
	protected void beforeInit() {

		addNewInstance( this );
	}
	
	@Override
	protected void beforeDeInit() {

		removeOldInstance( this );
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
				try {
					iter.deInit();
				} catch ( Throwable e ) {
					// ignore exceptions
					e.printStackTrace();
				}
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
}
