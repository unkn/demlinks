
package org.demlinks.exceptions;

/**
 * no need to declare it using "throws"
 * 
 */
public class InconsistentLinkException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7483548742692804173L;
	
	public InconsistentLinkException( String userMsg ) {
		super( userMsg );
	}
}
