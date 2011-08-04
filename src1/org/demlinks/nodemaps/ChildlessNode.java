/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.nodemaps;



import org.demlinks.node.*;
import org.q.*;



public class ChildlessNode extends Node {
	
	@Override
	public void integrityCheck() {
		
		super.integrityCheck();
		if ( numChildren() != 0 ) {
			Q.bug( "someone made the childless node have children" );
		}
	}
	
	
	@Override
	public boolean appendChild( final Node child ) {
		throw Q.badCall( "you're not supposed to add children" );
	}
	
	
	@Override
	public Node getChildNextOf( final Node ofWhatNode ) {
		throw Q.badCall();
	}
	
	
	@Override
	public Node getFirstChild() {
		throw Q.badCall();
	}
	
	
	@Override
	public Node getChildPrevOf( final Node ofWhatNode ) {
		throw Q.badCall();
	}
	
	
	@Override
	public Node getLastChild() {
		throw Q.badCall();
	}
	
	
	@Override
	public boolean hasChild( final Node child ) {
		throw Q.badCall();
	}
	
	
	@Override
	public boolean insertChildAfter( final Node newChild, final Node afterWhatChildNode ) {
		throw Q.badCall();
	}
	
	
	@Override
	public boolean insertChildBefore( final Node newChild, final Node beforeWhatChildNode ) {
		throw Q.badCall();
	}
	
	
	@Override
	public boolean removeChild( final Node child ) {
		throw Q.badCall();
	}
}
