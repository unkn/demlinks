package org.demlinks.javaone;

import java.util.ListIterator;


//TODO may want same interface used in both this list and its iterator

public class UniqueListOfNodes {

	private LinkedListSet<Node> listSet; // this is here instead of inherited because we don't want users to access other methods from it
	 
	public UniqueListOfNodes() {
		listSet = new LinkedListSet<Node>();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 842508346073648046L;

	public boolean append(Node node) {
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
		public void find(Node node) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void insert(Node whatNode, Location location) {
			// TODO Auto-generated method stub
			//UniqueListOfNodes.
			insert(whatNode, location); // TODO this will need to call insert of UniqueListOfNodes class
		}

	}

	//TODO temporary
	public ListIterator<Node> listIterator() {
		return listSet.listIterator();
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
