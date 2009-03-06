

package org.demlinks.nodemaps;



import org.demlinks.errors.BadCallError;
import org.demlinks.errors.BugError;
import org.demlinks.node.Node;



public class ChildlessNode extends Node {
	
	@Override
	public void integrityCheck() {

		super.integrityCheck();
		if ( this.numChildren() != 0 ) {
			throw new BugError( "someone made the childless node have children" );
		}
	}
	
	@Override
	public boolean appendChild( Node child ) {

		throw new BadCallError( "you're not supposed to add children" );
	}
	
	@Override
	public Node getChildNextOf( Node ofWhatNode ) {

		throw new BadCallError();
	}
	
	@Override
	public Node getFirstChild() {

		throw new BadCallError();
	}
	
	@Override
	public Node getChildPrevOf( Node ofWhatNode ) {

		throw new BadCallError();
	}
	
	@Override
	public Node getLastChild() {

		throw new BadCallError();
	}
	
	@Override
	public boolean hasChild( Node child ) {

		throw new BadCallError();
	}
	
	@Override
	public boolean insertChildAfter( Node newChild, Node afterWhatChildNode ) {

		throw new BadCallError();
	}
	
	@Override
	public boolean insertChildBefore( Node newChild, Node beforeWhatChildNode ) {

		throw new BadCallError();
	}
	
	@Override
	public boolean removeChild( Node child ) {

		throw new BadCallError();
	}
}
