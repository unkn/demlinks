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
import org.dml.level1.Symbol;
import org.dml.level1.SymbolBinding;
import org.dml.level1.JavaID;
import org.dml.level1.JavaIDBinding;
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
		// dbase, allows overriding existing ones from TupleBinding
		addNonPrimitive( Symbol.class, new SymbolBinding() );
		addNonPrimitive( JavaID.class, new JavaIDBinding() );
		addNonPrimitive( JUnit_Base1.class, new JUnit_Base1Binding() );
	}
	
	// parametric method
	private static <T> void addNonPrimitive( Class<T> cls,
			TupleBinding<T> binding ) {

		int formerSize = nonPrimitives.size();
		nonPrimitives.put( cls, binding );
		// making sure nothing is overwritten
		RunTime.assumedTrue( nonPrimitives.size() == formerSize + 1 );
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T> TupleBinding<T> getBinding( Class<T> cls ) {

		TupleBinding t = nonPrimitives.get( cls );// first
		if ( null == t ) {
			t = TupleBinding.getPrimitiveBinding( cls );// second
			if ( null == t ) {
				RunTime.bug( "TupleBinding not yet defined for class '"
						+ cls.getSimpleName()
						+ "' you may want to add it to the above list when defined!" );
			}
		}
		return t;
	}
}
