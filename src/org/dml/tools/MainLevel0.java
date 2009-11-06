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
	
	// defaults are no params, or no params means use defaults
	private static MethodParams<Object>				emptyParamList			= null;
	
	// var to see if we used init() instead of initMainLevel(...); its only
	// purpose is to prevent init() usage
	private boolean									inited					= false;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean									usingOwnVarLevel		= false;
	
	private static MethodParams<Object>				defaults				= null;
	
	private static MethodParams<Object>				temporaryLevel1Params	= null;
	
	private final ListOfUniqueNonNullObjects<Field>	listOfAnnotatedFields	= new ListOfUniqueNonNullObjects<Field>();
	
	
	public MainLevel0() {

		// since this is static:
		if ( null == emptyParamList ) {
			emptyParamList = new MethodParams<Object>();
			emptyParamList.init();
			// FIXME: when is this deInited? should be when last instance is
			// deInited, but can't compare class names, could be Level3 and
			// Level2 classes, but we can't deInit on last Level3.deInit
			// true that a deInit is not really needed, but as a concept...when?
		}
		
		if ( null == temporaryLevel1Params ) {
			temporaryLevel1Params = new MethodParams<Object>();
			temporaryLevel1Params.init();
			// FIXME: when's this deInited also?
		}
	}
	
	/**
	 * 1of5 call SUPER
	 * must override this in each level AND call super at end or beginning<br>
	 * <code>var1 = (VarLevel1)toValue;<br></code> it's important that these be
	 * chained by SUPER so that each field on each subclass is set to the new
	 * instance (which is only one)<br>
	 * 
	 * @param toValue
	 */
	protected void setVarLevelX( Object toValue ) {

		this.test1();
	}
	
	

	/**
	 * 2of5
	 * must override this in each Level w/o calling super, and use the right
	 * type<br>
	 * <code>
	 * var1 = new VarLevel1();<br></code>
	 */
	abstract protected void newVarLevelX();
	
	/**
	 * 3of5
	 * must override in each level w/o calling super<br>
	 * this method must make sure the obj is of VarLevelX type depending on the
	 * current variable type used in the class<br>
	 * forgetting to override this may cause unexpected bugs but you can see it
	 * when you get NullPointerException when calling a method only available in
	 * a later level<br>
	 * <code>if ( !( obj instanceof VarLevel1 ) ) {<br>
			// cannot be under VarLevel1, can be above tho<br>
			RunTime.badCall( "wrong type passed" );<br>
		}<br></code>
	 * 
	 * @param obj
	 */
	abstract protected void checkVarLevelX( Object obj );
	
	/**
	 * 4of5
	 * must override this, and don't call super <br>
	 * <code>return var1;<br></code>
	 * 
	 * @return
	 */
	abstract protected StaticInstanceTrackerWithMethodParams getVarLevelX();
	
	/**
	 * DO NOT override this, ever
	 * 
	 * @param varAny
	 *            the VarLevel at this level
	 * @param params
	 *            that must be passed to a super.initMainLevel()
	 * @return
	 */
	protected final MethodParams<Object> preInit( MethodParams<Object> params ) {

		// this part will have to be called in each subclass once
		// if it's just super()-ed it won't do because each private VarLevel in
		// each class would have to be set to the last instance
		MethodParams<Object> refToParams = params;
		if ( null == refToParams ) {
			// empty means use defaults
			refToParams = emptyParamList;
		}
		RunTime.assertNotNull( refToParams );
		
		// optional param, but the top level will supply this if toplevel exists
		// or the user will supply this if it is so desired but he will be
		// responsible for it being inited/deinited
		Reference<Object> ref = refToParams.get( PossibleParams.varLevelAll );
		if ( null == ref ) {
			// no VarLevelX given thus must use defaults for VarLevelX
			// maybe use some defaults ie. homeDir value to default
			if ( null == this.getVarLevelX() ) {
				// setVarLevelX(
				this.newVarLevelX();// );
			}
			usingOwnVarLevel = true;// 2
			

			// TODO avoid new-ing this every time; clone does the new
			MethodParams<Object> moo = this.getDefaults().getClone();
			// using defaults but overwriting them with params
			moo.mergeWith( refToParams, true );
			( this.getVarLevelX() ).init( moo );// 3
			moo.deInit();
			
			// set this for Level1
			synchronized ( temporaryLevel1Params ) {
				temporaryLevel1Params.set( PossibleParams.varLevelAll,
						this.getVarLevelX() );
				RunTime.assertTrue( temporaryLevel1Params.size() == 1 );
			}
			refToParams = temporaryLevel1Params;
		} else {
			Object obj = ref.getObject();
			RunTime.assertNotNull( obj );
			this.checkVarLevelX( obj );
			this.setVarLevelX( obj );
		}
		

		return refToParams;
	}
	
	/**
	 * override this and call internalInit(...) then super with the returned
	 * value<br>
	 * this method was previously named initMainLevel
	 * ie.<br>
	 * <code>super.init( preInit( var1, params ) );</code>
	 * 
	 * @param params
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		
		RunTime.assertNotNull( params );
		inited = true;// first
		super.init();// second
	}
	
	/**
	 * 5of5
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
				this.setVarLevelX( null );
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
	
	
	private void test1() {

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
							listOfAnnotatedFields.addFirst( field );
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
				RunTime.bug( "you have to annotate at least 1 field in each subclass" );
			}
			RunTime.assertTrue( 1 == count );
			
			// go to prev superclass ie. go next
			currentClass = currentClass.getSuperclass();
		}
		
		Field iter = listOfAnnotatedFields.getObjectAt( Position.FIRST );
		while ( null != iter ) {
			boolean prevState = iter.isAccessible();
			try {
				iter.setAccessible( true );
				System.out.println( iter.getName() + " / " + iter.getType()
						+ " / " + iter.get( this ) );
			} catch ( IllegalArgumentException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch ( IllegalAccessException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				iter.setAccessible( prevState );
			}
			
			// next
			iter = listOfAnnotatedFields.getObjectAt( Position.AFTER, iter );
		}
	}
}
