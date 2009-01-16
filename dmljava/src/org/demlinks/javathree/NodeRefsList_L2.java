package org.demlinks.javathree;


/**
 * handles the NodeRef list at the Node level
 *
 */
public class NodeRefsList_L2 extends NodeRefsList_L1 {
	
	/**
	 * @param location
	 * @return
	 */
	public Node removeNode(Location location) {
		NodeRef nr = getNodeRefAt(location);
		if (null != nr) {
			Node nod = nr.getNode();
			if (removeNodeRef(nr)) {
				return nod;
			}
		}
		return null;
	}
	
	/**
	 * @param nodeLevel0
	 * @return
	 */
	public boolean containsNode(Node nodeLevel0) {
		Debug.nullException(nodeLevel0);
		return (null != this.getNodeRef(nodeLevel0));
	}
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via this method
	 * @param node
	 * @return
	 */
	public NodeRef newNodeRef(Node node) {
		Debug.nullException(node);
		NodeRef n = new NodeRef();
		n.setNode(node);
		return n;
	}
	
	/**
	 * @param node
	 * @return
	 */
	public NodeRef getNodeRef(Node node) {
		return getNodeRef_L0(node);
	}

	/**
	 * @param node_L0
	 * @return
	 */
	public final NodeRef getNodeRef_L0(Node node_L0) {//TODO generalize NodeRef and NodeRefsList
		Debug.nullException(node_L0);
		NodeRef parser = getFirstNodeRef();
		while (null != parser) {
			if (node_L0.equals(parser.getNode())) {
				break;
			}
			parser = parser.getNext();
		}
		return parser;
	}

	/**
	 * @return
	 */
	public Node getFirstNode() {
		if (getFirstNodeRef() != null) {
			return getFirstNodeRef().getNode();
		}
		return null;
	}


	/**
	 * @param node
	 * @return
	 */
	public boolean addLast(Node node) {
		NodeRef nr = getNodeRef(node);
		if (null == nr) {
			nr = newNodeRef(node);
		}
		return addLast(nr);
	}

	//TODO addFirst
	//TODO insert(Node, Location);
	//TODO insert(Node, Location, Node);
	//TODO replace(Node, Node);
	//TODO replace(Node, Location);
	//TODO replace(Node, Location, Node);
	//find+replace current, is not an option 
	/**
	 * @param node
	 * @return true if existed; either way after call it's removed
	 */
	public boolean removeNode(Node node) {
		NodeRef nr = getNodeRef(node);
		if (null == nr) {
			return false;
		}
		return removeNodeRef(nr);
	}
	
	
	
}
