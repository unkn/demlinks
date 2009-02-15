
package org.demlinks.node;

import org.demlinks.debug.Debug;

public class GlobalNodes {
	
	
	// all the following are supposed to be parent nodes
	public static final Node	AllPointers				= new Node();
	public static final Node	AllNodesWithDupChildren	= new Node();
	public static final Node	AllRandomNodes			= new Node();
	public static final Node	AllIntermediaryNodes	= new Node();
	
	/**
	 * AllPointers -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isPointer( Node whatNode ) {
		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllPointers );
	}
	
	/**
	 * AllNodesWithDupChildren -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isNodeWithDupChildren( Node whatNode ) {
		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllNodesWithDupChildren );
	}
	
	/**
	 * AllRandomNodes -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isRandomNode( Node whatNode ) {
		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllRandomNodes );
	}
	
	public static boolean isIntermediaryNode( Node whatNode ) {
		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllIntermediaryNodes );
	}
}
