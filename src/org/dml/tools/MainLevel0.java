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



package org.dml.tools;



import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.dml.tracking.Factory;
import org.references.ListOfUniqueNonNullObjects;
import org.references.Position;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * NOTE: some comments might be old, from a previous version of this class
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
public abstract class MainLevel0 extends Initer {
	
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean									usingOwnVarLevel		= false;
	
	// FIXME: likely a bad idea to be static here:
	private MethodParams							defaults				= null;
	
	private final ListOfUniqueNonNullObjects<Field>	listOfAnnotatedFields	= new ListOfUniqueNonNullObjects<Field>();
	
	// true if at least 1 of the VarLevel fields is not a subclass of SIT ie. maybe is an interface, so we know to throw
	// bug only if we're about to new it ourselves ie. when not supplied by the user as already init-ed VarLevel
	private boolean									notSIT					= false;
	
	// TODO: accept more than 1 variable per subclass, should be easy, maybe add
	// a param to annotation indicating fields that pertain to same group (same
	// group in each subclass equates with same VarLevel)
	
	public MainLevel0() {

		super();
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
				RunTime.assumedTrue( iter.get( this ) == toValue );
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

		RunTime.assumedFalse( notSIT );// so it is a subclass of SIT, that is, it has init() and deInit()
		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		// constructor like new
		Constructor<?> con;
		try {
			con = lastField.getType().getConstructor( (Class<?>[])null );
			this.setAllVarLevelX( con.newInstance( (Object[])null ) );
		} catch ( Exception e ) {
			e.printStackTrace();
			RunTime.bug();
		}
		
	}
	
	private Field getFieldInLastSubClassWhichIs_This() {

		Field ret = listOfAnnotatedFields.getObjectAt( Position.LAST );
		RunTime.assumedTrue( null != ret );
		return ret;
	}
	
	/**
	 * @param obj
	 *            to check if it's at least of the required level type, could be
	 *            higher(subclassed) but not lower(superclass)
	 */
	private void checkVarLevelX( Object obj ) {

		RunTime.assumedNotNull( obj );
		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		if ( !lastField.getType().isAssignableFrom( obj.getClass() ) ) {
			// !true if lastField's class is a superclass(ie. base class) of the
			// obj's class, or the same class; OR obj is a subclass or same
			// class of the lastField's class
			RunTime.badCall( "wrong type passed, must be a subclass of " + lastField.getType().getSimpleName() );
		}
	}
	
	private Initer getVarLevelX() {

		Field lastField = this.getFieldInLastSubClassWhichIs_This();
		Initer ret = null;
		boolean prevState = lastField.isAccessible();
		try {
			if ( !prevState ) {
				lastField.setAccessible( true );
			}
			ret = (Initer)lastField.get( this );
		} catch ( IllegalArgumentException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RunTime.bug();
		} catch ( IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RunTime.bug();
		} finally {
			if ( !prevState ) {
				lastField.setAccessible( prevState );// false
			}
		}
		return ret;
		
	}
	
