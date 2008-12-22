/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.demlinks.javaone;

public interface OnLinkedListSetEvents<Obj> {
	/**
	 * override this
	 * @param oldObj
	 * @param newObj
	 */
	public void onAfterReplace(Obj oldObj, Obj newObj);
	
	/**
	 * @param oldObj
	 * @param newObj
	 * @return true if replace is allowed, false if you don't want the replace to occur
	 */
	public boolean onBeforeReplace(Obj oldObj, Obj newObj);

	
	/**
	 * this will be called right before the addition giving you the opportunity to abort the addition<br>
	 * the chances of failing considering the code following the call to this method, are slim<br>
	 * it this method returns false, then {@link #onFailedAddition(Object)} won't be called
	 * @param objectToBeAdded
	 * @return true if you allow the addition to occur, false otherwise
	 */
	public boolean onBeforeAddition(Obj objectToBeAdded);
	
	/**
	 * this will be called if the addition failed for some reason<br>
	 * Beware however that this method cannot be called unless after {@link #onBeforeAddition(Object)} was called<br>
	 * In other words, {@link #onBeforeAddition(Object)} will always be called before onFailedAddition<br>
	 * So if you're here, you can be sure you were also in {@link #onBeforeAddition(Object)} moments ago.
	 * @param objThatFailedToBeAdded
	 */
	public void onFailedAddition(Obj objThatFailedToBeAdded);
	
	/**
	 * this will be called only if the addition succeeded 
	 * @param objectJustAdded
	 */
	public void onAfterAddition(Obj objectJustAdded);
	
	/**
	 * @param objectToBeRemoved
	 * @return true if you allow removal of this object, false otherwise
	 */
	public boolean onBeforeRemove(Obj objectToBeRemoved);
	
	/**
	 * @param objectJustRemoved
	 */
	public void onAfterRemove(Obj objectJustRemoved);
}
