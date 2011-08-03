/**
 * uses NIO aka new I/O from jdk7 currently
 * File creation: Aug 18, 2010 12:42:30 PM
 */


package org.temporary.tests;



/*
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * - Neither the name of Oracle or the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static java.nio.file.LinkOption.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;



/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir {
	
	private final WatchService			watcher;
	private final Map<WatchKey, Path>	keys;
	private final boolean				recursive;
	private boolean						trace	= false;
	
	
	@SuppressWarnings( "unchecked" )
	static <T> WatchEvent<T> cast( final WatchEvent<?> event ) {
		
		return (WatchEvent<T>)event;
	}
	
	
	/**
	 * Register the given directory with the WatchService
	 */
	void register( final Path dir ) throws IOException {
		
		final WatchKey key =
			dir.register(
				watcher,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY );
		if ( trace ) {
			final Path prev = keys.get( key );
			if ( prev == null ) {
				System.out.format( "register: %s\n", dir );
			} else {
				if ( !dir.equals( prev ) ) {
					System.out.format( "update: %s -> %s\n", prev, dir );
				}
			}
		}
		keys.put( key, dir );
	}
	
	
	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 * 
	 * @throws IOException
	 */
	private void registerAll( final Path start ) throws IOException {
		
		// register directory and sub-directories
		Files.walkFileTree( start, new SimpleFileVisitor<Path>() {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object,
			 * java.nio.file.attribute.BasicFileAttributes)
			 */
			@Override
			public FileVisitResult preVisitDirectory( final Path dir, final BasicFileAttributes attrs ) throws IOException {
				try {
					WatchDir.this.register( dir );
				} catch ( final IOException x ) {
					throw new IOError( x );
				}
				return FileVisitResult.CONTINUE;
			}
		} );
	}
	
	
	/**
	 * Creates a WatchService and registers the given directory
	 */
	WatchDir( final Path dir, final boolean recursive1 ) throws IOException {
		
		watcher = FileSystems.getDefault().newWatchService();
		keys = new HashMap<WatchKey, Path>();
		recursive = recursive1;
		
		if ( recursive ) {
			System.out.format( "Scanning %s ...\n", dir );
			registerAll( dir );
			System.out.println( "Done." );
		} else {
			register( dir );
		}
		
		// enable trace after initial registration
		trace = true;
	}
	
	
	/**
	 * Process all events for keys queued to the watcher
	 */
	void processEvents() {
		
		for ( ;; ) {
			
			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch ( final InterruptedException x ) {
				return;
			}
			
			final Path dir = keys.get( key );
			if ( dir == null ) {
				System.err.println( "WatchKey not recognized!!" );
				continue;
			}
			
			for ( final WatchEvent<?> event : key.pollEvents() ) {
				@SuppressWarnings( "rawtypes" )
				final WatchEvent.Kind kind = event.kind();
				
				// TBD - provide example of how OVERFLOW event is handled
				if ( kind == StandardWatchEventKinds.OVERFLOW ) {
					continue;
				}
				
				// Context for directory entry event is the file name of entry
				final WatchEvent<Path> ev = cast( event );
				final Path name = ev.context();
				final Path child = dir.resolve( name );
				
				// print out event
				System.out.format( "%s: %s\n", event.kind().name(), child );
				
				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if ( recursive && ( kind == StandardWatchEventKinds.ENTRY_CREATE ) ) {
					try {
						// child.toFile().isDirectory()
						if ( Attributes.readBasicFileAttributes( child, NOFOLLOW_LINKS ).isDirectory() ) {
							registerAll( child );
						}
					} catch ( final IOException x ) {
						// ignore to keep sample readbale
					}
				}
			}
			
			// reset key and remove from set if directory no longer accessible
			final boolean valid = key.reset();
			if ( !valid ) {
				keys.remove( key );
				
				// all directories are inaccessible
				if ( keys.isEmpty() ) {
					break;
				}
			}
		}
	}
	
	
	static void usage() {
		
		System.err.println( "usage: java WatchDir [-r] dir" );
		System.exit( -1 );
	}
	
	
	public static void main( final String[] args ) throws IOException {
		
		// parse arguments
		if ( ( args.length == 0 ) || ( args.length > 2 ) ) {
			usage();
		}
		boolean recursive = false;
		int dirArg = 0;
		if ( args[0].equals( "-r" ) ) {
			if ( args.length < 2 ) {
				usage();
			}
			recursive = true;
			dirArg++;
		}
		
		// register directory and process its events
		final Path dir = Paths.get( args[dirArg] );
		new WatchDir( dir, recursive ).processEvents();
	}
}
