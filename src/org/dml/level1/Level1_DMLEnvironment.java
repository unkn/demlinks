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



import org.dml.tools.StaticInstanceTracker;



/**
 * facade design pattern
 * 
 */
public class Level1_DMLEnvironment extends StaticInstanceTracker {
	
	
	/**
	 * construct, don't forget to call init(...)
	 */
	public Level1_DMLEnvironment() {

		super();
	}
	
	/**
	 * @param strID
	 * @return
	 * @see NodeJID#ensureJIDFor(String)
	 */
	public NodeJID ensureJIDFor( String strID ) {

		return NodeJID.ensureJIDFor( strID );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		// TODO Auto-generated method stub
		
	}
}
