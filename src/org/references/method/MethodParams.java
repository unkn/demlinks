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



import java.util.NoSuchElementException;

import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTracker;
import org.references.ChainedReference;
import org.references.ListOfObjects;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.Reference;



/**
 * 
 * a list of all parameters passed to a method<br>
 * supposedly formed of objects which are references to real instances like
 * String<br>
 * you may have the same object twice, acting as two different parameters, but
 * this object is in fact a referent to the real instance which is ie. String,
 * 
 * same ParamName cannot have two objects in the same MethodParams list<br>
 */
public class MethodParams<T> extends StaticInstanceTracker {
	
	// T= base class, usually just Object
	// TODO make this allow any subclass of T
	
	// a list of instances ie. String, Integer, or even null(s) which can repeat
	// ie. A==B
	// objects of this list are the values
	private final ListOfObjects<T>							listOfParams			= new ListOfObjects<T>();
	private final ListOfUniqueNonNullObjects<ParamName<T>>	redundantListOfNames	= new ListOfUniqueNonNullObjects<ParamName<T>>();
	
	// can't use a Set or HashSet or TwoWayHashSet because we need to parse the
	// list, which could be done with an Iterator but I forgot why can't
	

	/**
	 * don't forget to call init() and deInit() when done
	 */
	public MethodParams() {

		super();
	}
	
	public int size() {

		return listOfParams.size();
	}
	
	
	public T getEx( ParamName<T> paramName ) {

		Reference<T> ref = this.get( paramName );
		if ( null == ref ) {
			throw new NoSuchElementException( "a certain parameter was expected but was not specified by caller" );
		} else {
			return ref.getObject();
		}
	}
	
	/**
	 * this method will search for paramName and return a reference to it's
	 * value Object<br>
	 * 
	 * @param paramName
	 * @return null if not found; use .getObject() to get the value
	 */
	public Reference<T> get( ParamName<T> paramName ) {

		// what this does is get the list paramName and intersect it with
		// the MethodParams list and should find 0 or 1 elements in common, if
		// more than 1 then maybe throw BadCallError or Bug
		RunTime.assumedNotNull( paramName );
		
		return this.internalGet( paramName );
	}
	
	private ChainedReference<T> internalGetFirst() {

		return listOfParams.getRefAt( Position.FIRST );
	}
	
	private ChainedReference<T> internalGetNextOf( ChainedReference<T> afterThis ) {

		return listOfParams.getRefAt( Position.AFTER, afterThis );
	}
	
	/**
	 * @param paramName
	 * @return reference; null if not found, or the ref pointing to the value,
	 *         the value can be null tho
	 */
	private ChainedReference<T> internalGet( ParamName<T> paramName ) {

		RunTime.assumedNotNull( paramName );
		
		int foundCounter = 0;// should not exceed 1
		ChainedReference<T> found = null;
		// parse listOfParams and check each element(the reference of each)
		// against ParamName list
		ChainedReference<T> citer = this.internalGetFirst();
		// listOfParams.getRefAt(
		// Position.FIRST );
		while ( null != citer ) {
			// paramName list can have only 1 reference from a MethodParams
			if ( paramName.contains( citer ) ) {
				foundCounter++;
				found = citer;// don't clone; new Reference<T>( iter );// clone
				// we could do a break; here but we want to make sure that it's
				// not found more than 1 times, that would mean Bug
				// can't have the same ParamName listed twice in the same params
				// list for same method
			}
			// go next
			citer = this.internalGetNextOf( citer );
			// listOfParams.getRefAt( Position.AFTER, citer );
		}
		RunTime.assumedTrue( foundCounter <= 1 );
		return found;
	}
	
	/**
	 * @param paramName
	 * @param value
	 *            can be null or an object that was already used as a parameter
	 *            one or more times
	 */
	public void set( ParamName<T> paramName, T value ) {

		RunTime.assumedNotNull( paramName );
		
		ChainedReference<T> cref = this.internalGet( paramName );
		if ( null == cref ) {
			int bug = listOfParams.size();// FIXME: remove
			cref = listOfParams.addFirst( value );
			RunTime.assumedTrue( listOfParams.size() == 1 + bug );
			paramName.add( cref );
			redundantListOfNames.addFirst( paramName );
			// FIXME: transaction needed
		} else {// already exists, must change
			cref.setObject( value );
		}
		
	}
	
