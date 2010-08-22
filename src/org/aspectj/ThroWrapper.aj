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

import org.dml.tools.RunTime;
import org.dml.tools.RuntimeWrappedThrowException;
import org.javapart.logger.Log;


/**
 * wrap all non-wrapped thrown exceptions within RuntimeWrappedThrowException
 *
 */
public aspect ThroWrapper {
    pointcut anyCall(): call(* *.*(..))//any calls to any methods in any package...
    					&& !this(ThroWrapper)//except calls from this aspect
    					&& !call(* RunTime.*(..))//except methods in RunTime.class
    					//disabling the following you must +4 to getLine ie. 6+2, if enabled 6+2-4
    					&& !call(* Log.*(..));//except methods in Log.class due to possible recursion

    Object around() : anyCall() {
    	//System.out.println("around: "+thisJoinPoint.getSignature());
    	try{
    		return proceed();
    	}catch(Throwable t) {
//    		if (t.getClass() ==  RuntimeWrappedThrowException.class) {
//    			System.err.println("EXCEPTED: "+t);
//    			throw new RuntimeWrappedThrowException(t);
////    			try{
////    				t.printStackTrace();
////    				RunTime.bug("this shouldn't happen");
////    			}finally{
////        			System.err.println("       "+ Log.getThisLineLocationWithinAspect( -4));
////    			}
//    		}else {
//    		StackTraceElement[] stea=Thread.currentThread().getStackTrace();
//			for ( int j = 0; j < stea.length; j++ )
//			{
//				System.out.println(stea[j]);
//			}
    			//System.err.println("WRAPPED: "+t);
    			RunTime.throWrapped( t );//this is caught again
//    		}
    			//throw new RuntimeWrappedThrowException(t);
//    			Delegation01.foo( 4, 5, 6 );
//    			try{
//    			}catch(Throwable t2) {
    			//System.err.println("above not thrown");
//    			}
    			//throw new RuntimeException("xx");
    	}//catch
    	return null;
    }//around
    
    //NOTE: after throwing won't work unless every method traced has checked exceptions declared, else they won't be caught
//    after() throwing (Exception e): publicCall() {
//	  System.out.println("Threw an exception: " + e);
//    }
//    after(): publicCall(){
//	  System.out.println("Returned or threw an Exception");
//    }
}
