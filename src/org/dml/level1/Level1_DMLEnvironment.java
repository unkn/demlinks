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
 * handling JavaIDs and Symbols
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
	
	@Override
	public JavaID getJavaID( Symbol symbol ) throws StorageException {

		RunTime.assumedNotNull( symbol );
		return storage.getJavaID( symbol );
	}
	
	@Override
	public Symbol ensureSymbol( JavaID theJavaID ) throws StorageException {

		RunTime.assumedNotNull( theJavaID );
		return storage.ensureSymbol( theJavaID );
	}
	
	@Override
	public Symbol getSymbol( JavaID identifiedByThisJavaID )
			throws StorageException {

		return storage.getSymbol( identifiedByThisJavaID );
	}
	
	@Override
	public Symbol createSymbol( JavaID fromJavaID ) throws StorageException {

		return storage.createSymbol( fromJavaID );
	}
	
	@Override
	public Symbol newUniqueSymbol() throws StorageException {

		return storage.newUniqueSymbol();
	}
	
	@Override
	public void newLink( Symbol noID, JavaID jid ) {

		storage.newLink( noID, jid );
	}
	
	@Override
	public boolean ensureLink( Symbol noID, JavaID jid ) {

		return storage.ensureLink( noID, jid );
	}
}
