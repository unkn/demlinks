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



package org.references;



import org.dml.tools.RunTime;



public enum Position {
	FIRST, LAST, BEFORE, AFTER;
	
	/**
	 * @param pos
	 * @return
	 */
	public static Position opposite( Position pos ) {

		switch ( pos ) {
		case FIRST:
			return LAST;
		case LAST:
			return FIRST;
		case BEFORE:
			return AFTER;
		case AFTER:
			return BEFORE;
			
		default:
			RunTime.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
	
	public static Position getAsEdge( Position pos ) {

		switch ( pos ) {
		case BEFORE:
			return FIRST;
		case AFTER:
			return LAST;
		case LAST:
		case FIRST:
			RunTime.badCall( "already edge" );
		default:
			RunTime.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
	
	/**
	 * @param pos
	 * @return
	 */
	public static Position getAsNear( Position pos ) {

		switch ( pos ) {
		case FIRST:
			return BEFORE;
		case LAST:
			return AFTER;
		case BEFORE:
		case AFTER:
			RunTime.badCall( "already near" );
		default:
			RunTime.bug( "shouldn't reach this" );
		}
		return null;// won't reach this
	}
}
