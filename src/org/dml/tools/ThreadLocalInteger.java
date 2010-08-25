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
 * File creation: Aug 25, 2010 11:15:21 AM
 */


package org.dml.tools;


/**
 * 
 *
 */
public class ThreadLocalInteger
		extends
		ThreadLocal<Integer>
{
	
	private final int	initialValue;
	
	
	/**
	 * constructor
	 * 
	 * @param initialValue1
	 */
	public ThreadLocalInteger(
			int initialValue1 )
	{
		initialValue = initialValue1;
	}
	

	@Override
	protected synchronized
			Integer
			initialValue()
	{
		return new Integer(
							initialValue );
	}
	

	public
			void
			increment()
	{
		this.set( this.get() + 1 );
	}
	

	public
			void
			decrement()
	{
		this.set( this.get() - 1 );
	}
}
