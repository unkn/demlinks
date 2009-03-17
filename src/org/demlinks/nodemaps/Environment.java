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


package org.demlinks.nodemaps;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.errors.RecursionTooDeepError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.exceptions.InconsistentLinkException;
import org.demlinks.node.Node;



public class Environment {
	
	
	// all the following are supposed to be parent nodes
	public static final Node						AllDomainPointerNodes			= new Node();
	public static final Node						AllPointerNodes					= new Node();
	public static final Node						AllNodeWithDupChildrenNodes		= new Node();
	public static final Node						AllRandomNodes					= new Node();
	public static final Node						AllIntermediaryNodes			= new Node();
	
	public static final Node						AllCharNodes					= new Node();
	public static final Node						AllWordNodes					= new Node();
	public static final Node						AllPhraseNodes					= new Node();
	public static final Node						AllWordDelimiterNodes			= new Node();
	
	// ----------- Nodes temporarily used by methods
	// used by WordMapping.getNodeForWord()
	public static Node								lastSolutionsForLastGottenWord	= new Node();
	// == two FIFO lists:
	public static Node								intermediaryNodeForNodeOnPos0	= new Node();
	public static Node								nodeThatHasToBeOnPos0			= new Node();
	// ==
	public static PointerNode						scanStatus						= new PointerNode();
	public static final Node						badChar							= new Node();
	public static final Node						completedWord					= new Node();
	public static NodeWithDupChildren				wordToBeProcessed				= new NodeWithDupChildren();
	
	// the following will always point at the intermediary:
	public static DomainPointerNode					expectedChar					= new DomainPointerNode(
																							wordToBeProcessed );
	
	// -----------
	
	// it's resizable, the initial allocation for array for in depth parsing
	public static final int							DEFAULT_UPLEVEL					= 100;
	

	private final TwoWayHashMap<String, CharNode>	mapCharsToNodes;
	
	public Environment() {

		this.mapCharsToNodes = new TwoWayHashMap<String, CharNode>();
	}
	
	// ---------------------------------- CharNode
	
