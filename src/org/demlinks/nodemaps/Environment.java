

package org.demlinks.nodemaps;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.InconsistentLinkException;
import org.demlinks.node.Node;



public class Environment {
	
	
	// all the following are supposed to be parent nodes
	public static final Node	AllPointerNodes				= new Node();
	public static final Node	AllNodeWithDupChildrenNodes	= new Node();
	public static final Node	AllRandomNodes				= new Node();
	public static final Node	AllIntermediaryNodes		= new Node();
	
	public static final Node	AllCharNodes				= new Node();
	public static final Node	AllWordNodes				= new Node();
	public static final Node	AllPhraseNodes				= new Node();
	public static final Node	AllDelimiterNodes			= new Node();
	
	// it's resizable, the initial allocation for array for in depth parsing
	public static final int		DEFAULT_UPLEVEL				= 100;
	
	/**
	 * AllPointerNodes -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isPointer( Node whatNode ) {

		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllPointerNodes );
	}
	
	/**
	 * AllNodeWithDupChildrenNodes -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isNodeWithDupChildren( Node whatNode ) {

		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllNodeWithDupChildrenNodes );
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
	
	/**
	 * @param whatNode
	 * @return
	 */
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
	
	/**
	 * @param whatNode
	 * @return
	 */
	public static boolean isWordNode( Node whatNode ) {

		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllWordNodes );
	}
	
	public static boolean isCharNode( Node whatNode ) {

		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllCharNodes );
	}
}
