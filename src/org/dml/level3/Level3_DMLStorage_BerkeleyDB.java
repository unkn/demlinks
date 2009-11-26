/**
 * File creation: Jun 17, 2009 6:54:03 PM
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


package org.dml.level3;



import org.dml.level2.Level2_DMLStorage_BerkeleyDB;
import org.references.method.MethodParams;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level3_DMLStorage_BerkeleyDB extends Level2_DMLStorage_BerkeleyDB
		implements Level3_DMLStorageWrapper {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_DMLStorage_BerkeleyDB#start(org.references.method
	 * .MethodParams)
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		// this method is not needed, but it's here for clarity
		super.start( params );
	}
	

}// end of class
