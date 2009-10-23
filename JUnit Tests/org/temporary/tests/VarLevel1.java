/**
 * File creation: Oct 23, 2009 8:42:08 AM
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


package org.temporary.tests;



/**
 * 
 *
 */
public class VarLevel1 implements VarLevel1Interface {
	
	/**
	 * 
	 */
	public void init() {

		System.out.println( this.getName() + " inited." );
	}
	
	public String getName() {

		return this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.VarLevel1Interface#deInit()
	 */
	@Override
	public void deInit() {

		System.out.println( this.getName() + " DeInited." );
	}
	
	public void sayHello() {

		System.out.println( this.getName() + " says Hello." );
	}
}
