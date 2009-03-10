

package org.demlinks.environment;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.nodemaps.IntermediaryNode;
import org.demlinks.nodemaps.NodeWithDupChildren;
import org.demlinks.nodemaps.PhraseNode;
import org.demlinks.nodemaps.WordDelimiterNode;
import org.demlinks.nodemaps.WordNode;



public class PhraseMapping extends WordMapping {
	
	public PhraseMapping() {

		super();
	}
	
	/**
	 * @param phrase
	 *            a string containing many words and punctuation/delimiters
	 * @return the PhraseNode for the added phrase
	 */
	public PhraseNode addPhrase( String phrase ) {

		Debug.nullException( phrase );
		NodeWithDupChildren dupList = new NodeWithDupChildren();
		// split it into words and delimiters
		String word = new String();
		for ( int i = 0; i < phrase.length(); i++ ) {
			char c = phrase.charAt( i );
			
			boolean isDelim = this.isWordDelimiter( c );
			if ( !isDelim ) {
				word += c;
			}
			if ( ( isDelim ) || ( i == phrase.length() - 1 ) ) {
				try {
					WordNode n = this.addWord( word );
					dupList.dupAppendChild( n );
					if ( isDelim ) {
						WordDelimiterNode wdn = this.addWordDelimiter( c );
						dupList.dupAppendChild( wdn );
						// this.ensureNodeForChar( c )
						// );
					}
				} catch ( BadParameterException e ) {
					throw new BadParameterException( "seems the word was bad" );
				}
				word = "";
			}
		}
		
		// TODO check if phrase exists already and return it
		// if not, raw add it:
		PhraseNode pn = new PhraseNode();
		IntermediaryNode parser = dupList.getIntermediaryForFirstChild();
		if ( null == parser ) {
			throw new BugError( "somehow" );
		}
		while ( null != parser ) {
			pn.dupAppendChild( parser.getPointee() );
			parser = dupList.getNextIntermediary( parser );
		}
		return pn;
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
