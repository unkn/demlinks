/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.demlinks.javathree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * provides 1 to 1 mapping between two Objects: Key<->Value<br>
 * can find one given the other ie. get value if you know key and get key if you know value<br>
 * permits no nulls<br>
 * uses .equals() to test see: {@link HashMap#get(Object)}<br>
 * seems it also uses "==" to test so if either equality yields true then it's considered the same object<br>
 *
 * @param <Key>
 * @param <Value>
 */
public class TwoWayHashMap<Key,Value> {
	private HashMap<Key,Value> forward;
	private HashMap<Value,Key> backward;
	
	TwoWayHashMap() {
		forward = new HashMap<Key, Value>();
		backward = new HashMap<Value, Key>();
	}
	
	/** (non-Javadoc)
	 * should contain no null values tho, thus a null return would mean there's no key->value tuple
	 * however empty strings are allowed, if any
	 * @see java.util.HashMap#get(Object key)
	 */
	public Value getValue(Key _k) {
		Debug.nullException(_k);
		return forward.get(_k);
	}
	
	public Key getKey(Value _v) {
		Debug.nullException(_v);
		return backward.get(_v);
	}

	/**
	 * adds or replaces Key-Value tuple
	 * @param _k key
	 * @param _v value
	 * @return false if key already existed and was associated with a value; true if it didn't exist
	 */
	public boolean putKeyValue(Key _k, Value _v) {
		Debug.nullException(_k,_v);
		Value prevFwdVal = forward.put(_k, _v); 
		boolean noPrevOne = prevFwdVal == null;
		if (!noPrevOne) {
			backward.remove(prevFwdVal); //this value was replaced so it must not exist in this other list
		}
		Key prevBackwdKey = backward.put(_v, _k);
		boolean noPrevTwo = prevBackwdKey == null;
		if (!noPrevTwo) {
			forward.remove(prevBackwdKey);
		}
		return noPrevOne && noPrevTwo;
	}

	/**
	 * @return number of elements in this 1 to 1 Map
	 */
	public int size() {
		return forward.size();// == backward.size()
	}

	/**
	 * @param _k
	 * @return
	 */
	public Value removeKey(Key _k) {
		Debug.nullException(_k);
		Value deleted = forward.remove(_k);
		Key tmpk = backward.remove(deleted);
		if (tmpk != _k) {
			throw new AssertionError("impartial removal, how?! and this is weird!");
		}
		return deleted;
	}
	
	/**
	 * @param _v
	 * @return
	 */
	public Key removeValue(Value _v) {
		Debug.nullException(_v);
		Key _k = getKey(_v);
		if (_v != removeKey(_k)) {
			throw new AssertionError("we removed the value of another key, this means 2 keys had same value in this 1 to 1 MAP");
		}
		return _k;
	}
	
	public Iterator<Map.Entry<Key, Value>> getKeyValueIterator() {
		return forward.entrySet().iterator();
	}
}
