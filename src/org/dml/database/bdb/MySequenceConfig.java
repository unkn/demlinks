/**
 * File creation: Jun 3, 2009 1:21:29 PM
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


package org.dml.database.bdb;



import com.sleepycat.je.SequenceConfig;



/**
 * 
 *
 */
public class MySequenceConfig extends SequenceConfig {
	
	public MySequenceConfig() {

		super();
		this.setAllowCreate( true );
		this.setAutoCommitNoSync( false );
		this.setWrap( true );
		this.setInitialValue( 0 );
		this.setExclusiveCreate( false );
		this.setCacheSize( 0 );// when 0 can use non-null transactions with
		// .get(,)
	}
}
