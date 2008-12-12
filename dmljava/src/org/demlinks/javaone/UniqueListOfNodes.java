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



//TODO may want same interface used in both this list and its iterator

public class UniqueListOfNodes {

	private Node ourFatherNode;
	private LinkedListSet<Node> listSet; // this is here instead of inherited because we don't want users to access other methods from it
	 
	public UniqueListOfNodes(Node fatherNode) {
		Environment.nullError(fatherNode);
		ourFatherNode = fatherNode;
		listSet = new LinkedListSet<Node>();
	}
	
	public Environment getEnvironment() {
		return ourFatherNode.getEnvironment();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 842508346073648046L;

	/**
	 * @param node
	 * @return true if list changed as a result of the call
	 */
	public boolean append(Object node) {
		Environment.nullError(node);
		if (getEnvironment().isTypeID(node))
		return listSet.add(node);
	}

	public boolean contains(Node node) {
		return listSet.contains(node);
	}

	public boolean isEmpty() {
		return listSet.isEmpty();
	}

	public int size() {
		return listSet.size();
	}

	/**
	 * the following won't work
	 * {@linkplain LinkedList#remove(Object)}
	 * {@linkplain LinkedListSet#remove(Object)}
	 * {@linkplain LinkedList#remove}
	 * {@linkplain LinkedListSet#remove}
	 * this works:
	 * @see java.util.LinkedList#remove(Object)
	 */
	public boolean remove(Node node) {
		return listSet.remove(node);
	}
	
	public NodeIterator nodeIterator(int index) {
		return new NodeItr(index);
	}
	
	private class NodeItr implements NodeIterator {
		NodeItr(int index) {
			
		}

		@Override
		public void find(Object node) {
			// TODO Auto-generated method stub

			String name = node.getClass().getSimpleName();
			if ( name.equals("Node")) {
				Node tmp = (Node)node;
				System.out.println(tmp);
			} else {
				if (name.equals("String")) {
					String tmp = (String)node;
					System.out.println(tmp);
				}
			}
		}

		@Override
		public void insert(Node whatNode, Location location) {
			// TODO Auto-generated method stub
			//UniqueListOfNodes.
			//insert(whatNode, location); // TODO this will need to call insert of UniqueListOfNodes class
//			if (null == whatNode) {
//				whatNode = new Node(environ);
//			}
		}

	}

	public void insert(Node whatNode, Location location, Node locationNode) {
		// TODO Auto-generated method stub
		if (locationNode == null) {
			//TODO disallow location == BEFORE or AFTER or INSTEADOF
		}
	}
	
	public void insert(Node whatNode, Location location) {
		insert(whatNode, location, null);
	}
	
}
