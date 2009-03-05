
package org.demlinks.exceptions;


public class BadParameterException extends RuntimeException {
	
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2755078884185265850L;
	
	public BadParameterException() {
		super();
	}
	
	public BadParameterException( String userMsg ) {
		super( userMsg );
	}
}
