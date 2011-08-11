/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ExceptionsHandling.ToE.swing;



import java.awt.*;

import javax.swing.*;

import org.ExceptionsHandling.ToE.*;
import org.q.*;



/**
 *
 */
public class CellRendererForTreeOfCalls
		extends CellRenderer
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2756022472340507674L;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object,
	 * boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public synchronized Component getTreeCellRendererComponent( final JTree tree, final Object value, final boolean sel,
																final boolean expanded, final boolean leaf, final int row,
																final boolean hasFocus1 ) {
		assert Q.nn( value );
		final Component that = super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus1 );
		assert ( this == that );// else investigate
		if ( value.getClass() != NodeForTreeOfCalls.class ) {
			Q.bug( "all Nodes in tree must be of this type" );
		}
		final NodeForTreeOfCalls nCall = (NodeForTreeOfCalls)value;
		// NodeForTreeOfCalls nCall =
		// TreeOfCalls.getTreeOfCalls().getCallForNode(
		// node );
		assert Q.nn( nCall );
		// NodeForTreeOfExceptions nodeEx =
		// nCall.getExceptionNode();
		// assert Q.nn( nodeEx );
		final StateOfAnException state = nCall.getState();
		assert Q.nn( state );
		final Color c = stateToColorMap.get( state );
		if ( null == c ) {
			Q.bug( "you forgot to add to 1-to-1 map in constructor here, look above" );
		}
		assert Q.nn( c );
		that.setForeground( c );
		return that;
	}
}
