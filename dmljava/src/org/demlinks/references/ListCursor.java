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

package org.demlinks.references;

import org.demlinks.javathree.Location;

public interface ListCursor<Obj> {

	Reference<Obj> getCurrentRef();

	public boolean isUndefined();

	boolean go(Location location) throws Exception;

	boolean go(Location location, Reference<Obj> locationRef);

	/**
	 * @return true if existed
	 */
	boolean remove(Location location);

}
