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



import org.dml.error.AssumptionError;
import org.dml.error.BadCallError;
import org.dml.error.BugError;
import org.javapart.logger.Log;



/**
 * these mustn't be disabled, because they're sometimes calling methods as
 * parameters<br>
 * uhm... nvm, it seems the methods in params will always get executed
 */
public class RunTime
{
	
	private static Throwable	allExceptionsChained	= null;
	
	
	/**
	 * - same as {@link RunTime#thro(normallyThrownOne)}<br>
	 * - see also {@link #throPostponed(Throwable)} if you want to wrap and also postpone<br>
	 * - the point where this was called is not shown on console output.err, only the point where the exception
	 * 'normallyThrownOne' was thrown is shown on console/links<br>
	 * - however in Eclipse Failure Trace you can also see the point where this method was called<br>
	 * - so use this method to wrap exceptions thrown by keyword 'throw' ie:<br>
	 * try {<br>
	 * throw new Exception();<br>
	 * } catch (Throwable t) {<br> {@link RunTime#throWrapped(t)};//wrap<br>
	 * }<br>
	 * 
	 * 
	 * @param normallyThrownOne
	 *            an exception thrown by java 'throw' keyword which you want to wrap around our RunTime.thro() and thus
	 *            chain it<br>
	 * 
	 */
	public static
			void
			throWrapped(
							Throwable normallyThrownOne )
	{
		
		RunTime.thro( normallyThrownOne );
	}
	

	/**
	 * use this to mute (and wrap) an exception<br>
	 * same as {@link RunTime#thro(normallyThrownOne)}<br>
	 * see also {@link #throWrapped(Throwable)}<br>
	 * use {@link RunTime#throwAllThatWerePosponed()} later on<br>
	 * 
	 * @param postponedOne
	 */
	public static
			void
			throPostponed(
							Throwable postponedOne )
	{
		
		try
		{
			RunTime.thro( postponedOne );
		}
		catch ( Throwable t )
		{
			// postponed but shown on .err console
		}
	}
	

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
	 * around RuntimeWrappedThrowException<br>
	 * -err, we're kinda assuming these will not be caught and handled, so they'll eventually cause program exit (but
	 * cleanly shut down) and so this is useful for logging<br>
	 * 
	 * @param newOne
	 * @throws Throwable
	 */
	public static
			void
			thro(
					Throwable newOne )
	{
		
		if ( null != allExceptionsChained )
		{
			// allExceptionsChained = newOne;
			// } else {
			if ( null == newOne.getCause() )
			{
				try
				{
					newOne.initCause( allExceptionsChained );
				}
				catch ( Throwable t )
				{
					// ignoring thrown irrelevant ones from initCause
					Log
							.bug( "this shouldn't happen and btw exception here is unable to be thrown, even if no catch block exists" );
				}
			}
			else
			{
				// ok so at this point, whatever chain of exceptions we had, is going to be lost and unreported within
				// eclipse, the only way to see them is if you look at console and that could be messy because you won't
				// know where in the console and which one of the following warnings (if in a junit with many tests
				// failed) is the right one
				// Log.mid1( "we got passed a chained exception(/throwable) so we discard the previous chain; "
				// + "so this should work well apparently unless we didn't chain the previous exception "
				// + "in this new exception that already had a chain; this is the default behavior to discard "
				// + "prev chain" );
				// Log.thro1( newOne.getLocalizedMessage() );
				// newOne.printStackTrace();
			}
		}
		// for both (1+2)paths in above if:
		allExceptionsChained = newOne;
		// }
		Log.throwReport(
							RunTime.class,
							allExceptionsChained );
		// ( modifier,
		// allExceptionsChained.getClass().getCanonicalName() + ": " + allExceptionsChained.getLocalizedMessage() );
		// System.out.println( Log.getLine( allExceptionsChained.getStackTrace(), 2 ) );
		
		internalWrappedThrow();
		// throw new RuntimeException( allExceptionsChained );
	}
	

	private static
			void
			internalWrappedThrow()
	{
		
		// wrapping this into RuntimeException 'cause it's unchecked aka no throws declaration needed
		throw new RuntimeWrappedThrowException(
												allExceptionsChained );
	}
	

	/**
	 * since RunTime.throWrapped(exception) we need to unwrap all of those RuntimeWrappedThrowException to get to the
	 * initial cause exception<br>
	 * 
	 * @param fromWhat
	 *            caught exception ie. from a catch block
	 * @return never null; the unwrapped exception (which won't be of RuntimeWrappedThrowException type)
	 */
	public static
			Throwable
			getUnwrappedExceptionNeverNull(
											Throwable fromWhat )
	{
		
		RunTime.assumedNotNull( fromWhat );
		Throwable tmpParser = fromWhat;
		while ( tmpParser.getClass() == RuntimeWrappedThrowException.class )
		{
			tmpParser = tmpParser.getCause();
			if ( null == tmpParser )
			{
				RunTime.bug( "should not be null! else what did the wraps wrap? heh bug somewhere" );
				// break;// return null;//and break from while
			}
		}
		
		return tmpParser;// can NOT be null
	}
	

