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
 * File creation: Jul 5, 2010 9:06:32 AM
 */


package org.dml.tools;



import java.util.HashMap;



/**
 * 
 * //NOTE: don't implement Factory/Initer to this
 */
public class NonNullHashMap<K, V> {
	
	private final HashMap<K, V>	composition	= new HashMap<K, V>();
	
	/**
	 * @param key
	 * @param value
	 * @return true if key already existed and was overwritten; false if key didn't exist and thus is unique so far<br>
	 * @see HashMap#put(Object, Object)
	 */
	public boolean put( K key, V value ) {

		RunTime.assumedNotNull( key, value );
		V val = composition.put( key, value );
		return null != val;// true means key is unique and didn't exist so val is null
		// RunTime.assumedTrue( null == val );//no overwriting?
	}
	
	/**
	 * @param key
	 * @return value or null
	 * @see HashMap#get(Object)
	 */
	public V getValue( K key ) {

		RunTime.assumedNotNull( key );
		return composition.get( key );// can be null if not found
		// RunTime.assumedNotNull( val );
	}
	
	/**
	 * remove key/value pair<br>
	 * 
	 * @param key
	 * @return null if didn't exist; or the Value if it was removed
	 */
	public V remove( K key ) {

		RunTime.assumedNotNull( key );
		return composition.remove( key );// can return null if didn't exist
	}
	
}
