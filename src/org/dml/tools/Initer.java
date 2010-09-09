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
public abstract class Initer
{
	
	// true on calling .start(); and true on calling .done()
	// true if in the process of initing, or done initing it; false only after exiting .done()
	private boolean			initingOrInited		= false;
	
	// true only after successful _init() until a successful _done(); so false on calling start() but true afterwards
	private boolean			initedSuccessfully	= false;
	
	private boolean			wasInitedAtLeastOne	= false;
	
	// saved params to be used on reInit(); they are always clone of passed params
	private MethodParams	formerParamsCloned	= null;
	
	
	/**
	 * constructor
	 */
	public Initer()
	{
		
		super();
	}
	

	/**
	 */
	private final
			void
			setInitingOrInited(
								boolean inited1 )
	{
		
		initingOrInited = inited1;
	}
	

	/**
	 * @return true if it's inside init() aka start()
	 */
	public final
			boolean
			isInitingOrInited()
	{
		
		return initingOrInited;
	}
	

	/**
	 * @return true if it's done initing hence it means it didn't throw exceptions while init-ing (or they were caught
	 *         inside start() and handled) and so it's successfully inited<br>
	 *         false only after calling done() aka Factory.deInit()<br>
	 */
	public final
			boolean
			isInitedSuccessfully()
	{
		
		return initedSuccessfully;
	}
	

	private final
			void
			setInitedSuccessfully(
									boolean inited1 )
	{
		
		initedSuccessfully = inited1;
	}
	

	/**
	 * @return true if it was inited at least once (even if it's deInited now or not) since it was allocated ie. via new
	 */
	public final
			boolean
			wasInitedEver()
	{
		return wasInitedAtLeastOne;
	}
	

	/**
	 * implement this start(), but use init() instead<br>
	 * the params here are already cloned from those passed to init(params)<br>
	 * isInited() will return true while in start() and it will remain true even if start() throws<br>
	 */
	protected abstract
			void
			start(
					MethodParams params );
	

	/**
	 * this will call deInit() and then init(params) where params are the last
	 * used params which were saved/cloned internally<br>
	 */
	public final
			void
			_restart_aka_deInit_and_initAgain_WithOriginalPassedParams()
	{
		
		if ( !this.isInitingOrInited() )
		{
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
	public final
			void
			_reInit_aka_initAgain_WithOriginalPassedParams()
	{
		// TODO: a bool that states if it was ever inited once, if wasn't throw here
		// FIXME: disable reinit and restart due to their ability to change the instance's contents while it is still
		// referred to by other instances which possibly have cached stuff from it when it had previous contents
		// FIXME: we should not allow reinit with different params than the first(last) time they were specified
		if ( this.isInitingOrInited() )
		{
			RunTime.badCall( "already inited. Maybe you wanted to use restart()" );
		}
		if ( !this.wasInitedEver() )
		{
			RunTime.badCall( "it was never inited, not even once, ergo the parameters passed to init would be null" );
		}
		// so wasn't inited then:
		this._init( formerParamsCloned );
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
	protected abstract
			void
			done(
					MethodParams params );
	

	/**
	 * isInited() is true while in this method<br>
	 */
	protected
			void
			beforeDone()
	{
		//
	}
	

	/**
	 * DON'T call this directly, use {@link Factory#deInit(Initer)}<br>
	 * you're allowed to call deInit even if init() failed
	 * ie. due to exceptions<br>
	 */
	public final
			void
			_deInit()
	{
		
		if ( !this.isInitingOrInited() )
		{
			RunTime.badCall( this.toString()
								+ " was not already init()-ed" );
		}
		else
		{
			try
			{
				this.beforeDone();
			}
			finally
			{
				try
				{
					this.done( formerParamsCloned );
				}
				finally
				{
					try
					{
						this.setInitingOrInited( false ); // ignore this:don't move this below .done() because .done()
															// may throw
					}
					finally
					{
						this.setInitedSuccessfully( false );
						RunTime.throwAllThatWerePosponed();
					}
				}
			}
			// formerParams are not managed here, only on init() ie. they're discarded in init()
			
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
	// // FIXME: this may throw but since we're in silent mode, we may want to mute it
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
	protected
			void
			beforeStart()
	{
		//
	}
	

	/**
	 * should only be called by one method: Factory.getNewInstance(...)<br>
	 * the params will be cloned (or copied) to be used by reInit()<br>
	 * should not call _init() again if the previous call to _init() failed due to exceptions, call _deInit() prior to
	 * another call to _init()<br>
	 * 
	 * @param params
	 *            null or the params
	 */
	public final
			void
			_init(
					MethodParams params )
	{
		
		if ( this.isInitingOrInited() )
		{
			RunTime.badCall( "already inited, you must deInit() before calling init(...) again" );
		}
		else
		{
			try
			{
				// this may throw
				this.beforeStart();
			}
			finally
			{
				wasInitedAtLeastOne = true;
				this.setInitingOrInited( true );
			}
			
			if ( params != formerParamsCloned )
			{
				// means: NOT called by reInit() or restart()
				if ( null != formerParamsCloned )
				{
					// means: was used before, we discard the one before
					// formerParams.deInit();
					// try
					// {
					// Factory.deInit( formerParams );
					// }
					// finally
					// {
					formerParamsCloned = null;
					// }
				}
				
				RunTime.assumedNull( formerParamsCloned );
				
				if ( null != params )
				{// we get a copy of passed params
					// this does init(null) inside
					formerParamsCloned = params.getClone();// this won't deInit because reInit might need it
					RunTime.assumedNotNull( formerParamsCloned );
				} // else is null
			} // else called by reInit() we don't mod them
			
			this.start( formerParamsCloned );// can be null params
			this.setInitedSuccessfully( true );
		}// wasn't inited
	}
	
}
