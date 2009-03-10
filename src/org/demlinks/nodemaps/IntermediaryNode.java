/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import org.demlinks.errors.BugError;
import org.demlinks.node.Node;



/**
 * the Node that's between a parent NodeWithDupChildren and a child as seen
 * through that NodeWithDupChildren <br>
 * 
 * NodeWithDupChildren -> IntermediaryNode -> normalChildNode<br>
 * all intermediary nodes are unique as children of NodeWithDupChildren<br>
 * but normalChildNode can be any Node even if it repeats, ie: <br>
 * NodeWithDupChildren -> RND1 -> A <br>
 * NodeWithDupChildren -> RND2 -> B <br>
 * NodeWithDupChildren -> RND3 -> A (again) <br>
 * NodeWithDupChildren -> RND4 -> C <br>
 */
public class IntermediaryNode extends PointerNode {
	
	public IntermediaryNode() {

		super();
		Environment.internalCreateNodeAsChildOf( this,
				Environment.AllIntermediaryNodes );
	}
	
	@Override
	public void integrityCheck() {

		super.integrityCheck();
		// if you remove extends PointerNode then uncomment:
		// if ( this.numChildren() > 1 ) {
		// throw new BugError(
		// "someone made the pointer have more than 1 child" );
		// }
		if ( !this.hasParent( Environment.AllIntermediaryNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
		
		// test for more than or less than 1 NodeWithDupChildren parent
		Node parser = this.getFirstParent();
		int count = 0;
		while ( null != parser ) {
			boolean one = Environment.isNodeWithDupChildren( parser );
			boolean two = parser.hasParent( Environment.AllNodeWithDupChildrenNodes );
			if ( one ^ two ) {
				throw new BugError(
						"inconsitency detected for that method in Environment" );
			}
			if ( one ) {// one == two, always
				count++;
			}
			parser = this.getParentNextOf( parser );
		}
		if ( count > 1 ) {
			throw new BugError( "IntermediaryNode should have only 0..1 "
					+ "NodeWithDupChildren type parents" );
		}
	}
	
	/**
	 * @return there's only one NodeWithDupChildrenNodes parent for any
	 *         IntermediaryNode; return it
	 */
	public NodeWithDupChildren getFather() {

		// TODO test unit for this
		this.integrityCheck();
		return (NodeWithDupChildren)this.getNextParent(
				Environment.AllNodeWithDupChildrenNodes, null );
	}
}
