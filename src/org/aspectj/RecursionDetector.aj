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


 * File creation: Aug 23, 2010 3:41:31 PM
 */
package org.aspectj;

import java.util.HashMap;
import java.util.HashSet;

import org.aspectj.lang.Signature;
import org.dml.tools.RunTime;
import org.dml.tools.ThreadLocalBoolean;
import org.dml.tools.ThreadLocalHashMap;
import org.dml.tools.ThreadLocalHashSet;
import org.dml.tools.ThreadLocalInteger;

/**
 * in this aspect it's
 * required to use either primitive type variables or variables in classes beginning with ThreadLocal ie. ThreadLocalBoolean
 * because otherwise, a call to a getter method there might return false in case of RunTime.recursiveLoopDetected which is 
 * method context sensitive value
 * - it's thread safe
 * -also, can't use static initializer blocks here on in RunTime if they modify variables used by this aspect; 
 *  or you get NoAspectBoundException
 */
public aspect RecursionDetector
{
	private static final boolean enableRecursionDetection=true;//doesn't depend on enableCallTracing below
	private static final boolean enablePrintRDs=true;//only in effect if above is true
	
	//doesn't depend on enableRecursionDetection above
	private static final boolean enableCallTracing=true;//see RunTime.callTracingFromHere
	private static final long ifRecursionStopAtLevel=5000;//5000 stacked callers, only if enableRecursionDetection true
	  
	//static {
		//these are valid only if one/more of the above are enabled
		//depends on:
		//RunTime.recursiveLoopDetected.set(false); //this is set dynamically by this aspect in each method that has recursion
 
		//if you set this to true anywhere in the program it will start tracing calls until set to false
		//RunTime.callTracingFromHere=false; 
	//}
	 
	//don't change value of these two:
	private static final ThreadLocalBoolean beforeAlready=new ThreadLocalBoolean(false );
	private static final ThreadLocalBoolean afterAlready=new ThreadLocalBoolean(false );
	
	private static final int CALL_LEVEL_INIT=-1;
	private static final ThreadLocalInteger callLevel=new ThreadLocalInteger(CALL_LEVEL_INIT);
	//private static int recursiveLoopDetectedAtLevel=0;
	
	private static final ThreadLocalHashSet<String> calls=new ThreadLocalHashSet<String>();
	//the following variable is unnecessary but just for fun/consistency-checking
	private static final ThreadLocalHashMap<Integer,String> perLevelStore=new ThreadLocalHashMap<Integer,String>();
	
	private static final ThreadLocalHashMap<Integer,Boolean> isLoopAtThisLevel=new ThreadLocalHashMap<Integer,Boolean>();
	
	pointcut anyCall(): call(* *..*..*(..))//any calls to any methods in any package...
						&& !this(RecursionDetector)
						//this is important because within a .get call the var RunTime.recursiveLoopDetected is false
						&& !call(* *..ThreadLocal*..*(..))
						&& !call(* *..Boolean..*(..))//not from Boolean class, excluded classes beginning with Boolean as their name ie. BooleanSome is exacluded
						//&& !call(* *..String..*(..))
						//&& !call(* java.lang.StringBuilder.append(..))
						// enable the following for a quicker response
						//&& !call(* java.io.PrintStream.println(..))
						&& !call(* java.lang.StringBuilder..*(..))
						//&& !call(* java.lang.String..*(..))
						&& !call(* java.lang.StackTraceElement..*(..))
						//&& !call(* java.lang..*..*(..))
						&& !call(* java.io.*..*(..))
						&& !call(* org.dml.tools.RunTime.isAspect*(..))
						&& !call(* org.dml.tools.RunTime.assumed*(..))
						&& !call(* org.dml.tools.RunTime.getCurrentStackTraceElementsArray(..))
						//&& !within(ThreadLocal*)
						//&& !call(public boolean org.temporary.tests.ThreadLocalBoolean.get())
//						&& !target(RecursionDetector)
//						&& !call(* RecursionDetector.*(..));
//						&& !this(TwoWayHashMap)
//						&& !this(RunTime)
//						&& !this(Log);
						; 
	
	before(): anyCall() {
		if ((enableRecursionDetection)||(enableCallTracing)) {
		if (beforeAlready.get()) {
			return;
		}else {
			if (afterAlready.get()) {
				return;
			}
			beforeAlready.set( true);
		}
		try{
			callLevel.increment();//++;//first
		 //System.out.println(Thread.currentThread()+" "+Thread.activeCount() );
			//System.out.println( "AJ: "+RunTime.recursiveLoopDetected+" // "+ RunTime.recursiveLoopDetected.initialValue);
			
		 //common: 2lines
			Signature sig=thisJoinPointStaticPart.getSignature();
			String link=sig2Link(sig,thisJoinPointStaticPart.getSourceLocation().toString());
			
		  if (enableRecursionDetection) {
			String which=sig.toLongString();
			if (null != perLevelStore.get().put( callLevel.get(), which )) {
				//BUG
				throwErr("#100 already existing call at that same level impossible");
			}
			
			RunTime.recursiveLoopDetected.set(!calls.get().add(which));//already there? it then got overwritten with same value
			//System.err.println("A: "+calls.size());
			if (null != isLoopAtThisLevel.get().put(callLevel.get(), RunTime.recursiveLoopDetected.get())) {
				throwErr("#121 a previous value should NOT have existed");
			}
			if (enablePrintRDs) {
				if (RunTime.recursiveLoopDetected.get()) {
					System.err.println("recursion about to begin in thread "+Thread.currentThread()+" at level("+callLevel.get()+") callee(called at line): "+link);
				}
			}
			
			if (callLevel.get() >= ifRecursionStopAtLevel) {
				throwErr("#5000 auto stopping due to detected recursion reaching set limit");
			}
		  }
		  if (enableCallTracing){
			if (RunTime.callTracingFromHere.get()) {
				System.err.println(formLevel("\u250D\u2501 "+RunTime.recursiveLoopDetected.get()+" "+link));
			}
		  }
		
		}finally{
			//last:
			beforeAlready.set(false);
		}
		}//enabled
	}

	after() : anyCall() {//returning normally or via thrown exception
		if ((enableRecursionDetection)||(enableCallTracing)) {
		if (afterAlready.get()){
			return;
		}else {
			if (beforeAlready.get()) {
				return;
			}
			afterAlready.set(true);
		}
		try{
			if (enableRecursionDetection) {
			if (callLevel.get() <=CALL_LEVEL_INIT) {
				throwErr("#000 bug somewhere");
			}
			}
			
			//common: 2lines
			Signature sig=thisJoinPointStaticPart.getSignature();
			String link=sig2Link(sig,thisJoinPointStaticPart.getSourceLocation().toString());
			String which=sig.toLongString();
			
			if (enableRecursionDetection) {
			boolean prevState=RunTime.recursiveLoopDetected.get();//this state in before()
			
			if (isLoopAtThisLevel.get().remove( callLevel.get() ) != prevState) {
				throwErr("#358 "+link+" "+callLevel.get()+" a previous value should have existed and be the same as the state");
			}
		
			if (!prevState) {
				//remove only if not loop detected on current, because it got overwritten last time
				//System.err.println("R: "+calls.size());
				//System.err.flush();
				if (!calls.get().remove( which )) {
					throwErr("#1034 "+link+" should've existed");
				}
			}
			
			
			//restore state
			if (callLevel.get()-1>CALL_LEVEL_INIT) {
				//restore recursion-detected status for our caller
				RunTime.recursiveLoopDetected.set(isLoopAtThisLevel.get().get( callLevel.get()-1 ));
			}else {
				RunTime.recursiveLoopDetected.set(false);
			}
			}
			
			if (enableCallTracing) {
				if (RunTime.callTracingFromHere.get()) {
					System.err.println(formLevel("\u2515\u2501 "+RunTime.recursiveLoopDetected.get()+" "+link));
				}
			}
			if (enableRecursionDetection) {
			
			if (which != perLevelStore.get().remove( callLevel.get() )){//||(callLevel==3)) {
				throwErr("#890 "+link+" should've existed");
			}
			
			
			}
		
		}finally{
			//LAST:
			callLevel.decrement();//--; 
			afterAlready.set(false);
		}
		}//enabled
	}
	
	/**
	 * @param s the method signature that's about to be called or that we're returning from
	 * @param loc location of the call made to "s" ; even when returning
	 * 			so it's not the location of "s", it's the location of the call to "s"
	 *		FIXME: make this the location of "s" instead
	 * @return
	 */
	@SuppressWarnings( "unused" )
	private String sig2Link(Signature s, String loc) {
		String _x=s.toLongString();//gotta get rid of params at end of method, to form valid eclipse console link
		return _x.substring( 0, _x.indexOf( "(" ) )+"("+loc+")";
	}
	
	private String formLevel(String msg) {
		String x="";
		for ( int i = 0; i < callLevel.get(); i++ )
		{
			x+="\u2502  ";
		}
		return String.format( "%-10s%s", Thread.currentThread().getName(), x+msg);
		//String.format( "%-" + callLevel + "s%s", " ",msg);
	}
	
	private static void throwErr(String msg){
		System.err.println(msg);
			throw new RuntimeException(msg+" in thread"+Thread.currentThread()+" in aspect "+RecursionDetector.class);
	}
}
