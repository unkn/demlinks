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


package org.aspectj.ExceptionsHandling.ToE.swing;



import java.lang.Thread.UncaughtExceptionHandler;

import org.q.*;



/**
 * that are not caught by aspectj, ie. in main or some static methods(?)<br>
 * for exceptions that slip through due to not being inside method calls because only the latter can be hooked by aspect<br>
 * 
 */
public class ExHandlerForThoseThatAreNotWithinCallsIeMain
		implements UncaughtExceptionHandler
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException( final Thread th, final Throwable ex ) {
		// TreeOfExceptions.lockTVC.lock();
		try {
			assert Q.nn( th );
			assert Q.nn( ex );
			System.err.println( "!======UNCAUGHT====! Exception in thread: \"" + th.getName() + "\" ex: " + ex.getClass() );
			try {
				// System.err.println( "QUEUE SIZE:"
				// + TreeOfExceptions.getTreeOfExceptions().queueFIFO.size() );
				// final boolean alreadyInTree = Q.isAspectWrappedException( ex );
				// if it's aspect wrapped it means it was thrown from aspect, which means it's already in ToE
				// if ( alreadyInTree )
				// {
				// try
				// {
				// alreadyInTree =
				// TreeOfExceptions.getTreeOfExceptions()// maybe init this at beginning in main thread!
				// .alreadyExists(
				// Q.getUnWrappedException( ex ) );
				// }
				// catch ( Throwable t )
				// {
				// alreadyInTree =
				// false;
				// System.err.println( "************ tree is broken:" );
				// ex.printStackTrace();
				// System.err.println( "************ end." );
				// }
				// }
				// fixed: fix this when it's in tree but something crashed the AWT thread and can no longer use the GUI
				// to see the exceptions //sorta fixed, assuming EDT being alive makes the tree always functional
				// if ( !alreadyInTree ) {
				try {
					// add it to tree via aspectj
					// because it's a call, it will be caught by aspect, even though this throw will be silenced
					// below
					// System.err.println( "visible:"
					// + TreeOfExceptions.isVisible() );// could be false here when Simple2.java exec
					final AlmostSlippedException ase = new AlmostSlippedException( ex );
					Q.toTree( ase );
					throw ase;
					// System.err.println( "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq begin" );
					// ex.printStackTrace();
					// System.err.println( "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq end" );
				} catch ( final Throwable t ) {
					// ignore it, 'cause we only called that to have aspectj add it to tree
				} finally {
					if ( TreeOfExceptions.isVisible() )// but here it will never be false, due to aspectj catching it
														// causing this thread to wait until isVisible
					// it will be false if tree wasShutdown
					{
						System.err
							.println( "___redirected_to_aspect__ exception was `sent` to be caught by aspect, look in the tree" );
					} else {
						System.err.println( "-----------------TREEEEE IS NOT VISIBLEEEEEEEEEEEEEEEEEEEEEE-----------" );
					}
					// and also report it on console
					// System.err.println(
					// "^^^^^^^^^^ reporting last exception on console, just in case tree isn't functional:" );
					// ex.printStackTrace();
					// System.err.println( "^^^^^^^^^^ ends" );
				}
				// } else {
				// // ex.printStackTrace();
				// System.err.println( "___ignored__ exception that was already caught by aspect thus it's in ToE"
				// + " or was reported on console, see above" );
				// }
				// if ( TreeOfExceptions.wasShutdownOnce )
				// {
				// TreeOfExceptions.getTreeOfExceptions().popQueue();
				// }
			} finally {
				System.err.println( "!====!done" );
			}
		} catch ( final Throwable dontThrowFromThisMethod ) {
			// avoid rethrowing it due to it might break EDT aka swing event queue thread, and make GUI hang
			// if we were to throw in this method then EDT thread will die and our tree won't receive any events of
			// newly added exceptions to it and it won't show them so user will not see them
			if ( Q.isBareException( dontThrowFromThisMethod ) ) {
				// this shouldn't happen
				System.err.println( ".................begin" );
				dontThrowFromThisMethod.printStackTrace();
				System.err.println( ".................end" );
			}
		}
		// finally
		// {
		// TreeOfExceptions.lockTVC.unlock();
		// }
	}
	
}
