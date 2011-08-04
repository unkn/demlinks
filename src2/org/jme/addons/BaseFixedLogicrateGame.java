/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// $Id: BaseGame.java 4131 2009-03-19 20:15:28Z blaine.dev $


package org.jme.addons;



import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.AbstractGame;
import com.jme.input.InputSystem;
import com.jme.system.GameSettings;
import com.jme.system.PropertiesGameSettings;
import com.jme.util.ThrowableHandler;
import com.jme.util.Timer;



/**
 * The simplest possible implementation of a game loop.
 * <p>
 * This class defines a pure high speed game loop that runs as fast as CPU/GPU
 * will allow. No handling of variable frame rates is included and, as a result,
 * this class is unsuitable for most production applications; it is useful as a
 * base for applications which require more specialised behaviour as it includes
 * basic game configuration code.
 * 
 * 
 * @author Mark Powell, Eric Woroshow
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Thu, 19 Mar
 *          2009) $
 */
/**
 * A game that attempts to run at a fixed logic rate.
 * <p>
 * The main loop makes every effort to update at the specified rate. The goal is
 * to keep a consistent game-play speed regardless of the frame rate achieved by
 * the visuals (i.e. the game will render as fast as the hardware permits, while
 * running it's logic at a fixed rate). This gives tighter control on how the
 * game state is processed, including such things as AI and physics.
 * <p>
 * The concept behind this is forcing every game logic tick to represent a fixed
 * amount of real-time. For example, if the logic is updated at a rate of 15
 * times per second, and we have a person moving at 30 pixels per second, each
 * update the person should move 2 pixels. To compensate for the non-constant
 * frame rate, we smooth the visuals using interpolation. So, if the scene is
 * rendered twice without the game logic being updated, we do not render the
 * same thing twice.
 * <p>
 * Using a fixed time-step model has a number of benefits: game logic is
 * simplified as there is no longer any need to add time deltas to achieve frame
 * rate independence. There is also a gain in efficiency as the logic can be run
 * at a lower frequency than the rendering, meaning that the logic may be
 * updated only once every second game - a net save in time. Finally, because
 * the exact same sequence of game logic code is executed every time, the game
 * becomes deterministic (that is to say, it will run the exact same way every
 * time).
 * <p>
 * Further extension of this class could be used to integrate both a fixed logic
 * rate and a fixed frame rate.
 * 
 * @author Eric Woroshow
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Thu, 19 Mar
 *          2009) $
 */
public abstract class BaseFixedLogicrateGame extends AbstractGame {
	
	private static final Logger	logger		= Logger.getLogger( BaseFixedLogicrateGame.class.getName() );
	protected ThrowableHandler	throwableHandler;
	

	private static final int	MAX_LOOPS	= 50;
	
	// Logic-rate managing variables
	private Timer				timer;
	
	private int					logicTPS;
	
	private long				tickTime;
	
	private long				time0, time1;
	
	private int					loops;
	
	/**
	 * <code>setLogicTicksPerSecond</code> sets the number of logic times per
	 * second the game should update the logic. This should not be called prior
	 * to the application being <code>start()</code> -ed.<br>
	 * but since timer = Timer.getTimer(); is init-ed inside start()... to avoid
	 * java.lang.NullPointerException
	 * you should do this in initGame() or later in simpleInitGame()
	 * 
	 * @param tps
	 *            the desired logic rate in ticks per second
	 */
	public void setLogicTicksPerSecond( int tps ) {

		if ( tps < 0 ) {
			throw new IllegalArgumentException(
					"Ticks per second cannot be less than zero." );
		}
		
		logicTPS = tps;
		tickTime = timer.getResolution() / logicTPS;
	}
	
