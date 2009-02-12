
package org.demlinks.node;

import org.demlinks.debug.Debug;
import org.demlinks.exceptions.InconsistentLinkException;

public class GlobalNodes {
	
	// parent nodes:
	public static Node	AllPointers				= new Node();
	public static Node	AllNodesWithDupChildren	= new Node();
	public static Node	AllRandomNodes			= new Node();
	
	public static boolean isPointer( Node whatNode )
			throws InconsistentLinkException {
		Debug.nullException( whatNode );
		return whatNode.hasParent( AllPointers );
	}
}
