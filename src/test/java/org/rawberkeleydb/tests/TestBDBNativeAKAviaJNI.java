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

import org.bdb.*;
import org.berkeleydb.*;
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 * with 100%cpu limit:
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
 * BDB2525 No log files found
 * environment open took: 31 ms
 * Database type: DatabaseType.BTREE
 * usedmem=4118520
 * adding from [0 to 111800) add100 executed in: 1,763 ms
 * usedmem=4224168
 * adding from [0 to 111800) add100 executed in: 407 ms
 * usedmem=1099752
 * adding from [0 to 111800) add100 executed in: 390 ms
 * usedmem=2519952
 * adding from [111800 to 223600) add100 executed in: 1,716 ms
 * usedmem=1099728
 * adding from [223600 to 335400) add100 executed in: 1,702 ms
 * usedmem=4224264
 * adding from [335400 to 447200) add100 executed in: 1,766 ms
 * usedmem=2898744
 * checking from 0 to 447200 check100 executed in: 1,797 ms
 * usedmem=1196136
 * all above adds/check (aka part2) executed in 9,541 ms
 * tearDown took: 547 ms
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
 * BDB2525 No log files found
 * environment open took: 0 ms
 * Database type: DatabaseType.HASH
 * usedmem=1364632
 * adding from [0 to 111800) add100 executed in: 7,457 ms
 * usedmem=4563064
 * adding from [0 to 111800) add100 executed in: 1,327 ms
 * usedmem=1344472
 * adding from [0 to 111800) add100 executed in: 1,328 ms
 * usedmem=2764408
 * adding from [111800 to 223600) add100 executed in: 8,097 ms
 * usedmem=1439136
 * adding from [223600 to 335400) add100 executed in: 7,021 ms
 * usedmem=4563000
 * adding from [335400 to 447200) add100 executed in: 10,185 ms
 * usedmem=3143088
 * checking from 0 to 447200 check100 executed in: 9,179 ms
 * usedmem=1439120
 * all above adds/check (aka part2) executed in 44,594 ms
 * tearDown took: 750 ms
 * ============== again now @ 100%:
 * Preloading depended library first: `libdb52`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
 * BDB2525 No log files found
 * environment open took: 31 ms
 * Database type: DatabaseType.BTREE
 * usedmem=5419864
 * adding from [0 to 111800) add100 executed in: 2,044 ms
 * usedmem=15363392
 * adding from [0 to 111800) add100 executed in: 391 ms
 * usedmem=13866864
 * adding from [0 to 111800) add100 executed in: 390 ms
 * usedmem=13327512
 * adding from [111800 to 223600) add100 executed in: 1,840 ms
 * usedmem=20775088
 * adding from [223600 to 335400) add100 executed in: 1,827 ms
 * usedmem=28336856
 * adding from [335400 to 447200) add100 executed in: 1,842 ms
 * usedmem=5073752
 * checking from 0 to 447200 check100 executed in: 1,654 ms
 * usedmem=16183472
 * all above adds/check (aka part2) executed in 9,988 ms
 * tearDown took: 890 ms
 * =====manually checked:
 * env home dir size: 48.8 MB (51,240,960 bytes)
 */
public class TestBDBNativeAKAviaJNI {
	
	// fastest with txn on and lock on, mvc off, dur off; 9,848 ms
	// fastest with txn on and mvc on, lock off, durable off; 15,199 ms - not fastest anymore
	// when locking is on, mvc must be off, else takes too long to check & unable to allocate space...(forgot exact msg)
	//
	
	// set all these 4 to true for consistency, but also lack of speed; all to false for max speed
	public static final boolean		ENABLE_TRANSACTIONS				= true;
	// when the dur is on: 41,968 ms when off: 9,848 ms
	@SuppressWarnings( "unused" )
	public static final boolean		DURABLE_TXNS					= false ? true : !ENABLE_TRANSACTIONS;
	public static final boolean		ENABLE_LOCKING					= true;
	@SuppressWarnings( "unused" )
	// only enabled when transactions are enabled, and if that first bool is true
	public static final boolean		MVC								= false ? ENABLE_TRANSACTIONS : false;
	
	
	// hash dbtype fails for 1000; 800 works though
	// if ie. 1800 then on hash, this err: BDB0689 theDBFileName page 10 is on free list with type 13;
	// if all those 4 above are set to false(when MVC is false) that error doesn't happen; with MVC true this happens:
	/*
	 * BDB0689 secondarytheDBFileName page 3 is on free list with type 13
	 * junitBDBJNI:: BDB0061 PANIC: Invalid argument
	 * panic event
	 * junitBDBJNI:: BDB0060 PANIC: fatal region error detected; run recovery
	 * panic event
	 * junitBDBJNI:: BDB0060 PANIC: fatal region error detected; run recovery
	 * panic event
	 */
	private static final int		HOWMANY							= 111800;
	
	private static final long		BDBLOCK_TIMEOUT_MicroSeconds	= 3 * 1000000;
	private static final String		secPrefix						= "secondary";
	private static final String		dbName							= "theDBFileName";
	
	@SuppressWarnings( "unused" )
	public static final LockMode	LOCKMODE						= ENABLE_TRANSACTIONS && ENABLE_LOCKING ? LockMode.RMW
																		: LockMode.DEFAULT;
	private static final boolean	NOWAIT							= false;
	
	
	protected Environment			env;
	private EnvironmentConfig		envConf;
	private File					storeDir;
	private SecondaryConfig			secAndPriConf;
	protected Database				priDb;
	private SecondaryDatabase		secDb;
	private int						leftOverForAdd100				= 0;
	
