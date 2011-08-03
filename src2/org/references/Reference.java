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



package org.references;



/**
 * 
 * NOTE: don't implement Factory/Initer to this
 * 
 * @param <T>
 */
public class Reference<T> // extends Object>
{
	
	// public transient volatile static Object unset = new Object();
	
	private T	object	= null;
	
	
	public Reference(
			T obj )
	{
		object = obj;
	}
	

	public Reference()
	{
		
		// constructor
		// this.setUnSet();
	}
	

	@Deprecated
	public
			boolean
			isUnSet()
	{
		return ( null == object );// don't use this.getObject() here else recursion
	}
	

	@Deprecated
	public
			void
			setUnSet()
	{
		object = null;
		// this.setObject( null );bad idea
	}
	

	public
			void
			setObject(
						T obj )
	{
		
		object = obj;
	}
	

	/**
	 * @return the object that this reference refers to
	 */
	public
			T
			getObject()
	{
		
		return object;
	}
	

	
	/**
	 * compares by contents that is:<br>
	 * refa.equals(refb) if refa.object == refb.object OR refa.object.equals(refb.object)<br>
	 * so if the object references aren't equal the .equals() on objects is invoked
	 * 
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public
			boolean
			equals(
					Object obj1 )
	{
		
		boolean ret = true;
		if ( !super.equals( obj1 ) )
		{
			ret = false;
			if ( null != obj1 )
			{
				if ( obj1.getClass() == this.getClass() )
				{
					T thisObj = this.getObject();
					T thatO = ( (Reference<T>)obj1 ).getObject();
					if ( thisObj == thatO )
					{
						ret = true;
					}
					else
					{
						if ( thisObj != null )
						{
							if ( thisObj.equals( thatO ) )
							{
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
	public
			int
			hashCode()
	{
		
		// TODO: maybe add hashCode() to all other .equals() that are overridden
		if ( null != this.getObject() )
		{
			return this.getObject().hashCode();
		}
		else
		{
			return super.hashCode();
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public
			String
			toString()
	{
		
		if ( null != this.getObject() )
		{
			return this.getClass().getCanonicalName()
					+ ":"
					+ this.getObject().toString();
		}
		else
		{
			return this.getClass().getCanonicalName()
					+ ":null";
		}
	}
}
