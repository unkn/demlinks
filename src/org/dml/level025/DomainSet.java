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

import com.sleepycat.je.DatabaseException;



/**
 * 
 * a set of Symbols which are all children of X, where X is settable<br>
 * X is the domain<br>
 * the domain and the fact that this is a DomainSet is only known in Java, not in the db<br>
 * TODO JUnit test
 */
public class DomainSet
		extends
		SetOfTerminalSymbols
{
	
	private static final TwoKeyHashMap<Level025_DMLEnvironment, Symbol, DomainSet>	allDomainSetInstances	= new TwoKeyHashMap<Level025_DMLEnvironment, Symbol, DomainSet>();
	private final Symbol															domain;
	
	
	private DomainSet(
			Level025_DMLEnvironment passedEnv,
			Symbol passedSelf,
			Symbol passedDomain )
	{
		
		super(
				passedEnv,
				passedSelf );
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf,
								passedDomain );
		RunTime.assumedTrue( passedEnv.isInited() );
		
		domain = passedDomain;
	}
	

	private final static
			void
			registerDSInstance(
								Level025_DMLEnvironment env,
								Symbol self,
								DomainSet newOne )
	{
		
		RunTime.assumedNotNull(
								env,
								self,
								newOne );
		RunTime.assumedFalse( allDomainSetInstances.ensure(
															env,
															self,
															newOne ) );
	}
	

	private final static
			DomainSet
			getDSInstance(
							Level025_DMLEnvironment env,
							Symbol self )
	{
		
		RunTime.assumedNotNull(
								env,
								self );
		return allDomainSetInstances.get(
											env,
											self );
	}
	

	public static
			DomainSet
			getAsDomainSet(
							Level025_DMLEnvironment passedEnv,
							Symbol passedSelf,
							Symbol passedDomain )
	{
		
		RunTime.assumedNotNull(
								passedEnv,
								passedSelf,
								passedDomain );
		
		// SetOfTerminalSymbols asSet = passedEnv.getAsSet( passedSelf );
		
		DomainSet existingOne = getDSInstance(
												passedEnv,
												passedSelf );
		if ( null != existingOne )
		{
			if ( existingOne.domain != passedDomain )
			{
				RunTime.badCall( "already existing DomainSet had different Domain setting" );
			}
			existingOne.assumedValid();
			return existingOne;
		}
		DomainSet ret = new DomainSet(
										passedEnv,
										passedSelf,
										passedDomain );
		ret.assumedValid();
		RunTime.assumedTrue( ret.getAsSymbol() == passedSelf );
		registerDSInstance(
							passedEnv,
							passedSelf,
							ret );
		return ret;
	}
	

	@Override
	public
			void
			assumedValid()
	{
		
		super.assumedValid();
		RunTime.assumedNotNull( domain );
		RunTime.assumedFalse( selfAsSymbol == domain );
		
		BDBVectorIterator<Symbol, Symbol> iter = env.getIterator_on_Terminals_of( selfAsSymbol );
		try
		{
			iter.goFirst();
			while ( null != iter.now() )
			{
				// each child of 'asSymbol' aka this DomainSet, must be a child
				// of domain too
				RunTime.assumedTrue( env.isVector(
													domain,
													iter.now() ) );
				iter.goNext();
			}
		}
		catch ( Throwable t )
		{
			RunTime.throWrapped( t );
		}
		finally
		{
			Factory.deInit( iter );
		}
	}
	

	/**
	 * @param element
	 * @return false if it didn't already exist
	 */
	@Override
	public
			boolean
			addToSet(
						Symbol element )
	{
		
		RunTime.assumedNotNull( element );
		RunTime.assumedFalse( selfAsSymbol == element );
		if ( !env.isVector(
							domain,
							element ) )
		{
			RunTime.badCall( "passed element is not from domain" );
		}
		return super.addToSet( element );
	}
	

	/**
	 * @param which
	 *            should be a child of domain
	 * @return true if self->which
	 */
	@Override
	public
			boolean
			hasSymbol(
						Symbol which )
	{
		
		RunTime.assumedNotNull( which );
		RunTime.assumedFalse( selfAsSymbol == which );
		if ( !env.isVector(
							domain,
							which ) )
		{
			RunTime.badCall( "passed element is not from domain" );
		}
		return super.hasSymbol( which );
	}
	
}
