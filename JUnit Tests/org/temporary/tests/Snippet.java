/**
 * File creation: Nov 6, 2009 1:00:16 AM
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

import org.dml.tools.VarLevel;



/**
 * 
 *
 */
public class Snippet {
	
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
}
