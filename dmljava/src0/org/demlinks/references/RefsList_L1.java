/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
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


package org.demlinks.references;



import org.demlinks.javathree.*;
import org.q.*;



/**
 * a double-linked list of References where no two are alike (no duplicates
 * allowed)<br>
 * these Refs however may contain the same objects thus allowing duplicate
 * objects<br>
 * but the list itself is comprised of unique References; the Refs are unique,
 * but there can be two different Refs pointing to same object<br>
 * null objects are allowed at this level<br>
 * no null Refs allowed<br>
 * ability to insert anywhere<br>
 * 
 * this is handled at Ref level, not at object level<br>
 */
public class RefsList_L1<Obje> {
	
	private int				cachedSize;		// cached size, prevents parsing the entire list
	private Reference<Obje>	firstRef;			// points to first Ref in list, or null if
	// the list is empty
	private Reference<Obje>	lastRef;			// points to last Ref in list, or null if
	// the list is empty
	private int				modCount	= 0;	// increased by 1 on each operation, useful to see
												
												
	// if someone else modified the list while using
	// a ListCursor
	
	// constructor
	/**
	 * 
	 */
	RefsList_L1() {
		this.setListToEmpty();
	}
	
	
	private void setModified() {
		modCount++;
	}
	
	
	public int getModified() {
		return modCount;
	}
	
	
	/**
	 * 
	 */
	private void setListToEmpty() {
		cachedSize = 0;// increased on add, decreased on remove and related
		firstRef = null;
		lastRef = null;
		this.setModified();
	}
	
	
	/**
	 * @return
	 */
	public int size() {
		return cachedSize;
	}
	
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return ( 0 == this.size() ) || ( firstRef == null ) || ( lastRef == null );
	}
	
	
	/**
	 * @param newLastRef
	 * @return false if already exists; true if it didn't but it does now after
	 *         call
	 */
	public boolean addLast( final Reference<Obje> newLastRef ) {
		assert Q.nn( newLastRef );
		if ( this.containsRef( newLastRef ) ) {
			return false;// already exists
		}
		if ( !newLastRef.isAlone() ) {// this allows null objects
			Q.badCall( "the new Ref must be empty, because we fill next and prev." );
		}
		this.setModified();
		if ( lastRef == null ) {// list is initially empty
			lastRef = firstRef = newLastRef;
		} else {// list not empty
			lastRef.setNext( newLastRef );
			newLastRef.setPrev( lastRef );
			lastRef = newLastRef;
		}
		cachedSize++;
		this.setModified();// again
		return true;
	}
	
	
	/**
	 * @return the firstNodeRef
	 */
	protected Reference<Obje> getFirstRef() {
		return firstRef;
	}
	
	
	/**
	 * @return the lastNodeRef
	 */
	protected Reference<Obje> getLastRef() {
		return lastRef;
	}
	
	
	/**
	 * @param killRef
	 * @return true if removed, false if it was already inexistent
	 */
	public boolean removeRef( final Reference<Obje> killRef ) {
		assert Q.nn( killRef );
		if ( !this.containsRef( killRef ) ) {
			return false;
		}
		
		this.setModified();
		
		final Reference<Obje> prev = killRef.getPrev();// beware if you remove this
		// local var
		final Reference<Obje> next = killRef.getNext();
		if ( prev != null ) {
			prev.setNext( next );
			// killRef.setPrev(null);//beware
		} else {
			if ( firstRef == killRef ) {
				firstRef = next;// can be null
			} else {
				throw Q.bug( "compromised integrity of list" );
			}
		}
		
		if ( next != null ) {
			next.setPrev( prev );// beware
			// killRef.setNext(null);
		} else {
			if ( lastRef == killRef ) {
				lastRef = prev;// can be null
			} else {
				throw Q.bug( "compromised integrity of list (2)" );
			}
		}
		
		// killRef.setObject(null);
		killRef.destroy();
		cachedSize--;
		this.setModified();
		return true;
	}
	
	
	/**
	 * @param whichRef
	 * @return true if the reference already exists; doesn't matter to what
	 *         object it points to
	 */
	public boolean containsRef( final Reference<Obje> whichRef ) {
		assert Q.nn( whichRef );
		Reference<Obje> parser = firstRef;
		while ( null != parser ) {
			if ( whichRef.equals( parser ) ) {
				return true;
			}
			parser = parser.getNext();
		}
		return false;
	}
	
	
	public Reference<Obje> getNodeRefAt( final Location location ) {
		switch ( location ) {
		case FIRST:
			return this.getFirstRef();
		case LAST:
			return this.getLastRef();
		default:
			throw Q.badCall( "undefined location here." );
		}
	}
	
	
	/**
	 * @param location
	 * @param locationNodeRef
	 * @return
	 * @throws Exception
	 */
	public Reference<Obje> getNodeRefAt( final Location location, final Reference<Obje> locationNodeRef ) throws Exception {
		
		assert Q.nn( location );
		assert Q.nn( locationNodeRef );
		
		if ( !this.containsRef( locationNodeRef ) ) {// this will unfortunately
														// parse the list until it
														// finds it
			return null;
		}
		
		switch ( location ) {
		case BEFORE:
			if ( locationNodeRef == null ) {
				// return getLastRef();
				throw new Exception();
			}
			return locationNodeRef.getPrev();
			
		case AFTER:
			if ( locationNodeRef == null ) {
				// return getFirstRef();
				throw new Exception();
			}
			return locationNodeRef.getNext();
			
		case FIRST:
		case LAST:
			return this.getNodeRefAt( location );
			
		default:
			throw Q.badCall( "undefined location within this context" );
		}
	}
	
	
	public ListCursor<Obje> getParser() {
		return new RefsListParser();
	}
	
	// TODO make this class public
	private class RefsListParser implements ListCursor<Obje> {
		
		// the list we're working on, is already being referred to, no need to
		// keep a ref to it
		Reference<Obje>	current;
		Reference<Obje>	copyOfCurrent;
		private int		modCount1;
		
		
		// constructor
		public RefsListParser() {
			current = null;
			this.copyCurrent();
			this.updateModStatus();
		}
		
		
		private void copyCurrent() {
			if ( null == current ) {
				copyOfCurrent = null;
				return;
			}
			copyOfCurrent = new Reference<Obje>( current );
		}
		
		
		/**
		 * @return true if current is null OR current was modified
		 */
		@Override
		public boolean isUndefined() {
			// TODO
			return ( ( null == current ) || ( !current.equals( copyOfCurrent ) ) );
		}
		
		
		/**
		 * tell the iterator to ignore if the list wasn't modified through
		 * itself
		 */
		@SuppressWarnings( "synthetic-access" )
		public void updateModStatus() {
			this.modCount1 = RefsList_L1.this.modCount;
		}
		
		
		/**
		 * @return true if the list was modified NOT using this iterator
		 */
		@SuppressWarnings( "synthetic-access" )
		public boolean modStatus() {
			return ( this.modCount1 != RefsList_L1.this.modCount );
		}
		
		
		@Override
		public Reference<Obje> getCurrentRef() {
			return current;
		}
		
		
		@Override
		public boolean go( final Location location ) throws Exception {
			assert Q.nn( location );
			switch ( location ) {
			case FIRST:
			case LAST:
				current = RefsList_L1.this.getNodeRefAt( location );
				break;
			
			case BEFORE:
			case AFTER:
				if ( this.isUndefined() ) {
					throw new Exception( "undefined state" );
				}
				current = RefsList_L1.this.getNodeRefAt( location, current );
				break;
			default:
				throw Q.badCall( "undefined location within this context" );
			}
			
			this.copyCurrent();
			return ( null != current );
		}
		
		
		@Override
		public boolean go( final Location location, final Reference<Obje> locationRef ) {
			// TODO Auto-generated method stub
			return false;
		}
		
		
		@Override
		public boolean remove( final Location location ) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	// TODO move method that will delete Ref and create a new one OR not
	// TODO addFirst or generalize addLast to insert(whatRef, location,
	// locationRef)
	// TODO parser
}
