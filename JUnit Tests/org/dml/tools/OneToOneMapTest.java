/**
 * File creation: Oct 24, 2009 1:59:22 PM
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


package org.dml.tools;



import org.junit.Test;
import org.references.Reference;



/**
 * 
 *
 */
public class OneToOneMapTest {
	
	OneToOneMap<String, Reference<String>>	map;
	
	@Test
	public void test1() {

		map = new OneToOneMap<String, Reference<String>>();
		
		String str = "one";
		Reference<String> estr = new Reference<String>();
		estr.setObject( str );
		
		map.setFirst( str );
		map.setSecond( estr );
		map.setBoth( str, estr );
		map.getFirst();
		map.getSecond();
		map.getBoth();
	}
}
