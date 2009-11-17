/**
 * File creation: Oct 19, 2009 11:30:51 PM
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



import org.dml.level1.Symbol;
import org.dml.tools.RunTime;



/**
 * list of NodeIDs in which order matters and it's known<br>
 * should be able to hold any number of NodeIDs even if they repeat inside the
 * list<br>
 * the order of insertion is kept<br>
 * this will be a double linked list represented in DMLEnvironment<br>
 * this is level 4
 */
public class ListID {
	
	Level3_DMLEnvironment	l3DMLEnvironment;
	Symbol					name;
	
	public ListID( Level3_DMLEnvironment l3_DMLEnv, Symbol name1 ) {

		RunTime.assertNotNull( l3_DMLEnv, name1 );
		l3DMLEnvironment = l3_DMLEnv;
		
		name = name1;
		// Symbol listSymbol = l3DMLEnvironment.getSymbol(
		// Level3_DMLEnvironment.listSymbolJavaID );
		l3DMLEnvironment.ensureVector( l3DMLEnvironment.allListsSymbol, name );
	}
	
	
	public boolean isValid() {

		return l3DMLEnvironment.isVector( l3DMLEnvironment.allListsSymbol, name );
	}
	

}
