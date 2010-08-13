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



package org.dml.level010;



import org.dml.database.bdb.level1.Bridge_SymbolAndBDB;
import org.dml.tools.RunTime;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;



/**
 * unfortunately enough, here we don't know from/to which DB the date is coming/going from/to<br>
 * 
 */
public class SymbolBinding extends TupleBinding<Symbol> {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sleepycat.bind.tuple.TupleBinding#entryToObject(com.sleepycat.bind
	 * .tuple.TupleInput)
	 */
	@Override
	public Symbol entryToObject( TupleInput input ) {

		long l = input.readLong();
		RunTime.assumedNotNull( l );
		Symbol nid = Bridge_SymbolAndBDB.newSymbolFrom( l );
		RunTime.assumedTrue( Bridge_SymbolAndBDB.getLongFrom( nid ) == l );
		return nid;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sleepycat.bind.tuple.TupleBinding#objectToEntry(java.lang.Object,
	 * com.sleepycat.bind.tuple.TupleOutput)
	 */
	@Override
	public void objectToEntry( Symbol alreadyExistingSymbol, TupleOutput output ) {

		RunTime.assumedNotNull( alreadyExistingSymbol, output );
		long myLong = Bridge_SymbolAndBDB.getLongFrom( alreadyExistingSymbol );
		RunTime.assumedNotNull( myLong );
		// System.out.println( object );
		// it will never be null before writing it to dbase, else bug somewhere
		output.writeLong( myLong );
	}
	
}
