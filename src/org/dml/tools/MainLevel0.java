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
import org.references.method.PossibleParams;



/**
 * 
 * this allows all subclasses to level up and use one field that will also
 * level up
 * 
 * 1. VarLevel field in each subclass(or level) should be declared private and
 * must extend StaticInstanceTracker and thus cannot be an
 * interface type<br>
 * ... there is only one VarLevel instance no matter at which level we are, once
 * the class is instantiated, all private fields marked with VarLevel will point
 * to the same instance which is of same type as the last subclass (which
 * instantiated it)<br>
 * 2. override getDefaults() and call its super first, and set your defaults
 * that will be used when params are missing or null instead of them or params
 * that are passed to the VarLevel init(..)
 * 3. the VarLevel and the MainLevel1 class have to extend
 * StaticInstanceTracker
 */
public abstract class MainLevel0 extends StaticInstanceTracker {
	
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean									usingOwnVarLevel		= false;
	
	private static MethodParams<Object>				defaults				= null;
	
	private final ListOfUniqueNonNullObjects<Field>	listOfAnnotatedFields	= new ListOfUniqueNonNullObjects<Field>();
	
	// TODO: accept more than 1 variable per subclass, should be easy, maybe add
	// a param to annotation indicating fields that pertain to same group (one
	// group per subclass)
	
	public MainLevel0() {

		this.processAnnotatedFields();
	}
	
	private void setAllVarLevelX( Object toValue ) {

		Field iter = listOfAnnotatedFields.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			boolean prevState = iter.isAccessible();
			try {
				iter.setAccessible( true );
				iter.set( this, toValue );
				// System.out.println( iter.getName() + " / " + iter.getType()
				// + " / " + iter.get( this ) );
				RunTime.assertTrue( iter.get( this ) == toValue );
			} catch ( IllegalArgumentException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RunTime.bug( "this usually happens when the VarLevel in each subclass are of class types that are not subclasses of previous VarLevel's type" );
				// for example: class A has Z var; and class B extends A has X
				// var and X doesn't extend Z, that is bad it should extend Z
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
	
	private StaticInstanceTracker getVarLevelX() {

		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		StaticInstanceTracker ret = null;
		boolean prevState = lastField.isAccessible();
		try {
			lastField.setAccessible( true );
			ret = (StaticInstanceTracker)lastField.get( this );
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
	 *            new VarLevel; if null or empty then defaults are used; params
	 *            are passed down to the VarLevel
	 */
	@Override
	protected void start( MethodParams<Object> params ) {

		// allows null argument
		MethodParams<Object> mixedParams = this.getDefaults().getClone();
		try {
			if ( null != params ) {
				mixedParams.mergeWith( params, true );// prio on passed params
			}
			
			// FIXME: maybe using same param here is bad idea
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
			// super.start( null );
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
			defaults.init( null );// FIXME: when's this deInit-ed?
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
							if ( !StaticInstanceTracker.class.isAssignableFrom( field.getType() ) ) {
								RunTime.bug( "wrong field type, must be a subclass of "
										+ StaticInstanceTracker.class.getSimpleName() );
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