	@SuppressWarnings( "unused" )
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
	 * @param c
	 * @return null or the node that was mapped to char c
	 */
	public CharNode getNodeForChar( char c ) {

		CharNode n = this.mapCharsToNodes.getValue( String.valueOf( c ) );
		return n;
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
	
	// ----------------------------------WordNode
	/**
	 * @param word
	 * @return WordNode
	 */
	public WordNode addWord( String word ) {

		Debug.nullException( word );
		if ( !this.isGoodWord( word ) ) {
			throw new BadParameterException();
		}
		
		WordNode wordNode;
		// if already exists, then return that one
		Node manyWords = this.getNodeForWord( word );
		if ( 0 != manyWords.numChildren() ) {
			// there may be more than 1 found
			if ( manyWords.numChildren() > 1 ) {
				// more than one words already found, which do we return?
			}
			
			// one word found:
			wordNode = (WordNode)manyWords.getFirstChild();// first it is then
			if ( !Environment.isWordNode( wordNode ) ) {
				throw new BugError(
						"getNodeForWord or isWordNode are inconsistent" );
			}
			return wordNode;
		}
		
		// else(word doesn't exist) add it now, raw mode
		// TODO maybe add it zip-mode, ie. as compact as possible maybe formed
		// of two words; since one word we didn't find(above)
		wordNode = new WordNode();
		
		char c;
		Node n;
		for ( int i = 0; i < word.length(); i++ ) {
			c = word.charAt( i );
			n = this.ensureNodeForChar( c );
			wordNode.dupAppendChild( n );
		}
		
		return wordNode;
	}
	
	
	/**
	 * We're looking for the WordNode(s) for the 'word' that's formed of
	 * CharNodes at the base<br>
	 * AllWordNodes->wn1->{a,wn2}<br>
	 * AllWordNodes->wn2->{c,t}<br>
	 * AllCharNodes->{a,c,t}<br>
	 * and we're looking for word 'act' then we return 'wn1'<br>
	 * Since there may be more word variants for 'act' we return a list of
	 * WordNode(s)<br>
	 * 
	 * @param word
	 * @return a list of WordNodes as solutions for the word
	 */
	public Node getNodeForWord( String word ) {

		
		Debug.nullException( word );
		if ( word.length() < 1 ) {
			throw new BadParameterException( "word must be at least 1 char" );
		}
		// list of all WordNodes that match this word
		Environment.lastSolutionsForLastGottenWord.clearAllChildren();
		Environment.wordToBeProcessed.dupClearAllChildren();
		Debug.assertTrue( Environment.wordToBeProcessed.numChildren() == 0 );
		Debug.assertTrue( Environment.lastSolutionsForLastGottenWord.numChildren() == 0 );
		// TODO split this in two: bool init(), bool nextStep()
		for ( int i = 0; i < word.length(); i++ ) {
			char c = word.charAt( i );
			Node n = this.getNodeForChar( c );
			if ( null == n ) {
				// one of the chars doesn't exist, hence the word doesn't exist
				return Environment.lastSolutionsForLastGottenWord;// empty list
			}
			Environment.wordToBeProcessed.dupAppendChild( n );
		}
		
		IntermediaryNode tmpNode = null;
		

		Environment.intermediaryNodeForNodeOnPos0.clearAllChildren();
		Environment.nodeThatHasToBeOnPos0.clearAllChildren();
		Debug.assertTrue( Environment.intermediaryNodeForNodeOnPos0.numChildren() == 0 );
		Debug.assertTrue( Environment.nodeThatHasToBeOnPos0.numChildren() == 0 );
		
		if ( Environment.nodeThatHasToBeOnPos0.appendChild( Environment.wordToBeProcessed.dupGetFirstChild() ) ) {
			throw new BugError( "couldn't've existed" );
		}
		// getNodeForChar above won't return null, because above we passed thru
		// a for all chars and it existed
		
		// next of first
		if ( Environment.wordToBeProcessed.numChildren() > 1 ) {
			Environment.expectedChar.pointTo( Environment.wordToBeProcessed.getIntermediaryAt( 1 )/* IN */);
		} else {
			Environment.expectedChar.setNull();
		}
		// int indexOfNextExpectedChar = 1;// 0 based though
		
		while ( true ) {
			// the while if, will help us handle 1 char words; nothing else
			// ie. the while will be broken only if our word is 1 char long,
			// hence index=0
			
			// attempts to find next WordNode for word[0], that's a
			// different parent
			// like parallel on the Z axis; same child CharNode
			
			tmpNode = this.getNextIntermediaryNodeForNodeAt(
					Environment.nodeThatHasToBeOnPos0.getLastChild(), 0,
					tmpNode );
			if ( null == tmpNode ) {
				// none found, hence there's no (more)word(s) having word[0] at
				// index 0
				tmpNode = (IntermediaryNode)Environment.intermediaryNodeForNodeOnPos0.getLastChild();
				if ( null == tmpNode ) {
					break;
				}
				if ( !Environment.intermediaryNodeForNodeOnPos0.removeChild( tmpNode ) ) {
					throw new BugError(
							"should've been true aka removed existing Node" );
				}
				
				if ( !Environment.nodeThatHasToBeOnPos0.removeChild( Environment.nodeThatHasToBeOnPos0.getLastChild() ) ) {
					throw new BugError();
				}
				
				// next of first
				Environment.expectedChar.pointTo( Environment.wordToBeProcessed.getIntermediaryAt( 1 )/* IN */);
				continue;
			}
			
			// not null
			NodeWithDupChildren wordNode = tmpNode.getFather();
			if ( null == wordNode ) {
				throw new BugError( "intermediary node w/o father?!" );
			}
			
			// wordNode already has intermediaryNodeForNodeOnPos0 on pos 0, thus
			// we try finding next chars of word from pos 1 in wordNode
			// continuing from pos 1
			Node backup = Environment.expectedChar.getPointee();
			// if ( null == backup ) {
			// throw new BugError();
			// }
			if ( null == tmpNode ) {
				throw new BugError( "this will never be null" );
			}
			this.digDownRight( wordNode, tmpNode, 0 );
			
			if ( Environment.scanStatus.getPointee() == Environment.badChar ) {
				// ie. -1 from encountering bad char
				// bad wordNode, need to get next wordNode
				Environment.expectedChar.integrityCheck();
				if ( null != backup ) {
					Environment.expectedChar.pointTo( backup );
				}
				continue;
			} else {
				if ( Environment.scanStatus.getPointee() != Environment.completedWord ) {
					// there's still some char(s) left
					// we would ideally go UP, and keep
					// indexOfNextExpectedChar where it is
					// but if u can't go up anymore, u come down and continue
					// horizontally from where u left off
					if ( Environment.nodeThatHasToBeOnPos0.appendChild( wordNode ) ) {
						throw new BugError( "shouldn't already exist" );
					}
					// index remains
					// start from beginning
					Environment.intermediaryNodeForNodeOnPos0.appendChild( tmpNode );
					tmpNode = null;
					continue;
				} else {
					if ( Environment.scanStatus.getPointee() == Environment.completedWord ) {
						// we found wordNode to be one solution
						// and we should go next wordNode
						Environment.lastSolutionsForLastGottenWord.appendChild( wordNode );
						// next of first
						if ( Environment.wordToBeProcessed.numChildren() > 1 ) {
							Environment.expectedChar.pointTo( Environment.wordToBeProcessed.getIntermediaryAt( 1 )/* IN */);
						} else {
							Environment.expectedChar.setNull();
						}
						continue;// this will go to while and attempt to go next
						// wordNode
					} else {
						// over length
						throw new BugError(
								"Environment.scanStatus has unexpected/unhandled pointee" );
					}// else
				}// else
			}// else
		}// while
		
		return Environment.lastSolutionsForLastGottenWord;// can be empty
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
	private void digDownRight( NodeWithDupChildren wordNode,
			IntermediaryNode lastINFound, int level ) {

		Debug.nullException( wordNode, level );
		Debug.assertTrue( Environment.isWordNode( wordNode ) );
		IntermediaryNode in = lastINFound;// can be null
		
		Environment.scanStatus.setNull();
		
		// Environment.expectedChar = new DomainPointerNode( expectedString );
		// DomainPointerNode expectedChar = new DomainPointerNode(
		// expectedString );
		// Environment.expectedChar.pointTo(
		// Environment.wordToBeProcessed.getChildAt( indexOfExpectedChar )/* IN
		// */);
		
		Node inList = new Node();
		Node currentNode0 = new Node();
		NodeWithDupChildren curr0 = wordNode;
		if ( currentNode0.appendChild( curr0 ) ) {
			throw new BugError();
		}
		

		while ( true ) {
			


			// this is like parallel on the X axis; same wordNode parent
			if ( null == in ) {
				// this should only happen once, when lastINFound is null
				in = curr0.getIntermediaryForFirstChild();
			} else {
				in = curr0.getNextIntermediary( in );
			}
			
			if ( Environment.expectedChar.getPointee() == null ) {
				Environment.scanStatus.pointTo( Environment.completedWord );
				// if ( indexOfExpectedChar == expectedString.numChildren() ) {
				// we completed the word, then we have to make sure
				// there's nothing else next
				if ( null != in ) {
					// yes there's more
					// then act like a bad char
					// indexOfExpectedChar = -1;
					Environment.scanStatus.pointTo( Environment.badChar );
					break;
				}
			}
			
			if ( null == in ) {
				// no more intermediaries? then break/exit method
				// and if word[0] has this wordNode then you should go upward,
				// on a parent of this wordNode that has this wordNode on
				// position 0 and check that parent's children for next expected
				// chars or wordNodes that contain the expected chars
				// break;
				level--;
				// use the last, then remove it
				in = (IntermediaryNode)inList.getLastChild();
				if ( null == in ) {
					break;
				}
				if ( !inList.removeChild( in ) ) {
					throw new BugError();
				}
				// remove first, then use the one that was before the removed
				// one
				if ( !currentNode0.removeChild( curr0 ) ) {
					throw new BugError();
				}
				curr0 = (NodeWithDupChildren)currentNode0.getLastChild();
				if ( null == curr0 ) {
					throw new BugError();
				}
				// if ( !Environment.isWordNode( curr0 ) ) {
				// throw new BugError();
				// }
				
				continue;
			} else {
				// found one, whose child may be Word or Char node
				Node wordOrChar = in.getPointee();
				if ( Environment.isCharNode( wordOrChar ) ) {
					// is CharNode then we check if it's the expected char
					if ( wordOrChar == ( (IntermediaryNode)Environment.expectedChar.getPointee() ).getPointee() ) {
						// if ( wordOrChar == expectedString.dupGetChildAt(
						// indexOfExpectedChar ) ) {
						// good, now expect next char
						// indexOfExpectedChar++;
						if ( !Environment.expectedChar.pointToNext() ) {
							Environment.expectedChar.setNull();
						}
						continue;
					} else {
						// bad char, the char we found was different than the
						// expected char
						// therefore we must choose another wordNode,
						// ie. next on Z axis
						// indexOfExpectedChar = -1;
						Environment.scanStatus.pointTo( Environment.badChar );
						break;
					}
				} else {
					// it's not Char
					if ( Environment.isWordNode( wordOrChar ) ) {
						if ( level >= Environment.DEFAULT_UPLEVEL ) {
							throw new RecursionTooDeepError( "too deep" );
						}
						
						level++;
						// expectedChar.dupAppendChild(wordOrChar);
						if ( inList.appendChild( in ) ) {
							throw new BugError( "couldn't've existed before" );
						}
						in = null;
						
						curr0 = (NodeWithDupChildren)wordOrChar;
						if ( currentNode0.appendChild( curr0 ) ) {
							throw new BugError(
									"maybe needs to make it NodeWithDupChildren" );
							// TODO check if this is needed, ever
						}
						
						continue;
					} else {
						// not char not word?!
						throw new BugError(
								"supposed to be CharNode OR WordNode not something else" );
					}// else
				}// else
			}// else
			


		}// while
		
	}
	
	/**
	 * we looking for a parent NodeWithDupChildren that has AllWordNodes as
	 * parent AND also has forNode as child at the specified indexPos;<br>
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
	 *         WordNode<br>
	 *         notice: returns only INs of WordNode not of others that are not
	 *         WordNode ie. if they don't have AllWordNodes as parent they're
	 *         ignored
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
			if ( null == wordNode ) {
				throw new InconsistentLinkException(
						"someone removed the IN from the NodeWithDupChildren" );
			}
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
	
	

	// ========================================================================
	
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
	 * used in constructor<br>
	 * makes parentNode -> childNode
	 * 
	 * @param childNode
	 * @param parentNode
	 */
	public static void internalEnsureNodeIsChildOf( Node childNode,
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
	
	public static boolean isWordDelimiter( Node whatNode ) {

		Debug.nullException( whatNode );
		whatNode.integrityCheck();
		return whatNode.hasParent( AllWordDelimiterNodes );
	}
	
	// --------------------------------------- PhraseNode
	
	/**
	 * @param phrase
	 *            a string containing many words and punctuation/delimiters
	 * @return the PhraseNode for the added phrase
	 */
	public PhraseNode addPhrase( String phrase ) {

		Debug.nullException( phrase );
		NodeWithDupChildren dupListOfWordsAndDelims = new NodeWithDupChildren();
		// split it into words and delimiters
		String word = new String();
		for ( int i = 0; i < phrase.length(); i++ ) {
			char c = phrase.charAt( i );
			
			boolean isDelim = this.isWordDelimiter( c );
			if ( !isDelim ) {
				if ( this.isWordAllowedChar( c ) ) {
					// continue building the word
					word += c;
				} else {
					throw new BugError(
							"not word and not delimiter char, unexpected char" );
				}
			}
			if ( ( isDelim ) || ( i == phrase.length() - 1 ) ) {
				WordNode n = this.addWord( word );
				System.out.println( "|" + word + "|" );
				dupListOfWordsAndDelims.dupAppendChild( n );
				if ( isDelim ) {
					// WordDelimiterNode wdn = this.addWordDelimiter( c );
					// TODO a delimiter may be more than 1 char! like '...'
					dupListOfWordsAndDelims.dupAppendChild( this.ensureNodeForDelimiter( c ) );
					System.out.println( "!" + c + "!" );
				}
				word = "";
			}
		}
		
		// TODO check if phrase exists already and return it
		// if not, raw add it:
		PhraseNode pn = new PhraseNode();
		IntermediaryNode parser = dupListOfWordsAndDelims.getIntermediaryForFirstChild();
		if ( null == parser ) {
			throw new BugError( "somehow" );
		}
		while ( null != parser ) {
			pn.dupAppendChild( parser.getPointee() );
			parser = dupListOfWordsAndDelims.getNextIntermediary( parser );
		}
		return pn;
	}
	
	/**
	 * @param delim
	 * @return
	 */
	public CharNode ensureNodeForDelimiter( char delim ) {

		Debug.nullException( delim );
		if ( !this.isWordDelimiter( delim ) ) {
			throw new BadParameterException();
		}
		
		CharNode n = this.getNodeForDelimiter( delim );
		if ( null == n ) {
			n = this.ensureNodeForChar( delim );// make new
			Environment.internalEnsureNodeIsChildOf( n,
					Environment.AllWordDelimiterNodes );
		}
		if ( null == n ) {
			throw new BugError( "'ensureNodeForChar' doesn't work as designed" );
		}
		return n;
	}
	
	/**
	 * @param delim
	 * @return
	 */
	public CharNode getNodeForDelimiter( char delim ) {

		Debug.nullException( delim );
		if ( !this.isWordDelimiter( delim ) ) {
			throw new BadParameterException();
		}
		CharNode cn = this.getNodeForChar( delim );
		if ( null != cn ) {
			if ( !Environment.isWordDelimiter( cn ) ) {
				throw new BugError();
			}
		}
		return cn;
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
		case '.':
		case ',':
		case '"':
		case '\'':
		case '@':
		case '!':
			return true;
		default:
			return false;
		}
	}
}
