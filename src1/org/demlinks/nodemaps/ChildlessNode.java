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
