/**
 * File creation: Nov 4, 2009 5:59:16 PM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
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


package org.dml.tools;



import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.Reference;
import org.references.method.MethodParams;
import org.temporary.tests.PossibleParams;
import org.temporary.tests.VarLevel;



/**
 * must not call super() on some methods because remember that the VarLevel is
 * Overridden on the next level<br>
 * 
 * 2. VarLevel field in each subclass(or level) must be declared private and all
 * *VarLevelX* methods must be overridden without calling their super, and they
 * will operate on this private field; with the exception of setVarLevelX which
 * must call super always<br>
 * 3. there is only one VarLevel instance no matter at which level we are, once
 * the class is instantiated
 * 4. as said in 2. you must override w/o calling super, the following methods:
 * getVarLevelX, checkVarLevelX, newVarLevelX;
 * 5. always call super on setVarLevelX and always override it
 * 6. VarLevelX variable must extend StaticInstanceTrackerWithMethodParams
 * 7. override getDefaults() and call its super first, and set your defaults
 * that will be used when params are missing or null instead of them
 */
public abstract class MainLevel0 extends StaticInstanceTrackerWithMethodParams {
	
	// var to see if we used init() instead of initMainLevel(...); its only
	// purpose is to prevent init() usage
	private boolean									inited					= false;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean									usingOwnVarLevel		= false;
	
	private static MethodParams<Object>				defaults				= null;
	
	private final ListOfUniqueNonNullObjects<Field>	listOfAnnotatedFields	= new ListOfUniqueNonNullObjects<Field>();
	
	// TODO: accept more than 1 variable per subclass, should be easy, maybe add
	// param to annotation
	
	public MainLevel0() {

		this.processAnnotatedFields();
	}
	
