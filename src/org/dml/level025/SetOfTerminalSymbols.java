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



package org.dml.level025;



import org.dml.database.bdb.level2.BDBVectorIterator;
import org.dml.level010.Symbol;
import org.dml.tools.RunTime;
import org.dml.tools.TwoKeyHashMap;
import org.dml.tracking.Factory;
import org.references.Position;

import com.sleepycat.je.DatabaseException;



/**
 * won't allow pointing to self<br>
 * self->x,y,z as terminals<br>
 * probably a bad idea to make a set of initials<br>
 */
public class SetOfTerminalSymbols
{
	
	private static final TwoKeyHashMap<Level025_DMLEnvironment, Symbol, SetOfTerminalSymbols>	allSetOfSymbolsInstances	= new TwoKeyHashMap<Level025_DMLEnvironment, Symbol, SetOfTerminalSymbols>();
	protected final Level025_DMLEnvironment														env;
	protected final Symbol																		selfAsSymbol;
	private BDBVectorIterator<Symbol, Symbol>													iter						= null;
	
	
	/**
	 * private constructor
	 */
	protected SetOfTerminalSymbols(
			Level025_DMLEnvironment passedEnv,
			Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		env = passedEnv;
		selfAsSymbol = passedSelf;
	}
	

	// TODO: new, existing, ensure
	public static
			SetOfTerminalSymbols
			getAsSet(
						Level025_DMLEnvironment passedEnv,
						Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		SetOfTerminalSymbols existingSOS = getSOSInstance(
															passedEnv,
															passedSelf );
		if ( null == existingSOS )
		{
			existingSOS = new SetOfTerminalSymbols(
													passedEnv,
													passedSelf );
			existingSOS.assumedValid();
			registerSOSInstance(
									passedEnv,
									passedSelf,
									existingSOS );
		}
		existingSOS.assumedValid();
		RunTime.assumedTrue( passedEnv == existingSOS.env );
		RunTime.assumedTrue( passedSelf == existingSOS.selfAsSymbol );
		return existingSOS;
	}
	

	/**
	 * 
	 */
	public
			void
			assumedValid()
	{
		
		RunTime.assumedNotNull(
								env,
								selfAsSymbol );
		RunTime.assumedTrue( env.isInited() );
	}
	

	private final static
			void
			registerSOSInstance(
									Level025_DMLEnvironment env,
									Symbol passedSelf,
									SetOfTerminalSymbols newOne )
	{
		
		RunTime.assumedNotNull(
								env,
								passedSelf,
								newOne );
		RunTime.assumedFalse( allSetOfSymbolsInstances.ensure(
																env,
																passedSelf,
																newOne ) );
	}
	

	private final static
			SetOfTerminalSymbols
			getSOSInstance(
							Level025_DMLEnvironment env,
							Symbol passedSelf )
	{
		
		RunTime.assumedNotNull(
								env,
								passedSelf );
		return allSetOfSymbolsInstances.get(
												env,
												passedSelf );
	}
	

	public
			Symbol
			getAsSymbol()
	{
		
		this.assumedValid();
		return selfAsSymbol;
	}
	

	/**
	 * @param element
	 * @return false if it didn't already exist
	 */
	public
			boolean
			addToSet(
						Symbol element )
	{
		
		RunTime.assumedNotNull( element );
		if ( selfAsSymbol == element )
		{
			RunTime.badCall();
		}
		return env.ensureVector(
									selfAsSymbol,
									element );
	}
	

	/**
	 * @param which
	 *            should be a child of domain
	 * @return true if self->which
	 */
	public
			boolean
			hasSymbol(
						Symbol which )
	{
		
		RunTime.assumedNotNull( which );
		RunTime.assumedFalse( selfAsSymbol == which );
		return env.isVector(
								selfAsSymbol,
								which );
	}
	

	public
			int
			size()
	{
		
		RunTime.assumedNotNull( selfAsSymbol );
		// cache won't do
		return env.countTerminals( selfAsSymbol );
	}
	

	/**
	 * @param element
	 * @return true if existed
	 */
	public
			boolean
			remove(
					Symbol element )
	{
		
		RunTime.assumedNotNull( element );
		RunTime.assumedFalse( selfAsSymbol == element );
		return env.removeVector(
									selfAsSymbol,
									element );
	}
	

	/**
	 * use iter.deInit() when done<br>
	 * 
	 * @return iter
	 */
	private
			void
			refreshIterator()
	{
		
		if ( null == iter )
		{
			iter = env.getIterator_on_Terminals_of( selfAsSymbol );
		}
		else
		{
			Factory.reInitIfNotInited( iter );
			// iter.reInit();
		}
		RunTime.assumedNotNull( iter );
	}
	

	private
			void
			deInitIterator()
	{
		
		RunTime.assumedNotNull( iter );
		Factory.deInit( iter );
		// iter.deInit();
	}
	

	/**
	 * @param side
	 *            only FIRST is allowed yet
	 * @return
	 */
	public
			Symbol
			getSide(
						Position side )
	{
		
		Symbol ret = null;
		
		this.refreshIterator();
		try
		{
			switch ( side )
			{
				case FIRST:
					iter.goFirst();
					break;
				default:
					RunTime.badCall( "unsupported position" );
			}
			
			ret = iter.now();
		}
		finally
		{
			this.deInitIterator();
		}
		
		return ret;
	}
	

	/**
	 * @param side
	 * @param ofThis
	 * @return null if none
	 */
	public
			Symbol
			getSideOf(
						Position side,
						Symbol ofThis )
	{
		
		RunTime.assumedNotNull(
								side,
								ofThis );
		Symbol ret = null;
		
		this.refreshIterator();
		try
		{
			iter.goTo( ofThis );
			if ( iter.now() != null )
			{
				
				switch ( side )
				{
					case BEFORE:
						iter.goPrev();
						break;
					case AFTER:
						iter.goNext();
						break;
					default:
						RunTime.badCall( "unsupported position" );
				}
				
				ret = iter.now();
			}
		}
		finally
		{
			this.deInitIterator();
		}
		
		return ret;
	}
}
