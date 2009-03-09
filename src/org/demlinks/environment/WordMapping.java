

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;
import org.demlinks.node.NodeList;
import org.demlinks.nodemaps.CharNode;
import org.demlinks.nodemaps.Environment;
import org.demlinks.nodemaps.IntermediaryNode;
import org.demlinks.nodemaps.NodeWithDupChildren;
import org.demlinks.nodemaps.WordNode;



public class WordMapping extends CharMapping {
	
	
	public WordMapping() {

		super();
		this.mapAllCharsNow();
	}
	
	/**
	 * @param word
	 * @return true if successfully added;false if already existed;
	 */
	public WordNode addWord( String word ) {

		Debug.nullException( word );
		if ( !this.isGoodWord( word ) ) {
			throw new BadParameterException();
		}
		
		// if already exists, then return that one
		WordNode wordNode = this.getNodeForWord( word );
		if ( null != wordNode ) {
			return wordNode;// already existed
		}
		// else add it now
		wordNode = new WordNode();
		
		char c;
		Node n;
		for ( int i = 0; i < word.length(); i++ ) {
			c = word.charAt( i );
			n = this.ensureNodeForChar( c );
			wordNode.appendChild( n );
		}
		
		return wordNode;
	}
	
	
	/**
	 * @param word
	 * @return
	 */
	public WordNode getNodeForWord( String word ) {

		char c;
		Node n;
		NodeList nl = new NodeList();
		for ( int i = 0; i < word.length(); i++ ) {
			c = word.charAt( i );
			n = this.getNodeForChar( c );
			if ( null == n ) {
				// one of the chars doesn't exist, hence the word doesn't exist
				return null;
			}
			// else
			// TODO solve
			// we need a parent(1) that has as parent AllWordNodes
			// this parent(1) also has a child 'n' at position 'i'
			// backtracking all the way
			// basically find all parents of 'n' that have parent AllWordNodes
			// AND have 'n' as a child at pos 'i'
			// AllWordNodes -> X -> 'n' , X is NodeWithDupChildren
			// we may also need Node.hasChildAt(child, pos)
			// parent.hasChildAtPos(n, i);
			nl.appendNode( n );
		}
		// we have them all chars in 'nl'
		// and in effect we can parse them in any direction
		// TODO
		return null;
	}
	
	/**
	 * we looking for a parent NodeWithDupChildren that has AllWords as parent
	 * AND also has chrNode as child at the specified indexPos;<br>
	 * AND we're continuing from previouslyFound NodeWithDupChildren, by
	 * skipping it
	 * 
	 * @param chrNode
	 * @param indexPos
	 *            position of chrNode in the list of children for the returned
	 *            node
	 * @param previouslyFound
	 *            continue from this, skipping it; go next
	 * @return
	 */
	private IntermediaryNode getNextWordNodeForCharAt( CharNode chrNode,
			int indexPos, IntermediaryNode previouslyFound ) {

		Debug.nullException( chrNode, indexPos );
		Node parser;
		if ( null == previouslyFound ) {
			parser = chrNode.getFirstParent();// could be just Node
		} else {
			parser = previouslyFound;
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BadParameterException( "wrong parameter" );
			}
			if ( ( (IntermediaryNode)parser ).getPointee() != chrNode ) {
				throw new BadParameterException(
						"chrNode is not a child of previouslyFound" );
			}
			
			NodeWithDupChildren wordNode = ( (IntermediaryNode)parser ).getFather();
			// getting next intermediary node
			parser = wordNode.getChildNextOf( parser );
			// and thus we skipped over previouslyFound
		}
		
		// we're here having the next potential intermediary node, under
		// 'parser'
		NodeWithDupChildren wordNode;
		while ( null != parser ) {
			if ( Environment.isIntermediaryNode( parser ) ) {
				wordNode = ( (IntermediaryNode)parser ).getFather();
			}
			parser = wordNode.getChildNextOf( parser );
		}
		
		return (IntermediaryNode)parser;
		
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
		
		for ( int i = 0; i < len; i++ ) {
			char p = word.charAt( i );
			if ( !this.isWordAllowedChar( p ) ) {
				return false;// bad word!
			}
		}
		return true;
	}
	
	/**
	 * @param chr
	 * @return
	 */
	public boolean isWordAllowedChar( char chr ) {

		if ( ( this.isLetter( chr ) ) || ( this.isInWordSpecialChars( chr ) )
				|| ( this.isDigit( chr ) ) ) {
			return true;
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
	 * @param chr
	 * @return
	 */
	public boolean isDigit( char chr ) {

		if ( ( chr >= '0' ) && ( chr <= '9' ) ) {
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
