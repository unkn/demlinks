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



package org.dml.level010;



import org.dml.storagewrapper.StorageException;
import org.dml.tools.MainLevel0;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;
import org.dml.tracking.Factory;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * handling JavaIDs and Symbols<br>
 * one to one mapping between JavaIDs and Symbols<br>
 * no JavaID exists w/o having a Symbol associated with it<br>
 * Symbols may exist w/o having a JavaID associated with them<br>
 * only 1 JavaID may be connected to any one Symbol<br>
 * 
 * Why is JavaID needed? to be able to refer to the same Symbol across
 * application restarts<br>
 * 
 */
public class Level010_DMLEnvironment extends MainLevel0 implements Level010_DMLStorageWrapper {
	
	@VarLevel
	private final Level010_DMLStorageWrapper	storage				= null;
	
	private boolean								usedDefaultStorage	= false;
	
	/**
	 * construct, don't forget to call init(with param/s)
	 */
	public Level010_DMLEnvironment() {

		super();
		
	}
	
	/**
	 * this will be called on init, when no storage was specified or passed to us on init<br>
	 * override but NEVER call super!<br>
	 */
	protected void internal_allocDefaultStorage( MethodParams params ) {

		// MethodParams<Object> storageParams = new MethodParams<Object>();
		// storageParams.init( null );
		// storageParams.set( PossibleParams.homeDir, Consts.BDB_ENV_PATH );
		// storageParams.set( PossibleParams.jUnit_wipeDBWhenDone, true );
		// storageParams.deInit();
		
		// Level010_DMLStorage_BerkeleyDB stor = new Level010_DMLStorage_BerkeleyDB();
		// stor.init( params );
		Level010_DMLStorage_BerkeleyDB stor = Factory.getNewInstanceAndInit( Level010_DMLStorage_BerkeleyDB.class,
				params );
		params.set( PossibleParams.varLevelAll, stor );
		// but reInit() or restart() won't see this set varlevel, although it will exec start() again and it will be set
		// again
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#start(org.references.method.MethodParams)
	 */
	@Override
	protected void start( MethodParams params ) {

		if ( ( null == params ) || ( null == params.get( PossibleParams.varLevelAll ) ) ) {
			// need own default storage type new-ed and init-ed
			this.internal_allocDefaultStorage( params );
			usedDefaultStorage = true;// must remain true, in case of restart() or reInit() I think?
		}
		super.start( params );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#done(org.references.method.MethodParams)
	 */
	@Override
	protected void done( MethodParams params ) {

		if ( usedDefaultStorage ) {
			RunTime.assumedNotNull( storage );// wicked if null
			// storage.deInit();
			// Factory.deInit( storage );fail 'cause it's interface
			storage.factoryDeInit();
			// don't set var to false here, or seems it doesn't matter, reInit() will call start() and set it it true
			// anyway
		}
		super.done( params );
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
	public Symbol getSymbol( JavaID identifiedByThisJavaID ) throws StorageException {

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
	public boolean ensureLink( Symbol symbol, JavaID jid ) {

		return storage.ensureLink( symbol, jid );
	}
}
