/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 6, 2011 10:25:22 AM
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
package org.rawberkeleydb.tests;

import java.io.*;
import java.util.concurrent.*;

import org.bdb.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.je.*;



/**
 * in order to be able to compile and execute this, due to both db.jar and je-4.1.10.jar using the same name for at least one
 * package, you must do this:
 * Project->Properties->Java Build Path->Order and Export
 * move db.jar on bottom such that je-4.1.10.jar is above it
 * this will make sure je's packages are seen first, but anything using db-native aka jni, won't compile
 */
public class TestBDBJE {
	
	private static final boolean	ENABLE_TRANSACTIONS				= false;
	private static final boolean	ENABLE_LOCKING					= false;
	private static final long		BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	private static final String		secPrefix						= "secondary";
	private static final String		dbName							= "theDBFileName";
	// hash dbtype fails for 1000
	private static final int		HOWMANY							= 111800;
	public static final LockMode	LOCK							= ENABLE_TRANSACTIONS ? LockMode.RMW : LockMode.DEFAULT;
	public static final Durability	DUR								= Durability.COMMIT_NO_SYNC;
	@SuppressWarnings( "unused" )
	public static final LockMode	LOCKMODE						= ENABLE_TRANSACTIONS && ENABLE_LOCKING ? LockMode.RMW
																		: LockMode.DEFAULT;
	
	
	private Environment				env;
	private EnvironmentConfig		envConf;
	private File					storeDir;
	private SecondaryConfig			secAndPriConf;
	private Database				priDb;
	private SecondaryDatabase		secDb;
	private int						leftOverForAdd100				= 0;
	
	
	/*
	 * this is the output for 1800 HOWMANY, yes txns and nolocking:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 951 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 749 ms
	 * adding from [0 to 1800) add100 executed in: 62 ms
	 * adding from [0 to 1800) add100 executed in: 47 ms
	 * adding from [1800 to 3600) add100 executed in: 125 ms
	 * adding from [3600 to 5400) add100 executed in: 126 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 249 ms
	 * all above adds/check (aka part2) executed in 1,483 ms
	 * tearDown took: 94 ms
	 * ------------------------ and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 1,014 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 734 ms
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 125 ms
	 * adding from [3600 to 5400) add100 executed in: 125 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 265 ms
	 * all above adds/check (aka part2) executed in 1,483 ms
	 * tearDown took: 78 ms
	 * ------------------- and one more time:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 936 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 749 ms
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 125 ms
	 * adding from [3600 to 5400) add100 executed in: 125 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 265 ms
	 * all above adds/check (aka part2) executed in 1,498 ms
	 * tearDown took: 78 ms
	 * ================== this is with locking and txns both on:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 983 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 734 ms
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 125 ms
	 * adding from [3600 to 5400) add100 executed in: 126 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 249 ms
	 * all above adds/check (aka part2) executed in 1,468 ms
	 * tearDown took: 94 ms
	 * ------------------------------ and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 951 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 733 ms
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 125 ms
	 * adding from [3600 to 5400) add100 executed in: 126 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 249 ms
	 * all above adds/check (aka part2) executed in 1,467 ms
	 * tearDown took: 78 ms
	 * --------- and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 1,170 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 717 ms
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [1800 to 3600) add100 executed in: 124 ms
	 * adding from [3600 to 5400) add100 executed in: 126 ms
	 * adding from [5400 to 7200) add100 executed in: 125 ms
	 * checking from 0 to 7200 check100 executed in: 250 ms
	 * all above adds/check (aka part2) executed in 1,468 ms
	 * tearDown took: 171 ms
	 * ====================== no txn and no locking:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 920 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 655 ms
	 * adding from [0 to 1800) add100 executed in: 47 ms
	 * adding from [0 to 1800) add100 executed in: 46 ms
	 * adding from [1800 to 3600) add100 executed in: 110 ms
	 * adding from [3600 to 5400) add100 executed in: 109 ms
	 * adding from [5400 to 7200) add100 executed in: 110 ms
	 * checking from 0 to 7200 check100 executed in: 234 ms
	 * all above adds/check (aka part2) executed in 1,327 ms
	 * tearDown took: 94 ms
	 * ---------- again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 921 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 670 ms
	 * adding from [0 to 1800) add100 executed in: 63 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 109 ms
	 * adding from [3600 to 5400) add100 executed in: 125 ms
	 * adding from [5400 to 7200) add100 executed in: 111 ms
	 * checking from 0 to 7200 check100 executed in: 219 ms
	 * all above adds/check (aka part2) executed in 1,328 ms
	 * tearDown took: 109 ms
	 * =================== no txn, yes locking:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 905 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 656 ms
	 * adding from [0 to 1800) add100 executed in: 62 ms
	 * adding from [0 to 1800) add100 executed in: 47 ms
	 * adding from [1800 to 3600) add100 executed in: 109 ms
	 * adding from [3600 to 5400) add100 executed in: 109 ms
	 * adding from [5400 to 7200) add100 executed in: 112 ms
	 * checking from 0 to 7200 check100 executed in: 219 ms
	 * all above adds/check (aka part2) executed in 1,314 ms
	 * tearDown took: 109 ms
	 * ------- again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 920 ms
	 * Database type: BTREE
	 * adding from [0 to 1800) add100 executed in: 672 ms
	 * adding from [0 to 1800) add100 executed in: 62 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 109 ms
	 * adding from [3600 to 5400) add100 executed in: 110 ms
	 * adding from [5400 to 7200) add100 executed in: 110 ms
	 * checking from 0 to 7200 check100 executed in: 234 ms
	 * all above adds/check (aka part2) executed in 1,328 ms
	 * tearDown took: 109 ms
	 */
	
