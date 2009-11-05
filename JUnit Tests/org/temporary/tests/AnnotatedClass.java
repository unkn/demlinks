/**
 * File creation: Nov 5, 2009 8:16:09 AM
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
@Author( name = "Johny" )
@Version( number = 1.0 )
public class AnnotatedClass {
	
	@Author( name = "Author1" )
	@Version( number = 2.0f )
	public void annotatedMethod1() {

	}
	
	@Author( name = "Author2" )
	@Version( number = 4.0 )
	public void annotatedMethod2() {

	}
}
