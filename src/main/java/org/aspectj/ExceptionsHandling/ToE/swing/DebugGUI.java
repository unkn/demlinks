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



import java.awt.*;

import javax.swing.*;

import org.q.*;



/**
 *
 */
public class DebugGUI
{
	
	private static JFrame		frameSingleton		= null;
	private static JSplitPane	splitPaneSingleton	= null;
	
	
	private DebugGUI() {
		Q.badCall( "use getJFrame() instead" );
	}
	
	
	public static synchronized JFrame getDebugJFrame() {
		if ( null == frameSingleton ) {
			// JDesktopPane desktop = new JDesktopPane();
			frameSingleton = new JFrame( "TreeOfExceptions and TreeOfCalls below" );
			
			frameSingleton.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );// EXIT_ON_CLOSE );
			final Dimension dim = new Dimension( 813, 476 );
			frameSingleton.setSize( dim );
			frameSingleton.setAlwaysOnTop( true );
			final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			frameSingleton.setLocation( screen.width - dim.width, screen.height - dim.height );
			// desktop.add( frameSingleton );
			// desktop.setL( frameSingleton, );
		}
		assert Q.nn( frameSingleton );
		return frameSingleton;
	}
	
	
	public static synchronized JSplitPane getDebugSplitPane() {
		if ( null == splitPaneSingleton ) {
			splitPaneSingleton = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
			
		}
		assert Q.nn( splitPaneSingleton );
		return splitPaneSingleton;
	}
}