	/*
	 * ===================== 111800 HOWMANY, txn on and lock on:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000001.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000002.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000003.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000004.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000005.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000006.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 968 ms
	 * Database type: BTREE
	 * usedmem=8253544
	 * adding from [0 to 111800) add100 executed in: 9,644 ms
	 * usedmem=63280432
	 * adding from [0 to 111800) add100 executed in: 2,826 ms
	 * usedmem=47603808
	 * adding from [0 to 111800) add100 executed in: 2,513 ms
	 * usedmem=62139712
	 * adding from [111800 to 223600) add100 executed in: 9,143 ms
	 * usedmem=111428680
	 * adding from [223600 to 335400) add100 executed in: 9,236 ms
	 * usedmem=131888984
	 * adding from [335400 to 447200) add100 executed in: 8,458 ms
	 * usedmem=191025712
	 * checking from 0 to 447200 check100 executed in: 10,408 ms
	 * usedmem=182286136
	 * all above adds/check (aka part2) executed in 52,228 ms
	 * tearDown took: 188 ms
	 * ------------- again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000001.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000002.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000003.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000004.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000005.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000006.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 920 ms
	 * Database type: BTREE
	 * usedmem=8253504
	 * adding from [0 to 111800) add100 executed in: 9,641 ms
	 * usedmem=63278464
	 * adding from [0 to 111800) add100 executed in: 2,811 ms
	 * usedmem=47599816
	 * adding from [0 to 111800) add100 executed in: 2,482 ms
	 * usedmem=62134896
	 * adding from [111800 to 223600) add100 executed in: 9,205 ms
	 * usedmem=111684080
	 * adding from [223600 to 335400) add100 executed in: 9,230 ms
	 * usedmem=132049496
	 * adding from [335400 to 447200) add100 executed in: 8,535 ms
	 * usedmem=191925384
	 * checking from 0 to 447200 check100 executed in: 10,455 ms
	 * usedmem=184823432
	 * all above adds/check (aka part2) executed in 52,359 ms
	 * tearDown took: 157 ms
	 * ===================== same ammount, no txn and no locking:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000001.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000002.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000003.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000004.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000005.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000006.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 936 ms
	 * Database type: BTREE
	 * usedmem=8176224
	 * adding from [0 to 111800) add100 executed in: 8,221 ms
	 * usedmem=32742856
	 * adding from [0 to 111800) add100 executed in: 2,016 ms
	 * usedmem=33471760
	 * adding from [0 to 111800) add100 executed in: 1,919 ms
	 * usedmem=33745088
	 * adding from [111800 to 223600) add100 executed in: 7,770 ms
	 * usedmem=66579760
	 * adding from [223600 to 335400) add100 executed in: 8,035 ms
	 * usedmem=95490864
	 * adding from [335400 to 447200) add100 executed in: 7,536 ms
	 * usedmem=100827160
	 * checking from 0 to 447200 check100 executed in: 8,209 ms
	 * usedmem=121744792
	 * all above adds/check (aka part2) executed in 43,706 ms
	 * tearDown took: 157 ms
	 * ------------ again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000000.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000001.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000002.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000003.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000004.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000005.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\00000006.jdb`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.info.0`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\je.lck`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * environment open took: 904 ms
	 * Database type: BTREE
	 * usedmem=8163272
	 * adding from [0 to 111800) add100 executed in: 8,221 ms
	 * usedmem=32749224
	 * adding from [0 to 111800) add100 executed in: 2,045 ms
	 * usedmem=33478760
	 * adding from [0 to 111800) add100 executed in: 1,935 ms
	 * usedmem=33752544
	 * adding from [111800 to 223600) add100 executed in: 7,723 ms
	 * usedmem=66901016
	 * adding from [223600 to 335400) add100 executed in: 8,036 ms
	 * usedmem=95525640
	 * adding from [335400 to 447200) add100 executed in: 7,536 ms
	 * usedmem=100752696
	 * checking from 0 to 447200 check100 executed in: 8,284 ms
	 * usedmem=121781520
	 * all above adds/check (aka part2) executed in 43,780 ms
	 * tearDown took: 220 ms
	 */
	@Before
	public void setUp() {
		storeDir = new File( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR );
		F.delFileOrTree( storeDir );
		storeDir.mkdirs();
		envConf = new EnvironmentConfig();
		
	}
	
	
	
