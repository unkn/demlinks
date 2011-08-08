/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 8, 2011 5:12:56 AM
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
 * * Neither the name of 'DemLinks' nor the names of its contributors
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
package org.berkeleydb;

import com.sleepycat.db.internal.*;



/**
 *
 */
public class PreloadBDBNativeLibraries {
	
	// set to true to use the debug versions of the libs (those with the "d" suffix) which is likely 3.4 times slower than norm.
	private static final boolean	DEBUG	= false;
	// final static Timer t = new Timer( Timer.TYPE.MILLIS );
	static {// this takes 0ms or 3,5 million ns aka 0.0035ms; it once took 16ms on real low cpu speed
	// t.start();
		if ( DEBUG ) {
			System.out.println( "DEBUG is enabled, slow mode..." );
		}
		final String suffix = "" + DbConstants.DB_VERSION_MAJOR + "" + DbConstants.DB_VERSION_MINOR + ( DEBUG ? "d" : "" );
		// if you install: Berkeley DB 11gR2 5.2.28 and restart, under windows this means the libdb52.dll would be on PATH
		// else you need the following because that .dll isn't on path so we manually load it rather than allow windows to load
		// it when db.jar is trying to load the libdb_java52.dll one whos dependency is libdb52.dll:
		// the property java.library.path (or something like that) is used to load only those with System.loadLibrary
		// but that lib's dependencies will be loaded by the OS which is using PATH only and that location isn't in PATH
		// that is why we load the dependecies manually here via System.loadLibrary
		// if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "win" ) > -1 ) {
		final String dependentLibName = "libdb" + suffix;
		System.out.println( "Preloading depended library first: `" + dependentLibName + "`" );
		System.loadLibrary( dependentLibName );// libdb52
		// }
		System.setProperty( "sleepycat.db.libname", "libdb_java" + suffix );// libdb_java52
		// see db_javaJNI class for more info, that's the class that will load the lib set in the property
		// db_javaJNI. j=new db_javaJNI();
		
	}
	
	
	public static void initIfNotInited() {
		// empty because the class' static block will be executed, once; regardless of how many times you call this method
		// t.stop();
		// System.out.println( "PreloadBDBNativeLibraries: " + t.getDeltaPrintFriendly() );
	}
}
