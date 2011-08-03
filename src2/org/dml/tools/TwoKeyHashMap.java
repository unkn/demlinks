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

import org.dml.error.BadCallError;



/**
 * overwrites are not allowed<br>
 * null values are not allowed<br>
 * you can add/get/remove, but once add-ed you must remove it to change the DATA<br>
 * there can be only one K1,K2 pair and it's associated with only one DATA<br>
 * K1 should be the key that's less likely to change as compared with K2<br>
 */
public class TwoKeyHashMap<K1, K2, DATA> {
	
	private final HashMap<K1, HashMap<K2, DATA>>	first	= new HashMap<K1, HashMap<K2, DATA>>();
	
	public TwoKeyHashMap() {

	}
	
	/**
	 * @param key1
	 * @param key2
	 * @param data
	 * @return
	 * @throws BadCallError
	 *             when overwrite detected
	 */
	public boolean ensure( K1 key1, K2 key2, DATA data ) {

		RunTime.assumedNotNull( key1, key2, data );
		DATA old = this.get( key1, key2 );
		boolean existed = ( null != old );
		if ( !existed ) {
			this.add( key1, key2, data );
			old = this.get( key1, key2 );
			RunTime.assumedTrue( old == data );
		} else {
			if ( old != data ) {
				RunTime.badCall( "you wanted to overwrite an older different value" );
			}
		}
		return existed;
	}
	
	public void add( K1 key1, K2 key2, DATA data ) {

		RunTime.assumedNotNull( key1, key2, data );
		HashMap<K2, DATA> second = first.get( key1 );
		if ( null != second ) {
			DATA old = second.get( key2 );
			if ( null != old ) {
				if ( old != data ) {
					RunTime.badCall( "attempted overwrite" );
				} else {
					RunTime.badCall( "already exists" );
				}
				return;// not reached
			}
		} else {
			// didn't exist
			second = new HashMap<K2, DATA>();
			RunTime.assumedNull( first.put( key1, second ) );
			RunTime.assumedTrue( first.get( key1 ) == second );
		}
		// if we're here, it doesn't exist yet
		RunTime.assumedNull( second.put( key2, data ) );
		RunTime.assumedTrue( second.get( key2 ) == data );
	}
	
	public void remove( K1 key1, K2 key2 ) {

		RunTime.assumedNotNull( key1, key2 );
		HashMap<K2, DATA> second = first.get( key1 );
		if ( null != second ) {
			if ( null != second.remove( key2 ) ) {
				// existed and was removed
				return;
			}
		}
		RunTime.bug( "requested pair was not found!" );
	}
	
	/**
	 * @param key1
	 * @param key2
	 * @return null if not found, else the DATA
	 */
	public DATA get( K1 key1, K2 key2 ) {

		RunTime.assumedNotNull( key1, key2 );
		HashMap<K2, DATA> second = first.get( key1 );
		DATA ret = null;
		if ( null != second ) {
			ret = second.get( key2 );
		}
		return ret;// can be null
	}
	
	/**
	 * 
	 */
	public void clear() {

		first.clear();
	}
}
