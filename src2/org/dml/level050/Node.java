/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



package org.dml.level050;



import org.dml.level010.*;
import org.dml.level025.*;
import org.dml.level040.*;
import org.dml.tools.*;
import org.references.*;



/**
 * AllNodes->self
 * self->parentsList
 * self->childrenList
 * AllNodeParents->parentsList
 * AllNodeChildren->childrenList
 * AllListsOrderedWithFastFind->parentsList
 * AllListsOrderedWithFastFind->childrenList
 * 
 * FIXME: I will want to have children in a Node point directly to the ref2child in another node... but even then, if
 * the other node decides to change that child, it will most likely remove the ref and make new one unless we also make
 * Replace methods and/or the ability to remove a Symbol only if it's not pointed by any parents, which in our case
 * would be a child from another node also parents of current node which wouldn't be an issue(this latter)
 */
public class Node {
	
	private static final TwoKeyHashMap<Level050_DMLEnvironment, DomainSet, Node>	allNodeInstances	=
																											new TwoKeyHashMap<Level050_DMLEnvironment, DomainSet, Node>();
	
	private final Level050_DMLEnvironment											env;
	private final DomainSet															selfAsDSet;
	
	// TODO accessor that finds again and sets the var on each access, just in
	// case the contents of self were changed by other than this
	private final ListOrderedOfSymbolsWithFastFind									cachedParentsList;
	private final ListOrderedOfSymbolsWithFastFind									cachedChildrenList;
	
	
	/**
	 * private constructor<br>
	 * all passed ones must already exist as if this Node already existed<br>
	 */
	private Node( final Level050_DMLEnvironment existingEnv, final DomainSet existingSelf,
			final ListOrderedOfSymbolsWithFastFind existingParentsList,
			final ListOrderedOfSymbolsWithFastFind existingChildrenList ) {
		
		RunTime.assumedNotNull( existingEnv, existingSelf, existingParentsList, existingChildrenList );
		RunTime.assumedTrue( existingEnv.isInitedSuccessfully() );
		env = existingEnv;
		selfAsDSet = existingSelf;
		cachedParentsList = existingParentsList;
		cachedChildrenList = existingChildrenList;
		
		RunTime.assumedTrue( isItself() );
	}
	
	
	/**
	 * @param passedEnv
	 * @param existingSelfSymbol
	 * @return
	 */
	public static Node getExistingNode( final Level050_DMLEnvironment passedEnv, final Symbol existingSelfSymbol ) {
		
		RunTime.assumedNotNull( passedEnv, existingSelfSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( !isNode( passedEnv, existingSelfSymbol ) ) {
			RunTime.badCall( "existingSelfSymbol is not already a Node" );
		}
		
		final DomainSet selfSet = passedEnv.getAsDomainSet( existingSelfSymbol, passedEnv.allListsOOSWFF_Set.getAsSymbol() );
		RunTime.assumedTrue( selfSet.getAsSymbol() == existingSelfSymbol );
		
		if ( selfSet.size() != 2 ) {
			RunTime.bug( "bad existing Node. It should have exactly 2 kids" );
		}
		
		Node ret = getInstance( passedEnv, selfSet );// existing instance?
		if ( null == ret ) {
			// new instance(in java) of an existing Node(in storage)
			Symbol childrenSymbol = null;
			// it's 2 so the stuff exists, we retrieve them
			final Symbol parentsSymbol =
				passedEnv.findCommonTerminalForInitials( existingSelfSymbol, passedEnv.allNodeParents_Set.getAsSymbol() );
			if ( null == parentsSymbol ) {
				RunTime.bug( "inconsistent Node detected" );
			} else {
				childrenSymbol = selfSet.getSide( Position.FIRST );
				RunTime.assumedNotNull( childrenSymbol );
				if ( childrenSymbol == parentsSymbol ) {
					// get the next one
					childrenSymbol = selfSet.getSideOf( Position.AFTER, childrenSymbol );
				}
				RunTime.assumedNotNull( childrenSymbol );
				RunTime.assumedTrue( passedEnv.allNodeChildren_Set.hasSymbol( childrenSymbol ) );
			}
			
			
			RunTime.assumedNotNull( parentsSymbol );
			RunTime.assumedNotNull( childrenSymbol );
			final ListOrderedOfSymbolsWithFastFind parents = passedEnv.getExistingListOOSWFF( parentsSymbol, false );
			parents.assumedValid();
			RunTime.assumedFalse( parents.isDUPAllowed() );
			
			// allow dup here can be true/false
			final ListOrderedOfSymbolsWithFastFind children = passedEnv.getExistingListOOSWFF( childrenSymbol );
			children.assumedValid();
			
			
			RunTime.assumedTrue( parents.getAsSymbol() == parentsSymbol );
			RunTime.assumedTrue( children.getAsSymbol() == childrenSymbol );
			
			
			// internal_setAsListOrderedOfSymbolsWFF( passedEnv, existingSelfSymbol );
			ret = new Node( passedEnv, selfSet, parents, children );
			ret.assumedValid();
			registerInstance( passedEnv, selfSet, ret );
		} else {
			ret.assumedValid();
		}
		
		return ret;
	}
	
	
	public static Node getExistingNode( final Level050_DMLEnvironment passedEnv, final Symbol existingSelfSymbol,
										final boolean passedAllowChildrenDUPs ) {
		
		RunTime.assumedNotNull( passedEnv, existingSelfSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		final Node ret = getExistingNode( passedEnv, existingSelfSymbol );
		
		// must match existing instance's allowDUPs else bad call
		if ( ret.isDUPAllowedForChildren() != passedAllowChildrenDUPs ) {
			RunTime.badCall( "A Node already existed with a different setting for allowDUPs!" );
		}
		return ret;
	}
	
	
	/**
	 * @param passedEnv
	 * @param existingSelfSymbol
	 * @param passedAllowChildrenDUPs
	 * @return
	 */
	public static Node getNewNode( final Level050_DMLEnvironment passedEnv, final Symbol existingSelfSymbol,
									final boolean passedAllowChildrenDUPs ) {
		
		RunTime.assumedNotNull( passedEnv, existingSelfSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( isNode( passedEnv, existingSelfSymbol ) ) {
			RunTime.badCall( "existingSelfSymbol is already a Node" );
		}
		
		final DomainSet selfSet = passedEnv.getAsDomainSet( existingSelfSymbol, passedEnv.allListsOOSWFF_Set.getAsSymbol() );
		RunTime.assumedTrue( selfSet.getAsSymbol() == existingSelfSymbol );
		
		if ( selfSet.size() != 0 ) {
			RunTime.bug( "bad existingSelfSymbol. It should have 0 kids" );
		}
		
		RunTime.assumedNull( getInstance( passedEnv, selfSet ) );
		
		final Symbol parentsSymbol = passedEnv.newUniqueSymbol();
		final Symbol childrenSymbol = passedEnv.newUniqueSymbol();
		
		RunTime.assumedNotNull( parentsSymbol );
		RunTime.assumedNotNull( childrenSymbol );
		final ListOrderedOfSymbolsWithFastFind parents = passedEnv.getNewListOOSWFF( parentsSymbol, false );
		parents.assumedValid();
		final ListOrderedOfSymbolsWithFastFind children = passedEnv.getNewListOOSWFF( childrenSymbol, passedAllowChildrenDUPs );
		children.assumedValid();
		RunTime.assumedTrue( parents.getAsSymbol() == parentsSymbol );
		RunTime.assumedTrue( children.getAsSymbol() == childrenSymbol );
		
		RunTime.assumedFalse( selfSet.addToSet( parentsSymbol ) );
		RunTime.assumedFalse( selfSet.addToSet( childrenSymbol ) );
		
		RunTime.assumedFalse( passedEnv.allNodeParents_Set.addToSet( parentsSymbol ) );
		RunTime.assumedFalse( passedEnv.allNodeChildren_Set.addToSet( childrenSymbol ) );
		
		internal_setAsListOrderedOfSymbolsWFF( passedEnv, existingSelfSymbol );
		final Node ret = new Node( passedEnv, selfSet, parents, children );
		// must match existing instance's allowDUPs else bad call
		if ( ret.isDUPAllowedForChildren() != passedAllowChildrenDUPs ) {
			RunTime.bug( "parents list failed to init right?" );
		}
		ret.assumedValid();
		registerInstance( passedEnv, selfSet, ret );
		
		return ret;
	}
	
	
	/**
	 * @param passedEnv
	 * @param existingSelfSymbol
	 * @param passedAllowChildrenDUPs
	 * @return
	 */
	public static Node ensureNode( final Level050_DMLEnvironment passedEnv, final Symbol existingSelfSymbol,
									final boolean passedAllowChildrenDUPs ) {
		
		RunTime.assumedNotNull( passedEnv, existingSelfSymbol );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		
		if ( isNode( passedEnv, existingSelfSymbol ) ) {
			return getExistingNode( passedEnv, existingSelfSymbol, passedAllowChildrenDUPs );
		} else {
			return getNewNode( passedEnv, existingSelfSymbol, passedAllowChildrenDUPs );
		}
	}
	
	
	private static void
			internal_setAsListOrderedOfSymbolsWFF( final Level050_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedSelf, passedEnv );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		// was not set before
		RunTime.assumedFalse( passedEnv.allNodes_Set.addToSet( passedSelf ) );
	}
	
	
	/**
	 * override this and don't call super()
	 */
	protected boolean isItself() {
		
		RunTime.assumedNotNull( selfAsDSet, env );
		RunTime.assumedTrue( env.isInitedSuccessfully() );
		return isNode( env, selfAsDSet.getAsSymbol() );
	}
	
	
	public static boolean isNode( final Level050_DMLEnvironment passedEnv, final Symbol passedSelf ) {
		
		RunTime.assumedNotNull( passedEnv, passedSelf );
		RunTime.assumedTrue( passedEnv.isInitedSuccessfully() );
		return passedEnv.allNodes_Set.hasSymbol( passedSelf );
	}
	
	
	public Symbol getAsSymbol() {
		
		assumedValid();
		return selfAsDSet.getAsSymbol();
	}
	
	
	/**
	 * 
	 */
	public void assumedValid() {
		
		RunTime.assumedNotNull( selfAsDSet, cachedParentsList, cachedChildrenList );
		selfAsDSet.assumedValid();
		RunTime.assumedTrue( isItself() );
		cachedParentsList.assumedValid();
		cachedChildrenList.assumedValid();
		// consistency check: we assume that the Node contents (the 2 kids) don't get changed at this top level
		RunTime.assumedTrue( selfAsDSet.hasSymbol( cachedParentsList.getAsSymbol() ) );
		RunTime.assumedTrue( env.allNodeParents_Set.hasSymbol( cachedParentsList.getAsSymbol() ) );
		RunTime.assumedTrue( selfAsDSet.hasSymbol( cachedChildrenList.getAsSymbol() ) );
		RunTime.assumedTrue( env.allNodeChildren_Set.hasSymbol( cachedChildrenList.getAsSymbol() ) );
		
		
		RunTime.assumedFalse( cachedParentsList.isDUPAllowed() );
		RunTime.assumedFalse( cachedParentsList.isNullAllowed() );
		RunTime.assumedFalse( cachedChildrenList.isNullAllowed() );
	}
	
	
	private final static void
			registerInstance( final Level050_DMLEnvironment env, final DomainSet domainSet, final Node newOne ) {
		
		RunTime.assumedNotNull( env, domainSet, newOne );
		RunTime.assumedFalse( allNodeInstances.ensure( env, domainSet, newOne ) );
	}
	
	
	private final static Node getInstance( final Level050_DMLEnvironment env, final DomainSet domainSet ) {
		
		RunTime.assumedNotNull( env, domainSet );
		return allNodeInstances.get( env, domainSet );
	}
	
	
	public long sizeOfChildren() {
		
		return cachedChildrenList.size();
	}
	
	
	public long sizeOfParents() {
		
		return cachedParentsList.size();
	}
	
	
	/**
	 * (it's not allowed for parents)
	 * 
	 * @return
	 */
	public boolean isDUPAllowedForChildren() {
		
		return cachedChildrenList.isDUPAllowed();
	}
	
	
	// /**
	// * @param new2
	// * @return
	// */
	// public boolean ensureChild( Node child ) {
	//
	// RunTime.assumedNotNull( child );
	// child.assumedValid();
	// this.assumedValid();
	// boolean ret = this.has( NodeType.CHILD, child );
	// if ( !ret ) {
	// this.internal_addChild( child.getAsSymbol() );
	// child.internal_addParent( this.getAsSymbol() );
	// }
	// this.assumedValid();
	// child.assumedValid();
	// return ret;
	// }
	
	// public boolean hasChild( Node child ) {
	//
	// RunTime.assumedNotNull( childrenList, child );
	// return childrenList.hasSymbol( child.getAsSymbol() );
	// }
	//
	// public boolean hasParent( Node parent ) {
	//
	// RunTime.assumedNotNull( parentsList, parent );
	// return parentsList.hasSymbol( parent.getAsSymbol() );
	// }
	
	public boolean has( final NodeType nodeType, final Node whichSymbol ) {
		
		RunTime.assumedNotNull( nodeType, whichSymbol );
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		return list.hasSymbol( whichSymbol.getAsSymbol() );
	}
	
	
	private ListOrderedOfSymbolsWithFastFind getListFor( final NodeType nodeType ) {
		
		RunTime.assumedNotNull( nodeType );
		ListOrderedOfSymbolsWithFastFind list = null;
		switch ( nodeType ) {
		case PARENT:
			list = cachedParentsList;
			break;
		case CHILD:
			list = cachedChildrenList;
			break;
		default:
			RunTime.bug( "impossible" );
		}
		RunTime.assumedNotNull( list );
		return list;
	}
	
	
	/**
	 * @param child
	 * @param first
	 * @return null if none
	 */
	public Node get( final NodeType nodeType, final Position pos ) {
		
		RunTime.assumedNotNull( nodeType, pos );
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		final Symbol sym = list.get( pos );
		if ( null != sym ) {
			return getExistingNode( env, sym );
		} else {
			return null;
		}
	}
	
	
	/**
	 * @param nodeType
	 * @param pos
	 * @param posNode
	 * @return
	 */
	public Node get( final NodeType nodeType, final Position pos, final Node posNode ) {
		
		RunTime.assumedNotNull( nodeType, pos, posNode );
		assumedValid();
		posNode.assumedValid();
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		final Symbol sym = list.get( pos, posNode.getAsSymbol() );
		if ( null != sym ) {
			return getExistingNode( env, sym );
		} else {
			return null;
		}
	}
	
	
	/**
	 * @param nodeType
	 * @param newNode
	 * @param pos
	 * @param posNode
	 */
	public void add( final NodeType nodeType, final Node newNode, final Position pos, final Node posNode ) {
		
		RunTime.assumedNotNull( nodeType, newNode, pos, posNode );
		RunTime.assumedFalse( isDUPAllowedForChildren() );
		assumedValid();
		newNode.assumedValid();
		posNode.assumedValid();
		
		this.internal_add( nodeType, newNode, pos, posNode );
		newNode.internal_append( NodeType.getOpposite( nodeType ), this );
	}
	
	
	public void add( final NodeType nodeType, final Node newNode, final Position pos ) {
		
		RunTime.assumedNotNull( nodeType, newNode, pos );
		
		assumedValid();
		newNode.assumedValid();
		if ( has( nodeType, newNode ) ) {
			RunTime.badCall( "already exists, maybe use ensure method instead" );
		}
		
		this.internal_add( nodeType, newNode, pos );
		newNode.internal_append( NodeType.getOpposite( nodeType ), this );
	}
	
	
	public boolean ensure( final NodeType nodeType, final Node whichNode ) {
		
		RunTime.assumedNotNull( nodeType, whichNode );
		whichNode.assumedValid();
		assumedValid();
		final boolean ret = has( nodeType, whichNode );
		if ( !ret ) {
			internal_append( nodeType, whichNode );
			whichNode.internal_append( NodeType.getOpposite( nodeType ), this );
		}
		assumedValid();
		whichNode.assumedValid();
		return ret;
	}
	
	
	private void internal_append( final NodeType nodeType, final Node newNode ) {
		
		RunTime.assumedNotNull( nodeType, newNode );
		final Symbol sym = newNode.getAsSymbol();
		RunTime.assumedNotNull( sym );
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		RunTime.assumedFalse( list.ensure( sym ) );
	}
	
	
	// TODO remove this method
	private void internal_add( final NodeType nodeType, final Node newNode, final Position pos ) {
		
		RunTime.assumedNotNull( nodeType, newNode, pos );
		final Symbol sym = newNode.getAsSymbol();
		RunTime.assumedNotNull( sym );
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		list.add( sym, pos );
	}
	
	
	// TODO remove this method
	private void internal_add( final NodeType nodeType, final Node newNode, final Position pos, final Node posNode ) {
		
		RunTime.assumedNotNull( nodeType, newNode, pos, posNode );
		final Symbol sym = newNode.getAsSymbol();
		RunTime.assumedNotNull( sym );
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		RunTime.assumedFalse( list.isDUPAllowed() );// because posNode can't be twice or else which we talkin'bout?
		list.add( sym, pos, posNode.getAsSymbol() );
	}
	
	
	/**
	 * @param child
	 * @param after
	 * @param new3
	 * @return what got removed, if any;<br>
	 *         null if none removed
	 */
	public Node remove( final NodeType nodeType, final Position pos, final Node posNode ) {
		
		RunTime.assumedNotNull( nodeType, pos, posNode );
		RunTime.assumedFalse( isDUPAllowedForChildren() );
		posNode.assumedValid();
		assumedValid();
		
		final Node whichNode = this.get( nodeType, pos, posNode );
		if ( null == whichNode ) {
			return null;// didn't exist
		}
		// exists:
		RunTime.assumedTrue( this.remove( nodeType, whichNode ) );
		RunTime.assumedFalse( has( nodeType, whichNode ) );
		return whichNode;
	}
	
	
	/**
	 * @param child
	 * @param first
	 * @return
	 */
	public Node remove( final NodeType nodeType, final Position pos ) {
		
		RunTime.assumedNotNull( nodeType, pos );
		assumedValid();
		final Node whichNode = this.get( nodeType, pos );
		if ( null == whichNode ) {
			return null;// didn't exist anyway
		}
		// existed
		RunTime.assumedTrue( this.remove( nodeType, whichNode ) );
		RunTime.assumedFalse( has( nodeType, whichNode ) );
		return whichNode;
	}
	
	
	/**
	 * @param child
	 * @param new3
	 * @return
	 */
	public boolean remove( final NodeType nodeType, final Node whichNodeToRemove ) {
		
		RunTime.assumedNotNull( nodeType, whichNodeToRemove );
		whichNodeToRemove.assumedValid();
		assumedValid();
		if ( !has( nodeType, whichNodeToRemove ) ) {
			return false;// didn't exist
		}
		// we're here so it exists
		// this->which remove (child of this)
		internal_remove( nodeType, whichNodeToRemove );
		// this<-which remove (parent of which)
		whichNodeToRemove.internal_remove( NodeType.getOpposite( nodeType ), this );
		
		RunTime.assumedFalse( whichNodeToRemove.has( NodeType.getOpposite( nodeType ), this ) );
		RunTime.assumedFalse( has( nodeType, whichNodeToRemove ) );
		return true;
	}
	
	
	private void internal_remove( final NodeType nodeType, final Node whichNodeToRemove ) {
		
		RunTime.assumedNotNull( nodeType, whichNodeToRemove );
		final Symbol sym = whichNodeToRemove.getAsSymbol();
		RunTime.assumedNotNull( sym );
		
		final ListOrderedOfSymbolsWithFastFind list = getListFor( nodeType );
		RunTime.assumedNotNull( list );
		RunTime.assumedTrue( list.remove( sym ) );
	}
	
}
