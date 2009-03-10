

package org.demlinks.errors;

public class RecursionTooDeepError extends AssertionError {
	
	// no need to spec "throws"
	
	public RecursionTooDeepError() {

		super();
	}
	
	public RecursionTooDeepError( String userMsg ) {

		super( userMsg );
	}
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6039267831384391833L;
	

}
