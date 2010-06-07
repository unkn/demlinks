/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



package org.references.method;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTracker;
import org.references.Reference;



/**
 * 
 * a list of all parameters passed to a method<br>
 * supposedly formed of objects which are references to real instances like
 * String<br>
 * you may have the same object twice, acting as two different parameters(different ParamName), but
 * this object is in fact a referent to the real instance which is ie. String,
 * 
 * same ParamName cannot have two objects in the same MethodParams list<br>
 * so there's a one to one mapping between a ParamName and it's value<br>
 * although the same ParamName can be used with another value in a different MethodParams<br>
 */
/**
 * 
 *
 */
/**
 * 
 *
 */
/**
 * 
 *
 */
/**
 * 
 *
 */
/**
 * 
 *
 */
public class MethodParams extends StaticInstanceTracker {
	
	// T= base class, usually just Object
	// TODO make this allow any subclass of T
	
	// a list of instances ie. String, Integer, or even null(s) which can repeat
	// ie. A==B
	// objects of this list are the values
	
	// this actually contains references to those objects(but the objects are not touched):
	// private final ListOfObjects<T> listOfParams = new ListOfObjects<T>();
	//
	// private final ListOfUniqueNonNullObjects<ParamName<T>> redundantListOfNames = new
	// ListOfUniqueNonNullObjects<ParamName<T>>();
	
	// ParamName, and it's Value(any subclass of Object or even just Object)
	private final HashMap<ParamName, Reference<Object>>	listOfParamsWithValues	= new HashMap<ParamName, Reference<Object>>();
	
	// can't use a Set or HashSet or TwoWayHashSet because we need to parse the
	// list, which could be done with an Iterator but I forgot why can't
	

	/**
	 * don't forget to call init() and deInit() when done
	 */
	public MethodParams() {

		super();
	}
	
	public int size() {

		return listOfParamsWithValues.size();
	}
	
	
	/**
	 * explicitly get value<br>
	 * 
	 * @param paramName
	 * @return
	 * @throws NoSuchElementException
	 *             if there is no mapping between paramName and a value
	 */
	public Object getEx( ParamName paramName ) {

		Reference<Object> ref = this.get( paramName );
		if ( null == ref ) {
			throw new NoSuchElementException( "a certain parameter was expected but was not specified by caller" );
		} else {
			return ref.getObject();
		}
	}
	
	// private ChainedReference<T> internalGetFirst() {
	//
	// return listOfParams.getRefAt( Position.FIRST );
	// }
	//
	// private ChainedReference<T> internalGetNextOf( ChainedReference<T> afterThis ) {
	//
	// return listOfParams.getRefAt( Position.AFTER, afterThis );
	// }
	
	/**
	 * this method will search for paramName and return a reference to it's
	 * value Object<br>
	 * 
	 * @param paramName
	 * @return reference; null if not found, or the ref pointing to the value,
	 *         the value can be null tho;<br>
	 *         that's why we need a reference because the value pointed to by ref can be null AND also there can be no
	 *         such ref indicated by returning null<br>
	 *         return null if not found; use .getObject() to get the value
	 */
	public Reference<Object> get( ParamName paramName ) {

		RunTime.assumedNotNull( paramName );
		return listOfParamsWithValues.get( paramName );
		
		// int foundCounter = 0;// should not exceed 1
		// ChainedReference<T> found = null;
		// // parse listOfParams and check each element(the reference of each)
		// // against ParamName list
		// ChainedReference<T> citer = this.internalGetFirst();
		// // listOfParams.getRefAt(
		// // Position.FIRST );
		// while ( null != citer ) {
		// // paramName list can have only 1 reference from a MethodParams
		// if ( paramName.contains( citer ) ) {
		// foundCounter++;
		// found = citer;// don't clone; new Reference<T>( iter );// clone
		// // we could do a break; here but we want to make sure that it's
		// // not found more than 1 times, that would mean Bug
		// // can't have the same ParamName listed twice in the same params
		// // list for same method
		// }
		// // go next
		// citer = this.internalGetNextOf( citer );
		// // listOfParams.getRefAt( Position.AFTER, citer );
		// }
		// RunTime.assumedTrue( foundCounter <= 1 );
		// return found;
	}
	
	/**
	 * @param paramName
	 * @param value
	 *            can be null or an object that was already used as a parameter
	 *            one or more times
	 */
	public void set( ParamName paramName, Object value ) {

		RunTime.assumedNotNull( paramName );
		
		Reference<Object> ref = this.get( paramName );
		if ( null == ref ) {
			ref = new Reference<Object>();
			int bug = listOfParamsWithValues.size();// FIXME: remove
			Object oldValue = listOfParamsWithValues.put( paramName, ref );
			RunTime.assumedNull( oldValue );
			RunTime.assumedTrue( listOfParamsWithValues.size() == 1 + bug );
		}
		
		ref.setObject( value );
	}
	
