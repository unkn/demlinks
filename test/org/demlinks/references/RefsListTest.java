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

import java.util.NoSuchElementException;

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
	public void testInsert() {
		assertFalse(refList.addFirst(ref1));
		assertFalse( refList.insertObjAt(ref2, Position.AFTER, ref1) );
		Reference<Object> ref3 = new Reference<Object>();
		assertFalse( refList.insertObjAt(ref3, Position.BEFORE, ref1) );
		assertTrue(refList.getFirstRef() == ref3);
		assertTrue(refList.getLastRef() == ref2);
		assertTrue(refList.getNodeRefAt(Position.BEFORE, ref2) == ref1);
		assertTrue(refList.size() == 3);
		
		assertTrue(refList.removeRef(ref3));
		
		boolean excepted = false;
		try {
			//even though ref2 exists, while ref3 doesn't, the call is bugged so we alert:
			//ref3 should exist
			refList.insertObjAt(ref2, Position.BEFORE, ref3);//3rd param, not exists
		}catch (NoSuchElementException e) {
			excepted = true;
		}
		assertTrue(excepted);
	}

	@Test
	public void testSomething() throws Exception {
		assertTrue(refList.isEmpty());

		assertFalse(refList.addLast(ref1));
		assertTrue(refList.containsRef(ref1));
		assertTrue(refList.addLast(ref1));
		assertTrue(refList.containsRef(ref1));

		int mod = refList.getModified();
		assertFalse(refList.addLast(ref2));
		assertTrue(mod != refList.getModified());
		mod = refList.getModified();
		assertTrue(refList.containsRef(ref2));
		assertFalse(mod != refList.getModified());

		assertTrue(refList.addLast(ref2));
		assertTrue(refList.addLast(ref1));// after this call ref1 mustn't move
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
		assertFalse(refList.addLast(ref3));// null objects can be added in this
		// list level
		
		
		Reference<Object> ref0 = new Reference<Object>();
		ref0.setObject(null);
		assertTrue(ref0.isAlone());
		assertFalse(refList.addFirst(ref0));
		assertTrue(refList.getFirstRef() == ref0);
		assertTrue(refList.size() == 4);
		
		boolean excepted = false;
		try {
			refList.addFirst(null);
		}catch (NullPointerException e) {//yeah stupid, I know :)
			excepted = true;
		}
		assertTrue(excepted);
		
		
	}
}
