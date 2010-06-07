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



package org.temporary.bdb.tests;



import java.io.File;
import java.io.IOException;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;



/**
 * this should conclude the fact that a sequence is a key->data pair
 * although the logs increase while get()-ing sequence each time, the key->data
 * of the sequence only exists once
 * 
 */
public class SequenceExample { // arg should be "bin" w/o quotes

	private static final int	EXIT_SUCCESS	= 0;
	private static final int	EXIT_FAILURE	= 1;
	private static final String	DB_NAME			= "sequence.db";
	private static final String	KEY_NAME		= "my_sequence";
	
	public SequenceExample() {

	}
	
	public static void usage() {

		System.out.println( "usage: java " + "je.SequenceExample " + "<dbEnvHomeDirectory>" );
		System.exit( EXIT_FAILURE );
	}
	
	public static void main( String[] argv ) {

		if ( argv.length != 1 ) {
			usage();
			return;
		}
		File envHomeDirectory = new File( argv[0] );
		
		try {
			SequenceExample app = new SequenceExample();
			app.run( envHomeDirectory );
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( EXIT_FAILURE );
		}
		System.exit( EXIT_SUCCESS );
	}
	
	public void run( File envHomeDirectory ) throws DatabaseException, IOException {

		/* Create the environment object. */
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate( true );
		Environment env = new Environment( envHomeDirectory, envConfig );
		
		/* Create the database object. */
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate( true );
		Database db = env.openDatabase( null, DB_NAME, dbConfig );
		
		/* Create the sequence object. */
		SequenceConfig config = new SequenceConfig();
		config.setAllowCreate( true );
		// config.setRange( 0, 99 );
		config.setWrap( true );
		DatabaseEntry key = new DatabaseEntry( KEY_NAME.getBytes( "UTF-8" ) );
		Sequence seq = db.openSequence( null, key, config );
		System.out.println( "Min:" + config.getRangeMin() );
		System.out.println( "Max:" + config.getRangeMax() );
		System.out.println( "init:" + config.getInitialValue() );
		/* Allocate a few sequence numbers. */
		for ( int i = 0; i < 10; i++ ) {
			long seqnum = seq.get( null, 30 );
			System.out.println( "Got sequence number: " + seqnum );
		}
		seq.close();
		
		DatabaseEntry data = new DatabaseEntry();
		DatabaseEntry kkey = new DatabaseEntry( KEY_NAME.getBytes( "UTF-8" ) );
		CursorConfig cursConf = new CursorConfig();
		cursConf.setReadCommitted( false );
		cursConf.setReadUncommitted( true );
		Cursor cur = db.openCursor( null, cursConf );
		try {
			OperationStatus ret = cur.getFirst( kkey, data, null );
			while ( ret == OperationStatus.SUCCESS ) {
				// data.setOffset( 20 );
				// OperationStatus ret=db1.get(null, kkey, data, null);
				System.out.println( "found: " + data );
				data.setData( null );
				ret = cur.getNext( kkey, data, null );
			}
		} finally {
			cur.close();
		}
		
		/* Close all. */

		db.close();
		env.close();
	}
}
