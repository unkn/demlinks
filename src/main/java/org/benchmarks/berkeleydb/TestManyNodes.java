/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAG
 */
package org.benchmarks.berkeleydb;

import org.bdb.*;
import org.bdbLevel1.*;
import org.generic.env.*;
import org.toolza.*;



/**
 *
 */
public class TestManyNodes {
	
	private final static String			END								= "END";
	private final static String			START							= "START";
	private final static String			MIDDLE							= "MIDDLE";
	private final static String			ROOT_LIST						= "ROOT_LIST";
	
	// 10000 to 20k seems optimal
	private static final int			HOWMANY_PER_TRANSACTION			= 10000;
	private static final int			HOWMANY_RELATIONSHIPS_FOR_ONE	= 1000000;
	
	private final GenericEnvironment	env;
	private GenericNode					list;
	private GenericNode					middleElement;
	private GenericNode					headElement;
	private GenericNode					tailElement;
	
	
	public TestManyNodes( final GenericEnvironment _env ) {
		assert null != _env;
		env = _env;
	}
	
	
	private static void showMem() {
		System.out.println( "usedmem=" + ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) );
	}
	
	
	public static void main( final String[] args ) {
		showMem();
		final boolean deleteFirst = true;
		final TestManyNodes t2 =
			new TestManyNodes( new BDBEnvironment( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR, deleteFirst ) );
		
		showMem();
		t2.init();
		showMem();
		
		t2.run();
		showMem();
		t2.shutdown();
		showMem();
		Runtime.getRuntime().gc();
		showMem();
	}
	
	
	public void init() {
		GenericTransaction txn = env.beginTransaction();
		try {
			list = env.getNode( ROOT_LIST );
			middleElement = env.getNode( MIDDLE );
			headElement = env.getNode( START );
			tailElement = env.getNode( END );
			if ( null == list ) {
				assert null == middleElement;
				System.out.println( env.getClass() );
				
				list = env.createOrGetNode( ROOT_LIST );
				tailElement = env.createOrGetNode( END );
				headElement = env.createOrGetNode( START );
				middleElement = env.createOrGetNode( MIDDLE );
				env.makeVector( list, headElement );
				System.out.println( "first time creating the relationships..." );
				t3.start();
				for ( int i = 0; i < HOWMANY_RELATIONSHIPS_FOR_ONE; i++ ) {
					env.makeVector( list, env.createNewUniqueNode() );
					if ( ( i % HOWMANY_PER_TRANSACTION ) == 0 ) {
						txn.success();
						txn.finish();
						txn = env.beginTransaction();
					}
					
					if ( i == ( HOWMANY_RELATIONSHIPS_FOR_ONE / 2 ) ) {
						env.makeVector( list, middleElement );
					}
				}
				t3.stop();
				System.out.println( "just created " + A.number( HOWMANY_RELATIONSHIPS_FOR_ONE / 2 ) + " rels, took="
					+ t3.getDeltaPrintFriendly() );
				showMem();
				
				
				System.out.println( "first time creating more relationships..." );
				t3.start();
				for ( int i = 0; i < HOWMANY_RELATIONSHIPS_FOR_ONE; i++ ) {
					env.makeVector( env.createNewUniqueNode(), tailElement );
					if ( ( i % HOWMANY_PER_TRANSACTION ) == 0 ) {
						txn.success();
						txn.finish();
						txn = env.beginTransaction();
					}
					
					if ( i == ( HOWMANY_RELATIONSHIPS_FOR_ONE / 2 ) ) {
						env.makeVector( list, tailElement );
					}
				}
				t3.stop();
				System.out.println( "just created another bunch of " + A.number( HOWMANY_RELATIONSHIPS_FOR_ONE / 2 )
					+ " rels, took=" + t3.getDeltaPrintFriendly() );
				showMem();
			} else {
				assert null != middleElement;
				assert null != headElement;
				assert null != tailElement;
			}// if
			txn.success();
		} finally {
			txn.finish();
			System.out.println( "closed transaction" );
		}
	}
	
	
	public void run() {
		final GenericTransaction t = env.beginTransaction();
		try {
			System.out.println( "run for `" + env.getClass() + "`" );
			System.out.println( "trying isVector():" );
			int repeat = 10;
			do {
				goFind2( list, headElement );
				goFind2( list, middleElement );
				goFind2( list, tailElement );
			} while ( --repeat > 0 );
			showMem();
			
			
			t.success();
		} finally {
			t.finish();
		}
	}
	
	
	public void shutdown() {
		env.shutdown( false );// no need to be in finally, it's already on shutdownhook
	}
	
	private final Timer	t3	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void goFind2( final GenericNode initialNode, final GenericNode terminalNode ) {
		t3.start();
		final boolean ret = env.isVector( initialNode, terminalNode );
		assert ret;
		t3.stop();
		System.out.printf(
			"%10s -> %10s %10s%n",
			env.getName( initialNode ),
			env.getName( terminalNode ),
			t3.getDeltaPrintFriendly() );
	}
	
	
}
