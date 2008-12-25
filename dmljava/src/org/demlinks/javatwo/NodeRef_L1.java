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

package org.demlinks.javatwo;

import static org.demlinks.javaone.Environment.nullException;

/**
 * a NodeReference may only exist within the list of children or the list of parents of a NodeLevel0<br>
 * a NR is a pointer to a NodeLevel0<br>
 * a NR has a parent NodeLevel0 and that's the same Node that has it in its list<br>
 * a NR may exist only in one list hence may only have 1 node parent<br>
 * a NR may point only to one NodeLevel0 hence may only have 1 child<br>
 * a NR has a previous NR and a next NR to facilitate parsing the list it's in<br>
 * a NR is unique<br>a NR should be uniquely identifiable<br>
 * at its creation a NR should know all the 4 data it's comprised of: 
 * parent node, child node, prev NR, next NR; 
 * even if the latter two may be null to signal loneliness in the list<br>
 */
public class NodeRef_L1 extends NodeRef_L0 {
	
	@Override
	public boolean setNextNodeRef(NodeRef_L0 nextNodeRef) {
		return setNextNodeRef_L1(nextNodeRef);
	}
	
	/**
	 * @param newNext is inserted between this and this.next
	 * @return
	 */
	public final boolean setNextNodeRef_L1(NodeRef_L0 newNext) {
		if (null == newNext) {
			if (null != this.getNextNodeRef()) {
				throw new AssertionError();
			}
			return true;
		}
		if (!newNext.isAlone()) {
			return false;
		}
		
		NodeRef_L0 oldNext=this.getNextNodeRef();//oldNext
		newNext.setPrevNodeRef_L0(this);//this <- newNext
		if (oldNext != null) {
			newNext.setNextNodeRef_L0(oldNext);//newNext -> oldNext
			oldNext.setPrevNodeRef_L0(newNext);//newNext <- oldNext
		}
		this.setNextNodeRef_L0(newNext);//this -> newNext
		return true;
	}
	
	@Override
	public boolean setPrevNodeRef(NodeRef_L0 prevNodeRef) {
		return setPrevNodeRef_L1((NodeRef_L1) prevNodeRef);
	}

	public final boolean setPrevNodeRef_L1(NodeRef_L1 newPrev) {
		NodeRef_L0 oldPrev=this.getPrevNodeRef();//oldPrev
		
		if (null == newPrev) {
			if (null != oldPrev ) {
				throw new AssertionError();
			}
			return true;
		}
		if (!newPrev.isAlone()) {
			return false;
		}
		
		
		newPrev.setNextNodeRef_L0(this);//newPrev -> this
		if (oldPrev != null) {//exists oldPrev?
			newPrev.setPrevNodeRef_L0(oldPrev);//oldPrev <- newPrev 
			oldPrev.setNextNodeRef_L0(newPrev);//oldPrev -> newPrev
		}
		this.setPrevNodeRef_L0(newPrev);//newPrev <- this
		return true;
	}

	public boolean selfRemove() {
		return selfRemove_L1();
	}

	public boolean selfRemove_L1() {
		NodeRef_L0 prev = this.getPrevNodeRef();
		NodeRef_L0 next = this.getNextNodeRef();
		if (prev != null) {
			prev.setNextNodeRef_L0(next);
			this.setPrevNodeRef_L0(null);
		}
		if (next != null) {
			next.setPrevNodeRef_L0(prev);
			this.setNextNodeRef_L0(null);
		}
		return setNode_L0(null);
	}
	
	@Override
	public boolean setNode(Node_L0 node) {
		return setNode_L1(node);
	}
	
	/**
	 * doesn't allow null
	 * @param node
	 * @return
	 */
	public boolean setNode_L1(Node_L0 node) {
		nullException(node);
		return setNode_L0(node);
	}
}
