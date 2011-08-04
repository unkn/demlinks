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

package org.demlinks.javathree;

import java.util.*;



public class IDToNodeMap {
	
	// the contents of the object ID are mapped to the object Node, such that two different ID objects with same content will
	// point to same Node object and a Node object is different if it's another object even if their contents are the same.
	private final TwoWayHashMap<Id, Node0>	map;
	
	
	public IDToNodeMap() {
		map = new TwoWayHashMap<Id, Node0>();
	}
	
	
	public Node0 getNode( final Id id ) {
		return map.getValue( id );
	}
	
	
	public Id getID( final Node0 node ) {
		return map.getKey( node );
	}
	
	
	/**
	 * @param id
	 * @param node
	 * @return false if the id WAS already mapped to a node; true if it wasn't hence it's unique
	 */
	public boolean put( final Id id, final Node0 node ) {
		return map.putKeyValue( id, node );
	}
	
	
	public Node0 removeID( final Id id ) {
		return map.removeKey( id );
	}
	
	
	public Id removeNode( final Node0 node ) {
		return map.removeValue( node );
	}
	
	
	public int size() {
		return map.size();
	}
	
	
	public Iterator<Map.Entry<Id, Node0>> getKeyValueIterator() {
		return map.getKeyValueIterator();
	}
}
