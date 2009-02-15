
package org.demlinks.errors;

public class BugError extends AssertionError { // no need to spec "throws"
												// anymore

	public BugError( String userMsg ) {
		super( userMsg );
	}
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6948765962049619515L;
}
