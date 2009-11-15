/**
 * File creation: Nov 15, 2009 4:22:08 PM
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


package org.dml.level1;



import org.dml.tools.RunTime;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;



/**
 * 
 *
 */
public class NodeJIDBinding extends TupleBinding<NodeJID> {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sleepycat.bind.tuple.TupleBinding#entryToObject(com.sleepycat.bind
	 * .tuple.TupleInput)
	 */
	@Override
	public NodeJID entryToObject( TupleInput input ) {

		// Data must be read in the same order that it was
		// originally written.
		String strJID = input.readString();
		RunTime.assertNotNull( strJID );
		NodeJID myJID = NodeJID.ensureJIDFor( strJID );// new NodeJID( strJID );
		
		return myJID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sleepycat.bind.tuple.TupleBinding#objectToEntry(java.lang.Object,
	 * com.sleepycat.bind.tuple.TupleOutput)
	 */
	@Override
	public void objectToEntry( NodeJID object, TupleOutput output ) {

		NodeJID myJID = object;
		String strJID = myJID.getObject();
		RunTime.assertNotNull( strJID );
		// it will never be null before writing it to dbase, else bug somewhere
		output.writeString( strJID );
	}
	
}
