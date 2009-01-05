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


/**
 * Node level only, no IDs here
 * treating Nodes as Node objects only
 */
public class Environment_L1 {
	
	//methods

	public boolean link(Node parentNode, Node childNode) {
		//assumes both Nodes exist and are not null params, else expect exceptions
		boolean ret1 = parentNode.linkForward(childNode);
		boolean ret2 = childNode.linkBackward(parentNode);
		if (ret1 ^ ret2) {
			throw new AssertionError("inconsistent link detected");
		}
		return ret1;
	}

	/**
	 * parentNode -> childNode<br>
	 * parentNode <- childNode<br>
	 * @param parentNode
	 * @param childNode
	 * @return true if (mutual) link between the two nodes exists
	 */
	public boolean isLink(Node parentNode, Node childNode) {
		Debug.nullException(parentNode, childNode);
		boolean one = parentNode.isLinkForward(childNode);
		boolean two = childNode.isLinkBackward(parentNode);
		if (one ^ two) {
			throw new AssertionError("inconsistent link detected");
		}
		return one;
	}
	
	//TODO unLink
}
