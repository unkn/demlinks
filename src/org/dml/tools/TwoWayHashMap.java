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



package org.dml.tools;



import java.util.HashMap;

import org.dml.level010.Symbol;



/**
 * 
 *
 */
public class TwoWayHashMap<KEY, DATA>
{
	
	private final HashMap<KEY, DATA>	keyData	= new HashMap<KEY, DATA>();
	private final HashMap<DATA, KEY>	dataKey	= new HashMap<DATA, KEY>();
	
	
	/**
	 * @param key
	 * @return true if existed
	 */
	public
			boolean
			removeByKey(
							KEY key )
	{
		RunTime.assumedNotNull( key );
		DATA data = keyData.get( key );
		if ( null != data )
		{
			// consistency check
			KEY key2 = dataKey.get( data );
			RunTime.assumedNotNull( key2 );
			RunTime.assumedTrue( key == key2 );
			RunTime.assumedTrue( keyData.remove( key ) == data );
			RunTime.assumedTrue( dataKey.remove( data ) == key2 );
			return true;
		}
		return false;// can be null
	}
	

	/**
	 * @param key
	 * @return null if not found
	 */
	public
			DATA
			getData(
						KEY key )
	{
		
		RunTime.assumedNotNull( key );
		
		DATA data = keyData.get( key );
		if ( null != data )
		{
			// consistency check
			KEY key2 = dataKey.get( data );
			RunTime.assumedTrue( key == key2 );
		}
		return data;// can be null
	}
	

	/**
	 * @param data
	 * @return null if not found
	 */
	public
			KEY
			getKey(
					DATA data )
	{
		
		RunTime.assumedNotNull( data );
		KEY key = dataKey.get( data );
		if ( null != key )
		{
			DATA data2 = keyData.get( key );
			RunTime.assumedTrue( data == data2 );
		}
		return key;
	}
	

	/**
	 * @param key
	 * @param data
	 * @return true if already existed
	 */
	public
			boolean
			ensure(
					KEY key,
					DATA data )
	{
		
		RunTime.assumedNotNull(
								key,
								data );
		DATA d1 = keyData.put(
								key,
								data );
		
		if ( null != d1 )
		{
			if ( d1 != data )
			{
				RunTime.badCall( "You attempted to overwrite an already existing key with a new value. Not acceptable" );
			}
		}
		KEY k1 = dataKey.put(
								data,
								key );
		if ( null == d1 )
		{// KEY didn't exist, then so must the DATA not exist
			RunTime.assumedNull( k1 );
		}
		else
		{
			RunTime.assumedTrue( k1 == key );
		}
		return ( null != d1 );
	}
	

	public
			void
			clear()
	{
		keyData.clear();
		dataKey.clear();
	}
}
