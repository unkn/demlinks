/**
 * File creation: Oct 22, 2009 7:20:11 AM
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


package org.temporary.tests;



import org.junit.Test;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;



/**
 * 
 *
 */
public class XInstance {
	
	@Test
	public void test1() {

		S1 s1 = new S1(), s11 = new S1();
		S2 s2 = new S2();
		
		try {
			S1_1 s1_1 = new S1_1();
			
			s1_1.deinitAllLikeMe();
			throw new Exception();
			// s2.deinit();
		} catch ( Exception e ) {
			System.out.println( "All of em" );
			Base.deinitAll();
		}
	}
}



abstract class Base {
	
	abstract protected void _deinit();
	
	public final void deinit() {

		this._deinit();
		b.remove( this );
	}
	
	public Base() {

		b.add( this );
	}
	
	public static final void deinitAll() {

		b.deinitAll();
	}
	
	public void deinitAllLikeMe() {

		b.deinitAllLike( this );
	}
	
	private final static BaseList	b	= new BaseList();
}



class S1 extends Base {
	
	@Override
	protected void _deinit() {

		System.out.println( "I am deinit " + this.getClass().getCanonicalName() );
	}
}



class S1_1 extends S1 {
	
	@Override
	protected void _deinit() {

		System.out.println( "I am deinit S1_1" );
		super._deinit();
	}
}



class S2 extends Base {
	
	@Override
	protected void _deinit() {

		System.out.println( "I am deinit S2" );
	}
}



class BaseList {
	
	public void add( Base b ) {

		_list.addLast( b );
	}
	
	public void deinitAll() {

		Base b;
		while ( null != ( b = _list.getObjectAt( Position.FIRST ) ) ) {
			b.deinit();
			_list.removeObject( b );
		}
	}
	
	public void remove( Base b ) {

		_list.removeObject( b );
	}
	
	public void deinitAllLike( Base e ) {

		Base b;
		for ( int i = 0; i < _list.size(); ) {
			b = _list.getObjectAt( i );
			if ( e.getClass() == b.getClass() ) {
				b.deinit();
				_list.removeObject( b );
			} else {
				i++;
			}
		}
	}
	
	protected ListOfUniqueNonNullObjects<Base>	_list	= new ListOfUniqueNonNullObjects<Base>();
}
