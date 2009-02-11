
package org.demlinks.exceptions;

public class InconsistentLinkDetected extends Exception {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7483548742692804173L;
	
	public InconsistentLinkDetected( String userMsg ) {
		super( userMsg );
	}
}
