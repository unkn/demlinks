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
 * File creation: Sep 9, 2010 11:17:21 AM
 */


package org.dml.level020;



import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.database.bdb.level2.VectorIterator;
import org.dml.level010.Symbol;
import org.dml.level010.TheStoredSymbol;
import org.dml.tools.Initer;
import org.dml.tools.RunTime;
import org.dml.tracking.Factory;
import org.references.method.MethodParams;



/**
 * 
 *
 */
public class SymbolIterator
		implements
		VectorIterator<Symbol>
{
	
	private VectorIterator<TheStoredSymbol>	seed;
	
	
	/**
	 * @param seed1
	 *            an preInited iterator, which we will deInit on our own
	 */
	public SymbolIterator(
			VectorIterator<TheStoredSymbol> seed1 )
	{
		RunTime.assumedNotNull( seed1 );
		// RunTime.assumedTrue( seed1.isInitedSuccessfully() );
		seed = seed1;
	}
	

	private
			VectorIterator<TheStoredSymbol>
			getSeed()
	{
		RunTime.assumedNotNull( seed );
		return seed;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#goFirst()
	 */
	@Override
	public
			void
			goFirst()
	{
		this.getSeed().goFirst();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#goTo(java.lang.Object)
	 */
	@Override
	public
			void
			goTo(
					Symbol element )
	{
		this.getSeed().goTo(
						element.getTheStoredSymbol() );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#now()
	 */
	@Override
	public
			Symbol
			now()
	{
		return Symbol.getNew(
								this.getSeed().getBDBL1(),
								this.getSeed().now() );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#goNext()
	 */
	@Override
	public
			void
			goNext()
	{
		this.getSeed().goNext();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#goPrev()
	 */
	@Override
	public
			void
			goPrev()
	{
		this.getSeed().goPrev();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#count()
	 */
	@Override
	public
			long
			count()
	{
		return this.getSeed().count();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#delete()
	 */
	@Override
	public
			boolean
			delete()
	{
		return this.getSeed().delete();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#getBDBL1()
	 */
	@Override
	public
			Level1_Storage_BerkeleyDB
			getBDBL1()
	{
		return this.getSeed().getBDBL1();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.database.bdb.level2.VectorIterator#close()
	 */
	@Override
	public
			void
			close()
	{
		try
		{
			this.getSeed().close();
		}
		finally
		{
			seed = null;// for safety when calling other methods after close we will catch them
		}
	}
	
}
