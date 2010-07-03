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



package org.javapart.logger;



public class Log {
	
	
	// static final int methodWOClassNameWidth = 30;
	// static final int methodWithClassNameWidth = 50;
	static final int					fileAndLineWidth						= 30 + 50;
	static final int					spacesBeforeMsg							= 10;
	
	private final static LogFlags		CurrentLogFlags[]						= {
			// LogFlags.Entry, LogFlags.Mid, LogFlags.Exit,
			LogFlags.Bug, LogFlags.Warn, LogFlags.Result, LogFlags.Special, LogFlags.Thro,
																				};
	
	// if no elements, then all methods are shown
	private final static String			ShowOnlyTheseMethodsAndTheirChildren[]	= {
																				// "<init>"
																				
																				// "appendLinkPC"
																				};
	private final static boolean		alsoShowChildrenMethods					= true;
	
	private static StackTraceElement[]	stea									= null;
	
	private static final Integer		currentMethodLocation					= 5;		// 5
																							
	private final static String			nl										= "\n";
	
	/*
	 * public Log() {
	 * 
	 * this.log( "Logger initialized." ); }
	 */

	public static void thro0( int modifier, String msg ) {

		log( modifier, LogFlags.Thro, "Throws: " + msg );
	}
	
	public static void thro( String msg ) {

		thro0( 0, msg );
	}
	
	public static void thro1( String msg ) {

		thro0( +1, msg );
	}
	
	public static void thro2( String msg ) {

		thro0( +2, msg );
	}
	
	public static void thro() {

		thro0( 0, "" );
	}
	
	// don't merge the three methods, else line numbers will get screwed
	private static void special0( String msg ) {

		log( 0, LogFlags.Special, "special: " + msg );
	}
	
	public static void special( String msg ) {

		special0( msg );
	}
	
	public static void special() {

		special0( "" );
	}
	
	
	private static void entry0( String msg ) {

		log( 0, LogFlags.Entry, "entry : " + msg );
	}
	
	public static void entry( String msg ) {

		entry0( msg );
	}
	
	public static void entry() {

		entry0( "" );
	}
	
	private static void exit0( String msg ) {

		log( 0, LogFlags.Exit, "exit  : " + msg );
	}
	
	public static void exit( String msg ) {

		exit0( msg );
	}
	
	public static void exit() {

		exit0( "" );
	}
	
	private static void mid0( String msg ) {

		log( 0, LogFlags.Mid, "mid  : " + msg );
	}
	
	public static void mid( String msg ) {

		mid0( msg );
	}
	
	public static void mid() {

		mid0( "" );
	}
	
	private static void log( int modifier, LogFlags logFlag, String msg ) {

		// StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		// System.out.println( " !" + stea.length + "! " );
		// // throw new AssertionError();
		//
		//
		// StackTraceElement ste = stea[stea.length - 3];
		// ste = ( new Exception() ).getStackTrace()[4];
		if ( hasFlag( logFlag ) ) {
			// if ( logFlag == LogFlags.Result ) {
			// System.err.print( msg );
			// } else {
			String loc = getCurrentLocation( modifier );
			if ( mustShowCurrentMethod( modifier ) ) {
				
				// System.err.print( loc + nl + String.format( "%-" + spacesBeforeMsg + "s%s", " ", msg ) );
				System.err.print( msg + nl + String.format( "%-" + spacesBeforeMsg + "s%s", " ", loc ) + nl );
				// }
			}
		}
	}
	
	/**
	 * @return true if currentMethod is a method that must show logs for, or a
	 *         child of such method
	 */
	private static boolean mustShowCurrentMethod( int modifier ) {

		if ( ShowOnlyTheseMethodsAndTheirChildren.length == 0 ) {
			return true;// show all
		}
		
		int init = 0;
		int dest = stea.length;
		if ( !alsoShowChildrenMethods ) {
			init = currentMethodLocation + modifier;
			dest = init + 1;
		}
		for ( int x = init; x < dest; x++ ) {
			String comparer = stea[x].getMethodName();
			for ( String element : ShowOnlyTheseMethodsAndTheirChildren ) {
				if ( comparer == element ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean hasFlag( LogFlags logFlag ) {

		for ( LogFlags currentLogFlag : CurrentLogFlags ) {
			if ( logFlag == currentLogFlag ) {
				return true;
			}
		}
		return false;
	}
	
	private static String getCurrentLocation( int modifier ) {

		// try {
		// throw new Exception();
		// } catch ( Exception e ) {
		// e.printStackTrace();
		// }
		stea = Thread.currentThread().getStackTrace();
		StackTraceElement ste = stea[currentMethodLocation + modifier];
		
		// String msg = new String();
		// int width = methodWOClassNameWidth;// methodName size
		
		// if ( hasFlag( LogFlags.ShowClassName ) ) {
		// msg = msg.concat( ste.getClassName() + "." );
		// width = methodWithClassNameWidth;
		// }
		// msg = msg.concat( ste.getMethodName() );
		
		// this was a workaround for eclipse showing links properly when clicked to go at the right source even if other
		// projects were open that would've make it go to their sources in rt.jar file
		// return String.format( /* "%-" + width + "s "+ */"%-" + ( fileAndLineWidth ) + "s", /* msg, */
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()
				+ ")";// );
	}
	
	public static void result( boolean boo ) {

		result0( ( boo == true ? "true" : "false" ) + nl );
	}
	
	private static void result0( String msg ) {

		log( 0, LogFlags.Result, "result: " + msg );
	}
	
	public static void result( String msg ) {

		result0( msg );
	}
	
	private static void warn0( String msg ) {

		log( 0, LogFlags.Warn, "warn  : " + msg );
	}
	
	public static void warn( String msg ) {

		warn0( msg );
	}
	
	public static void warn() {

		warn0( "" );
	}
	
	private static void bug0( String msg ) {

		log( 0, LogFlags.Bug, "BUG   : " + msg );
	}
	
	public static void bug( String msg ) {

		bug0( msg );
	}
	
	public static void bug() {

		bug0( "" );
	}
}
