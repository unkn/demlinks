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

import java.util.IdentityHashMap;

public class TwoWayIdentityHashMap<Key,Value> {
	private IdentityHashMap<Key,Value> forward;
	private IdentityHashMap<Value,Key> backward;
	
	TwoWayIdentityHashMap() {
		forward = new IdentityHashMap<Key, Value>();
		backward = new IdentityHashMap<Value, Key>();
	}
	
	public Value getValue(Key _k) {
		return forward.get(_k);
	}
	
	public Key getKey(Value _v) {
		return backward.get(_v);
	}

	public void putKeyValue(Key _k, Value _v) {
		forward.put(_k, _v);
		backward.put(_v, _k);
	}

	public int size() {
		return forward.size();// == backward.size()
	}

	public Value removeKey(Key _k) {
		Value deleted = forward.remove(_k);
		backward.remove(deleted);
		return deleted;
	}
}
