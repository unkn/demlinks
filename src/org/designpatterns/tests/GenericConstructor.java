/**
 * File creation: Jul 1, 2010 2:29:14 AM
 * from the web
 */


package org.designpatterns.tests;


/**
 * 
 *
 */
public class GenericConstructor {
	
	public static void main( String args[] ) {

		try {
			String name = "java.lang.String";
			String methodName = "toLowerCase";
			
			// get String Class
			Class cl = Class.forName( name );
			
			// get the constructor with one parameter
			java.lang.reflect.Constructor constructor = cl.getConstructor( new Class[] {
				String.class
			} );
			
			// create an instance
			Object invoker = constructor.newInstance( new Object[] {
				"REAL'S HOWTO"
			} );
			
			// the method has no argument
			Class arguments[] = new Class[] {};
			
			// get the method
			java.lang.reflect.Method objMethod = cl.getMethod( methodName, arguments );
			
			// convert "REAL'S HOWTO" to "real's howto"
			Object result = objMethod.invoke( invoker, (Object[])arguments );
			
			System.out.println( result );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
