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
 * File creation: Jul 18, 2010 4:50:39 PM
 */


package org.temporary.tests;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 * This snippet shows how to writer and register the shutdown hook with the VM.
 * 
 * @author java4learners
 * 
 */
public class ShutdownHookExample {
	
	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static final void main( String args[] ) throws Exception {

		File file = new File( "test.dat" );
		// open the file writer
		final FileWriter fileWriter = new FileWriter( file );
		
		// Add the shutdown hook as new java thread to the runtime.
		// can be added as inner class or a separate class that implements
		// Runnable or extends Thread
		Runtime.getRuntime().addShutdownHook( new Thread() {
			
			@Override
			public void run() {

				System.out.println( "in : run () : shutdownHook" );
				
				// save state, resource clean,up etc.
				if ( fileWriter != null ) {
					try {
						// try to close the open file
						fileWriter.flush();
						fileWriter.close();
						System.out.println( "File closed successfully" );
					} catch ( IOException e ) {
						System.out.println( "Failed to flush/close the file :" + e.getMessage() );
						e.printStackTrace();
					}
				}
				System.out.println( "Shutdown hook completed..." );
			}
		} );
		
		// main sample code, open file and write, then throw dummy Exception
		// once the JVM terminates, the shutdown hook, would close the file.
		
		for ( int i = 0; i < 10; i++ ) {
			fileWriter.write( "Line : " + i );
		}
		throw new RuntimeException( "Dummy Exception to cause JVM to exit" );
	}
}
