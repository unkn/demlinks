

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;
import org.demlinks.node.NodeList;
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
	public NodeList getNodeForWord( String word ) {

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
		// list of all WordNodes that match this word
		NodeList solutions = new NodeList();
		// we have them all chars in 'nl'
		// and in effect we can parse them in any direction
		// TODO
		
		IntermediaryNode intermediaryNodeForNodeOnPos0 = null;// intermediary
		// nodes
		// for word[0]
		Node nodeThatHasToBeOnPos0 = nl.getNodeAt( 0 );// first char
		
		int indexOfNextExpectedChar = 1;// 0 based though
		while ( indexOfNextExpectedChar <= ( word.length() - 1 ) ) {
			// the while if, will help us handle 1 char words; nothing else
			// ie. the while will be broken only if our word is 1 char long,
			// hence index=0
			
			// attempts to find next WordNode for word[charIndex], that's a
			// different parent
			// like parallel on the Z axis; same child CharNode
			intermediaryNodeForNodeOnPos0 = this.getNextIntermediaryNodeForNodeAt(
					nodeThatHasToBeOnPos0, 0, intermediaryNodeForNodeOnPos0 );
			if ( null == intermediaryNodeForNodeOnPos0 ) {
				// none found, hence there's no (more)word(s) having word[0] at
				// index 0
				break;// this is the only one that will break the while
			}
			NodeWithDupChildren wordNode = intermediaryNodeForNodeOnPos0.getFather();
			if ( null == wordNode ) {
				throw new BugError( "intermediary node w/o father?!" );
			}
			
			// wordNode already has intermediaryNodeForNodeOnPos0 on pos 0, thus
			// we try finding next chars of word from pos 1 in wordNode
			// continuing from pos 1
			int backup = indexOfNextExpectedChar;
			indexOfNextExpectedChar = this.digDeep( wordNode, nl,
					indexOfNextExpectedChar, intermediaryNodeForNodeOnPos0 );
			
			if ( indexOfNextExpectedChar < 0 ) {
				// ie. -1 from encountering bad char
				// bad wordNode, need to get next wordNode
				indexOfNextExpectedChar = backup;
				continue;
			} else {
				if ( indexOfNextExpectedChar < word.length() - 1 ) {
					// there's still some char(s) left
					// we would ideally go UP, and keep
					// indexOfNextExpectedChar
					// where it is
					// TODO
					nodeThatHasToBeOnPos0 = wordNode;
					intermediaryNodeForNodeOnPos0 = null;// start from
					// beginning
					continue;
				} else {
					if ( indexOfNextExpectedChar == word.length() ) {
						// we found wordNode to be one solution
						// and we should go next wordNode
						solutions.addLast( wordNode );
						indexOfNextExpectedChar = 1;
						continue;// this will go to while and attempt to go next
						// wordNode
					} else {
						// indexOfNextExpectedChar > word.length()
						throw new BugError( "went over bounds" );
					}// else
				}// else
			}// else
		}// while
		
		return solutions;// can be empty
	}
	
	/**
	 * recursive method
	 * 
	 * @param wordNode
	 * @param expectedString
	 *            the chars in the order in which they're expected to be found
	 * @param indexOfExpectedChar
	 *            the index of the next char that is to be expected to exist<br>
	 *            this index is only applied on <tt>expectedString</tt>
	 * @param lastINFound
	 *            we're skipping this, and the next IntermediaryNode found will
	 *            be either CharNode or WordNode<br>
	 *            if CharNode then it must be equal to expectedString[charIndex]
	 *            and if so charIndex++ and go on with parsing; if CharNode is
	 *            different char than expected then abort with return -1 and you
	 *            should choose another wordNode<br>
	 *            else if WordNode, digDeep() until u find a CharNode then apply
	 *            what we said above for CharNode<br>
	 * @return the index of the next char that needs to be checked for<br>
	 *         OR -1 if there was an unexpected char encountered, which means, u
	 *         should get another <tt>wordNode</tt>
	 */
	private int digDeep( NodeWithDupChildren wordNode, NodeList expectedString,
			int indexOfExpectedChar, IntermediaryNode lastINFound ) {

		Debug.nullException( wordNode, expectedString, indexOfExpectedChar );
		IntermediaryNode in = lastINFound;// can be null
		
		// if more chars to go, and no bad char encountered
		while ( ( indexOfExpectedChar < ( expectedString.size() - 1 ) )
				&& ( indexOfExpectedChar >= 0 ) ) {
			// this is like parallel on the X axis; same wordNode parent
			in = wordNode.getNextIntermediary( in );
			if ( null == in ) {
				// no more intermediaries? then break/exit method
				// and if word[0] has this wordNode then you should go upward,
				// on a parent of this wordNode that has this wordNode on
				// position 0 and check that parent's children for next expected
				// chars or wordNodes that contain the expected chars
				break;
			} else {
				// found one, whose child may be Word or Char node
				Node wordOrChar = in.getPointee();
				if ( Environment.isCharNode( wordOrChar ) ) {
					// is CharNode then we check if it's the expected char
					if ( wordOrChar == expectedString.getNodeAt( indexOfExpectedChar ) ) {
						// good, now expect next char
						indexOfExpectedChar++;
						continue;
					} else {
						// bad char, the char we found was different than the
						// expected char
						// therefore we must choose another wordNode,
						// ie. next on Z axis
						indexOfExpectedChar = -1;
						break;
					}
				} else {
					// it's not Char
					if ( Environment.isWordNode( wordOrChar ) ) {
						indexOfExpectedChar = this.digDeep(
								(NodeWithDupChildren)wordOrChar,
								expectedString, indexOfExpectedChar, null );
						continue;
					} else {
						// not char not word?!
						throw new BugError(
								"supposed to be CharNode OR WordNode not something else" );
					}// else
				}// else
			}// else
		}// while
		
		return indexOfExpectedChar;
	}
	
	/**
	 * we looking for a parent NodeWithDupChildren that has AllWords as parent
	 * AND also has forNode as child at the specified indexPos;<br>
	 * AND we're continuing from previouslyFound IntermediaryNode, by skipping
	 * it
	 * 
	 * @param forNode
	 * @param indexPos
	 *            position of chrNode in the list of children for the returned
	 *            node
	 * @param previouslyFoundIN
	 *            continue from this IntermediaryNode, skipping it; go next;<br>
	 *            if null, try first IntermediaryNode found while parsing from
	 *            the beginning of parents of forNode
	 * @return an IntermediaryNode that is between IntermediaryNode.getFather()
	 *         which you wanted AND forNode<br>
	 *         you would append .getFather() on the return node to get the
	 *         WordNode
	 */
	protected IntermediaryNode getNextIntermediaryNodeForNodeAt( Node forNode,
			int indexPos, IntermediaryNode previouslyFoundIN ) {

		Debug.nullException( forNode, indexPos );
		Node parser = null;
		if ( null != previouslyFoundIN ) {
			parser = previouslyFoundIN;
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BadParameterException( "wrong parameter" );
			}
			if ( ( (IntermediaryNode)parser ).getPointee() != forNode ) {
				throw new BadParameterException(
						"chrNode is not a child of previouslyFound" );
			}
			
			// get next parent of chrNode, that's intermediary node and that's
			// next of previouslyFound
			
			// so could be null
		}
		

		// parser can be null here before assignment
		parser = forNode.getNextParent( Environment.AllIntermediaryNodes,
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
			parser = forNode.getNextParent( Environment.AllIntermediaryNodes,
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
