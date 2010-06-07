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



import java.util.HashMap;

import org.dml.tools.RunTime;
import org.references.Reference;



/**
 * Java-Identifier a.k.a. JavaID = node name on the java level which is
 * basically
 * a string<br>
 * this is how we identify Nodes at the java level, using JavaIDs<br>
 * 
 * - you're supposed to always use JavaIDs while in java, to access Nodes<br>
 * 
 * - it's probably a bad idea to cache ie. Symbols because while cached, it may<br>
 * be
 * removed from the underlying storage(ie. BDB) by some other
 * program/thread(assuming concurrency will ever be allowed),
 * although this might be challenging to implement (or limiting).<br>
 * 
 * - JavaIDs(Level1) != Symbols(Level2)
 * 
 * ---------- ignoring the above, I say:
 * JavaID is a way of referring to the same Symbol across application restarts
 * ie. using ensureSymbol(javaID) method (found in the environment not here)
 * on first run, a symbol is associated with a javaID, and on any next calls or app restarts
 * the same symbol is retrieved when referring to that javaID<br>
 * - one to one mapping between JavaID in java(instance) and JavaID in storage(the stored JavaID)<br>
 */
public class JavaID extends Reference<String> {
	
	// this will keep track of all JavaIDs of all environments
	// there's no point of having same JavaID twice for diff environments at least not when using same storage (bdb)
	protected static final HashMap<String, JavaID>	all_JavaIDs	= new HashMap<String, JavaID>();
	
	// protected because used in JUnit
	
	protected static final void junitClearAll() {

		RunTime.assumedNotNull( all_JavaIDs );
		if ( null != all_JavaIDs ) {
			all_JavaIDs.clear();
		}
	}
	
	/**
	 * get the JavaID for this string<br>
	 * make a new one if it doesn't exist<br>
	 * one to one mapping between the string and the JavaID
	 * 
	 * @param strID
	 *            a normal string that's supposed to be used as an ID
	 * @return JavaID (java ID) - the encapsulated string strID<br>
	 *         should never return null
	 */
	public static JavaID ensureJavaIDFor( String strID ) {

		RunTime.assumedNotNull( strID );
		JavaID curr = all_JavaIDs.get( strID );
		if ( null == curr ) {
			// create new
			curr = new JavaID( strID );
			if ( all_JavaIDs.put( strID, curr ) != null ) {
				RunTime.bug( "a value already existed?!! wicked! it means that the above .get() is bugged?!" );
			}
		}
		return curr;
	}
	
	
	/**
	 * private constructor to prevent usage via new
	 * 
	 * @param strID
	 */
	private JavaID( String strID ) {

		RunTime.assumedNotNull( strID );
		
		this.setObject( strID );
		
	}
	
	@Override
	public String toString() {

		return this.getClass().getSimpleName() + ":" + this.getObject();
	}
	
	
	/**
	 * equals always compares by content BUT in this case it ONLY compares by
	 * references, because JavaID instances are not supposed to have the same
	 * contents, ever<br>
	 * 
	 * @param jid
	 * @return
	 */
	@Override
	public boolean equals( Object jid ) {

		RunTime.assumedNotNull( jid );
		if ( ( !this.getClass().isAssignableFrom( jid.getClass() ) ) || ( this.getClass() != jid.getClass() ) ) {
			RunTime.bug( "you passed a different type parameter; must be a bug somewhere" );
		}
		return this == jid;
	}
}
