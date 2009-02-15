
package org.demlinks.node;



public class NodeWithDupChildren extends Node {
	
	/**
	 * is the following true ?<br>
	 * this -> someIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 * @return true if the child exists
	 */
	public boolean dupHasChild( Node childNode ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * make the following true: <br>
	 * this -> newIntermediaryNode -> childNode
	 * 
	 * @param childNode
	 * @return true if the childNode existed already, and this new one was
	 *         appended too false if childNode didn't exist before call, but it
	 *         does now
	 */
	public boolean dupAppendChild( Node childNode ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void integrityCheck() {
		// TODO Auto-generated method stub
		super.integrityCheck();
	}
	
	public IntermediaryNode getIntermediaryForLastChild() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the IN for the first child of <tt>this</tt>
	 */
	public IntermediaryNode getIntermediaryForFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * acts like {@link #getIntermediaryForFirstChild()} except it looks only
	 * for child nodes equal to <tt>childNode</tt>
	 * 
	 * @param childNode
	 * @return
	 */
	public IntermediaryNode getIntermediaryForFirstChild( Node childNode ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param childNode
	 *            look only for this Node in children
	 * @param startingFromThisIN
	 *            the IntermediaryNode that was last found; skip this, continue
	 *            from it til end
	 * @return the IN for the next Node that equals <tt>childNode</tt> if any,
	 *         or null;
	 */
	public IntermediaryNode getIntermediaryForNextChild( Node childNode,
			IntermediaryNode startingFromThisIN ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param lastFoundIN
	 *            continue after this IN and go until end
	 * @return the next IN after <tt>lastFoundIN</tt> is any, or null;
	 */
	public IntermediaryNode getIntermediaryForNextChild(
			IntermediaryNode lastFoundIN ) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