	/**
	 * this is used to compare if the just caught exception <tt>wrappedException</tt> is of the type of
	 * <tt>ofThisExceptionType</tt><br>
	 * 
	 * @param wrappedException
	 *            usually a freshly caught exception from a catch block (which is potentially but not necessarily a
	 *            wrapped exception)
	 * @param ofThisExceptionType
	 *            ie. BadCallError.class
	 * @return
	 */
	public static
			boolean
			isThisWrappedException_of_thisType(
												Throwable wrappedException,
												Class<? extends Throwable> ofThisExceptionType )
	{
		
		RunTime.assumedNotNull(
								wrappedException,
								ofThisExceptionType );
		Throwable unwrappedException = RunTime.getUnwrappedExceptionNeverNull( wrappedException );
		if ( null == unwrappedException )
		{
			RunTime
					.bug( "that exception contained only wraps ie. the RuntimeWrappedThrowException was peeled off and yet there was no Cause detected; must be bug somewhere" );
		}
		return ( unwrappedException.getClass() == ofThisExceptionType );
	}
	

	/**
	 * not just for jUnit
	 */
	public static
			void
			clearThrowChain()
	{
		
		allExceptionsChained = null;
	}
	

	/**
	 * for jUnit; really don't use these; if you catch an exception you're not certain that it was the last one, unless
	 * you're catching Throwable instance<br>
	 * this will clear the last thrown exception which may be a wrap, so maybe you want to use the other method:
	 * 
	 * @see #clearLastThrown_andAllItsWraps()
	 */
	public static
			void
			clearLastThrown()
	{
		
		if ( null != allExceptionsChained )
		{
			allExceptionsChained = allExceptionsChained.getCause();
		}
	}
	

	/**
	 * this will unwrap to the real thrown exception and then it will clear it, but it's cause won't be cleared<br>
	 * so all wraps and the last real exception are cleared<br>
	 */
	public static
			void
			clearLastThrown_andAllItsWraps()
	{
		
		if ( null != allExceptionsChained )
		{
			allExceptionsChained = RunTime.getUnwrappedExceptionNeverNull( allExceptionsChained );
			if ( null == allExceptionsChained )
			{
				RunTime.bug( "what? it was all only wraps? bug somewhere" );
			}
			allExceptionsChained = allExceptionsChained.getCause();
		}
	}
	

	/**
	 * @param t
	 *            the previous (normally thrown) java exception to chain as cause of this bug<br>
	 */
	public static
			void
			bug(
					Throwable t )
	{
		
		bug0(
				t,
				"Bug detected." );
	}
	

	public static
			void
			bug()
	{
		
		bug0(
				null,
				"Bug detected." );
	}
	

	/**
	 * @param cause
	 *            the previous (normally thrown) java exception to chain as cause of this bug<br>
	 * @msg
	 */
	private static
			void
			bug0(
					Throwable cause,
					String msg )
	{
		
		try
		{
			if ( null != cause )
			{
				RunTime.thro( cause );// chain it
			}
		}
		finally
		{
			RunTime.thro( new BugError(
										msg ) );
		}
	}
	

	/**
	 * @param cause
	 *            a normally thrown java exception (ie. not thrown with RunTime.thro()) that will be chained as cause of
	 *            this bug<br>
	 * @param msg
	 */
	public static
			void
			bug(
					Throwable cause,
					String msg )
	{
		
		bug0(
				cause,
				"Bug detected: " + msg );
	}
	

	public static
			void
			bug(
					String msg )
	{
		
		bug0(
				null,
				"Bug detected: " + msg );
	}
	

	public static
			void
			badCall()
	{
		
		badCall0(
					null,
					"" );
	}
	

	public static
			void
			badCall(
						String msg )
	{
		
		badCall0(
					null,
					msg );
	}
	

	public static
			void
			badCall(
						Throwable cause )
	{
		
		badCall0(
					cause,
					"" );
	}
	

	public static
			void
			badCall(
						Throwable cause,
						String msg )
	{
		
		badCall0(
					cause,
					msg );
	}
	

