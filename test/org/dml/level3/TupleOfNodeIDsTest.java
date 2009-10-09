/**
 * File creation: May 31, 2009 10:45:44 AM
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


package org.dml.level3;



import org.dml.level1.NodeJID;
import org.dml.level2.NodeID;
import org.dml.storagewrapper.Storage;
import org.dml.storagewrapper.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



/**
 * 
 *
 */
public class TupleOfNodeIDsTest {
	
	// TupleOfNodeIDs one, two, three;
	NodeID	n1, n2, n3;
	
	@Before
	public void setUp() {

		Storage.init();
	}
	
	@After
	public void tearDown() {

		Storage.deInit();
	}
	
	@Test
	public void test1() throws StorageException {

		n1 = NodeID.ensureNode( NodeJID.ensureJIDFor( "A" ) );
		n2 = NodeID.ensureNode( NodeJID.ensureJIDFor( "B" ) );
		// one = TupleOfNodeIDs.groupAsKeyValue( n1, n2 );
		System.out.println( n1 + "!" + n2 );
		
	}
}
