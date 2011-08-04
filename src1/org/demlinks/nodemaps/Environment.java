/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import org.demlinks.errors.*;
import org.demlinks.exceptions.*;
import org.demlinks.node.*;
import org.q.*;



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
	public static DomainPointerNode					expectedChar					= new DomainPointerNode( wordToBeProcessed );
	
	// -----------
	
	// it's resizable, the initial allocation for array for in depth parsing
	public static final int							DEFAULT_UPLEVEL					= 100;
	
	
	private final TwoWayHashMap<String, CharNode>	mapCharsToNodes;
	
	
	public Environment() {
		
		mapCharsToNodes = new TwoWayHashMap<String, CharNode>();
	}
	
	
	// ---------------------------------- CharNode
	
	@SuppressWarnings( "unused" )
	private void mapAllChars() {
		
		
		// int count = 0;
		for ( char c = 0; c <= 255; c++ ) {// [0..255] inclusive
			// count++;
			mapNewChar( c );
			
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
	public CharNode mapNewChar( final char c ) {
		
		// assert null != c );
		
		if ( isMappedChar( c ) ) {
			throw new BadParameterException( "mapping already exists." );
		}
		
		final CharNode node = new CharNode();
		final String s = String.valueOf( c );
		if ( !mapCharsToNodes.putKeyValue( s, node ) ) {
			// if false, then already existed
			Q.bug( "'c' was already associated: " + s + ":" + mapCharsToNodes.getValue( s ) );
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
	public boolean isMappedChar( final char c ) {
		
		final Node n = getNodeForChar( c );
		// this.mapCharsToNodes.getValue( String.valueOf( c ) );
		return ( null != n );
	}
	
	
	/**
	 * @param c
	 * @return null or the node that was mapped to char c
	 */
	public CharNode getNodeForChar( final char c ) {
		
		final CharNode n = mapCharsToNodes.getValue( String.valueOf( c ) );
		return n;
	}
	
	
	/**
	 * If there's no such Node, it will be created and mapped for char c
	 * 
	 * @param c
	 * @return the Node that's mapped to char c; never null
	 */
	public CharNode ensureNodeForChar( final char c ) {
		
		// Node n = this.mapCharsToNodes.getValue( String.valueOf( c ) );
		CharNode n = getNodeForChar( c );
		if ( null == n ) {
			n = mapNewChar( c );// make new
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
	public boolean isLetter( final char chr ) {
		
		if ( ( ( chr >= 'a' ) && ( chr <= 'z' ) ) || ( ( chr >= 'A' ) && ( chr <= 'Z' ) ) ) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param chr
	 * @return
	 */
	public boolean isDigit( final char chr ) {
		
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
	public WordNode addWord( final String word ) {
		
		assert null != word;
		if ( !isGoodWord( word ) ) {
			throw new BadParameterException();
		}
		
		WordNode wordNode;
		// if already exists, then return that one
		final Node manyWords = getNodeForWord( word );
		if ( 0 != manyWords.numChildren() ) {
			// there may be more than 1 found
			if ( manyWords.numChildren() > 1 ) {
				// more than one words already found, which do we return?
			}
			
			// one word found:
			wordNode = (WordNode)manyWords.getFirstChild();// first it is then
			if ( !Environment.isWordNode( wordNode ) ) {
				throw new BugError( "getNodeForWord or isWordNode are inconsistent" );
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
			n = ensureNodeForChar( c );
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
	public Node getNodeForWord( final String word ) {
		
		
		assert null != word;
		if ( word.length() < 1 ) {
			throw new BadParameterException( "word must be at least 1 char" );
		}
		// list of all WordNodes that match this word
		Environment.lastSolutionsForLastGottenWord.clearAllChildren();
		Environment.wordToBeProcessed.dupClearAllChildren();
		assert Environment.wordToBeProcessed.numChildren() == 0;
		assert Environment.lastSolutionsForLastGottenWord.numChildren() == 0;
		// TODO split this in two: bool init(), bool nextStep()
		for ( int i = 0; i < word.length(); i++ ) {
			final char c = word.charAt( i );
			final Node n = getNodeForChar( c );
			if ( null == n ) {
				// one of the chars doesn't exist, hence the word doesn't exist
				return Environment.lastSolutionsForLastGottenWord;// empty list
			}
			Environment.wordToBeProcessed.dupAppendChild( n );
		}
		
		IntermediaryNode tmpNode = null;
		
		
		Environment.intermediaryNodeForNodeOnPos0.clearAllChildren();
		Environment.nodeThatHasToBeOnPos0.clearAllChildren();
		assert Environment.intermediaryNodeForNodeOnPos0.numChildren() == 0;
		assert Environment.nodeThatHasToBeOnPos0.numChildren() == 0;
		
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
			
			tmpNode = getNextIntermediaryNodeForNodeAt( Environment.nodeThatHasToBeOnPos0.getLastChild(), 0, tmpNode );
			if ( null == tmpNode ) {
				// none found, hence there's no (more)word(s) having word[0] at
				// index 0
				tmpNode = (IntermediaryNode)Environment.intermediaryNodeForNodeOnPos0.getLastChild();
				if ( null == tmpNode ) {
					break;
				}
				if ( !Environment.intermediaryNodeForNodeOnPos0.removeChild( tmpNode ) ) {
					throw new BugError( "should've been true aka removed existing Node" );
				}
				
				if ( !Environment.nodeThatHasToBeOnPos0.removeChild( Environment.nodeThatHasToBeOnPos0.getLastChild() ) ) {
					Q.bug();
				}
				
				// next of first
				Environment.expectedChar.pointTo( Environment.wordToBeProcessed.getIntermediaryAt( 1 )/* IN */);
				continue;
			}
			
			// not null
			final NodeWithDupChildren wordNode = tmpNode.getFather();
			if ( null == wordNode ) {
				throw new BugError( "intermediary node w/o father?!" );
			}
			
			// wordNode already has intermediaryNodeForNodeOnPos0 on pos 0, thus
			// we try finding next chars of word from pos 1 in wordNode
			// continuing from pos 1
			final Node backup = Environment.expectedChar.getPointee();
			// if ( null == backup ) {
			// throw new BugError();
			// }
			// if ( null == tmpNode ) {
			// throw new BugError( "this will never be null" );
			// }
			digDownRightForWord( wordNode, tmpNode, 0 );
			
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
						throw new BugError( "Environment.scanStatus has unexpected/unhandled pointee" );
					}// else
				}// else
			}// else
		}// while
		
		return Environment.lastSolutionsForLastGottenWord;// can be empty
	}
	
	
	/**
	 * non-recursive method
	 * 
	 * @param wordNode
	 *            continuing from wordNode->lastINFound
	 * @param lastINFound
	 *            IntermediaryNode<br>
	 *            we're skipping this, and the next IntermediaryNode found will
	 *            be either CharNode or WordNode<br>
	 *            if CharNode then it must be equal to expectedString[charIndex]
	 *            and if so charIndex++ and go on with parsing; if CharNode is
	 *            different char than expected then abort with return -1 and you
	 *            should choose another wordNode<br>
	 *            else if WordNode, digDeep() until u find a CharNode then apply
	 *            what we said above for CharNode<br>
	 */
	private void digDownRightForWord( final NodeWithDupChildren wordNode, final IntermediaryNode lastINFound, int level ) {
		
		assert null != wordNode;
		assert Environment.isWordNode( wordNode );
		IntermediaryNode in = lastINFound;// can be null
		
		Environment.scanStatus.setNull();
		
		// Environment.expectedChar = new DomainPointerNode( expectedString );
		// DomainPointerNode expectedChar = new DomainPointerNode(
		// expectedString );
		// Environment.expectedChar.pointTo(
		// Environment.wordToBeProcessed.getChildAt( indexOfExpectedChar )/* IN
		// */);
		
		final Node inList = new Node();
		final Node currentNode0 = new Node();
		NodeWithDupChildren curr0 = wordNode;
		if ( currentNode0.appendChild( curr0 ) ) {
			Q.bug();
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
					Q.bug();
				}
				// remove first, then use the one that was before the removed
				// one
				if ( !currentNode0.removeChild( curr0 ) ) {
					Q.bug();
				}
				curr0 = (NodeWithDupChildren)currentNode0.getLastChild();
				if ( null == curr0 ) {
					Q.bug();
				}
				// if ( !Environment.isWordNode( curr0 ) ) {
				// throw new BugError();
				// }
				
				continue;
			} else {
				// found one, whose child may be Word or Char node
				final Node wordOrChar = in.getPointee();
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
							throw new BugError( "maybe needs to make it NodeWithDupChildren" );
							// TODO check if this is needed, ever
						}
						
						continue;
					} else {
						// not char not word?!
						throw new BugError( "supposed to be CharNode OR WordNode not something else" );
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
	protected IntermediaryNode getNextIntermediaryNodeForNodeAt( final Node forNode, final int indexPos,
																	final IntermediaryNode previouslyFoundIN ) {
		
		assert null != forNode;
		Node parser = null;
		if ( null != previouslyFoundIN ) {
			parser = previouslyFoundIN;
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BadParameterException( "wrong parameter" );
			}
			if ( ( (IntermediaryNode)parser ).getPointee() != forNode ) {
				throw new BadParameterException( "chrNode is not a child of previouslyFound" );
			}
			
			// get next parent of chrNode, that's intermediary node and that's
			// next of previouslyFound
			
			// so could be null
		}
		
		
		// parser can be null here before assignment
		parser = forNode.getNextParent( Environment.AllIntermediaryNodes, parser );
		// we're here having the next potential intermediary node, under
		// 'parser'
		while ( null != parser ) {
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BugError( "we're supposed to only get \"parser\"'s" + " that are intermediary nodes" );
			}
			// we need to check if the intermediary is child of a WordNode
			// since there can be only 1 father for an IntermediaryNode
			final NodeWithDupChildren wordNode = ( (IntermediaryNode)parser ).getFather();
			if ( null == wordNode ) {
				throw new InconsistentLinkException( "someone removed the IN from the NodeWithDupChildren" );
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
			parser = forNode.getNextParent( Environment.AllIntermediaryNodes, parser );
		}
		
		return null;
		
	}
	
	
	/**
	 * @param word
	 * @return
	 */
	public boolean isGoodWord( final String word ) {
		
		assert null != word;
		final int len = word.length();
		if ( len <= 0 ) {
			throw new BadParameterException();
		}
		
		for ( int i = 0; i < len; i++ ) {
			final char p = word.charAt( i );
			if ( !isWordAllowedChar( p ) ) {
				return false;// bad word!
			}
		}
		return true;
	}
	
	
	/**
	 * @param chr
	 * @return
	 */
	public boolean isWordAllowedChar( final char chr ) {
		
		if ( ( isLetter( chr ) ) || ( isInWordSpecialChars( chr ) ) || ( isDigit( chr ) ) ) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param c
	 * @return
	 */
	public boolean isInWordSpecialChars( final char c ) {
		
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
	public static boolean isPointer( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllPointerNodes );
	}
	
	
	public static boolean isPhrase( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllPhraseNodes );
	}
	
	
	/**
	 * AllNodeWithDupChildrenNodes -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isNodeWithDupChildren( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllNodeWithDupChildrenNodes );
	}
	
	
	/**
	 * AllRandomNodes -> whatNode ?
	 * 
	 * @param whatNode
	 * @return
	 */
	public static boolean isRandomNode( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllRandomNodes );
	}
	
	
	/**
	 * @param whatNode
	 * @return
	 */
	public static boolean isIntermediaryNode( final Node whatNode ) {
		
		assert null != whatNode;
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
	protected static void internalEnsureNodeIsChildOf( final Node childNode, final Node parentNode ) {
		
		assert null != childNode;
		assert null != parentNode;
		boolean existsAlready = false;
		try {
			existsAlready = parentNode.appendChild( childNode );
		} catch ( final InconsistentLinkException e ) {// half of the link exists
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
	public static boolean isWordNode( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllWordNodes );
	}
	
	
	public static boolean isCharNode( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllCharNodes );
	}
	
	
	public static boolean isWordDelimiter( final Node whatNode ) {
		
		assert null != whatNode;
		whatNode.integrityCheck();
		return whatNode.hasParent( AllWordDelimiterNodes );
	}
	
	
	// --------------------------------------- PhraseNode
	
	/**
	 * @param phrase
	 *            a string containing many words and punctuation/delimiters
	 * @return the PhraseNode for the added phrase
	 */
	public PhraseNode addPhrase( final String phrase ) {
		
		assert null != phrase;
		assert !phrase.isEmpty();
		
		
		final Node manyPhrases = getNodeForPhrase( phrase );
		if ( manyPhrases.numChildren() != 0 ) {
			final PhraseNode phraseNode = (PhraseNode)manyPhrases.getFirstChild();
			if ( !isPhrase( phraseNode ) ) {
				Q.bug();
			}
			return phraseNode;
		}
		// else, doesn't exist already
		
		final NodeWithDupChildren dupListOfWordsAndDelims = new NodeWithDupChildren();
		// split it into words and delimiters
		String word = new String();
		final int len = phrase.length();
		for ( int i = 0; i < len; i++ ) {
			final char c = phrase.charAt( i );
			
			final boolean isDelim = isDelimiter( c );
			if ( !isDelim ) {
				if ( isWordAllowedChar( c ) ) {
					// continue building the word
					word += c;
				} else {
					Q.bug( "not word char and not delimiter char => unexpected char" );
				}
			}// not else!
			
			if ( ( isDelim ) || ( i == ( phrase.length() - 1 ) ) ) {
				if ( !word.isEmpty() ) {
					// System.out.println( "|" + word + "|" );
					dupListOfWordsAndDelims.dupAppendChild( addWord( word ) );
					word = "";
				}
				if ( isDelim ) {
					// TODO a delimiter may be more than 1 char! like '...'
					dupListOfWordsAndDelims.dupAppendChild( ensureNodeForDelimiter( c ) );
					// System.out.println( "!" + c + "!" );
				}
				
			}
		}
		
		// raw add it:
		final PhraseNode pn = new PhraseNode();
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
	
	
	public Node getNodeForPhrase( final String phrase ) {
		
		// TODO Auto-generated method stub
		final Node p = new Node();
		return p;
	}
	
	
	/**
	 * @param delim
	 * @return
	 */
	public CharNode ensureNodeForDelimiter( final char delim ) {
		
		// assert null != delim );
		if ( !isDelimiter( delim ) ) {
			throw new BadParameterException();
		}
		
		CharNode n = getNodeForDelimiter( delim );
		if ( null == n ) {
			n = ensureNodeForChar( delim );// make new
			Environment.internalEnsureNodeIsChildOf( n, Environment.AllWordDelimiterNodes );
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
	public CharNode getNodeForDelimiter( final char delim ) {
		
		// assert null != delim );
		if ( !isDelimiter( delim ) ) {
			throw new BadParameterException();
		}
		final CharNode cn = getNodeForChar( delim );
		if ( null != cn ) {
			if ( !Environment.isWordDelimiter( cn ) ) {
				Q.bug();
			}
		}
		return cn;
	}
	
	
	/**
	 * isWordDelimiter
	 * 
	 * @param c
	 *            char
	 * @return true if the char is used between two words
	 */
	public boolean isDelimiter( final char c ) {
		
		if ( isWordAllowedChar( c ) ) {
			return false;
		}
		
		switch ( c ) {
		case ' ':
		case '/':
		case '+':
		case '|':
		case '.':
		case ',':
		case '"':
			// case '\'':
		case '@':
		case '!':
			return true;
		default:
			return false;
		}
	}
}
