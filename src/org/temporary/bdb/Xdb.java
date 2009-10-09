/**
 * File creation: May 29, 2009 2:48:33 PM
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



import java.io.File;

import org.omg.CORBA.Environment;



/**
 * 
 *
 */
public class Xdb {
	
	Environment			env;
	static final String	homeDir	= "c:/sometmp";
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {

		Xdb instax = null;
		try {
			instax = new Xdb();
			instax.run();
		} catch ( Exception e ) {
			
		} finally {
			instax.done();
		}
		
	}
	
	/**
	 * 
	 */
	private void done() {

		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @throws DatabaseException
	 * @throws EnvironmentLockedException
	 * 
	 */
	private void run() throws EnvironmentLockedException, DatabaseException {

		/* Open the environment. */
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate( true );
		envConfig.setTransactional( true );
		env = new Environment( new File( homeDir ), envConfig );
		
	}
}
