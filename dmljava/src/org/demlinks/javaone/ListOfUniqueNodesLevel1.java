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

package org.demlinks.javaone;

public class ListOfUniqueNodesLevel1 extends ListOfUniqueNodesLevel0 {

	NodeLevel1 fatherNodeL1;
	private List opposedList=null;//TODO we might instead cache the List itself, here; however we must make sure the NodeL1 or L0 won't ever destroy the lists and recreate them within the Node's lifetime
	
	public ListOfUniqueNodesLevel1(NodeLevel1 nodeLevel1Father) {
		super();
		fatherNodeL1 = nodeLevel1Father;
	}
	
	/**
	 * must call this after 'new'
	 */
	public void init() {
		whoAmI();
	}

	
	private void whoAmI() {
		if (opposedList != null) {
			return;
		}
		ListOfUniqueNodesLevel0 par = fatherNodeL1.get(List.PARENTS);
		ListOfUniqueNodesLevel0 chi = fatherNodeL1.get(List.CHILDREN);
		if (par == this) {
			opposedList = List.CHILDREN;
		} else {
			if (chi == this) {
				opposedList = List.PARENTS;
			} else {
				throw new AssertionError("the list doesn't pertain to this node, ie. it's extra");
			}
		}
	}

	@Override
	public boolean onBeforeAddition(NodeLevel0 nodeL1ToBeAdded) {
		if (!super.onBeforeAddition(nodeL1ToBeAdded)) {//this is not needed here, since L0 does nothing with it, but maybe to keep consistency it's here
			return false;
		}
		//so we're trying to add nodeL1ToBeAdded in the list of fatherNodeL1
		//and so, in the opposite list of nodeL1ToBeAdded we will add now fatherNodeL1
		return nodeL1ToBeAdded.get(opposedList).addLast(fatherNodeL1);
	}
	
	
}