	private void setupBDBNativeEnv() throws DatabaseException {
		envConf.setAllowCreate( true );
		envConf.setLocking( ENABLE_LOCKING );
		// envConf.setInitializeCDB( true );//this1of2
		// envConf.setCDBLockAllDatabases( true );//this2of2 are unique and go together
		
		envConf.setLocking( ENABLE_TRANSACTIONS );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds, TimeUnit.MICROSECONDS );
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		envConf.setDurability( DUR );
		// envConf.setTxnNoSync( true );// XXX: should be false for consistency
		// envConf.setTxnWriteNoSync( true );// can't use both
		// envConf.setTxnNotDurable( true );
		// envConf.setTxnNoWait( true );
		// envConf.setTxnSnapshot( ENABLE_TRANSACTIONS );
		envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds, TimeUnit.MICROSECONDS );
		//
		// // envConf.setLogDirectory( logDirectory )
		// envConf.setMaxLogFileSize( Integer.MAX_VALUE );//this allocated 2gig log
		// envConf.setMaxLogFileSize( 10 * 1024 * 1024 );// 10meg alloc
		
		// envConf.setInitializeRegions( false );// XXX: maybe experiment with this, unsure
		// envConf.setInitializeReplication( false );// for now
		// envConf.setInitialMutexes( 100 );// must investigate 10 is not enough!
		
		// envConf.setJoinEnvironment( false );
		//
		//
		// envConf.setPrivate( BerkEnv.once );// this fails true as long as we have that single-open constraint
		//
		//
		// envConf.setUseEnvironment( false );
		// envConf.setUseEnvironmentRoot( false );
		
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		timed.start();
		env = new Environment( storeDir, envConf );
		timed.stop();
		System.out.println( "environment open took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	private void setupBDBNativeDb() {
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( false );// not needed tho, only populated if sec is empty but pri isn't
		System.out.println( "Database type: BTREE" );
		// secAndPriConf.setChecksum( true );// this has virtually no impact
		// // secConf.setEncrypted( password )
		// secAndPriConf.setMultiversion( false );
		// secAndPriConf.setReverseSplitOff( false );
		// secAndPriConf.setTransactionNotDurable( false );
		// secAndPriConf.setUnsortedDuplicates( false );
		// secAndPriConf.setDeferredWrite( false );
		// secAndPriConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secAndPriConf.setExclusiveCreate( false );
		secAndPriConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secAndPriConf.setReadOnly( false );
		secAndPriConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secAndPriConf.setTransactional( ENABLE_TRANSACTIONS );
		
		assert !secAndPriConf.getSortedDuplicates();
		// assert !secAndPriConf.getUnsortedDuplicates();
		// assert !secAndPriConf.getReverseSplitOff();
		secAndPriConf.setKeyCreator( new SecondaryKeyCreator() {
			
			@Override
			public boolean createSecondaryKey( final SecondaryDatabase secondary, final com.sleepycat.je.DatabaseEntry key,
												final com.sleepycat.je.DatabaseEntry data,
												final com.sleepycat.je.DatabaseEntry result ) {
				// if ( data.getData().length != data.getSize() ) {
				// XXX: this happens with bdb native (ie. .dll version) but not with bdb je aka java edition version
				// Q.warn( "len=" + data.getData().length + " size=" + data.getSize() + " data=" + data );
				// }
				// assert data.getData().length == data.getSize() : "len=" + data.getData().length + " size=" + data.getSize()
				// + " data=" + data;
				// XXX: looks like length and size can differ ie. 8 vs 100, maybe latter is with padding
				result.setData( data.getData() );
				result.setSize( data.getSize() );// this seems useless but let above assert check that for us
				
				// System.out.println( key + "!" + data + "!" + result );
				return true;
			}
		} );
		
		
		
		priDb = env.openDatabase(
		// BETransaction.getCurrentTransaction( _env ),
			null,
			dbName,
			secAndPriConf/*
						 * using the same conf from secondary, but it will be treated as just a simple DatabaseConfig
						 * instead
						 */
		);
		secDb = env.openSecondaryDatabase( null,
		// BETransaction.getCurrentTransaction( _env ),
			secPrefix + dbName,
			priDb,
			secAndPriConf );
	}
	
	
	@After
	public void tearDown() throws DatabaseException {
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		timed.start();
		if ( null != secDb ) {
			secDb.close();
		}
		if ( null != priDb ) {
			priDb.close();
		}
		
		if ( null != env ) {
			env.close();
		}
		timed.stop();
		System.out.println( "tearDown took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	private void add100( final boolean firstTime, final boolean cont ) throws DatabaseException {
		beginTxn();
		addCheckTimer.start();
		try {
			// final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			// final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			int initial = 0;
			if ( cont ) {
				initial = leftOverForAdd100;
			}
			int i;
			final int final_ = ( initial + HOWMANY );
			System.out.print( "adding from [" + initial + " to " + final_ + ") " );
			for ( i = initial; i < final_; i++ ) {
				final String key = "" + i;
				// final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				LongBinding.longToEntry( i, deData );
				OperationStatus ret = null;
				try {
					ret = priDb.putNoOverwrite( t, deKey, deData );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ( ( firstTime || cont ) && ( ret == OperationStatus.SUCCESS ) )
					|| ( ( !firstTime ) && ( ret == OperationStatus.KEYEXIST ) );
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			leftOverForAdd100 = i;
			// System.out.println( "leftover=" + leftOverForAdd100 );
			
			// System.out.println( "committing" );
			commit();
		} catch ( final Throwable t2 ) {
			abort();
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "add100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	private Transaction	t;
	
	
	private void beginTxn() throws DatabaseException {
		t = null;
		if ( ENABLE_TRANSACTIONS ) {
			final TransactionConfig txnConfig = new TransactionConfig();
			txnConfig.setNoWait( true );
			// txnConfig.setSync( true );
			// txnConfig.set
			t = env.beginTransaction( null, txnConfig );
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void abort() throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.abort();
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void commit() throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.commit();
		}
	}
	
	final Timer	addCheckTimer	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void check100() throws DatabaseException {
		beginTxn();
		addCheckTimer.start();
		try {
			// final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			// final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			
			System.out.print( "checking from 0 to " + leftOverForAdd100 + " " );
			for ( int i = 0; i < leftOverForAdd100; i++ ) {
				final String key = "" + i;
				// final Long data = new Long( i );
				final DatabaseEntry deKey = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
				final DatabaseEntry deData = new DatabaseEntry();
				LongBinding.longToEntry( i, deData );
				OperationStatus ret = null;
				try {
					ret = priDb.get( t, deKey, deData, LOCKMODE );
				} catch ( final DatabaseException e ) {
					Q.rethrow( e );
				}
				// System.out.println( ret );
				assert ret == OperationStatus.SUCCESS;
				assert i == LongBinding.entryToLong( deData );
				// assert i != data2;
				// ret.equals( OperationStatus.KEYEXIST )
				
			}// for
			
			// System.out.println( "committing" );
			commit();
		} catch ( final Throwable t2 ) {
			abort();
			
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "check100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	@Test
	public void testBTree() throws DatabaseException {
		// only BTree exists in BDB JE
		setupBDBNativeEnv();
		setupBDBNativeDb();
		part2();
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	private void part2() throws DatabaseException {
		leftOverForAdd100 = 0;
		final Timer t1 = new Timer( Timer.TYPE.MILLIS );
		t1.start();
		F.showMem();
		add100( true, true );
		F.showMem();
		add100( false, false );
		F.showMem();
		add100( false, false );
		F.showMem();
		add100( false, true );
		F.showMem();
		add100( false, true );
		F.showMem();
		add100( false, true );
		F.showMem();
		check100();
		F.showMem();
		t1.stop();
		System.out.println( "all above adds/check (aka part2) executed in " + t1.getDeltaPrintFriendly() );
	}
	
	
}
