

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.ListOfNodeLists;
import org.demlinks.node.Node;
import org.demlinks.node.NodeList;
import org.demlinks.node.Position;
import org.demlinks.nodemaps.Environment;
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
		


		// Node parser;
		int childAtIndex = 0;
		int upIndex = 0;
		// int[] indexes = new int[word.length()];
		
		// an array of X elements for each child in 'nl'
		// each element is a vertical-list of <horizontal-list of nodes>
		ListOfNodeLists[] upList = new ListOfNodeLists[word.length()];
		

		boolean moreSolutions = true;
		// -------------------------------------------
		while ( moreSolutions ) {
			
			Node thisValidParent[childAtIndex] = getNextValidParent(thisValidParent[childAtIndex]);
			if ( null != thisValidParent[childAtIndex] ) {
				if ( nl.getNodeAt( childAtIndex ) == thisValidParent.getFirstChild() ) {
					//found this parent to be right for that char
					childAtIndex++;//expect next char
					
					getNextChildForThisParent();
					if (found any) {
						switch() {
						case isWordNode:
						case isCharNode:
							
						default:
								throw new Exception();
						}
					}
					if (canGoNextChar()) {
						goNextChar();
					}
				} else {
					//no good, go next parent, horizontally
					
				}
				
			}
		}
		// -------------------------------------------
		while ( moreSolutions ) {
			Node foundValidParentJustNow = null;
			
			// get next valid parent for current childAtIndex
			if ( null == upList[childAtIndex] ) {
				upList[childAtIndex] = new ListOfNodeLists();// 2Dlist
				// first list in the vertical list is this new horizontal list
				upList[childAtIndex].addFirst( new NodeList() );
				// the horizontal list:
				NodeList tmpNL = upList[childAtIndex].getObjectAt( Position.FIRST );
				// we find first valid parent for word[0] char, and add it to
				// horizontal list
				foundValidParentJustNow = nl.getNodeAt( childAtIndex ).getNextParent(
						Environment.AllWordNodes, null );
				if ( foundValidParentJustNow != null ) {
					// the list doesn't allow nulls
					tmpNL.addFirst( foundValidParentJustNow );
				}
			} else {
				// already having a parent, which we tried, and now we could go
				// upward
				// foundValidParentJustNow;
			}
			

			if ( foundValidParentJustNow != null ) {
				// yes there was one more valid parent
				
				// if this parent is the same as the wannabe solution found
				// parent
				// recheckIF:
				if ( foundValidParentJustNow == foundParent ) {
					// attempt to go next char in the word, unless already was
					// at last, and if so we found 1 solution
					// if (weCanGoNext()) {
					if ( childAtIndex < ( word.length() - 1 ) ) {
						// goNext();
						// if we were not at the last childAtIndex then go next
						childAtIndex++;
						// must be starting from the beginning for next one
						if ( upList[childAtIndex] != null ) {
							while ( !upList[childAtIndex].isEmpty() ) {
								NodeList inl = upList[childAtIndex].getObjectAt( Position.FIRST );
								inl.removeAll();
								upList[childAtIndex].removeObject( inl );
							}
							upList[childAtIndex] = null;
							// and the beginning of while will reinit it;
						}
						continue;// the while, i hope
					} else {
						// it was last, then we found 1 solution
						// show foundValidParentJustNow, that's the common
						// parent all those chars have
						System.out.println( foundValidParentJustNow );
						childAtIndex = 0;
						// do not clear the list @ childAtIndex
						// should we go up or next?
						continue;
					}
				} else {
					// current parent is valid but different than what we found
					// before
					// then we attempt to get a valid parent of this parent
					// another backtracking here for , foundParent and
					// currentParent
					// back();
					if ( weCanGoUp() ) {
						goUp();
						recheckIF();
					} else {
						if ( weCanGoNext() ) {
							weGoNext();
						} else {
							if ( weCanGoBack() ) {
								weGoBack();// horizontally
							} else {
								moreSolutions = false;
								continue;
							}
						}
					}
					goUp();
					getValidParent();
					recheck_IF();
				}
			} else {
				// no more valid parents here
				if ( index > 0 ) {
					index--;// backtrack
					// when u going back, u'd want to go to parent on top of
					// current parent, unless there's none, or until there's
					// none, and THEN go to next parent horizontally
				} else {
					// no more solutions, since we were at first childAtIndex
					// and it had no more validParents
					moreSolutions = false;
				}
			}
		}
		
		// parser = nl.getNodeAt( index );
		// indexes[index] = 0;
		
		// parser.getParentAt( indexes[index] );
		while ( childAtIndex < word.length() ) {
			lastValidParentOf[childAtIndex] = nl.getNodeAt( childAtIndex ).getFirstParent();
			
			while ( lastValidParentOf[childAtIndex] != null ) {
				if ( isValidParent( lastValidParentOf[childAtIndex] ) ) {
					break;
				}
				lastValidParentOf[childAtIndex] = lastValidParentOf[childAtIndex].getParentNextOf( lastValidParentOf[childAtIndex] );
			}
			// no more parents
			if ( lastValidParentOf[childAtIndex] != null ) {
				// valid parent here
				if ( continuation( lastValidParentOf[childAtIndex] ) ) {
					childAtIndex++;
					
				}
			} else {
				// no more parents
				if ( childAtIndex <= 0 ) {
					// no solutions?
				}
			}
			
		}// while
		
		while ( null != parser ) {
			
			childAtIndex++;
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
