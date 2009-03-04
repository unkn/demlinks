
package org.demlinks.node;

import org.demlinks.constants.DO;
import org.demlinks.debug.Debug;
import org.demlinks.errors.BugError;



public class NodeWithDupChildren extends Node {
	
	public NodeWithDupChildren() {
		super();// if u forget this, it's called anyway =)
		GlobalNodes.internalCreateNodeAsChildOf( this,
				GlobalNodes.AllNodesWithDupChildren );
	}
	
	/**
	 * is the following true ?<br>
	 * this -> someIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 * @return true if the child exists
	 */
	public boolean dupHasChild( Node childNode ) {
		// basically parse all children and check if .getPointee() is
		// childNode
		return null != this.getIntermediaryForFirstChild( childNode );
	}
	
	/**
	 * make the following true: <br>
	 * this -> newIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 */
	public void dupAppendChild( Node childNode ) {
		Debug.nullException( childNode );
		IntermediaryNode IN = new IntermediaryNode();
		if ( IN.pointTo( childNode ) ) {
			// already pointed? impossible
			throw new BugError();
		}
		if ( this.appendChild( IN ) ) {
			// IN existed? impossible
			throw new BugError( "couldn't've existed, it was made by new" );
		}
	}
	
	@Override
	public void integrityCheck() {
		super.integrityCheck();
		
		if ( !this.hasParent( GlobalNodes.AllNodesWithDupChildren ) ) {
			throw new BugError( "somehow the parent was removed" );
		}
	}
	
	public IntermediaryNode getIntermediaryForLastChild() {
		return (IntermediaryNode)this.getLastChild();
	}
	
	/**
	 * @return the IN for the first child of <tt>this</tt>
	 */
	public IntermediaryNode getIntermediaryForFirstChild() {
		return (IntermediaryNode)this.getFirstChild();
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
	public IntermediaryNode getIntermediaryForFirstChild( Node childNode ) {
		
		Debug.nullException( childNode );
		return this.getIntermediaryForNextChild( childNode, this
				.getIntermediaryForFirstChild(), false );
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
	public IntermediaryNode getIntermediaryForLastChild( Node childNode ) {
		
		Debug.nullException( childNode );
		return this.getIntermediaryForPrevChild( childNode, this
				.getIntermediaryForLastChild(), DO.NOSKIP );
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
	public IntermediaryNode getIntermediaryForNextChild( Node childNode,
			IntermediaryNode startingFromThisIN, boolean skipIN ) {
		
		Debug.nullException( childNode, startingFromThisIN, skipIN );
		// we skip startingFromThisIN because startingFromThisIN.child ==
		// childNode already
		IntermediaryNode parser;
		if ( skipIN == DO.SKIP ) {
			parser = this.getNextIntermediary( startingFromThisIN );
		} else {
			// DO.NOSKIP:
			parser = startingFromThisIN;
		}
		while ( parser != null ) {
			if ( !GlobalNodes.isIntermediaryNode( parser ) ) {
				throw new BugError( "should be!" );
			}
			Node tmpChild = ( parser ).getPointee();
			if ( tmpChild == childNode ) {
				return parser;
			}
			// else, continue parsing
			parser = (IntermediaryNode)( this.getChildNextOf( parser ) );
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
	public IntermediaryNode getIntermediaryForPrevChild( Node childNode,
			IntermediaryNode startingFromThisIN, boolean skipIN ) {
		
		Debug.nullException( childNode, startingFromThisIN, skipIN );
		// we skip startingFromThisIN because startingFromThisIN.child ==
		// childNode already
		IntermediaryNode parser;
		if ( skipIN ) {
			parser = this.getPrevIntermediary( startingFromThisIN );
		} else {
			parser = startingFromThisIN;
		}
		while ( parser != null ) {
			if ( !GlobalNodes.isIntermediaryNode( parser ) ) {
				throw new BugError( "should be!" );
			}
			Node tmpChild = ( parser ).getPointee();
			if ( tmpChild == childNode ) {
				return parser;
			}
			// else, continue parsing
			parser = (IntermediaryNode)( this.getChildPrevOf( parser ) );
		}
		return null;
		// TODO yes I see dup of code because should be params are in the names
		// of the methods
	}
	
	/**
	 * @param startingFromThisIN
	 * @return the next IN that is after <tt>startingFromThisIN</tt>
	 */
	public IntermediaryNode getNextIntermediary(
			IntermediaryNode startingFromThisIN ) {
		
		Debug.nullException( startingFromThisIN );
		IntermediaryNode iN = (IntermediaryNode)( this
				.getChildNextOf( startingFromThisIN ) );
		if ( null != iN ) {
			if ( !GlobalNodes.isIntermediaryNode( iN ) ) {
				throw new BugError( "should be!" );
			}
		}
		return iN;
	}
	
	/**
	 * @param startingFromThisIN
	 * @return the prev IN that is before <tt>startingFromThisIN</tt>
	 */
	public IntermediaryNode getPrevIntermediary(
			IntermediaryNode startingFromThisIN ) {
		
		Debug.nullException( startingFromThisIN );
		IntermediaryNode iN = (IntermediaryNode)( this
				.getChildPrevOf( startingFromThisIN ) );
		
		if ( null != iN ) {
			if ( !GlobalNodes.isIntermediaryNode( iN ) ) {
				throw new BugError( "should be!" );
			}
		}
		
		return iN;
	}
	
	public int getCountOfChildren( Node childNode ) {
		Debug.nullException( childNode );
		int count = 0;
		IntermediaryNode iN = this.getIntermediaryForFirstChild( childNode );
		while ( iN != null ) {
			count++;
			iN = this.getIntermediaryForNextChild( childNode, iN, DO.SKIP );
		}
		return count;
	}
	


}
