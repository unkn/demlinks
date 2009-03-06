

package org.demlinks.errors;

public class BadCallError extends Error {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1975911903344951011L;
	
	public BadCallError() {

		super();
	}
	
	public BadCallError( String userMsg ) {

		super( userMsg );
	}
}
