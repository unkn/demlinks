/**
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
 * 
 * 
 * File creation: Jul 5, 2010 1:55:25 AM
 */


package org.references;



import org.dml.tools.RunTime;



/**
 * 
 * NOTE: the objects must be non-null except when the current tree has no parent aka null parent then value can be null<br>
 * objects can repeat, so no checks are performed for dups in any direction horiz/vert<br>
 * objects are stored as the field 'value' below<br>
 */
public class TreeOfNonNullObjects<T> {
	
	private ListOfUniqueNonNullObjects<TreeOfNonNullObjects<T>>	children	= null;
	
	// null only when this tree is root or head
	private TreeOfNonNullObjects<T>								parent		= null;
	
	// the value must not be null; it's null only when this tree is the root or head
	private T													value		= null;
	
	/**
	 * constructor<br>
	 * use this when init-ing the head or root of the tree<br>
	 * parent and value are both null<br>
	 */
	public TreeOfNonNullObjects() {

	}
	
	/**
	 * constructor<br>
	 * use this when init-ing subtrees, aka not the root or head of the tree<br>
	 * 
	 * @param value1
	 *            non-null value aka object
	 */
	public TreeOfNonNullObjects( TreeOfNonNullObjects<T> parentTree, T objectValue ) {

		RunTime.assumedNotNull( parentTree, objectValue );
		this.setValue( objectValue );// 1st
		this.setParent( parentTree );// 2nd
		RunTime.assumedNotNull( value );// constraint
	}
	
	/**
	 * @return never null
	 */
	private ListOfUniqueNonNullObjects<TreeOfNonNullObjects<T>> getChildren() {

		// init on demand
		if ( null == children ) {
			children = new ListOfUniqueNonNullObjects<TreeOfNonNullObjects<T>>();
		}
		RunTime.assumedNotNull( children );
		return children;
	}
	
	/**
	 * @param newParent
	 *            can be null
	 */
	private void setParent( TreeOfNonNullObjects<T> newParent ) {

		RunTime.assumedNotNull( newParent, value );
		parent = newParent;
	}
	
	public TreeOfNonNullObjects<T> getParent() {

		return parent;// can be null if this is root aka head of tree
	}
	
	/**
	 * this will add as first in list of children<br>
	 * 
	 * @param child
	 *            non-null
	 */
	public TreeOfNonNullObjects<T> addChildFirst( T childValue ) {

		RunTime.assumedNotNull( childValue );
		TreeOfNonNullObjects<T> newChildInCurrent = new TreeOfNonNullObjects<T>( this, childValue );
		if ( this.getChildren().addFirstQ( newChildInCurrent ) ) {
			RunTime.bug( "subtree already exists, impossible" );
		}
		return newChildInCurrent;
	}
	
	/**
	 * @param pos
	 *            only Position.FIRST and LAST
	 * @return null if none
	 */
	public TreeOfNonNullObjects<T> getChildAt( Position pos ) {

		TreeOfNonNullObjects<T> subTree = null;
		if ( null != children ) {
			subTree = this.getChildren().getObjectAt( pos );
			// can be null here too
		}
		return subTree;// can be null;
	}
	
	/**
	 * @param value1
	 *            the value to set; can be null
	 */
	private void setValue( T value1 ) {

		RunTime.assumedNotNull( value1 );
		this.value = value1;
	}
	
	/**
	 * @return the value
	 */
	public T getValue() {

		RunTime.assumedNotNull( value );// not allowed
		return value;// can not be null
	}
	
	/**
	 * @param childAKASubTree
	 * @return true if existed; false if didn't exist; either way it's removed
	 */
	public boolean removeChild( TreeOfNonNullObjects<T> childAKASubTree ) {

		RunTime.assumedNotNull( childAKASubTree );
		boolean res = false;
		
		if ( null != children ) {
			res = this.getChildren().removeObject( childAKASubTree );
		}
		
		return res;
	}
	
	/**
	 * @return true if no children aka children list is empty
	 */
	public boolean isEmpty() {

		if ( null != children ) {
			return this.getChildren().isEmpty();
		} else {
			return true;
		}
	}
	
	/**
	 * @return
	 */
	public int size() {

		if ( this.isEmpty() ) {
			return 0;
		}
		return this.getChildren().size();
	}
}
