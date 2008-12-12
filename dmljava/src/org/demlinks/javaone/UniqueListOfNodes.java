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
	 * @param node can be a Node object or a String ID of a Node object
	 * @return true if list changed as a result of the call
	 * @throws Exception 
	 * @transaction protected
	 */
	public boolean append(Object node) throws Exception {
		// the node can be:
		// 0. null -> throws error
		// 1. non-existing (or from another environ, same thing)
		// 1.1 Node object -> throw exception ? or use that object(maybe was created by the caller as temporary for a new
		// 1.2 String ID -> create new local Node()
		// 2. existing (implies only in this environment tested)
		// 2.1 Node object -> use it 
		// 2.2 String ID -> get it's Node object
		Environment.nullError(node);
		Environment env=getEnvironment();
		Node nodeToAdd=env.getNode(node);
		boolean tempNode = false;
		if (null == nodeToAdd) {
			//doesn't exist
			if (Environment.isTypeNode(node)) {
				throw new Exception("you passed me a Node object that doesn't exist; at least in this environment");
			} else {
				if (Environment.isTypeID(node)) {
					nodeToAdd = new Node(env);
					tempNode = true; // yes we created a new node from an ID but we didn't yet map the ID to the Node
				}
			}
		}//if
		
		boolean ret = listSet.add(nodeToAdd);//true = added a new one, hence it didn't exist previously
		//technicly if the above call throws exception, we don't have to undo anything until now
		// we also need to add the reverse link
		boolean ret2=false;
		try {
			ret2 = nodeToAdd.get(List.PARENTS).append(this.ourFatherNode);//or maybe create an internal _append() to bypass the circular calls
			if (tempNode) {
				//here we attempt to map the ID to Node, this should work because ID didn't exist above
				env.mapNode((String)node, nodeToAdd);
				//TODO make this except in a junit test, and check if correctly removed the append() we were supposed to do
			}
		} catch (Exception e) {
			if (ret) {
				//if ret==true this means we above added the nodeToAdd and hence if we wanna undo we have to remove it now
				listSet.remove(nodeToAdd);//remove works since it's a listSet hence there can't be 2 elemens that are the same
				//so above we're sure to remove the only existing and out element: nodeToAdd
			}
			if (ret2) {
				//remove reverse link also
				nodeToAdd.get(List.PARENTS).remove(this.ourFatherNode);
			}
			throw e;
		}
		return ret;
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
