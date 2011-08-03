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



import org.demlinks.constants.*;
import org.demlinks.debug.*;
import org.demlinks.errors.*;
import org.demlinks.exceptions.*;
import org.demlinks.node.*;



public class NodeWithDupChildren extends Node {
	
	public NodeWithDupChildren() {
		
		super();// if u forget this, it's called anyway =)
		Environment.internalEnsureNodeIsChildOf( this, Environment.AllNodeWithDupChildrenNodes );
	}
	
	
	@Override
	public Node getChildAt( final int zeroBasedIndex ) {
		
		throw new BadCallError( "use .dup* methods" );
	}
	
	
	@Override
	public boolean appendChild( final Node child ) {
		
		throw new BadCallError( "use .dup* methods" );
	}
	
	
	public IntermediaryNode getIntermediaryAt( final int zeroBasedIndex ) {
		
		return (IntermediaryNode)super.getChildAt( zeroBasedIndex );
	}
	
	
	/**
	 * is the following true ?<br>
	 * this -> someIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 * @return true if the child exists
	 */
	public boolean dupHasChild( final Node childNode ) {
		
		// basically parse all children and check if .getPointee() is
		// childNode
		return null != this.getIntermediaryForFirstChild( childNode );
	}
	
	
	/**
	 * @param zeroBasedIndex
	 * @return bypassing IntermediaryNode, returns the child at that index
	 * @see #getChildAt(int)
	 */
	public Node dupGetChildAt( final int zeroBasedIndex ) {
		
		final IntermediaryNode in = (IntermediaryNode)super.getChildAt( zeroBasedIndex );
		if ( null != in ) {
			return in.getPointee();
		} else {
			return null;
		}
	}
	
	
	/**
	 * make the following true: <br>
	 * this -> newIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 */
	public void dupAppendChild( final Node childNode ) {
		
		Debug.nullException( childNode );
		final IntermediaryNode IN = new IntermediaryNode();
		if ( IN.pointTo( childNode ) ) {
			// already pointed? impossible
			throw new BugError();
		}
		if ( super.appendChild( IN ) ) {
			// IN existed? impossible
			throw new BugError( "couldn't've existed, it was made by new" );
		}
	}
	
	
	/**
	 * returns first child bypassing the IntermediaryNode<br>
	 * this->IN->child0<br>
	 * 
	 * @return child0
	 */
	public Node dupGetFirstChild() {
		
		return this.getIntermediaryForFirstChild().getPointee();
	}
	
	
	/**
	 * @return
	 * @see #dupGetFirstChild()
	 */
	public Node dupGetLastChild() {
		
		return this.getIntermediaryForLastChild().getPointee();
	}
	
	
	@Override
	public void integrityCheck() {
		
		super.integrityCheck();
		
		if ( !hasParent( Environment.AllNodeWithDupChildrenNodes ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
	
	public IntermediaryNode getIntermediaryForLastChild() {
		
		return (IntermediaryNode)getLastChild();
	}
	
	
	/**
	 * @return the IN for the first child of <tt>this</tt>
	 */
	public IntermediaryNode getIntermediaryForFirstChild() {
		
		return (IntermediaryNode)getFirstChild();
	}
	
	
	/**
	 * acts like {@link #getIntermediaryForFirstChild()} except it looks only
	 * for child nodes equal to <tt>childNode</tt><br>
	 * while parsing from first to last, the first child that equals childNode,
	 * its IN is returned
	 * 
	 * @param childNode
	 * @return
	 */
	public IntermediaryNode getIntermediaryForFirstChild( final Node childNode ) {
		
		Debug.nullException( childNode );
		return getIntermediaryForNextChild( childNode, this.getIntermediaryForFirstChild(), false );
	}
	
	
	/**
	 * acts like {@link #getIntermediaryForFirstChild(Node)} but parses in
	 * reverse direction<br>
	 * while parsing from last to first, the first(yes first) child that equals
	 * childNode, its IN is returned
	 * 
	 * @param childNode
	 * @return
	 */
	public IntermediaryNode getIntermediaryForLastChild( final Node childNode ) {
		
		Debug.nullException( childNode );
		return getIntermediaryForPrevChild( childNode, this.getIntermediaryForLastChild(), DO.NOSKIP );
	}
	
	
	/**
	 * @param childNode
	 *            look only for this Node in children
	 * @param startingFromThisIN
	 *            the IntermediaryNode that was last found
	 * @param skipIN
	 *            true if to skip the startingFromThisIN, or false if to
	 *            consider it
	 * @return the IN for the next Node that equals <tt>childNode</tt> if any,
	 *         or null;
	 */
	public IntermediaryNode getIntermediaryForNextChild( final Node childNode, final IntermediaryNode startingFromThisIN,
															final boolean skipIN ) {
		
		Debug.nullException( childNode, startingFromThisIN );
		// we skip startingFromThisIN because startingFromThisIN.child ==
		// childNode already
		IntermediaryNode parser;
		if ( skipIN == DO.SKIP ) {
			parser = getNextIntermediary( startingFromThisIN );
		} else {
			// DO.NOSKIP:
			parser = startingFromThisIN;
		}
		while ( parser != null ) {
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BugError( "should be!" );
			}
			final Node tmpChild = ( parser ).getPointee();
			if ( tmpChild == childNode ) {
				return parser;
			}
			// else, continue parsing
			parser = (IntermediaryNode)( getChildNextOf( parser ) );
		}
		return null;
	}
	
	
	/**
	 * @param childNode
	 *            look only for this Node in children
	 * @param startingFromThisIN
	 *            the IntermediaryNode that was last found; usually
	 * @param skipIN
	 *            true if to skip the startingFromThisIN, or false if to
	 *            consider it
	 * @return the IN for the prev Node that equals <tt>childNode</tt> if any,
	 *         or null;
	 */
	public IntermediaryNode getIntermediaryForPrevChild( final Node childNode, final IntermediaryNode startingFromThisIN,
															final boolean skipIN ) {
		
		Debug.nullException( childNode, startingFromThisIN );
		// we skip startingFromThisIN because startingFromThisIN.child ==
		// childNode already
		IntermediaryNode parser;
		if ( skipIN ) {
			parser = getPrevIntermediary( startingFromThisIN );
		} else {
			parser = startingFromThisIN;
		}
		while ( parser != null ) {
			if ( !Environment.isIntermediaryNode( parser ) ) {
				throw new BugError( "should be!" );
			}
			final Node tmpChild = ( parser ).getPointee();
			if ( tmpChild == childNode ) {
				return parser;
			}
			// else, continue parsing
			parser = (IntermediaryNode)( getChildPrevOf( parser ) );
		}
		return null;
		// TODO yes I see dup of code because should be params are in the names
		// of the methods
	}
	
	
	/**
	 * @param startingFromThisIN
	 * @return the next IN that is after <tt>startingFromThisIN</tt>
	 */
	public IntermediaryNode getNextIntermediary( final IntermediaryNode startingFromThisIN ) {
		
		Debug.nullException( startingFromThisIN );
		final IntermediaryNode iN = (IntermediaryNode)( getChildNextOf( startingFromThisIN ) );
		if ( null != iN ) {
			if ( !Environment.isIntermediaryNode( iN ) ) {
				throw new BugError( "should be!" );
			}
		}
		return iN;
	}
	
	
	/**
	 * @param startingFromThisIN
	 * @return the prev IN that is before <tt>startingFromThisIN</tt>
	 */
	public IntermediaryNode getPrevIntermediary( final IntermediaryNode startingFromThisIN ) {
		
		Debug.nullException( startingFromThisIN );
		final IntermediaryNode iN = (IntermediaryNode)( getChildPrevOf( startingFromThisIN ) );
		
		if ( null != iN ) {
			if ( !Environment.isIntermediaryNode( iN ) ) {
				throw new BugError( "should be!" );
			}
		}
		
		return iN;
	}
	
	
	public int getCountOfChildren( final Node childNode ) {
		
		Debug.nullException( childNode );
		integrityCheck();
		int count = 0;
		IntermediaryNode iN = this.getIntermediaryForFirstChild( childNode );
		while ( iN != null ) {
			count++;
			iN = getIntermediaryForNextChild( childNode, iN, DO.SKIP );
		}
		return count;
	}
	
	
	/**
	 * this->IN->child<br>
	 * will unlink both links, first IN->child then this->IN
	 * 
	 * @param intermediaryNodeToRemove
	 */
	public void dupRemoveIntermediaryNode( final IntermediaryNode intermediaryNodeToRemove ) {
		
		Debug.nullException( intermediaryNodeToRemove );
		integrityCheck();
		if ( !hasChild( intermediaryNodeToRemove ) ) {
			throw new BadParameterException( "the IN is not from this NodeWithDupChildren" );
		}
		
		intermediaryNodeToRemove.setNull();// unlinks IN->child
		
		// remove this->IN:
		if ( !removeChild( intermediaryNodeToRemove ) ) {
			throw new BugError( "should've returned true" );
		}
	}
	
	
	@Override
	public boolean clearAllChildren() {
		
		throw new BadCallError( "use .dup*" );
	}
	
	
	/**
	 * @return true if there was at least 1 child that was removed<br>
	 *         false is nothing was removed, already empty;
	 */
	public boolean dupClearAllChildren() {
		
		// TODO junit test
		IntermediaryNode parser = this.getIntermediaryForFirstChild();
		boolean ret = false;
		while ( null != parser ) {
			dupRemoveIntermediaryNode( parser );
			parser = this.getIntermediaryForFirstChild();
			ret = true;
		}
		return ret;
	}
}