	/**
	 * easy cast wrapper
	 * 
	 * @param paramName
	 * @return
	 */
	public String getExString( ParamName<T> paramName ) {

		RunTime.assumedNotNull( paramName );
		return (String)this.getEx( paramName );
	}
	
	
	/**
	 * @param varLevelAll
	 * @return false if didn't exist; true if it didn't; but it doesn't now
	 *         after call
	 */
	public boolean remove( ParamName<T> paramName ) {

		RunTime.assumedNotNull( paramName );
		
		ChainedReference<T> cref = this.internalGet( paramName );
		if ( null == cref ) {
			return false;// not found
		} else {// already exists
			boolean ret = listOfParams.removeRef( cref );
			RunTime.assumedTrue( ret );
			paramName.remove( cref );
			redundantListOfNames.removeObject( paramName );
			// FIXME: transaction needed
			return ret;
		}
	}
	
	/**
	 * this will merge the two, such as <code>this</code> will have both<br>
	 * 
	 * @param withThisNewOnes
	 *            the contents of this parameter will be copied to <code>this</code>
	 * @param overwrite
	 *            if true then overwrite the params that already exist in <code>this</code> with those that exist in
	 *            <code>withThisNewOnes</code><br>
	 *            clearly this means that those that don't exist in <code>this</code> will be added, but those that do
	 *            exist in <code>this</code> will be lost
	 */
	public void mergeWith( MethodParams<T> withThisNewOnes, boolean overwrite ) {

		RunTime.assumedNotNull( withThisNewOnes );
		
		ParamName<T> iter = withThisNewOnes.getFirstParamName();
		while ( null != iter ) {
			boolean already = this.get( iter ) != null;
			if ( ( already && overwrite ) || ( !already ) ) {
				this.set( iter, withThisNewOnes.getEx( iter ) );
			}
			iter = withThisNewOnes.getNextParamName( iter );
		}
		
	}
	
	public void clear() {

		ParamName<T> iter = this.getFirstParamName();
		while ( null != iter ) {
			ParamName<T> next = this.getNextParamName( iter );
			this.remove( iter );
			iter = next;
		}
		RunTime.assumedTrue( this.size() == 0 );
		RunTime.assumedTrue( redundantListOfNames.size() == 0 );
		
	}
	
	private ParamName<T> getFirstParamName() {

		return redundantListOfNames.getObjectAt( Position.FIRST );
	}
	
	private ParamName<T> getNextParamName( ParamName<T> nextOfThis ) {

		return redundantListOfNames.getObjectAt( Position.AFTER, nextOfThis );
	}
	
	private ParamName<T> internalGetParamName( ChainedReference<T> thatPointsToThisRef ) {

		RunTime.assumedNotNull( thatPointsToThisRef );
		
		ParamName<T> ret = null;
		// we need to find the paramname who's value this is
		ParamName<T> namesIter = this.getFirstParamName();
		int count = 0;
		while ( null != namesIter ) {
			if ( namesIter.contains( thatPointsToThisRef ) ) {
				// found it, and since it can be only one...we should break;
				// break; but we won't break , we try find bugs if any
				count++;
				// two params cannot contain same ref, even thos
				// ref.getObject() can be same; so we detect Bug here
				RunTime.assumedTrue( count <= 1 );
				ret = namesIter;
			}
			
			// fetch next ParamName
			namesIter = this.getNextParamName( namesIter );
		}
		
		return ret;
	}
	
	/**
	 * after calling this, you will need to use deInit() on the returned,
	 * because the returned did an init()
	 * 
	 * @return
	 */
	public MethodParams<T> getClone() {

		MethodParams<T> clone = new MethodParams<T>();
		clone.init( null );// must be null or recursion
		// we parse all ref to values, which are in 'this' and add each to the
		// cloned
		ChainedReference<T> ref = this.internalGetFirst();
		while ( null != ref ) {
			ParamName<T> pName = this.internalGetParamName( ref );
			RunTime.assumedNotNull( pName );
			clone.set( pName, ref.getObject() );
			// fetch next ref to value
			ref = this.internalGetNextOf( ref );
		}
		
		RunTime.assumedTrue( clone.size() == this.size() );
		RunTime.assumedTrue( clone.redundantListOfNames.size() == this.redundantListOfNames.size() );
		
		return clone;
	}
	
	@Override
	protected void done( MethodParams<Object> params ) {

		this.clear();
		RunTime.assumedTrue( this.size() == 0 );
		RunTime.assumedTrue( redundantListOfNames.size() == 0 );
	}
	
	
	@Override
	protected void start( MethodParams<Object> params ) {

		// params should be null, passed via init(...)
		RunTime.assumedTrue( null == params );
		
		// assumed it's empty on start, or else bugged
		RunTime.assumedTrue( this.size() == 0 );
		RunTime.assumedTrue( redundantListOfNames.size() == 0 );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return listOfParams.toString();
	}
}
