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
 * File creation: Aug 8, 2010 5:57:20 PM
 */


package org.dml.tracking;



import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class ShutDownHook {
	
	public ShutDownHook( Thread hookThread ) {

		RunTime.assumedNotNull( hookThread );
		Runtime.getRuntime().addShutdownHook( hookThread );
		Log.special( "added shutdown-hook Factory" );
	}
}
