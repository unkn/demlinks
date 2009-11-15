/**
 * File creation: May 30, 2009 8:20:44 PM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
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


package org.dml.level1;



import org.dml.tools.RunTime;



/**
 * is stored in Storage<br>
 * so basically, NodeID is a long, in Storage
 * and it's one2one associated with a NodeJavaID (which is basically a String in
 * java)<br>
 * it's not really important what NodeID is, rather it's important this one2one
 * association between the two<br>
 * 
 * in Storage, a Sequence is used to generate a new NodeID that won't equate
 * with any other already existent<br>
 */
public class NodeID {
	
	/**
	 * The <code>Class</code> instance representing the primitive type
	 * <code>float</code>.
	 * 
	 * @since JDK1.1
	 */
	// public static final Class<Float> TYPE =
	// Class.getPrimitiveClass("NodeID");
	
	private final long	itself;
	
	/**
	 * constructor, call only internally
	 * 
	 * @param iD
	 */
	public NodeID( long iD ) {

		itself = iD;
	}
	
	// /**
	// * constructor, call only internally (maybe also within the package)<br>
	// * must be a long inside that string
	// *
	// * @param iD
	// * a long expressed as a string
	// * @throws NumberFormatException
	// */
	// public NodeID( String iD ) {
	//
	// itself = Long.parseLong( iD );
	// // itself = Long.valueOf( iD );
	// }
	
	// /**
	// * @return the string representation of this NodeID, usually long to
	// string
	// * transformation
	// */
	// public String getAsString() {
	//
	// return String.valueOf( itself );
	// }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return this.getClass().getSimpleName() + ":" + String.valueOf( itself );
	}
	
	/**
	 * compares by content if refs are different
	 * 
	 * @param nid
	 * @return
	 */
	@Override
	public boolean equals( Object nid ) {

		RunTime.assertNotNull( nid );
		if ( ( !this.getClass().isAssignableFrom( nid.getClass() ) )
				|| ( this.getClass() != nid.getClass() ) ) {
			RunTime.bug( "you passed a different type parameter; must be a bug somewhere" );
		}
		if ( ( super.equals( nid ) ) || // ( this.getAsString().equals( (
				// (NodeID)nid ).getAsString() ) ) ) {
				( ( (NodeID)nid ).itself == itself ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public long internalGetForBinding() {

		return itself;
	}
}
