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

import java.util.ListIterator;

/**
 * * a list of unique Node objects (no two are the same)<br>
 * * the order of Nodes in the list matters
 */
public class UniqueListOfNodes { // order matters; Nodes are unique

	private LinkedListSet<Node> listSet; // this is here instead of inherited because we don't want users to access other methods from it
	 
	public UniqueListOfNodes() {
		listSet = new LinkedListSet<Node>();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 842508346073648046L;

	
	
	public boolean append(Node node) {
		Environment.nullException(node);
		return listSet.add(node);
	}
	
	public boolean contains(Node node) {
		Environment.nullException(node);
		return listSet.contains(node);
	}
	
	public boolean isEmpty() {
		return listSet.isEmpty();
	}

	public int size() {
		return listSet.size();
	}

	/**
	 * @see java.util.LinkedList#remove(Object)
	 */
	public boolean remove(Node node) {
		Environment.nullException(node);
		return listSet.remove(node);
	}

	public NodeIterator nodeIterator(int index) {
		return new NodeItr(index);
	}
	
	private class NodeItr implements NodeIterator {
		ListIterator<Node> litr;
		int posNow;
		
		//constructor
		NodeItr(int index) { //if index = size() then position at end
			litr = listSet.listIterator(index);
//			if (index < 0 || index > size()) {
//				throw new IndexOutOfBoundsException("Index: "+index+
//								    ", Size: "+size());
//			}
//			if (index == size()) { // size could be 0
//				index = size() >> 1;
//			}
//			posNow = index;
		}

		@Override
		public boolean find(Object node) {
			Environment.nullException(node);
			int idx = listSet.indexOf(node);
			if (idx == -1) {
				return false;
			}
			posNow= idx;
			return true;
		}

		@Override
		public boolean insert(Node whatNode, Location location) {
			Environment.nullException(whatNode);
			// TODO Auto-generated method stub
			return false;
		}

	}//NodeItr class


	
}
