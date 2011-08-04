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


package org.demlinks.node;



import javax.naming.*;

import org.demlinks.exceptions.*;
import org.q.*;



public class Node {
	
	// lists of unique elements(Nodes)
	private NodeList	parentsList;
	private NodeList	childrenList;
	
	
	public Node() {
		
		createLists();
	}
	
	
	private void createLists() {
		
		parentsList = new NodeList();
		childrenList = new NodeList();
	}
	
	
	/**
	 * creates both links:<br>
	 * this -> child : "this" will know it has a child "child"<br>
	 * this <- child : and the child "child" will know it has a parent "this"
	 * 
	 * @param child
	 * @return true if child existed before call and hence it could be anywhere
	 *         in the list not necessarily at the end of it; false if it didn't
	 *         exist and it is now (after call) at the end(appended) of the list
	 *         also note that if child existed then so did "this"(the parent)
	 *         for the child
	 */
	public boolean appendChild( final Node child ) {
		
		assert Q.nn( child );
		final boolean existed1 = internalAppendChild( child );
		final boolean existed2 = child.internalAppendParent( this );
		if ( existed1 ^ existed2 ) {
			// if either one existed, then inconsistent link detected
			// somewhere something made a boo boo
			throw new InconsistentLinkException( "inconsistent link detected" );
			// maybe undo-ing here before throwing is not a good idea
		}
		return existed1;// should be same value as existed2
	}
	
	
	/**
	 * parent <- this<br>
	 * parent -> this
	 * 
	 * @see {@link #appendChild(Node)}
	 */
	public boolean appendParent( final Node parent ) {
		
		assert Q.nn( parent );
		return parent.appendChild( this );
	}
	
	
	/**
	 * @param child
	 * @return true if child didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendChild( final Node child ) {
		
		return childrenList.appendNode( child );
	}
	
	
	/**
	 * @param parent
	 * @return true if parent didn't exist; false if it did exist before call
	 */
	protected boolean internalAppendParent( final Node parent ) {
		
		return parentsList.appendNode( parent );
	}
	
	
	/**
	 * this -> child ? AND:<br>
	 * this <- child
	 * 
	 * @param child
	 * @return true if <tt>this</tt> has <tt>child</tt> in its children list and <tt>child</tt> has <tt>this</tt> in its parents
	 *         list is only one
	 *         of the links exists
	 */
	public boolean hasChild( final Node child ) {
		
		assert Q.nn( child );
		final boolean link1 = childrenList.hasNode( child );
		final boolean link2 = child.parentsList.hasNode( this );// isn't that private
		// field?
		if ( link1 ^ link2 ) {
			throw new InconsistentLinkException( "inconsistent link detected" );
		}
		return link1;
	}
	
	
	/**
	 * @param child
	 * @param index
	 * @return true if child is at index
	 */
	public boolean hasChildAtPos( final Node child, final int index ) {
		
		assert Q.nn( child );
		final boolean link1 = childrenList.hasNodeAtPos( child, index );
		final boolean link2 = child.parentsList.hasNode( this );// not at pos
		if ( ( link1 ) && ( !link2 ) ) {// special case IF
			throw new InconsistentLinkException( "detected" );
		}
		return link1;
	}
	
	
	/**
	 * parent <- this ? AND: parent -> this
	 * 
	 * @param parent
	 * @return true if <tt>this</tt> has <tt>parent</tt> in its parents list and <tt>parent</tt> has <tt>this</tt> in its
	 *         children list
	 * @see {@link #hasChild(Node)}
	 */
	public boolean hasParent( final Node parent ) {
		
		assert Q.nn( parent );
		return parent.hasChild( this );// counting on hasChild to check both
	}
	
	
	/**
	 * @param child
	 * @return true if child existed before call
	 * @throws CannotProceedException
	 */
	protected boolean internalRemoveChild( final Node child ) {
		
		return childrenList.removeNode( child );
	}
	
	
	protected boolean internalRemoveParent( final Node parent ) {
		
		return parentsList.removeNode( parent );
	}
	
	
	/**
	 * remove both links:<br>
	 * this -> child<br>
	 * this <- child
	 * 
	 * @param child
	 * @return true if both links existed before call
	 * @throws CannotProceedException
	 *             if one or both links still exist after call if only one of
	 *             the links existed before call, now neither should exist
	 */
	public boolean removeChild( final Node child ) {
		
		final boolean link1 = internalRemoveChild( child );
		final boolean link2 = child.internalRemoveParent( this );
		if ( link1 ^ link2 ) {
			throw new InconsistentLinkException( "inconsistent link detected" );
		}
		return link1;
	}
	
	
	/**
	 * @param parent
	 * @return
	 * @see #removeChild(Node)
	 */
	public boolean removeParent( final Node parent ) {
		
		return parent.removeChild( this );
	}
	
	
	public Node getFirstChild() {
		
		return childrenList.getFirstNode();
	}
	
	
	public Node getLastChild() {
		
		return childrenList.getLastNode();
	}
	
	
	public Node getFirstParent() {
		
		return parentsList.getFirstNode();
	}
	
	
	public Node getLastParent() {
		
		return parentsList.getLastNode();
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return null or the child node that's at position index in this'
	 *         childrenList
	 */
	public Node getChildAt( final int zeroBasedIndex ) {
		
		// assert Q.nn(zeroBasedIndex );
		return childrenList.getNodeAt( zeroBasedIndex );
	}
	
	
	/**
	 * @param index
	 *            0 based index
	 * @return null or the parent node that's at position index in this'
	 *         parentsList
	 */
	public Node getParentAt( final int zeroBasedIndex ) {
		
		// assert Q.nn(zeroBasedIndex );
		return parentsList.getNodeAt( zeroBasedIndex );
	}
	
	
	/**
	 * @param ofWhatNode
	 *            is an existing child Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getChildNextOf( final Node ofWhatNode ) {
		
		assert Q.nn( ofWhatNode );
		return childrenList.getNodeAfter( ofWhatNode );
	}
	
	
	/**
	 * @param ofWhatNode
	 *            is an existing child Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getChildPrevOf( final Node ofWhatNode ) {
		
		assert Q.nn( ofWhatNode );
		return childrenList.getNodeBefore( ofWhatNode );
	}
	
	
	/**
	 * @param ofWhatNode
	 *            is an existing parent Node
	 * @return null or the node following <tt>ofWhatNode</tt>
	 */
	public Node getParentNextOf( final Node ofWhatNode ) {
		
		assert Q.nn( ofWhatNode );
		return parentsList.getNodeAfter( ofWhatNode );
	}
	
	
	/**
	 * @param thatHasThisParent
	 *            consider only parents that have parent 'thatHasParent'<br>
	 *            ie. thatHasParent->parentFound->this
	 * @param continueFromNode
	 *            skip this node; it's the last found;<br>
	 *            if null then start from beginning
	 * @return the next Parent where thatHasParent->Parent->child<br>
	 *         or null
	 */
	public Node getNextParent( final Node thatHasThisParent, final Node continueFromNode ) {
		
		assert Q.nn( thatHasThisParent );
		
		Node parser;
		if ( continueFromNode == null ) {
			parser = getFirstParent();
		} else {
			parser = getParentNextOf( continueFromNode );// by skipping it
		}
		
		while ( parser != null ) {
			if ( parser.hasParent( thatHasThisParent ) ) {
				return parser;
			}
			parser = getParentNextOf( parser );
		}
		return null;
	}
	
	
	/**
	 * @param ofWhatNode
	 *            is an existing parent Node
	 * @return null or the node before <tt>ofWhatNode</tt>
	 */
	public Node getParentPrevOf( final Node ofWhatNode ) {
		
		assert Q.nn( ofWhatNode );
		return parentsList.getNodeBefore( ofWhatNode );
	}
	
	
	/**
	 * @return number of parents for this node
	 */
	public int numParents() {
		
		return parentsList.size();
	}
	
	
	/**
	 * @return number of children for this node
	 */
	public int numChildren() {
		
		return childrenList.size();
	}
	
	
	/**
	 * @param inWhichList
	 *            children or parents list
	 * @param whatNewNode
	 *            node that doesn't exist in "inWhichList"
	 * @param pos
	 *            BEFORE or AFTER
	 * @param posNode
	 *            a Node in "inWhichList" list, that "pos" is referring to
	 * @return
	 */
	private boolean internalInsert( final NodeList inWhichList, final Node whatNewNode, final Position pos, final Node posNode ) {
		
		assert Q.nn( inWhichList );
		assert Q.nn( whatNewNode );
		assert Q.nn( pos );
		assert Q.nn( posNode );
		// some people are paranoid here :-"
		if ( ( inWhichList != childrenList ) && ( inWhichList != parentsList ) ) {
			throw new BadCallError( "invalid list specified" );
		}
		if ( ( pos != Position.BEFORE ) && ( pos != Position.AFTER ) ) {
			throw new BadCallError( "undefined location within this context" );
		}
		return inWhichList.insert( whatNewNode, pos, posNode );
	}
	
	
	public boolean insertChildAfter( final Node newChild, final Node afterWhatChildNode ) {
		
		assert Q.nn( newChild );
		assert Q.nn( afterWhatChildNode );
		return internalInsert( childrenList, newChild, Position.AFTER, afterWhatChildNode );
	}
	
	
	public boolean insertChildBefore( final Node newChild, final Node beforeWhatChildNode ) {
		
		assert Q.nn( newChild );
		assert Q.nn( beforeWhatChildNode );
		return internalInsert( childrenList, newChild, Position.BEFORE, beforeWhatChildNode );
	}
	
	
	public boolean insertParentAfter( final Node newParent, final Node afterWhatParentNode ) {
		
		assert Q.nn( newParent );
		assert Q.nn( afterWhatParentNode );
		return internalInsert( parentsList, newParent, Position.AFTER, afterWhatParentNode );
	}
	
	
	public boolean insertParentBefore( final Node newParent, final Node beforeWhatParentNode ) {
		
		assert Q.nn( newParent );
		assert Q.nn( beforeWhatParentNode );
		return internalInsert( parentsList, newParent, Position.BEFORE, beforeWhatParentNode );
	}
	
	
	public void integrityCheck() {
		
		assert Q.nn( childrenList );
		assert Q.nn( parentsList );
		
		Node parser = childrenList.getFirstNode();
		while ( null != parser ) {
			if ( !( ( hasChild( parser ) ) && ( parser.hasParent( this ) ) ) ) {
				throw new InconsistentLinkException( "half link detected?" );
				// basically hasChild or hasParent will throw before we do here
			}
			parser = childrenList.getNodeAfter( parser );
		}
		
		parser = parentsList.getFirstNode();
		while ( null != parser ) {
			if ( !( ( hasParent( parser ) ) && ( parser.hasChild( this ) ) ) ) {
				throw new InconsistentLinkException( "half link detected?" );
				// basically hasChild or hasParent will throw before we do here
			}
			parser = parentsList.getNodeAfter( parser );
		}
	}
	
	
	/**
	 * remove all children
	 * 
	 * @return true if there was at least one child removed;<br>
	 *         false if children list was already empty hence nothing needed to
	 *         be removed<br>
	 *         whatever the return, the same result is accomplished
	 */
	public boolean clearAllChildren() {
		
		boolean ret = false;
		Node parser = getFirstChild();
		while ( null != parser ) {
			if ( !removeChild( parser ) ) {
				throw new BugError( "should always be true here" );
			}
			parser = getFirstChild();
			ret = true;// at least one child existed
		}
		return ret;
		// TODO junit test
	}
}
