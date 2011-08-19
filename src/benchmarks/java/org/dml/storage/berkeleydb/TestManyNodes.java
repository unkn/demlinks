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
package org.dml.storage.berkeleydb;

import org.JUnitCommons.*;
import org.dml.storage.berkeleydb.generics.*;
import org.dml.storage.commons.*;
import org.junit.*;
import org.q.*;
import org.storage.*;
import org.toolza.*;



/**
 *
 */
public class TestManyNodes
		extends JUnitHooker
{
	
	private final static String		END								= "END";
	private final static String		START							= "START";
	private final static String		MIDDLE							= "MIDDLE";
	private final static String		ROOT_LIST						= "ROOT_LIST";
	
	// 10000 to 20k seems optimal
	private static final int		HOWMANY_PER_TRANSACTION			= 30000;
	private static final int		HOWMANY_RELATIONSHIPS_FOR_ONE	= 1000000;
	private static final boolean	deleteBeforeInit				= true;
	
	private NodeGeneric				list;
	private NodeGeneric				middleElement;
	private NodeGeneric				headElement;
	private NodeGeneric				tailElement;
	private StorageGeneric			storage;
	
	
	private static void showMem() {
		System.out
			.println( "usedmem=" + A.number( ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) ) );
	}
	
	
	private void setUp( final StorageType type, final BDBStorageSubType subType ) {
		showMem();
		final StorageConfig cfg = new StorageConfig();
		cfg.setBDBType( subType );
		cfg.setHomeDir( JUnitConstants.ENVIRONMENT_STORE_DIR + subType );
		cfg.setDeleteBefore( deleteBeforeInit );
		storage = StorageFactory.getStorage( type, cfg );
		System.out.println( storage.getClass() );
		showMem();
	}
	
	
	private void tearDown() {
		showMem();
		if ( null != storage ) {
			storage.shutdown( false );// no need to be in finally, it's already on shutdownhook
		}
		showMem();
		Runtime.getRuntime().gc();
		System.out.println( "after garbage collector" );
		showMem();
	}
	
	
	@Test
	public void goBDBJE() {
		exec( StorageType.BDB, BDBStorageSubType.JE );
	}
	
	
	@Test
	public void goBDBJNI() {
		exec( StorageType.BDB, BDBStorageSubType.JNI );
	}
	
	
	private final Timer	et	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void exec( final StorageType type, final BDBStorageSubType subType ) {
		et.start();
		try {
			setUp( type, subType );
			init();
			run();
		} finally {
			tearDown();
			et.stop();
			System.out.println( "The above " + type + " " + subType + " took: " + et );
		}
	}
	
	
	private void init() {
		showMem();
		TransactionGeneric txn = storage.beginTransaction();
		try {
			list = storage.getNode( ROOT_LIST );
			middleElement = storage.getNode( MIDDLE );
			headElement = storage.getNode( START );
			tailElement = storage.getNode( END );
			if ( null == list ) {
				assert null == middleElement;
				System.out.println( storage.getClass() );
				
				list = storage.createOrGetNode( ROOT_LIST );
				tailElement = storage.createOrGetNode( END );
				headElement = storage.createOrGetNode( START );
				middleElement = storage.createOrGetNode( MIDDLE );
				storage.makeVector( list, headElement );
				System.out.println( "first time creating the relationships..." );
				final int half = HOWMANY_RELATIONSHIPS_FOR_ONE / 2;
				Q.enableInfoReporting( false );
				t3.start();
				int i = 0;
				try {
					for ( i = 0; i < half; i++ ) {
						storage.makeVector( list, storage.createNewUniqueNode() );
						if ( ( i % HOWMANY_PER_TRANSACTION ) == 0 ) {
							txn.success();
							txn.finished();
							txn = storage.beginTransaction();
							System.out.println( "new txn at " + i );
						}
						
						if ( i == ( half / 2 ) ) {
							storage.makeVector( list, middleElement );
						}
					}// for
					
				} finally {
					System.out.println( i );
				}
				t3.stop();
				Q.enableInfoReporting( true );
				System.out.println( "just created " + A.number( half ) + " rels, took=" + t3.getDeltaPrintFriendly() );
				showMem();
				
				
				System.out.println( "creating more relationships..." );
				Q.enableInfoReporting( false );
				t3.start();
				for ( i = 0; i < half; i++ ) {
					storage.makeVector( storage.createNewUniqueNode(), tailElement );
					if ( ( i % HOWMANY_PER_TRANSACTION ) == 0 ) {
						txn.success();
						txn.finished();
						txn = storage.beginTransaction();
					}
					
					if ( i == ( half / 2 ) ) {
						storage.makeVector( list, tailElement );
					}
				}
				t3.stop();
				Q.enableInfoReporting( true );
				System.out.println( "just created another bunch of " + A.number( half ) + " rels, took="
					+ t3.getDeltaPrintFriendly() );
				showMem();
			} else {
				assert null != middleElement;
				assert null != headElement;
				assert null != tailElement;
			}// if
			txn.success();
		} finally {
			txn.finished();
			System.out.println( "closed transaction" );
			showMem();
		}
	}
	
	
	/*
	 * with 100% cpu max limit, rather than the usual 44% where not specified:
	 * ----------------
	 * usedmem=317920
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\dbthatstoresallsequences`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000004`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000005`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000006`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000007`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000008`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000009`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000010`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000011`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000012`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000013`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000014`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000015`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000016`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000017`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000018`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000019`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000020`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000021`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000022`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000023`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000024`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000025`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000026`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000027`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000028`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000029`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000030`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000031`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000032`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000033`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000034`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000035`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000036`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000037`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000038`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000039`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000040`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000041`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000042`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000043`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000044`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000045`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000046`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000047`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000048`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000049`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000050`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000051`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000052`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000053`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000054`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000055`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000056`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000057`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000058`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000059`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000060`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000061`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000062`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000063`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000064`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000065`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000066`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000067`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000068`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000069`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000070`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000071`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000072`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000073`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000074`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000075`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000076`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000077`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000078`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000079`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000080`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000081`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000082`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000083`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000084`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000085`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000086`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000087`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000088`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000089`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000090`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000091`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000092`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000093`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\map(nameString2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\mapOne2Many(nodeLong2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\mapOne2Many(nodeLong2nodeLong)_backward_but_also_primary`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarymap(nameString2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 1052: register environment
	 * BDB1525 1052: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 1052: adding self to registry
	 * BDB1532 1052: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 1052: recovery completed, unlocking
	 * usedmem=2745216
	 * class org.bdbLevel1.BDBEnvironment
	 * first time creating the relationships...
	 * new txn at 0
	 * new txn at 30000
	 * new txn at 60000
	 * new txn at 90000
	 * new txn at 120000
	 * new txn at 150000
	 * new txn at 180000
	 * new txn at 210000
	 * new txn at 240000
	 * new txn at 270000
	 * new txn at 300000
	 * new txn at 330000
	 * new txn at 360000
	 * new txn at 390000
	 * new txn at 420000
	 * new txn at 450000
	 * new txn at 480000
	 * 500000
	 * just created 500,000 rels, took=34,790 ms
	 * usedmem=2277176
	 * first time creating more relationships...
	 * just created another bunch of 500,000 rels, took=36,053 ms
	 * usedmem=974184
	 * closed transaction
	 * usedmem=974184
	 * run for `class org.bdbLevel1.BDBEnvironment`
	 * trying isVector():
	 * ROOT_LIST -> START 63 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * ROOT_LIST -> START 0 ms
	 * ROOT_LIST -> MIDDLE 0 ms
	 * ROOT_LIST -> END 0 ms
	 * usedmem=1068896
	 * usedmem=1068896
	 * usedmem=1068896
	 * usedmem=601856
	 */
	private void run() {
		showMem();
		final TransactionGeneric t = storage.beginTransaction();
		try {
			System.out.println( "run for `" + storage.getClass() + "`" );
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
			t.finished();
			showMem();
		}
	}
	
	private final Timer	t3	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void goFind2( final NodeGeneric parentNode, final NodeGeneric childNode ) {
		t3.start();
		final boolean ret = storage.isVector( parentNode, childNode );
		assert ret;
		t3.stop();
		System.out.printf(
			"%10s -> %10s %10s%n",
			storage.getName( parentNode ),
			storage.getName( childNode ),
			t3.getDeltaPrintFriendly() );
	}
	
	
}
