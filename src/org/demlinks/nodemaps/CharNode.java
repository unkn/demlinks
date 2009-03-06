

package org.demlinks.nodemaps;



import org.demlinks.errors.BugError;



public class CharNode extends ChildlessNode {
	
	public CharNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this, Environment.AllCharNodes );
	}
	
	@Override
	public void integrityCheck() {

		super.integrityCheck();
		if ( !this.hasParent( Environment.AllCharNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
}
