

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;
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
		WordNode wn = getWord( word );
		if ( null != wn ) {
			return wn;// aready existed
		}
		// else add it now
		wn = new WordNode();
		// TODO Auto-generated method stub
		boolean mostLikelyWordDoesntExist = false;
		char c;
		Node n;
		for ( int i = 0; i < word.length(); i++ ) {
			c = word.charAt( i );
			if ( !this.isMappedChar( c ) ) {
				mostLikelyWordDoesntExist = true;
			}
			n = this.ensureNodeForChar( c );
			// TODO attempt to find a word that has all collected 'n' nodes in
			// that order
			// if exists, then this word we try to add, exists, maybe return IT
			// instead of boolean
		}
		
		WordNode wordNode = null;
		
		if ( !mostLikelyWordDoesntExist ) {
			// attempt to find it; it may not exist still;
			// TODO
		}
		
		if ( wordNode == null ) {
			wordNode = new WordNode();
			// TODO and add all children 'n' nodes collected before
		}
		
		return wordNode;
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
