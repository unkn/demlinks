/**
 * File creation: Oct 24, 2009 12:10:12 PM
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


package org.dml.tools;



import java.io.Serializable;



/**
 * 
 *
 */
public class Encapsulated<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6093951622384455765L;
	private T					obj;
	
	/**
	 * @param string
	 */
	public void encapsulateThis( T obj1 ) {

		obj = obj1;
		
	}
	
	public T getEncapsulated() {

		return obj;
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
					T thisEnc = this.getEncapsulated();
					T thatEnc = ( (Encapsulated<T>)obj1 ).getEncapsulated();
					if ( thisEnc == thatEnc ) {
						ret = true;
					} else {
						if ( thisEnc != null ) {
							if ( thisEnc.equals( thatEnc ) ) {
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
		if ( null != obj ) {
			return obj.hashCode();
		} else {
			return super.hashCode();
		}
	}
}
