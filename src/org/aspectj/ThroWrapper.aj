/**
    Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
    Copyright (C) 2005-2010 UnKn     <unkn@users.sourceforge.net>

 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.


 * File creation: Aug 22, 2010 6:53:10 PM
 */
package org.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.designpatterns.tests.Delegation01;
import org.dml.tools.RunTime;
import org.dml.tools.RuntimeWrappedThrowException;


/**
 * 
 *
 */
aspect ThroWrapper {
    pointcut publicCall(): call(void *.*(..)) && !this(ThroWrapper) ;
//    after() returning (Object o): publicCall() {
//	  System.out.println("Returned normally with " + o);
//    }
    
    void around(): publicCall() {
    	System.out.println("around: "+thisJoinPoint.getSignature());
    	//try{
    	Throwable x=null;
    	try{
    		proceed();
    	}catch(Throwable t) {
    		x=t;
    		throw new RuntimeException("wth1");
    	}
    	if (null != x) {
    		throw new RuntimeException("wth2");
    	}
//    	else 
//    		throw new RuntimeException("xx");
    	//}catch(Throwable t) {
//    		StackTraceElement[] stea=Thread.currentThread().getStackTrace();
//			for ( int j = 0; j < stea.length; j++ )
//			{
//				System.out.println(stea[j]);
//			}
//    		if (t.getClass() !=  RuntimeWrappedThrowException.class) {
//    			System.err.println("EXCEPTED: "+t);
    			
    			//RunTime.throWrapped( t );//this won't throw for some reason!?!!
    			//throw new RuntimeWrappedThrowException(t);
    			//Delegation01.foo( 4, 5, 6 );
//    			try{
//    			}catch(Throwable t2) {
    			//System.err.println("above not thrown");
//    			}
    			//throw new RuntimeException("xx");
//    		}
//    	}
    	//return proceed();
    }
    
//    after() throwing (Exception e): publicCall() {
//	  System.out.println("Threw an exception: " + e);
//    }
//    after(): publicCall(){
//	  System.out.println("Returned or threw an Exception");
//    }
}
