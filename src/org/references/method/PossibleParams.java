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



package org.references.method;



/**
 * 
 * this will have fields that are lists of objects that are references to real
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
	public static final ParamName<Object>	varLevelAll				= new ParamName<Object>();
	public static final ParamName<Object>	homeDir					= new ParamName<Object>();
	
	// true if a call to BDB init should empty the database(s) before init
	public static final ParamName<Object>	jUnit_wipeDB			= new ParamName<Object>();
	public static final ParamName<Object>	jUnit_wipeDBWhenDone	= new ParamName<Object>();
	
	// DatabaseCapsule:
	public static final ParamName<Object>	level1_BDBStorage		= new ParamName<Object>();
	public static final ParamName<Object>	dbName					= new ParamName<Object>();
	public static final ParamName<Object>	priDbConfig				= new ParamName<Object>();
	public static final ParamName<Object>	secDbConfig				= new ParamName<Object>();
	public static final ParamName<Object>	priDb					= new ParamName<Object>();
	
}
