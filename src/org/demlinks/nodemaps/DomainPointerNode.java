

package org.demlinks.nodemaps;



import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;
import org.demlinks.exceptions.BadParameterException;
import org.demlinks.node.Node;



public class DomainPointerNode extends PointerNode {
	
	// can point to children only from this domain
	Node	domain;
	
	public DomainPointerNode( Node domain1 ) {

		super();
		Debug.nullException( domain1 );
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllDomainPointerNodes );
		this.domain = domain1;
	}
	
	@Override
	public void integrityCheck() {

		super.integrityCheck();
		// if ( this.numChildren() > 1 ) {
		// throw new BugError(
		// "someone made the pointer have more than 1 child" );
		// }
		if ( !this.hasParent( Environment.AllDomainPointerNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
		Node p = this.getPointee();
		if ( null != p ) {
			if ( !this.domain.hasChild( p ) ) {
				throw new BugError( "pointee is not from domain" );
			}
		}
	}
	
	@Override
	public boolean pointTo( Node pointee ) {

		Debug.nullException( pointee );
		if ( !this.domain.hasChild( pointee ) ) {
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

		if ( !this.canPointToNext() ) {
			return false;
		}
		
		return this.pointTo( this.domain.getChildNextOf( this.getPointee() ) );
	}
	
	/**
	 * @return true if there's a next that we can point to using
	 *         {@link #pointToNext()}
	 */
	public boolean canPointToNext() {

		Node current = this.getPointee();
		if ( null != current ) {
			Node next = this.domain.getChildNextOf( current );
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

		Node current = this.getPointee();
		if ( null != current ) {
			Node prev = this.domain.getChildPrevOf( current );
			if ( null != prev ) {
				if ( !this.pointTo( prev ) ) {
					throw new BugError(
							"should be true since a pointee existed before" );
				}
				return true;
			}
		}
		return false;
	}
}
