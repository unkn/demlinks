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

/**
 * NodeLevel1
 * linkTo(child) makes sure child.parentList.has(this) and also this.childList.has(child) 
 */
public class NodeLevel1 extends NodeLevel0 {

	/**
	 * 
	 */
	public NodeLevel1() {
		super();
	}

	/**
	 * @param childNode2
	 * @return
	 */
	public boolean linkTo(NodeLevel1 childNode2) {
		boolean ret1 = super.linkTo(childNode2);
		boolean ret2 = ((NodeLevel0)childNode2).linkFrom(this);
		return ret1 && ret2;
	}
	
	/**
	 * @param parentNode2
	 * @return
	 */
	public boolean linkFrom(NodeLevel1 parentNode2) {
		boolean ret1 = super.linkFrom(parentNode2);
		boolean ret2 = ((NodeLevel0)parentNode2).linkTo(this);
		return ret1 && ret2;
	}
	
	/**
	 * @param childNodeL1
	 * @return
	 */
	public boolean isLinkTo(NodeLevel1 childNodeL1) {
		boolean ret1 = super.isLinkTo(childNodeL1);
		boolean ret2 = ((NodeLevel0)childNodeL1).isLinkFrom(this);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1 && ret2;
	}
	
	/**
	 * @param parentNodeL1
	 * @return
	 */
	public boolean isLinkFrom(NodeLevel1 parentNodeL1) {
		boolean ret1 = super.isLinkFrom(parentNodeL1);
		boolean ret2 = ((NodeLevel0)parentNodeL1).isLinkTo(this);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1 && ret2;
	}
	
}
