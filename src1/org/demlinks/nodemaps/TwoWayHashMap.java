/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
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


package org.demlinks.nodemaps;



import java.util.*;

import org.q.*;



/**
 * provides 1 to 1 mapping between two Objects: Key<->Value<br>
 * can find one given the other ie. get value if you know key and get key if you
 * know value<br>
 * permits no nulls<br>
 * uses .equals() to test see: {@link HashMap#get(Object)}<br>
 * seems it also uses "==" to test so if either equality yields true then it's
 * considered the same object<br>
 * 
 * @param <Key>
 * @param <Value>
 */
public class TwoWayHashMap<Key, Value> {
	
	private final HashMap<Key, Value>	forward;
	private final HashMap<Value, Key>	backward;
	
	
	public TwoWayHashMap() {
		
		this.forward = new HashMap<Key, Value>();
		this.backward = new HashMap<Value, Key>();
	}
	
	
	/**
	 * (non-Javadoc) should contain no null values tho, thus a null return would
	 * mean there's no key->value tuple however empty strings are allowed, if
	 * any
	 * 
	 * @see java.util.HashMap#get(Object key)
	 */
	public Value getValue( final Key _k ) {
		
		assert null != ( _k );
		return this.forward.get( _k );
	}
	
	
	public Key getKey( final Value _v ) {
		
		assert null != ( _v );
		return this.backward.get( _v );
	}
	
	
	/**
	 * adds or replaces Key-Value tuple
	 * 
	 * @param _k
	 *            key
	 * @param _v
	 *            value
	 * @return false if key already existed and was associated with a value;
	 *         true if it didn't exist
	 */
	public boolean putKeyValue( final Key _k, final Value _v ) {
		
		assert null != ( _k );
		assert null != _v;
		final Value prevFwdVal = this.forward.put( _k, _v );
		final boolean noPrevOne = prevFwdVal == null;
		if ( !noPrevOne ) {
			this.backward.remove( prevFwdVal ); // this value was replaced so it
												// must not exist in this other
												// list
		}
		final Key prevBackwdKey = this.backward.put( _v, _k );
		final boolean noPrevTwo = prevBackwdKey == null;
		if ( !noPrevTwo ) {
			this.forward.remove( prevBackwdKey );
		}
		return noPrevOne && noPrevTwo;
	}
	
	
	/**
	 * @return number of elements in this 1 to 1 Map
	 */
	public int size() {
		
		return this.forward.size();// == backward.size()
	}
	
	
	/**
	 * @param _k
	 * @return
	 */
	public Value removeKey( final Key _k ) {
		
		assert null != ( _k );
		final Value deleted = this.forward.remove( _k );
		final Key tmpk = this.backward.remove( deleted );
		if ( tmpk != _k ) {
			Q.bug( "impartial removal, how?! and this is weird!" );
		}
		return deleted;
	}
	
	
	/**
	 * @param _v
	 * @return
	 */
	public Key removeValue( final Value _v ) {
		
		assert null != ( _v );
		final Key _k = this.getKey( _v );
		if ( _v != this.removeKey( _k ) ) {
			Q.bug( "we removed the value of another key, this means 2 keys had same value in this 1 to 1 MAP" );
		}
		return _k;
	}
	
	
	public Iterator<Map.Entry<Key, Value>> getKeyValueIterator() {
		
		return this.forward.entrySet().iterator();
	}
}
