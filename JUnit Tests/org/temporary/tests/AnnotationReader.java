/**
 * File creation: Nov 5, 2009 8:16:44 AM
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



/**
 * 
 *
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;



public class AnnotationReader {
	
	public static void main( String args[] ) throws Exception {

		Class<AnnotatedClass> classObject = AnnotatedClass.class;
		readAnnotation( classObject );
		Method method1 = classObject.getMethod( "annotatedMethod1",
				new Class[] {} );
		readAnnotation( method1 );
		Method method2 = classObject.getMethod( "annotatedMethod2",
				new Class[] {} );
		readAnnotation( method2 );
	}
	
	static void readAnnotation( AnnotatedElement element ) {

		try {
			System.out.println( "\nFinding annotations on "
					+ element.getClass().getName() );
			Annotation[] classAnnotations = element.getAnnotations();
			
			for ( Annotation annotation : classAnnotations ) {
				if ( annotation instanceof Author ) {
					Author author = (Author)annotation;
					System.out.println( "Author name:" + author.name() );
				} else if ( annotation instanceof Version ) {
					Version version = (Version)annotation;
					System.out.println( "Version number:" + version.number() );
				}
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}
