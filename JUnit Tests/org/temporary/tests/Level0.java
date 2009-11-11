/**
 * File creation: Nov 5, 2009 9:07:12 PM
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


package org.temporary.tests;



import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.dml.tools.RunTime;
import org.dml.tools.StaticInstanceTrackerWithMethodParams;
import org.dml.tools.VarLevel;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;



/**
 * 
 *
 */
@Deprecated
public abstract class Level0 extends StaticInstanceTrackerWithMethodParams {
	
	private static MethodParams<Object>	defaults			= null;
	
	// true if we inited a default 'var' so we know to deInit it
	// we won't deInit passed 'var' param
	private boolean						usingOwnVarLevel	= false;
	
	// var to see if we used init() instead of initMainLevel(...); its only
	// purpose is to prevent init() usage
	private boolean						inited				= false;
	
	@SuppressWarnings( "unused" )
	private void test1() {

		// FIXME temporary, delete this
		// System.out.println( this.getClass() );
		Field[] fields = this.getClass().getDeclaredFields();
		// System.out.println( fields.length );
		int count = 0;
		for ( Field field : fields ) {
			Annotation[] allAnno = field.getAnnotations();
			// System.out.println( allAnno.length );
			for ( Annotation annotation : allAnno ) {
				count++;
				System.out.println( count );
				if ( annotation instanceof VarLevel ) {
					System.out.println( annotation + "+" + field.getName()
							+ "+" + field.getType() );
					try {
						System.out.println( "Before: " + field.get( this ) );
						Constructor<?> con = field.getType().getConstructor(
								null );
						field.set( this, con.newInstance( null ) );
						System.out.println( "After : " + field.get( this ) );
					} catch ( IllegalArgumentException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch ( IllegalAccessException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch ( SecurityException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch ( NoSuchMethodException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch ( InstantiationException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch ( InvocationTargetException e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		}
	}
	
	/**
	 * uses: PossibleParams.varLevelAll
	 * 
	 * @see org.dml.tools.StaticInstanceTrackerWithMethodParamsInterface#init(org
	 *      .references.method.MethodParams)
	 */
	@Override
	public void init( MethodParams<Object> params ) {

		// allows null argument
		MethodParams<Object> mixedParams = this.getDefaults().getClone();
		if ( null != params ) {
			mixedParams.mergeWith( params, true );// prio on passed params
		}
		try {
			Reference<Object> ref = mixedParams.get( PossibleParams.varLevelAll );
			if ( null == ref ) {
				// not specified own storage
				if ( null == this.getVarLevelX() ) {
					this.newVarLevelX();
					// storageL1 = new Level2_BerkeleyDBStorage();
				}
				usingOwnVarLevel = true;
				this.getVarLevelX().init( mixedParams );
				
			} else {
				Object obj = ref.getObject();
				RunTime.assertNotNull( obj );
				this.checkVarLevelX( obj );
				this.setVarLevelX( obj );
				// it's already inited by caller (assumed)
			}
			inited = true;
			super.init();
		} finally {
			mixedParams.deInit();
		}
	}
	
	protected MethodParams<Object> getDefaults() {

		if ( null == defaults ) {
			defaults = new MethodParams<Object>();
			defaults.init();
			// FIXME: when's this deInited, true it doesn't really have to
			// deInit
		}
		return defaults;
	}
	
	abstract protected StaticInstanceTrackerWithMethodParams getVarLevelX();
	
	/**
	 * 1of2
	 * must override this in each Level w/o calling super, and use the right
	 * type<br>
	 * <code>
	 * var1 = new VarLevel1();<br>
		return var1;<br></code>
	 */
	abstract protected void newVarLevelX();
	
	/**
	 * override only in base class
	 * 
	 * @param toValue
	 */
	abstract protected void setVarLevelX( Object toValue );
	
	/**
	 * 2of2
	 * override this in each subclass
	 * 
	 * * must override in each level w/o calling super<br>
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
				this.getVarLevelX().deInit();// 2
				// not setting it to null, since we might use it on the next
				// call
			} else {
				this.setVarLevelX( null );
			}
		}
	}
	

}
