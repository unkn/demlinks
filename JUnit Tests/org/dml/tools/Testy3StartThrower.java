/**
 * File creation: Oct 20, 2009 1:34:29 AM
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


package org.dml.tools;



/**
 * 
 *
 */
public class Testy3StartThrower extends Testy {
	
	public static Testy3StartThrower getNew() {

		Testy3StartThrower t2 = new Testy3StartThrower();
		t2.init( "two" + new Object() );
		return t2;
		
	}
	
	@Override
	public void start() {

		RunTime.thro( new RuntimeException( "start" ) );
	}
	
}
