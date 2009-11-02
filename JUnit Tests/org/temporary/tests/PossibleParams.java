/**
 * File creation: Oct 27, 2009 3:45:28 AM
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



import org.references.method.ParamName;



/**
 * 
 *this will have fields that are lists of objects that are references to real
 * instances like String<br>
 * 
 */
public class PossibleParams {
	
	// these paramNames will point to objects in the list of MethodParams, but
	// these objects are the references that point to the real instances such as
	// String
	// these lists can hold objects but the objects that they will hold are the
	// references from similar typed list, not the instances that these refs
	// point at
	public static ParamName<Object>	varLevelAll	= new ParamName<Object>();
	public static ParamName<Object>	varLevel3	= new ParamName<Object>();
	public static ParamName<Object>	homeDir		= new ParamName<Object>();
	

}
