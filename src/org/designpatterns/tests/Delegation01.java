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



package org.designpatterns.tests;



import java.util.HashSet;

import org.dml.tools.RunTime;
import org.dml.tracking.Log;



/**
 * 
 *
 */
public class Delegation01
{
	
	private final Window	win;
	
	private class Window
			extends
			Geo
	{
		
		private Geo	shape;
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.designpatterns.tests.Delegation01.Geo#getArea()
		 */
		@Override
		public
				long
				getArea()
		{
			
			return shape.getArea();
		}
		

		/**
		 * @param r
		 */
		public
				void
				setShape(
							Geo g )
		{
			
			assert g != null;
			shape = g;
		}
	}
	
	private abstract class Geo
	{
		
		public abstract
				long
				getArea();
	}
	
	private class Rect
			extends
			Geo
	{
		
		private final long	x, y;
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.designpatterns.tests.Delegation01.Geo#getArea()
		 */
		@Override
		public
				long
				getArea()
		{
			
			return x * y;
		}
		

		public Rect(
				long _x,
				long _y )
		{
			
			x = _x;
			y = _y;
		}
		
	}
	
	private class Circle
			extends
			Geo
	{
		
		private final long	r;
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.designpatterns.tests.Delegation01.Geo#getArea()
		 */
		@Override
		public
				long
				getArea()
		{
			return r;
		}
		

		@SuppressWarnings( "synthetic-access" )
		public Circle(
				long ray )
		{
			
			r = ray;
		}
	}
	
	
	
	/**
	 * 
	 */
	public Delegation01()
	{
		
		win = new Window();
		Rect r = new Rect(
							1,
							55 );
		Circle c = new Circle(
								99 );
		win.setShape( r );
		System.out.println( win.getArea() );
		win.setShape( c );
		System.out.println( win.getArea() );
	}
	

	// public
	// void
	// loop(
	// int i )
	// {
	// System.out.println( "in with: " + i );
	// if ( !RunTime.recursiveLoopDetected() )
	// {
	// this.loop( i + 1 );
	// }
	// // if ( i < 10 )
	// // {
	// // return this.loop( i + 1 );
	// // }
	// // else
	// // {
	// // return RunTime.getCurrentStackTraceElementsArray();
	// // }
	// }
	


	public static
			void
			main(
					String[] args )
					throws Exception
	{
		
		@SuppressWarnings( "unused" )
		Delegation01 d = new Delegation01();
		// d.loop( 1 );
		// try
		// {
		// FIXME:
		// System.out.println( "this line: " + Log.getThisLineLocation( -1 - 3 + 6 + 2 ) );
		// moo();
		// foo(
		// 1,
		// 2,
		// 3 );
		// }
		// finally
		// {
		// throw new RuntimeException(
		// "finally" );
		// }
		// throw new RuntimeException();
	}
	

	/**
	 * 
	 */
	private static
			void
			moo()
	{
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		for ( int i = 0; i < stea.length; i++ )
		{
			System.out.println( i + " " + stea[i] );
		}
	}
	

	public static
			void
			foo(
					int i,
					int j,
					int k )
	{
		throw new RuntimeException(
									"bleh " + i + " " + j + " " + k );
	}
}
