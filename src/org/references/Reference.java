/**
 * File creation: Oct 27, 2009 6:23:12 AM
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


package org.references;



import java.io.Serializable;



/**
 * 
 *
 */
public class Reference<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4067289925841216474L;
	private T					object				= null;
	
	public Reference() {

	}
	
	/**
	 * @return the object that this reference refers to
	 */
	public void setObject( T obj ) {

		object = obj;
	}
	
	public T getObject() {

		return object;
	}
	
	
	@SuppressWarnings( "unchecked" )
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object obj1 ) {

		boolean ret = true;
		if ( !super.equals( obj1 ) ) {
			ret = false;
			if ( null != obj1 ) {
				if ( obj1.getClass() == this.getClass() ) {
					T thisObj = this.getObject();
					T thatO = ( (Reference<T>)obj1 ).getObject();
					if ( thisObj == thatO ) {
						ret = true;
					} else {
						if ( thisObj != null ) {
							if ( thisObj.equals( thatO ) ) {
								ret = true;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		// TODO: maybe add hashCode() to all other .equals() that are overridden
		if ( null != this.getObject() ) {
			return this.getObject().hashCode();
		} else {
			return super.hashCode();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		if ( null != this.getObject() ) {
			return this.getObject().toString();
		} else {
			return "null";
		}
	}
}
