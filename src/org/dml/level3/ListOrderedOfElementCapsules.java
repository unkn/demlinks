/**
 * File creation: Nov 20, 2009 5:29:32 AM
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



import org.dml.level1.Symbol;
import org.dml.tools.RunTime;
import org.references.Position;



/**
 * a list where the order is maintained<br>
 * list of ElementCapsules<br>
 * 
 */
public class ListOrderedOfElementCapsules {
	
	Level3_DMLEnvironment	envL3;
	Symbol					name;
	
	public ListOrderedOfElementCapsules( Level3_DMLEnvironment l3_DMLEnv,
			Symbol name1 ) {

		RunTime.assumedNotNull( l3_DMLEnv, name1 );
		envL3 = l3_DMLEnv;
		
		name = name1;
		// Symbol listSymbol = l3DMLEnvironment.getSymbol(
		// Level3_DMLEnvironment.listSymbolJavaID );
		this.internal_setName();
	}
	
	/**
	 * override this and don't call super()
	 */
	protected void internal_setName() {

		envL3.ensureVector( envL3.listOrderedOfElementCapsules_Symbol, name );
	}
	
	/**
	 * override this and don't call super()
	 */
	protected boolean internal_hasNameSetRight() {

		return envL3.isVector( envL3.listOrderedOfElementCapsules_Symbol, name );
	}
	
	public boolean isValid() {

		// TODO more to add here
		return this.internal_hasNameSetRight();
	}
	
	/**
	 * @param last
	 * @param candidate
	 */
	private void internal_dmlRegisterNewFirstOrLast( Position last,
			ElementCapsule candidate ) {

		// TODO Auto-generated method stub
		
	}
	
	public void add_ElementCapsule( Position pos, ElementCapsule theNew ) {

		switch ( pos ) {
		case FIRST:
		case LAST:
			RunTime.assumedNotNull( theNew );
			RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
			
			if ( this.isEmpty() ) {
				// no first/last
				this.internal_dmlRegisterNewFirstOrLast(
						Position.opposite( pos ), theNew );
			} else {// not empty list
				// has a last
				ElementCapsule theOld = this.get_ElementCapsule( pos );// first
				RunTime.assumedNotNull( theOld );// current first exists!
				
				// first.prev is null
				RunTime.assumedNull( theOld.getSideCapsule( pos ) );
				
				// redundant check:
				RunTime.assumedTrue( theNew.isAlone() );// prev=next=null
				
				// newfirst.next=oldfirst
				theNew.setCapsule( Position.opposite( pos ), theOld );
				// oldfirst.prev=newfirst
				theOld.setCapsule( pos, theNew );
				// setFirst=newfirst
				
			}
			this.internal_dmlRegisterNewFirstOrLast( pos, theNew );// common
			break;
		
		default:
			RunTime.badCall( "bad position" );
			break;
		}
	}
	
	public int size() {

		return 0;
		
		// TODO
	}
	
	public boolean isEmpty() {

		RunTime.assumedTrue( this.get_ElementCapsule( Position.FIRST ) == null );
		RunTime.assumedTrue( this.get_ElementCapsule( Position.LAST ) == null );
		return this.size() == 0;
	}
	
	/**
	 * @param first
	 * @return
	 */
	private ElementCapsule get_ElementCapsule( Position pos ) {

		// TODO Auto-generated method stub
		return null;
	}
}
