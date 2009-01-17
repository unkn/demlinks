package org.demlinks.references;

import org.demlinks.debug.Debug;
import org.demlinks.javathree.Location;


/**
 * handles the NodeRef list at the Node level
 *
 */
public class RefsList_L2<E> extends RefsList_L1<E> {
	
	/**
	 * @param location
	 * @return
	 */
	public E removeObject(Location location) {
		Reference<E> nr = getNodeRefAt(location);
		if (null != nr) {
			E nod = nr.getObject();
			if (removeRef(nr)) {
				return nod;
			}
		}
		return null;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public boolean containsObject(E obj) {
		Debug.nullException(obj);
		return (null != this.getRef(obj));
	}
	
	/**
	 * creates a new NodeRef to be added to this list, but it's not added via this method
	 * @param obj
	 * @return
	 */
	public Reference<E> newRef(E obj) {
		Debug.nullException(obj);
		Reference<E> n = new Reference<E>();
		n.setObject(obj);
		return n;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public Reference<E> getRef(E obj) {
		return getRef_L0(obj);
	}

	/**
	 * @param obj
	 * @return
	 */
	public final Reference<E> getRef_L0(E obj) {
		Debug.nullException(obj);
		Reference<E> parser = getFirstRef();
		while (null != parser) {
			if (obj.equals(parser.getObject())) {
				break;
			}
			parser = parser.getNext();
		}
		return parser;
	}

	/**
	 * @return
	 */
	public E getFirstObject() {
		if (getFirstRef() != null) {
			return getFirstRef().getObject();
		}
		return null;
	}


	/**
	 * @param obj
	 * @return
	 */
	public boolean addLast(E obj) {
		Reference<E> nr = getRef(obj);
		if (null == nr) {
			nr = newRef(obj);
		}
		return addLast(nr);
	}

	//TODO addFirst
	//TODO insert(Node, Location);
	//TODO insert(Node, Location, Node);
	//TODO replace(Node, Node);
	//TODO replace(Node, Location);
	//TODO replace(Node, Location, Node);
	//find+replace current, is not an option 
	/**
	 * @param obj
	 * @return true if existed; either way after call it's removed
	 */
	public boolean removeObject(E obj) {
		Reference<E> nr = getRef(obj);
		if (null == nr) {
			return false;
		}
		return removeRef(nr);
	}
	
	
	
}
