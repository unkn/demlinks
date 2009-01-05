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

import java.util.Iterator;
import java.util.Map;

public class IDToNodeMap {//TODO change String to a new class named ID but only after everything works
//the contents of the object ID are mapped to the object Node, such that two different ID objects with same content will 
//	point to same Node object and a Node object is different if it's another object even if their contents are the same.
	private TwoWayHashMap<String, Node> map;
	
	public IDToNodeMap() {
		map = new TwoWayHashMap<String, Node>();
	}
	
	public Node getNode(String id) {
		return map.getValue(id);
	}
	
	public String getID(Node node) {
		return map.getKey(node);
	}

	/**
	 * @param id
	 * @param node
	 * @return true if the id WAS already mapped to a node
	 * @throws Exception
	 */
	public boolean put(String id, Node node) {
		return map.putKeyValue(id, node);
	}

	public Node removeID(String id) {
		return map.removeKey(id);
	}
	
	public String removeNode(Node node) {
		return map.removeValue(node);
	}
	
	public int size() {
		return map.size();
	}
	
	public Iterator<Map.Entry<String, Node>> getKeyValueIterator() {
		return map.getKeyValueIterator();
	}
}
