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
 * File creation: Aug 24, 2010 8:24:44 PM
 */


package org.temporary.tests;



import org.dml.tools.RunTime;



/**
 * 
 * don't rename to ThreadLocal* because it will have no call tracing
 */
public class TestThreadLocal
{
	
	private static final int					LOOP_MAX1		= 3150;
	private static final int					LOOP_MAX2		= 1000;
	
	// The next serial number to be assigned
	private static int							nextSerialNum	= 0;
	
	private static ThreadLocal<Integer>			serialNum		= new ThreadLocal<Integer>()
																{
																	
																	@SuppressWarnings( "synthetic-access" )
																	@Override
																	protected synchronized
																			Integer
																			initialValue()
																	{
																		return new Integer(
																							nextSerialNum++ );
																	}
																};
	
	private static volatile transient boolean	quit			= false;
	
	
	public static
			int
			get()
	{
		return ( ( serialNum.get() ) ).intValue();
	}
	

	public static
			void
			set(
					Integer val )
	{
		serialNum.set( val );
	}
	

	public static
			int
			some()
	{
		return TestThreadLocal.get();
	}
	

	public static
			void
			main(
					String[] args )
	{
		// ThreadLocalTest tlt = new ThreadLocalTest();
		RunTime.callTracingFromHere.set( true );
		System.out.println( TestThreadLocal.get() );
		Thread t = new Thread()
		{
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@SuppressWarnings( "synthetic-access" )
			@Override
			public
					void
					run()
			{
				RunTime.callTracingFromHere.set( true );
				// super.run();
				System.out.println( TestThreadLocal.get() );
				int count = 0;
				while ( !quit )
				{
					if ( 1 != TestThreadLocal.some() )
					{
						System.err.println( "fail in thread1" );
						quit = true;
					}
					if ( ++count > LOOP_MAX2 )
					{
						break;
					}
				}
				reportQuitStatus();
			}
		};
		// t.run();// same thread as current
		t.start();// start this as new thread
		int count = 0;
		while ( !quit )
		{
			if ( 0 != TestThreadLocal.get() )
			{
				System.err.println( "fail in main" );
				quit = true;
			}
			if ( ++count > LOOP_MAX1 )
			{
				break;
			}
		}
		
		reportQuitStatus();
	}
	

	public static
			void
			reportQuitStatus()
	{
		if ( quit )
		{
			System.err.println( "BUG" );
		}
		else
		{
			System.out.println( "safe exit from "
								+ Thread.currentThread().getName() );
		}
	}
}
