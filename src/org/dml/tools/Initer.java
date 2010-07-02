/**
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
 * 
 * 
 * File creation: Jul 1, 2010 8:06:03 PM
 */


package org.dml.tools;



import org.references.method.MethodParams;



/**
 * 
 *
 */
public abstract class Initer {
	
	private boolean			inited			= false;
	private MethodParams	formerParams	= null;
	
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
	 * implement this start(), but use init() instead<br>
	 * the params here are already cloned from those passed to init(params)<br>
	 * isInited() will return true while in start() and it will remain true even if start() throws<br>
	 */
	protected abstract void start( MethodParams params );
	
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
	 * FIXME: good luck preventing this from being called directly; should be called only from deInit(); but should also
	 * be overriddable
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
	
	
	protected abstract void beforeDeInit();
	
	/**
	 * this will not except if already deInit()-ed
	 * 
	 * @see #deInit()
	 */
	public final void deInitSilently() {

		if ( this.isInited() ) {
			try {
				try {
					this.beforeDeInit();
				} catch ( Throwable e ) {
					e.printStackTrace();
				} finally {
					this.done( formerParams );
				}
				// formerParams are not managed here, only on init() ie. discarded
			} finally {
				this.setInited( false ); // ignore this:don't move this below .done() because .done() may throw
			}
		}
	}
	
	

	protected abstract void beforeInit();
	
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
		this.beforeInit();
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
}
