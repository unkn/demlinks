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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.demlinks.crap.Position;
import org.junit.Before;
import org.junit.Test;

public class RefsListTest {
	RefsList<Object> refList;
	Object obj1, obj2;
	Reference<Object> ref1, ref2;

	@Before
	public void init() {
		refList = new RefsList<Object>();
		obj1 = new Object();
		obj2 = new Object();
		ref1 = new Reference<Object>();
		ref2 = new Reference<Object>();
		ref1.setObject(obj1);
		assertTrue(ref1.getObject() == obj1);
		ref2.setObject(obj2);
		assertTrue(ref2.getObject() == obj2);
	}

	@Test
	public void something() throws Exception {
		assertTrue(refList.isEmpty());

		assertTrue(refList.addLast(ref1));
		assertTrue(refList.containsRef(ref1));
		assertFalse(refList.addLast(ref1));
		assertTrue(refList.containsRef(ref1));

		int mod = refList.getModified();
		assertTrue(refList.addLast(ref2));
		assertTrue(mod != refList.getModified());
		mod = refList.getModified();
		assertTrue(refList.containsRef(ref2));
		assertFalse(mod != refList.getModified());

		assertFalse(refList.addLast(ref2));
		assertFalse(refList.addLast(ref1));// after this call ref1 mustn't move
		assertFalse(mod != refList.getModified());
		// from first position
		assertTrue(refList.getFirstRef() == ref1);
		assertTrue(refList.getLastRef() == ref2);

		assertTrue(refList.getNodeRefAt(Position.FIRST) == refList
				.getFirstRef());
		assertTrue(refList.getNodeRefAt(Position.LAST) == refList.getLastRef());
		assertTrue(refList.getNodeRefAt(Position.AFTER, ref1) == ref2);
		assertTrue(refList.getNodeRefAt(Position.BEFORE, ref2) == ref1);
		assertFalse(mod != refList.getModified());

		Reference<Object> ref3 = new Reference<Object>();
		ref3.setObject(null);
		assertTrue(ref3.isDead());
		assertTrue(refList.addLast(ref3));// null objects can be added in this
		// list level

//		ListCursor<Object> p = refList.getParser();
//
//		assertTrue(p.isUndefined());
//
//		boolean ex = false;
//		try {
//			p.go(Location.AFTER);
//		} catch (Exception e) {
//			ex = true;
//		}
//		assertTrue(ex);
//
//		ex = false;
//		try {
//			p.go(Location.BEFORE);
//		} catch (Exception e) {
//			ex = true;
//		}
//		assertTrue(ex);
//
//		assertTrue(p.go(Location.FIRST));
//		assertFalse(p.isUndefined());
//
//		ex = false;
//		try {
//			p.go(null);
//		} catch (NullPointerException e) {
//			ex = true;
//		}
//		assertTrue(ex);
//
//		assertTrue(ref1 == p.getCurrentRef());
//		assertTrue(p.go(Location.AFTER));
//		assertTrue(ref2 == p.getCurrentRef());
//		assertTrue(p.go(Location.LAST));
//		assertTrue(ref3 == p.getCurrentRef());
//		assertFalse(p.go(Location.AFTER));
//		assertTrue(p.isUndefined());
//
//		assertTrue(p.go(Location.BEFORE, ref3));
//		assertFalse(p.isUndefined());
//		assertTrue(ref2 == p.getCurrentRef());
		// assertTrue(p.remove());
		// p.remove(Location.CURRENT);
		// p.go(Location.BEFORE);
		//
		// p.Parser p = refList.getParser(Location.BEFORE, ref2);
	}
}
