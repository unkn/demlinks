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
						// TODO a delimiter may be more than 1 char ?!
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
