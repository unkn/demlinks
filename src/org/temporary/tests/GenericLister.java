/**
 * File creation: Oct 24, 2009 9:50:53 PM
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


package org.temporary.tests;



import org.dml.tools.RunTime;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;



/**
 * 
 *
 */
public class GenericLister<T> {
	
	protected final ListOfUniqueNonNullObjects<T>	list	= new ListOfUniqueNonNullObjects<T>();
	
	/**
	 * @param s
	 */
	public void addFirst( T obj ) {

		this.checkInvariants();
		list.addFirst( obj );
	}
	
	/**
	 * @param es
	 */
	public void append( T obj ) {

		this.checkInvariants();
		list.addLast( obj );
	}
	
	public void checkInvariants() {

	}
	
	public boolean replaceFirst( T obj ) {

		this.checkInvariants();
		return false;
		
		// return this.replace( 0, obj );
		// list.replaceFirst(obj);
		// TODO
	}
	
	public boolean replaceLast( T obj ) {

		this.checkInvariants();
		return false;
		
		// return replace(list.size()-1, obj); if size() is 0 check!
		// list.replaceLast(obj);
		// TODO
	}
	
	public boolean replace( int index, T obj ) {

		this.checkInvariants();
		return false;
		
		// TODO
	}
	
	public int compareTo( GenericLister<T> toThis,
			GenericListerComparator<T> usingComparator ) {

		RunTime.assumedNotNull( usingComparator );
		return usingComparator.compareTwo( this, toThis );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {

		boolean ret = super.equals( obj );
		if ( !ret ) {
			// ret=100==this.compareTo( obj, usingComparator )
			// TODO
		}
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String str = this.getClass().getSimpleName() + ": ";
		T iter = list.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			str += iter.toString() + ", ";
			iter = list.getObjectAt( Position.AFTER, iter );
		}
		return str + ".";
	}
}
