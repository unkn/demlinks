/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */


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
	}
	
	protected void mapAllCharsNow() {

		this.mapAllChars();
	}
	
	private void mapAllChars() {

		
		// int count = 0;
		for ( char c = 0; c <= 255; c++ ) {// [0..255] inclusive
			// count++;
			this.mapNewChar( c );
			
			// System.out.println( String.valueOf( (int)c ) + " "
			// + String.valueOf( c ) + " " + node );
			
		}
		// System.out.println( count );
	}
	
	/**
	 * will throw exception if mapping already existed
	 * 
	 * @param c
	 * @return the node that was just mapped to char c
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
	
	
	/**
	 * @param chr
	 * @return
	 */
	public boolean isLetter( char chr ) {

		if ( ( ( chr >= 'a' ) && ( chr <= 'z' ) )
				|| ( ( chr >= 'A' ) && ( chr <= 'Z' ) ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param chr
	 * @return
	 */
	public boolean isDigit( char chr ) {

		if ( ( chr >= '0' ) && ( chr <= '9' ) ) {
			return true;
		}
		return false;
	}
	
}
