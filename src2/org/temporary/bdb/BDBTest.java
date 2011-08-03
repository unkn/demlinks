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



package org.temporary.bdb;



import org.dml.JUnits.*;
import org.dml.database.bdb.level1.*;
import org.dml.level010.*;
import org.dml.tracking.*;
import org.references.method.*;

import com.sleepycat.db.*;



/**
 * 
 *
 */
public class BDBTest {
	
	public static void main( final String[] args ) throws DatabaseException {
		
		MethodParams params = MethodParams.getNew();
		// params.init( null );
		params.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		params.set( PossibleParams.jUnit_wipeDB, Boolean.TRUE );
		params.set( PossibleParams.jUnit_wipeDBWhenDone, Boolean.TRUE );
		Level1_Storage_BerkeleyDB b;
		// new Level1_Storage_BerkeleyDB();
		// b.init( params );
		b = Factory.getNewInstanceAndInit( Level1_Storage_BerkeleyDB.class, params );
		// params.deInit();
		// Factory.deInit( params );
		params = null;
		Symbol n1, n2, f1, f2;
		try {
			final JavaID fromJavaID = JavaID.ensureJavaIDFor( "duh" );
			n1 = b.getDBMap_JavaIDs_To_Symbols().createSymbol( fromJavaID );
			n2 = b.getDBMap_JavaIDs_To_Symbols().getSymbol( fromJavaID );
			final JavaID jid2 = JavaID.ensureJavaIDFor( "duh2" );
			f1 = b.getDBMap_JavaIDs_To_Symbols().createSymbol( jid2 );
			f2 = b.getDBMap_JavaIDs_To_Symbols().getSymbol( jid2 );
			
			System.out.println( b.getDBMap_JavaIDs_To_Symbols().getJavaID( f1 ) );
		} finally {
			Factory.deInit( b );
			// b.deInit();
		}
		System.out.println( "end. " + n1 + "==" + n2 );
		System.out.println( "end. " + f1 + "==" + f2 );
	}
}
