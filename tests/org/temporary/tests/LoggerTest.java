/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
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



import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;



/**
 * 
 *
 */
public class LoggerTest {
	
	private static final Logger	logger	= Logger.getLogger( LoggerTest.class.getName() );
	
	@Test
	public void test1() {

		logger.info( "info msg" );
		assertNull( logger.getLevel() );
		logger.setLevel( Level.ALL );
		assertTrue( logger.getLevel() == Level.ALL );
		assertTrue( logger.isLoggable( Level.CONFIG ) );
		logger.config( "config msg" );// unseen?
		logger.fine( "fine msg" );// unseen?
		logger.severe( "severe msg" );
		
	}
}