	/**
	 * easy cast wrapper
	 * 
	 * @param paramName
	 * @return
	 */
	public String getExString( ParamName paramName ) {

		RunTime.assumedNotNull( paramName );
		return (String)this.getEx( paramName );
	}
	
	
	/**
	 * @param
	 * @return false if didn't exist; true if it did exist, but it doesn't now
	 *         after call
	 */
	public boolean remove( ParamName paramName ) {

		RunTime.assumedNotNull( paramName );
		return null != listOfParamsWithValues.remove( paramName );
		// ChainedReference<T> cref = this.internalGet( paramName );
		// if ( null == cref ) {
		// return false;// not found
		// } else {// already exists
		// boolean ret = listOfParams.removeRef( cref );
		// RunTime.assumedTrue( ret );
		// paramName.remove( cref );
		// redundantListOfNames.removeObject( paramName );
		// // FIXME: transaction needed
		// return ret;
		// }
	}
	
	/**
	 * this will merge the two, such as <code>this</code> will have both<br>
	 * 
	 * @param withThisNewOnes
	 *            the contents of this parameter list will be copied to <code>this</code>
	 * @param overwrite
	 *            if true then overwrite the params that already exist in <code>this</code> with those that exist in
	 *            <code>withThisNewOnes</code><br>
	 *            clearly this means that those that don't exist in <code>this</code> will be added from
	 *            withThisNewOnes, but those that do
	 *            exist in <code>this</code> will be lost
	 */
	public void mergeWith( MethodParams withThisNewOnes, boolean overwrite ) {

		RunTime.assumedNotNull( withThisNewOnes, overwrite );
		if ( this == withThisNewOnes ) {
			RunTime.badCall( "attempted to merge with self" );
		}
		
		Iterator<Entry<ParamName, Reference<Object>>> iter = withThisNewOnes.getIter();
		

		while ( iter.hasNext() ) {
			Entry<ParamName, Reference<Object>> current = iter.next();
			ParamName paramName = current.getKey();
			Reference<Object> refToValue = current.getValue();
			boolean alreadyExists = this.get( paramName ) != null;
			if ( ( alreadyExists && overwrite ) || ( !alreadyExists ) ) {
				// doesn't reuse refs from the other MethodParams!
				this.set( paramName, refToValue.getObject() );
				// the values are the same:
				RunTime.assumedTrue( this.getEx( paramName ) == withThisNewOnes.getEx( paramName ) );
				// the refs are not the same:
				RunTime.assumedTrue( this.get( paramName ) != withThisNewOnes.get( paramName ) );
			}
		}
		
	}
	
	/**
	 * @return
	 * 
	 */
	private Iterator<Entry<ParamName, Reference<Object>>> getIter() {

		return listOfParamsWithValues.entrySet().iterator();
		
	}
	
	public void clear() {

		listOfParamsWithValues.clear();
		RunTime.assumedTrue( this.size() == 0 );
		
	}
	
	// private ParamName<T> internalGetParamName( ChainedReference<T> thatPointsToThisRef ) {
	//
	// RunTime.assumedNotNull( thatPointsToThisRef );
	//
	// ParamName<T> ret = null;
	// // we need to find the paramname who's value this is
	// ParamName<T> namesIter = this.getFirstParamName();
	// int count = 0;
	// while ( null != namesIter ) {
	// if ( namesIter.contains( thatPointsToThisRef ) ) {
	// // found it, and since it can be only one...we should break;
	// // break; but we won't break , we try find bugs if any
	// count++;
	// // two params cannot contain same ref, even thos
	// // ref.getObject() can be same; so we detect Bug here
	// RunTime.assumedTrue( count <= 1 );
	// ret = namesIter;
	// }
	//
	// // fetch next ParamName
	// namesIter = this.getNextParamName( namesIter );
	// }
	//
	// return ret;
	// }
	
	/**
	 * after calling this, you will need to use deInit() on the returned,
	 * because the returned did an init()
	 * FIXME: apparently bugged
	 * 
	 * @return
	 */
	public MethodParams getClone() {

		MethodParams clone = new MethodParams();
		clone.init( null );// must be null or recursion
		// we parse all ref to values, which are in 'this' and add each to the
		// cloned
		RunTime.assumedTrue( clone.size() == 0 );
		clone.mergeWith( this, false );// there's nothing to overwrite
		RunTime.assumedTrue( clone.size() == this.size() );
		RunTime.assumedTrue( clone.listOfParamsWithValues.size() == listOfParamsWithValues.size() );
		RunTime.assumedTrue( clone != this );
		
		return clone;
	}
	
	@Override
	protected void done( MethodParams params ) {

		this.clear();
		RunTime.assumedTrue( this.size() == 0 );
		RunTime.assumedTrue( listOfParamsWithValues.size() == 0 );
	}
	
	
	@Override
	protected void start( MethodParams params ) {

		// params should be null, passed via init(...)
		RunTime.assumedTrue( null == params );
		
		// assumed it's empty on start, or else bugged
		RunTime.assumedTrue( this.size() == 0 );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return listOfParamsWithValues.toString();
	}
}
