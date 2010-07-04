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



import org.dml.error.BadCallError;
import org.dml.error.BugError;
import org.javapart.logger.Log;



/**
 * these mustn't be disabled, because they're sometimes calling methods as
 * parameters<br>
 * uhm... nvm, it seems the methods in params will always get executed
 */
public class RunTime {
	
	private static Throwable	allExceptionsChained	= null;
	
	/**
	 * -this method will attempt to chain all exceptions thrown by it and it also throws them as it's called<br>
	 * -if you're trying to re-throw a caught exception do not chain it with new Exception(e) as a param to this method<br>
	 * -so basically when you're trying to rethrow normally thrown exceptions ie. thos with 'throw new' well after you
	 * catch them just do a RunTime.thro(e) and not a 'new Exception(e)'<br>
	 * -if you're trying to throw new exception, obviously then use new Exception("msg") as param<br>
	 * -if you're passing an exception which is already part of a chain, then the previous chain is discarded and the
	 * exception becomes the new chain<br>
	 * -note that by default throw new Exception(msg) will override all previous exceptions thrown (ie. they're
	 * forgotten) unless you throw new Exception(msg, prevException) where prevException is an exception just like this:
	 * chained; at least they are not visible in eclipse unless they are chained<br>
	 * -NOTE: there's no need (but you can) to declare a "throws" clause on method header definition because all throws
	 * are wrapped
	 * around RuntimeException<br>
	 * -err, we're kinda assuming these will not be caught and handled, so they'll eventually cause program exit (but
	 * cleanly shut down) and so this is useful for logging<br>
	 * 
	 * @param newOne
	 * @throws Throwable
	 */
	public static void thro( Throwable newOne ) {

		thro0( 0, newOne );
	}
	
	/**
	 * @see #thro(Throwable)
	 * @param newOne
	 */
	public static void thro1( Throwable newOne ) {

		thro0( +1, newOne );
	}
	
	/**
	 * @see #thro(Throwable)
	 * @param newOne
	 */
	public static void thro2( Throwable newOne ) {

		thro0( +2, newOne );
	}
	
	private static void thro0( int modifier, Throwable newOne ) {

		if ( null != allExceptionsChained ) {
			// allExceptionsChained = newOne;
			// } else {
			if ( null == newOne.getCause() ) {
				try {
					newOne.initCause( allExceptionsChained );
				} catch ( Throwable t ) {
					// ignoring thrown irrelevant ones from initCause
					Log.bug( "this shouldn't happen and btw exception here is unable to be thrown, even if no catch block exists" );
				}
			} else {
				// ok so at this point, whatever chain of exceptions we had, is going to be lost and unreported within
				// eclipse, the only way to see them is if you look at console and that could be messy because you won't
				// know where in the console and which one of the following warnings (if in a junit with many tests
				// failed) is the right one
				Log.warn( "we got passed a chained exception(/throwable) so we discard the previous chain; so this should work well apparently unless we didn't chain the previous exception in this new exception that already had a chain" );
				// Log.thro1( newOne.getLocalizedMessage() );
				// newOne.printStackTrace();
			}
		}
		// for both (1+2)paths in above if:
		allExceptionsChained = newOne;
		// }
		Log.throwReport( allExceptionsChained, modifier );
		// ( modifier,
		// allExceptionsChained.getClass().getCanonicalName() + ": " + allExceptionsChained.getLocalizedMessage() );
		// System.out.println( Log.getLine( allExceptionsChained.getStackTrace(), 2 ) );
		
		internalWrappedThrow();
		// throw new RuntimeException( allExceptionsChained );
	}
	
	private static void internalWrappedThrow() {

		// wrapping this into RuntimeException 'cause it's unchecked aka no throws declaration needed
		throw new RuntimeException( allExceptionsChained );
	}
	
	/**
	 * not just for jUnit
	 */
	public static void clearThrowChain() {

		allExceptionsChained = null;
	}
	
