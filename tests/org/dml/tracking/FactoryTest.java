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
 * File creation: Jul 3, 2010 7:36:53 AM
 */


package org.dml.tracking;



import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.javapart.logger.Log;
import org.junit.Test;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.ParamID;



/**
 * 
 *
 */
public class FactoryTest {
	
	static ParamID	specB	= ParamID.getNew( "specB" );
	
	public static class B extends Initer {
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#start(org.references.method.MethodParams)
		 */
		@Override
		protected void start( MethodParams params ) {

			// TODO Auto-generated method stub
			
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#done(org.references.method.MethodParams)
		 */
		@Override
		protected void done( MethodParams params ) {

			// TODO Auto-generated method stub
			
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#beforeDeInit()
		 */
		@Override
		protected void beforeDone() {

			// TODO Auto-generated method stub
			
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#beforeInit()
		 */
		@Override
		protected void beforeStart() {

			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class A extends Initer {
		
		private B		b;
		private boolean	initedOurOwn	= false;
		private B		db				= null;
		
		public A() {

		}
		
		/**
		 * so this will init these in A, but will never deInit them<br>
		 * 
		 * @return
		 */
		public B getSomeNewDB() {

			B b = Factory.getNewInstanceAndInit( B.class );
			return b;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#start(org.references.method.MethodParams)
		 */
		@Override
		protected void start( MethodParams params ) {

			Reference<Object> tempRef2B = null;
			if ( null != params ) {
				tempRef2B = params.get( specB );
			}
			if ( null == tempRef2B ) {
				initedOurOwn = true;
				b = Factory.getNewInstanceAndInit( B.class );
			} else {
				initedOurOwn = false;
				b = (B)tempRef2B.getObject();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#done(org.references.method.MethodParams)
		 */
		@Override
		protected void done( MethodParams params ) {

			if ( null != db ) {
				// save something to db
				RunTime.assumedTrue( db.isInited() );// yeah it was deInit-ed by Factory.deInitAll()
			}
			if ( initedOurOwn ) {
				Factory.deInit( b );
				initedOurOwn = false;
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#beforeDeInit()
		 */
		@Override
		protected void beforeDone() {

			// TODO Auto-generated method stub
			
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.dml.tools.Initer#beforeInit()
		 */
		@Override
		protected void beforeStart() {

			// TODO Auto-generated method stub
			
		}
		
		/**
		 * @param someNewDB
		 */
		public void setUseDB( B someNewDB ) {

			RunTime.assumedNotNull( someNewDB );
			RunTime.assumedTrue( someNewDB.isInited() );
			db = someNewDB;
		}
		
	}
	
	@Test
	public void test1() {

		Log.entry();
		try {
			// System.out.println( Log.getThisLineLocation() );
			// System.exit( 0 );
			A a = Factory.getNewInstanceAndInit( A.class );
			RunTime.assumedTrue( a.initedOurOwn );
			Factory.deInit( a );
			RunTime.assumedFalse( a.initedOurOwn );
			
			MethodParams params = Factory.getNewInstanceAndInit( MethodParams.class );
			B newB = Factory.getNewInstanceAndInit( B.class );
			params.set( specB, newB );
			a = Factory.getNewInstanceAndInit( A.class, params );
			RunTime.assumedFalse( a.initedOurOwn );
			RunTime.assumedTrue( a.b == newB );
			Factory.deInit( a );
			RunTime.assumedFalse( a.initedOurOwn );
			Factory.reInit_aka_InitAgain_WithOriginalPassedParams( a );
		} finally {
			Factory.deInitAll();
		}
	}
	
	
	@Test
	public void testOrder() {

		Log.entry();
		try {
			// A a = Factory.getNewInstanceAndInit( A.class );
			
			MethodParams params = Factory.getNewInstanceAndInit( MethodParams.class );
			B newB = Factory.getNewInstanceAndInit( B.class );
			params.set( specB, newB );
			A aa = Factory.getNewInstanceAndInit( A.class, params );
			RunTime.assumedFalse( aa.initedOurOwn );
			Factory.deInit( aa );
			Factory.init( aa, null );
			RunTime.assumedTrue( aa.initedOurOwn );
			
			// Factory.deInit( aa );
			// so init A, init B
		} finally {
			Factory.deInitAll();
			// deinit A (which also deInits B)
		}
		Log.exit();
	}
	
	@Test
	public void testOrder2() {// this is a known-bugs thingy; this is supposed to fail
	
		// this will test what happens if an class uses an inited param(which was inited by some other class)
		// on its deinit or done method
		// for example: class A will use a 'db' which was inited by someone else, and this 'db' was passed to it and A
		// will use it until A dies; and on A.done , A wants to do something with the db ie. write a log entry in db
		// but if exception happened before A deInit-ed, then db is deinited first, by deInitAll() because db was inited
		// long after A was inited; and so then after a while when A is deinited by same deInitAll() it will fail
		// because the db is deInited
		
		try {
			A a = Factory.getNewInstanceAndInit( A.class );
			// A aa = Factory.getNewInstanceAndInit( A.class );
			B db = Factory.getNewInstanceAndInit( B.class );// doesn't matter where from
			a.setUseDB( db );
			// and assume somehow something throws and a.deInit is never called
			RunTime.thro( new Exception( "unfortunate" ) );// this jumps to deInitAll() below
			Factory.deInit( a );
			Factory.deInit( db );
			// on deInitAll() below, the db will get deInit-ed first, but 'a' is still going to use it on its own deInit
			// so fail
		} finally {
			Factory.deInitAll();
		}
	}
}
