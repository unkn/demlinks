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
 * File creation: Sep 9, 2010 12:47:49 PM
 */


package org.dml.database.bdb.level2;



import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.IniterInterface;



/**
 * @param <ELEMENT>
 * 
 * 
 */
public interface VectorIterator<ELEMENT>
{
	
	public
			void
			goFirst();
	

	public
			void
			goTo(
					ELEMENT element );
	

	public
			ELEMENT
			now();
	

	public
			void
			goNext();
	

	public
			void
			goPrev();
	

	public
			long
			count();
	

	public
			boolean
			delete();
	

	public
			Level1_Storage_BerkeleyDB
			getBDBL1();
	

	public
			void
			close();
}
