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

		RunTime.assertNotNull( l3_DMLEnv, name1 );
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
	
	public void addLast_ElementCapsule( ElementCapsule newLast ) {

		this.add_ElementCapsule( Position.LAST, newLast );
		// RunTime.assertNotNull( newLast );
		// RunTime.assertTrue( newLast.isAlone() );// prev=next=null
		//		
		// boolean wasEmpty = this.emptyHandling( newLast );
		// if ( !wasEmpty ) {
		// // has a last
		// ElementCapsule last = getLast_ElementCapsule();
		// RunTime.assertNotNull( last );
		// RunTime.assertTrue( null == last.getNextCapsule() );
		// RunTime.assertTrue( newLast.isAlone() );// prev=next=null
		// newLast.setPrevCapsule( last );
		// last.setNextCapsule( newLast );
		// internal_setLast( newLast );
		// }
	}
	
	/**
	 * will add ONLY if list is empty<br>
	 * 
	 * @param candidate
	 * @return true if list was empty and it was added; false if wasn't empty
	 *         and wasn't added
	 */
	private boolean addIfListIsEmpty( ElementCapsule candidate ) {

		RunTime.assertNotNull( candidate );
		RunTime.assertTrue( candidate.isAlone() );// prev=next=null
		if ( this.isEmpty() ) {
			// no first/last
			this.internal_registerNewFirstOrLast( Position.LAST, candidate );
			this.internal_registerNewFirstOrLast( Position.FIRST, candidate );
			return true;
		}
		return false;
	}
	
	/**
	 * @param last
	 * @param candidate
	 */
	private void internal_registerNewFirstOrLast( Position last,
			ElementCapsule candidate ) {

		// TODO Auto-generated method stub
		
	}
	
	public void addFirst_ElementCapsule( ElementCapsule newFirst ) {

		this.add_ElementCapsule( Position.FIRST, newFirst );
		// RunTime.assertNotNull( newFirst );
		// RunTime.assertTrue( newFirst.isAlone() );// prev=next=null
		//		
		// boolean wasEmpty = this.emptyHandling( newFirst );
		// if ( !wasEmpty ) {
		// // has a last
		// ElementCapsule first = getFirst_ElementCapsule();
		// RunTime.assertNotNull( first );
		// RunTime.assertTrue( null == first.getPrevCapsule() );
		// RunTime.assertTrue( newFirst.isAlone() );// prev=next=null
		// newFirst.setNextCapsule( first );
		// first.setPrevCapsule( newFirst );
		// internal_setFirst( newFirst );
		// }
	}
	
	public void add_ElementCapsule( Position pos, ElementCapsule theNew ) {

		switch ( pos ) {
		case FIRST:
		case LAST:
			RunTime.assertNotNull( theNew );
			RunTime.assertTrue( theNew.isAlone() );// prev=next=null
			
			boolean wasEmpty = this.addIfListIsEmpty( theNew );
			if ( !wasEmpty ) {
				// has a last
				ElementCapsule theOld = get_ElementCapsule( pos );// first
				RunTime.assertNotNull( theOld );// current first exists!
				
				// first.prev is null
				RunTime.assertTrue( null == theOld.getSideCapsule( Position.opposite( pos ) ) );
				
				RunTime.assertTrue( theNew.isAlone() );// prev=next=null
				
				// newfirst.next=oldfirst
				theNew.setCapsule( Position.opposite( pos ), theOld );
				// oldfirst.prev=newfirst
				theOld.setCapsule( pos, theNew );
				// setFirst=newfirst
				this.internal_registerNewFirstOrLast( pos, theNew );
			}
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

		RunTime.assertTrue( get_ElementCapsule( Position.FIRST ) == null );
		RunTime.assertTrue( get_ElementCapsule( Position.LAST ) == null );
		return this.size() == 0;
	}
}
