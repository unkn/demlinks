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
 * File creation: Aug 23, 2010 5:25:47 AM
 */


package org.dml.tools;



/**
 * used by RunTimeTest.java
 * 
 */
public class RunTimeTest2
{
	
	public static
			StackTraceElement
			outterCall()
	{
		return RunTime.getTheCaller_OutsideOfThisClass();
	}
	

	public
			StackTraceElement
			innerCall()
	{
		return RunTime.getTheCaller_OutsideOfThisClass();
	}
	

	public
			StackTraceElement
			wtw()
	{
		return outterCall();
	}
	

	public
			StackTraceElement
			wtw2()
	{
		return this.innerCall();
	}
	

	public
			StackTraceElement
			wtw3()
	{
		return this.wtw();
	}
	

	public
			StackTraceElement
			wtw4()
	{
		return this.wtw3();
	}
	

	public
			StackTraceElement
			wtw5()
	{
		return this.wtw2();
	}
	

	public
			StackTraceElement
			wtw6()
	{
		return this.wtw5();
	}
	

	
	// do not rename method
	public static
			StackTraceElement
			getCurrentStackTraceElement(
											int modifier )
	{
		StackTraceElement[] stea = RunTime.getCurrentStackTraceElementsArray();
		for ( int i = 0; i < stea.length; i++ )
		{
			System.out.println( "!"
								+ i
								+ " "
								+ stea[i] );
		}
		return RunTime.getCurrentStackTraceElement( 1 + modifier );
	}
	

	// do not rename method
	public static
			StackTraceElement
			getCurrentStackTraceElement()
	{
		return getCurrentStackTraceElement( 1 );
	}
}
