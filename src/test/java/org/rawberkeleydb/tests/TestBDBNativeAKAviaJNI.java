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
import org.junit.*;
import org.q.*;
import org.toolza.*;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.db.*;



/**
 *
 */
public class TestBDBNativeAKAviaJNI {
	
	// set all these 4 to true for consistency, but also lack of speed; all to false for max speed
	private static final boolean	ENABLE_TRANSACTIONS				= true;
	@SuppressWarnings( "unused" )
	private static final boolean	DURABLE_TXNS					= true ? ENABLE_TRANSACTIONS : false;
	private static final boolean	ENABLE_LOCKING					= false;
	@SuppressWarnings( "unused" )
	// only enabled when transactions are enabled, and if that first bool is true
	private static final boolean	MVC								= false ? ENABLE_TRANSACTIONS : false;
	
	
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
	
	
	private Environment				env;
	private EnvironmentConfig		envConf;
	private File					storeDir;
	private SecondaryConfig			secAndPriConf;
	private Database				priDb;
	private SecondaryDatabase		secDb;
	private int						leftOverForAdd100				= 0;
	
	
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
	 * ================= txn on, dur on, no locking no mvc:
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
	
	
	private void setupBDBNativeDb( final DatabaseType dbtype ) {
		
		secAndPriConf = new SecondaryConfig();
		secAndPriConf.setAllowCreate( true );
		secAndPriConf.setAllowPopulate( false );// not needed tho, only populated if sec is empty but pri isn't
		secAndPriConf.setType( dbtype );
		System.out.println( "Database type: " + dbtype );
		secAndPriConf.setChecksum( false );// this has virtually no impact
		// secConf.setEncrypted( password )
		secAndPriConf.setMultiversion( MVC );
		secAndPriConf.setReverseSplitOff( false );
		secAndPriConf.setTransactionNotDurable( DURABLE_TXNS );// XXX: normally false
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
	
	
	@SuppressWarnings( "unused" )
	private void setupBDBNativeEnv() throws FileNotFoundException, DatabaseException {
		envConf.setAllowCreate( true );
		envConf.setLockDown( false );
		envConf.setDirectDatabaseIO( true );// XXX: experiment with this!
		envConf.setDirectLogIO( true );// XXX: and this
		//
		// // envConf.setEncrypted( password )
		envConf.setOverwrite( false );
		
		envConf.setErrorStream( System.err );
		envConf.setErrorPrefix( "junitBDBJNI:" );
		
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
		
		envConf.setHotbackupInProgress( false );
		envConf.setInitializeCache( true );// XXX: experiment with this
		// envConf.setInitializeCDB( true );//this1of2
		// envConf.setCDBLockAllDatabases( true );//this2of2 are unique and go together
		
		if ( ENABLE_LOCKING ) {
			envConf.setInitializeLocking( ENABLE_LOCKING );
		} else {
			envConf.setInitializeLocking( ENABLE_TRANSACTIONS );
		}
		envConf.setLockDetectMode( LockDetectMode.YOUNGEST );
		envConf.setLockTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		// final int x = 100;
		// envConf.setMaxLockers( x );
		// envConf.setMaxLockObjects( x );
		// envConf.setMaxLocks( x );
		envConf.setTransactional( ENABLE_TRANSACTIONS );
		if ( ENABLE_TRANSACTIONS ) {
			// // envConf.setDurability( DUR );
			envConf.setTxnNoSync( true );// XXX: should be false for consistency
			// envConf.setTxnWriteNoSync( true );// can't use both
			envConf.setTxnNotDurable( true );
			envConf.setTxnNoWait( true );
			envConf.setTxnSnapshot( MVC );
			envConf.setTxnTimeout( BDBLOCK_TIMEOUT_MicroSeconds );
		}
		
		//
		envConf.setInitializeLogging( true );// XXX: set to true tho
		envConf.setLogInMemory( false );
		envConf.setLogAutoRemove( false );// set to true for smaller sized dbs
		envConf.setLogBufferSize( 0 );// XXX: uses defaults; can experiment with this
		envConf.setLogZero( false );// XXX:wow this totally writes 2gig of zeroes when true
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
		envConf.setMultiversion( true );// oh yeah xD
		//
		envConf.setNoLocking( false );
		envConf.setNoPanic( false );
		envConf.setNoMMap( false );
		//
		// envConf.setPrivate( BerkEnv.once );// this fails true as long as we have that single-open constraint
		//
		envConf.setReplicationInMemory( false );
		//
		envConf.setSystemMemory( true );// XXX: experiment, this fails
		//
		// // envConf.setTemporaryDirectory( temporaryDirectory )
		envConf.setThreaded( true );
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
			txnConfig.setSnapshot( MVC );
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
	public void testBTree() throws DatabaseException, FileNotFoundException {
		setupBDBNativeEnv();
		setupBDBNativeDb( DatabaseType.BTREE );
		part2();
	}
	
	
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
