
package org.demlinks.node;

import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.InconsistentLinkException;

public class Environment {
	
	
	// all the following are supposed to be parent nodes
	public static final Node	AllPointers				= new Node();
	public static final Node	AllNodesWithDupChildren	= new Node();
	public static final Node	AllRandomNodes			= new Node();
	public static final Node	AllIntermediaryNodes	= new Node();
	
	public static final Node	AllChars				= new Node();
	public static final Node	AllWords				= new Node();
	public static final Node	AllPhrases				= new Node();
	public static final Node	AllDelimiters			= new Node();
	
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
	
	/**
	 * used in constructor
	 * 
	 * @param childNode
	 * @param parentNode
	 */
	protected static void internalCreateNodeAsChildOf( Node childNode,
			Node parentNode ) {
		Debug.nullException( childNode, parentNode );
		boolean existsAlready = false;
		try {
			existsAlready = parentNode.appendChild( childNode );
		} catch ( InconsistentLinkException e ) {// half of the link exists
			// already
			existsAlready = true;// so we can throw
		} finally {
			if ( existsAlready ) {
				throw new BugError( "parentNode->childNode already existing?!" );
			}
		}
	}
}
