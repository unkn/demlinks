/**
 * File creation: Nov 15, 2009 4:07:07 PM
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


package org.dml.database.bdb.level1;



import java.util.HashMap;
import java.util.Map;

import org.dml.database.JUnit_Base1;
import org.dml.database.JUnit_Base1Binding;
import org.dml.level1.NodeID;
import org.dml.level1.NodeIDBinding;
import org.dml.level1.NodeJID;
import org.dml.level1.NodeJIDBinding;
import org.dml.tools.RunTime;

import com.sleepycat.bind.tuple.TupleBinding;



/**
 * 
 *
 */
public class AllTupleBindings {
	
	@SuppressWarnings( "unchecked" )
	private static final Map<Class, TupleBinding>	nonPrimitives	= new HashMap<Class, TupleBinding>();
	static {
		// add to this list any objects that you expect to store into a BDB
		// dbase
		addNonPrimitive( NodeID.class, new NodeIDBinding() );
		addNonPrimitive( NodeJID.class, new NodeJIDBinding() );
		addNonPrimitive( JUnit_Base1.class, new JUnit_Base1Binding() );
	}
	
	// parametric method
	private static <T> void addNonPrimitive( Class<T> cls,
			TupleBinding<T> binding ) {

		nonPrimitives.put( cls, binding );
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T> TupleBinding<T> getBinding( Class<T> cls ) {

		TupleBinding t = TupleBinding.getPrimitiveBinding( cls );
		if ( null == t ) {
			t = nonPrimitives.get( cls );
			if ( null == t ) {
				RunTime.bug( "TupleBinding not yet defined for class '"
						+ cls.getSimpleName()
						+ "' you may want to add it to the above list when defined!" );
			}
		}
		return t;
	}
}
