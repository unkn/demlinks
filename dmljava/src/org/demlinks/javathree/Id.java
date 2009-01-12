/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.demlinks.javathree;

public class Id {
	private String content=null;
	
	//constructor
	Id(String givenContent) {
		Debug.nullException(givenContent);
		if (givenContent.isEmpty()) {
			throw new AssertionError("bad call");
		}
		content = new String(givenContent);
	}
	
	protected String get() {
		return new String(content);
	}
	
	@Override
	public int hashCode() {
		return content.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
//		if (super.equals(obj)) {
//			return true;
//		}
		//if (obj istypeof Id) {
		return ( ((Id)obj).get().equals( this.get() ) ); 
	}

	@Override
	public String toString() {
		return this.get();
	}
}
