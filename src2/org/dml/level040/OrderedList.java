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



package org.dml.level040;



import org.dml.level010.Symbol;
import org.references.Position;



/**
 * 
 *
 */
public interface OrderedList
{
	
	public
			boolean
			ensure(
					Symbol whichSymbol );
	

	/**
	 * @param whichSymbol
	 * @return
	 */
	public
			boolean
			hasSymbol(
						Symbol whichSymbol );
	

	/**
	 * 
	 */
			void
			assumedValid();
	

	/**
	 * @return
	 */
			long
			size();
	

	/**
	 * @param last
	 * @param e1
	 */
			void
			add(
					Symbol which,
					Position pos );
	

	/**
	 * cannot be used when DUPs are allowed
	 * 
	 * @param e2
	 * @param before
	 * @param e1
	 */
			void
			add(
					Symbol which,
					Position pos,
					Symbol posSym );
	

	/**
	 * @param pos
	 * @param posSymbol
	 * @return null if didn't exist, else the Symbol which was removed
	 */
			Symbol
			remove(
					Position pos,
					Symbol posSymbol );
	

	/**
	 * @param pos
	 * @return null if didn't exist, else the Symbol which was removed
	 */
			Symbol
			remove(
					Position pos );
	

	/**
	 * @param whichSymbol
	 * @return true if existed
	 */
			boolean
			remove(
					Symbol whichSymbol );
	

	/**
	 * @param first
	 * @return
	 */
			Symbol
			get(
					Position first );
	

	/**
	 * can't use this when DUPs are allowed ie. posSym may exist twice, so which one r u referring to?
	 * 
	 * @param after
	 * @param e2
	 * @return
	 */
			Symbol
			get(
					Position pos,
					Symbol posSym );
	

	/**
	 * @return
	 */
			Symbol
			getAsSymbol();
}
