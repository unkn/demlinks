package org.demlinks.javaone;

public class Extractor {
	Node node;
	String nodeID;
	Environment environ;
	boolean created=false;//true means it's an ID and it didn't exist so we created it, and will map it later with map()
	boolean mapped=false;
	boolean undone=false;
	
	Extractor(Environment env, Object node) {
		Environment.nullError(env, node);
		environ = env;
		
		Node ourNode = env.getNode(node);
		if (null == ourNode) {
			//doesn't exist
			if (Environment.isTypeID(node)) {
				ourNode = new Node(env);
				nodeID = (String)node;
				created = true;
			} else {
					if (Environment.isTypeNode(node)) {
						//we were passed a node object that doesn't exist
						throw new AssertionError("won't handle those, but we could");
					} else {
						throw new AssertionError("unhandled type");
					}
			}//else
		}//if
		this.node = ourNode;
		return this.node;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public void undo() {
		if (undone) {
			throw new AssertionError("called undo twice!");
		}
		if (created) {
			// java will dispose of the above new Node()
			created=false;
			if (mapped) {
				//unmap
				environ.internalUnMapNode(nodeID, node);
				mapped=false;
			}
		}
		undone = true;
	}

	public void map() throws Exception {
		if (created) {
			if (mapped) { 
				throw new AssertionError("called map() twice!");
			}
			//only if created we'd need to map it to an ID
			environ.internalMapNode(nodeID, node);
			mapped=true;
		}
		if (undone) {
			throw new AssertionError("called after undo()");
		}
	}
}
