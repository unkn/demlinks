/**
 * File creation: Oct 19, 2009 3:26:40 AM
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


package org.temporary.bdb;



import org.dml.JUnits.Consts;
import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.level1.Symbol;
import org.dml.level1.NodeJavaID;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.DatabaseException;



/**
 * 
 *
 */
public class BDBTest {
	
	public static void main( String[] args ) throws DatabaseException {

		MethodParams<Object> params = new MethodParams<Object>();
		params.init( null );
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, true );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		Level1_Storage_BerkeleyDB b = new Level1_Storage_BerkeleyDB();
		b.init( params );
		params.deInit();
		params = null;
		Symbol n1, n2, f1, f2;
		try {
			NodeJavaID fromJavaID = NodeJavaID.ensureJavaIDFor( "duh" );
			n1 = b.getDBMap_JavaIDs_To_NodeIDs().createSymbol( fromJavaID );
			n2 = b.getDBMap_JavaIDs_To_NodeIDs().getSymbol( fromJavaID );
			NodeJavaID jid2 = NodeJavaID.ensureJavaIDFor( "duh2" );
			f1 = b.getDBMap_JavaIDs_To_NodeIDs().createSymbol( jid2 );
			f2 = b.getDBMap_JavaIDs_To_NodeIDs().getSymbol( jid2 );
			
			System.out.println( b.getDBMap_JavaIDs_To_NodeIDs().getNodeJavaID(
					f1 ) );
		} finally {
			b.deInit();
		}
		System.out.println( "end. " + n1 + "==" + n2 );
		System.out.println( "end. " + f1 + "==" + f2 );
	}
}
