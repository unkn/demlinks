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



import org.dml.database.bdb.level2.VectorIterator;
import org.dml.level010.Symbol;
import org.dml.level010.TheStoredSymbol;
import org.dml.tools.RunTime;



/**
 * 
 *
 */
public class SymbolIterator
		implements
		VectorIterator<Symbol>
{
	
	private final VectorIterator<TheStoredSymbol>	seed;
	
	
	public SymbolIterator(
			VectorIterator<TheStoredSymbol> seed1 )
	{
		RunTime.assumedNotNull( seed1 );
		seed = seed1;
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
		seed.goFirst();
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
		seed.goTo( element.getTheStoredSymbol() );
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
		return seed.now();
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
		seed.goNext();
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
		seed.goPrev();
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
		return seed.count();
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
		return seed.delete();
	}
	
}
