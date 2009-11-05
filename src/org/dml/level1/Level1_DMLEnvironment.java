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



import org.dml.level2.Level2_DMLStorageWrapper;
import org.dml.tools.MainLevel0;
import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.references.method.MethodParams;



/**
 * facade design pattern
 * 
 */
public class Level1_DMLEnvironment extends MainLevel0 {
	
	
	private Level1_DMLStorageWrapper	Storage	= null;
	
	/**
	 * construct, don't forget to call init()
	 */
	public Level1_DMLEnvironment() {

		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.tools.StaticInstanceTrackerWithMethodParams#init(org.references
	 * .method.MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		super.init( this.internalInit( Storage, params ) );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#checkVarLevelX(java.lang.Object)
	 */
	@Override
	protected void checkVarLevelX( Object obj ) {

		if ( !( obj instanceof Level2_DMLStorageWrapper ) ) {
			// cannot be under Level1_DMLStorageWrapper, can be above tho
			RunTime.badCall( "wrong type passed" );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#getVarLevelX()
	 */
	@Override
	protected StaticInstanceTrackerWithMethodParams getVarLevelX() {

		return (StaticInstanceTrackerWithMethodParams)Storage;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#newVarLevelX()
	 */
	@Override
	protected Object newVarLevelX() {

		Storage = new Level1_BerkeleyDBStorage();
		return Storage;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.MainLevel0#setVarLevelX(java.lang.Object)
	 */
	@Override
	protected void setVarLevelX( Object toValue ) {

		Storage = (Level1_DMLStorageWrapper)toValue;
	}
}
