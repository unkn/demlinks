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
import org.temporary.tests.ThreadLocalBoolean;


/**
 * wrap all non-wrapped thrown exceptions within RuntimeWrappedThrowException<br>
 * FIXME: methods with same name but different params signatures will have bad file:line numbering although the caller 
 * method is detected ok; the correct line is on the internal aspect method names ending like _aroundBody10 but not the
 * $advice ending ones, for example:
 * 
0 java.lang.Thread.getStackTrace(Thread.java:1578)
1 org.dml.tools.RunTime.getCurrentStackTraceElementsArray(RunTime.java:689)
2 org.dml.tools.RunTimeTest.getCurrentStackTraceElementsArray_aroundBody10(RunTimeTest.java:144)
3 org.dml.tools.RunTimeTest.getCurrentStackTraceElementsArray_aroundBody11$advice(RunTimeTest.java:67)
4 org.dml.tools.RunTimeTest.testCaller(RunTimeTest.java:141)
 * the correct line for org.dml.tools.RunTimeTest.testCaller is (RunTimeTest.java:144) from the aroundBody10 aspect method
 * 
0 java.lang.Thread.getStackTrace(Thread.java:1578)
1 org.dml.tools.RunTime.getCurrentStackTraceElementsArray(RunTime.java:689)
2 org.dml.tools.RunTimeTest.getCurrentStackTraceElementsArray_aroundBody10(RunTimeTest.java:144)
3 org.dml.tools.RunTimeTest.getCurrentStackTraceElementsArray_aroundBody11$advice(RunTimeTest.java:75)
4 org.dml.tools.RunTimeTest.testCaller(RunTimeTest.java:141)
141		G g = new G();
142		
143		// don't move relative to each other the following lines!! each must be on 1 line in that order else fails
144		StackTraceElement[] steaR = RunTime.getCurrentStackTraceElementsArray();
 * so you see, the line reported is the line of prev statement ignoring comments
 * the correct report would be org.dml.tools.RunTimeTest.testCaller(RunTimeTest.java:144) so line num from aroundBody10
 */ 
public aspect ThroWrapper {
	private static final ThreadLocalBoolean alreadyCalled=new ThreadLocalBoolean(false );
	
	static {
		//never set this to false, to disable aspect you have to comment all lines
		RunTime.throWrapperAspectEnabled=true;//used to calculate getLine when this aspect is on ie. +2 to location
		//RunTime.throWrapperAspectEnabledJump=+2;//how many more lines ahead until exact location when aspect is on
	}
	
    pointcut anyCall(): call(* *..*..*(..))//any calls to any methods in any package...
    					&& !this(ThroWrapper)//except calls from this aspect
    					&& !call(* *..ThreadLocal*..*(..))//exclude all class names beginning with ThreadLocal ie. includes ThreadLocalBoolean
    					//&& !call(* *..Thread.currentThread(..))
    					&& !call(* *..Boolean..*(..))//excluding Boolean class - or else infinite recursion
    					//&& !call(* *..RunTime.*..*(..))//except methods in RunTime.class
    					//disabling the following you must +4 to getLine ie. 6+2, if enabled 6+2-4
    					//&& !call(* *..Log.*..*(..))//except methods in Log.class due to possible recursion
    					//if you comment the following then add a maybe +2 to location
    					&& !call(public StackTraceElement[] Thread.getStackTrace())
    					//the following three are just to avoid 1 level of unnecessary recursion but only the 1st would be really necessary
    					&& !call(* *..RunTime.internalWrappedThrow(..))
    					&& !call(* *..RunTime.thro(..))
    					&& !call(* *..RunTime.throWrapped(..))
    					//for extra speed
    					&& !call(* java.lang.StringBuilder..*(..))
						&& !call(* java.lang.String..*(..))
						&& !call(* java.lang.StackTraceElement..*(..))
						&& !call(* java.lang..*..*(..))
						&& !call(* java.io.*..*(..))
						&& !call(* org.dml.tools.RunTime.isAspect*(..))
						&& !call(* org.dml.tools.RunTime.assumed*(..))
						&& !call(* org.dml.tools.RunTime.getCurrentStackTraceElementsArray(..))
    					//&& !call(* *..RunTime.assumed*(..))
    					//workaround for line numbering being bad but only for same name methods with obv. diff param signature
    					//&& !call(* RunTime.getCurrentStackTraceElement*(..))
    					;
    					//&& !target(org.dml.tools.RunTime);
    					//;
    

    Object around() : anyCall() {//if (RunTime.throWrapperAspectEnabled == true) && anyCall() {
    	if (alreadyCalled.get()) {
    		return proceed();
    	}else {
    		alreadyCalled.set(true);
    	}
    	//System.out.println("around: "+thisJoinPoint.getSignature());
    	try{
    		alreadyCalled.set(false);
    		try {
    			return proceed();
    		}finally{
    			alreadyCalled.set(true);
    		}
    	}catch(Throwable t) {
    			RunTime.throWrapped( t );//this is caught again
    	}//catch
    	finally{
    		alreadyCalled.set(false);
    	}
    	return null;
    }//around
    
    //NOTE: after() throwing() won't work unless every method traced has checked exceptions declared, else they won't be caught
//    after() throwing (Exception e): publicCall() {
//	  System.out.println("Threw an exception: " + e);
//    }
//    after(): publicCall(){
//	  System.out.println("Returned or threw an Exception");
//    }
}
