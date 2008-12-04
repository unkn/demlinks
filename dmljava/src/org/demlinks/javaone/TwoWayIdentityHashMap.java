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
