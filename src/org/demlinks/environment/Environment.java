
package org.demlinks.environment;

import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.GlobalNodes;
import org.demlinks.node.Node;


public class Environment {
	
	private TwoWayHashMap<String, Node>	mapAllCharsToNodes;
	
	public Environment() {
		this.init();
	}
	
	private void init() {
		this.mapAllChars();
	}
	
	private void mapAllChars() {
		this.mapAllCharsToNodes = new TwoWayHashMap<String, Node>();
		
		// int count = 0;
		for ( char c = 0; c <= 255; c++ ) {
			// count++;
			Node node = new Node();
			this.mapAllCharsToNodes.putKeyValue( String.valueOf( c ), node );
			// System.out.println( String.valueOf( (int)c ) + " "
			// + String.valueOf( c ) + " " + node );
			GlobalNodes.AllChars.appendChild( node );
		}
		// System.out.println( count );
	}
	
	/**
	 * @param word
	 * @return true if successfully added;false if already existed;
	 */
	public boolean addWord( String word ) {
		Debug.nullException( word );
		if ( !this.isGoodWord( word ) ) {
			throw new BadParameterException();
		}
		// TODO Auto-generated method stub
		boolean mostLikelyWordDoesntExist = false;
		char c;
		Node n;
		for ( int i = 0; i < word.length(); i++ ) {
			c = word.charAt( i );
			if ( !this.isNodeForChar( c ) ) {
				mostLikelyWordDoesntExist = true;
			}
			n = this.getNodeForChar( c );
		}
		
		if ( mostLikelyWordDoesntExist ) {
			// not all char nodes existed, so the word couldn't exist
			// TODO make new Node for this word
			Node wordNode = new Node();
			GlobalNodes.AllWords.appendChild( wordNode );
		}
		return true;
	}
	
	/**
	 * @param c
	 *            char
	 * @return true if already a Node is mapped for char c
	 */
	public boolean isNodeForChar( char c ) {
		String s = new String();
		s += c;
		Node n = this.mapAllCharsToNodes.getValue( s );
		return ( null != n );
	}
	
	/**
	 * If there's no such Node, it will be created and mapped for char c
	 * 
	 * @param c
	 * @return the Node that's mapped to char c; never null
	 */
	public Node getNodeForChar( char c ) {
		String s = new String();
		s += c;
		Node n = this.mapAllCharsToNodes.getValue( s );
		// tmp comment://TODO remove comments below
		// if ( null == n ) {
		// n = new Node();
		// this.mapAllCharsToNodes.putKeyValue( s, n );
		// }
		if ( null == n ) {
			throw new BugError();
		}
		return n;
	}
	
	/**
	 * @param word
	 * @return
	 */
	public boolean isGoodWord( String word ) {
		Debug.nullException( word );
		int len = word.length();
		if ( len <= 0 ) {
			throw new BadParameterException();
		}
		
		char firstChar = word.charAt( 0 );
		if ( this.isLetter( firstChar ) ) {
			if ( len > 1 ) {
				char lastChar = word.charAt( len - 1 );
				if ( this.isLetter( lastChar ) ) {
					// first and last chars are letters
					for ( int i = 1; i < len; i++ ) { // parse except first/last
						// chars
						char p = word.charAt( i );
						if ( ( !this.isLetter( p ) )
								&& ( !this.isInWordSpecialChars( p ) ) ) {
							return false;// bad word!
						}
					}
				}
			}
			return true;// good word;
		}
		return false;
	}
	
	/**
	 * @param c
	 * @return
	 */
	public boolean isInWordSpecialChars( char c ) {
		if ( ( c == '-' ) || ( c == '\'' ) ) {
			return true;
		}
		return false;
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
	 * @param phrase
	 *            a string containing many words and punctuation/delimiters
	 * @return true if successfully added; false if already existed
	 */
	public boolean addPhrase( String phrase ) {
		Debug.nullException( phrase );
		// TODO Auto-generated method stub
		// split it into words and delimiters
		String word = new String();
		for ( int i = 0; i < phrase.length(); i++ ) {
			char c = phrase.charAt( i );
			
			if ( this.isWordDelimiter( c ) ) {
				try {
					this.addWord( word );
				} catch ( BadParameterException e ) {
					throw new BadParameterException( "seems the word was bad" );
				}
			}
			word += c;
		}
		return false;
	}
	
	/**
	 * @param c
	 *            char
	 * @return true if the char is used between two words
	 */
	public boolean isWordDelimiter( char c ) {
		switch ( c ) {
		case ' ':
		case '/':
		case '+':
		case '|':
			return true;
		default:
			return false;
		}
	}
}
