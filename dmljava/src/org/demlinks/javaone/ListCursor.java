package org.demlinks.javaone;

public interface ListCursor<Obj> {

	boolean goTo(Location location);

	boolean goTo(Obj object);

	boolean insert(Obj insertObject, Location location, Obj locationObject);

	Obj replace(Obj whichObj, Obj withThisObj);

	Obj replace(Location location, Obj withThisObj);

}
