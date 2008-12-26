package org.demlinks.javathree;

import static org.demlinks.javathree.Environment.nullException;

/**
 * handles the NodeRef list at the Node level
 *
 */
public class NodeRefsList_L2 extends NodeRefsList_L1 {
	
	/**
	 * @param nodeLevel0
	 * @return
	 */
	public boolean containsNode(Node nodeLevel0) {
		nullException(nodeLevel0);
		return (null != this.getNodeRef(nodeLevel0));
	}
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via this method
	 * @param node
	 * @return
	 */
	public NodeRef newNodeRef(Node node) {
		nullException(node);
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
	public final NodeRef getNodeRef_L0(Node node_L0) {
		nullException(node_L0);
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

	/**
	 * @param node
	 * @return
	 */
	public boolean removeNode(Node node) {
		NodeRef nr = getNodeRef(node);
		if (null == nr) {
			return false;
		}
		return removeNodeRef(nr);
	}
	
	
	
}
