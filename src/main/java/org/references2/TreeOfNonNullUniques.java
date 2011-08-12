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

package org.references2;



import org.q.*;
import org.references.*;
import org.toolza.*;



/**
 * @param <USEROBJECT>
 *            unique in the entire tree<br>
 *            never null, no nulls or dups allowed in tree<br>
 *            the unicity of user object depends on it's .equals and hashCode(?) methods<br>
 *            so far, two diff instances where i1 != i2 can be equal if their hashCode and equals (or only equals
 *            actually) report that i1.equals(i2) || i2.equals(i1)<br>
 */
public class TreeOfNonNullUniques<USEROBJECT>
{
	
	// List aka Node
	
	// FIXME: make it thread-safe OR make sure it's thread safe
	private final USEROBJECT												rootUserObject;
	private ListOfUniqueLists												rootList;
	// FIXME: possibly two objects that have diff state (since they are equal via equals but they can be diff instances)
	// can exist in the tree in two different node's lists as children and both are sharing the same instance then ? forgot if I
	// fixed this
	private RAMTwoWayHashMapOfNonNullUniques<USEROBJECT, ListOfUniqueLists>	map;
	
	// because the map keeps track only of unique `USEROBJECT`s
	private int																cachedSize	= 0;
	
	
	/**
	 * constructor<br>
	 * 
	 * @param rootNode1
	 *            this object will represent the rootNode
	 */
	public TreeOfNonNullUniques( final USEROBJECT rootNode1 ) {
		assert null != rootNode1;
		rootUserObject = rootNode1;
		rootList = null;
	}
	
	
	// public synchronized
	// void
	// removeSubTree(
	// USEROBJECT passedParentObject )
	// {
	// ListOfUniqueLists list =
	// getListForUserObject( passedParentObject );
	// if ( null == list )
	// {
	// Q.badCall( "should've existed" );
	// }
	//
	// int size;
	// while ( ( size =
	// getChildCount0( passedParentObject ) ) > 0 )
	// {
	// USEROBJECT child =
	// getChildAtIndex(
	// passedParentObject,
	// size - 1 );
	// ListOfUniqueLists childList =
	// getListForUserObject( child );
	// removeSubTree( child );
	// list.removeObject( childList );
	// }
	// // ListOfUniqueLists iter;
	// // while ( null != ( iter =
	// // list.getObjectAt( Position.LAST ) ) )
	// // {
	// // removeSubTree( getUserObjectForList( iter ) );
	// // }
	// passedParentObject ) );// should exist!
	// }
	
	
	/**
	 * this means size()==0 after this<br>
	 */
	public synchronized void clearAllExceptRoot() {
		// System.out.println( "in clear" );
		map.removeAll();
		assert map.size() == 0;
		map = null;
		rootList = null;
		// rootUserObject,// must use the field here
		// getRootList() ) );
		cachedSize = 0;
		// System.out.println( "out of clear" );
		// ListOfUniqueLists root =
		// getRootList();
		// while ( !root.isEmpty() )
		// {
		// root.removeRef( root.getFirstRef() );
		// }
	}
	
	
	public synchronized USEROBJECT getRootUserObject() {
		return getUserObjectForList( getRootList() );
	}
	
	
	private synchronized ListOfUniqueLists getRootList() {
		if ( null == rootList ) {
			rootList = new ListOfUniqueLists();
		}
		assert null != rootList;
		return rootList;
	}
	
	
	private synchronized RAMTwoWayHashMapOfNonNullUniques<USEROBJECT, ListOfUniqueLists> get_OBJ2List_Map() {
		if ( null == map ) {
			map = new RAMTwoWayHashMapOfNonNullUniques<USEROBJECT, ListOfUniqueLists>();
			final boolean tempRet = map.ensureExists( rootUserObject,// must use the field here
				getRootList() );
			assert !tempRet;
			
		}
		assert null != map;
		return map;
	}
	
	
	public synchronized int size() {
		// int size =
		// getMap().size();
		return cachedSize;
		// return size - 1;
	}
	
	
	/**
	 * @param userObj
	 *            not null, but can allow root object
	 * @return true/false
	 */
	public synchronized boolean hasUserObject( final USEROBJECT userObj ) {
		assert null != userObj;
		final boolean has = null != getUserObject( userObj );
		return has;
	}
	
	
	/**
	 * @param userObj
	 *            ie. `new`-ed object (not null!)
	 * @return the actual user object which happens to .equals(userObj) but may not necessarily be the same instance<br>
	 *         this returned object exists in tree, but the passed userObj may or may not already exist in tree since it
	 *         was created using `new` it most likely doesn't exist in tree<br>
	 */
	public synchronized USEROBJECT getUserObject( final USEROBJECT userObj ) {
		assert null != userObj;
		final ListOfUniqueLists list = getListForUserObject( userObj );
		if ( null == list ) {
			return null;
		} else {
			return getUserObjectForList( list );
		}
	}
	
	
	private synchronized void onAdd() {
		assert size() >= 0;
		cachedSize++;
	}
	
	
	@SuppressWarnings( "unused" )
	private synchronized void onRemove() {
		assert size() > 0;
		cachedSize--;
	}
	
	
	public synchronized MethodReturnsForTree
			isChildAtPos( final USEROBJECT child, final USEROBJECT toParent, final Position pos ) {
		assert null != child;
		assert Position.isFirstOrLast( pos );
		assert !Z.equalsWithCompatClasses_allowsNull( child, toParent ) : Q.badCall( "the same object `" + child + "`.equals(`"
			+ toParent + "`) cannot be both parent and child to itself" );
		// }
		final ListOfUniqueLists parentList = getListForUserObject( toParent );
		assert null != parentList :
		// if ( null == parentList ) {
		Q.badCall( "the parent object `" + toParent + "` didn't exist in tree" );
		// return MethodReturnsForTree.FAIL;// to avoid null warnings below; this isn't reached though!
		// }
		final ListOfUniqueLists childList = getListForUserObject( child );
		MethodReturnsForTree ret = MethodReturnsForTree.UNSET;
		if ( null != childList ) {
			// child exists already
			// we check if it's in the right place
			// first the parentList must have at least 1 child
			final int len = parentList.size();
			assert len >= 0;
			if ( len < 1 ) {// this parent has no children, so:
				ret = MethodReturnsForTree.HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_PARENT;
			} else {// so it has 1 child or more, then we check if our childList is at expected position aka LAST
				if ( childList != parentList.getObjectAt( pos ) ) {// it's not in that expected position in parent
					if ( parentList.containsObject( childList ) ) {// but it is in parent
						ret = MethodReturnsForTree.HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_POSITION_IN_SAME_PARENT;
					} else {// and it's not even in that parent anyway
						ret = MethodReturnsForTree.HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_PARENT;
					}
				} else {// IT is in that exact expected position in parent
					ret = MethodReturnsForTree.SUCCESS_BUT_ALREADY_EXISTED_WHERE_EXPECTED;// else exists
				}
			}
		} else {// child didn't already exist
			ret = MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST;
		}
		
		assert MethodReturnsForTree.UNSET != ret;
		return ret;
	}
	
	
	/**
	 * @param child
	 *            can't be null, the child to be added
	 * @param toParent
	 *            if null then root is considered parent<br>
	 *            this can be a different object than the object alredy existing in the tree, as long as it's .equals()
	 *            makes them equal... see FinderObjectTest.java in junit tests src folder, it's xFinderInst variable<br>
	 * @param pos
	 * @return see {@link MethodReturnsForTree}<br>
	 *         if the child exists but in a different place than specified/wanted with the
	 *         params, then nothing is changes and return is
	 *         MethodReturns.HALF_SUCCESS_BECAUSE_EXISTED_NOT_WHERE_EXPECTED<br>
	 */
	public synchronized MethodReturnsForTree addChildInParentAtPos( final USEROBJECT child, final USEROBJECT toParent,
																	final Position pos ) {
		// S.entry();
		// try
		// {
		assert null != child;
		assert Position.isFirstOrLast( pos );
		assert !Z.equalsWithCompatClasses_allowsNull( child, toParent ) : Q
			.badCall( "cannot add the same object (.equals()) as child to itself" );
		// }
		final MethodReturnsForTree ret = isChildAtPos( child, toParent, pos );
		if ( MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST != ret ) {
			return ret;// ie. already existed where expected, or existed somewhere else OR FAIL(well not in this case)
		}
		ListOfUniqueLists childList = getListForUserObject( child );
		final ListOfUniqueLists parentList = getListForUserObject( toParent );
		assert null != parentList;// due to isChildAtPos() call above, it it is null that one woulda failed first
		assert null == childList :
		// if ( null != childList ) {
		Q.bug( "bug in previous call to isChildAtPos(), because childList should be null here aka inexistent yet" );
		// }
		// child is new
		// TODO: txbegin
		childList = new ListOfUniqueLists();
		parentList.addObjectAtPosition( pos, childList );
		onAdd();
		final boolean existed = get_OBJ2List_Map().ensureExists( child, childList );
		assert !existed :
		// if ( existed ) {
		Q.bug( "the `child` and the new childList should not have already existed, because we determined so far "
			+ "that there was no list 1-to-1 mapped to `child` prior to being here" );
		// }
		assert !existed;// else bug somewhere
		// TODO: txend
		assert MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST == ret;
		return ret;
		// }
		// finally
		// {
		// S.exit();
		// }
	}
	
	
	/**
	 * @param parent
	 *            null means root
	 * @return number of children that parent has
	 */
	public synchronized int getChildCount0( final USEROBJECT parent ) {
		// S.entry();
		final ListOfUniqueLists parentList = getListForUserObject( parent );
		assert null != parentList;
		final int size = parentList.size();
		// S.exit();
		return size;
	}
	
	
	/**
	 * @param userObject
	 * @return null or the list
	 */
	private synchronized ListOfUniqueLists getListForUserObject( final USEROBJECT userObject ) {
		final ListOfUniqueLists retList = get_OBJ2List_Map().getData( null == userObject ? getRootUserObject() : userObject );
		return retList;// can be null
	}
	
	
	/**
	 * @param list
	 *            must not be null
	 * @return
	 */
	private synchronized USEROBJECT getUserObjectForList( final ListOfUniqueLists list ) {
		assert null != list;
		final USEROBJECT node = get_OBJ2List_Map().getKey( list );
		assert null != node :
		// if ( null == node ) {
		Q.badCall( "that list was not 1-to-1 mapped to any userobject aka node" );
		// return null;// not reached
		// }
		return node;
		// }
	}
	
	
	/**
	 * @param parentObject
	 *            if null then root is considered
	 * @param indexInParentObject
	 *            should be within range or throws
	 * @return the child, or throws!
	 */
	public synchronized USEROBJECT getChildAtIndex( final USEROBJECT parentObject, final int indexInParentObject ) {
		final ListOfUniqueLists parentList = getListForUserObject( parentObject );
		assert null != parentList;
		assert assumedValidIndex( parentList, indexInParentObject );
		final ListOfUniqueLists childList = parentList.getObjectAt( indexInParentObject );
		assert ( null != childList ) : Q.bug( "not found and index was valid ? maybe bug in getObjectAt()?" );
		return getUserObjectForList( childList );
	}
	
	
	/**
	 * @param parent
	 * @param index
	 */
	private boolean assumedValidIndex( final ListOfUniqueLists nodeList, final int index ) {
		assert null != nodeList;
		if ( ( index >= nodeList.size() ) || ( index < 0 ) ) {
			Q.badCall( "invalid index!" );
		}
		return true;
	}
	
	
	/**
	 * @param parentObject
	 *            null for root
	 * @param childObject
	 * @return index of child in parent
	 */
	public synchronized int getIndexOfChild0( final USEROBJECT parentObject, final USEROBJECT childObject ) {
		assert null != childObject;
		assert !Z.equalsWithCompatClasses_allowsNull( childObject, parentObject ) :
		// if ( ) {
		Q.badCall( "cannot have the same object as both parent and child to itself!" );
		// }
		final ListOfUniqueLists parentList = getListForUserObject( parentObject );
		final ListOfUniqueLists childList = getListForUserObject( childObject );
		assert null != parentList;
		assert null != childList;
		return parentList.getIndexOfObject( childList );
	}
	
	
	/**
	 * @param child
	 *            cannot be null because root has no parent
	 * @param parent
	 *            can be null to signal it's root
	 * @return true/false
	 */
	public synchronized boolean isChildParent( final USEROBJECT child, final USEROBJECT parent ) {
		assert null != child;
		assert
		// if (
		!Z.equalsWithCompatClasses_allowsNull( child, parent ) :
		// ) {
		Q.badCall( "cannot have the same object as both parent and child to itself!" );
		// }
		final ListOfUniqueLists childList = getListForUserObject( child );
		assert null != childList;// should alredy exist
		final ListOfUniqueLists parentList = getListForUserObject( parent );
		assert null != parentList;
		return parentList.containsObject( childList );
	}
	
	
	/**
	 * moves the existingChild from root parent to existingParent<br>
	 * it's moved to last child in existingParent<br>
	 * 
	 * @param existingChild
	 *            never null
	 * @param existingParent
	 *            never null
	 * @param pos
	 */
	public synchronized void moveChildFromRootToParent( final USEROBJECT existingChild, final USEROBJECT existingParent,
														final Position pos ) {
		assert null != existingChild;
		assert null != existingParent;// cannot be null to signal it's root, because of what this method does
		assert null != pos;
		assert Position.isFirstOrLast( pos );
		assert !Z.equalsWithCompatClasses_allowsNull( getRootUserObject(), existingParent ) : Q
			.badCall( "you passed root as parent" );
		// }
		assert !Z.equalsWithCompatClasses_enforceNotNull( existingChild, existingParent ) : Q
			.badCall( "cannot have the same object as both parent and child to itself!" );
		// }
		
		
		// null means root, so root has the child
		assert isChildParent( existingChild, null ) : Q.badCall( "you assumed that root already has that child,"
			+ " but it didn't!" );
		final ListOfUniqueLists childList = getListForUserObject( existingChild );
		final ListOfUniqueLists parentList = getListForUserObject( existingParent );
		assert !parentList.containsObject( childList );// it cannot containt it, if root already is
		final ListOfUniqueLists rootList1 = getRootList();
		assert !Z.equals_enforceExactSameClassTypesAndNotNull( rootList1, parentList );
		// txbegin
		assert rootList1.containsObject( childList );
		assert !parentList.containsObject( childList );
		
		final boolean tempRet = parentList.addObjectAtPosition( pos, childList );
		assert !tempRet;
		
		assert !rootList1.containsObject( childList );
		assert parentList.containsObject( childList );
		// txend
	}
}
