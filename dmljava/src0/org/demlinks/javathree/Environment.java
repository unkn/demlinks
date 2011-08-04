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


package org.demlinks.javathree;



import java.util.*;

import org.demlinks.references.*;
import org.q.*;



/**
 * at this level the Node objects are given String IDs<br>
 * such that a String ID can be referring to only one Node object<br>
 * so there's an 1 to 1 mapping between ID and Node<br>
 * a Node will exist only if it has at least one link or rather is part of the
 * link<br>
 * a link is a tuple of Nodes; link is imaginary so to speak<br>
 * sourceID -> destinationID means: the Node object identified by sourceID will
 * have its forward list contain the Node object identified by destinationID<br>
 * sourceID <- destinationID means: the Node identified by destinationID will
 * have its backwards list contain the Node object identified by sourceID<br>
 * 
 */
public class Environment {
	
	// fields
	private final IDToNodeMap	allIDNodeTuples;	// unique elements
													
													
	// constructor
	/**
	 * Environment containing ID to Node mappings<br>
	 * ID is {@link String} identifier Node is a {@link NodeLevel0} object
	 */
	public Environment() {
		super();
		allIDNodeTuples = new IDToNodeMap();
	}
	
	
	// methods
	
	/**
	 * @param nodeID
	 * @return true if the node exists in this environment, doesn't matter if it
	 *         has any forward/backwards
	 */
	public boolean isNode( final Id nodeID ) {
		assert Q.nn( nodeID );
		return ( null != getNode( nodeID ) );
	}
	
	
	/**
	 * @return the Node object that's mapped to the ID, if it doesn't exist in
	 *         the Environment then null
	 */
	private Node0 getNode( final Id nodeID ) {
		assert Q.nn( nodeID );
		return allIDNodeTuples.getNode( nodeID );
	}
	
	
	/**
	 * @return the ID that is mapped to the Node object, in this environment, or
	 *         null if there's no such mapping
	 */
	private Id getID( final Node0 node ) {
		assert Q.nn( node );
		return allIDNodeTuples.getID( node );// should be useful when parsing
	}
	
	
	/**
	 * @param nodeID
	 * @param nodeObject
	 * @return false if id was already mapped to a node; true if it wasn't
	 * @throws Exception
	 */
	private boolean internalMapIDToNode( final Id nodeID, final Node0 nodeObject ) {
		return allIDNodeTuples.put( nodeID, nodeObject );
	}
	
	
	private void internalUnMapID( final Id nodeID ) {
		allIDNodeTuples.removeID( nodeID );
	}
	
	
	/**
	 * @return number of Nodes in the environment
	 */
	public int size() {
		return allIDNodeTuples.size();
	}
	
	
	/**
	 * this will create a new Node object and map it to the given ID<br>
	 * unless it already exists<br>
	 * 
	 * @param nodeID
	 *            supposedly unused ID
	 * @return if the ID is already mapped then it will return its respective
	 *         Node object
	 * @throws Exception
	 */
	private Node0 ensureNode( final Id nodeID ) {
		Node0 n = getNode( nodeID );
		if ( null == n ) {
			n = new Node0();
			if ( !internalMapIDToNode( nodeID, n ) ) {
				Q.bug( "overwritten something, which is impossible" );
			}
		}
		return n;
	}
	
	
	/**
	 * @param sourceNode
	 * @param destinationNode
	 * @return
	 */
	private boolean internalLinkForward( final Node0 sourceNode, final Node0 destinationNode ) {
		// this method is here to prevent the ie. test suite calling link(node,
		// node)
		// assumes both Nodes exist and are not null params, else expect
		// exceptions
		final boolean ret1 = sourceNode.linkForward( destinationNode );
		final boolean ret2 = destinationNode.linkBackward( sourceNode );
		if ( ret1 ^ ret2 ) {
			Q.bug( "inconsistent link detected" );
		}
		return ret1;
	}
	
	
	/**
	 * this will link the two nodes identified by those IDs<br>
	 * this will link forward sourceID to destinationID<br>
	 * and also link backward destinationID to sourceID<br>
	 * if there is no Node for the specified ID it will be created and mapped to
	 * it<br>
	 * there will be no linkBackward() because it would be just a matter of
	 * exchanging parameter places<br>
	 * sourceID -> destinationID (the Node object identified by sourceID will
	 * have its forward list contain the Node object identified by
	 * destinationID)<br>
	 * sourceID <- destinationID (the Node identified by destinationID will have
	 * its backwards list contain the Node object identified by sourceID)<br>
	 * 
	 * @param sourceID
	 *            ie. backward
	 * @param destinationID
	 *            ie. forward
	 * @throws Exception
	 *             if ID to Node mapping fails
	 * @transaction protected
	 */
	public boolean linkForward( final Id sourceID, final Id destinationID ) throws Exception {
		// 1.it will create empty Node objects if they don't already exist
		// 2.map them to IDs
		// 3.THEN link them
		
		boolean sourceCreated = false;
		Node0 sourceNode = getNode( sourceID );// fetch existing Node
		if ( null == sourceNode ) {
			// ah there was no existing Node object with that ID
			// we create a new one
			sourceNode = ensureNode( sourceID );
			sourceCreated = true;
		}
		
		boolean destinationCreated = false;
		Node0 destinationNode = getNode( destinationID );// fetch existing Node
															// identified by
															// destinationID
		if ( null == destinationNode ) {
			// nothing existing? create one
			destinationNode = ensureNode( destinationID );
			destinationCreated = true;
		}
		
		boolean ret = false;
		try {
			ret = internalLinkForward( sourceNode, destinationNode );// link the
			// Node
			// objects
		} catch ( final Exception e ) {
			try {
				if ( sourceCreated ) {
					removeNode( sourceID );
				}
				
				if ( destinationCreated ) {
					removeNode( destinationID );
				}
			} catch ( final Exception f ) {
				e.printStackTrace();
				Q.rethrow( f );
			}
			throw e;
		}
		return ret;
	}
	
	
	/**
	 * remove the mapping between Node and its ID<br>
	 * basically it will unmap the ID from the Node object only if the Node
	 * object has no forward and no backwards
	 * 
	 * @param nodeID
	 * @return the removed Node
	 */
	public Node0 removeNode( final Id nodeID ) {
		final Node0 n = getNode( nodeID );
		if ( n == null ) {
			Q.badCall( "attempt to remove a non-existing node ID" );
		}
		if ( !n.isAlone() ) {
			Q.badCall( "attempt to remove a non-empty node. Clear its lists first!" );
		}
		internalUnMapID( nodeID );
		return n;
	}
	
	
	/**
	 * @param sourceID
	 * @param destinationID
	 * @return
	 */
	public boolean isLinkForward( final Id sourceID, final Id destinationID ) {
		assert Q.nn( sourceID );
		assert Q.nn( destinationID );
		final Node0 sourceNode = getNode( sourceID );
		final Node0 destinationNode = getNode( destinationID );
		if ( ( null != sourceNode ) && ( null != destinationNode ) ) {
			return internalIsLinkForward( sourceNode, destinationNode );
		}
		// backward OR forward doesn't exist hence neither the link
		return false;
	}
	
	
	/**
	 * sourceNode -> destinationNode<br>
	 * sourceNode <- destinationNode<br>
	 * 
	 * @param sourceNode
	 * @param destinationNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	private boolean internalIsLinkForward( final Node0 sourceNode, final Node0 destinationNode ) {
		assert Q.nn( sourceNode );
		assert Q.nn( destinationNode );
		final boolean one = sourceNode.isLinkForward( destinationNode );
		final boolean two = destinationNode.isLinkBackward( sourceNode );
		if ( one ^ two ) {
			Q.bug( "inconsistent link detected" );
		}
		return one;
	}
	
	
	public boolean unLinkForward( final Id backwardId, final Id forwardId ) {
		assert Q.nn( backwardId );
		assert Q.nn( forwardId );
		final Node0 sourceNode = getNode( backwardId );
		final Node0 destinationNode = getNode( forwardId );
		if ( ( null != sourceNode ) && ( null != destinationNode ) ) {
			return internalUnLinkForward( sourceNode, destinationNode );
		}
		return false;
	}
	
	
	/**
	 * @param sourceNode
	 * @param destinationNode
	 * @return true if link existed before call; false if it didn't exist before
	 *         call; either way it no longer exists after call
	 */
	private boolean internalUnLinkForward( final Node0 sourceNode, final Node0 destinationNode ) {
		assert Q.nn( sourceNode );
		assert Q.nn( destinationNode );
		final boolean one = sourceNode.unLinkForward( destinationNode );
		final boolean two = destinationNode.unLinkBackward( sourceNode );
		if ( one ^ two ) {
			Q.bug( "inconsistent link detected" );
		}
		return one;
	}
	
	
	public int getSize( final Id nodeID, final List list ) {
		final Node0 n = getNode( nodeID );
		if ( null == n ) {
			throw new NoSuchElementException( "inexistent Node, in the environment" );
		}
		return n.getList( list ).size();
	}
	
	
	public NodeParser getParser( final Id nodeID, final List list, final Location location ) {
		final Parser p = new Parser( nodeID, list, location );
		return p;
	}
	
	private class Parser implements NodeParser {
		
		Reference<Node0>	current	= null;
		NodeRefsList		nrl		= null;
		
		
		// TODO parser for NodeRefsList
		// TODO remove L1 and L2 from NodeRefsList by generalizing to RefsList
		// or so
		public Parser( final Id nodeID, final List list, final Location location ) {
			// this(nodeID, list, location, null);
			assert Q.nn( nodeID );
			assert Q.nn( list );
			assert Q.nn( location );
			@SuppressWarnings( "synthetic-access" )
			final Node0 n = getNode( nodeID );
			assert Q.nn( n );
			nrl = n.getList( list );
			assert Q.nn( nrl );
			current = nrl.getNodeRefAt( location );// could be null
		}
		
		
		@Override
		public Id getCurrentID() {
			if ( current == null ) {
				return null;
			}
			final Node0 n = current.getObject();
			@SuppressWarnings( "synthetic-access" )
			final Id i = getID( n );
			return i;// could be null
		}
		
		
		@Override
		public void go( final Location location ) throws Exception {
			// TODO when list is modified, add a variable that's incremented on
			// add/replace/move in NodeRefsList_L1, copy it here
			current = nrl.getNodeRefAt( location, current );
		}
		
	}
}
