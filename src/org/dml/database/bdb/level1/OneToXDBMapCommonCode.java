/**
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
 * 
 * 
 * File creation: Sep 8, 2010 11:05:55 PM
 */


package org.dml.database.bdb.level1;



import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.references.Reference;



/**
 * 
 *
 */
public class OneToXDBMapCommonCode
{
	
	public final static
			void
			theDone(
						boolean initedSuccess,
						Reference<Initer> ref2ForwardDB,
						Reference<Initer> ref2BackwardDB )
	{
		RunTime.assumedNotNull(
								initedSuccess,
								ref2ForwardDB,
								ref2BackwardDB );
		Initer forwardDB = ref2ForwardDB.getObject();
		Initer backwardDB = ref2BackwardDB.getObject();
		
		if ( initedSuccess )
		{
			RunTime.assumedNotNull(
									forwardDB,
									backwardDB );
		}// else they they may be in 4 states, 1) both null, 2)3)only one of them null and 4) both non-null due to
			// failed init()
		
		try
		{
			if ( null != backwardDB )
			{
				try
				{
					Factory.deInit( backwardDB );// first close this
				}
				finally
				{
					backwardDB = null;
				}
			}
		}
		finally
		{
			if ( null != forwardDB )
			{
				try
				{
					Factory.deInit( forwardDB );
				}
				finally
				{
					forwardDB = null;
				}
			}
		}// fin
	}
}
