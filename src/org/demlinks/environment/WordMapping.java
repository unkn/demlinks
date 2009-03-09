

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
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
		int charIndex = 0;
		int horizIndex = 0;
		int vertIndex = 0;
		IntermediaryNode[] horiz = new IntermediaryNode[word.length()];
recheckIF:
		horiz[charIndex]=
		IntermediaryNode in = this.getNextIntermediaryNodeForCharAt(
				(CharNode)nl.getNodeAt( charIndex ), charIndex, null );
		if ( null != in ) {
			WordNode wordNode = (WordNode)in.getFather();
			// so we found the wordNode that starts with word[0]
			// now we need to get next child and see if it's word[1]
			// unless there's no word[1] ie. length=1
			// if (word.length())
			charIndex++;
			if ( charIndex >= word.length() ) {
				// we're done here, solution is wordNode
				return wordNode;
			}
			IntermediaryNode in = (IntermediaryNode)wordNode.getChildNextOf( in );
			if (null == in) {
				//no more? horizontally
			}
			if ( !Environment.isIntermediaryNode( in ) ) {
				throw new BugError(
						"wordNode is not trully NodeWithDupChildren"
								+ "it should have only IntermediaryNode(s) as children" );
			}
			Node any2 = in.getPointee();
			if ( Environment.isWordNode( any2 ) ) {
				
			}
			if ( Environment.isCharNode( any2 ) ) {
				if ( any2 == nl.getNodeAt( charIndex ) ) {
					
				} else {
					//not our needed char
					charIndex--;
					goto recheckIF;
				}
			}
			
		} else {
			// there's no word starting with word[0] so return null;
			return null;
		}
		
		return null;
	}
	
	/**
	 * we looking for a parent NodeWithDupChildren that has AllWords as parent
	 * AND also has chrNode as child at the specified indexPos;<br>
	 * AND we're continuing from previouslyFound IntermediaryNode, by skipping
	 * it
	 * 
	 * @param chrNode
	 * @param indexPos
	 *            position of chrNode in the list of children for the returned
	 *            node
	 * @param previouslyFoundIN
	 *            continue from this IntermediaryNode, skipping it; go next;<br>
	 *            if null, try first IntermediaryNode found while parsing from
	 *            the beginning of parents of chrNode
	 * @return an IntermediaryNode that is between IntermediaryNode.getFather()
	 *         which you wanted AND chrNode<br>
	 *         you would append .getFather() on the return node to get the
	 *         WordNode
	 */
	protected IntermediaryNode getNextIntermediaryNodeForCharAt(
			CharNode chrNode, int indexPos, IntermediaryNode previouslyFoundIN ) {

		Debug.nullException( chrNode, indexPos );
		Node parser = null;
		if ( null != previouslyFoundIN ) {
			parser = previouslyFoundIN;
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BadParameterException( "wrong parameter" );
			}
			if ( ( (IntermediaryNode)parser ).getPointee() != chrNode ) {
				throw new BadParameterException(
						"chrNode is not a child of previouslyFound" );
			}
			
			// get next parent of chrNode, that's intermediary node and that's
			// next of previouslyFound
			
			// so could be null
		}
		

		// parser can be null here before assignment
		parser = chrNode.getNextParent( Environment.AllIntermediaryNodes,
				parser );
		// we're here having the next potential intermediary node, under
		// 'parser'
		while ( null != parser ) {
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BugError( "we're supposed to only get \"parser\"'s"
						+ " that are intermediary nodes" );
			}
			// we need to check if the intermediary is child of a WordNode
			// since there can be only 1 father for an IntermediaryNode
			NodeWithDupChildren wordNode = ( (IntermediaryNode)parser ).getFather();
			if ( Environment.isWordNode( wordNode ) ) {
				// so it's a WordNode
				// now we need to check the intermediaryNode's index is
				if ( wordNode.hasChildAtPos( parser, indexPos ) ) {
					// the we found this intermediary node that's between the
					// wordNode and the charNode where charNode is at indexPos
					// in this wordNode
					return (IntermediaryNode)parser;
				}
			}
			
			// try next parent, but only look for intermediary nodes, and
			// continue from last found parent
			parser = chrNode.getNextParent( Environment.AllIntermediaryNodes,
					parser );
		}
		
		return null;
		
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
