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


/**
 * 
 *
 */
public class ThreadLocalTest
{
	
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
			void
			main(
					String[] args )
	{
		// ThreadLocalTest tlt = new ThreadLocalTest();
		System.out.println( ThreadLocalTest.get() );
		Thread t = new Thread()
		{
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public
					void
					run()
			{
				// TODO Auto-generated method stub
				// super.run();
				System.out.println( ThreadLocalTest.get() );
				while ( !quit )
				{
					if ( 1 != ThreadLocalTest.get() )
					{
						System.err.println( "fail in thread1" );
						quit = true;
					}
				}
			}
		};
		// t.run();// same thread as current
		t.start();// start this as new thread
		while ( !quit )
		{
			if ( 0 != ThreadLocalTest.get() )
			{
				System.err.println( "fail in main" );
				quit = true;
			}
		}
	}
}
