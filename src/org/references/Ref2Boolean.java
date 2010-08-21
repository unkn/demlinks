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
 * File creation: Aug 19, 2010 11:40:50 PM
 */


package org.references;



import org.dml.tools.RunTime;



/**
 * 
 *
 */
@Deprecated
public class Ref2Boolean
		extends
		Reference<Boolean>
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.references.Reference#getObject()
	 */
	@Override
	public
			Boolean
			getObject()
	{
		// Boolean b = super.getObject();
		if ( this.isUnSet() )
		{
			// RunTime.assumedNotNull( b );
			RunTime.badCall( "boolean was never set" );
		}
		return super.getObject();
	}
}
