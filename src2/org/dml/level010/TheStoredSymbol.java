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
 * File creation: Sep 2, 2010 4:36:23 AM
 */


package org.dml.level010;



import org.dml.tools.RunTime;
import org.references.*;
import org.toolza.*;



/**
 * Symbol in storage<br>
 * instances of Symbol gotten from storage, which are in common to all environments<br>
 * for example, two different environments may return the same Long but because they are in different environment they
 * are different Symbols<br>
 * but this class doesn't care about different environments - that will be done in Symbol class - this class is a
 * mapping between the stored and retrieved Symbol before it is split by environments<br>
 */
public class TheStoredSymbol
{
	
	// DONE maybe do equals and hashCode for Maps when checking in Symbol because we can compare two different
	// TheStoredSymbol instances with same contents aka long value due to below clearing of some cached
	// TheStoredSymbol-s when cache is over X
	
	// we cache them here to avoid doing many 'new's but FIXME: we shouldn't cache too many for memory reasons
	private static final TwoWayHashMapOfNonNullUniques<TheStoredSymbol, Long>	allStoredSymbolsFromAllEnvironments	= new TwoWayHashMapOfNonNullUniques<TheStoredSymbol, Long>();
	
	private final Long											l;
	
	
	/**
	 * private constructor
	 */
	private TheStoredSymbol(
			Long l1 )
	{
		l = l1;
		RunTime.assumedNotNull( l );
	}
	

	/**
	 * @param l1
	 * @return never null
	 */
	public static
			TheStoredSymbol
			getNew(
					Long l1 )
	{
		RunTime.assumedNotNull( l1 );
		TheStoredSymbol ret = null;
		ret = allStoredSymbolsFromAllEnvironments.getKey( l1 );
		if ( null == ret )
		{
			ret = new TheStoredSymbol(
										l1 );
			RunTime.assumedFalse( allStoredSymbolsFromAllEnvironments.putOrGet(
																				ret,
																				l1 ) );
		}
		RunTime.assumedNotNull( ret );
		return ret;
	}
	

	public
			Long
			getLong()
	{
		
		// RunTime.assumedNotNull( allStoredSymbols );
		// Long ret = allStoredSymbols.getData( this );
		// return ret;
		RunTime.assumedNotNull( l );
		return l;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public
			boolean
			equals(
					Object obj )
	{
		if ( null != obj )
		{
			if ( super.equals( obj ) )
			{
				return true;
			}
			else
			{
				if ( obj.getClass().equals(
											this.getClass() ) )
				{
					if ( l.equals( ( (TheStoredSymbol)obj ).l ) )
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public
			int
			hashCode()
	{
		return l.hashCode();
		// return super.hashCode();
	}
	

	/**
	 * for use in junit only!
	 */
	public static
			void
			junitClearCache()
	{
		allStoredSymbolsFromAllEnvironments.clear();
	}
	
}
