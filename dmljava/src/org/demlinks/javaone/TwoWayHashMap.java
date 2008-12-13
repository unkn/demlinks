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

package org.demlinks.javaone;

import java.util.HashMap;

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
		nullError(_k);
		return forward.get(_k);
	}
	
	public Key getKey(Value _v) {
		nullError(_v);
		return backward.get(_v);
	}

	/**
	 * not allowing replacing existing key->value tuples, this should only add new key->value that didn't previously exist
	 * @throws Exception if the key->value tuple existed, or rather is key->anyvalue existed, except key->null tuple<br>
	 * @transaction protected
	 */
	public void putKeyValue(Key _k, Value _v) throws Exception {
		nullError(_k);
		nullError(_v);
		Value one = forward.put(_k, _v); 
		if (one != null) {
			forward.remove(_k);//undo-ing transaction
			throw new Exception("the value("+one+") just got replaced (by "+_v+"), in key "+_k);
		}
		Key two = backward.put(_v, _k);
		if (two != null) {
			//if we're here then "one" did not got replaced hence it's safe to remove it since it didn't previously exist or
			//worst case scenario key -> null (association), null is the Value
			forward.remove(_k); // we undo this transaction even if we throw exception
			backward.remove(_v);//this too!
			throw new Exception("the key("+two+") just got replaced (by "+_k+"), in value "+_v);
		}
	}

	private static void nullError(Object obj) {
		if (null == obj) {
			throw new NullPointerException("object shouldn't be null. Like ever!");
		}
	}

	public int size() {
		return forward.size();// == backward.size()
	}

	public Value removeKey(Key _k) {
		nullError(_k);
		Value deleted = forward.remove(_k);
		Key tmpk = backward.remove(deleted);
		if (tmpk != _k) {
			throw new AssertionError("impartial removal, how?! and this is weird!");
		}
		return deleted;
	}
}
