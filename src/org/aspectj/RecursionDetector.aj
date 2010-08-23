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

/**
 * 
 *
 */
public aspect RecursionDetector
{
	static {
		RunTime.recursiveLoopDetected=false; 
		//RunTime.recursionObject=null;
	}
	private static final int CALL_LEVEL_INIT=-1;
	private static int callLevel=CALL_LEVEL_INIT;
	//private static int recursiveLoopDetectedAtLevel=0;
	
	private static HashSet<String> calls=new HashSet<String>();
	//the following variable is unnecessary but just for fun/consistency-checking
	private static HashMap<Integer,String> perLevelStore=new HashMap<Integer,String>();
	
	private static HashMap<Integer,Boolean> isLoopAtThisLevel=new HashMap<Integer,Boolean>();
	
	pointcut anyCall(): call(* *.*(..))//any calls to any methods in any package...
						&& !this(RecursionDetector);
	
	before(): anyCall() {
		callLevel++;
		Signature sig=thisJoinPointStaticPart.getSignature();
		String link=sig2Link(sig,thisJoinPointStaticPart.getSourceLocation().toString());
	//System.err.println(formLevel("\u250D "+RunTime.recursiveLoopDetected+" "+link));
		String which=sig.toLongString();
		if (null != perLevelStore.put( callLevel, which )) {
			//BUG
			err("#100 already existing call at that same level impossible");
		}
		
		boolean recursionYes=!calls.add(which);//already there? it then got overwritten with same value
		if (null != isLoopAtThisLevel.put(callLevel, recursionYes)) {
			err("#121 a previous value shouldn't have existed");
		}
		if (recursionYes) {
			RunTime.recursiveLoopDetected=true;
			System.err.println("recursion about to begin at: "+link);
		}
	}

	after() : anyCall() {//returning normally or via thrown exception
		if (callLevel <=CALL_LEVEL_INIT) {
			err("#000 bug somewhere");
		}
		
		Signature sig=thisJoinPointStaticPart.getSignature();
		String link=sig2Link(sig,thisJoinPointStaticPart.getSourceLocation().toString());
		//System.err.println(formLevel("\u2515 "+RunTime.recursiveLoopDetected+" "+link));
		
		if (null == isLoopAtThisLevel.remove( callLevel )) {
			err("#358 a previous value should have existed");
		}
		
		callLevel--;
		
		if (callLevel>CALL_LEVEL_INIT) {
			//restore loopdetected status for our caller
			RunTime.recursiveLoopDetected=isLoopAtThisLevel.get( callLevel );
		}
		
		String which=sig.toLongString();
		if (which != perLevelStore.remove( callLevel+1 )){//||(callLevel==3)) {
			err("#890 "+link+" at the same call level we returned from a different method " +
					"than what we called");
		}
		//callLevel--;
		if (!RunTime.recursiveLoopDetected) {
			//remove only if not loop detected on current, because it got overwritten last time
			if (!calls.remove( which )) {
				err("#1034 "+link+" should've existed");
			}
		}
	}
	
	/**
	 * @param s the method signature that's about to be called or that we're returning from
	 * @param loc location of the call made to "s" ; even when returning
	 * 			so it's not the location of "s", it's the location of the call to "s"
	 *		FIXME: make this the location of "s" instead
	 * @return
	 */
	private String sig2Link(Signature s, String loc) {
		String _x=s.toLongString();//gotta get rid of params at end of method, to form valid eclipse console link
		return _x.substring( 0, _x.indexOf( "(" ) )+"("+loc+")";
	}
	
	private String formLevel(String msg) {
		String x="";
		for ( int i = 0; i < callLevel; i++ )
		{
			x+="\u2502  ";
		}
		return x+msg;//String.format( "%-" + callLevel + "s%s", " ",msg);
	}
	
	private static void err(String msg){
		System.err.println(msg);
		throw new RuntimeException(msg+" in aspect "+RecursionDetector.class);
	}
}
