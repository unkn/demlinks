
package org.demlinks.exceptions;

public class InconsistentLinkException extends Exception {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7483548742692804173L;
	
	public InconsistentLinkException( String userMsg ) {
		super( userMsg );
	}
}
