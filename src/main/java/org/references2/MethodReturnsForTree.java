/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.references2;

import org.q.*;



/**
 *
 */
public enum MethodReturnsForTree {
		SUCCESS_BUT_ALREADY_EXISTED_WHERE_EXPECTED, // exists in that parent at the expected position
		SUCCESS_AND_DIDNT_ALREADY_EXIST, // didn't exist anywhere
		HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_PARENT,
		HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_POSITION_IN_SAME_PARENT,
		FAIL,
		UNSET;
	
	
	public boolean isSuccess( final MethodReturnsForTree mr ) {
		switch ( mr ) {
		case SUCCESS_BUT_ALREADY_EXISTED_WHERE_EXPECTED:
		case SUCCESS_AND_DIDNT_ALREADY_EXIST:
			return true;
			// break;
		case FAIL:
			return false;
		case HALF_SUCCESS_BECAUSE_EXISTED_IN_A_DIFFERENT_PARENT:
			return false;
		case UNSET:
			Q.bug( "you forgot to set the right return type" );
			return false;// not reached
		default:
			Q.bug( "you forgot to define newly added enums" );
			return false;// unreachable
			// break;
		}
	}// method
	
	
}
