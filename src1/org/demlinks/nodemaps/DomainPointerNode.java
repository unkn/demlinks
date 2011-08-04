package org.demlinks.nodemaps;



import org.demlinks.exceptions.*;
import org.demlinks.node.*;
import org.q.*;



public class DomainPointerNode extends PointerNode {
	
	// can point to children only from this domain
	private final Node	domain;
	
	
	public DomainPointerNode( final Node domain1 ) {
		
		super();
		assert null != domain1;
		Environment.internalEnsureNodeIsChildOf( this, Environment.AllDomainPointerNodes );
		domain = domain1;
	}
	
	
	@Override
	public void integrityCheck() {
		
		super.integrityCheck();
		// if ( this.numChildren() > 1 ) {
		// throw new BugError(
		// "someone made the pointer have more than 1 child" );
		// }
		if ( !hasParent( Environment.AllDomainPointerNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
		final Node p = getPointee();
		if ( null != p ) {
			if ( !domain.hasChild( p ) ) {
				throw new BugError( "pointee is not from domain" );
			}
		}
	}
	
	
	@Override
	public boolean pointTo( final Node pointee ) {
		
		assert null != pointee;
		if ( !domain.hasChild( pointee ) ) {
			throw new BadParameterException( "parameter is not from domain" );
		}
		return super.pointTo( pointee );
	}
	
	
	/**
	 * @return true if successfully moved to point to next Node from the domain;<br>
	 *         false if wasn't already pointing to anything OR there's nothing
	 *         else more to point to, in the next direction;
	 */
	public boolean pointToNext() {
		
		if ( !canPointToNext() ) {
			return false;
		}
		
		pointTo( domain.getChildNextOf( getPointee() ) );
		return true;
	}
	
	
	/**
	 * @return true if there's a next that we can point to using {@link #pointToNext()}
	 */
	public boolean canPointToNext() {
		
		final Node current = getPointee();
		if ( null != current ) {
			final Node next = domain.getChildNextOf( current );
			if ( null != next ) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @return
	 * @see #pointToNext()
	 */
	public boolean pointToPrev() {
		
		final Node current = getPointee();
		if ( null != current ) {
			final Node prev = domain.getChildPrevOf( current );
			if ( null != prev ) {
				if ( !pointTo( prev ) ) {
					throw new BugError( "should be true since a pointee existed before" );
				}
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @return the domain this pointer is defined upon
	 */
	public Node getDomain() {
		
		return domain;
	}
	// TODO junit test for this class
}
