package org.demlinks.javathree;

public class IDToNodeMap {//TODO change String to a new class named ID
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

	public void put(String id, Node node) throws Exception {
		map.putKeyValue(id, node);
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
}
