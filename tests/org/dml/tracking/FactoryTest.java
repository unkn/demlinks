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
		
		public A() {

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
			@SuppressWarnings( "unused" )
			A a = Factory.getNewInstanceAndInit( A.class );
			// so init A, init B
		} finally {
			Factory.deInitAll();
			// deinit A (which also deInits B)
		}
	}
}