	private static
			void
			badCall0(
						Throwable cause,
						String msg )
	{
		
		// String msg2 = "BADCALL: " + msg;
		// Log.thro2( msg2 );
		try
		{
			if ( null != cause )
			{
				RunTime.thro( cause );// chain it
			}
		}
		finally
		{
			RunTime.thro( new BadCallError(
											msg ) );
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
	public static
			void
			assumedTrue(
							boolean b )
	{
		
		if ( !b )
		{
			RunTime.thro( new AssumptionError(
												"expected true condition was false!" ) );
		}
	}
	

	public static
			void
			assumedNotNull(
							Object... obj )
	{
		
		if ( null == obj )
		{
			RunTime.badCall( "must supply at least one parameter" );
		}
		else
		{
			for ( int i = 0; i < obj.length; i++ )
			{
				if ( null == obj[i] )
				{
					RunTime.thro( new AssumptionError(
														"expected non-null object[" + ( i + 1 ) + "] was null!" ) );
				}
			}
		}
	}
	

	public static
			void
			assumedNull(
							Object... obj )
	{
		if ( null == obj )
		{
			RunTime.badCall( "must supply at least one parameter" );
		}
		else
		{
			for ( int i = 0; i < obj.length; i++ )
			{
				if ( null != obj[i] )
				{
					RunTime.thro( new AssumptionError(
														"expected null object[" + ( i + 1 ) + "] was NOT null!" ) );
				}
			}
		}
	}
	

	public static
			void
			assumedFalse(
							boolean b )
	{
		
		if ( b )
		{
			RunTime.thro( new AssumptionError(
												"expected false condition was true!" ) );
		}
		
	}
	

	/**
	 * @return non-null StackTraceElement of the caller, that is the method that has us as statement<br>
	 *         ie. return unqualified method name of the caller<br>
	 *         public void something() {<br>
	 *         //...code<br>
	 *         System.out.println(RunTime.getCurrentStackTraceElement().getMethodName());<br>
	 *         //...more code<br>
	 *         }<br>
	 *         //the above prints "something"<br>
	 */
	public static
			StackTraceElement
			getCurrentStackTraceElement()
	{
		
		// be careful moving stuff here, the number depends on the location of statement before call
		return RunTime.getCurrentStackTraceElement( +1 );
	}
	

	/**
	 * @param modifier
	 *            use positive values(ie. +1) to get caller of caller's STE<br>
	 *            a value of 0 gets current STE that is, the caller of this method<br>
	 * @return non-null StackTraceElement<br>
	 *         ie. public void something() {<br>
	 *         //...code<br>
	 *         System.out.println(RunTime.getCurrentStackTraceElement(0).getMethodName());<br>
	 *         //...more code<br>
	 *         }<br>
	 *         //the above prints "something" - the unqualified method name<br>
	 */
	public static
			StackTraceElement
			getCurrentStackTraceElement(
											int modifier )
	{
		
		RunTime.assumedNotNull( modifier );
		// do not reorganize the following code into more methods, else the below number will have to change
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		RunTime.assumedNotNull( (Object)stea );
		final int whereIsCaller = 2 + modifier;
		RunTime.assumedTrue( stea.length >= 1 + whereIsCaller );
		StackTraceElement ste = stea[whereIsCaller];
		RunTime.assumedNotNull( ste );
		return ste;// never null
	}
	

	public static
			StackTraceElement
			getTheCaller_OutsideOfClass(
											Class<?> whichClass )
	{
		// don't change depth of this method
		RunTime.assumedNotNull( whichClass );
		return private_getTheCaller_OutsideOfThisClass( whichClass.getName() );
	}
	

	public static
			StackTraceElement
			getTheCaller_OutsideOfThisClass()
	{
		// don't change depth of this method
		StackTraceElement ourCaller = RunTime.getCurrentStackTraceElement( +1 );
		RunTime.assumedNotNull( ourCaller );
		return private_getTheCaller_OutsideOfThisClass( ourCaller.getClassName() );
	}
	

	/**
	 * private<br>
	 * this should only be called from a RunTime method that's only 1 level deep<br>
	 * 
	 * @param whichClassName
	 * @return
	 */
	private static
			StackTraceElement
			// StackTraceElement ourCaller,
			private_getTheCaller_OutsideOfThisClass(
														String whichClassName )
	{// Class<?> whichClass ) {
	
		// StackTraceElement ourCaller = RunTime.getCurrentStackTraceElement( +1 + 1 );
		
		// Class<?> whichClass = ourCaller.getClassName()
		// RunTime.assumedNotNull( whichClass );
		// String whichClassName = whichClass.getName();// ourCaller.getClassName();// .getCanonicalName();
		RunTime.assumedNotNull(
		// ourCaller,
				whichClassName );
		
		// String ourCallersName = ourCaller.getMethodName();
		// RunTime.assumedNotNull( ourCallersName );
		// System.out.println( whichClassName + "!" + ourCallersName );
		
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		

		int i = +2 + 1;
		boolean findThisClassFirst = true;
		while ( i < stea.length )
		{
			StackTraceElement ste = stea[i];
			i++;
			// System.err.println( ste );
			if ( findThisClassFirst )
			{
				if ( whichClassName.equals( ste.getClassName() ) )
				{
					// System.out.println( ste.getMethodName() );
					// we found this class aka Factory class
					// if ( ourCallersName.equals( ste.getMethodName() ) )
					// {
					// we found exactly this method we're in aka init()
					// System.out.println( ste );
					findThisClassFirst = false;
					// }
				}
			}
			else
			{// found factory already, now we must find non-factory
				if ( !whichClassName.equals( ste.getClassName() ) )
				{
					// we found the one that is outside Factory class and that called us
					// System.out.println( ste );
					return ste;
					// break;
				}
			}
			
		}
		return null;
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
	public static
			void
			throwAllThatWerePosponed()
	{
		
		if ( null != allExceptionsChained )
		{
			try
			{
				internalWrappedThrow();
				
			}
			finally
			{
				clearThrowChain();
			}
		}// else ignore
		
	}
}
