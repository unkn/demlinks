/**
 * File creation: Nov 6, 2009 12:44:40 AM
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
public class Super2 extends SuperBase {
	
	public static void main( String[] args ) {

		Super2 s = new Super2();
		s.init();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.temporary.tests.SuperBase#init()
	 */
	@Override
	public void init() {

		// var = "2";
		this.show();
		super.init();
	}
}
