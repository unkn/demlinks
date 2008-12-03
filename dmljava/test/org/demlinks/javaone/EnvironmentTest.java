/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@sourceforge.net>
 	
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

import static org.junit.Assert.*;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void testLink() {
		Environment a = new Environment();
		assertTrue(null == a.getNode("C"));
		a.link("A", "B");
		assertTrue(a.isLink("A","B"));
		assertTrue(a.size() == 2);
		Node _par = a.getNode("A");
		Node _chi = a.getNode("B");
		assertTrue(_chi.isLinkFrom(_par));
		assertTrue(_par.isLinkTo(_chi));
		
		a.unlink("A","B");
		assertTrue(0 == _chi.childrenList.size());
		assertTrue(0 == _chi.parentsList.size());
		assertTrue(_chi.isDead());
		assertTrue(null == a.getNode("B"));
		
	}

}