	static {
		PreloadBDBNativeLibraries.initIfNotInited();
	}
	
	
	/*
	 * this is with 1800 HOWMANY, and all the 4 options set to false:
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 171 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 17 ms
	 * adding from [1800 to 3600) add100 executed in: 374 ms
	 * adding from [3600 to 5400) add100 executed in: 297 ms
	 * adding from [5400 to 7200) add100 executed in: 203 ms
	 * checking from 0 to 7200 check100 executed in: 951 ms
	 * all above adds/check (aka part2) executed in 2,044 ms
	 * tearDown took: 235 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 15 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [1800 to 3600) add100 executed in: 280 ms
	 * adding from [3600 to 5400) add100 executed in: 484 ms
	 * adding from [5400 to 7200) add100 executed in: 2,123 ms
	 * checking from 0 to 7200 check100 executed in: 141 ms
	 * all above adds/check (aka part2) executed in 3,153 ms
	 * tearDown took: 359 ms
	 * -------------------- and one more:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 234 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [1800 to 3600) add100 executed in: 94 ms
	 * adding from [3600 to 5400) add100 executed in: 359 ms
	 * adding from [5400 to 7200) add100 executed in: 234 ms
	 * checking from 0 to 7200 check100 executed in: 640 ms
	 * all above adds/check (aka part2) executed in 1,624 ms
	 * tearDown took: 157 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 15 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 78 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 234 ms
	 * adding from [3600 to 5400) add100 executed in: 499 ms
	 * adding from [5400 to 7200) add100 executed in: 2,185 ms
	 * checking from 0 to 7200 check100 executed in: 110 ms
	 * all above adds/check (aka part2) executed in 3,153 ms
	 * tearDown took: 359 ms
	 * =============== now with txns on, all other on false:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 1020: register environment
	 * BDB1525 1020: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 1020: adding self to registry
	 * BDB1532 1020: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 1020: recovery completed, unlocking
	 * environment open took: 141 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 188 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 390 ms
	 * adding from [3600 to 5400) add100 executed in: 453 ms
	 * adding from [5400 to 7200) add100 executed in: 281 ms
	 * checking from 0 to 7200 check100 executed in: 562 ms
	 * all above adds/check (aka part2) executed in 1,921 ms
	 * tearDown took: 109 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 1020: register environment
	 * BDB1525 1020: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 1020: adding self to registry
	 * BDB1532 1020: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 1020: recovery completed, unlocking
	 * environment open took: 0 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 141 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 3,635 ms
	 * adding from [3600 to 5400) add100 executed in: 28,066 ms
	 * adding from [5400 to 7200) add100 executed in: 33,499 ms
	 * checking from 0 to 7200 check100 executed in: 781 ms
	 * all above adds/check (aka part2) executed in 66,169 ms
	 * tearDown took: 234 ms
	 * ============ now with txns on and durable on:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 1480: register environment
	 * BDB1525 1480: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 1480: adding self to registry
	 * BDB1532 1480: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 1480: recovery completed, unlocking
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 171 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 15 ms
	 * adding from [1800 to 3600) add100 executed in: 220 ms
	 * adding from [3600 to 5400) add100 executed in: 327 ms
	 * adding from [5400 to 7200) add100 executed in: 219 ms
	 * checking from 0 to 7200 check100 executed in: 514 ms
	 * all above adds/check (aka part2) executed in 1,514 ms
	 * tearDown took: 142 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 1480: register environment
	 * BDB1525 1480: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 1480: adding self to registry
	 * BDB1532 1480: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 1480: recovery completed, unlocking
	 * environment open took: 0 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 109 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 1,888 ms
	 * adding from [3600 to 5400) add100 executed in: 7,832 ms
	 * adding from [5400 to 7200) add100 executed in: 12,933 ms
	 * checking from 0 to 7200 check100 executed in: 563 ms
	 * all above adds/check (aka part2) executed in 23,372 ms
	 * tearDown took: 296 ms
	 * --------------- and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 872: register environment
	 * BDB1525 872: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 872: adding self to registry
	 * BDB1532 872: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 872: recovery completed, unlocking
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 171 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [1800 to 3600) add100 executed in: 202 ms
	 * adding from [3600 to 5400) add100 executed in: 281 ms
	 * adding from [5400 to 7200) add100 executed in: 312 ms
	 * checking from 0 to 7200 check100 executed in: 562 ms
	 * all above adds/check (aka part2) executed in 1,623 ms
	 * tearDown took: 111 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 872: register environment
	 * BDB1525 872: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 872: adding self to registry
	 * BDB1532 872: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 872: recovery completed, unlocking
	 * environment open took: 0 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 109 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 15 ms
	 * adding from [1800 to 3600) add100 executed in: 1,295 ms
	 * adding from [3600 to 5400) add100 executed in: 7,583 ms
	 * adding from [5400 to 7200) add100 executed in: 14,119 ms
	 * checking from 0 to 7200 check100 executed in: 500 ms
	 * all above adds/check (aka part2) executed in 23,653 ms
	 * tearDown took: 343 ms
	 * =============== same but changed order:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 2272: register environment
	 * BDB1525 2272: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 2272: adding self to registry
	 * BDB1532 2272: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 2272: recovery completed, unlocking
	 * environment open took: 125 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 203 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [1800 to 3600) add100 executed in: 1,279 ms
	 * adding from [3600 to 5400) add100 executed in: 5,570 ms
	 * adding from [5400 to 7200) add100 executed in: 11,436 ms
	 * checking from 0 to 7200 check100 executed in: 500 ms
	 * all above adds/check (aka part2) executed in 19,036 ms
	 * tearDown took: 219 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 2272: register environment
	 * BDB1525 2272: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 2272: adding self to registry
	 * BDB1532 2272: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 2272: recovery completed, unlocking
	 * environment open took: 0 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 94 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [1800 to 3600) add100 executed in: 234 ms
	 * adding from [3600 to 5400) add100 executed in: 327 ms
	 * adding from [5400 to 7200) add100 executed in: 219 ms
	 * checking from 0 to 7200 check100 executed in: 561 ms
	 * all above adds/check (aka part2) executed in 1,483 ms
	 * tearDown took: 157 ms
	 * =========== txn on dur on mvc on, lock still off:
	 * this causes hash to fail;
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB1524 740: register environment
	 * BDB1525 740: creating .\bin\JUnit.tempDb\__db.register
	 * BDB1526 740: adding self to registry
	 * BDB1532 740: locking slot 00 at offset 0
	 * BDB2525 No log files found
	 * BDB1533 740: recovery completed, unlocking
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 171 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [1800 to 3600) add100 executed in: 218 ms
	 * adding from [3600 to 5400) add100 executed in: 313 ms
	 * adding from [5400 to 7200) add100 executed in: 218 ms
	 * checking from 0 to 7200 check100 executed in: 406 ms
	 * all above adds/check (aka part2) executed in 1,405 ms
	 * tearDown took: 140 ms
	 * ==================== all 4 on:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 171 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [1800 to 3600) add100 executed in: 219 ms
	 * adding from [3600 to 5400) add100 executed in: 359 ms
	 * adding from [5400 to 7200) add100 executed in: 203 ms
	 * checking from 0 to 7200 check100 executed in: 967 ms
	 * all above adds/check (aka part2) executed in 1,966 ms
	 * tearDown took: 251 ms
	 * ---------- and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 188 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 18 ms
	 * adding from [1800 to 3600) add100 executed in: 234 ms
	 * adding from [3600 to 5400) add100 executed in: 297 ms
	 * adding from [5400 to 7200) add100 executed in: 218 ms
	 * checking from 0 to 7200 check100 executed in: 967 ms
	 * all above adds/check (aka part2) executed in 1,953 ms
	 * tearDown took: 360 ms
	 * 
	 * ================== all on except mvc which is off:
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 172 ms
	 * adding from [0 to 1800) add100 executed in: 38 ms
	 * adding from [0 to 1800) add100 executed in: 11 ms
	 * adding from [1800 to 3600) add100 executed in: 218 ms
	 * adding from [3600 to 5400) add100 executed in: 344 ms
	 * adding from [5400 to 7200) add100 executed in: 202 ms
	 * checking from 0 to 7200 check100 executed in: 1,046 ms
	 * all above adds/check (aka part2) executed in 2,046 ms
	 * tearDown took: 547 ms
	 * -----------and again:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 187 ms
	 * adding from [0 to 1800) add100 executed in: 32 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [1800 to 3600) add100 executed in: 249 ms
	 * adding from [3600 to 5400) add100 executed in: 312 ms
	 * adding from [5400 to 7200) add100 executed in: 234 ms
	 * checking from 0 to 7200 check100 executed in: 1,108 ms
	 * all above adds/check (aka part2) executed in 2,138 ms
	 * tearDown took: 188 ms
	 * ----------------- and again with allowing hash now:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 124 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 187 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 34 ms
	 * adding from [1800 to 3600) add100 executed in: 1,872 ms
	 * adding from [3600 to 5400) add100 executed in: 5,648 ms
	 * adding from [5400 to 7200) add100 executed in: 12,512 ms
	 * checking from 0 to 7200 check100 executed in: 844 ms
	 * all above adds/check (aka part2) executed in 21,129 ms
	 * tearDown took: 234 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 0 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 110 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 15 ms
	 * adding from [1800 to 3600) add100 executed in: 219 ms
	 * adding from [3600 to 5400) add100 executed in: 281 ms
	 * adding from [5400 to 7200) add100 executed in: 187 ms
	 * checking from 0 to 7200 check100 executed in: 952 ms
	 * all above adds/check (aka part2) executed in 1,795 ms
	 * tearDown took: 250 ms
	 * ----------- and again with hash as second:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * adding from [0 to 1800) add100 executed in: 203 ms
	 * adding from [0 to 1800) add100 executed in: 31 ms
	 * adding from [0 to 1800) add100 executed in: 17 ms
	 * adding from [1800 to 3600) add100 executed in: 237 ms
	 * adding from [3600 to 5400) add100 executed in: 280 ms
	 * adding from [5400 to 7200) add100 executed in: 190 ms
	 * checking from 0 to 7200 check100 executed in: 1,378 ms
	 * all above adds/check (aka part2) executed in 2,356 ms
	 * tearDown took: 204 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 0 ms
	 * Database type: DatabaseType.HASH
	 * adding from [0 to 1800) add100 executed in: 125 ms
	 * adding from [0 to 1800) add100 executed in: 16 ms
	 * adding from [0 to 1800) add100 executed in: 15 ms
	 * adding from [1800 to 3600) add100 executed in: 1,061 ms
	 * adding from [3600 to 5400) add100 executed in: 4,775 ms
	 * adding from [5400 to 7200) add100 executed in: 12,562 ms
	 * checking from 0 to 7200 check100 executed in: 1,125 ms
	 * all above adds/check (aka part2) executed in 19,679 ms
	 * tearDown took: 143 ms
	 */
	
	
	/*
	 * ============= with 111800 HOWMANY, and all 4 options on:
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4128048
	 * adding from [0 to 111800) add100 executed in: 12,277 ms
	 * usedmem=4223800
	 * adding from [0 to 111800) add100 executed in: 4,400 ms
	 * usedmem=1099648
	 * adding from [0 to 111800) add100 executed in: 3,636 ms
	 * usedmem=2519712
	 * adding from [111800 to 223600) add100 executed in: 13,261 ms
	 * usedmem=1099640
	 * adding from [223600 to 335400) add100 executed in: 16,116 ms
	 * usedmem=4223816
	 * adding from [335400 to 447200) add100 executed in: 13,869 ms
	 * usedmem=2898440
	 * checking from 0 to 447200 junitBDBJNI:: BDB2034 unable to allocate memory for mutex; resize mutex region
	 * ================ with mvc off:
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4193320
	 * adding from [0 to 111800) add100 executed in: 12,137 ms
	 * usedmem=4318392
	 * adding from [0 to 111800) add100 executed in: 5,258 ms
	 * usedmem=1194368
	 * adding from [0 to 111800) add100 executed in: 3,792 ms
	 * usedmem=2519712
	 * adding from [111800 to 223600) add100 executed in: 12,528 ms
	 * usedmem=1194400
	 * adding from [223600 to 335400) add100 executed in: 16,258 ms
	 * usedmem=4318456
	 * adding from [335400 to 447200) add100 executed in: 13,027 ms
	 * usedmem=2898472
	 * checking from 0 to 447200 junitBDBJNI:: BDB2034 unable to allocate memory for mutex; resize mutex region
	 * ================== all off:
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4188848
	 * adding from [0 to 111800) add100 executed in: 14,118 ms
	 * usedmem=4301184
	 * adding from [0 to 111800) add100 executed in: 4,259 ms
	 * usedmem=1094064
	 * adding from [0 to 111800) add100 executed in: 3,293 ms
	 * usedmem=2509032
	 * adding from [111800 to 223600) add100 executed in: 13,870 ms
	 * usedmem=5210424
	 * adding from [223600 to 335400) add100 executed in: 13,651 ms
	 * usedmem=3200808
	 * adding from [335400 to 447200) add100 executed in: 14,649 ms
	 * usedmem=1191192
	 * checking from 0 to 447200 check100 executed in: 16,412 ms
	 * usedmem=1671328
	 * all above adds/check (aka part2) executed in 80,252 ms
	 * tearDown took: 126 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 15 ms
	 * Database type: DatabaseType.HASH
	 * usedmem=1808240
	 * adding from [0 to 111800)
	 * ============== txn on, recovery on, all 3 other off:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4125192
	 * adding from [0 to 111800) add100 executed in: 127,313 ms
	 * usedmem=4226200
	 * adding from [0 to 111800) add100 executed in: 4,697 ms
	 * usedmem=1101768
	 * adding from [0 to 111800) add100 executed in: 2,793 ms
	 * usedmem=2521992
	 * adding from [111800 to 223600)
	 * stopped it
	 * ================= txn on, dur off(fixed dur from now on, above it still not fixed meaning above:
	 * ============ `dur on` means it's off), yes locking, no mvc:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4144784
	 * adding from [0 to 111800) add100 executed in: 12,386 ms
	 * usedmem=4226560
	 * adding from [0 to 111800) add100 executed in: 5,009 ms
	 * usedmem=1102016
	 * adding from [0 to 111800) add100 executed in: 3,885 ms
	 * usedmem=2522216
	 * adding from [111800 to 223600) add100 executed in: 12,029 ms
	 * usedmem=1102048
	 * adding from [223600 to 335400) add100 executed in: 15,868 ms
	 * usedmem=4226392
	 * adding from [335400 to 447200) add100 executed in: 13,823 ms
	 * usedmem=2900872
	 * checking from 0 to 447200 junitBDBJNI:: BDB2034 unable to allocate memory for mutex; resize mutex region
	 * ============ same but disabled locking:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.0.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.1.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.10.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.11.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.12.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.13.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.14.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.15.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.16.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.17.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.18.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.19.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.2.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.20.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.21.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.22.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.23.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.24.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.25.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.26.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.27.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.28.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.29.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.3.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.30.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.31.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.32.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.33.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.34.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.35.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.36.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.4.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.5.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.6.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.7.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.8.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.freezer.0.9.4K`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4194824
	 * adding from [0 to 111800) add100 executed in: 12,667 ms
	 * usedmem=4321104
	 * adding from [0 to 111800) add100 executed in: 4,712 ms
	 * usedmem=1196696
	 * adding from [0 to 111800) add100 executed in: 3,480 ms
	 * usedmem=2522216
	 * adding from [111800 to 223600) add100 executed in: 13,651 ms
	 * usedmem=1196768
	 * adding from [223600 to 335400) add100 executed in: 16,210 ms
	 * usedmem=4321112
	 * adding from [335400 to 447200) add100 executed in: 13,838 ms
	 * usedmem=2901008
	 * checking from 0 to 447200 junitBDBJNI:: BDB2055 Lock table is out of available lock entries
	 * tearDown took: 63 ms
	 * ======================= txn on, mvc on, lock off, txndur off:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4121208
	 * adding from [0 to 111800) add100 executed in: 11,216 ms
	 * usedmem=4226368
	 * adding from [0 to 111800) add100 executed in: 4,634 ms
	 * usedmem=1102000
	 * adding from [0 to 111800) add100 executed in: 3,511 ms
	 * usedmem=2522200
	 * adding from [111800 to 223600) add100 executed in: 12,278 ms
	 * usedmem=1101936
	 * adding from [223600 to 335400) add100 executed in: 15,523 ms
	 * usedmem=4226376
	 * adding from [335400 to 447200) add100 executed in: 12,107 ms
	 * usedmem=2900856
	 * checking from 0 to 447200 check100 executed in: 23,151 ms
	 * usedmem=1198376
	 * all above adds/check (aka part2) executed in 82,420 ms
	 * tearDown took: 64 ms
	 * =============== same but with direct db io set to false, log io to true still:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4121184
	 * adding from [0 to 111800) add100 executed in: 6,677 ms
	 * usedmem=4214056
	 * adding from [0 to 111800) add100 executed in: 1,849 ms
	 * usedmem=1101576
	 * adding from [0 to 111800) add100 executed in: 1,483 ms
	 * usedmem=2516376
	 * adding from [111800 to 223600) add100 executed in: 7,083 ms
	 * usedmem=1101592
	 * adding from [223600 to 335400) add100 executed in: 11,672 ms
	 * usedmem=4214152
	 * adding from [335400 to 447200) add100 executed in: 9,314 ms
	 * usedmem=2893640
	 * checking from 0 to 447200 check100 executed in: 7,349 ms
	 * usedmem=1197584
	 * all above adds/check (aka part2) executed in 45,427 ms
	 * tearDown took: 375 ms
	 * ========== same but both db&log direct i/o set to false:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4125440
	 * adding from [0 to 111800) add100 executed in: 6,630 ms
	 * usedmem=4214296
	 * adding from [0 to 111800) add100 executed in: 1,842 ms
	 * usedmem=1101616
	 * adding from [0 to 111800) add100 executed in: 1,500 ms
	 * usedmem=2516624
	 * adding from [111800 to 223600) add100 executed in: 7,037 ms
	 * usedmem=1101704
	 * adding from [223600 to 335400) add100 executed in: 11,797 ms
	 * usedmem=4214488
	 * adding from [335400 to 447200) add100 executed in: 9,267 ms
	 * usedmem=2893968
	 * checking from 0 to 447200 check100 executed in: 7,286 ms
	 * usedmem=1197728
	 * all above adds/check (aka part2) executed in 45,359 ms
	 * tearDown took: 500 ms
	 * ============== same but with nommap :
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4059384
	 * adding from [0 to 111800) add100 executed in: 6,677 ms
	 * usedmem=4215728
	 * adding from [0 to 111800) add100 executed in: 1,841 ms
	 * usedmem=1101656
	 * adding from [0 to 111800) add100 executed in: 1,483 ms
	 * usedmem=2517176
	 * adding from [111800 to 223600) add100 executed in: 7,133 ms
	 * usedmem=1101656
	 * adding from [223600 to 335400) add100 executed in: 11,872 ms
	 * usedmem=4215800
	 * adding from [335400 to 447200) add100 executed in: 9,363 ms
	 * usedmem=2894720
	 * checking from 0 to 447200 check100 executed in: 7,257 ms
	 * usedmem=1197776
	 * all above adds/check (aka part2) executed in 45,642 ms
	 * tearDown took: 360 ms
	 * =============== with systemmemory false, nommap false:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4144952
	 * adding from [0 to 111800) add100 executed in: 6,802 ms
	 * usedmem=4215688
	 * adding from [0 to 111800) add100 executed in: 1,826 ms
	 * usedmem=1101680
	 * adding from [0 to 111800) add100 executed in: 1,483 ms
	 * usedmem=2517168
	 * adding from [111800 to 223600) add100 executed in: 7,101 ms
	 * usedmem=1101640
	 * adding from [223600 to 335400) add100 executed in: 11,672 ms
	 * usedmem=4215784
	 * adding from [335400 to 447200) add100 executed in: 9,329 ms
	 * usedmem=2894704
	 * checking from 0 to 447200 check100 executed in: 7,226 ms
	 * usedmem=1197760
	 * all above adds/check (aka part2) executed in 45,439 ms
	 * tearDown took: 454 ms
	 * ================= mvc on, locking on, txn on, txndur off:
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4186408
	 * adding from [0 to 111800) add100 executed in: 6,693 ms
	 * usedmem=4310024
	 * adding from [0 to 111800) add100 executed in: 1,876 ms
	 * usedmem=1195936
	 * adding from [0 to 111800) add100 executed in: 1,498 ms
	 * usedmem=2516968
	 * adding from [111800 to 223600) add100 executed in: 7,133 ms
	 * usedmem=1195936
	 * adding from [223600 to 335400) add100 executed in: 11,671 ms
	 * usedmem=4309880
	 * adding from [335400 to 447200) add100 executed in: 9,346 ms
	 * usedmem=2894504
	 * checking from 0 to 447200 junitBDBJNI:: BDB2034 unable to allocate memory for mutex; resize mutex region
	 * ======== after fixing:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4144480
	 * adding from [0 to 111800) add100 executed in: 6,474 ms
	 * usedmem=4226584
	 * adding from [0 to 111800) add100 executed in: 1,577 ms
	 * usedmem=1101968
	 * adding from [0 to 111800) add100 executed in: 1,592 ms
	 * usedmem=2522296
	 * adding from [111800 to 223600) add100 executed in: 6,272 ms
	 * usedmem=1101936
	 * adding from [223600 to 335400) add100 executed in: 6,350 ms
	 * usedmem=4226640
	 * adding from [335400 to 447200) add100 executed in: 6,366 ms
	 * usedmem=2901008
	 * checking from 0 to 447200 check100 executed in: 6,553 ms
	 * usedmem=1198368
	 * all above adds/check (aka part2) executed in 35,184 ms
	 * tearDown took: 641 ms
	 * ================= txn on, locking on, txndur off, mvc off:
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4138832
	 * adding from [0 to 111800) add100 executed in: 6,521 ms
	 * usedmem=4214032
	 * adding from [0 to 111800) add100 executed in: 1,610 ms
	 * usedmem=1101560
	 * adding from [0 to 111800) add100 executed in: 1,592 ms
	 * usedmem=2516240
	 * adding from [111800 to 223600) add100 executed in: 6,319 ms
	 * usedmem=1101640
	 * adding from [223600 to 335400) add100 executed in: 6,428 ms
	 * usedmem=4308168
	 * adding from [335400 to 447200) add100 executed in: 6,430 ms
	 * usedmem=2893488
	 * checking from 0 to 447200 check100 executed in: 6,788 ms
	 * usedmem=1197616
	 * all above adds/check (aka part2) executed in 35,688 ms
	 * tearDown took: 1,093 ms
	 * ============== as above but with txndur on:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 125 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4123584
	 * adding from [0 to 111800) add100 executed in: 12,498 ms
	 * usedmem=4226352
	 * adding from [0 to 111800) add100 executed in: 1,685 ms
	 * usedmem=1101984
	 * adding from [0 to 111800) add100 executed in: 1,561 ms
	 * usedmem=2522184
	 * adding from [111800 to 223600) add100 executed in: 13,402 ms
	 * usedmem=1101920
	 * adding from [223600 to 335400) add100 executed in: 13,261 ms
	 * usedmem=4226360
	 * adding from [335400 to 447200) add100 executed in: 13,856 ms
	 * usedmem=2900840
	 * checking from 0 to 447200 check100 executed in: 6,631 ms
	 * usedmem=1198328
	 * all above adds/check (aka part2) executed in 62,910 ms
	 * tearDown took: 672 ms
	 * ============ again, same with hashdb test too:
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
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4148912
	 * adding from [0 to 111800) add100 executed in: 6,381 ms
	 * usedmem=4223744
	 * adding from [0 to 111800) add100 executed in: 1,607 ms
	 * usedmem=1099680
	 * adding from [0 to 111800) add100 executed in: 1,624 ms
	 * usedmem=2519616
	 * adding from [111800 to 223600) add100 executed in: 6,350 ms
	 * usedmem=1099696
	 * adding from [223600 to 335400) add100 executed in: 6,413 ms
	 * usedmem=4223592
	 * adding from [335400 to 447200) add100 executed in: 6,429 ms
	 * usedmem=2898312
	 * checking from 0 to 447200 check100 executed in: 6,912 ms
	 * usedmem=1196000
	 * all above adds/check (aka part2) executed in 35,716 ms
	 * tearDown took: 890 ms
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 16 ms
	 * Database type: DatabaseType.HASH
	 * usedmem=1307464
	 * adding from [0 to 111800) add100 executed in: 15,445 ms
	 * usedmem=4562656
	 * adding from [0 to 111800) add100 executed in: 3,230 ms
	 * usedmem=1439056
	 * adding from [0 to 111800) add100 executed in: 3,183 ms
	 * usedmem=2764264
	 * adding from [111800 to 223600) add100 executed in: 16,615 ms
	 * usedmem=1439008
	 * adding from [223600 to 335400) add100 executed in: 14,993 ms
	 * usedmem=4562744
	 * adding from [335400 to 447200) add100 executed in: 20,187 ms
	 * usedmem=3142904
	 * checking from 0 to 447200 check100 executed in: 18,862 ms
	 * usedmem=1344376
	 * all above adds/check (aka part2) executed in 92,515 ms
	 * tearDown took: 703 ms
	 * ====== same but with envConf.setTxnWriteNoSync( true );
	 * previously was envConf.setTxnNoSync( true ); and the other was commented
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\dbthatstoresallsequences`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\map(nameString2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\mapOne2Many(nodeLong2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\mapOne2Many(nodeLong2nodeLong)_backward_but_also_primary`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarymap(nameString2nodeLong)`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.register`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4142304
	 * adding from [0 to 111800) add100 executed in: 6,427 ms
	 * usedmem=4225952
	 * adding from [0 to 111800) add100 executed in: 1,595 ms
	 * usedmem=1101888
	 * adding from [0 to 111800) add100 executed in: 1,576 ms
	 * usedmem=2521824
	 * adding from [111800 to 223600) add100 executed in: 6,319 ms
	 * usedmem=1101904
	 * adding from [223600 to 335400) add100 executed in: 6,381 ms
	 * usedmem=4225800
	 * adding from [335400 to 447200) add100 executed in: 6,460 ms
	 * usedmem=2900520
	 * checking from 0 to 447200 check100 executed in: 6,756 ms
	 * usedmem=1198248
	 * all above adds/check (aka part2) executed in 35,530 ms
	 * tearDown took: 1,030 ms
	 * ========= same and setSystemMemory(true), seems faster:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4145240
	 * adding from [0 to 111800) add100 executed in: 6,349 ms
	 * usedmem=4226456
	 * adding from [0 to 111800) add100 executed in: 1,562 ms
	 * usedmem=1101808
	 * adding from [0 to 111800) add100 executed in: 1,563 ms
	 * usedmem=2522176
	 * adding from [111800 to 223600) add100 executed in: 6,350 ms
	 * usedmem=1101808
	 * adding from [223600 to 335400) add100 executed in: 6,319 ms
	 * usedmem=4226512
	 * adding from [335400 to 447200) add100 executed in: 6,350 ms
	 * usedmem=2900880
	 * checking from 0 to 447200 check100 executed in: 6,553 ms
	 * usedmem=1198248
	 * all above adds/check (aka part2) executed in 35,077 ms
	 * tearDown took: 937 ms
	 * ========== txn on, lock on, mvc on, txndur off, fail:
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 110 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4132136
	 * adding from [0 to 111800) add100 executed in: 6,599 ms
	 * usedmem=4214072
	 * adding from [0 to 111800) add100 executed in: 1,795 ms
	 * usedmem=1101656
	 * adding from [0 to 111800) add100 executed in: 1,436 ms
	 * usedmem=2516456
	 * adding from [111800 to 223600) add100 executed in: 6,959 ms
	 * usedmem=1101672
	 * adding from [223600 to 335400) add100 executed in: 11,578 ms
	 * usedmem=4214232
	 * adding from [335400 to 447200) add100 executed in: 9,237 ms
	 * usedmem=2893720
	 * checking from 0 to 447200 junitBDBJNI:: BDB3017 unable to allocate space from the buffer cache
	 * tearDown took: 47 ms
	 * ============ txn on, lock on, mvc off, txndur off, setChecksum(true):
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\log.0000000001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\secondarytheDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\theDBFileName`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.001`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.002`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb\__db.003`
	 * deleting: `E:\wrkspc\demlinks\.\bin\JUnit.tempDb`
	 * BDB2525 No log files found
	 * environment open took: 109 ms
	 * Database type: DatabaseType.BTREE
	 * usedmem=4144960
	 * adding from [0 to 111800) add100 executed in: 6,427 ms
	 * usedmem=4215256
	 * adding from [0 to 111800) add100 executed in: 1,670 ms
	 * usedmem=1101472
	 * adding from [0 to 111800) add100 executed in: 1,657 ms
	 * usedmem=2516880
	 * adding from [111800 to 223600) add100 executed in: 6,334 ms
	 * usedmem=1101536
	 * adding from [223600 to 335400) add100 executed in: 6,429 ms
	 * usedmem=4215440
	 * adding from [335400 to 447200) add100 executed in: 6,398 ms
	 * usedmem=2894320
	 * checking from 0 to 447200 check100 executed in: 7,223 ms
	 * usedmem=1197592
	 * all above adds/check (aka part2) executed in 36,138 ms
	 * tearDown took: 891 ms
	 */
	@Before
	public void setUp() {
		storeDir = new File( JUnitConstants.BDB_ENVIRONMENT_STORE_DIR );
		F.delFileOrTree( storeDir );
		storeDir.mkdirs();
		envConf = new EnvironmentConfig();
		
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
			env.closeForceSync();
		}
		timed.stop();
		System.out.println( "tearDown took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	@SuppressWarnings( "unused" )
	protected void setupBDBNativeEnv() throws FileNotFoundException, DatabaseException {
		// useless:
		envConf.setEventHandler( new EventHandlerAdapter() {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.sleepycat.db.EventHandlerAdapter#handlePanicEvent()
			 */
			@Override
			public void handlePanicEvent() {
				System.err.println( "panic event" );
			}
		} );
		
		envConf.setFeedbackHandler( new FeedbackHandler() {
			
			@Override
			public void recoveryFeedback( final Environment environment, final int percent ) {
				System.err.println( "recoveryFeedback" );
			}
			
			
			@Override
			public void upgradeFeedback( final Database database, final int percent ) {
				System.err.println( "upgradeFeedback" );
			}
			
			
			@Override
			public void verifyFeedback( final Database database, final int percent ) {
				System.err.println( "verifyFeedback" );
			}
		} );
		
		
		envConf.setAllowCreate( true );
		envConf.setLockDown( false );
		envConf.setDirectDatabaseIO( false );// false here is twice as fast as true
		envConf.setDirectLogIO( false );// XXX: and this
		//
		// // envConf.setEncrypted( password )
		envConf.setOverwrite( false );
		
		envConf.setErrorStream( System.err );
		envConf.setErrorPrefix( "junitBDBJNI:" );
		envConf.setHotbackupInProgress( false );
		envConf.setInitializeCache( true );// XXX: experiment with this
		// envConf.setInitializeCDB( true );//this1of2
		// envConf.setCDBLockAllDatabases( true );//this2of2 are unique and go together
		
		if ( ENABLE_LOCKING ) {
			envConf.setInitializeLocking( ENABLE_LOCKING );
		} else {
			envConf.setInitializeLocking( false && ENABLE_TRANSACTIONS );
		}
		envConf.setLockDetectMode( LockDetectMode.YOUNGEST );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		// final int x = HOWMANY;
		// envConf.setMaxLockers( x );
		// envConf.setMaxLockObjects( x );
		envConf.setMaxLocks( 10000 );// 10k seems ok, but 1k not
		envConf.setMutexIncrement( 10000 );// 100 or 1k or 5k is not enough
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		envConf.setMultiversion( MVC );// oh yeah xD
		
		if ( ENABLE_TRANSACTIONS ) {
			// // envConf.setDurability( DUR );
			// envConf.setTxnNoSync( true );// XXX: should be false for consistency
			envConf.setTxnWriteNoSync( true );// can't use both
			envConf.setTxnNotDurable( !DURABLE_TXNS );// fixed but prolly got overwritten by db's setting so no diff here
			envConf.setTxnNoWait( NOWAIT );
			envConf.setTxnSnapshot( MVC );// basically no effect in speed
			envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		}
		
		//
		envConf.setInitializeLogging( true );// XXX: set to true tho
		envConf.setLogInMemory( false );
		envConf.setLogAutoRemove( false );// set to true for smaller sized dbs
		envConf.setLogBufferSize( 0 );// XXX: uses defaults; can experiment with this
		envConf.setLogZero( false );// XXX:wow this totally writes 2gig(MaxLogFileSize) of zeroes when true
		// // envConf.setLogDirectory( logDirectory )
		// envConf.setMaxLogFileSize( Integer.MAX_VALUE );//this allocated 2gig log
		// envConf.setMaxLogFileSize( 10 * 1024 * 1024 );// 10meg alloc
		envConf.setRunFatalRecovery( false );
		
		// envConf.setInitializeRegions( false );// XXX: maybe experiment with this, unsure
		// envConf.setInitializeReplication( false );// for now
		// envConf.setInitialMutexes( 100 );// must investigate 10 is not enough!
		
		// envConf.setJoinEnvironment( false );
		//
		envConf.setRunRecovery( true && ENABLE_TRANSACTIONS );
		envConf.setRegister( false && ENABLE_TRANSACTIONS );
		//
		envConf.setMessageStream( System.err );
		//
		// envConf.setNoLocking( false );
		// envConf.setNoPanic( false );
		envConf.setNoMMap( false );// XXX:false seems faster but possibly only having any effect when db.setReadOnly(true)
		//
		// envConf.setPrivate( BerkEnv.once );// this fails true as long as we have that single-open constraint
		//
		// envConf.setReplicationInMemory( false );
		//
		envConf.setSystemMemory( false );// false is faster, but now true is faster grrr
		// required false under ubuntu linux or else this error happens java.lang.IllegalArgumentException: Invalid argument:
		// BDB0115 no base system shared memory ID specified
		
		// // envConf.setTemporaryDirectory( temporaryDirectory )
		envConf.setThreaded( false );// must be false for this test anyway
		//
		// envConf.setUseEnvironment( false );
		// envConf.setUseEnvironmentRoot( false );
		
		envConf.setYieldCPU( false );// XXX: experiment with this
		// envConf.setVerbose( VerboseConfig.FILEOPS, true );
		envConf.setVerbose( VerboseConfig.DEADLOCK, true );
		envConf.setVerbose( VerboseConfig.RECOVERY, true );
		envConf.setVerbose( VerboseConfig.REGISTER, true );
		envConf.setVerbose( VerboseConfig.REPLICATION, true );
		
		final Timer timed = new Timer( Timer.TYPE.MILLIS );
		timed.start();
		env = new Environment( storeDir, envConf );
		timed.stop();
		System.out.println( "environment open took: " + timed.getDeltaPrintFriendly() );
	}
	
	
	protected void setupBDBNativeDb( final DatabaseType dbtype ) {
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( false );// not needed tho, only populated if sec is empty but pri isn't
		secAndPriConf.setType( dbtype );
		System.out.println( "Database type: " + dbtype );
		secAndPriConf.setChecksum( true );// this has virtually no impact
		// secConf.setEncrypted( password )
		secAndPriConf.setMultiversion( MVC );
		// secAndPriConf.setSnapshot( MVC );
		secAndPriConf.setReverseSplitOff( false );
		secAndPriConf.setTransactionNotDurable( !DURABLE_TXNS );// XXX: normally false
		secAndPriConf.setUnsortedDuplicates( false );
		// secAndPriConf.setDeferredWrite( false );
		// secAndPriConf.setForeignKeyDatabase( null );TODO:bdb method bugged for null param; report this!
		secAndPriConf.setExclusiveCreate( false );
		secAndPriConf.setImmutableSecondaryKey( false );// XXX: investigate if true is needed here
		secAndPriConf.setReadOnly( false );
		secAndPriConf.setSortedDuplicates( false );// must be false
		// secConf.setTemporary( false );
		secAndPriConf.setTransactional( ENABLE_TRANSACTIONS );
		
		assert !secAndPriConf.getSortedDuplicates();
		assert !secAndPriConf.getUnsortedDuplicates();
		assert !secAndPriConf.getReverseSplitOff();
		secAndPriConf.setKeyCreator( new SecondaryKeyCreator() {
			
			@Override
			public boolean createSecondaryKey( final SecondaryDatabase secondary, final DatabaseEntry key,
												final DatabaseEntry data, final DatabaseEntry result ) {
				
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
		
		
		
		// pri db
		try {
			priDb = env.openDatabase(
			// BETransaction.getCurrentTransaction( _env ),
				null,
				dbName,
				null,
				secAndPriConf/*
							 * using the same conf from secondary, but it will be treated as just a simple DatabaseConfig
							 * instead
							 */
			);
			secDb = env.openSecondaryDatabase( null,
			// BETransaction.getCurrentTransaction( _env ),
				secPrefix + dbName,
				null,
				priDb,
				secAndPriConf );
		} catch ( final FileNotFoundException e ) {
			throw Q.rethrow( e );
		} catch ( final DatabaseException e ) {
			throw Q.rethrow( e );
		}
	}
	
	final DatabaseEntry	deKey	= new DatabaseEntry();
	final DatabaseEntry	deData	= new DatabaseEntry();
	
	
	protected void add100( final boolean firstTime, final boolean cont ) throws DatabaseException {
		final Transaction t = beginTxn( null );
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
				// final DatabaseEntry deKey = new DatabaseEntry();
				// final DatabaseEntry deData = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
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
			commit( t );
		} catch ( final Throwable t2 ) {
			abort( t );
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "add100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	protected Transaction beginTxn( final Transaction parent ) throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			final TransactionConfig txnConfig = new TransactionConfig();
			txnConfig.setNoWait( NOWAIT );
			txnConfig.setSnapshot( MVC );
			// txnConfig.setSync( true );
			// txnConfig.set
			return env.beginTransaction( parent, txnConfig );
		} else {
			return null;
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	protected void abort( final Transaction t ) throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.abort();
		}
	}
	
	
	/**
	 * @throws DatabaseException
	 * 
	 */
	protected void commit( final Transaction t ) throws DatabaseException {
		if ( ENABLE_TRANSACTIONS ) {
			t.commit();
		}
	}
	
	final Timer	addCheckTimer	= new Timer( Timer.TYPE.MILLIS );
	
	
	private void check100() throws DatabaseException {
		final Transaction t = beginTxn( null );
		addCheckTimer.start();
		try {
			// final TupleBinding<String> keyBinding = AllTupleBindings.getBinding( String.class );
			// final TupleBinding<Long> dataBinding = AllTupleBindings.getBinding( Long.class );
			
			System.out.print( "checking from 0 to " + leftOverForAdd100 + " " );
			for ( int i = 0; i < leftOverForAdd100; i++ ) {
				final String key = "" + i;
				// final Long data = new Long( i );
				// final DatabaseEntry deKey = new DatabaseEntry();
				// final DatabaseEntry deData = new DatabaseEntry();
				StringBinding.stringToEntry( key, deKey );
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
			commit( t );
		} catch ( final Throwable t2 ) {
			abort( t );
			
			Q.rethrow( t2 );
		}
		addCheckTimer.stop();
		System.out.println( "check100 executed in: " + addCheckTimer.getDeltaPrintFriendly() );
	}
	
	
	
	@Test
	public void testBTree() throws DatabaseException, FileNotFoundException {
		setupBDBNativeEnv();
		setupBDBNativeDb( DatabaseType.BTREE );
		part2();
	}
	
	
	@Ignore
	@Test
	public void testHash() throws DatabaseException, FileNotFoundException {
		setupBDBNativeEnv();
		setupBDBNativeDb( DatabaseType.HASH );
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