	private void setAllVarLevelX( Object toValue ) {

		Field iter = listOfAnnotatedFields.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			boolean prevState = iter.isAccessible();
			try {
				iter.setAccessible( true );
				// System.out.println( iter.getName() + " / " + iter.getType()
				// + " / " + iter.get( this ) );
				iter.set( this, toValue );
				RunTime.assertTrue( iter.get( this ) == toValue );
			} catch ( IllegalArgumentException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RunTime.bug();
			} catch ( IllegalAccessException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RunTime.bug();
			} finally {
				iter.setAccessible( prevState );
			}
			
			// next
			iter = listOfAnnotatedFields.getObjectAt( Position.AFTER, iter );
		}
	}
	
	

	private void newVarLevelX() {

		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		Constructor<?> con;
		try {
			con = lastField.getType().getConstructor( (Class<?>[])null );
			this.setAllVarLevelX( con.newInstance( (Object[])null ) );
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RunTime.bug();
		}
		
	}
	
	private Field getFieldInLastSubClassWhichIs_This() {

		Field ret = listOfAnnotatedFields.getObjectAt( Position.LAST );
		RunTime.assertTrue( null != ret );
		return ret;
	}
	
	/**
	 * @param obj
	 *            to check if it's at least of the required level type, could be
	 *            higher(subclassed) but not lower(superclass)
	 */
	private void checkVarLevelX( Object obj ) {

		RunTime.assertNotNull( obj );
		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		if ( !lastField.getType().isAssignableFrom( obj.getClass() ) ) {
			// !true if lastField's class is a superclass(ie. base class) of the
			// obj's class, or the same class; OR obj is a subclass or same
			// class of the lastField's class
			RunTime.badCall( "wrong type passed, must be a subclass of "
					+ lastField.getType().getSimpleName() );
		}
	}
	
	private StaticInstanceTrackerWithMethodParams getVarLevelX() {

		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		StaticInstanceTrackerWithMethodParams ret = null;
		boolean prevState = lastField.isAccessible();
		try {
			lastField.setAccessible( true );
			ret = (StaticInstanceTrackerWithMethodParams)lastField.get( this );
		} catch ( IllegalArgumentException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RunTime.bug();
		} catch ( IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RunTime.bug();
		} finally {
			lastField.setAccessible( prevState );
		}
		return ret;
		
	}
	
	/**
	 * override this and call super
	 * 
	 * @param params
	 *            can accept a passed VarLevel or other params needed to init a
	 *            new VarLevel; if null or empty then defaults are used
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		// allows null argument
		MethodParams<Object> mixedParams = this.getDefaults().getClone();
		try {
			if ( null != params ) {
				mixedParams.mergeWith( params, true );// prio on passed params
			}
			
			Reference<Object> ref = mixedParams.get( PossibleParams.varLevelAll );
			if ( null == ref ) {
				// not specified own VarLevel by user, then we make one which we
				// will deInit later
				if ( null == this.getVarLevelX() ) {
					this.newVarLevelX();// 1
				}
				usingOwnVarLevel = true;// 2
				this.getVarLevelX().init( mixedParams );// 3
			} else {
				Object obj = ref.getObject();
				RunTime.assertNotNull( obj );
				this.checkVarLevelX( obj );
				this.setAllVarLevelX( obj );
				// it's already inited by caller (assumed) so we won't init it
			}
			inited = true;
			super.init();
		} finally {
			mixedParams.deInit();
		}
	}
	
	/**
	 * override this WITH calling its super first<br>
	 * and set your own defaults
	 * 
	 * @return
	 */
	protected MethodParams<Object> getDefaults() {

		if ( null == defaults ) {
			defaults = new MethodParams<Object>();
			defaults.init();// FIXME: when's this deInit-ed?
		}
		
		return defaults;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done() {

		inited = false;// first
		
		if ( null != this.getVarLevelX() ) {
			// could be not yet inited due to throws in initMainLevel()
			if ( usingOwnVarLevel ) {
				// we inited it, then we deinit it
				usingOwnVarLevel = false;// 1 //this did the trick
				( this.getVarLevelX() ).deInit();// 2
				// not setting it to null, since we might use it on the next
				// call
			} else {
				this.setAllVarLevelX( null );
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#start()
	 */
	@Override
	protected void start() {

		if ( !inited ) {
			// called init() which is not supported
			RunTime.badCall( "please don't use init() w/o params" );
			// this.initMainLevel( null );this won't work, init() recursion
		}
	}
	
	
	/**
	 * fields from subclasses
	 */
	private void processAnnotatedFields() {

		Class<?> currentClass = this.getClass();
		while ( currentClass != MainLevel0.class ) {
			Field[] fields = currentClass.getDeclaredFields();
			
			// how many fields are VarLevel annotated per class
			int count = 0;
			
			for ( Field field : fields ) {
				Annotation[] allAnno = field.getAnnotations();
				for ( Annotation annotation : allAnno ) {
					if ( annotation instanceof VarLevel ) {
						count++;
						
						boolean prev = field.isAccessible();
						try {
							field.setAccessible( true );
							// System.out.println( field.get( this )
							// + " / "
							// + field.getType()
							// + " / "
							// +
							// StaticInstanceTrackerWithMethodParams.class.isInstance(
							// field.get( this ) )
							// + field.getType().isAssignableFrom(
							// StaticInstanceTrackerWithMethodParams.class )
							// + " / "
							// +
							// StaticInstanceTrackerWithMethodParams.class.isAssignableFrom(
							// field.getType() ) );
							// if ( !( field.get( this ) instanceof
							// StaticInstanceTrackerWithMethodParams ) ) {
							if ( !StaticInstanceTrackerWithMethodParams.class.isAssignableFrom( field.getType() ) ) {
								RunTime.bug( "wrong field type, must be a subclass of "
										+ StaticInstanceTrackerWithMethodParams.class.getSimpleName() );
							}
							// make sure this class' field is last!
							// by using LIFO
							listOfAnnotatedFields.addFirst( field );
						} catch ( IllegalArgumentException e ) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							RunTime.bug();
						} finally {
							field.setAccessible( prev );
						}// try
					}// if
				}// for
			}// for
			
			if ( 1 != count ) {
				// FIXME: 1. maybe this is not desired: one field per subclass a
				// must?
				// FIXME: 2. maybe we have multiple fields per class... then
				// what?
				RunTime.bug( "you have to annotate just 1 field in each subclass, not more, not less" );
			}
			RunTime.assertTrue( 1 == count );
			
			// go to prev superclass ie. go next
			currentClass = currentClass.getSuperclass();
		}
		

	}
}