	/**
	 * @param params
	 *            can accept a passed VarLevel or other params needed to init a
	 *            new VarLevel; if null or empty then defaults are used; params
	 *            are passed down to the VarLevel
	 */
	@Override
	protected void start( MethodParams params ) {

		// allows null argument
		MethodParams mixedParams = this.getDefaults().getClone();
		try {
			if ( null != params ) {
				mixedParams.mergeWith( params, true );// prio on passed params
			}
			
			// FIXME: maybe using same param here is bad idea
			Reference<Object> ref = mixedParams.get( PossibleParams.varLevelAll );
			if ( null == ref ) {
				if ( notSIT ) {
					RunTime.badCall( "caller must either have all VarLevels subclass of "
							+ Initer.class.getSimpleName()
							+ " so that we can new and init the var, or the caller must pass us that var already new-ed and init-ed!" );
				}
				// not specified own VarLevel by user, then we make one which we
				// will deInit later
				if ( null == this.getVarLevelX() ) {
					this.newVarLevelX();// 1
				}
				usingOwnVarLevel = true;// 2
				// FIXME: if this VarLevel is also a MainLevel0 expecting a PossibleParams.varLevelAll we're passing the
				// same above varLevelAll to it, which is bad that's why we need a user settable name in the annotation,
				// or a new random one of each annotation; the latter seems to be a better idea except that on call we
				// don't know what to set
				// if we want to pass a varlevel to the below init? hmm since we did the new maybe we don't have to
				// so maybe all I just said above is crap
				
				// if we're here PossibleParams.varLevelAll doesn't exist so, this init won't see it either, just in
				// case it's a MainLevel0 too.
				// this.getVarLevelX().init( mixedParams );// 3
				Factory.init( this.getVarLevelX(), mixedParams );// 3
			} else {
				Object obj = ref.getObject();
				RunTime.assumedNotNull( obj );
				this.checkVarLevelX( obj );
				this.setAllVarLevelX( obj );
				// it's already inited by caller (assumed) so we won't init it
			}
			// super.start( null );
		} finally {
			// mixedParams.deInit();
			Factory.deInit( mixedParams );
		}
		RunTime.assumedTrue( this.getVarLevelX().isInited() );
	}
	
	/**
	 * override this WITH calling its super first<br>
	 * and set your own defaults<br>
	 * over these defaults are merged the params you pass on init/start<br>
	 * like this:
	 * MethodParams&ltObject&gt def = super.getDefaults();<br>
	 * def.set( PossibleParams.homeDir, DEFAULT_BDB_ENV_PATH );<br>
	 * def.set( PossibleParams.jUnit_wipeDB, false );<br>
	 * return def;<br>
	 * 
	 * @return
	 */
	protected MethodParams getDefaults() {

		if ( null == defaults ) {
			defaults = MethodParams.getNew();// FIXME: when's this deInit-ed?
			// defaults.init( null );
		}
		
		return defaults;
	}
	
	/**
	 * you must deAlloc whatever you want before calling super<br>
	 * 
	 * @see org.dml.tools.StaticInstanceTracker#done()
	 */
	@Override
	protected void done( MethodParams params ) {

		if ( null != this.getVarLevelX() ) {
			// could be not yet inited due to throws in initMainLevel()
			if ( usingOwnVarLevel ) {
				RunTime.assumedFalse( notSIT );
				// we inited it, then we deinit it
				usingOwnVarLevel = false;// 1 //this did the trick
				// ( this.getVarLevelX() ).deInit();// 2
				Factory.deInit( this.getVarLevelX() );// 2
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
							if ( !Initer.class.isAssignableFrom( field.getType() ) ) {
								notSIT = true;
								// RunTime.bug( "wrong field type, must be a subclass of "
								// + StaticInstanceTracker.class.getSimpleName() );
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
			
			// if ( 1 != count ) {
			// // fixed: 1. maybe this is not desired: one field per subclass a
			// // must?
			// // FIXME: 2. maybe we have multiple fields per class... then
			// // what?
			//
			// if ( currentClass != this.getClass() ) {
			// // 3. for now we allow last class to have no annotated
			// // fields
			// RunTime.bug(
			// "you have to annotate just 1 field in each subclass, not more, not less"
			// );
			// }
			// }
			// fixed: allowing 0 or 1 at most, VarLevel -s per subclass
			RunTime.assumedTrue( count <= 1 );// 3 above
			
			// go to prev superclass ie. go next
			currentClass = currentClass.getSuperclass();
		}
		

	}
	
	/**
	 * calls Factory.deInit(this);
	 * this is done because we cannot call Factory.deInit(storage) where storage is an interface and @ VarLevel
	 */
	public void factoryDeInit() {

		Factory.deInit( this );
	}
}
