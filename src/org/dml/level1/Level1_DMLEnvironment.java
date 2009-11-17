/**
 * File creation: May 30, 2009 12:16:28 AM
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


package org.dml.level1;



import org.dml.storagewrapper.StorageException;
import org.dml.tools.MainLevel0;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;
import org.references.method.MethodParams;



/**
 * handling Symbols
 * 
 */
public class Level1_DMLEnvironment extends MainLevel0 implements
		Level1_DMLStorageWrapper {
	
	@VarLevel
	private final Level1_DMLStorage_BerkeleyDB	storage	= null;
	
	
	/**
	 * construct, don't forget to call init(with param/s)
	 */
	public Level1_DMLEnvironment() {

		super();
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#start(org.references.method.MethodParams)
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		// this method is not needed, but it's here for clarity
		super.start( params );
	}
	
	// ---------------------------------------------
	/**
	 * there's a one to one mapping between NodeID and NodeJavaID<br>
	 * given the NodeID return its NodeJavaID<br>
	 * NodeIDs are on some kind of Storage<br>
	 * 
	 * @param symbol
	 * @return NodeJavaID
	 * @throws StorageException
	 */
	public JavaID getJavaID( Symbol symbol )
			throws StorageException {

		RunTime.assertNotNull( symbol );
		return storage.getJavaID( symbol );
	}
	
	/**
	 * eget=ensure get<br>
	 * make a new one if it doesn't exist<br>
	 * but if exists don't complain<br>
	 * 
	 * @param theJavaID
	 *            this JavaID and this Node will be mapped 1 to 1
	 * @return never null
	 * @throws StorageException
	 */
	public Symbol ensureSymbol( JavaID theJavaID )
			throws StorageException {

		RunTime.assertNotNull( theJavaID );
		return storage.ensureSymbol( theJavaID );
	}
	
	/**
	 * @param identifiedByThisJavaID
	 * @throws StorageException
	 */
	public Symbol getSymbol( JavaID identifiedByThisJavaID )
			throws StorageException {

		return storage.getSymbol( identifiedByThisJavaID );
	}
	
	/**
	 * @param fromJavaID
	 * @return
	 * @throws StorageException
	 */
	public Symbol createSymbol( JavaID fromJavaID )
			throws StorageException {

		return storage.createSymbol( fromJavaID );
	}
	
}