	/**
	 * for jUnit; really don't use these; if you catch an exception you're not certain that it was the last one, unless
	 * you're catching Throwable instance
	 */
	public static void clearLastThrown() {

		allExceptionsChained = allExceptionsChained.getCause();
	}
	
	
	/**
	 * @param t
	 *            the previous (normally thrown) java exception to chain as cause of this bug<br>
	 */
	public static void bug( Throwable t ) {

		bug0( t, "Bug detected." );
	}
	
	public static void bug() {

		bug0( null, "Bug detected." );
	}
	
	/**
	 * @param cause
	 *            the previous (normally thrown) java exception to chain as cause of this bug<br>
	 * @msg
	 */
	private static void bug0( Throwable cause, String msg ) {

		try {
			if ( null != cause ) {
				RunTime.thro2( cause );// chain it
			}
		} finally {
			RunTime.thro2( new BugError( msg ) );
		}
	}
	
	/**
	 * @param cause
	 *            a normally thrown java exception (ie. not thrown with RunTime.thro()) that will be chained as cause of
	 *            this bug<br>
	 * @param msg
	 */
	public static void bug( Throwable cause, String msg ) {

		bug0( cause, "Bug detected: " + msg );
	}
	
	public static void bug( String msg ) {

		bug0( null, "Bug detected: " + msg );
	}
	
	public static void badCall() {

		badCall0( null, "" );
	}
	
	public static void badCall( String msg ) {

		badCall0( null, msg );
	}
	
	public static void badCall( Throwable cause ) {

		badCall0( cause, "" );
	}
	
	public static void badCall( Throwable cause, String msg ) {

		badCall0( cause, msg );
	}
	
	private static void badCall0( Throwable cause, String msg ) {

		// String msg2 = "BADCALL: " + msg;
		// Log.thro2( msg2 );
		try {
			if ( null != cause ) {
				RunTime.thro2( cause );// chain it
			}
		} finally {
			RunTime.thro2( new BadCallError( msg ) );
		}
	}
	
	// public static void thro( Exception ex ) throws Exception {
	//
	// Log.thro( ex.getLocalizedMessage() );
	// throw new Exception( ex );
	// }
	//
	// public static void thro( RuntimeException rtex ) {
	//
	// // FIXME: maybe here show the level before this, ie. the line and file
	// // prior to this call
	// Log.thro( rtex.getLocalizedMessage() );
	// throw new RuntimeException( rtex );
	// }
	

	/**
	 * @param b
	 */
	public static void assumedTrue( boolean b ) {

		if ( !b ) {
			RunTime.thro1( new AssertionError( "expected true condition was false!" ) );
		}
	}
	
	public static void assumedNotNull( Object... obj ) {

		for ( int i = 0; i < obj.length; i++ ) {
			if ( null == obj[i] ) {
				RunTime.thro1( new AssertionError( "expected non-null object[" + ( i + 1 ) + "] was null!" ) );
			}
		}
	}
	
	public static void assumedNull( Object... obj ) {

		for ( int i = 0; i < obj.length; i++ ) {
			if ( null != obj[i] ) {
				RunTime.thro1( new AssertionError( "expected null object[" + ( i + 1 ) + "] was NOT null!" ) );
			}
		}
	}
	
	public static void assumedFalse( boolean b ) {

		if ( b ) {
			RunTime.thro1( new AssertionError( "expected false condition was true!" ) );
		}
		
	}
	
	/**
	 * The purpose of this is to give time to do the deinitializing code<br>
	 * you can postpone all exceptions thrown with RunTime.thro() as follows:<br>
	 * try {<br>
	 * //some exceptions thrown here with RunTime.thro() will happen
	 * }catch(Throwable t) {<br>
	 * //do nothing<br>
	 * }<br>
	 * //later you can call this method to throw all those postponed exceptions <br>
	 * throwPostponed(); <br>
	 * <br>
	 * all normally thrown exceptions (ie. with keyword 'throw') will just overwrite anything thrown before<br>
	 */
	public static void throwPosponed() {

		if ( null != allExceptionsChained ) {
			internalWrappedThrow();
			clearThrowChain();
		}// else ignore
		
	}
}
