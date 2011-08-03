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
	
	// these paramNames are 1to1 mapped with objects in the list of MethodParams, such as Object or String etc.
	// two different MethodParams instances can use the same ParamName with different values in each instance
	
	public static final ParamID	varLevelAll				= ParamID.getNew( "varLevelAll" );
	public static final ParamID	homeDir					= ParamID.getNew( "homeDir" );
	
	// true if a call to BDB init should empty the database(s) before init
	public static final ParamID	jUnit_wipeDB			= ParamID.getNew( "jUnit_wipeDB" );
	public static final ParamID	jUnit_wipeDBWhenDone	= ParamID.getNew( "jUnit_wipeDBWhenDone" );
	
	// DatabaseCapsule:
	public static final ParamID	level1_BDBStorage		= ParamID.getNew( "level1_BDBStorage" );
	public static final ParamID	dbName					= ParamID.getNew( "dbName" );
	public static final ParamID	priDbConfig				= ParamID.getNew( "priDbConfig" );
	public static final ParamID	secDbConfig				= ParamID.getNew( "secDbConfig" );
	public static final ParamID	priDb					= ParamID.getNew( "priDb" );
	
}
