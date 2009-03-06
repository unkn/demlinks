

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;
import org.demlinks.nodemaps.CharNode;



public class CharMapping {
	
	private final TwoWayHashMap<String, CharNode>	mapCharsToNodes;
	
	public CharMapping() {

		this.mapCharsToNodes = new TwoWayHashMap<String, CharNode>();
		// this.init();
		// }
		//	
		// private void init() {
		//
		// this.mapAllChars();
	}
	
	// private void mapAllChars() {
	//
	// this.mapCharsToNodes = new TwoWayHashMap<String, CharNode>();
	//		
	// // int count = 0;
	// for ( char c = 0; c <= 255; c++ ) {// [0..255] inclusive
	// // count++;
	// this.mapChar( c );
	//			
	// // System.out.println( String.valueOf( (int)c ) + " "
	// // + String.valueOf( c ) + " " + node );
	//			
	// }
	// // System.out.println( count );
	// }
	
	/**
	 * will throw exception if mapping didn't exist
	 * 
	 * @param c
	 * @return the node that was already mapped to char c
	 * @see #isMappedChar(char)
	 */
	public CharNode mapNewChar( char c ) {

		Debug.nullException( c );
		
		if ( this.isMappedChar( c ) ) {
			throw new BadParameterException( "mapping already exists." );
		}
		
		CharNode node = new CharNode();
		String s = String.valueOf( c );
		if ( !this.mapCharsToNodes.putKeyValue( s, node ) ) {
			// if false, then already existed
			throw new BugError( "'c' was already associated: " + s + ":"
					+ this.mapCharsToNodes.getValue( s ) );
			// which means the method isNodeForChar(c) above doesn't work as it
			// should
		}
		
		return node;
	}
	
	/**
	 * @param c
	 *            char
	 * @return true if already a Node is mapped for char c
	 */
	public boolean isMappedChar( char c ) {

		Node n = this.getNodeForChar( c );
		// this.mapCharsToNodes.getValue( String.valueOf( c ) );
		return ( null != n );
	}
	
	

	/**
	 * If there's no such Node, it will be created and mapped for char c
	 * 
	 * @param c
	 * @return the Node that's mapped to char c; never null
	 */
	public CharNode ensureNodeForChar( char c ) {

		// Node n = this.mapCharsToNodes.getValue( String.valueOf( c ) );
		CharNode n = this.getNodeForChar( c );
		if ( null == n ) {
			n = this.mapNewChar( c );// make new
		}
		if ( null == n ) {
			throw new BugError( "mapChar doesn't work as designed" );
		}
		return n;
	}
	
	/**
	 * @param c
	 * @return null or the node that was mapped to char c
	 */
	public CharNode getNodeForChar( char c ) {

		CharNode n = this.mapCharsToNodes.getValue( String.valueOf( c ) );
		return n;
	}
	

}
