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



import org.dml.error.BadCallError;



/**
 * STATICKEY will keep it's place, that is, it's not part of the two way<br>
 * the two way is between KEY and DATA<br>
 * so you can access by KEY or by DATA<br>
 * NULL is not allowed<br>
 * DUPS are obviously not allowed<br>
 */
public class TwoWayTwoKeyHashMap<STATICKEY, KEY, DATA> {
	
	private final TwoKeyHashMap<STATICKEY, KEY, DATA>	keyData	= new TwoKeyHashMap<STATICKEY, KEY, DATA>();
	private final TwoKeyHashMap<STATICKEY, DATA, KEY>	dataKey	= new TwoKeyHashMap<STATICKEY, DATA, KEY>();
	
	
	/**
	 * @param staticKey
	 * @param key
	 * @return null if not found, else the DATA
	 */
	public DATA getData( STATICKEY staticKey, KEY key ) {

		RunTime.assumedNotNull( staticKey, key );
		return keyData.get( staticKey, key );
	}
	
	public KEY getKey( STATICKEY staticKey, DATA data ) {

		RunTime.assumedNotNull( staticKey, data );
		return dataKey.get( staticKey, data );
	}
	
	/**
	 * will not overwrite! will throw instead
	 * 
	 * @param staticKey
	 * @param key
	 * @param data
	 * @return true if already existed
	 * @throws BadCallError
	 *             when overwrite detected
	 */
	public boolean ensure( STATICKEY staticKey, KEY key, DATA data ) {

		RunTime.assumedNotNull( staticKey, key, data );
		boolean existing1 = keyData.ensure( staticKey, key, data );
		boolean existing2 = dataKey.ensure( staticKey, data, key );
		if ( existing1 != existing2 ) {
			RunTime.bug( "inconsistency detected, between our two private fields" );
		}
		return existing1;// which is == existing2
	}
	
	public void clear() {

		keyData.clear();
		dataKey.clear();
	}
}