	/**
	 * Ticks logic at a fixed rate while rendering as fast as hardware permits.
	 */
	@Override
	public final void start() {

		logger.info( "Application started." );
		try {
			this.getAttributes();
			
			if ( !finished ) {
				this.initSystem();
				
				this.assertDisplayCreated();
				
				timer = Timer.getTimer();
				this.setLogicTicksPerSecond( 60 ); // default to 60 tps
				
				this.initGame();
				
				// main loop
				while ( !finished && !display.isClosing() ) {
					time1 = timer.getTime();
					loops = 0;
					
					while ( ( ( time1 - time0 ) > tickTime )
							&& ( loops < MAX_LOOPS ) ) {
						// handle input events prior to updating the scene
						// - some applications may want to put this into update
						// of
						// the game state
						InputSystem.update();
						
						// update game state, do not use interpolation parameter
						this.update( -1.0f );
						time0 += tickTime;
						loops++;
					}
					
					// If the game logic takes far too long, discard the pending
					// time
					if ( ( time1 - time0 ) > tickTime ) {
						time0 = time1 - tickTime;
					}
					
					float percentWithinTick = Math.min( 1.0f,
							(float)( time1 - time0 ) / tickTime );
					// render scene with interpolation value
					this.render( percentWithinTick );
					
					// swap buffers
					display.getRenderer().displayBackBuffer();
					
					Thread.yield();
				}
			}
		} catch ( Throwable t ) {
			logger.logp( Level.SEVERE, this.getClass().toString(), "start()",
					"Exception in game loop", t );
			if ( throwableHandler != null ) {
				throwableHandler.handle( t );
			}
		}
		
		this.cleanup();
		logger.info( "Application ending." );
		
		if ( display != null ) {
			display.reset();
		}
		this.quit();
	}
	
	/**
	 * Closes the display
	 * 
	 * @see AbstractGame#quit()
	 */
	@Override
	protected void quit() {

		if ( display != null ) {
			display.discard();
		}
		// System.exit( 0 ); ?
	}
	
	/**
	 * Get the exception handler if one has been set.
	 * 
	 * @return the exception handler, or {@code null} if not set.
	 */
	protected ThrowableHandler getThrowableHandler() {

		return throwableHandler;
	}
	
	/**
	 * 
	 * @param throwableHandler1
	 */
	protected void setThrowableHandler( ThrowableHandler throwableHandler1 ) {

		throwableHandler = throwableHandler1;
	}
	
	/**
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	@Override
	protected abstract void update( float interpolation );
	
	/**
	 * Renders the scene. Under no circumstances should the render method alter
	 * anything that could directly or indirectly modify the game logic.
	 * 
	 * @param percentWithinTick
	 *            decimal value representing the position between update ticks
	 * @see AbstractGame#render(float interpolation)
	 */
	@Override
	protected abstract void render( float percentWithinTick );
	
	/**
	 * @see AbstractGame#initSystem()
	 */
	@Override
	protected abstract void initSystem();
	
	/**
	 * @see AbstractGame#initGame()
	 */
	@Override
	protected abstract void initGame();
	
	/**
	 * @see AbstractGame#reinit()
	 */
	@Override
	protected abstract void reinit();
	
	/**
	 * @see AbstractGame#cleanup()
	 */
	@Override
	protected abstract void cleanup();
	
	/**
	 * @see AbstractGame#getNewSettings()
	 */
	@Override
	protected GameSettings getNewSettings() {

		return new BaseGameSettings();
	}
	
	/**
	 * A PropertiesGameSettings which defaults Fullscreen to TRUE.
	 */
	static class BaseGameSettings extends PropertiesGameSettings {
		
		static {
			// This is how you programmatically override the DEFAULT_*
			// settings of GameSettings.
			// You can also make declarative overrides by using
			// "game-defaults.properties" in a CLASSPATH root directory (or
			// use the 2-param PropertiesGameSettings constructor for any name).
			// (This is all very different from the user-specific
			// "properties.cfg"... or whatever file is specified below...,
			// which is read from the current directory and is
			// session-specific).
			defaultFullscreen = Boolean.TRUE;
			defaultSettingsWidgetImage = "/jmetest/data/images/Monkey.png";
		}
		
		/**
		 * Populates the GameSettings from the (session-specific) .properties
		 * file.
		 */
		BaseGameSettings() {

			super( "properties.cfg" );
			this.load();
		}
	}
}
