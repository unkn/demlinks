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



import org.dml.tracking.Factory;
import org.references.method.MethodParams;



/**
 * 
 *
 */
/**
 * 
 *
 */
public abstract class Initer {
	
	// true on calling .start(); and true on calling .done()
	private boolean			inited			= false;
	
	// saved params to be used on reInit(); they are always clone of passed params
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
	 * used params which were saved/cloned internally<br>
	 */
	public final void _restart_aka_deInit_and_initAgain_WithOriginalPassedParams() {

		if ( !this.isInited() ) {
			RunTime.badCall( "not already inited. Just use reInit" );
		}
		this._deInit();
		this._reInit_aka_initAgain_WithOriginalPassedParams();
	}
	
	/**
	 * reInit with original params, can only be used if not already inited<br>
	 * should've been already inited once, but now it should be in deInit-ed state; if wasn't init-ed ever, then params
	 * are null<br>
	 */
	public final void _reInit_aka_initAgain_WithOriginalPassedParams() {

		if ( this.isInited() ) {
			RunTime.badCall( "already inited. Maybe you wanted to use restart()" );
		}
		// so wasn't inited then:
		this._init( formerParams );
		// Factory.reInit( formerParams );
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
	 * isInited() is true while in this method<br>
	 */
	protected abstract void beforeDone();
	
	/**
	 * @return
	 */
	public final void _deInit() {

		if ( !this.isInited() ) {
			RunTime.badCall( this.toString() + " was not already init()-ed" );
		} else {
			try {
				try {
					this.beforeDone();
				} finally {
					this.done( formerParams );
				}
				// formerParams are not managed here, only on init() ie. they're discarded in init()
			} finally {
				this.setInited( false ); // ignore this:don't move this below .done() because .done() may throw
			}
		}
	}
	
	// /**
	// * this will not except if already deInit()-ed
	// *
	// * @see #deInit()
	// */
	// public final void internal_DeInitSilently_use_Factory_instead() {
	//
	// // FIXME: this is used in deInit() also, but there we don't want it silent
	// if ( this.isInited() ) {
	// try {
	// try {
	// this.beforeDone();
	// } catch ( Throwable e ) {// FIXME: remove this catch if we still want to throw but deferred
	// e.printStackTrace();
	// } finally {
	// // FIXME: this may throw but since we're in silent mode, we may want to muff it
	// this.done( formerParams );
	// }
	// // formerParams are not managed here, only on init() ie. discarded
	// } finally {
	// this.setInited( false ); // ignore this:don't move this below .done() because .done() may throw
	// }
	// }
	// }
	


	/**
	 * isInited() is false until after this method completes; so you see it as false from within this method<br>
	 */
	protected abstract void beforeStart();
	
	/**
	 * should only be called by one method: Factory.getNewInstance(...)<br>
	 * the params will be cloned (or copied) to be used by reInit()<br>
	 * 
	 * @param params
	 *            null or the params
	 */
	public final void _init( MethodParams params ) {

		if ( this.isInited() ) {
			RunTime.badCall( "already inited, you must deInit() before calling init(...) again" );
		} else {
			try {
				// this may throw
				this.beforeStart();
			} finally {
				this.setInited( true );
			}
			
			if ( params != formerParams ) {
				// means: NOT called by reInit() or restart()
				if ( null != formerParams ) {
					// means: was used before, we discard the one before
					// formerParams.deInit();
					try {
						Factory.deInit( formerParams );
					} finally {
						formerParams = null;
					}
				}
				
				RunTime.assumedNull( formerParams );
				
				if ( null != params ) {// we get a copy of passed params
					// this does init(null) inside
					formerParams = params.getClone();
					RunTime.assumedNotNull( formerParams );
				} // else is null
			} // else called by reInit() we don't mod them
			
			this.start( formerParams );// can be null params
		}
	}
	
}
